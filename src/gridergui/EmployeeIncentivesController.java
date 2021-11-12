package gridergui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;
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
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import javafx.scene.input.KeyEvent;
import org.rmj.appdriver.StringUtil;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.fund.manager.base.LMasDetTrans;

public class EmployeeIncentivesController implements Initializable, ScreenInterface {
    private final String pxeModuleName = "EmployeeIncentivesController";
    private double xOffset = 0;
    private double yOffset = 0;
    
    private GRider oApp;
    private Incentive oTrans;
    private LMasDetTrans oListener;
    
    private int pnIndex = -1;    
    private int pnRow = -1;
    private int pnEditMode;

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
    private AnchorPane searchBar;
    @FXML
    private Button btnBrowse1;    
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
    private TextField txtSeeks01;
    @FXML
    private TextField txtSeeks02;
    @FXML
    private TableView tblIncentives;
    private TableColumn<?, ?> Index01;
    private TableColumn<?, ?> Index02;
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
        
        btnNew.setOnAction(this::cmdButton_Click);
        btnAddIncentives.setOnAction(this::cmdButton_Click);
        btnAddDeductions.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        
        //initialize class
        oTrans  = new Incentive(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        
        /*Add listener to text fields*/
        txtSeeks01.focusedProperty().addListener(txtField_Focus);
        txtSeeks02.focusedProperty().addListener(txtField_Focus);
        txtField01.focusedProperty().addListener(txtField_Focus);
        txtField02.focusedProperty().addListener(txtField_Focus);
        txtField03.focusedProperty().addListener(txtField_Focus);
        txtField04.focusedProperty().addListener(txtField_Focus);
        txtField16.focusedProperty().addListener(txtField_Focus);
        
        txtSeeks01.setOnKeyPressed(this::txtField_KeyPressed);
        txtSeeks02.setOnKeyPressed(this::txtField_KeyPressed);
        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
        txtField02.setOnKeyPressed(this::txtField_KeyPressed);
        txtField03.setOnKeyPressed(this::txtField_KeyPressed);
        txtField04.setOnKeyPressed(this::txtField_KeyPressed);
        txtField16.setOnKeyPressed(this::txtField_KeyPressed);
        
        txtField05.setOnKeyPressed(this::txtArea_KeyPressed);
        txtField05.focusedProperty().addListener(txtArea_Focus);
        
        CommonUtils.addTextLimiter(txtField04, 6);
        CommonUtils.addTextLimiter(txtField05, 128);
        
        pnEditMode = EditMode.UNKNOWN;
        pbLoaded = true;
    } 
    
    private void txtArea_KeyPressed(KeyEvent event){
        TextArea txtField = (TextArea)event.getSource();
        int lnIndex = Integer.parseInt(((TextArea)event.getSource()).getId().substring(8,10));
        String lsValue = txtField.getText();
            
        switch (event.getCode()){
        case ENTER:
        case DOWN:
            CommonUtils.SetNextFocus(txtField);
            break;
        case UP:
            CommonUtils.SetPreviousFocus(txtField);
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
                        case 3:
                            if(!oTrans.searchDepartment(lsValue, false)) MsgBox.showOk("No department was selected.");
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
    
    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
    
    private void loadIncentives() throws SQLException{
        //load to grid the incentives.
        inc_data.clear();
        int lnCtr; 
        System.out.println("INCENTIVES");
//        for (int counter = ; counter > 0; counter--){
//            System.out.println(counter);
       
        for (lnCtr = 1; lnCtr <= oTrans.getIncentiveCount(); lnCtr++){
            inc_data.add(new TableIncentives(String.valueOf(lnCtr),
                oTrans.getIncentiveInfo(lnCtr, "xInctvNme").toString(),
                oTrans.getIncentiveInfo(lnCtr, "nQtyGoalx").toString(),
                oTrans.getIncentiveInfo(lnCtr, "nQtyActlx").toString(),
                oTrans.getIncentiveInfo(lnCtr, "nAmtGoalx").toString(),
                oTrans.getIncentiveInfo(lnCtr, "nAmtActlx").toString(),
                oTrans.getIncentiveInfo(lnCtr, "nInctvAmt").toString()));
            
            System.out.println(oTrans.getIncentiveInfo(lnCtr, "xInctvNme"));
            System.out.println(oTrans.getIncentiveInfo(lnCtr, "nQtyGoalx"));
            System.out.println(oTrans.getIncentiveInfo(lnCtr, "nQtyActlx"));
            System.out.println(oTrans.getIncentiveInfo(lnCtr, "nAmtGoalx"));
            System.out.println(oTrans.getIncentiveInfo(lnCtr, "nAmtActlx"));
            System.out.println(oTrans.getIncentiveInfo(lnCtr, "nInctvAmt"));
        }
       
            System.out.println("DEDUCTIONS");
            for (lnCtr = 1; lnCtr <= oTrans.getDeductionCount(); lnCtr++){
                inc_data.add(new TableIncentives(String.valueOf(tblIncentives.getItems().size() + 1), 
                    oTrans.getDeductionInfo(lnCtr, "sRemarksx").toString(),
                    "",
                    "",
                    "",
                    "",
                    oTrans.getDeductionInfo(lnCtr, "nDedctAmt").toString()));
                
                //to display these fields on grid.
                System.out.println(oTrans.getDeductionInfo(lnCtr, "sRemarksx"));
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println(oTrans.getDeductionInfo(lnCtr, "nDedctAmt"));
            }
        initGrid1();
        
    }
    
    private void loadDetail(){
        try {
            data.clear();
            for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
                data.add(new TableModel(String.valueOf(lnCtr),
                    oTrans.getDetail(lnCtr, "xEmployNm").toString()));
                
                //to display these fields.
                System.out.println(oTrans.getDetail(lnCtr, "xEmployNm"));
                System.out.println(oTrans.getDetail(lnCtr, "xEmpLevNm"));
                System.out.println(oTrans.getDetail(lnCtr, "xPositnNm"));
                System.out.println(oTrans.getDetail(lnCtr, "xSrvcYear"));
                System.out.println(oTrans.getDetail(lnCtr, "nTotalAmt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MsgBox.showOk(e.getMessage());
        }
    }

    final ChangeListener<? super Boolean> txtArea_Focus = (o,ov,nv)->{ 
        if (!pbLoaded) return;
        
        TextArea txtField = (TextArea)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 9));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
            
        try {
            if(!nv){ /*Lost Focus*/
                switch (lnIndex){
                    case 5:
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
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 9));
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
                    case 4:
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
        AnchorPane myBox = (AnchorPane) AnchorMain.getParent();
        myBox.getChildren().clear();
    }
    
    private void loadIncentiveDetail(){
        try {
            Stage stage = new Stage();
            
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("AddIncentives.fxml"));

            AddIncentivesController loControl = new AddIncentivesController();
            loControl.setGRider(oApp);
            loControl.setIncentiveObject(oTrans);
            
            fxmlLoader.setController(loControl);
            
            //loControl.maintransaction(txtField1.getText());
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
        } catch (IOException e) {
            e.printStackTrace();
            MsgBox.showOk(e.getMessage());
            System.exit(1);
        }
    }
    
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        switch (lsButton){
            case "btnNew": 
                try {
                    if (oTrans.NewTransaction() ){
                        clearFields();
                        loadMaster();
                    } else
                        MsgBox.showOk(oTrans.getMessage());
                } catch (SQLException e) {
                    e.printStackTrace();
                    MsgBox.showOk(e.getMessage());
                }            
                break;
            case "btnAddIncentives":
                try {
                    if (oTrans.searchIncentive("", false))
                        loadIncentives();
                    else
                        MsgBox.showOk(oTrans.getMessage());
                } catch (SQLException e) {
                    e.printStackTrace();
                    MsgBox.showOk(e.getMessage());
                }
                
                //tawagin ito pag update lang.
                //loadIncentiveDetail();
                break;
            case "btnAddDeductions":
                String lsValue = ShowMessageFX.InputText("Please input deduction description.", "Add Deduction", "Add Deduction");
                
                if (!lsValue.isEmpty()){
                    try {
                        if (oTrans.addDeduction(lsValue))
                            loadIncentives();
                        else
                            MsgBox.showOk(oTrans.getMessage());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        MsgBox.showOk(e.getMessage());
                    }
                }
                break;
            case "btnClose":
                Stage stage = new Stage();
                stage.close();
                break;
            case "btnSave":
                break;
        }
    } 

    private void loadMaster() throws SQLException {
        txtField01.setText((String) oTrans.getMaster(1));
        txtField02.setText(CommonUtils.xsDateMedium((Date) oTrans.getMaster(2)));
        txtField03.setText((String) oTrans.getMaster(17));
        txtField04.setText((String) oTrans.getMaster(4));
        txtField05.setText((String) oTrans.getMaster(5));
        txtField16.setText((String) oTrans.getMaster(16));
            
        txtField04.setDisable(false);
            
        for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
            data.add(new TableModel(String.valueOf(lnCtr),
                oTrans.getDetail(lnCtr, "xEmployNm").toString()));
        }       
        initGrid();
        
        pnRow = 0;
    }
    
    
    
    private void clearFields(){
        txtField01.setText("");
        txtField02.setText("");
        txtField03.setText("");
        txtField04.setText("");
        txtField05.setText("");
        txtField16.setText("");
        txtSeeks01.setText("");
        txtSeeks02.setText("");
    }
        
    private void initGrid() {
        index01.setStyle("-fx-alignment: CENTER;");
        index02.setStyle("-fx-alignment: CENTER-LEFT;");
        
        index01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        index02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblemployee.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblemployee.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
               
            });
        });
        tblemployee.setItems(data);
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
    }
    
    @FXML
    private void tblIncentives_Clicked(MouseEvent event) {
        pnRow = tblIncentives.getSelectionModel().getSelectedIndex(); 
        
        loadIncentiveDetail();
       MsgBox.showOk("ola");
    }
}       