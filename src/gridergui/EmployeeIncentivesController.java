

package gridergui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agent.MsgBox;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.Incentive; 
import static javafx.scene.input.KeyCode.F3;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.rmj.appdriver.StringUtil;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.fund.manager.base.LMasDetTrans;

public class EmployeeIncentivesController implements Initializable, ScreenInterface {
    private final String pxeModuleName = "Employee Incentives";
    private double xOffset = 0;
    private double yOffset = 0;
    
    private GRider oApp;
    private Incentive oTrans;
    private LMasDetTrans oListener;
    
    private String oTransnox = "";
    private boolean state = false;
    
    private int pnIndex = -1;    
    private int pnRow = -1;
    private int pnEmp = -1;
    private int pnEditMode;
    private int lnCtr;

    private boolean pbLoaded = false;
    
    private ObservableList<TableModel> data = FXCollections.observableArrayList();
    private ObservableList<TableIncentives> inc_data = FXCollections.observableArrayList();
    @FXML
    private Button btnNew;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnAddIncentives;
    @FXML
    private Button btnAddDeductions;
    @FXML
    private AnchorPane AnchorMain;
    @FXML
    private Label lblHeader; 
    
    @FXML
    private TableView tblemployee;
    @FXML
    private TableColumn index01;
    @FXML
    private TableColumn index02;
    @FXML
    private TableColumn index03;
    @FXML
    private TableColumn index04;
    @FXML
    private TableColumn index05;
    @FXML
    private TableColumn index06;
    @FXML
    private AnchorPane searchBar;
    @FXML
    private TextField txtField01;
    @FXML
    private TextField txtField02;
    @FXML
    private TextField txtField03;
    @FXML
    private TextField txtField04;
    @FXML
    private TextArea txtField05;
    @FXML
    private TextField txtField16;
    @FXML
    private TextField txtSeeks21;
    @FXML
    private TextField txtSeeks22;
    @FXML
    private TableView tblIncentives;
    @FXML
    private TableColumn<?, ?> incindex01;
    @FXML
    private TableColumn<?, ?> incindex02;
    @FXML
    private TableColumn<?, ?> incindex03;
    @FXML
    private TableColumn<?, ?> incindex04;
    @FXML
    private TableColumn<?, ?> incindex05;
    @FXML
    private TableColumn<?, ?> incindex06;
    @FXML
    private TableColumn<?, ?> incindex07;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnActivate;
    @FXML
    private Button btnDeactivate;
    @FXML
    private Button btnDelEmp;
    
    public void setTransaction(String fsValue){
        oTransnox = fsValue;
    }
    public void setState(boolean fsValue){
        state = fsValue;
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {  

        oListener = new LMasDetTrans() {
            @Override
            public void MasterRetreive(int fnIndex, Object foValue) {
                switch(fnIndex){
                    case 1:
                        txtField01.setText((String) foValue); break;
                    case 2:
                        txtField02.setText((String) foValue); break;
                    case 4:
                        txtField04.setText((String) foValue); break;
                    case 5:
                        txtField05.setText((String) foValue); break;
                    case 17:
                        txtField03.setText((String) foValue); 
                        loadIncentives();
                        loadDetail();
                        break;
                    case 16:
                        txtField16.setText((String) foValue); break;
                }
            }
            @Override
            public void DetailRetreive(int fnRow, int fnIndex, Object foValue) {
            }
         };
        
        tblIncentives_column();
        tblemployee_column();
        
        btnNew.setOnAction(this::cmdButton_Click);
        btnAddIncentives.setOnAction(this::cmdButton_Click);
        btnAddDeductions.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        btnSave.setOnAction(this::cmdButton_Click);
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnUpdate.setOnAction(this::cmdButton_Click);
        btnCancel.setOnAction(this::cmdButton_Click);
        btnDelEmp.setOnAction(this::cmdButton_Click);
        
        //initialize class
        oTrans  = new Incentive(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
      
        if(!oTransnox.isEmpty()){
            oTrans.setTranStat(10);
        }else{
            oTrans.setTranStat(0);
        }
        oTrans.setWithUI(true);
        
        /*Add listener to text fields*/
        txtSeeks21.focusedProperty().addListener(txtField_Focus);
        txtSeeks22.focusedProperty().addListener(txtField_Focus);
        txtField01.focusedProperty().addListener(txtField_Focus);
        txtField02.focusedProperty().addListener(txtField_Focus);
        txtField03.focusedProperty().addListener(txtField_Focus);
        txtField04.focusedProperty().addListener(txtField_Focus);
        txtField16.focusedProperty().addListener(txtField_Focus);
        
        txtField05.focusedProperty().addListener(txtArea_Focus);
        
        txtSeeks21.setOnKeyPressed(this::txtField_KeyPressed);
        txtSeeks22.setOnKeyPressed(this::txtField_KeyPressed);
        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
        txtField02.setOnKeyPressed(this::txtField_KeyPressed);
        txtField03.setOnKeyPressed(this::txtField_KeyPressed);
        txtField04.setOnKeyPressed(this::txtField_KeyPressed);
        txtField16.setOnKeyPressed(this::txtField_KeyPressed);
        txtField05.setOnKeyPressed(this::txtArea_KeyPressed);

        CommonUtils.addTextLimiter(txtField04, 6);
        CommonUtils.addTextLimiter(txtField05, 128);
        
        initGrid();
        initGrid1();
        
        try { 
            if(oTransnox.isEmpty()){
                pnEditMode = EditMode.UNKNOWN;
                initButton(pnEditMode);
            } else {
                if (oTrans.SearchTransaction(oTransnox, true)){
                    loadMaster();
                    loadIncentives();
                    
                    if (oTrans.UpdateTransaction()){
                        pnEditMode = oTrans.getEditMode(); 
                        initButton(pnEditMode);
                      } else 
                        MsgBox.showOk(oTrans.getMessage());
                } else
                    MsgBox.showOk(oTrans.getMessage());
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeIncentivesController.class.getName()).log(Level.SEVERE, null, ex);
        }  

        pbLoaded = true;
    } 
    
    private void txtArea_KeyPressed(KeyEvent event){
        if (event.getCode() == ENTER || event.getCode() == DOWN){ 
            event.consume();
            CommonUtils.SetNextFocus((TextArea)event.getSource());
        }else if (event.getCode() ==KeyCode.UP){
        event.consume();
            CommonUtils.SetPreviousFocus((TextArea)event.getSource());
        }
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
                        case 21: /*search oTransaction*/
                            if (oTrans.SearchTransaction(txtSeeks21.getText(), true)){
                                loadMaster();
                                loadIncentives();
                                pnEditMode = EditMode.READY;
                            }else
                                MsgBox.showOk(oTrans.getMessage());
                            break;
                        case 22:/*search branch*/
                            if (oTrans.SearchTransaction(txtSeeks22.getText(), false)){
                                loadMaster();
                                loadIncentives();
                                pnEditMode = EditMode.READY;
                            }else
                                MsgBox.showOk(oTrans.getMessage());
                            break;
                        case 3: /*search department*/
                            if(!oTrans.searchDepartment(lsValue, false)) {
                                txtField.setText((String) oTrans.getMaster("xDeptName"));
                                MsgBox.showOk("Unable to update department.");
                            } 
                                
                            break;
                    }
            }      
        } catch (SQLException e) {
            e.printStackTrace();
            MsgBox.showOk(e.getMessage());
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
    
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
     
        btnCancel.setVisible(lbShow);
        btnSearch.setVisible(lbShow);
        btnSave.setVisible(lbShow);
        btnDelEmp.setVisible(lbShow);
        btnActivate.setVisible(lbShow);
        btnDeactivate.setVisible(lbShow);
        btnAddIncentives.setVisible(lbShow);
        btnAddDeductions.setVisible(lbShow);
        
        btnSave.setManaged(lbShow);
        btnCancel.setManaged(lbShow);
        btnSearch.setManaged(lbShow);
        btnUpdate.setVisible(!lbShow);
        btnBrowse.setVisible(!lbShow);
        btnNew.setVisible(!lbShow);
        
        txtSeeks21.setDisable(!lbShow);
        txtSeeks22.setDisable(!lbShow);
        
        if (lbShow){
            txtSeeks21.setDisable(lbShow);
            txtSeeks21.clear();
            txtSeeks22.setDisable(lbShow);
            txtSeeks22.clear();
            
            btnCancel.setVisible(lbShow);
            btnSearch.setVisible(lbShow);
            btnSave.setVisible(lbShow);
            btnUpdate.setVisible(!lbShow);
            btnBrowse.setVisible(!lbShow);
            btnNew.setVisible(!lbShow);
            btnBrowse.setManaged(false);
            btnNew.setManaged(false);
            btnUpdate.setManaged(false);
            btnActivate.setVisible(!lbShow);
            btnDeactivate.setVisible(!lbShow);
            
              
        }
        else{
            txtSeeks21.setDisable(lbShow);
            txtSeeks21.requestFocus();
            txtSeeks22.setDisable(lbShow);  
        }
        if (lbShow = (fnValue == EditMode.UPDATE)) {
                btnAddIncentives.setDisable(true);
                btnAddDeductions.setDisable(true);
        } else {
                btnAddIncentives.setDisable(false);
                btnAddDeductions.setDisable(false);
        }
    }
    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }    
    
    private void loadIncentives(){
        try {
            inc_data.clear();
            for (lnCtr = 1; lnCtr <= oTrans.getIncentiveCount(); lnCtr++){
                inc_data.add(new TableIncentives(String.valueOf(lnCtr),
                    oTrans.getIncentiveInfo(lnCtr, "xInctvNme").toString(),
                    oTrans.getIncentiveInfo(lnCtr, "nQtyGoalx").toString(),
                    oTrans.getIncentiveInfo(lnCtr, "nQtyActlx").toString(),
                    oTrans.getIncentiveInfo(lnCtr, "nAmtGoalx").toString(),
                    oTrans.getIncentiveInfo(lnCtr, "nAmtActlx").toString(),
                    oTrans.getIncentiveInfo(lnCtr, "nInctvAmt").toString(),
                    oTrans.getIncentiveInfo(lnCtr, "sRemarksx").toString(),
                    oTrans.getIncentiveInfo(lnCtr, "sInctveCD").toString()));
            }

            for (lnCtr = 1; lnCtr <= oTrans.getDeductionCount(); lnCtr++){
                inc_data.add(new TableIncentives(String.valueOf(inc_data.size() + 1), 
                    oTrans.getDeductionInfo(lnCtr, "sRemarksx").toString() + "(Deduction)",
                    "",
                    "",
                    "",
                    "",
                    oTrans.getDeductionInfo(lnCtr, "nDedctAmt").toString(),
                    "",
                    ""));          
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MsgBox.showOk(e.getMessage());
        }
    }
    private void loadDetail(){
        try {
            data.clear();
            for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
                data.add(new TableModel(String.valueOf(lnCtr),
                    oTrans.getDetail(lnCtr, "xEmployNm").toString(),
                    oTrans.getDetail(lnCtr, "xEmpLevNm").toString(),
                    oTrans.getDetail(lnCtr, "xPositnNm").toString(),
                    oTrans.getDetail(lnCtr, "xSrvcYear").toString(),
                    (CommonUtils.NumberFormat((Number)oTrans.getDetail(lnCtr, "nTotalAmt"), "#,##0.00"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MsgBox.showOk(e.getMessage());
        }
    }
    
    private void loadMasters(){
        try {
            data.clear();
            for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
                data.add(new TableModel(String.valueOf(lnCtr),
                    oTrans.getDetail(lnCtr, "xEmployNm").toString(),
                    oTrans.getDetail(lnCtr, "xEmpLevNm").toString(),
                    oTrans.getDetail(lnCtr, "xPositnNm").toString(),
                    oTrans.getDetail(lnCtr, "xSrvcYear").toString(),
                    (CommonUtils.NumberFormat((Number)oTrans.getDetail(lnCtr, "nTotalAmt"), "#,##0.00"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MsgBox.showOk(e.getMessage());
        }
    }

    final ChangeListener<? super Boolean> txtArea_Focus = (o,ov,nv)->{ 
        if (!pbLoaded) return;
        
        TextArea txtField = (TextArea)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
            try {
                if(!nv){ /*Lost Focus*/
                    switch (lnIndex){
                        case 05:
                           oTrans.setMaster(lnIndex, lsValue); break;
                    }
                } else
                    txtField.selectAll();

                pnIndex = lnIndex;
            } catch (SQLException e) {
                MsgBox.showOk(e.getMessage());
                System.exit(1);
            }
    };
 
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{ 
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
            
        try {   
            if(!nv){ /*Lost Focus*/
                switch (lnIndex){
                    case 1:
                    case 2:
                    case 3:
                    case 16:
                        break;
                    case 04:
                        if (!StringUtil.isNumeric(lsValue))
                            oTrans.setMaster(lnIndex, "");
                       else
                            oTrans.setMaster(lnIndex, lsValue);
                }
            } else
                txtField.selectAll();

            pnIndex = lnIndex;
        } catch (SQLException e) {
            MsgBox.showOk(e.getMessage());
            System.exit(1);
        }
    };

    private void unloadForm(){
        StackPane myBox = (StackPane) AnchorMain.getParent();
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
    
    private void loadIncentiveDetail(String fsCode, int fnRow) throws SQLException{
        try {
            Stage stage = new Stage();
            
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("AddIncentives.fxml"));

            AddIncentivesController loControl = new AddIncentivesController();
            loControl.setGRider(oApp);
            loControl.setIncentiveObject(oTrans);
            loControl.setIncentiveCode(fsCode);
            loControl.setTableRow(fnRow);
            
            fxmlLoader.setController(loControl);
            
            //load the main interface
            Parent parent = fxmlLoader.load();
                
            parent.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });
            
            //set the main interface as the scene
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("");
            stage.showAndWait();
            
            loadDetail();
            loadIncentives();
        } catch (IOException e) {
            e.printStackTrace();
            MsgBox.showOk(e.getMessage());
            System.exit(1);
        }
    }
    private void loadDeductionDetail(int fnRow) throws SQLException{
        try {
            Stage stage = new Stage();
            
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("AddDeduction.fxml"));

            AddDeductionController loControl1 = new AddDeductionController();
            loControl1.setGRider(oApp);
            loControl1.setDeductionObject(oTrans);
            loControl1.setTableRows(fnRow);
            
            fxmlLoader.setController(loControl1);
            
            //load the main interface
            Parent parent = fxmlLoader.load();
                
            parent.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });
            
            //set the main interface as the scene
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("");
            stage.showAndWait();
            
            loadDetail();
            loadIncentives();
        } catch (IOException e) {
            e.printStackTrace();
            MsgBox.showOk(e.getMessage());
            System.exit(1);
        }
    }
    
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        try {
                switch (lsButton){
                case "btnDelEmp":
                    if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Remove this employee?") == true){  
                        if (oTrans.removeDetail(pnEmp+ 1))
                            loadDetail();
                        else
                            MsgBox.showOk(oTrans.getMessage());
                    }
                  
                    break;
                case "btnNew": 
                        if (oTrans.NewTransaction() ){
                            clearFields();
                            loadMaster();
                            pnEditMode = EditMode.ADDNEW;
                        } else
                            MsgBox.showOk(oTrans.getMessage());
                    break;
                case "btnAddIncentives":
                    if (oTrans.searchIncentive("", false)){
                        loadIncentives();
                    }
                    break;
                case "btnAddDeductions":
                    String lsValue = ShowMessageFX.InputText("Please input deduction description.", "Add Deduction", "Add Deduction");

                        if (!lsValue.isEmpty()){
                            if (oTrans.addDeduction(lsValue))
                                loadIncentives();
                        }
                    break;
                case "btnClose":
                        if(ShowMessageFX.OkayCancel(null, pxeModuleName, "Are you sure, do you want to close?") == true){
                            if(state){
                                onsuccessUpdate();
                            }else{ 
                                unloadForm();
                            }
                            break;
                        } 
                            return;
                case "btnSave":
                        if (oTrans.SaveTransaction()){
                            
                            MsgBox.showOk("Transaction save successfully.");
                            if(state){
                               onsuccessUpdate();
                            }else{
                                clearFields();
                                oTrans = new Incentive(oApp, oApp.getBranchCode(), false);
                                oTrans.setListener(oListener);
                                oTrans.setWithUI(true);
                                pnEditMode = EditMode.UNKNOWN;
                            }
                        } else {
                            MsgBox.showOk(oTrans.getMessage());
                        }
                    break;
                 case "btnBrowse":
                        
                        if (oTrans.SearchTransaction(txtSeeks21.getText(), true)){
                            loadMaster();
                            loadIncentives();
                        } else if(oTrans.SearchTransaction(txtSeeks22.getText(), false)){
                            loadMaster();
                            loadIncentives();
                        }else 
                            MsgBox.showOk(oTrans.getMessage());
//                         if (oTrans.SearchTransaction(txtSeeks21.getText(), false)){
//                            loadMaster();
//                            loadIncentives();
//                            pnEditMode = EditMode.READY;
//                        }else
//                            MsgBox.showOk(oTrans.getMessage());
                        break;
                 case "btnUpdate":  
                        if (oTrans.UpdateTransaction()){
                            pnEditMode = oTrans.getEditMode(); 
                          } else 
                            MsgBox.showOk(oTrans.getMessage());
                    break;
                case "btnCancel":
                        if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to disregard changes?") == true){  
                            if(state){
                               onsuccessUpdate();
                            }else{
                                clearFields();
                                oTrans = new Incentive(oApp, oApp.getBranchCode(), false);
                                oTrans.setListener(oListener);
                                oTrans.setWithUI(true);
                                pnEditMode = EditMode.UNKNOWN;
                            }
                        }
                    break;
            }
            initButton(pnEditMode);
        } catch (SQLException e) {
                    e.printStackTrace();
                    MsgBox.showOk(e.getMessage());
                } 
    }
    private void loadMaster() throws SQLException {
        txtSeeks21.setText((String) oTrans.getMaster(1));
        txtSeeks22.setText((String) oTrans.getMaster(16));
        
        txtField01.setText((String) oTrans.getMaster(1));
        txtField02.setText(CommonUtils.xsDateMedium((Date) oTrans.getMaster(2)));
        txtField03.setText((String) oTrans.getMaster(17));
        txtField04.setText((String) oTrans.getMaster(4));
        txtField05.setText((String) oTrans.getMaster(5));
        txtField16.setText((String) oTrans.getMaster(16));
        txtField04.setDisable(false);
            
        for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
            data.add(new TableModel(String.valueOf(lnCtr),
                    oTrans.getDetail(lnCtr, "xEmployNm").toString(),
                    oTrans.getDetail(lnCtr, "xEmpLevNm").toString(),
                    oTrans.getDetail(lnCtr, "xPositnNm").toString(),
                    oTrans.getDetail(lnCtr, "xSrvcYear").toString(),
                    oTrans.getDetail(lnCtr, "nTotalAmt").toString()));
        }       
        pnRow = 0;
        pnEmp = 0;
    } 
    private void clearFields(){
        txtField01.setText("");
        txtField02.setText("");
        txtField03.setText("");
        txtField04.setText("");
        txtField05.setText("");
        txtField16.setText("");
        txtSeeks21.setText("");
        txtSeeks22.setText("");
        data.clear();
        inc_data.clear();
    }
        
    private void initGrid() {
        index01.setStyle("-fx-alignment: CENTER;");
        index02.setStyle("-fx-alignment: CENTER-LEFT;");
        index03.setStyle("-fx-alignment: CENTER-LEFT;");
        index04.setStyle("-fx-alignment: CENTER-LEFT;");
        index05.setStyle("-fx-alignment: CENTER-LEFT;");
        index06.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        index01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        index02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        index03.setCellValueFactory(new PropertyValueFactory<>("index03"));
        index04.setCellValueFactory(new PropertyValueFactory<>("index04"));
        index05.setCellValueFactory(new PropertyValueFactory<>("index05"));
        index06.setCellValueFactory(new PropertyValueFactory<>("index06"));
        tblemployee.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblemployee.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        tblemployee.setItems(data);
        tblemployee.getSelectionModel().select(pnEmp + 1);
        tblemployee.autosize();
    }
    
    private void initGrid1() {
        incindex01.setStyle("-fx-alignment: CENTER;");
        incindex02.setStyle("-fx-alignment: CENTER-LEFT;");
        incindex03.setStyle("-fx-alignment: CENTER-LEFT;");
        incindex04.setStyle("-fx-alignment: CENTER-LEFT;");
        incindex05.setStyle("-fx-alignment: CENTER-LEFT;");
        incindex06.setStyle("-fx-alignment: CENTER-LEFT;");
        incindex07.setStyle("-fx-alignment: CENTER-LEFT;");
        
        incindex01.setCellValueFactory(new PropertyValueFactory<>("incindex01"));
        incindex02.setCellValueFactory(new PropertyValueFactory<>("incindex02"));
        incindex03.setCellValueFactory(new PropertyValueFactory<>("incindex03"));
        incindex04.setCellValueFactory(new PropertyValueFactory<>("incindex04"));
        incindex05.setCellValueFactory(new PropertyValueFactory<>("incindex05"));
        incindex06.setCellValueFactory(new PropertyValueFactory<>("incindex06"));
        incindex07.setCellValueFactory(new PropertyValueFactory<>("incindex07"));
        tblIncentives.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblIncentives.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        tblIncentives.setItems(inc_data);
        tblIncentives.autosize();
    }
    
    @FXML
    private void tblemployee_Clicked(MouseEvent event) {
        pnEmp = tblemployee.getSelectionModel().getSelectedIndex();
    }
    
    @FXML
    private void tblIncentives_Clicked(MouseEvent event) {
        try {
            
            pnRow = tblIncentives.getSelectionModel().getSelectedIndex(); 
            TableIncentives ti = (TableIncentives) tblIncentives.getItems().get(pnRow);
            
            if(ti.getIncindex02().contains("Deduction")){
                
                AddDeductionController.setData(ti);
                loadDeductionDetail(pnRow + 1 - (oTrans.getIncentiveCount())); 
                
            } else{
                loadIncentiveDetail((String) oTrans.getIncentiveInfo(pnRow + 1, "sInctveCD"), pnRow + 1); 
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            MsgBox.showOk(ex.getMessage());
        }
    }
    
    public void tblIncentives_column(){
         incindex01.prefWidthProperty().bind(tblIncentives.widthProperty().multiply(0.04));
         incindex02.prefWidthProperty().bind(tblIncentives.widthProperty().multiply(0.250));
         incindex03.prefWidthProperty().bind(tblIncentives.widthProperty().multiply(0.14));
         incindex04.prefWidthProperty().bind(tblIncentives.widthProperty().multiply(0.14));
         incindex05.prefWidthProperty().bind(tblIncentives.widthProperty().multiply(0.14));
         incindex06.prefWidthProperty().bind(tblIncentives.widthProperty().multiply(0.14));
         incindex07.prefWidthProperty().bind(tblIncentives.widthProperty().multiply(0.14));
         
         incindex01.setResizable(false);  
         incindex02.setResizable(false);  
         incindex03.setResizable(false);  
         incindex04.setResizable(false);  
         incindex05.setResizable(false);  
         incindex06.setResizable(false);
         incindex07.setResizable(false);   
    }
    
    public void tblemployee_column(){
         index01.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.04));
         index02.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.275));
         index03.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.24));
         index04.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.24));
         index05.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.10));
         index06.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.10));
         
         index01.setResizable(false);  
         index02.setResizable(false);  
         index03.setResizable(false);  
         index04.setResizable(false);  
         index05.setResizable(false);  
         index06.setResizable(false);  
    }
    
    private void onsuccessUpdate(){
        StackPane myBox = (StackPane) AnchorMain.getParent();
        myBox.getChildren().clear();
        myBox.getChildren().add(setScene());
          
    }
    private AnchorPane setScene(){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("IncentiveConfirmation.fxml"));

            IncentiveConfirmationController loControl = new IncentiveConfirmationController();
            loControl.setGRider(oApp);
            loControl.setTransaction(oTransnox);
            fxmlLoader.setController(loControl);
            
            //load the main interface
                
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