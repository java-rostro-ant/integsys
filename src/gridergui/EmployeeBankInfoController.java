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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import org.rmj.fund.manager.base.LMasDetTrans;
import org.rmj.fund.manager.parameters.IncentiveBankInfo;

/**
 * FXML Controller class
 *
 * @author user
 */
public class EmployeeBankInfoController implements Initializable , ScreenInterface{
    private GRider oApp;
    private IncentiveBankInfo oTrans;
    private LMasDetTrans oListener;
    
    private int pnIndex = -1;
    private int pnEditMode;
    private int pnRow = 0;
    private int pnSubItems = 0;
    private boolean pbLoaded = false;
    private String psOldRec;
    private String psBarcode = "";
    private String psDescript = "";
    
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtSeeks5;
    @FXML
    private TextField txtField1;
    @FXML
    private TextField txtField2;
    @FXML
    private TextField txtField3;
    
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnActivate;
    @FXML
    private Button btnDeactivate;
    @FXML
    private AnchorPane AnchorMainBankInfo;
    @FXML
    private HBox hbButtons;
    @FXML
    private Label lblHeader;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        oListener = new LMasDetTrans() {
            @Override
            public void MasterRetreive(int i, Object o) {
                System.out.print("listener = " +  i);
                switch (i){
                    case 3: //sBackAcct
                        txtField3.setText((String) o);
                        break;
                    case 7: //xEmployNm
                        txtField1.setText((String) o);
                        break;
                    case 4: 
                        try {
                            System.out.print(oTrans.getMaster(4));
                            lblStatus.setVisible(true);
                            if(oTrans.getMaster(4).toString().equalsIgnoreCase("1")){
                                lblStatus.setText("Active");
                                lblStatus.setStyle("-fx-background-color: green;");
                            }else if(oTrans.getMaster(4).toString().equalsIgnoreCase("0")){
                                lblStatus.setText("Inactive");
                                lblStatus.setStyle("-fx-background-color: red;");
                            }else{
                               lblStatus.setVisible(false); 
                            }
                        } catch (SQLException ex) {
                             MsgBox.showOk(oTrans.getMessage());
                        }
                        break;
                    case 8: //xBankName
                        txtField2.setText((String) o);
                        break;
                }
            }

            @Override
            public void DetailRetreive(int i, int i1, Object o) {
            }
        };
        
        oTrans = new IncentiveBankInfo(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnNew.setOnAction(this::cmdButton_Click);
        btnSave.setOnAction(this::cmdButton_Click);
        btnUpdate.setOnAction(this::cmdButton_Click);
        btnSearch.setOnAction(this::cmdButton_Click);
        btnCancel.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        btnActivate.setOnAction(this::cmdButton_Click);
        btnDeactivate.setOnAction(this::cmdButton_Click);
//      text field focus
        txtField1.focusedProperty().addListener(txtField_Focus);
        txtField2.focusedProperty().addListener(txtField_Focus);
        txtField3.focusedProperty().addListener(txtField_Focus);
//      text field  key pressed
        txtSeeks5.setOnKeyPressed(this::txtField_KeyPressed);
        txtField1.setOnKeyPressed(this::txtField_KeyPressed);
        txtField2.setOnKeyPressed(this::txtField_KeyPressed);
        txtField3.setOnKeyPressed(this::txtField_KeyPressed); 
        txtField3.textProperty().addListener((final ObservableValue<? extends String> ov, final String oldValue, final String newValue) -> {
            if (txtField3.getText().length() > 12) {
                String s = txtField3.getText().substring(0, 12);
                txtField3.setText(s);
            }
        });
        
        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);
        
    }    
    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
    
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        try {
            switch (lsButton){
                 case "btnBrowse":
                        if (oTrans.SearchRecord(txtSeeks5.getText(), false)){
                            loadRecord();
                            pnEditMode = EditMode.READY;
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                    break;
                case "btnNew": //create new transaction
                        pbLoaded = true;
                        if (oTrans.NewRecord()){
                            loadRecord();
                            pnEditMode = oTrans.getEditMode();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                    break;
                 case "btnSave":
                        if (oTrans.SaveRecord()){
                            clearFields();
                            pnEditMode = EditMode.UNKNOWN;
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                    break;
                case "btnUpdate":
                        if (oTrans.UpdateRecord()){
                            pnEditMode = oTrans.getEditMode();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                    break;
                case "btnSearch":
                        switch (pnIndex){
                            case 1:
                                if (!oTrans.searchEmployee(txtField1.getText(), false)) 
                                    MsgBox.showOk(oTrans.getMessage());

                                break;
                            case 2:
                                if (!oTrans.searchBank(txtField2.getText(), false))
                                    MsgBox.showOk(oTrans.getMessage());

                                break;
                        }
                    break;
                case "btnCancel":
                    clearFields();
                    oTrans = new IncentiveBankInfo(oApp, oApp.getBranchCode(), false);
                    oTrans.setListener(oListener);
                    oTrans.setWithUI(true);
                    pnEditMode = EditMode.UNKNOWN;
                    //reload detail
                    break;
                case "btnActivate":
                    if (oTrans.ActivateRecord()){
                        MsgBox.showOk("Account successfully activated!");
                        clearFields();
                    }else
                        MsgBox.showOk(oTrans.getMessage());
                    break;
                case "btnDeactivate":
                    if (oTrans.DeactivateRecord()){
                        MsgBox.showOk("Account successfully deactivated!");
                        clearFields();
                    }else
                        MsgBox.showOk(oTrans.getMessage());
                    break;

                case "btnClose":
                    if(ShowMessageFX.OkayCancel(null, "Employee Bank Info", "Do you want to disregard changes?") == true){
                        unloadForm();
                        break;
                    } else
                        return;
            }
            
            initButton(pnEditMode);
        } catch (SQLException e) {
            e.printStackTrace();
            MsgBox.showOk(e.getMessage());
        }
    } 
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        
        btnCancel.setVisible(lbShow);
        btnSearch.setVisible(lbShow);
        btnSave.setVisible(lbShow);
        btnActivate.setVisible(!lbShow);
        btnDeactivate.setVisible(!lbShow);
        
        btnSave.setManaged(lbShow);
        btnCancel.setManaged(lbShow);
        btnSearch.setManaged(lbShow);
        btnUpdate.setVisible(!lbShow);
        btnBrowse.setVisible(!lbShow);
        btnNew.setVisible(!lbShow);
        
        txtSeeks5.setDisable(!lbShow);
        txtField1.setDisable(!lbShow);
        txtField2.setDisable(!lbShow);
        txtField3.setDisable(!lbShow);
        
        if (lbShow){
            txtSeeks5.setDisable(lbShow);
            txtSeeks5.clear();
            txtField1.requestFocus();
            btnCancel.setVisible(lbShow);
            btnSearch.setVisible(lbShow);
            btnSave.setVisible(lbShow);
            btnUpdate.setVisible(!lbShow);
            btnBrowse.setVisible(!lbShow);
            btnNew.setVisible(!lbShow);
            btnBrowse.setManaged(false);
            btnNew.setManaged(false);
            btnUpdate.setManaged(false);
            btnActivate.setManaged(false);
            btnDeactivate.setManaged(false);
        }
        else{
            txtSeeks5.setDisable(lbShow);
            txtSeeks5.requestFocus();
        }
    }
    public void clearFields(){
        txtField1.clear();
        txtField2.clear();
        txtField3.clear();
        lblStatus.setVisible(false);
    }
    
    private void unloadForm(){
        StackPane myBox = (StackPane) AnchorMainBankInfo.getParent();
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
            
            if(oTrans.getMaster(4).toString().equalsIgnoreCase("1")){
            lblStatus.setVisible(true);
                lblStatus.setText("Active");
                lblStatus.setStyle("-fx-background-color: green;");
            }else if(oTrans.getMaster(4).toString().equalsIgnoreCase("0")){
                lblStatus.setVisible(true);
                lblStatus.setText("Inactive");
                lblStatus.setStyle("-fx-background-color: red;");
            }else{
                lblStatus.setVisible(false);
            }
            txtField1.setText((String) oTrans.getMaster(7));
            txtField2.setText((String) oTrans.getMaster(8));
            txtField3.setText((String) oTrans.getMaster(3));
            
            if (!txtField1.isDisabled()) txtField1.requestFocus();
        } catch (SQLException e) {
            MsgBox.showOk(e.getMessage());
        }
    }
    
    
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 9));
       
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
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 9));
             
        try{
           switch (event.getCode()){
            case F3:
                switch (lnIndex){
                case 1: /*sBranchCd*/
                     oTrans.searchEmployee(txtField.getText(), false);
                     break;
                case 2: /*sBankNme*/
                     oTrans.searchBank(txtField.getText(), false); 
                     break;
                case 5: /*Search*/
                    if (oTrans.SearchRecord(txtSeeks5.getText(), false)){
                        loadRecord();
                        pnEditMode = EditMode.READY;
                    } else 
                        MsgBox.showOk(oTrans.getMessage());
                     break;
                }   
            case ENTER:
            case DOWN:
                CommonUtils.SetNextFocus(txtField); break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        } 
        }catch(SQLException e){
                MsgBox.showOk(e.getMessage());
        }
        
    }
}


