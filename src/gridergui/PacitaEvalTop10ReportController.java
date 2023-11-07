package gridergui;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.grewards.base.LMasDetTrans;
import org.rmj.grewards.report.EvaluationTop10;

/**
 * FXML Controller class
 *
 * @author User
 */
public class PacitaEvalTop10ReportController implements Initializable, ScreenInterface{
    private final String pxeModuleName = "Pacita's TOP 10 Report";
    final static int interval = 100;
    private Timeline timeline;
    private Integer timeSeconds = 3;
    private GRider oApp;
    
    private EvaluationTop10 oTrans;
    private LMasDetTrans oListener;
    private final boolean pbLoaded = false;    
    private boolean running = false;
    private JasperPrint jasperPrint;
    private JasperReport jasperReport;
    public String reportCategory;
//    private String sPeriodYearxx = "";
//    private String sPeriodMonthxx = "";
    private String sPeriodFrom = "";
    private String sPeriodTo = "";
     
//    private String sPeriodxx = "";
    private JRViewer jrViewer;
//    private JasperPreview jasperPreview;
    @FXML
    private Button btnGenerate,btnCloseReport;
    @FXML
    private AnchorPane reportPane, AnchorMainRaffleReport;
    @FXML
    private Label lblReportsTitle;
//    @FXML
//    private ComboBox dpPeriodYear,dpPeriodMonth;
    @FXML
    private TextField txtField01;
    @FXML
    private VBox vbProgress;
    @FXML
    private DatePicker dpDateFrom,dpDateTo;
    
    private ObservableList<TableModel> master_data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnGenerate.setOnAction(this::cmdButton_Click);
        btnCloseReport.setOnAction(this::cmdButton_Click);
       
        oListener = new LMasDetTrans() {
            @Override
            public void MasterRetreive(int fnIndex, Object foValue) {
                switch (fnIndex) {
                }
            }
            @Override
            public void DetailRetreive(int fnRow, int fnIndex, Object foValue) {
            }
         };
        
        
        
        oTrans  = new EvaluationTop10(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        initDatePicker();
        initFields();
         
//        dpPeriodMonth.getItems().addAll(getMonthList());
//        dpPeriodYear.getItems().addAll(getYearList());
//        
//        dpPeriodYear.valueProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue != null) {
//                sPeriodYearxx = (String) dpPeriodYear.getValue();
//                System.out.println("Selected year: " + sPeriodYearxx);
//                sPeriodxx = sPeriodYearxx + "-" + sPeriodMonthxx;
//            }
//        });
//        
//        dpPeriodMonth.valueProperty().addListener((observable, oldValue, newValue) -> {
//            int nPeriodMonthxx;
//            if (newValue != null) {
//                if (dpPeriodMonth.getSelectionModel().getSelectedIndex() != 0){
//                    
//                    nPeriodMonthxx =  dpPeriodMonth.getSelectionModel().getSelectedIndex();
//                    System.out.println("Selected month: " + nPeriodMonthxx);
//                    sPeriodMonthxx = "0" + String.valueOf(nPeriodMonthxx);
//                    sPeriodxx = sPeriodYearxx + "-" + sPeriodMonthxx;
//                }else {
//                    sPeriodMonthxx = "" ;
//                    sPeriodxx = sPeriodYearxx + "-" + sPeriodMonthxx;
//                        }
//            }
//        });
    }  
    private Stage getStage(){
	return (Stage) txtField01.getScene().getWindow();
    }
    
//    private List<String> getYearList() {
//        List<String> years = new ArrayList<>();
//        years.add(0, ""); 
//        int currentYear = Year.now().getValue();
//        for (int year = currentYear; year >= 1945; year--) {
//            years.add(Integer.toString(year));
//        }
//        return years;
//    }
//    
//    private List<String> getMonthList() {
//        List<String> monthNames = new ArrayList<>();
//        monthNames.add(0, ""); 
//        
//        for (Month month : Month.values()) {
//        monthNames.add(month.toString());
//        }
//        return monthNames;
//    }
    
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
    
    
    private void initFields(){
        lblReportsTitle.setText(pxeModuleName);
    }
    private void initDatePicker(){
        dpDateFrom.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                getFormattedDateFromDatePicker(dpDateFrom,true);
            }
        });
        dpDateTo.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                getFormattedDateFromDatePicker(dpDateTo,false);
            }
        });
    }
    
    private void cmdButton_Click(ActionEvent event) {
            String lsButton = ((Button)event.getSource()).getId();
            switch (lsButton){
               
                case "btnGenerate":
                    
//                    if(dpPeriod.getValue() == null){
//                        sPeriodYearxx = "";
                            if(sPeriodFrom.isEmpty() || sPeriodTo.isEmpty()){
                                sPeriodFrom = "";
                                sPeriodTo = "";
                                ShowMessageFX.Warning(getStage(), "Period Filter must not be empty","Warning", null);

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

                            LoadReport();
                        }
                } // KeyFrame event handler
            ));
            timeline.playFromStart();
        }
    }
    
    private String getFormattedDateFromDatePicker(DatePicker datePicker, boolean isDateFrom) {
        //Get the selected date
        if(datePicker.getValue() != null){
            LocalDate selectedDate = datePicker.getValue();
        //Create DateTimeFormatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //Convert LocalDate to formatted String
            if(isDateFrom){
                sPeriodFrom = selectedDate.format(formatter);
            }else {
                sPeriodTo = selectedDate.format(formatter);   
            }
        
            return selectedDate.format(formatter);
        }else{
            return "";
        }
    }
    private boolean LoadReport(){
         Map<String, Object> params = new HashMap<>();
            params.put("sPrintdBy", System.getProperty("user.name"));
            params.put("sReportDt", CommonUtils.xsDateLong(oApp.getServerDate()));
            params.put("sCompnyNm", "Guanzon Group of Companies");
            params.put("sBranchNm", oApp.getBranchName());
            params.put("sAddressx", oApp.getAddress());
        try{
                 params.put("sReportNm", pxeModuleName);
                 System.out.println("TO : "+sPeriodFrom+ "FROM : " +sPeriodTo);
                      if(oTrans.OpenRecord(sPeriodFrom,sPeriodTo)){ 
                        master_data.clear();
                        for (int x = 1; x <= oTrans.getItemCount(); x++){
                            
                    master_data.add(new TableModel(
                        oTrans.getRecord(x, "AreaDesc").toString(),
                        oTrans.getRecord(x, "sBranchNm").toString(),
                        oTrans.getRecord(x, "xBranchCount").toString(),
                            CommonUtils.NumberFormat((BigDecimal) oTrans.getRecord(x, "xRating"),"##0.00"),
                        String.valueOf(x)
                    ));

                        }
                    }
                    String sourceFileName = "D://GGC_Java_Systems/reports/PacitaEvalSummarized.jasper";
                    
                    String printFileName = null;
                    JRBeanCollectionDataSource beanColDataSource1 = new JRBeanCollectionDataSource(master_data);

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
 
    

