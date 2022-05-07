/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import static gridergui.CashCountHistoryController.oTrans;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agent.MsgBox;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.CashCount;
import org.rmj.fund.manager.base.InventoryCount;
import org.rmj.fund.manager.base.LTransaction;

/**
 * FXML Controller class
 *
 * @author user
 */
public class InventoryHistoryController  implements Initializable , ScreenInterface{
    private GRider oApp;
    private InventoryCount oTrans;
    static LTransaction listener;
        
    private boolean pbLoaded = false;
    private int pnEditMode;
    
    private int pnRow = -1;
    
    private ObservableList<TableInventory> data = FXCollections.observableArrayList();
    
    @FXML
    private AnchorPane AnchorPaneInventory;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnClose;
    @FXML
    private AnchorPane searchBar;
    @FXML
    private TextField txtSeeks38;
    @FXML
    private TextField txtSeeks39;
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtField03;
    @FXML
    private TextField txtField01;
    @FXML
    private TextField txtField02;
    @FXML
    private TextField txtField04;
    @FXML
    private TableColumn index01;
    @FXML
    private TableColumn index02;
    @FXML
    private TableColumn index03;
    @FXML
    private TableView tblInvDetails;
    @FXML
    private TextField txtField05;
    @FXML
    private TextField txtField06;
    @FXML
    private TextField txtField07;
    @FXML
    private TextField txtField08;
    @FXML
    private TextField txtField09;
    @FXML
    private TextField txtField10;
    @FXML
    private TextField txtField11;
    @FXML
    private TextArea txtField12;
    @FXML
    private TextField txtField13;
    @FXML
    private TextField txtField14;

    private Stage getStage(){
	return (Stage) txtField01.getScene().getWindow();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        listener = new LTransaction() {
            @Override
                public void MasterRetreive(int fnIndex, Object foValue) {
                    
                }
                public void DetailRetreive(int fnRow, int fnIndex, Object foValue) throws SQLException {
                    loadMaster();
                    loadDetail();
                }
        
        };
        // TODO
        
            btnBrowse.setOnAction(this::cmdButton_Click);
            btnClose.setOnAction(this::cmdButton_Click);
            
            txtSeeks38.setOnKeyPressed(this::txtField_KeyPressed);
            txtSeeks39.setOnKeyPressed(this::txtField_KeyPressed);
            
            initGrid();
           
            

            oTrans = new InventoryCount(oApp, oApp.getBranchCode(), false);
            oTrans.setListener(listener);
            oTrans.setTranStat(1023);
            oTrans.setWithUI(true);
            pbLoaded = true;
    } 


    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
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
    
    private void cmdButton_Click(ActionEvent event) {
        try {
            String lsButton = ((Button)event.getSource()).getId();
            switch (lsButton){
                case "btnBrowse":
                        if (oTrans.SearchTransaction(txtSeeks38.getText(), true)){
                            clearFields();
                            loadMaster();
                            loadDetail();
                        }else if (oTrans.SearchTransaction(txtSeeks39.getText(), false)){
                            clearFields();
                            loadMaster();
                            loadDetail();
                        }else{
                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                            clearFields();
                        }
                    break;
                case "btnClose":
                    unloadForm();
                    break;
                default:
//                    ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                    return;
            }
        } catch (SQLException ex) {
            Logger.getLogger(InventoryHistoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
             
        try{
           switch (event.getCode()){
            case F3:
                switch (lnIndex){
                     case 38: /*Search*/
                        if (oTrans.SearchTransaction(txtSeeks38.getText(), false)){
                            clearFields();
                            loadMaster();
                            loadDetail();
                        } else 
                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                            clearFields();
                        break;
                    case 39: /*Search*/
                        if (oTrans.SearchTransaction(txtSeeks39.getText(), false)){
                            clearFields();
                            loadMaster();
                            loadDetail();
                        } else 
                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                            clearFields();
                        break;
                }   
                break;
            case ENTER:
                switch (lnIndex){
                    case 38: /*Search*/
                        if (oTrans.SearchTransaction(txtSeeks38.getText(), false)){
                            clearFields();
                            loadMaster();
                            loadDetail();
                        } else 
                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                        break;
                    case 39: /*Search*/
                        if (oTrans.SearchTransaction(txtSeeks39.getText(), false)){
                            clearFields();
                            loadMaster();
                            loadDetail();
                        } else 
                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                        break;
                }   
                break;
            case DOWN:
                CommonUtils.SetNextFocus(txtField);break;    
            case UP:
                CommonUtils.SetPreviousFocus(txtField);break;
            }    
        }catch(SQLException e){
                ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }
    }
    
    private void loadMaster() throws SQLException {
        oTrans.displayMasFields();
        if(oTrans.getMaster(14).toString().equalsIgnoreCase("0")){
            lblStatus.setVisible(true);
            lblStatus.setText("OPEN");
        }else if(oTrans.getMaster(14).toString().equalsIgnoreCase("1")){
            lblStatus.setVisible(true);
            lblStatus.setText("CLOSED");
        }else if(oTrans.getMaster(14).toString().equalsIgnoreCase("2")){
            lblStatus.setVisible(true);
            lblStatus.setText("POSTED");
        }else if(oTrans.getMaster(14).toString().equalsIgnoreCase("3")){
            lblStatus.setVisible(true);
            lblStatus.setText("CANCELLED");
        }else{
            lblStatus.setVisible(false);
        }
        
        txtField01.setText((String) oTrans.getMaster(1));
        txtField04.setText(oTrans.getMaster(3).toString());
        txtField02.setText((String) oTrans.getMaster(17));
        txtField03.setText((String) oTrans.getMaster(19)); 
    }
    
    private void loadDetail() throws SQLException {    

        data.clear();
        for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
                data.add(new TableInventory(String.valueOf(lnCtr),
                    oTrans.getDetail(lnCtr, "xBarrcode").toString(),
                    oTrans.getDetail(lnCtr, "xDescript").toString(),"","",""));
                
                System.out.println( String.valueOf(lnCtr));
                System.out.println( oTrans.getDetail(lnCtr, "xBarrcode").toString());
                System.out.println( oTrans.getDetail(lnCtr, "xDescript").toString());
            }
        tblInvDetails.getSelectionModel().select(pnRow - 1);   
    } 
 
    private void initGrid() {
        index01.setStyle("-fx-alignment: CENTER;");
        index02.setStyle("-fx-alignment: CENTER-LEFT;");
        index03.setStyle("-fx-alignment: CENTER-LEFT;");
        
        index01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        index02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        index03.setCellValueFactory(new PropertyValueFactory<>("index03"));
        
        tblInvDetails.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblInvDetails.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        data.clear();
        tblInvDetails.setItems(data); 
    }

    @FXML
    private void tblInvDetails_Clicked() {
        pnRow = tblInvDetails.getSelectionModel().getSelectedIndex() + 1;
        getSelectedItem();
       
        tblInvDetails.setOnKeyReleased((KeyEvent t)-> {
                KeyCode key = t.getCode();
                switch (key){
                    case DOWN:
                        pnRow = tblInvDetails.getSelectionModel().getSelectedIndex();
                        if (pnRow == tblInvDetails.getItems().size()) {
                            pnRow = tblInvDetails.getItems().size();
                            getSelectedItem();
                        }else {
                            int y = 1;
                            pnRow = pnRow + y;
                            getSelectedItem();
                        }
                        break;
                    case UP:
                        int pnRows = 0;
                        int x = -1;
                        pnRows = tblInvDetails.getSelectionModel().getSelectedIndex() + 1;
                            pnRow = pnRows; 
                            getSelectedItem();
                        break;
                    default:
                        return; 
                }
            });
    }
    
    private void getSelectedItem(){
            try {
                txtField05.setText((String) oTrans.getDetail(pnRow, "xBarrcode"));
                txtField06.setText((String) oTrans.getDetail(pnRow, "xDescript"));
                txtField07.setText((String) oTrans.getDetail(pnRow, "xWHouseNm"));
                txtField08.setText((String) oTrans.getDetail(pnRow, "xBinNamex"));
                txtField09.setText((String) oTrans.getDetail(pnRow, "xSectnNme"));
                txtField10.setText(String.valueOf(oTrans.getDetail(pnRow, "nQtyOnHnd")));
                txtField11.setText(String.valueOf(oTrans.getDetail(pnRow, "nActCtr01")));
                txtField13.setText(String.valueOf(oTrans.getDetail(pnRow, "nActCtr02"))); 
                txtField14.setText(String.valueOf(oTrans.getDetail(pnRow, "nActCtr03")));  
                txtField12.setText((String) oTrans.getDetail(pnRow, "sRemarksx"));
            }   
            catch (SQLException ex) {
                ex.printStackTrace();
                ShowMessageFX.Warning(getStage(),ex.getMessage(), "Warning", null);
            }
    }
    
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{ 
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
            
        if(!nv){ /*Lost Focus*/
            switch (lnIndex){
                case 05:
                case 06:
                case 07:
                case 8:
                case 9:
                case 10:
                case 11:
                case 13:
                case 14:
                    
            }
        } else
            txtField.selectAll();
    };
    final ChangeListener<? super Boolean> txtArea_Focus = (o,ov,nv)->{ 
        if (!pbLoaded) return;
        
        TextArea txtField = (TextArea)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
        if(!nv){ /*Lost Focus*/
            switch (lnIndex){
                case 12:
                    
            }
        } else
            txtField.selectAll();
    };

    private void unloadForm(){
        StackPane myBox = (StackPane) AnchorPaneInventory.getParent();
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
   private void clearFields(){
        txtField01.setText("");
        txtField02.setText("");
        txtField03.setText("");
        txtField04.setText("");
        txtField05.setText("");
        txtField06.setText("");
        txtField07.setText("");
        txtField08.setText("");
        txtField09.setText("");
        txtField10.setText("");
        txtField11.setText("");
        txtField12.setText("");
        txtField13.setText("");
        txtField14.setText("");
        txtSeeks38.setText("");
        txtSeeks39.setText("");
        data.clear();
    }
}
