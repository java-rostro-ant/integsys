/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agent.MsgBox;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.fund.manager.base.Incentive;
import org.rmj.fund.manager.base.LMasDetTrans;

/**
 * FXML Controller class
 *
 * @author user
 */
public class ReportsController implements Initializable, ScreenInterface{
    
    private GRider oApp;
    private Incentive oTrans;
    private LMasDetTrans oListener;
    
    private final boolean pbLoaded = false;
    
    private JasperPrint jasperPrint;
    private JasperReport jasperReport;
    public String reportCategory;
    private ToggleGroup rbGroup;
//    private JasperPreview jasperPreview;
    @FXML
    private Button btnGenerate;
    @FXML
    private AnchorPane reportPane;
    @FXML
    private RadioButton rbDetailed;
    @FXML
    private RadioButton rbSummarized;
    @FXML
    private RadioButton rbGlobal;
    @FXML
    private RadioButton rbChart;
    @FXML
    private DatePicker dpFrom;
    @FXML
    private DatePicker dpThru;
    @FXML
    private Label lblReportsTitle;
//    @FXML
//    private GridPane gpStandard;
    @FXML
    private GridPane gpIndex02;
    @FXML
    private GridPane gpIndex03;
    @FXML
    private GridPane gpIndex04;
    @FXML
    private GridPane gpIndex05;
    @FXML
    private VBox vbProgress;
//    @FXML
//    private GridPane gpAudit;
//    @FXML
//    private GridPane gpPayroll;
    @FXML
    private VBox vbContainer;
    
    
    private final ArrayList<TableModel> pmodel = new ArrayList<>();
    private final ObservableList<TableModel> inc_data = FXCollections.observableArrayList();
    public void setReportCategory(String foValue){
        System.out.println(foValue);
        reportCategory = foValue;
    }
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        btnGenerate.setOnAction(this::cmdButton_Click);
       
       
        oListener = new LMasDetTrans() {
            @Override
            public void MasterRetreive(int fnIndex, Object foValue) {
                
            }
            @Override
            public void DetailRetreive(int fnRow, int fnIndex, Object foValue) {
            }
         };
        oTrans  = new Incentive(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        initToggleGroup();
        initDatePicker();
        initFields();
 
    }  
    private void initToggleGroup(){
        rbGroup = new ToggleGroup();
        rbDetailed.setToggleGroup(rbGroup);
        rbSummarized.setToggleGroup(rbGroup);
        rbGlobal.setToggleGroup(rbGroup);
        rbChart.setToggleGroup(rbGroup);
        rbDetailed.setSelected(true);
    }
    private void initDatePicker(){
        dpFrom.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if(dpThru.getValue() != null){
                    if (date != null && !empty) {
                        setDisable(empty || date.compareTo(LocalDate.now()) > 0 || date.compareTo(dpThru.getValue()) > 0);
                    }
                }else{
                    setDisable(empty || date.compareTo(LocalDate.now()) > 0);
                }
            }
        });
        dpThru.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if(dpFrom.getValue() != null){
                    if (date != null && !empty) {
                        setDisable(empty || date.compareTo(LocalDate.now()) > 0 || date.compareTo(dpFrom.getValue()) < 0);
                    }
                }else{
                    setDisable(empty || date.compareTo(LocalDate.now()) > 0);
                }
                
            }
        });
    }
    private void showReport(){
        
        SwingNode swingNode = new SwingNode();
        JRViewer jrViewer =  new JRViewer(jasperPrint);
//        JasperViewer.viewReport(jasperPrint, false);
         
        jrViewer.setOpaque(true);
        jrViewer.setVisible(true);
        jrViewer.setFitPageZoomRatio();
       
            
        swingNode.setContent(jrViewer);
        swingNode.setVisible(true);
//        reportPane.getChildren().clear();
        reportPane.setTopAnchor(swingNode,0.0);
        reportPane.setBottomAnchor(swingNode,0.0);
        reportPane.setLeftAnchor(swingNode,0.0);
        reportPane.setRightAnchor(swingNode,0.0);
        reportPane.getChildren().add(swingNode);
    }    
 
    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
    private void loadDetail(){
        try {
            inc_data.clear();
            for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
                inc_data.add(new TableModel(String.valueOf(lnCtr),
                    oTrans.getDetail(lnCtr, "xEmployNm").toString(),
                    oTrans.getDetail(lnCtr, "xEmpLevNm").toString(),
                    oTrans.getDetail(lnCtr, "xPositnNm").toString(),
                    oTrans.getDetail(lnCtr, "xSrvcYear").toString(),
                    (CommonUtils.NumberFormat((Number)oTrans.getDetail(lnCtr, "nTotalAmt"), "#,##0.00")),
                    (CommonUtils.NumberFormat((Number)oTrans.getDetail(lnCtr, "nTotalAmt"), "#,##0.00")),
                    (CommonUtils.NumberFormat((Number)oTrans.getDetail(lnCtr, "nTotalAmt"), "#,##0.00"))));
            }
            String sourceFileName = 
            "D://GGC_Java_Systems/reports/product_report.jasper";
            String printFileName = null;
            JRBeanCollectionDataSource beanColDataSource = new 
               JRBeanCollectionDataSource(inc_data);
            Map params = new HashMap();
            params.put("sBranchNme", oTrans.getMaster(16));
            params.put("sTransNox", oTrans.getMaster(1));
            params.put("sPeriod", oTrans.getMaster(4));
               try {
                 jasperPrint =JasperFillManager.fillReport(
                        sourceFileName, params, beanColDataSource);
               
               
                printFileName = jasperPrint.toString();
                if(printFileName != null){
                    
                    showReport();
                    
                 }
            } catch (JRException ex) {
                Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MsgBox.showOk(e.getMessage());
        }
    }
    private void initFields(){
        lblReportsTitle.setText(reportCategory + " REPORT");
        if(reportCategory.equalsIgnoreCase("STANDARD")){
              gpIndex02.setManaged(false);
              gpIndex02.setVisible(false);
              gpIndex03.setManaged(false);
              gpIndex03.setVisible(false);
        }else if(reportCategory.equalsIgnoreCase("AUDIT")){
              gpIndex03.setManaged(false);
              gpIndex03.setVisible(false);
              gpIndex04.setManaged(false);
              gpIndex04.setVisible(false);
        }else if(reportCategory.equalsIgnoreCase("PAYROLL")){
              gpIndex04.setManaged(false);
              gpIndex05.setManaged(false);
              gpIndex04.setVisible(false);
              gpIndex05.setVisible(false);
        }
    }
    private void cmdButton_Click(ActionEvent event) {
            String lsButton = ((Button)event.getSource()).getId();
            switch (lsButton){
                case "btnGenerate":
                    showProgress();
                    loadReport();
                    
                   
//                    if (oTrans.SearchTransaction("", false)){
//                           
//                        reportPane.requestFocus();
//                       
//                        loadDetail();
//                        hideProgress();
//                            
//                    }else{
//                        hideProgress();
//                        MsgBox.showOk(oTrans.getMessage());
//                    } 
                break;
            }
    } 
    
    private void showProgress(){
        vbProgress.getChildren().clear();
        vbProgress.setAlignment(Pos.CENTER);
        ProgressIndicator pi = new ProgressIndicator();
        pi.setOpacity(1.0);
        pi.setStyle(" -fx-progress-color: #0086b3;");
        VBox box = new VBox(pi);
        box.setAlignment(Pos.CENTER);
        vbProgress.getChildren().add(box);
    }
    private void hideProgress(){
        vbProgress.getChildren().clear();
    }
    private boolean loadReport(){
 if (rbChart.isSelected()){
        String lsReport = "D://GGC_Java_Systems/reports/BankInfoChart.jasper";
        String lsSQL = "SELECT COUNT(sBankAcct) AS bankInfo04, e.sBranchNm bankInfo05, d.sBankName bankInfo03, c.sCompnyNm bankInfo02\n" +
              " FROM Employee_Incentive_Bank_Info a \n" +
              " LEFT JOIN Client_Master c\n" +
              "  ON a.sEmployID = c.sClientID \n" +
              "  LEFT JOIN Banks d \n" +
              "  ON a.sBankIDxx = d.sBankIDxx,\n" +
              "   Employee_Master001 b \n" +
              "   LEFT JOIN Branch e \n" +
              "   ON b.sBranchCd = e.sBranchCd \n" +
              "   WHERE a.sEmployID = b.sEmployID AND b.cRecdStat = '1' GROUP BY d.sBankName ORDER BY e.sBranchNm";

        ResultSet loRS = oApp.executeQuery(lsSQL);
        //Convert the data-source to JasperReport data-source
        JRResultSetDataSource jrRS = new JRResultSetDataSource(loRS);

        //Create the parameter
        Map<String, Object> params = new HashMap<>();
        params.put("sReportNm", "Report Name");
        params.put("sPrintdBy", "Printed By");
        params.put("sCompnyNm", "Company Name");
        params.put("sBranchNm", "Branch Name");
        params.put("sAddressx", "Branch Address");

        try {
            jasperPrint = JasperFillManager.fillReport(lsReport,
                                                        params, 
                                                        jrRS);

            if (jasperPrint != null) showReport();
        } catch (JRException e) {
            e.printStackTrace();
        }
}
else if (rbDetailed.isSelected()){
         String lsReport = "D://GGC_Java_Systems/reports/BankAccountInfo.jasper";
         String lsSQL = "SELECT e.sBranchNm bankInfo05, c.sCompnyNm bankInfo02, d.sBankName bankInfo03, a.sBankAcct bankInfo04" +
                  "   FROM Employee_Incentive_Bank_Info a LEFT JOIN Client_Master c ON a.sEmployID = c.sClientID\n" +
                  "   LEFT JOIN Banks d ON a.sBankIDxx = d.sBankIDxx\n" +
                 "	, Employee_Master001 b LEFT JOIN Branch e ON b.sBranchCd = e.sBranchCd\n" +
                  "   WHERE a.sEmployID = b.sEmployID\n" +
                  "	AND b.cRecdStat = '1' ORDER BY e.sBranchNm;";

        ResultSet loRS = oApp.executeQuery(lsSQL);
        //Convert the data-source to JasperReport data-source
        JRResultSetDataSource jrRS = new JRResultSetDataSource(loRS);

        //Create the parameter
        Map<String, Object> params = new HashMap<>();
        params.put("sReportNm", "Report Name");
        params.put("sPrintdBy", "Printed By");
        params.put("sCompnyNm", "Company Name");
        params.put("sBranchNm", "Branch Name");
        params.put("sAddressx", "Branch Address");

        try {
            jasperPrint = JasperFillManager.fillReport(lsReport,
                                                        params, 
                                                        jrRS);

            if (jasperPrint != null) showReport();
        } catch (JRException e) {
            e.printStackTrace();
        }
}
else if(rbSummarized.isSelected()){
System.out.println("wala pa");
}else if(rbGlobal.isSelected()){
System.out.println("wala pa");
}
        return true;
    }
}


