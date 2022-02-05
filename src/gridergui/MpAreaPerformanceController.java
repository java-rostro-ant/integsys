/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agent.MsgBox;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.Incentive;
import org.rmj.fund.manager.base.LMasDetTrans;

/**
 * FXML Controller class
 *
 * @author user
 */
public class MpAreaPerformanceController implements Initializable , ScreenInterface{
    private GRider oApp;
    private Incentive oTrans;
    private LMasDetTrans oListener;
    
    private int pnIndex = -1;
    private int pnEditMode;
    private int pnRow = 0;
    private int pnSubItems = 0;
    private boolean pbLoaded = false;
    private String psOldRec;
    private String psBarcode = "";
    private String psDescript = "";
    ObservableList<String> cType = FXCollections.observableArrayList("Branch", "Main Office", "Both");
    ObservableList<String> cPercent = FXCollections.observableArrayList("No", "Yes", "Both");
    
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtSeeks05;
    @FXML
    private TextField txtSeeks06;
    @FXML
    private TextField txtField01;
    @FXML
    private TextField txtField02;
   
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnClose;
    @FXML
    private AnchorPane AnchorMainMcAreaInfo;
    @FXML
    private HBox hbButtons;
    @FXML
    private Label lblHeader;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        oListener = new LMasDetTrans() {
            @Override
            public void MasterRetreive(int i, Object o) {
                switch (i){
                    case 1: //xEmployNm
                        txtField01.setText((String) o);
                        break;
                    case 2: 
                        txtField02.setText((String) o);
                        break;
                    case 6:
                         try {
                            lblStatus.setVisible(true);
                            if(oTrans.getMaster(6).toString().equalsIgnoreCase("1")){
                                lblStatus.setText("Active");
                                lblStatus.setStyle("-fx-background-color: green;");
                            }else if(oTrans.getMaster(6).toString().equalsIgnoreCase("0")){
                                lblStatus.setText("Inactive");
                                lblStatus.setStyle("-fx-background-color: red;");
                            }else{
                               lblStatus.setVisible(false); 
                            }
                        } catch (SQLException ex) {
                             MsgBox.showOk(oTrans.getMessage());
                        }
                         break;
                }
            }

            @Override
            public void DetailRetreive(int i, int i1, Object o) {
            }
        };
        
        oTrans = new Incentive(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnNew.setOnAction(this::cmdButton_Click);
        btnSave.setOnAction(this::cmdButton_Click);
        btnUpdate.setOnAction(this::cmdButton_Click);
        btnCancel.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
//      text field focus
        txtField01.focusedProperty().addListener(txtField_Focus);
        txtField02.focusedProperty().addListener(txtField_Focus);
//      text field  key pressed
        txtSeeks05.setOnKeyPressed(this::txtField_KeyPressed);
        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
        txtField02.setOnKeyPressed(this::txtField_KeyPressed);
        
        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);
    }    

    @Override
    public void setGRider(GRider foValue) {
         oApp = foValue;
    }
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
//        try {
            switch (lsButton){
                 case "btnBrowse":
                      
                    break;
                case "btnNew": //create new transaction
                        pbLoaded = true;
                       
                    break;
                 case "btnSave":
                       
                       
                    break;
                case "btnUpdate":
                       
                    break;
                case "btnCancel":
                    
                    clearFields();
                    oTrans = new Incentive(oApp, oApp.getBranchCode(), false);
                    oTrans.setListener(oListener);
                    oTrans.setWithUI(true);
                    pnEditMode = EditMode.UNKNOWN;
                    //reload detail
                    break;
                
                case "btnClose":
                    if(ShowMessageFX.OkayCancel(null, "MP Area Performance", "Are you sure, do you want to close?") == true){
                        unloadForm();
                        break;
                    } else
                        return;
            }
            
            initButton(pnEditMode);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            MsgBox.showOk(e.getMessage());
//        }
    } 
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        
        btnCancel.setVisible(lbShow);
        btnSave.setVisible(lbShow);
        
        btnSave.setManaged(lbShow);
        btnCancel.setManaged(lbShow);
        btnUpdate.setVisible(!lbShow);
        btnBrowse.setVisible(!lbShow);
        btnNew.setVisible(!lbShow);
        
        txtSeeks05.setDisable(!lbShow);
        txtField01.setDisable(true);
        txtField02.setDisable(!lbShow);
        
        if (lbShow){
            txtSeeks05.setDisable(lbShow);
            txtSeeks05.clear();
            txtField02.requestFocus();
            btnCancel.setVisible(lbShow);
            btnSave.setVisible(lbShow);
            btnUpdate.setVisible(!lbShow);
            btnBrowse.setVisible(!lbShow);
            btnNew.setVisible(!lbShow);
            btnBrowse.setManaged(false);
            btnNew.setManaged(false);
            btnUpdate.setManaged(false);
        }
        else{
            txtSeeks05.setDisable(lbShow);
            txtSeeks05.requestFocus();
        }
    }
    public void clearFields(){
        txtField01.clear();
        txtField02.clear();
        txtSeeks05.clear();
        txtSeeks06.clear();
        lblStatus.setVisible(false);
        
                
    }
    
    private void unloadForm(){
        StackPane myBox = (StackPane) AnchorMainMcAreaInfo.getParent();
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
    
    private void loadRecord(){
        try {
            if(oTrans.getMaster(6).toString().equalsIgnoreCase("1")){
            lblStatus.setVisible(true);
                lblStatus.setText("Active");
                lblStatus.setStyle("-fx-background-color: green;");
            }else if(oTrans.getMaster(6).toString().equalsIgnoreCase("0")){
                lblStatus.setVisible(true);
                lblStatus.setText("Inactive");
                lblStatus.setStyle("-fx-background-color: red;");
            }else{
                lblStatus.setVisible(false);
            }
            txtField01.setText((String) oTrans.getMaster(1));
            txtField02.setText((String) oTrans.getMaster(2));
            txtSeeks05.setText((String) oTrans.getMaster(2));
        } catch (SQLException e) {
            MsgBox.showOk(e.getMessage());
        }
    }
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean sendIncentives(){
        try {
         
            oTrans.setMaster("sInctveDs", txtField02.getText());
            
            } catch (SQLException ex) {
              MsgBox.showOk(ex.getMessage());
        }

        return true;
    }
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
       
        String lsValue = txtField.getText();
        if (lsValue == null) return;
            
        if(!nv){ //Lost Focus
            try {
                switch (lnIndex){
                    case 3: //sBankAcct
                        oTrans.setMaster(lnIndex, lsValue);
                        break;
                }
            } catch (SQLException e) {
                MsgBox.showOk(e.getMessage());
            }
            
        } else{ //Focus
            pnIndex = lnIndex;
            txtField.selectAll();
        }
    };   
    private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
             
        switch (event.getCode()){
            case F3:
                switch (lnIndex){
                    
                    case 5: /*Search*/
                        break;
                    case 6: /*Search*/
                        break;
                }   
            case ENTER:
            case DOWN:
                CommonUtils.SetNextFocus(txtField); break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        }
        
    }
  
    
}
