/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gridergui;

import com.itextpdf.text.pdf.qrcode.Mode;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import static java.lang.Boolean.FALSE;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.grewards.base.LMasDetTrans;
import org.rmj.grewards.base.PacitaEvaluation;

/**
 * FXML Controller class
 *
 * @author User
 */
public class PacitaEvaluationHisController implements Initializable, ScreenInterface {

    @FXML
    private AnchorPane AnchorMainPacita;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnClose, btnRefresh, btnPrint;
    @FXML
    private Pagination pagination;
    @FXML
    private TableView tblMaster;
    @FXML
    private TableColumn masIndex01, masIndex02, masIndex03, masIndex04, masIndex05;
    @FXML
    private TreeTableView tblDetail;
    @FXML
    private TreeTableColumn pacitaindex01, pacitaindex02, pacitaindex03;
    @FXML
    private Label lblTotalRte;
    @FXML
    private AnchorPane searchBar;
    @FXML
    private TextField txtSeeks99, txtSeeks98, txtSeeks97;

    private GRider oApp;
    private PacitaEvaluation oTrans;
    private LMasDetTrans oListener;

    private boolean pbLoaded = false;
    private int pnRow = -1;
    private int oldPnRow = -1;
    private int pnEditMode;
    private int pagecounter;
    private String oldTransNo = "";
    private String EvalType = "";

    private static final int ROWS_PER_PAGE = 30;

    private FilteredList<TableModel> filteredData;

    private final ObservableList<TableModel> master_data = FXCollections.observableArrayList();
    private final ObservableList<TableModel> detailpacita_data = FXCollections.observableArrayList();
    private final ObservableList<TableModel> detailpacitachild_data = FXCollections.observableArrayList();

    private Stage getStage() {
        return (Stage) txtSeeks99.getScene().getWindow();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        oTrans = new PacitaEvaluation(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        pbLoaded = true;

        loadMaster();

        pnEditMode = EditMode.UNKNOWN;
        btnClose.setOnAction(this::cmdButton_Click);
        btnRefresh.setOnAction(this::cmdButton_Click);
        btnPrint.setOnAction(this::cmdButton_Click);
        pagination.setPageFactory(this::createPage);

    }

    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, master_data.size());
        if (master_data.size() > 0) {
            tblMaster.setItems(FXCollections.observableArrayList(master_data.subList(fromIndex, toIndex)));
        }
        return tblMaster;
    }

    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }

    private void loadMaster() {
        int lnCtr;
        try {
            master_data.clear();

            if (oTrans.LoadList("")) {
                for (lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++) {
                    master_data.add(new TableModel(String.valueOf(lnCtr),
                            (String) oTrans.getRecord(lnCtr, "sTransNox"),
                            CommonUtils.xsDateLong((Date) oTrans.getRecord(lnCtr, "dTransact")),
                            oTrans.getRecord(lnCtr, "sCompnyNm").toString(),
                            oTrans.getRecord(lnCtr, "sBranchNm").toString(),
                            oTrans.getRecord(lnCtr, "sPayloadx").toString(),
                            oTrans.getRecord(lnCtr, "nRatingxx").toString(),
                            oTrans.getRecord(lnCtr, "cTranStat").toString(),
                            oTrans.getRecord(lnCtr, "AreaDesc").toString(),
                            oTrans.getRecord(lnCtr, "sDeptName").toString()));
                }
                initGrid();
            }
            loadTab();

        } catch (SQLException ex) {
            System.out.println("SQLException" + ex.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("NullPointerException" + ex.getMessage());
        } catch (DateTimeException ex) {
//            MsgBox.showOk(ex.getMessage());
            System.out.println("DateTimeException" + ex.getMessage());
        }
    }

    public void loadPacitaDetail(String cEvaltype) {
        try {

            if (cEvaltype.contentEquals("015")) {
                cEvaltype = "4";
                EvalType = cEvaltype;
            }
//            System.out.println("cEvaltype : "+ cEvaltype);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(filteredData.get(pagecounter).getIndex06());
            JSONArray payloadArray = (JSONArray) jsonObject.get("sPayloadx");
            detailpacita_data.clear();
            if (oTrans.LoadPacita(cEvaltype)) {

                for (int lnCtr = 1; lnCtr <= oTrans.getPacitaItemCount(); lnCtr++) {
                    String jsonxRatingxx = "";
                    String psMaxRating = "";
                    if (!(oTrans.getPacita(lnCtr, "nMaxValue").toString()).equals("0.00")) {
                        psMaxRating = oTrans.getPacita(lnCtr, "nMaxValue").toString();
                        JSONObject payloadObject = (JSONObject) payloadArray.get((Integer) oTrans.getPacita(lnCtr, "nEntryNox") - 1);
                        if (payloadObject.get("nEntryNox").toString().equals(oTrans.getPacita(lnCtr, "nEntryNox").toString())) {
                            if (payloadObject.get("xRatingxx").toString().equals("1")) {
                                jsonxRatingxx = "PASSED";
                            } else {
                                jsonxRatingxx = "FAILED";
                            }
                        }
                    }
                    detailpacita_data.add(new TableModel("",
                            jsonxRatingxx,
                            oTrans.getPacita(lnCtr, "sFieldNmx").toString(),
                            psMaxRating,
                            oTrans.getPacita(lnCtr, "cParentxx").toString()));

                }
//            list master's
                //dummy
                TreeItem<TableModel> rootMaster = new TreeItem<>();

                for (TableModel rowDataMaster : detailpacita_data) {
                    TreeItem<TableModel> item = new TreeItem<>(rowDataMaster);
                    rootMaster.getChildren().add(item);

                    //listing detail
                    //clear to refresh data03 each loop
                    detailpacitachild_data.clear();

                    //loading children data
                    if (!rowDataMaster.getIndex05().equals("0")) {
                        if (oTrans.LoadPacitaChild(cEvaltype, rowDataMaster.getIndex05())) {
                            if (oTrans.getPacitaChildItemCount() > 0) {
                                for (int lnCtr = 2; lnCtr <= oTrans.getPacitaChildItemCount(); lnCtr++) {
                                    String jsonxRatingxx = "";

                                    JSONObject payloadObject = (JSONObject) payloadArray.get((Integer) oTrans.getPacitaChild(lnCtr, "nEntryNox") - 1);
                                    if (payloadObject.get("nEntryNox").toString().equals(oTrans.getPacitaChild(lnCtr, "nEntryNox").toString())) {
                                        if (payloadObject.get("xRatingxx").toString().equals("1")) {
                                            jsonxRatingxx = "PASSED";
                                        } else {
                                            jsonxRatingxx = "FAILED";
                                        }
                                    }
                                    detailpacitachild_data.add(new TableModel("",
                                            jsonxRatingxx,
                                            oTrans.getPacitaChild(lnCtr, "sFieldNmx").toString(),
                                            oTrans.getPacitaChild(lnCtr, "nMaxValue").toString(),
                                            oTrans.getPacitaChild(lnCtr, "cParentxx").toString()));
                                }

                                for (TableModel rowDataDetail : detailpacitachild_data) {
                                    TreeItem<TableModel> item1 = new TreeItem<>(rowDataDetail);
                                    item.getChildren().add(item1);

                                }
                                if (item.isExpanded());
                                {
                                    item.setExpanded(true);
                                }
                            }
                        }
                    }
                }
                //display
                tblDetail.setRoot(rootMaster);
                tblDetail.setShowRoot(false);

            } else {
                detailpacita_data.clear();
                TreeItem<TableModel> rootMaster = new TreeItem<>();

                for (TableModel rowDataMaster : detailpacita_data) {
                    TreeItem<TableModel> item = new TreeItem<>(rowDataMaster);
                    rootMaster.getChildren().add(item);
                }
                //listing detail
                //clear to refresh data03 each loop
                detailpacitachild_data.clear();
                tblDetail.setRoot(rootMaster);
                tblDetail.setShowRoot(false);

            }

        } catch (SQLException ex) {
            ShowMessageFX.Warning(getStage(), ex.getMessage(), "Warning", null);
        } catch (ParseException ex) {
            ShowMessageFX.Warning(getStage(), ex.getMessage(), "Warning", null);
        }
    }

    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button) event.getSource()).getId();
        try {
            switch (lsButton) {

                case "btnClose":
                    if (ShowMessageFX.OkayCancel(null, "Panalo Parameter", "Are you sure, do you want to close?") == true) {
                        unloadForm();
                        break;
                    } else {
                        return;
                    }
                case "btnRefresh":
                    clearFields();
                    loadMaster();
                    break;
                case "btnPrint":
                    if (!oldTransNo.equals("")) {
                        if (oTrans.getMaster("cTranStat").equals(3)) {
                            ShowMessageFX.Warning("Trasaction may be CANCELLED.", "Pacita Evaluation", "Can't print transactions!!!");
                            return;
                        }

                        if (ShowMessageFX.YesNo(null, "", "Do you want to print this transasction?") == true) {
                            if (!printTransfer()) {
                                return;
                            }
//                            clearFields();
////                            initGrid();
//                            pnEditMode = EditMode.UNKNOWN;

                        }
                    } else {
                        ShowMessageFX.Warning(null, "Pacita Evaluation", "Please select a record to print!");
                    }

                    break;

            }

//            initButton(pnEditMode);
        } catch (NullPointerException e) {
            e.printStackTrace();
            ShowMessageFX.Warning(getStage(), e.getMessage(), "Warning", null);
        } catch (SQLException ex) {
            ShowMessageFX.Warning(getStage(), ex.getMessage(), "Warning", null);
        }
    }

    private void initGrid() {

        masIndex01.setStyle("-fx-alignment: CENTER;");
        masIndex02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        masIndex03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        masIndex04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        masIndex05.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 0;");

        masIndex01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        masIndex02.setCellValueFactory(new PropertyValueFactory<>("index03"));
        masIndex03.setCellValueFactory(new PropertyValueFactory<>("index05"));
        masIndex04.setCellValueFactory(new PropertyValueFactory<>("index04"));
        masIndex05.setCellValueFactory(new PropertyValueFactory<>("index07"));

        tblMaster.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblMaster.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        filteredData = new FilteredList<>(master_data, b -> true);
        autoSearch(txtSeeks99);
        autoSearch(txtSeeks98);
        autoSearch(txtSeeks97);

        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<TableModel> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        // 	  Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tblMaster.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tblMaster.setItems(sortedData);
        tblMaster.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblMaster.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
            header.setDisable(true);
        });
        initGrid1();
    }

    private void initGrid1() {

        pacitaindex02.setStyle("-fx-alignment: CENTER;");
        pacitaindex03.setStyle("-fx-alignment: CENTER;");

        pacitaindex01.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TableModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TableModel, String> p) {
                return p.getValue().getValue().index03Property();
            }
        });
        pacitaindex02.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TableModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TableModel, String> p) {
                return p.getValue().getValue().index04Property();
            }
        });
        pacitaindex03.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TableModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TableModel, String> p) {
                return p.getValue().getValue().index02Property();
            }
        });

    }

    private void autoSearch(TextField txtField) {
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        boolean fsCode = true;
        txtField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(master -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare order no. and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();
                switch (lnIndex) {
                    case 99:
                        if (lnIndex == 99) {
                            return (master.getIndex04().toLowerCase().contains(lowerCaseFilter)); // Does not match.   
                        } else {
                            return (master.getIndex04().toLowerCase().contains(lowerCaseFilter)); // Does not match.
                        }

                    case 98:
                        if (lnIndex == 98) {
                            return (master.getIndex05().toLowerCase().contains(lowerCaseFilter)); // Does not match.   
                        } else {
                            return (master.getIndex05().toLowerCase().contains(lowerCaseFilter)); // Does not match.
                        }
                    case 97:
                        if (lnIndex == 97) {
                            return (master.getIndex03().toLowerCase().contains(lowerCaseFilter)); // Does not match.   
                        } else {
                            return (master.getIndex03().toLowerCase().contains(lowerCaseFilter)); // Does not match.
                        }
                    default:
                        return true;

                }
            });

            changeTableView(0, ROWS_PER_PAGE);
        });
        loadTab();
    }

    private void loadTab() {
        int totalPage = (int) (Math.ceil(master_data.size() * 1.0 / ROWS_PER_PAGE));
        pagination.setPageCount(totalPage);
        pagination.setCurrentPageIndex(0);
        changeTableView(0, ROWS_PER_PAGE);
        pagination.currentPageIndexProperty().addListener(
                (observable, oldValue, newValue) -> changeTableView(newValue.intValue(), ROWS_PER_PAGE));

    }

    private void changeTableView(int index, int limit) {
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, master_data.size());

        int minIndex = Math.min(toIndex, filteredData.size());
        SortedList<TableModel> sortedData = new SortedList<>(
                FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tblMaster.comparatorProperty());
        tblMaster.setItems(sortedData);
    }

    @FXML
    void tblMaster_Clicked(MouseEvent event) {
        pnRow = tblMaster.getSelectionModel().getSelectedIndex();
        pagecounter = pnRow + pagination.getCurrentPageIndex() * ROWS_PER_PAGE;
        if (pagecounter >= 0) {
            if (event.getClickCount() > 0) {

//                clear();
                getSelectedItems();
//                loadRedeemDetail(oldTransNo);

                tblMaster.setOnKeyReleased((KeyEvent t) -> {
                    KeyCode key = t.getCode();
                    switch (key) {
                        case DOWN:
                            pnRow = tblMaster.getSelectionModel().getSelectedIndex();
                            pagecounter = pnRow + pagination.getCurrentPageIndex() * ROWS_PER_PAGE;
                            if (pagecounter == tblMaster.getItems().size()) {
                                pagecounter = tblMaster.getItems().size();
                                getSelectedItems();
                            } else {
                                int y = 1;
                                pnRow = pnRow + y;
                                getSelectedItems();
                            }
                            break;
                        case UP:
                            pnRow = tblMaster.getSelectionModel().getSelectedIndex();
                            pagecounter = pnRow + pagination.getCurrentPageIndex() * ROWS_PER_PAGE;
                            getSelectedItems();
                            break;
                        default:
                            return;
                    }
                });
            }
        }
    }

////    @FXML
////    private void tblRedemption_Clicked(MouseEvent event) {
////
////    }
    private void getSelectedItems() {
        try {
            EvalType = "";
            if (!filteredData.get(pagecounter).getIndex06().trim().isEmpty()) {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(filteredData.get(pagecounter).getIndex06());
                String sEvalType = (String) jsonObject.get("sEvalType");
                EvalType = sEvalType;
                loadPacitaDetail(sEvalType);
                oldTransNo = filteredData.get(pagecounter).getIndex02();
                lblTotalRte.setText(filteredData.get(pagecounter).getIndex07());
            }

        } catch (ParseException ex) {
            Logger.getLogger(PacitaEvaluationHisController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void unloadForm() {
        StackPane myBox = (StackPane) AnchorMainPacita.getParent();
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

    public void clearFields() {

        txtSeeks99.clear();
        txtSeeks98.clear();
        txtSeeks97.clear();
        master_data.clear();
        detailpacita_data.clear();
        detailpacitachild_data.clear();
//        lblStatus.setVisible(false);
        oTrans = new PacitaEvaluation(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        pbLoaded = true;
    }

    private boolean printTransfer() {
        int pnRow = 0;
        String sourceFileName = "D://GGC_Java_Systems/reports/PacitaEvaluation.jasper";
        try {
            JSONArray json_arr = new JSONArray();
            json_arr.clear();
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(filteredData.get(pagecounter).getIndex06());
            JSONArray payloadArray = (JSONArray) jsonObject.get("sPayloadx");

//            detailpacita_data.clear();
            /*ADD THE DETAIL*/
            for (int lnCtr = 1; lnCtr <= oTrans.getPacitaItemCount(); lnCtr++) {
                pnRow = lnCtr;
                JSONObject json_obj = new JSONObject();
                json_obj.put("sField01", detailpacita_data.get(lnCtr - 1).getIndex03());
                json_obj.put("sField02", detailpacita_data.get(lnCtr - 1).getIndex04());
                json_obj.put("sField03", detailpacita_data.get(lnCtr - 1).getIndex02());
                json_arr.add(json_obj);

                if (!detailpacita_data.get(lnCtr - 1).getIndex05().equals("0")) {
                    if (oTrans.LoadPacitaChild(EvalType, detailpacita_data.get(lnCtr - 1).getIndex05())) {
                        if (oTrans.getPacitaChildItemCount() > 0) {

                            for (int lnCtrChild = 2; lnCtrChild <= oTrans.getPacitaChildItemCount(); lnCtrChild++) {
                                JSONObject json_objchild = new JSONObject();
                                String jsonxRatingxx = "";
                                JSONObject payloadObject = (JSONObject) payloadArray.get((Integer) oTrans.getPacitaChild(lnCtrChild, "nEntryNox") - 1);
                                if (payloadObject.get("nEntryNox").toString().equals(oTrans.getPacitaChild(lnCtrChild, "nEntryNox").toString())) {
                                    if (payloadObject.get("xRatingxx").toString().equals("1")) {
                                        jsonxRatingxx = "PASSED";
                                    } else {
                                        jsonxRatingxx = "FAILED";
                                    }
                                }

                                json_objchild.put("sField01", "\t" + oTrans.getPacitaChild(lnCtrChild, "sFieldNmx").toString());
                                json_objchild.put("sField02", oTrans.getPacitaChild(lnCtrChild, "nMaxValue").toString());
                                json_objchild.put("sField03", jsonxRatingxx);
                                json_arr.add(json_objchild);
                            }
                        }
                    }
                }
            }

//        String lsSQL = "SELECT sBranchNm FROM Branch WHERE sBranchCD = " + SQLUtil.toSQL((String) oTrans.getMaster("sBranchCd"));
//        ResultSet loRS = oApp.executeQuery(lsSQL);
//        
//            if (loRS.next())
//                lsSQL = loRS.getString("sBranchNm");
//            else
//                lsSQL = (String) oTrans.getMaster("sBranchCd");
            //Create the parameter
            Map<String, Object> params = new HashMap<>();
//        params.put("sCompnyNm", "Guanzon Group of Company");  
//        params.put("sBranchNm", oApp.getBranchName());
//        params.put("sAddressx", oApp.getAddress());
//        params.put("sReportNm", "Pacita Evaluation");

            params.put("psEvaluator", filteredData.get(pagecounter).getIndex04());
            params.put("psBranch", filteredData.get(pagecounter).getIndex05());
            params.put("pstotalRate", filteredData.get(pagecounter).getIndex07());
            params.put("psArea", filteredData.get(pagecounter).getIndex09());
            params.put("psDepartment", filteredData.get(pagecounter).getIndex10());
            params.put("pdTransact", filteredData.get(pagecounter).getIndex03());
            params.put("sPrintdBy", System.getProperty("user.name"));
//                
//        
            InputStream stream = new ByteArrayInputStream(json_arr.toJSONString().getBytes("UTF-8"));
            JsonDataSource jrjson = new JsonDataSource(stream);

            JasperPrint _jrprint = JasperFillManager.fillReport(sourceFileName, params, jrjson);
            JasperViewer jv = new JasperViewer(_jrprint, false);
            jv.setVisible(true);
//            jv.setAlwaysOnTop(true);
        } catch (JRException | UnsupportedEncodingException ex) {
            Logger.getLogger(PacitaEvaluationHisController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PacitaEvaluationHisController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(PacitaEvaluationHisController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }
}
