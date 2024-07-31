package gridergui;

import java.io.IOException;
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
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
import org.rmj.fund.manager.base.IncentiveReportNew;
import reportmodel.IncentiveDetail;

/**
 * FXML Controller class
 *
 * @author Maynard 07/18/2024
 */
public class IncentiveReportsNewController implements Initializable, ScreenInterface {

    private final String pxeModuleName = "Employee Incentives Report";
    final static int interval = 100;
    private Timeline timeline;
    private Integer timeSeconds = 3;

    private GRider oApp;
    private IncentiveReportNew oTrans;

    public String reportCategory;
    private Date pdPeriod = null;

    private boolean pbLoaded = false;
    private boolean running = false;

    private JasperPrint jasperPrint;
    private JasperReport jasperReport;
    private JRViewer jrViewer;
    private ToggleGroup rbGroup, rbGroupSummarized;

    @FXML
    private AnchorPane AnchorMainIncentiveReport;
    @FXML
    private Label lblReportsTitle;
    @FXML
    private Button btnGenerate, btnCloseReport;
    @FXML
    private TextField txtField01, txtField02, txtField03, txtField04, txtField05;
    @FXML
    private RadioButton rbDetailed, rbSummarized;
    @FXML
    private RadioButton rbEmployee, rbEmployeeCategory, rbBranchCategory;
    @FXML
    private VBox vbProgress;
    @FXML
    private AnchorPane reportPane;
    @FXML
    private GridPane frGridPane01, frGridPane02, frGridPane03,
            frGridPane04, frGridPane05, frGridPane06;
    @FXML
    private ComboBox cmbStatus;

    ObservableList<String> cStatus = FXCollections.observableArrayList(
            "OPEN", "FOR APPROVAL", "APPROVED", "CANCELLED", "RELEASED", "ALL");

    @FXML
    void cmbStatus_Clicked(ActionEvent event) {
        int lnSelectIndex = cmbStatus.getSelectionModel().getSelectedIndex();

        switch (lnSelectIndex) {
            case 0://open
            case 1://for approval
            case 2://approved
            case 3://cancelled
            case 4://released
                oTrans.setTranStat(lnSelectIndex);
                return;
            case 5://all status 
                oTrans.setTranStat(-1);
                break;

        }

    }

    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }

    private ObservableList<IncentiveDetail> IncentiveData = FXCollections.observableArrayList();

    public void setReportCategory(String foValue) {
        System.out.println(foValue);
        reportCategory = foValue;
    }

    private Stage getStage() {
        return (Stage) AnchorMainIncentiveReport.getScene().getWindow();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        oTrans = new IncentiveReportNew(oApp, oApp.getBranchCode(), false);
        oTrans.setWithUI(true);

        txtField01.focusedProperty().addListener(txtField_Focus);
        txtField02.focusedProperty().addListener(txtField_Focus);
        txtField03.focusedProperty().addListener(txtField_Focus);
        txtField04.focusedProperty().addListener(txtField_Focus);
        txtField05.focusedProperty().addListener(txtField_Focus);
        initStatus();
        initToggleGroup();
        initGrid();
        initFields();

        btnGenerate.setOnAction(this::cmdButton_Click);
        btnCloseReport.setOnAction(this::cmdButton_Click);
        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
        txtField02.setOnKeyPressed(this::txtField_KeyPressed);
        txtField03.setOnKeyPressed(this::txtField_KeyPressed);
        txtField04.setOnKeyPressed(this::txtField_KeyPressed);
        txtField05.setOnKeyPressed(this::txtField_KeyPressed);
        pbLoaded = true;
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
                        if (lsValue.trim().length() > 4 && lsValue.trim().length() <= 7) {
                            pdPeriod = SQLUtil.toDate(lsValue.trim() + " 01", "yyyyMM dd");
                            txtField.setText(CommonUtils.dateFormat(pdPeriod, "yyyy MMMM"));
                        } else {
                            pdPeriod = null;
                            txtField.setText("");
                        }
                        return;

                    case 2:
                        if (lsValue.isEmpty() || oTrans.getBranch() == null) {
                            oTrans.setBranch();
                            txtField.setText("");
                        }
                        return;

                    case 3:
                        if (lsValue.isEmpty() || oTrans.getDivision() == null) {
                            oTrans.setDivision();
                            txtField.setText("");
                        }
                        return;
                    case 4:
                        if (lsValue.isEmpty() || oTrans.getCategory() == null) {
                            oTrans.setCategory();
                            txtField.setText("");
                        }
                        return;
                    case 5:
                        if (lsValue.isEmpty() || oTrans.getBranchArea() == null) {
                            oTrans.setBranchArea();
                            txtField.setText("");
                        }
                        return;

                }
            } else { //Focus
                switch (lnIndex) {
                    case 1:
                        if (pdPeriod != null) {
                            txtField.setText(CommonUtils.dateFormat(pdPeriod, "YYYY MM"));
                        }
                        return;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        if (!lsValue.trim().isEmpty()) {
                            txtField.setText((String) oTrans.getFilter(lnIndex, "xxColName"));
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

    private void initStatus() {
        cmbStatus.setItems(cStatus);
        cmbStatus.getSelectionModel().select(5);
        oTrans.setTranStat(-1);

    }

    private void initToggleGroup() {
        rbGroup = new ToggleGroup();
        rbGroupSummarized = new ToggleGroup();

        rbDetailed.setToggleGroup(rbGroup);
        rbSummarized.setToggleGroup(rbGroup);

        rbEmployee.setToggleGroup(rbGroupSummarized);
        rbEmployeeCategory.setToggleGroup(rbGroupSummarized);
//        rbBranch.setToggleGroup(rbGroupSummarized);
        rbBranchCategory.setToggleGroup(rbGroupSummarized);

        rbDetailed.setSelected(true);
        rbEmployee.setSelected(true);

        rbGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ob,
                    Toggle o, Toggle n) {
                initGrid();
            }
        });

        rbGroupSummarized.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ob,
                    Toggle o, Toggle n) {
                initGrid();
            }
        });

    }

    private void initGrid() {
        boolean lbShow = rbSummarized.isSelected();

        frGridPane02.setVisible(lbShow);
        frGridPane02.setManaged(lbShow);
        frGridPane03.setManaged(true);
        frGridPane05.setVisible(false);

        if (lbShow) {
            frGridPane05.setVisible(rbEmployeeCategory.isSelected() || rbBranchCategory.isSelected());
        }
    }

    private void initFields() {
        lblReportsTitle.setText(reportCategory + " REPORT");
    }

    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button) event.getSource()).getId();
        switch (lsButton) {

            case "btnGenerate":
                if (txtField02.getText().trim().isEmpty()) {
                    oTrans.setBranch();
                }
                if (txtField03.getText().trim().isEmpty()) {
                    oTrans.setDivision();
                }
                if (txtField04.getText().trim().isEmpty()) {
                    oTrans.setCategory();
                }
                if (txtField05.getText().trim().isEmpty()) {
                    oTrans.setBranchArea();
                }

                if (pdPeriod == null) {
                    ShowMessageFX.Warning(getStage(), "Incentive period must not be empty", "Warning", null);
                    vbProgress.setVisible(false);
                } else {
                    generateReport();
                }

                break;
            case "btnCloseReport":
                if (ShowMessageFX.OkayCancel(getStage(), null, pxeModuleName, "Are you sure, do you want to close?") == true) {
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

                            loadIncentiveReport();
                        }
                    } // KeyFrame event handler
                    ));
            timeline.play();
        }
    }

    private boolean loadIncentiveReport() {
        RadioButton lnSelectedRBMain = (RadioButton) rbGroup.getSelectedToggle();
        String lnRBMain = lnSelectedRBMain.getId();
        RadioButton lnSelectedRBDetail = (RadioButton) rbGroupSummarized.getSelectedToggle();
        String lnRBDetail = lnSelectedRBDetail.getId();
        String lsPeriod = CommonUtils.dateFormat(pdPeriod, "YYYYMM");
        System.out.println("nMonthxx = " + lsPeriod);
        switch (lnRBMain) {

            case "rbDetailed":
                return loadIncentiveDetailed(lsPeriod);

            case "rbSummarized":
                switch (lnRBDetail) {
                    case "rbEmployee":
                        return loadIncentiveSummaryEmployee(lsPeriod);
                    case "rbEmployeeCategory":
                        return loadIncentiveSumEmployeeCategory(lsPeriod);
                    case "rbBranchCategory":
                        return loadIncentiveSumBranchCategory(lsPeriod);

                }
                break;
        }
        return false;
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

    private void txtField_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();
        int lnIndex = Integer.parseInt(((TextField) event.getSource()).getId().substring(8, 10));
        String lsValue = txtField.getText();

        try {
            switch (event.getCode()) {
                case F3:
                case ENTER:
                    switch (lnIndex) {
                        case 2:
                        /*search branch*/
                        case 3:
                        /*search division*/
                        case 4:
                        /*search category*/
                        case 5:
                            /*search branch area*/
                            if (oTrans.searchFilter(lnIndex, lsValue, false)) {
                                txtField.setText((String) oTrans.getFilter(lnIndex, "xxColName"));
                            } else {
                                ShowMessageFX.Warning(getStage(), "Unable to search Filter Category.", "Warning", null);
                                txtField.setText("");
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

    private boolean loadIncentiveSummaryEmployee(String fsPeriod) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("sPrintdBy", System.getProperty("user.name"));
            params.put("sReportDt", CommonUtils.xsDateLong(oApp.getServerDate()));
            params.put("sCompnyNm", "Guanzon Group of Companies");
            params.put("sBranchNm", oApp.getBranchName());
            params.put("sAddressx", oApp.getAddress());
            params.put("sReportNm", "Branch & Employee Incentive Summarized Report");

            IncentiveData.clear();
            if (oTrans.OpenRecord(fsPeriod, false)) {
                if (oTrans.procReportSummarizedEmployee()) {
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
                    
                    System.err.println("System Adding Data to Jasper!");
                    double totals = 0.0;
                    for (int x = 1; x <= lnItemCount; x++) {
                        Date ldDatePeriod = SQLUtil.toDate(oTrans.getRecord(x, "sMonthxxx").toString().trim() + " 01", "yyyyMM dd");

                        IncentiveData.add(new IncentiveDetail(
                                oTrans.getRecord(x, "sDivsnDsc").toString(),
                                oTrans.getRecord(x, "sCompnyNm").toString(),
                                oTrans.getRecord(x, "sPositnNm").toString(),
                                oTrans.getRecord(x, "sBranchNm").toString(),
                                oTrans.getRecord(x, "nInctvAmt").toString(),
                                oTrans.getRecord(x, "nDedctAmt").toString(),
                                oTrans.getRecord(x, "nTotalAmt").toString(),
                                (String) CommonUtils.dateFormat(ldDatePeriod, "yyyy MMMM"),
                                oTrans.getRecord(x, "sTransNox").toString(),
                                oTrans.getRecord(x, "sBankName").toString(),
                                oTrans.getRecord(x, "sBankAcct").toString(),
                                oTrans.getRecord(x, "sEmployID").toString()));

                    }
                    System.err.println("System Finished Adding Data to Jasper!");

                    String sourceFileName = "D://GGC_Java_Systems/reports/IncentiveSummaryEmployeeNew.jasper";

                    String printFileName = null;
                    JRBeanCollectionDataSource beanColDataSource1 = new JRBeanCollectionDataSource(IncentiveData);

                    jasperPrint = JasperFillManager.fillReport(
                            sourceFileName, params, beanColDataSource1);

                    printFileName = jasperPrint.toString();
                    if (printFileName != null) {
                        showReport();
                    }
                } else {
                    running = false;
                    vbProgress.setVisible(false);
                    timeline.stop();
                    Platform.runLater(() -> {
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(), "Warning", null);
                    });
                    return false;
                }
            }else {
                    running = false;
                    vbProgress.setVisible(false);
                    timeline.stop();
                    Platform.runLater(() -> {
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(), "Warning", null);
                    });
                    return false;
                }

        } catch (JRException | SQLException | ClassCastException ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
            running = false;
            vbProgress.setVisible(false);
            timeline.stop();
            Platform.runLater(() -> {
                ShowMessageFX.Warning(getStage(), oTrans.getMessage() + " " + ex.getMessage(), "Catch Error", null);
            });
            return false;
        }
        return true;
    }

    private boolean loadIncentiveSumBranchCategory(String fsPeriod) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("sPrintdBy", System.getProperty("user.name"));
            params.put("sReportDt", CommonUtils.xsDateLong(oApp.getServerDate()));
            params.put("sCompnyNm", "Guanzon Group of Companies");
            params.put("sBranchNm", oApp.getBranchName());
            params.put("sAddressx", oApp.getAddress());
            params.put("sReportNm", "Branch & Incentive Category Summarized Report");

            IncentiveData.clear();
            if (oTrans.OpenRecord(fsPeriod, true)) {
                if (oTrans.procReportSummarizedBranchCategory()) {
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
                    
                    
                    System.err.println("System Adding Data to Jasper!");
                    for (int x = 1; x <= lnItemCount; x++) {
                        Date ldDatePeriod = SQLUtil.toDate(oTrans.getRecord(x, "sMonthxxx").toString().trim() + " 01", "yyyyMM dd");

                        IncentiveData.add(new IncentiveDetail(
                                oTrans.getRecord(x, "sDivsnDsc").toString(),
                                oTrans.getRecord(x, "sBranchCd").toString(),
                                oTrans.getRecord(x, "sBranchNm").toString(),
                                (String) CommonUtils.dateFormat(ldDatePeriod, "yyyy MMMM"),
                                oTrans.getRecord(x, "sInctveCD").toString(),
                                oTrans.getRecord(x, "sInctveDs").toString(),
                                oTrans.getRecord(x, "nTotalAmt").toString(),
                                oTrans.getRecord(x, "nInctvAmt").toString(),
                                oTrans.getRecord(x, "nDedctAmt").toString(),
                                "",
                                "",
                                ""));

                    }
                    System.err.println("System Finished Adding Data to Jasper!");

                    String sourceFileName = "D://GGC_Java_Systems/reports/IncentiveSummaryBranchCategory.jasper";

                    String printFileName = null;
                    JRBeanCollectionDataSource beanColDataSource1 = new JRBeanCollectionDataSource(IncentiveData);

                    jasperPrint = JasperFillManager.fillReport(
                            sourceFileName, params, beanColDataSource1);

                    printFileName = jasperPrint.toString();
                    if (printFileName != null) {
                        showReport();
                    }
                } else {
                    running = false;
                    vbProgress.setVisible(false);
                    timeline.stop();
                    Platform.runLater(() -> {
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(), "Warning", null);
                    });
                    return false;
                }
            }else {
                    running = false;
                    vbProgress.setVisible(false);
                    timeline.stop();
                    Platform.runLater(() -> {
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(), "Warning", null);
                    });
                    return false;
                }

        } catch (JRException | SQLException | ClassCastException ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
            running = false;
            vbProgress.setVisible(false); 
           timeline.stop();
            Platform.runLater(() -> {
                ShowMessageFX.Warning(getStage(), oTrans.getMessage() + " " + ex.getMessage(), "Catch Error", null);
            });
            return false;
        }
        return true;
    }

    private boolean loadIncentiveSumEmployeeCategory(String fsPeriod) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("sPrintdBy", System.getProperty("user.name"));
            params.put("sReportDt", CommonUtils.xsDateLong(oApp.getServerDate()));
            params.put("sCompnyNm", "Guanzon Group of Companies");
            params.put("sBranchNm", oApp.getBranchName());
            params.put("sAddressx", oApp.getAddress());
            params.put("sReportNm", "Employee & Incentive Category Summarized Report");

            IncentiveData.clear();
            if (oTrans.OpenRecord(fsPeriod, false)) {

                System.err.println("System Processing Data!");
                if (oTrans.procReportSummarizedEmployeeCategory()) {
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
                    double totals = 0.0;

                    System.err.println("System Adding Data to Jasper!");
                    for (int x = 1; x <= lnItemCount; x++) {
                        Date ldDatePeriod = SQLUtil.toDate(oTrans.getRecord(x, "sMonthxxx").toString().trim() + " 01", "yyyyMM dd");

                        IncentiveData.add(new IncentiveDetail(
                                oTrans.getRecord(x, "sDivsnDsc").toString(),
                                oTrans.getRecord(x, "sBranchCd").toString(),
                                oTrans.getRecord(x, "sBranchNm").toString(),
                                (String) CommonUtils.dateFormat(ldDatePeriod, "yyyy MMMM"),
                                oTrans.getRecord(x, "sInctveCD").toString(),
                                oTrans.getRecord(x, "sInctveDs").toString(),
                                oTrans.getRecord(x, "nTotalAmt").toString(),
                                oTrans.getRecord(x, "nInctvAmt").toString(),
                                oTrans.getRecord(x, "nDedctAmt").toString(),
                                oTrans.getRecord(x, "sEmployID").toString(),
                                oTrans.getRecord(x, "sCompnyNm").toString(),
                                ""));

                    }
                    System.err.println("System Finished Adding Data to Jasper!");

                    String sourceFileName = "D://GGC_Java_Systems/reports/IncentiveSummaryEmployeeCategory.jasper";

                    String printFileName = null;
                    JRBeanCollectionDataSource beanColDataSource1 = new JRBeanCollectionDataSource(IncentiveData);

                    jasperPrint = JasperFillManager.fillReport(
                            sourceFileName, params, beanColDataSource1);

                    printFileName = jasperPrint.toString();
                    if (printFileName != null) {
                        showReport();
                    }
                } else {
                    running = false;
                    vbProgress.setVisible(false);
                    timeline.stop();
                    Platform.runLater(() -> {
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(), "Warning", null);
                    });
                    return false;
                }
            }else {
                    running = false;
                    vbProgress.setVisible(false);
                    timeline.stop();
                    Platform.runLater(() -> {
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(), "Warning", null);
                    });
                    return false;
                }

        } catch (JRException | SQLException | ClassCastException ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
            running = false;
            vbProgress.setVisible(false);
            timeline.stop();
            Platform.runLater(() -> {
                ShowMessageFX.Warning(getStage(), oTrans.getMessage() + " " + ex.getMessage(), "Catch Error", null);
            });
            return false;
        }
        return true;
    }

    private boolean loadIncentiveDetailed(String fsPeriod) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("sPrintdBy", System.getProperty("user.name"));
            params.put("sReportDt", CommonUtils.xsDateLong(oApp.getServerDate()));
            params.put("sCompnyNm", "Guanzon Group of Companies");
            params.put("sBranchNm", oApp.getBranchName());
            params.put("sAddressx", oApp.getAddress());
            params.put("sReportNm", "Incentive Detailed Report");

            IncentiveData.clear();
            if (oTrans.OpenRecord(fsPeriod, true)) {

                System.err.println("System Processing Data!");
                if (oTrans.procReportDetailedReport()) {
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

                    System.err.println("System Adding Data to Jasper! ");
                    for (int x = 1; x <= lnItemCount; x++) {

                        Date ldDatePeriod = SQLUtil.toDate(oTrans.getRecord(x, "sMonthxxx").toString().trim() + " 01", "yyyyMM dd");

                        Object recdStat = oTrans.getRecord(x, "cRecdStat");
                        String lsEmployStatus = (recdStat != null && recdStat.toString().equals("1")) ? "Active" : "Inactive";

                        Integer tranStatIndex = Integer.parseInt(oTrans.getRecord(x, "cTranStat").toString());
                        String lsTransactionStatus;
                        if (tranStatIndex != null && tranStatIndex >= 0 && tranStatIndex < cStatus.size() - 1) {
                            lsTransactionStatus = cStatus.get(tranStatIndex);
                        } else {
                            lsTransactionStatus = "UNKNOWN";
                        }

                        IncentiveData.add(new IncentiveDetail(
                                oTrans.getRecord(x, "sDivsnDsc").toString(),
                                oTrans.getRecord(x, "sAreaDesc").toString(),
                                oTrans.getRecord(x, "sBranchCd").toString(),
                                oTrans.getRecord(x, "sBranchNm").toString(),
                                oTrans.getRecord(x, "sTransNox").toString(),
                                oTrans.getRecord(x, "sCompnyNm").toString(),
                                oTrans.getRecord(x, "sPositnNm").toString(),
                                (String) CommonUtils.dateFormat(ldDatePeriod, "yyyy MMMM"),
                                oTrans.getRecord(x, "sInctveDs").toString(),
                                oTrans.getRecord(x, "nTotalAmt").toString(),
                                lsTransactionStatus,
                                lsEmployStatus));

                    }
                    System.err.println("System Finished Adding Data to Jasper!");
                    String sourceFileName = "D://GGC_Java_Systems/reports/IncentiveDetailed.jasper";

                    String printFileName = null;
                    JRBeanCollectionDataSource beanColDataSource1 = new JRBeanCollectionDataSource(IncentiveData);

                    jasperPrint = JasperFillManager.fillReport(
                            sourceFileName, params, beanColDataSource1);

                    printFileName = jasperPrint.toString();
                    if (printFileName != null) {
                        showReport();
                    }
                } else {
                    running = false;
                    vbProgress.setVisible(false);
                    timeline.stop();
                    Platform.runLater(() -> {
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(), "Warning", null);
                    });
                    return false;
                }
            } else {
                    running = false;
                    vbProgress.setVisible(false);
                    timeline.stop();
                    Platform.runLater(() -> {
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(), "Warning", null);
                    });
                    return false;
                }

        } catch (JRException | SQLException | ClassCastException ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
            running = false;
            vbProgress.setVisible(false);
            timeline.stop();
            Platform.runLater(() -> {
                ShowMessageFX.Warning(getStage(), oTrans.getMessage() + " " + ex.getMessage(), "Catch Error", null);
            });
            return false;
        }
        return true;
    }

    private void unloadForm() {
        StackPane myBox = (StackPane) AnchorMainIncentiveReport.getParent();
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
