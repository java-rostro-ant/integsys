package gridergui;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.grewards.base.LMasDetTrans;
import org.rmj.grewards.report.EvaluationSummarizedOfficer;

/**
 * FXML Controller class
 *
 * @author User
 */
public class PacitaEvalSummarizedOfficerReportController implements Initializable, ScreenInterface {

    private final String pxeModuleName = "Pacita's Summarized w/Officer Report";
    final static int interval = 100;
    private Timeline timeline;
    private Integer timeSeconds = 3;
    private GRider oApp;

    private EvaluationSummarizedOfficer oTrans;
    private LMasDetTrans oListener;
    private boolean pbLoaded = false;
    private boolean running = false;
    private JasperPrint jasperPrint;
    private JasperReport jasperReport;
    public String reportCategory;

    private Date pdPeriodTo = null;
    private Date pdPeriodFrom = null;

//    private String sPeriodxx = "";
    private JRViewer jrViewer;
//    private JasperPreview jasperPreview;
    @FXML
    private Button btnGenerate, btnCloseReport;
    @FXML
    private AnchorPane reportPane, AnchorMainRaffleReport;
    @FXML
    private Label lblReportsTitle;
//    @FXML
//    private ComboBox dpPeriodYear,dpPeriodMonth;
    @FXML
    private TextField txtField01, txtField02, txtField03, txtField04, txtField05, txtField06;
    @FXML
    private VBox vbProgress;
//    @FXML
//    private DatePicker dpDateFrom,dpDateTo;

    private ObservableList<TableModel> master_data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnGenerate.setOnAction(this::cmdButton_Click);
        btnCloseReport.setOnAction(this::cmdButton_Click);

        oTrans = new EvaluationSummarizedOfficer(oApp, oApp.getBranchCode(), false);
        oTrans.setWithUI(true);

        initFields();
        txtField01.focusedProperty().addListener(txtField_Focus);
        txtField02.focusedProperty().addListener(txtField_Focus);
        txtField03.focusedProperty().addListener(txtField_Focus);
        txtField04.focusedProperty().addListener(txtField_Focus);
        txtField05.focusedProperty().addListener(txtField_Focus);
        txtField06.focusedProperty().addListener(txtField_Focus);
        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
        txtField02.setOnKeyPressed(this::txtField_KeyPressed);
        txtField03.setOnKeyPressed(this::txtField_KeyPressed);
        txtField04.setOnKeyPressed(this::txtField_KeyPressed);
        txtField05.setOnKeyPressed(this::txtField_KeyPressed);
        txtField06.setOnKeyPressed(this::txtField_KeyPressed);
        pbLoaded = true;
    }

    private Stage getStage() {
        return (Stage) txtField01.getScene().getWindow();
    }

    private void showReport() {

        jrViewer = new JRViewer(jasperPrint);

        SwingNode swingNode = new SwingNode();
        jrViewer.setOpaque(true);
        jrViewer.setVisible(true);
        jrViewer.setFitPageZoomRatio();

        swingNode.setContent(jrViewer);
        swingNode.setVisible(true);
        reportPane.setTopAnchor(swingNode, 0.0);
        reportPane.setBottomAnchor(swingNode, 0.0);
        reportPane.setLeftAnchor(swingNode, 0.0);
        reportPane.setRightAnchor(swingNode, 0.0);
        reportPane.getChildren().add(swingNode);
        reportPane.setVisible(true);
        running = false;
        vbProgress.setVisible(false);
        timeline.stop();

    }

    private void hideReport() {
        jrViewer = new JRViewer(null);
        reportPane.getChildren().clear();

        jrViewer.setVisible(false);
        running = false;
        reportPane.setVisible(false);
        vbProgress.setVisible(true);
    }

    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }

    private void txtField_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();
        int lnIndex = Integer.parseInt(((TextField) event.getSource()).getId().substring(8, 10));
        String lsValue = txtField.getText();

        try {
            switch (event.getCode()) {
                case F3:
                case ENTER:
                    switch (lnIndex) {

                        case 3:
                            /*search officer*/
                            if (oTrans.searchOfficer(lsValue, false)) {
                                txtField03.setText((String) oTrans.getOfficer("sCompnyNm"));
                            } else {
                                ShowMessageFX.Warning(getStage(), "Unable to search officer.", "Warning", null);
                            }
                            break;
                        case 4:
                            /*search branch area*/
                            if (oTrans.searchBranchArea(lsValue, false)) {
                                txtField04.setText((String) oTrans.getBranchArea("sAreaDesc"));
                            } else {
                                ShowMessageFX.Warning(getStage(), "Unable to search branch area.", "Warning", null);
                            }
                            break;
                        case 5:
                            /*search branch*/
                            if (oTrans.searchBranch(lsValue, false)) {
                                txtField05.setText((String) oTrans.getBranch("sBranchNm"));
                            } else {
                                ShowMessageFX.Warning(getStage(), "Unable to search branch.", "Warning", null);
                            }
                            break;
                        case 6:
                            /*search division*/
                            if (oTrans.searchDivision(lsValue, false)) {
                                txtField06.setText((String) oTrans.getDivision("sDivsnDsc"));
                            } else {
                                ShowMessageFX.Warning(getStage(), "Unable to search division.", "Warning", null);
                            }
                            break;

                    }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        switch (event.getCode()) {
            case ENTER:
            case DOWN:
                CommonUtils.SetNextFocus(txtField);
                break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        }
    }

    final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {
        if (!pbLoaded) {
            return;
        }

        try {
            TextField txtField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
            int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
            String lsValue = txtField.getText();

            if (!nv) {
                /*Lost Focus*/
                switch (lnIndex) {
                    case 1:
                        if (lsValue == null || lsValue == "") {
                            return;
                        }
                        if (lsValue.trim().length() > 8 && lsValue.trim().length() <= 11) {
                            pdPeriodTo = SQLUtil.toDate(lsValue, "yyyy-MM-dd");
                            txtField.setText(CommonUtils.dateFormat(pdPeriodTo, "yyyy MMMM dd"));
                        } else {
                            pdPeriodTo = null;
                            txtField.setText("");
                        }
                        return;

                    case 2:
                        if (lsValue == null || lsValue == "") {
                            return;
                        }
                        if (lsValue.trim().length() > 8 && lsValue.trim().length() <= 11) {
                            pdPeriodFrom = SQLUtil.toDate(lsValue, "yyyy-MM-dd");
                            txtField.setText(CommonUtils.dateFormat(pdPeriodFrom, "yyyy MMMM dd"));
                        } else {
                            pdPeriodFrom = null;
                            txtField.setText("");
                        }
                        return;

                    case 3:
                        if (lsValue.isEmpty() || oTrans.getOfficer() == null) {
                            oTrans.setOfficer();
                            txtField.setText("");
                        }
                        return;
                    case 4:
                        if (lsValue.isEmpty() || oTrans.getBranchArea() == null) {
                            oTrans.setBranchArea();
                            txtField.setText("");
                        }
                        return;
                    case 5:
                        if (lsValue.isEmpty() || oTrans.getBranch() == null) {
                            oTrans.setBranch();
                            txtField.setText("");
                        }
                        return;
                    case 6:
                        if (lsValue.isEmpty() || oTrans.getDivision() == null) {
                            oTrans.setDivision();
                            txtField.setText("");
                        }
                        return;

                }
            } else { //Focus
                switch (lnIndex) {
                    case 1:
                        if (pdPeriodTo != null) {
                            txtField.setText(CommonUtils.dateFormat(pdPeriodTo, "yyyy-MM-dd"));
                        }
                        return;
                    case 2:
                        if (pdPeriodFrom != null) {
                            txtField.setText(CommonUtils.dateFormat(pdPeriodFrom, "yyyy-MM-dd"));
                        }
                        return;
                    case 3:
                        if (!lsValue.trim().isEmpty()) {
                            txtField.setText((String) oTrans.getOfficer(2));
                        }
                        return;
                    case 4:
                        if (!lsValue.trim().isEmpty()) {
                            txtField.setText((String) oTrans.getBranchArea(2));
                        }
                        return;
                    case 5:
                        if (!lsValue.trim().isEmpty()) {
                            txtField.setText((String) oTrans.getBranch(2));
                        }
                        return;

                }
                txtField.selectAll();
            }
        } catch (SQLException ex) {
            Platform.runLater(() -> {
                ShowMessageFX.Warning(getStage(), ex.getMessage(), "Catch Error", null);
            });

        }
    };

    private void initFields() {
        lblReportsTitle.setText(pxeModuleName);
    }

    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button) event.getSource()).getId();
        switch (lsButton) {

            case "btnGenerate":

                if (txtField03.getText().isEmpty()) {
                    oTrans.setOfficer();
                }
                if (txtField04.getText().isEmpty()) {
                    oTrans.setBranchArea();
                }
                if (txtField05.getText().isEmpty()) {
                    oTrans.setBranch();
                }
                if (txtField06.getText().isEmpty()) {
                    oTrans.setDivision();
                }

//                    if(dpPeriod.getValue() == null){
//                        sPeriodxx = "";
                if (pdPeriodTo == null || pdPeriodFrom == null) {
                    ShowMessageFX.Warning(getStage(), "Date Criteria must not be empty", "Warning", null);

                    vbProgress.setVisible(false);
                } else {
                    generateReport();
                }

//                    }
                break;

            case "btnCloseReport":
                if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Are you sure, do you want to close?") == true) {
                    unloadForm();
                    break;
                }
                return;
        }
    }

    private void generateReport() {
        hideReport();
        if (!running) {
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(1), (ActionEvent event1) -> {
                        timeSeconds--;
                        // update timerLabel
                        if (timeSeconds <= 0) {
                            timeSeconds = 0;
                        }
                        if (timeSeconds == 0) {

                            LoadReport();
                        }
                    } // KeyFrame event handler
                    ));
            timeline.playFromStart();
        }
    }

    private boolean LoadReport() {
        Map<String, Object> params = new HashMap<>();
        params.put("sPrintdBy", System.getProperty("user.name"));
        params.put("sReportDt", CommonUtils.xsDateLong(oApp.getServerDate()));
        params.put("sCompnyNm", "Guanzon Group of Companies");
        params.put("sBranchNm", oApp.getBranchName());
        params.put("sAddressx", oApp.getAddress());
        try {
            params.put("sReportNm", pxeModuleName);
            String lsPeriodTo = CommonUtils.dateFormat(pdPeriodTo, "yyyy-MM-dd");
            String lsPeriodFrom = CommonUtils.dateFormat(pdPeriodFrom, "yyyy-MM-dd");

            params.put("sReportNm", pxeModuleName);
            if (oTrans.OpenRecord(lsPeriodTo, lsPeriodFrom)) {
                int lnItemCount = oTrans.getItemCount();

                if (lnItemCount <= 0) {
                    running = false;
                    vbProgress.setVisible(false);
                    timeline.stop();

                    Platform.runLater(() -> {
                        ShowMessageFX.Warning(getStage(), "No Record Found", "Information", null);
                    });
                    return false;
                }
                System.out.println("TotalData  = " + lnItemCount);
                master_data.clear();
                for (int x = 1; x <= oTrans.getItemCount(); x++) {
                    double lnRating = Double.valueOf(CommonUtils.NumberFormat((BigDecimal) oTrans.getRecord(x, "xRating"), "##0.00"));
                    int lnCount = Integer.parseInt(oTrans.getRecord(x, "xBranchCount").toString());
                    double lnTotalRating = Double.valueOf(CommonUtils.NumberFormat((BigDecimal) oTrans.getRecord(x, "xTotalAverage"), "##0.00"));
                    master_data.add(new TableModel(
                            oTrans.getRecord(x, "AreaDesc").toString(),
                            oTrans.getRecord(x, "sBranchNm").toString(),
                            oTrans.getRecord(x, "sCompnyNm").toString(),
                            lnCount,
                            lnRating,
                            lnTotalRating
                    ));

                }
            } else {
                running = false;
                vbProgress.setVisible(false);
                timeline.stop();
                Platform.runLater(() -> {
                    ShowMessageFX.Warning(getStage(), oTrans.getMessage(), " Error", null);
                });

            }
            String sourceFileName = "D://GGC_Java_Systems/reports/PacitaEvalSummarizedOfficer.jasper";

            String printFileName = null;
            JRBeanCollectionDataSource beanColDataSource1 = new JRBeanCollectionDataSource(master_data);

            try {
                jasperPrint = JasperFillManager.fillReport(
                        sourceFileName, params, beanColDataSource1);

                printFileName = jasperPrint.toString();
                if (printFileName != null) {
                    showReport();
                }
            } catch (JRException ex) {
                Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
                running = false;
                vbProgress.setVisible(false);
                timeline.stop();
                Platform.runLater(() -> {
                    ShowMessageFX.Warning(getStage(), oTrans.getMessage() + " " + ex.getMessage(), "Catch Error", null);
                });
                return false;
            }

        } catch (SQLException e) {
            running = false;
            vbProgress.setVisible(false);
            timeline.stop();
            Platform.runLater(() -> {
                ShowMessageFX.Warning(getStage(), oTrans.getMessage() + " " + e.getMessage(), "Catch Error", null);
            });
            return false;
        }
        return true;

    }

    private void unloadForm() {
        StackPane myBox = (StackPane) AnchorMainRaffleReport.getParent();
        myBox.getChildren().clear();
        myBox.getChildren().add(getScene("MainScreenBG.fxml"));
    }

    private AnchorPane getScene(String fsFormName) {
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
