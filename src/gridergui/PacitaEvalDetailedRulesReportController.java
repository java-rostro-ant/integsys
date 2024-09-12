package gridergui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.Year;
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
import javafx.scene.control.TreeItem;
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
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.view.JasperViewer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.MiscUtil;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.grewards.report.EvaluationDetailedRules;

/**
 * FXML Controller class
 *
 * @author User
 */
public class PacitaEvalDetailedRulesReportController implements Initializable, ScreenInterface {

    private final String pxeModuleName = "Pacita's Detailed w/ Rules Report";
    final static int interval = 100;
    private Timeline timeline;
    private Integer timeSeconds = 3;
    private GRider oApp;

    private String EvalType = "";
    private EvaluationDetailedRules oTrans;
    private boolean pbLoaded = false;
    private boolean running = false;
    private JasperPrint jasperPrint;
    private JasperReport jasperReport;
    public String reportCategory;
    private Date pdPeriodTo = null;
    private Date pdPeriodFrom = null;

    private JRViewer jrViewer;
//    private JasperPreview jasperPreview;
    @FXML
    private Button btnGenerate, btnCloseReport;
    @FXML
    private AnchorPane reportPane, AnchorMainRaffleReport;
    @FXML
    private Label lblReportsTitle;
    @FXML
    private TextField txtField01, txtField02, txtField03, txtField04, txtField05;
    @FXML
    private VBox vbProgress;

    private ObservableList<TableModel> master_data = FXCollections.observableArrayList();
    private final ObservableList<TableModel> detailpacita_data = FXCollections.observableArrayList();
    private final ObservableList<TableModel> detailpacitachild_data = FXCollections.observableArrayList();
    private ObservableList<TableModel> combined_data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnGenerate.setOnAction(this::cmdButton_Click);
        btnCloseReport.setOnAction(this::cmdButton_Click);

        oTrans = new EvaluationDetailedRules(oApp, oApp.getBranchCode(), false);
        oTrans.setWithUI(true);

        initFields();
        txtField01.focusedProperty().addListener(txtField_Focus);
        txtField02.focusedProperty().addListener(txtField_Focus);
        txtField03.focusedProperty().addListener(txtField_Focus);
        txtField04.focusedProperty().addListener(txtField_Focus);
        txtField05.focusedProperty().addListener(txtField_Focus);
        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
        txtField02.setOnKeyPressed(this::txtField_KeyPressed);
        txtField03.setOnKeyPressed(this::txtField_KeyPressed);
        txtField04.setOnKeyPressed(this::txtField_KeyPressed);
        txtField05.setOnKeyPressed(this::txtField_KeyPressed);
        pbLoaded = true;
    }

    private Stage getStage() {
        return (Stage) AnchorMainRaffleReport.getScene().getWindow();
    }

    private List<String> getYearList() {
        List<String> years = new ArrayList<>();
        int currentYear = Year.now().getValue();
        for (int year = currentYear; year >= 1945; year--) {
            years.add(Integer.toString(year));
        }
        return years;
    }

    private void showReport() {

        jrViewer = new JRViewer(jasperPrint);
//        JasperViewer.viewReport(jasperPrint, false);

        SwingNode swingNode = new SwingNode();
        jrViewer.setOpaque(true);
        jrViewer.setVisible(true);
        jrViewer.setFitPageZoomRatio();

        swingNode.setContent(jrViewer);
        swingNode.setVisible(true);
//        reportPane.getChildren().clear();
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
//        jasperPrint = null;
        jrViewer = new JRViewer(null);
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

        String lsPeriodTo = CommonUtils.dateFormat(pdPeriodTo, "yyyy-MM-dd");
        String lsPeriodFrom = CommonUtils.dateFormat(pdPeriodFrom, "yyyy-MM-dd");

        try {
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
                // Clear existing data
                combined_data.clear();
                master_data.clear();
                detailpacita_data.clear();
                detailpacitachild_data.clear();

                // Load master data
                for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++) {
                    master_data.add(new TableModel(
                            String.valueOf(lnCtr),
                            (String) oTrans.getRecord(lnCtr, "sTransNox"),
                            CommonUtils.xsDateLong((Date) oTrans.getRecord(lnCtr, "dTransact")),
                            oTrans.getRecord(lnCtr, "sCompnyNm").toString(),
                            oTrans.getRecord(lnCtr, "sBranchNm").toString(),
                            oTrans.getRecord(lnCtr, "sPayloadx").toString(),
                            oTrans.getRecord(lnCtr, "nRatingxx").toString(),
                            oTrans.getRecord(lnCtr, "cTranStat").toString(),
                            oTrans.getRecord(lnCtr, "AreaDesc").toString(),
                            oTrans.getRecord(lnCtr, "sDeptName").toString()
                    ));
                }

                // Load detail data for each master record
                for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++) {
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(master_data.get(lnCtr - 1).getIndex06());
                    String sEvalType = (String) jsonObject.get("sEvalType");
                    EvalType = sEvalType;
                    loadPacitaDetail(lnCtr - 1, sEvalType);

                }

                JRBeanCollectionDataSource CombineData = new JRBeanCollectionDataSource(combined_data);

                // Generate and show the report
                generateReport(params, CombineData);

            } else {
                running = false;
                vbProgress.setVisible(false);
                timeline.stop();
                Platform.runLater(() -> {
                    ShowMessageFX.Warning(getStage(), oTrans.getMessage(), " Error", null);
                });

            }
        } catch (SQLException | ParseException e) {
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

    public void loadPacitaDetail(int lnRow, String cEvaltype) {
        try {
            if ("015".equals(cEvaltype)) {
                cEvaltype = "4";
            }

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(master_data.get(lnRow).getIndex06());
            JSONArray payloadArray = (JSONArray) jsonObject.get("sPayloadx");
            detailpacita_data.clear();

            if (oTrans.LoadPacita(cEvaltype)) {
                for (int lnCtr = 1; lnCtr <= oTrans.getPacitaItemCount(); lnCtr++) {
                    String jsonxRatingxx = "";
                    String psMaxRating = "";

                    if (!"0.00".equals(oTrans.getPacita(lnCtr, "nMaxValue").toString())) {
                        psMaxRating = oTrans.getPacita(lnCtr, "nMaxValue").toString();
                        JSONObject payloadObject = (JSONObject) payloadArray.get((Integer) oTrans.getPacita(lnCtr, "nEntryNox") - 1);
                        if (payloadObject.get("nEntryNox").toString().equals(oTrans.getPacita(lnCtr, "nEntryNox").toString())) {
                            jsonxRatingxx = "1".equals(payloadObject.get("xRatingxx").toString()) ? "PASSED" : "FAILED";
                        }
                    }

                    detailpacita_data.add(new TableModel(
                            String.valueOf(lnRow + 1),
                            jsonxRatingxx,
                            oTrans.getPacita(lnCtr, "sFieldNmx").toString(),
                            psMaxRating,
                            oTrans.getPacita(lnCtr, "cParentxx").toString()
                    ));
                }

                for (int x = 0; x <= detailpacita_data.size() - 1; x++) {
                    combined_data.add(new TableModel(
                            master_data.get(lnRow).getIndex01(),
                            master_data.get(lnRow).getIndex02(),
                            master_data.get(lnRow).getIndex03(),
                            master_data.get(lnRow).getIndex04(),
                            master_data.get(lnRow).getIndex05(),
                            master_data.get(lnRow).getIndex06(),
                            master_data.get(lnRow).getIndex07(),
                            master_data.get(lnRow).getIndex08(),
                            master_data.get(lnRow).getIndex09(),
                            master_data.get(lnRow).getIndex10(),
                            detailpacita_data.get(x).getIndex02(),
                            detailpacita_data.get(x).getIndex03(),
                            "parent", "", ""
                    ));
                }

                // Load child details if present
                for (TableModel rowDataMaster : detailpacita_data) {
                    if (!"0".equals(rowDataMaster.getIndex05())) {
                        loadPacitaChildDetails(lnRow, cEvaltype, rowDataMaster, payloadArray);
                    }
                }
            } else {
                running = false;
                vbProgress.setVisible(false);
                timeline.stop();
                Platform.runLater(() -> {
                    ShowMessageFX.Warning(getStage(), oTrans.getMessage(), " Error", null);
                });

            }

        } catch (SQLException | ParseException ex) {
            Platform.runLater(() -> {
                ShowMessageFX.Warning(getStage(), ex.getMessage(), "Warning", null);
            });
        }
    }

    private void loadPacitaChildDetails(int lnRow, String cEvaltype, TableModel rowDataMaster, JSONArray payloadArray) {
        detailpacitachild_data.clear();
        try {
            if (oTrans.LoadPacitaChild(cEvaltype, rowDataMaster.getIndex05())) {
                for (int lnCtr = 2; lnCtr <= oTrans.getPacitaChildItemCount(); lnCtr++) {
                    String jsonxRatingxx = "";

                    JSONObject payloadObject = (JSONObject) payloadArray.get((Integer) oTrans.getPacitaChild(lnCtr, "nEntryNox") - 1);
                    if (payloadObject.get("nEntryNox").toString().equals(oTrans.getPacitaChild(lnCtr, "nEntryNox").toString())) {
                        jsonxRatingxx = "1".equals(payloadObject.get("xRatingxx").toString()) ? "PASSED" : "FAILED";
                    }

                    detailpacitachild_data.add(new TableModel(
                            String.valueOf(lnRow + 1),
                            jsonxRatingxx,
                            oTrans.getPacitaChild(lnCtr, "sFieldNmx").toString(),
                            oTrans.getPacitaChild(lnCtr, "nMaxValue").toString(),
                            oTrans.getPacitaChild(lnCtr, "cParentxx").toString()
                    ));

                }

                for (int y = 0; y <= detailpacitachild_data.size() - 1; y++) {
                    combined_data.add(new TableModel(
                            master_data.get(lnRow).getIndex01(),
                            master_data.get(lnRow).getIndex02(),
                            master_data.get(lnRow).getIndex03(),
                            master_data.get(lnRow).getIndex04(),
                            master_data.get(lnRow).getIndex05(),
                            master_data.get(lnRow).getIndex06(),
                            master_data.get(lnRow).getIndex07(),
                            master_data.get(lnRow).getIndex08(),
                            master_data.get(lnRow).getIndex09(),
                            master_data.get(lnRow).getIndex10(),
                            detailpacitachild_data.get(y).getIndex02(),
                            "\t" + detailpacitachild_data.get(y).getIndex03(),
                            "child", "", ""
                    ));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PacitaEvalDetailedRulesReportController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void generateReport(Map<String, Object> params, JRBeanCollectionDataSource JSMainData) {
        try {
            String sourceFileName = "D://GGC_Java_Systems/reports/PacitaEvaluationNew.jasper";

            String printFileName = null;

            jasperPrint = JasperFillManager.fillReport(
                    sourceFileName, params, JSMainData);

            printFileName = jasperPrint.toString();
            if (printFileName != null) {
                showReport();

            }
        } catch (JRException ex) {
            Logger.getLogger(ReportsController.class
                    .getName()).log(Level.SEVERE, null, ex);
            running = false;
            vbProgress.setVisible(false);
            timeline.stop();
        }
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
