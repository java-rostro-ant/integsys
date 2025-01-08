package gridergui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rmj.fund.manager.base.IncentiveReleaseNew;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.sf.jasperreports.swing.JRViewer;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.LMasDetTrans;
import org.rmj.fund.manager.parameters.IncentiveBankInfo;

/**
 * FXML Controller class
 *
 * @author Valencia, Maynard
 */
public class IncentiveReleasingNewController implements Initializable, ScreenInterface {

    private GRider oApp;
    private IncentiveReleaseNew oTrans;
    private IncentiveBankInfo trans;

    private LMasDetTrans oListener;

    private int pnEditMode;
    private int pnRow = 0;
    private boolean pbLoaded = false;
    private Date pdPeriod = null;

    @FXML
    private AnchorPane AnchorMainIncentiveRelease;
    @FXML
    private TextField txtField01;
    @FXML
    private TextField txtField02;
    @FXML
    private TextField txtField99;
    @FXML
    private TextField txtField98;
    @FXML
    private TextField txtSeeks05;
    @FXML
    private Button btnRetrieve;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnApproved;
    @FXML
    private Button btnClose;
    @FXML
    private HBox hbButtons;
    @FXML
    private TableView tblemployee;
    @FXML
    private TableColumn empIndex01, empIndex02, empIndex03, empIndex04, empIndex05, empIndex06, empIndex07, empIndex08;
    @FXML
    private TableView tblincetives;
    @FXML
    private TableColumn incIndex01, incIndex02, incIndex03;
    @FXML
    private Label lblTotal, lblStatus;
    @FXML
    private VBox vbProgress;

    private boolean pbRunning = false;
    final static int piInterval = 100;
    private Timeline ptTimeline;
    private Integer piTimeSeconds = 8;

    private final ObservableList<Release> Incentive_Directory = FXCollections.observableArrayList();
    private final ObservableList<Release> Employee_Data = FXCollections.observableArrayList();

    private Stage getStage() {
        return (Stage) AnchorMainIncentiveRelease.getScene().getWindow();
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        oListener = new LMasDetTrans() {
            @Override
            public void MasterRetreive(int i, Object o) {
                loadRecord();
                switch (i) {

                }
            }

            @Override
            public void DetailRetreive(int i, int i1, Object o) {
            }
        };

        oTrans = new IncentiveReleaseNew(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);

        initButtonClick();
        intTextField();
        pnEditMode = EditMode.UNKNOWN;

        txtField01.setDisable(true);
        txtField02.setDisable(true);
        initButton(pnEditMode);
        pbLoaded = true;
    }

    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }

    private void initButtonClick() {
        btnRetrieve.setOnAction(this::cmdButton_Click);
        btnSave.setOnAction(this::cmdButton_Click);
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnCancel.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        btnApproved.setOnAction(this::cmdButton_Click);

    }

    private void intTextField() {
        txtSeeks05.setOnKeyPressed(this::txtField_KeyPressed);
        txtField99.setOnKeyPressed(this::txtField_KeyPressed);
        txtField98.setOnKeyPressed(this::txtField_KeyPressed);

        txtField98.focusedProperty().addListener(txtField_Focus);
        txtField99.focusedProperty().addListener(txtField_Focus);
    }

    private void initButton(int fnValue) {
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);

        btnRetrieve.setVisible(true);
        btnBrowse.setVisible(!lbShow);
        btnApproved.setVisible(!lbShow);
        btnSave.setVisible(lbShow);
        btnCancel.setVisible(lbShow);

        btnBrowse.setManaged(!lbShow);
        btnApproved.setManaged(!lbShow);
        btnRetrieve.setManaged(true);
        btnSave.setManaged(lbShow);
        btnCancel.setManaged(lbShow);

    }

    public void initGrid() {
        incIndex01.setCellValueFactory(new PropertyValueFactory<Release, String>("incIndex01"));
        incIndex02.setCellValueFactory(new PropertyValueFactory<Release, String>("incIndex02"));
        incIndex03.setCellValueFactory(new PropertyValueFactory<Release, String>("incIndex03"));
        /*making column's position uninterchangebale*/
        tblincetives.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblincetives.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });

        /*Assigning data to table*/
        tblincetives.setItems(Incentive_Directory);

    }

    public void initEmployeeGrid() {
        empIndex01.setStyle("-fx-alignment: CENTER-LEFT;");
        empIndex02.setStyle("-fx-alignment: CENTER-LEFT;");
        empIndex03.setStyle("-fx-alignment: CENTER-LEFT;");
        empIndex06.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 0;");
        empIndex07.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 0;");
        empIndex08.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 0;");
        empIndex01.setCellValueFactory(new PropertyValueFactory<Release, String>("empIndex01"));
        empIndex02.setCellValueFactory(new PropertyValueFactory<Release, String>("empIndex02"));
        empIndex03.setCellValueFactory(new PropertyValueFactory<Release, String>("empIndex03"));
        empIndex04.setCellValueFactory(new PropertyValueFactory<Release, String>("empIndex04"));
        empIndex05.setCellValueFactory(new PropertyValueFactory<Release, String>("empIndex05"));
        empIndex06.setCellValueFactory(new PropertyValueFactory<Release, String>("empIndex06"));
        empIndex07.setCellValueFactory(new PropertyValueFactory<Release, String>("empIndex07"));
        empIndex08.setCellValueFactory(new PropertyValueFactory<Release, String>("empIndex08"));

        empIndex05.setCellFactory(column -> {
            return new TableCell<Release, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if (item.equalsIgnoreCase("inactive")) {
                            setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
                        } else {
                            setStyle(""); // Reset to default if not "inactive"
                        }
                    }
                }
            };
        });
        /*making column's position uninterchangebale*/
        tblemployee.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblemployee.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
            header.prefWidthProperty().bind(tblemployee.widthProperty().divide(7));
        });

        tblemployee.setItems(Employee_Data);

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
                    case 99:
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
                    case 98:
                        if (lsValue.isEmpty() || oTrans.getDivision() == null) {
                            oTrans.setDivision();
                            txtField.setText("");
                        }
                        return;

                }
            } else { //Focus
                switch (lnIndex) {
                    case 99:
                        if (pdPeriod != null) {
                            txtField.setText(CommonUtils.dateFormat(pdPeriod, "YYYY MM"));
                        }
                        return;
                    case 98:
                        if (!lsValue.trim().isEmpty()) {
                            txtField.setText((String) oTrans.getDivision("sDivsnDsc"));
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

    private void generateTransaction(String fsPeriod) {

        pbRunning = false;
        vbProgress.setVisible(true);

        if (!pbRunning) {
            ptTimeline = new Timeline();
            ptTimeline.setCycleCount(Timeline.INDEFINITE);
            ptTimeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(1), (ActionEvent event1) -> {
                        piTimeSeconds--;
                        // update timerLabel
                        if (piTimeSeconds <= 0) {
                            piTimeSeconds = 0;
                        }
                        if (piTimeSeconds == 0) {

                            try {
                                if (oTrans.NewTransaction(fsPeriod)) {
                                    loadRecord();
                                } else {
                                    pbRunning = false;
                                    vbProgress.setVisible(false);
                                    ptTimeline.stop();
                                    ShowMessageFX.Warning(getStage(), oTrans.getMessage(), "Warning", null);
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(IncentiveReleasingNewController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } // KeyFrame event handler
                    ));
            ptTimeline.playFromStart();
        }
    }

    private void loadRecord() {
        try {
            txtField01.setText((String) oTrans.getMaster("sTransNox"));
            txtField02.setText(CommonUtils.xsDateLong((Date) oTrans.getMaster("dTransact")));

            Incentive_Directory.clear();
            Employee_Data.clear();
            int lnDetail = 0;
            double lnTotalAmount = 0;

            System.err.println("Start Adding Transaction Details");
            System.err.println("Loop count = " + oTrans.getItemCount());

            // A map to store the grouped totals by branch for directory
            Map<String, Release> groupedDirectoryData = new LinkedHashMap<>();

            // A map to store the grouped totals by employee and branch
            Map<String, Release> groupedData = new LinkedHashMap<>();
            for (int lnRow = 0; lnRow <= oTrans.getItemCount() - 1; lnRow++) {
                String lsPeriod = oTrans.getDetail(lnRow).getMaster("sMonthxxx").toString();
                Date ldDate = SQLUtil.toDate(lsPeriod.trim() + " 01", "yyyyMM dd");

                String lsBranch = oTrans.getDetail(lnRow).getMaster("xBranchNm").toString();

                // Reset branch total for each branch
                double byBranchTotal = 0;

                for (int lnCtr = 1; lnCtr <= oTrans.getDetail(lnRow).getItemCount(); lnCtr++) {
                    String EmployeeStat = oTrans.getDetail(lnRow).getDetail(lnCtr, "cRecdStat").toString().equals("1") ? "ACTIVE" : "INACTIVE";

                    double xIncentive = Double.parseDouble(oTrans.getDetail(lnRow).getDetail(lnCtr, "xIncentve").toString());
                    double xDeduction = Double.parseDouble(oTrans.getDetail(lnRow).getDetail(lnCtr, "xDeductnx").toString());
                    double lnTotalEmpIncentive = xIncentive - xDeduction;

                    // Create a unique key using branch and employee name
                    String key = lsBranch + "-" + oTrans.getDetail(lnRow).getDetail(lnCtr, "xEmployNm").toString();

                    if (groupedData.containsKey(key)) {
                        // If this branch-employee combination already exists, sum the values
                        Release existingRelease = groupedData.get(key);

                        // Parse the existing total, removing commas first
                        double existingnewIncentive = Double.parseDouble(existingRelease.getEmpIndex06().replace(",", ""));
                        double existingnewDeduction = Double.parseDouble(existingRelease.getEmpIndex07().replace(",", ""));

                        double newIncentive = existingnewIncentive + xIncentive;
                        double newDeduction = existingnewDeduction + xDeduction;
                        double newTotalIncentive = newIncentive - newDeduction;

                        // Update the Release object
                        existingRelease.setEmpIndex06(CommonUtils.NumberFormat(newIncentive, "###,###,##0.00"));
                        existingRelease.setEmpIndex07(CommonUtils.NumberFormat(newDeduction, "###,###,##0.00"));
                        existingRelease.setEmpIndex08(CommonUtils.NumberFormat(newTotalIncentive, "###,###,##0.00"));
                    } else {
                        lnDetail++;
                        // If not exists, create a new entry
                        Release newRelease = new Release(
                                String.valueOf(lnDetail),
                                lsBranch,
                                oTrans.getDetail(lnRow).getDetail(lnCtr, "xEmployNm").toString(),
                                oTrans.getDetail(lnRow).getDetail(lnCtr, "xPositnNm").toString(),
                                EmployeeStat,
                                CommonUtils.NumberFormat(xIncentive, "###,###,##0.00"),
                                CommonUtils.NumberFormat(xDeduction, "###,###,##0.00"),
                                CommonUtils.NumberFormat(lnTotalEmpIncentive, "###,###,##0.00")
                        );

                        groupedData.put(key, newRelease);
                    }

                    lnTotalAmount += lnTotalEmpIncentive;
                    byBranchTotal += lnTotalEmpIncentive;
                }

                // Add or update the branch data in groupedDirectoryData
                String keyDirectory = lsBranch;
                if (groupedDirectoryData.containsKey(keyDirectory)) {
                    // Update existing branch total
                    Release existingDirectoryRelease = groupedDirectoryData.get(keyDirectory);

                    // Parse the existing total, removing commas first
                    double existingTotal = Double.parseDouble(existingDirectoryRelease.getIncIndex03().replace(",", ""));

                    // Calculate the new total by adding the branch total
                    double newBranchTotal = existingTotal + byBranchTotal;
                    existingDirectoryRelease.setIncIndex03(CommonUtils.NumberFormat(newBranchTotal, "###,###,##0.00"));
                } else {
                    // Create a new entry for the branch
                    Release newDirectoryRelease = new Release(
                            lsBranch,
                            CommonUtils.dateFormat(ldDate, "MMMM YYYY"),
                            CommonUtils.NumberFormat(byBranchTotal, "###,###,##0.00")
                    );
                    groupedDirectoryData.put(keyDirectory, newDirectoryRelease);
                }
            }

            // Add the grouped branch data to Incentive_Directory
            Incentive_Directory.addAll(groupedDirectoryData.values());
            // Add the grouped branch and employee data to Employee_Data
            Employee_Data.addAll(groupedData.values());
            lblTotal.setText(CommonUtils.NumberFormat(lnTotalAmount, "###,###,##0.00"));

            System.err.println("Finish Adding Transaction Details");

            initEmployeeGrid();
            initGrid();
            getTransactionStatus();
            reorderIncentiveDirectory();

            pbRunning = false;
            vbProgress.setVisible(false);
            ptTimeline.stop();

        } catch (NullPointerException | SQLException e) {
            ShowMessageFX.Warning(getStage(), e.getMessage(), "Warning", null);
            Logger.getLogger(IncentiveReleasingNewController.class.getName()).log(Level.SEVERE, null, e);
            pbRunning = false;
            vbProgress.setVisible(false);
            ptTimeline.stop();
        }
    }

    public void reorderIncentiveDirectory() {
        // Reorder based on branch name (alphabetical order) and by date (descending order)
        Collections.sort(Incentive_Directory, new Comparator<Release>() {
            @Override
            public int compare(Release incDirectory1, Release incDirectory2) {
                // Compare by branch name first
                int branchComparison = incDirectory1.getIncIndex01().compareTo(incDirectory2.getIncIndex01());
                if (branchComparison != 0) {
                    return branchComparison;
                }
                return 1;
            }
        });
    }

    private void unloadForm() {
        StackPane myBox = (StackPane) AnchorMainIncentiveRelease.getParent();
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

    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button) event.getSource()).getId();
        try {
            switch (lsButton) {
                case "btnBrowse": //browse transaction
                    if (oTrans.SearchTransaction(txtSeeks05.getText(), true)) {

                        loadRecord();
                        txtSeeks05.setText(txtField01.getText());
                        pnEditMode = oTrans.getEditMode();
                    } else {
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(), "Warning", null);
                    }
                    break;

                case "btnApproved": //approve transaction
                    if (oTrans.ConfirmTransaction()) {
                        if (ShowMessageFX.OkayCancel(null, "Incentive Releasing", "Are you sure, do you want to Release?") == true) {
                            if (oTrans.ReleaseTransaction()) {
                                ShowMessageFX.Warning(getStage(), "Transaction successfully Release.", "Warning", null);
                                clearFields();
                                pnEditMode = oTrans.getEditMode();
                            }
                        }

                    } else {
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(), "Warning", null);
                    }
                    break;
                case "btnRetrieve": //create new transaction

                    if (txtField98.getText().trim().isEmpty()) {
                        oTrans.setDivision();
                    }
                    if (pdPeriod == null) {
                        ShowMessageFX.Warning(getStage(), "Incentive period must not be empty", "Warning", null);

                    } else {
                        String lsPeriod = CommonUtils.dateFormat(pdPeriod, "YYYYMM");
                        System.out.println("nMonthxx = " + lsPeriod);

                        generateTransaction(lsPeriod);
                        pnEditMode = oTrans.getEditMode();

                    }
                    break;
                case "btnSave": //save transaction
                    if (oTrans.SaveTransaction()) {
                        ShowMessageFX.Warning(getStage(), "Transaction Successfully Saved.", "Warning", null);
                        clearFields();

                    } else {
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(), "Warning", null);
                    }

                    break;
                case "btnCancel": //cancel transaction
                    clearFields();
                    //reload detail
                    break;

                case "btnClose": //close releasing form
                    if (ShowMessageFX.OkayCancel(null, "Incentive Releasing", "Are you sure, do you want to close?") == true) {
                        unloadForm();
                        break;
                    } else {
                        return;
                    }
            }

            initButton(pnEditMode);
        } catch (SQLException e) {
            e.printStackTrace();
            ShowMessageFX.Warning(getStage(), e.getMessage(), "Warning", null);
        }
    }

    @FXML
    private void tblincetives_Click(MouseEvent event
    ) {

//        try {
        pnRow = tblincetives.getSelectionModel().getSelectedIndex();
        if (pnRow >= 0) {

            if (Employee_Data.size() > 0) {
                String tblbranch = Incentive_Directory.get(pnRow).getIncIndex01();
                for (int lnCtr = 0; lnCtr <= Employee_Data.size() - 1; lnCtr++) {
                    String listBranch = Employee_Data.get(lnCtr).getEmpIndex02();

                    if (listBranch.equals(tblbranch)) {
                        // Branch found, select the row
                        tblemployee.getSelectionModel().select(lnCtr);
                        tblemployee.scrollTo(lnCtr);

                        break;
                    }
                }
            }
        }

//        } catch (SQLException ex) {
//            ShowMessageFX.Warning(getStage(), ex.getMessage(), "Warning", null);
//        }
    }

    private void txtField_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        try {
            switch (event.getCode()) {
                case F3:
                    switch (lnIndex) {

                        case 5:
                            /*Browse*/
                            pbLoaded = true;
                            if (oTrans.SearchTransaction(lsValue, true)) {
                                loadRecord();
                                txtSeeks05.setText(txtField01.getText());
                                pnEditMode = oTrans.getEditMode();
                            } else {
                                ShowMessageFX.Warning(getStage(), oTrans.getMessage(), "Warning", null);
                            }
                            break;

                        case 98:
                            /*Search*/
                            pbLoaded = true;
                            if (oTrans.searchDivision(lsValue, false)) {
//                                loadRecord();
                                txtField.setText((String) oTrans.getDivision("sDivsnDsc"));
                                pnEditMode = oTrans.getEditMode();
                            } else {
                                txtField.setText("");
                                ShowMessageFX.Warning(getStage(), oTrans.getMessage(), "Warning", null);
                            }
                            break;
                    }
                case ENTER:
                case DOWN:
                    CommonUtils.SetNextFocus(txtField);
                    break;
                case UP:
                    CommonUtils.SetPreviousFocus(txtField);
            }
        } catch (SQLException e) {
            ShowMessageFX.Warning(getStage(), e.getMessage(), "Warning", null);
        }

    }

    private void clearFields() {
        oTrans = new IncentiveReleaseNew(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        pnEditMode = EditMode.UNKNOWN;
        trans = new IncentiveBankInfo(oApp, oApp.getBranchCode(), false);
        Incentive_Directory.clear();
        Employee_Data.clear();
        txtField01.clear();
        txtField02.clear();
        txtField99.clear();
        txtField98.clear();
        pdPeriod = null;

        lblStatus.setVisible(false);
    }

    private void getTransactionStatus() {
        try {
            if (oTrans.getMaster("cTranStat").toString().equalsIgnoreCase("0")) {
                lblStatus.setVisible(true);
                lblStatus.setText("OPEN");
            } else if (oTrans.getMaster("cTranStat").toString().equalsIgnoreCase("1")) {
                lblStatus.setVisible(true);
                lblStatus.setText("CLOSED");
            } else if (oTrans.getMaster("cTranStat").toString().equalsIgnoreCase("2")) {
                lblStatus.setVisible(true);
                lblStatus.setText("RELEASED");
            } else if (oTrans.getMaster("cTranStat").toString().equalsIgnoreCase("3")) {
                lblStatus.setVisible(true);
                lblStatus.setText("CANCELLED");
            } else {
                lblStatus.setVisible(false);
            }
        } catch (SQLException ex) {
            Logger.getLogger(IncentiveReleasingNewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
