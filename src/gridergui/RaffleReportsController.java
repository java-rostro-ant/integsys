/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gridergui;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.management.timer.Timer;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.StringHelper;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.fund.manager.base.LMasDetTrans;
import org.rmj.fund.manager.base.RaffleReport;

/**
 * FXML Controller class
 *
 * @author User
 */
public class RaffleReportsController implements Initializable, ScreenInterface{
    private final String pxeModuleName = "Raffle's Report";
    final static int interval = 100;
    private Timeline timeline;
    private Integer timeSeconds = 3;
    private GRider oApp;
    
    private RaffleReport oTrans;
    private LMasDetTrans oListener;
    private final boolean pbLoaded = false;    
    private boolean running = false;
    private JasperPrint jasperPrint;
    private JasperReport jasperReport;
    public String reportCategory;
    private ToggleGroup rbGroup;
    private String sPeriodxx = "";
    private JRViewer jrViewer;
//    private JasperPreview jasperPreview;
    @FXML
    private Button btnGenerate,btnCloseReport;
    @FXML
    private AnchorPane reportPane, AnchorMainRaffleReport;
    @FXML
    private Label lblReportsTitle;
    @FXML
    private DatePicker dpPeriod;
    @FXML
    private TextField txtField01;
    @FXML
    private ComboBox cbStatus;
    @FXML
    private VBox vbProgress;
    
    private ObservableList<RaffleReportsModel> raffle_data = FXCollections.observableArrayList();
    ObservableList<String> cStatus = FXCollections.observableArrayList( "ALL", "WINNER", "NON-WINNER");
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
//                    case 18:
//                        txtField01.setText((String) foValue);
//                        break;
                }
            }
            @Override
            public void DetailRetreive(int fnRow, int fnIndex, Object foValue) {
            }
         };
        
        oTrans  = new RaffleReport(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
//        oTrans.setTranStat(12);
        oTrans.setWithUI(true);
        initStatus();
        initDatePicker();
        initFields();
        
//        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
    }  
    private Stage getStage(){
	return (Stage) txtField01.getScene().getWindow();
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
    private void initStatus(){
        cbStatus.setItems(cStatus);
        cbStatus.getSelectionModel().select(0);
        
    }
    private void showReport(){
        
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
        reportPane.setVisible(true);
        running = false;
        vbProgress.setVisible(false);
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
        vbProgress.setVisible(true);
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
                            if(oTrans.SearchRecord(lsValue, false)) {
//                                txtField01.setText((String) oTrans.ge("sBranchNm"));
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
        lblReportsTitle.setText("RAFFLE REPORT");
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
    private String getFormattedDateFromDatePicker(DatePicker datePicker) {
        //Get the selected date
        if(datePicker.getValue() != null){
            LocalDate selectedDate = datePicker.getValue();
        //Create DateTimeFormatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
//                    if(txtField01.getText().isEmpty()){
//                        oTrans.setBranch();
//                    }
//                    if(dpPeriod.getValue() == null){
//                        sPeriodxx = "";
                            if(dpPeriod.getValue() == null){
                                sPeriodxx = "";
                                ShowMessageFX.Warning(getStage(), "Raffle period must not be empty","Warning", null);

                                vbProgress.setVisible(false);
                            }else{
                                generateReport();
                            }
                        
//                    }
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

                            loadRaffleWinners();
                        }
                } // KeyFrame event handler
            ));
            timeline.playFromStart();
        }
    }
    
    
    private boolean loadRaffleWinners(){
         Map<String, Object> params = new HashMap<>();
            params.put("sPrintdBy", System.getProperty("user.name"));
            params.put("sReportDt", CommonUtils.xsDateLong(oApp.getServerDate()));
            params.put("sCompnyNm", "Guanzon Group of Companies");
            params.put("sBranchNm", oApp.getBranchName());
            params.put("sAddressx", oApp.getAddress());
        try{
            if(cbStatus.getSelectionModel().getSelectedIndex() == 0){
                oTrans.setTranStat(10);
            }else if(cbStatus.getSelectionModel().getSelectedIndex() == 1){
                oTrans.setTranStat(1);
            }else if(cbStatus.getSelectionModel().getSelectedIndex() == 2){
                oTrans.setTranStat(0);
            }
            
                 params.put("sReportNm", "Raffle Detailed Report");
                      if(oTrans.OpenRecord(sPeriodxx)){ 
                        raffle_data.clear();
                        for (int x = 1; x <= oTrans.getItemCount(); x++){
                            
                    raffle_data.add(new RaffleReportsModel(
                        String.valueOf(x),
                        oTrans.getRecord(x, "sCompanyNm").toString(),
                        oTrans.getRecord(x, "cCallStat").toString(),
                        oTrans.getRecord(x, "sPanaloDs").toString(),
                        oTrans.getRecord(x, "nAmountxx").toString(),
                        oTrans.getRecord(x, "sMobileNox").toString(),
                        oTrans.getRecord(x, "cTranstat").toString(),
                        oTrans.getRecord(x, "sBranchNme").toString(),
                        StringHelper.prepad((String) oTrans.getRecord(x,"sRaffleNo"), 6, '0'),
                        oTrans.getRecord(x, "nItemQtyx").toString(),
                        oTrans.getRecord(x, "xAddressx").toString(),
                        oTrans.getRecord(x, "sAcctNmbr").toString()
                    ));

                        }
                    }
                    String sourceFileName = "D://GGC_Java_Systems/reports/RaffleWinners.jasper";
                    
                    String printFileName = null;
                    JRBeanCollectionDataSource beanColDataSource1 = new JRBeanCollectionDataSource(raffle_data);

                    try {
                         jasperPrint =JasperFillManager.fillReport(
                                sourceFileName, params, beanColDataSource1);
                       
                        printFileName = jasperPrint.toString();
                        if(printFileName != null){
                             showReport();
                         }
                    } catch (JRException ex) {
                        Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
                        running = false;
                        vbProgress.setVisible(false);
                        timeline.stop();
                    }
                
            
        }catch(SQLException e){
            running = false;
            vbProgress.setVisible(false);
            timeline.stop();
        }
        return true;
    }

    private void unloadForm(){
        StackPane myBox = (StackPane) AnchorMainRaffleReport.getParent();
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
 
    

