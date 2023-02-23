/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import java.io.IOException;
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
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
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
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.fund.manager.base.BankInfoReport;
import org.rmj.fund.manager.base.Incentive;
import org.rmj.fund.manager.base.LMasDetTrans;
import reportmodel.IncentiveMaster;

/**
 * FXML Controller class
 *
 * @author user
 */
public class ReportsController implements Initializable, ScreenInterface{
    private final String pxeModuleName = "Bank Info Report";
    
    private GRider oApp;
    private BankInfoReport oTrans;
    private LMasDetTrans oListener;
    
    private final boolean pbLoaded = false;
    
    private JasperPrint jasperPrint;
    private JasperReport jasperReport;
    private JRViewer jrViewer;
    public String reportCategory;
    private ToggleGroup rbGroup;
    
    private boolean running = false;
    final static int interval = 100;
    private Timeline timeline;
    private Integer timeSeconds = 3;
//    private JasperPreview jasperPreview;
    @FXML
    private Button btnGenerate,btnCloseReport;
    @FXML
    private AnchorPane reportPane, AnchorMainReport;
    @FXML
    private Label lblReportsTitle;
    @FXML
    private TextField txtField01,txtField02;
    @FXML
    private VBox vbProgress;
//    @FXML
//    private GridPane gpAudit;
//    @FXML
//    private GridPane gpPayroll;
    @FXML
    private VBox vbContainer;
    
    
    private final ArrayList<TableModel> pmodel = new ArrayList<>();
    private final ObservableList<BankInfoReportModel> data = FXCollections.observableArrayList();
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
        btnCloseReport.setOnAction(this::cmdButton_Click);
       
       
        oListener = new LMasDetTrans() {
            @Override
            public void MasterRetreive(int fnIndex, Object foValue) {
                switch (fnIndex) {
                    case 1:
                        txtField01.setText((String)foValue);
                        break;
                    case 2:
                        txtField02.setText((String)foValue);
                        break;
                    default:
                        throw new AssertionError();
                }
                
            }
            @Override
            public void DetailRetreive(int fnRow, int fnIndex, Object foValue) {
            }
         };
        oTrans  = new BankInfoReport(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        oTrans.setTranStat(1);
        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
        txtField02.setOnKeyPressed(this::txtField_KeyPressed);
        lblReportsTitle.setText("BANK ACCOUNT INFO REPORT");
 
    }  
    private Stage getStage(){
	return (Stage) txtField01.getScene().getWindow();
    }
    private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();
        int lnIndex = Integer.parseInt(((TextField)event.getSource()).getId().substring(8,10));
        String lsValue = txtField.getText();
        
        try {
            switch (event.getCode()) {
                case F3:
                case ENTER:
                    switch (lnIndex){
                        case 1: /*search branch*/
                            if(oTrans.searchBranch(lsValue, false)) {
                                txtField.setText((String) oTrans.getBranch("sBranchNm"));
//                                ShowMessageFX.Warning(getStage(), "Unable to search branch.", "Warning", null);
                            } else{
                               ShowMessageFX.Warning(getStage(),oTrans.getMessage(), "Warning", null);
                            }
                            break;
                        case 2: /*search department*/
                            if(oTrans.searchDepartment(lsValue, false)) {
                                txtField.setText((String) oTrans.getDepartment("sDeptName"));
//                                ShowMessageFX.Warning(getStage(), "Unable to search branch.", "Warning", null);
                            } else{
                               ShowMessageFX.Warning(getStage(), oTrans.getMessage(), "Warning", null);
                            }
                            break;
                    }
            }      
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        switch (event.getCode()){
        case ENTER:
        case DOWN:
            CommonUtils.SetNextFocus(txtField);
            break;
        case UP:
            CommonUtils.SetPreviousFocus(txtField);
        }
    } 
    private void showReport(){
        
        vbProgress.setVisible(false);
        jrViewer =  new JRViewer(jasperPrint);
//        JasperViewer.viewReport(jasperPrint, false);
         
        SwingNode swingNode = new SwingNode();
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
        running = true;
        reportPane.setVisible(true);
        timeline.stop();
    }    
    private void hideReport(){
//        jasperPrint = null;
        jrViewer =  new JRViewer(null);
        reportPane.getChildren().clear();
//        JasperViewer.viewReport(jasperPrint, false);
         
        jrViewer.setVisible(false);
        running = false;
        reportPane.setVisible(false);
//        vbProgress.setVisible(true);
    }    
    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
    private void cmdButton_Click(ActionEvent event) {
            String lsButton = ((Button)event.getSource()).getId();
            switch (lsButton){
                case "btnGenerate":
                {
                    try {
                        if(txtField01.getText().trim().toString().isEmpty()){
                            oTrans.setBranch();
                        }
                        
                        if(txtField02.getText().trim().toString().isEmpty()){
                            oTrans.setDepartment();
                        } 
                       
                        if(oTrans.OpenRecord()){
                           vbProgress.setVisible(true);
                           generateReport();
                    
                        }else{
                            
                            vbProgress.setVisible(false);
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
                 case "btnCloseReport":
                        if(ShowMessageFX.OkayCancel(null, pxeModuleName, "Are you sure, do you want to close?") == true){
                             unloadForm();
                            break;
                        } 
                            return;

            }
    } 
    
    
    private void generateReport(){
        hideReport();
        if(!running){
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(1), (ActionEvent event1) -> {
                        timeSeconds--;
                        // update timerLabel
                        if(timeSeconds <= 0){
                           timeSeconds = 0 ; 
                        }
                        if (timeSeconds == 0) {

                            loadReport();
                        }
                } // KeyFrame event handler
            ));
            timeline.playFromStart();
        }
    }
    private boolean loadReport(){
        String sourceFileName =  "D://GGC_Java_Systems/reports/BankAccountInfo.jasper";
        
         //Create the parameter
        Map<String, Object> params = new HashMap<>();
//                params.put("sReportNm", "Department Incentive Report");
        params.put("sPrintdBy", System.getProperty("user.name"));
        params.put("sReportDt", CommonUtils.xsDateLong(oApp.getServerDate()));
        params.put("sCompnyNm", "Guanzon Group of Companies");
        params.put("sBranchNm", oApp.getBranchName());
        params.put("sAddressx", oApp.getAddress());
        params.put("sReportNm", "Bank Account Info Report");
        try {
//            jrRS = new JRResultSetDataSource(oTrans.getQuery());
            data.clear();
            for (int x = 1; x <= oTrans.getItemCount(); x++){
            
                data.add(new BankInfoReportModel(
                    oTrans.getDetail(x, "sBranchNm").toString(),
                    oTrans.getDetail(x, "sCompnyNm").toString(),
                    oTrans.getDetail(x, "sBankName").toString(),
                    oTrans.getDetail(x, "sBankAcct").toString(),
                    oTrans.getDetail(x, "sDeptName").toString()
                    ));
                }

            String printFileName = null;
            JRBeanCollectionDataSource beanColDataSource1 = new JRBeanCollectionDataSource(data);
           
            try {
                 jasperPrint =JasperFillManager.fillReport(
                        sourceFileName, params, beanColDataSource1);
                 
                printFileName = jasperPrint.toString();
                if(printFileName != null){
                    showReport();
                 }
            } catch (JRException ex) {
                running = false;
                vbProgress.setVisible(false);
                timeline.stop();
            }
        } catch (SQLException ex) {
            running = false;
            vbProgress.setVisible(false);
            timeline.stop();
        }
        return true;
// if (rbChart.isSelected()){
//        String lsReport = "D://GGC_Java_Systems/reports/BankInfoChart.jasper";
//        String lsSQL = "SELECT COUNT(sBankAcct) AS bankInfo04, e.sBranchNm bankInfo05, d.sBankName bankInfo03, c.sCompnyNm bankInfo02\n" +
//              " FROM Employee_Incentive_Bank_Info a \n" +
//              " LEFT JOIN Client_Master c\n" +
//              "  ON a.sEmployID = c.sClientID \n" +
//              "  LEFT JOIN Banks d \n" +
//              "  ON a.sBankIDxx = d.sBankIDxx,\n" +
//              "   Employee_Master001 b \n" +
//              "   LEFT JOIN Branch e \n" +
//              "   ON b.sBranchCd = e.sBranchCd \n" +
//              "   WHERE a.sEmployID = b.sEmployID AND b.cRecdStat = '1' GROUP BY d.sBankName ORDER BY e.sBranchNm";
//
//        ResultSet loRS = oApp.executeQuery(lsSQL);
//        //Convert the data-source to JasperReport data-source
//        JRResultSetDataSource jrRS = new JRResultSetDataSource(loRS);
//
//        //Create the parameter
//        Map<String, Object> params = new HashMap<>();
//        params.put("sReportNm", "Report Name");
//        params.put("sPrintdBy", "Printed By");
//        params.put("sCompnyNm", "Company Name");
//        params.put("sBranchNm", "Branch Name");
//        params.put("sAddressx", "Branch Address");
//
//        try {
//            jasperPrint = JasperFillManager.fillReport(lsReport,
//                                                        params, 
//                                                        jrRS);
//
//            if (jasperPrint != null) showReport();
//        } catch (JRException e) {
//            e.printStackTrace();
//        }
//}
//else if (rbDetailed.isSelected()){
//         String lsReport = "D://GGC_Java_Systems/reports/BankAccountInfo.jasper";
//         String lsSQL = "SELECT e.sBranchNm bankInfo05, c.sCompnyNm bankInfo02, d.sBankName bankInfo03, a.sBankAcct bankInfo04" +
//                  "   FROM Employee_Incentive_Bank_Info a LEFT JOIN Client_Master c ON a.sEmployID = c.sClientID\n" +
//                  "   LEFT JOIN Banks d ON a.sBankIDxx = d.sBankIDxx\n" +
//                 "	, Employee_Master001 b LEFT JOIN Branch e ON b.sBranchCd = e.sBranchCd\n" +
//                  "   WHERE a.sEmployID = b.sEmployID\n" +
//                  "	AND b.cRecdStat = '1' ORDER BY e.sBranchNm;";
//
//        ResultSet loRS = oApp.executeQuery(lsSQL);
//        //Convert the data-source to JasperReport data-source
//        JRResultSetDataSource jrRS = new JRResultSetDataSource(loRS);
//
//        //Create the parameter
//        Map<String, Object> params = new HashMap<>();
//        params.put("sReportNm", "Report Name");
//        params.put("sPrintdBy", "Printed By");
//        params.put("sCompnyNm", "Company Name");
//        params.put("sBranchNm", "Branch Name");
//        params.put("sAddressx", "Branch Address");
//
//        try {
//            jasperPrint = JasperFillManager.fillReport(lsReport,
//                                                        params, 
//                                                        jrRS);
//
//            if (jasperPrint != null) showReport();
//        } catch (JRException e) {
//            e.printStackTrace();
//        }
//}
//else if(rbSummarized.isSelected()){
//System.out.println("wala pa");
//}else if(rbGlobal.isSelected()){
//System.out.println("wala pa");
//}
//        return true;
    }
    private void unloadForm(){
        StackPane myBox = (StackPane) AnchorMainReport.getParent();
        myBox.getChildren().clear();
        myBox.getChildren().add(getScene("MainScreenBG.fxml"));
    }
    private AnchorPane getScene(String fsFormName){
         ScreenInterface fxObj = new MainScreenBGController();
         fxObj.setGRider(oApp);
        
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(fxObj.getClass().getResource(fsFormName));
        fxmlLoader.setController(fxObj);      
   
        AnchorPane root;
        try {
            root = (AnchorPane) fxmlLoader.load();
            FadeTransition ft = new FadeTransition(Duration.millis(1500));
            ft.setNode(root);
            ft.setFromValue(1);
            ft.setToValue(1);
            ft.setCycleCount(1);
            ft.setAutoReverse(false);
            ft.play();

            return root;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }
}


