/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gridergui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import static gridergui.IncentiveReportsController.dateToWord;
import java.io.IOException;
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
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.StringUtil;
import org.rmj.appdriver.agent.MsgBox;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.Incentive;
import org.rmj.fund.manager.base.IncentiveReports;
import org.rmj.fund.manager.base.LMasDetTrans;
import reportmodel.IncentiveDetail;
import reportmodel.IncentiveMaster;

/**
 * FXML Controller class
 *
 * @author User
 */
public class ShowProgressController implements Initializable, ScreenInterface {  
    private final String pxeModuleName = "AddIncentivesController";
    
    private GRider oApp;
    private int pnIndex = -1;
    private int pnRow = 0;
    
    private boolean pbLoaded = false;
    private boolean state = false;
    private LMasDetTrans oListener;
    
    private boolean rbDetailed,rbSummarized,rbEmployee,rbCategory;
    private String psCode;
    private IncentiveReports oTrans;
    
    private int pnEditMode;
    private String sPeriodxx = "";
    
    private ObservableList<IncentiveDetail> inc_detail = FXCollections.observableArrayList();
    private  ObservableList<IncentiveMaster> inc_data = FXCollections.observableArrayList();
    public static TableModel empModel;
  
    private JasperPrint jasperPrint;
    
    @FXML
    private AnchorPane reportPane;
    
    @FXML
    private Button btnExit;
    
    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
    public void setReportObject(IncentiveReports foValue){
        oTrans = foValue;
    }
    
    public void setIncentiveCode(String fsValue){
        psCode = fsValue;
    }
    public void setJasperPrint(JasperPrint jprint){
        jasperPrint = jprint;
    }
    
    public void setPeriod(String val){
        sPeriodxx = val;
    }
    
    public void setState(boolean fsValue){
        state = fsValue;
    }
    
    private Stage getStage(){
	return (Stage) btnExit.getScene().getWindow();
    }
    public void setSelected(String foVal,String loVal){
        rbDetailed = false;
        rbSummarized = false;
        rbEmployee = false;
        rbCategory = false;
        System.out.println(foVal);
        System.out.println(loVal);
        if(foVal.equalsIgnoreCase("rbDetailed")){
            rbDetailed = true;
            if(loVal.equalsIgnoreCase("rbEmployee")){
                rbEmployee = true;
            }else{
                rbCategory = true;
            }
        }else  if(foVal.equalsIgnoreCase("rbSummarized")){
            rbSummarized = true;
        }
    }
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
            oListener = new LMasDetTrans() {
                @Override
                public void MasterRetreive(int fnIndex, Object foValue) {
                }
                @Override
                public void DetailRetreive(int fnRow, int fnIndex, Object foValue) {               
                }
            };
            
            oTrans.setListener(oListener);
            pnEditMode = oTrans.getEditMode();
            
            pbLoaded = true;
            new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    loadIncentiveEmployee();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ShowProgressController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
         
          btnExit.setOnAction(e->{
              CommonUtils.closeStage(btnExit);          
          });
    }
    
    public static String dateToWord (String dtransact) {
       
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMM");
        try {
            Date date = new Date();
            DateFormat formatter = new SimpleDateFormat("yyyyMM");
            date = (Date)formatter.parse(dtransact);  
            SimpleDateFormat fmt = new SimpleDateFormat("MMM yyyy");
            String todayStr = fmt.format(date);
            
            return todayStr;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
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
            if (rbDetailed){
               
                if(rbEmployee){
                      params.put("sReportNm", "Branch Employee Incentive Detailed Report");
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
                                dateToWord(oTrans.getDetail(x, "sMonthxxx").toString()),
                                oTrans.getDetail(x, "sRemarksx").toString(),
                                oTrans.getDetail(x, "sTransNox").toString(),
                                oTrans.getDetail(x, "xBankName").toString(),
                                oTrans.getDetail(x, "xBankAcct").toString()));

                        }
                    }
                    String sourceFileName = 
                    "D://GGC_Java_Systems/reports/Employee_Incentive_Detailed_Report.jasper";
                    String printFileName = null;
                    JRBeanCollectionDataSource beanColDataSource1 = new JRBeanCollectionDataSource(inc_detail);

                    try {
                         jasperPrint =JasperFillManager.fillReport(
                                sourceFileName, params, beanColDataSource1);
        //               
                        printFileName = jasperPrint.toString();
                        if(printFileName != null){
//                            IncentiveReportsController.setIncDetail(inc_detail);
//                           IncentiveReportsController.setIncData(inc_data);
//                           IncentiveReportsController.setJasperPrint(jasperPrint);
//                           IncentiveReportsController.showReport();
                             // get a handle to the stage
                            btnExit.fire();
                           
                         }
                    } catch (JRException ex) {
                        Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else if(rbCategory){
                      params.put("sReportNm", "Branch Employee and Category Incentive Detailed Report");
                    if(oTrans.OpenTransactionCategory(sPeriodxx)){ 
                        inc_detail.clear();
                        System.out.println("detail count = " + oTrans.getItemCount());
                        for (int x = 1; x <= oTrans.getItemCount(); x++){
                            inc_detail.add(new IncentiveDetail(
                                String.valueOf(x),
                                oTrans.getDetailCategory(x, "xEmployNm").toString(),
                                oTrans.getDetailCategory(x, "xDeductnx").toString(),
                                oTrans.getDetailCategory(x, "xPositnNm").toString(),
                                oTrans.getDetailCategory(x, "xIncentve").toString(),
                                oTrans.getDetailCategory(x, "nTotalAmt").toString(),
                                oTrans.getDetailCategory(x, "xBranchNm").toString(),
                                dateToWord(oTrans.getDetailCategory(x, "sMonthxxx").toString()),
                                oTrans.getDetailCategory(x, "sRemarksx").toString(),
                                oTrans.getDetailCategory(x, "sTransNox").toString(),
                                oTrans.getDetailCategory(x, "sInctveDs").toString(),
                                oTrans.getDetailCategory(x, "xBankAcct").toString()));

                        }
                    }
                    String sourceFileName = 
                    "D://GGC_Java_Systems/reports/Category_Incentive_Detailed_Report.jasper";
                    String printFileName = null;
                    JRBeanCollectionDataSource beanColDataSource1 = new JRBeanCollectionDataSource(inc_detail);

                    try {
                         jasperPrint =JasperFillManager.fillReport(
                                sourceFileName, params, beanColDataSource1);
        //               
                        printFileName = jasperPrint.toString();
                        if(printFileName != null){

//                           IncentiveReportsController.setIncDetail(inc_detail);
//                           IncentiveReportsController.setIncData(inc_data);
//                           IncentiveReportsController.setJasperPrint(jasperPrint);
//                           IncentiveReportsController.showReport();
                            btnExit.fire();
                         }
                    } catch (JRException ex) {
                        Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
       
            }else if(rbSummarized){
            params.put("sReportNm", "Branch Incentive Summary Report");
            if(oTrans.OpenTransactionMaster(sPeriodxx)){ 
            inc_data.clear();
            for (int x = 1; x <= oTrans.getItemMasterCount(); x++){
                String total = oTrans.getMaster(x, "xTotalAmt").toString();
              
                inc_data.add(new IncentiveMaster(
                    String.valueOf(x),
                    oTrans.getMaster(x, "sTransNox").toString(),
                    oTrans.getMaster(x, "xBranchNm").toString(),
                    dateToWord(oTrans.getMaster(x, "sMonthxxx").toString()),
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
                    
                    btnExit.fire();
                    CommonUtils.closeStage(btnExit);
                 }
            } catch (JRException ex) {
                Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        }catch(SQLException e){}
        return true;
    
       
    }
}
