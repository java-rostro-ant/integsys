/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gridergui;

import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.IncentiveReport;
import org.rmj.fund.manager.base.LMasDetTrans;
import reportmodel.IncentiveDetail;
import reportmodel.IncentiveMaster;

/**
 * FXML Controller class
 *
 * @author User
 */
public class IncentiveReportsController implements Initializable, ScreenInterface{
    
    private GRider oApp;
    private IncentiveReport oTrans;
    private LMasDetTrans oListener;
    
    private final boolean pbLoaded = false;
    
    private JasperPrint jasperPrint;
    private JasperReport jasperReport;
    public String reportCategory;
    private ToggleGroup rbGroup;
    private String sPeriodxx = "";
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
    private Label lblReportsTitle;
    @FXML
    private DatePicker dpPeriod;
    @FXML
    private TextField txtField01, txtField02;
    @FXML
    private VBox vbProgress;
    @FXML
    private VBox vbContainer;
    @FXML
    private GridPane grid01, grid02, grid03;
    
    private ObservableList<IncentiveDetail> inc_detail = FXCollections.observableArrayList();
    private final ObservableList<IncentiveMaster> inc_data = FXCollections.observableArrayList();
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
                switch (fnIndex) {
                    case 18:
                        txtField02.setText((String) foValue);
                        break;
                }
            }
            @Override
            public void DetailRetreive(int fnRow, int fnIndex, Object foValue) {
            }
         };
        
        oTrans  = new IncentiveReport(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setTranStat(1023);
        oTrans.setWithUI(true);
        initToggleGroup();
        initDatePicker();
        initFields();
        
        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
        txtField02.setOnKeyPressed(this::txtField_KeyPressed);
    }  
    private Stage getStage(){
	return (Stage) txtField02.getScene().getWindow();
    }
    private void initToggleGroup(){
        rbGroup = new ToggleGroup();
        rbDetailed.setToggleGroup(rbGroup);
        rbSummarized.setToggleGroup(rbGroup);
        rbDetailed.setSelected(true);
        rbGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() 
        {
            public void changed(ObservableValue<? extends Toggle> ob, 
                                                    Toggle o, Toggle n)
            {
  
               initGrid();
            }
        });
  
       
    }
    private void initDatePicker(){
        dpPeriod.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                getFormattedDateFromDatePicker(dpPeriod);
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
    
     private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();
        int lnIndex = Integer.parseInt(((TextField)event.getSource()).getId().substring(8,10));
        String lsValue = txtField.getText();
        
        try {
            switch (event.getCode()) {
                case F3:
                case ENTER:
                    switch (lnIndex){
                        case 1: /*search employee*/
                            if(oTrans.searchEmployee(lsValue, false)) {
                                txtField.setText((String) oTrans.getEmployee("sCompnyNm"));
                                txtField02.setText((String) oTrans.getBranch("sBranchNm"));
//                                ShowMessageFX.Warning(getStage(), "Unable to search branch.", "Warning", null);
                            } else{
                               ShowMessageFX.Warning(getStage(), "Unable to search employee.", "Warning", null);
                            }
                            break;
                        case 2: /*search branch*/
                            if(oTrans.searchBranch(lsValue, false)) {
                                txtField.setText((String) oTrans.getBranch("sBranchNm"));
//                                ShowMessageFX.Warning(getStage(), "Unable to search branch.", "Warning", null);
                            } else{
                               ShowMessageFX.Warning(getStage(), "Unable to search branch.", "Warning", null);
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
    
    private void initFields(){
        lblReportsTitle.setText(reportCategory + " REPORT");
//        if(reportCategory.equalsIgnoreCase("STANDARD")){
//              gpIndex02.setManaged(false);
//              gpIndex02.setVisible(false);
//              gpIndex03.setManaged(false);
//              gpIndex03.setVisible(false);
//        }else if(reportCategory.equalsIgnoreCase("AUDIT")){
//              gpIndex03.setManaged(false);
//              gpIndex03.setVisible(false);
//              gpIndex04.setManaged(false);
//              gpIndex04.setVisible(false);
//        }else if(reportCategory.equalsIgnoreCase("PAYROLL")){
//              gpIndex04.setManaged(false);
//              gpIndex05.setManaged(false);
//              gpIndex04.setVisible(false);
//              gpIndex05.setVisible(false);
//        }
    }
    public static String dateToWord (String dtransact) {
       
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMM");
        try {
            Date date = new Date();
            DateFormat formatter = new SimpleDateFormat("yyyyMM");
            date = (Date)formatter.parse(dtransact);  
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMM");
            String todayStr = fmt.format(date);
            
            return todayStr;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String getFormattedDateFromDatePicker(DatePicker datePicker) {
        //Get the selected date
        if(datePicker.getValue() != null){
            LocalDate selectedDate = datePicker.getValue();
        //Create DateTimeFormatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
            //Convert LocalDate to formatted String
            sPeriodxx = selectedDate.format(formatter);
            
            return selectedDate.format(formatter);
        }else{
            return "";
        }
        
    }
    
    private void cmdButton_Click(ActionEvent event) {
            String lsButton = ((Button)event.getSource()).getId();
            switch (lsButton){
                        
                case "btnGenerate":
                    showProgress();
                    if(txtField01.getText().isEmpty()){
                        oTrans.setEmployee();
                    }
                    
                    if(txtField02.getText().isEmpty()){
                        oTrans.setBranch();
                    }
                    if(dpPeriod.getValue() == null){
                        sPeriodxx = "";
                        ShowMessageFX.Warning(getStage(), "Incentive period must not be empty","Warning", null);
                        hideProgress();
                    }else{
                        getFormattedDateFromDatePicker(dpPeriod);
                        if(!txtField01.getText().isEmpty()){
                            loadIncentiveEmployee();
                        }else{
                            loadReport();
                        }
                    }
                break;
            }
    } 
    private void initGrid(){
         boolean lbShow = rbSummarized.isSelected();
     
        grid02.setVisible(!lbShow);
        grid02.setManaged(!lbShow);
        
        if (lbShow){
            grid02.setVisible(!lbShow);
            grid02.setManaged(false);
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
    private boolean loadIncentiveEmployee(){
         Map<String, Object> params = new HashMap<>();
//            if (rbDetailed.isSelected()){
//                params.put("sReportNm", "Branch Incentive Detailed Report");
//            }else {
//                params.put("sReportNm", "Branch Incentive Summary Report");
//            }
            params.put("sPrintdBy", System.getProperty("user.name"));
            params.put("sReportDt", CommonUtils.xsDateLong(oApp.getServerDate()));
            params.put("sCompnyNm", "Guanzon Group of Companies");
            params.put("sBranchNm", oApp.getBranchName());
            params.put("sAddressx", oApp.getAddress());
        try{
            if (rbDetailed.isSelected()){
                 params.put("sReportNm", "Branch Incentive Detailed Report");
                if(oTrans.OpenTransactionEmployee(sPeriodxx)){ 
                    inc_detail.clear();
                    for (int x = 1; x <= oTrans.getItemCount(); x++){
                        inc_detail.add(new IncentiveDetail(
                            String.valueOf(x),
                            oTrans.getDetail(x, "xEmployNm").toString(),
                            oTrans.getDetail(x, "xEmpLevNm").toString(),
                            oTrans.getDetail(x, "xPositnNm").toString(),
                            oTrans.getDetail(x, "xSrvcYear").toString(),
                            oTrans.getDetail(x, "nTotalAmt").toString(),
                            oTrans.getDetail(x, "xBranchNm").toString(),
                            oTrans.getDetail(x, "sMonthxxx").toString(),
                            oTrans.getDetail(x, "sRemarksx").toString(),
                            oTrans.getDetail(x, "sTransNox").toString(),
                            oTrans.getDetail(x, "xBankName").toString(),
                            oTrans.getDetail(x, "xInctvNme").toString()));
                    }
                }
            String sourceFileName = 
            "D://GGC_Java_Systems/reports/Filtered_Incentive_Detail.jasper";
            String printFileName = null;
            JRBeanCollectionDataSource beanColDataSource1 = new JRBeanCollectionDataSource(inc_detail);
            
            try {
                 jasperPrint =JasperFillManager.fillReport(
                        sourceFileName, params, beanColDataSource1);
//               
                printFileName = jasperPrint.toString();
                if(printFileName != null){
                    
                    showReport();
                    
                 }
            } catch (JRException ex) {
                Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
            }
       
        }
        }catch(SQLException e){}
        return true;
    
       
    }
    private boolean loadReport(){
        //Create the parameter
            Map<String, Object> params = new HashMap<>();
//            if (rbDetailed.isSelected()){
//                params.put("sReportNm", "Branch Incentive Detailed Report");
//            }else {
//                params.put("sReportNm", "Branch Incentive Summary Report");
//            }
            params.put("sPrintdBy", System.getProperty("user.name"));
            params.put("sReportDt", CommonUtils.xsDateLong(oApp.getServerDate()));
            params.put("sCompnyNm", "Guanzon Group of Companies");
            params.put("sBranchNm", oApp.getBranchName());
            params.put("sAddressx", oApp.getAddress());
        try{
            if (rbDetailed.isSelected()){
                 params.put("sReportNm", "Branch Incentive Detailed Report");
                if(oTrans.OpenTransaction(sPeriodxx)){ 
                    inc_detail.clear();
                    for (int x = 1; x <= oTrans.getItemCount(); x++){
                        inc_detail.add(new IncentiveDetail(
                            oTrans.getDetail(x, "nEntryNox").toString(),
                            oTrans.getDetail(x, "xEmployNm").toString(),
                            oTrans.getDetail(x, "xEmpLevNm").toString(),
                            oTrans.getDetail(x, "xPositnNm").toString(),
                            oTrans.getDetail(x, "xSrvcYear").toString(),
                            oTrans.getDetail(x, "nTotalAmt").toString(),
                            oTrans.getDetail(x, "xBranchNm").toString(),
                            oTrans.getDetail(x, "sMonthxxx").toString(),
                            oTrans.getDetail(x, "sRemarksx").toString(),
                            oTrans.getDetail(x, "sTransNox").toString(),
                            oTrans.getDetail(x, "xBankName").toString(),
                            oTrans.getDetail(x, "xBankAcct").toString()));
                    }
                }
            String sourceFileName = 
            "D://GGC_Java_Systems/reports/Incentive_Detailed_Report.jasper";
            String printFileName = null;
            JRBeanCollectionDataSource beanColDataSource1 = new JRBeanCollectionDataSource(inc_detail);
            
            try {
                 jasperPrint =JasperFillManager.fillReport(
                        sourceFileName, params, beanColDataSource1);
//               
                printFileName = jasperPrint.toString();
                if(printFileName != null){
                    
                    showReport();
                    
                 }
            } catch (JRException ex) {
                Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
            }
       
        }
        else if(rbSummarized.isSelected()){
            params.put("sReportNm", "Branch Incentive Summary Report");
            if(oTrans.OpenTransactionMaster(sPeriodxx)){ 
            inc_data.clear();
            for (int x = 1; x <= oTrans.getItemMasterCount(); x++){
                String total = oTrans.getMaster(x, "xTotalAmt").toString();
              
                inc_data.add(new IncentiveMaster(
                    String.valueOf(x),
                    oTrans.getMaster(x, "sTransNox").toString(),
                    oTrans.getMaster(x, "xBranchNm").toString(),
                    oTrans.getMaster(x, "sMonthxxx").toString(),
                    String.valueOf(oTrans.OpenToTalMaster(x, oTrans.getMaster(x, "sTransNox").toString()))));
                }
        }
            String sourceFileName = 
            "D://GGC_Java_Systems/reports/Incentives_Summary.jasper";
            String printFileName = null;
            JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(inc_data);
            
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
        } 
    
    }catch(SQLException e){}
        return true;
    }
}
 
    

