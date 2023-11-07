package gridergui;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
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
import org.rmj.appdriver.MiscUtil;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.grewards.base.LMasDetTrans;
import org.rmj.grewards.report.EvaluationSummarized;

/**
 * FXML Controller class
 *
 * @author User
 */
public class PacitaEvalSummarizedReportController implements Initializable, ScreenInterface{
    private final String pxeModuleName = "Pacita's Summary Report";
    final static int interval = 100;
    private Timeline timeline;
    private Integer timeSeconds = 3;
    private GRider oApp;
    
    private EvaluationSummarized oTrans;
    private LMasDetTrans oListener;
    private final boolean pbLoaded = false;    
    private boolean running = false;
    private JasperPrint jasperPrint;
    private JasperReport jasperReport;
    public String reportCategory;
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
    private ComboBox dpPeriod;
    @FXML
    private TextField txtField01,txtField02;
    @FXML
    private VBox vbProgress;
    
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
        
        
        
        oTrans  = new EvaluationSummarized(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        
        initFields();
        
        txtField02.setOnKeyPressed(this::txtField_KeyPressed);       
        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
        List<String> years = getYearList();
        dpPeriod.getItems().addAll(getYearList());
        dpPeriod.setValue(years.get(0));
        dpPeriod.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                sPeriodxx = (String) dpPeriod.getValue();
                System.out.println("Selected year: " + sPeriodxx);
            }
        });
    }  
    private Stage getStage(){
	return (Stage) txtField01.getScene().getWindow();
    }
    
    private List<String> getYearList() {
        List<String> years = new ArrayList<>();
        int currentYear = Year.now().getValue();
        for (int year = currentYear; year >= 1945; year--) {
            years.add(Integer.toString(year));
        }
        return years;
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
                        case 1: /*search branch*/
                            if(oTrans.searchBranch(lsValue, false)) {
                                txtField01.setText((String) oTrans.getBranch("sBranchNm"));
                            } else{
                               ShowMessageFX.Warning(getStage(), "Unable to search branch.", "Warning", null);
                            }
                            break;
                        case 2: /*search officer*/
                            if(oTrans.searchOfficer(lsValue, false)) {
                                txtField02.setText((String) oTrans.getOfficer("sCompnyNm"));
                            } else{
                               ShowMessageFX.Warning(getStage(), "Unable to search officer.", "Warning", null);
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
        lblReportsTitle.setText(pxeModuleName);
    }
    
    private void cmdButton_Click(ActionEvent event) {
            String lsButton = ((Button)event.getSource()).getId();
            switch (lsButton){
               
                case "btnGenerate":
                    if(txtField01.getText().isEmpty()){
                        oTrans.setBranch();
                    }
                    if(txtField02.getText().isEmpty()){
                        oTrans.setOfficer();
                    }
//                    if(dpPeriod.getValue() == null){
//                        sPeriodxx = "";
                            if(dpPeriod.getValue() == null){
                                sPeriodxx = "";
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
    
    
    private boolean LoadReport(){
         Map<String, Object> params = new HashMap<>();
            params.put("sPrintdBy", System.getProperty("user.name"));
            params.put("sReportDt", CommonUtils.xsDateLong(oApp.getServerDate()));
            params.put("sCompnyNm", "Guanzon Group of Companies");
            params.put("sBranchNm", oApp.getBranchName());
            params.put("sAddressx", oApp.getAddress());
        try{
                 params.put("sReportNm", pxeModuleName);
                      if(oTrans.OpenRecord(sPeriodxx)){ 
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
 
    

