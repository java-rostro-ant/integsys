/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agent.MsgBox;
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
    private TextField txtFieldSearch;
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
        // TODO
        //initialize class
        
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
        
        txtField1.focusedProperty().addListener(txtField_Focus);
        txtField2.focusedProperty().addListener(txtField_Focus);
        txtField3.focusedProperty().addListener(txtField_Focus);
        
        oListener = new LMasDetTrans() {
            @Override
            public void MasterRetreive(int i, Object o) {
                switch (i){
                    case 3: //sBackAcct
                        txtField3.setText((String) o);
                        break;
                    case 7: //xEmployNm
                        txtField1.setText((String) o);
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
        
        
        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);
        
    }    
    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
    
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        
        switch (lsButton){
             case "btnBrowse":
                    try {
                        if (oTrans.SearchRecord(txtFieldSearch.getText(), false)){
                            loadRecord();
                            pnEditMode = EditMode.READY;
                        } else 
                            MsgBox.showOk(oTrans.getMessage());

                    } catch (SQLException e) {
                        e.printStackTrace();
                        MsgBox.showOk(e.getMessage());
                    }
                break;
            case "btnNew": //create new transaction
                try {
                    pbLoaded = true;
                    if (oTrans.NewRecord()){
                        loadRecord();
                        pnEditMode = oTrans.getEditMode();
                    } else 
                        MsgBox.showOk(oTrans.getMessage());
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                    MsgBox.showOk(e.getMessage());
                }
                break;
             case "btnSave":
                    try {
                        if (oTrans.SaveRecord()){
                            clearFields();
                            pnEditMode = EditMode.UNKNOWN;
                        } else 
                            MsgBox.showOk(oTrans.getMessage());

                    } catch (SQLException e) {
                        e.printStackTrace();
                        MsgBox.showOk(e.getMessage());
                    }
                break;
            case "btnUpdate":
                    try {
                        if (oTrans.UpdateRecord()){
                            pnEditMode = oTrans.getEditMode();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        MsgBox.showOk(e.getMessage());
                    }
                break;
            case "btnSearch":
                    try {
                        switch (pnIndex){
                            case 1:
                                if (oTrans.searchEmployee(txtField1.getText(), false)) 
                                    MsgBox.showOk(oTrans.getMessage());
                                
                                break;
                            case 2:
                                if (oTrans.searchBank(txtField2.getText(), false))
                                    MsgBox.showOk(oTrans.getMessage());
                                
                                break;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        MsgBox.showOk(e.getMessage());
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
                
                //reload detail
                break;
            case "btnDeactivate":
                //reload detail
                break;
                
            case "btnClose":
                MsgBox.showOk("hello");
                break;
        }
        initButton(pnEditMode);
    } 
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        
        btnCancel.setVisible(lbShow);
        btnSearch.setVisible(lbShow);
        btnSave.setVisible(lbShow);
        
        btnSave.setManaged(lbShow);
        btnCancel.setManaged(lbShow);
        btnSearch.setManaged(lbShow);
        
                
        btnUpdate.setVisible(!lbShow);
        btnBrowse.setVisible(!lbShow);
        btnNew.setVisible(!lbShow);
        txtFieldSearch.setDisable(!lbShow);
        txtField1.setDisable(!lbShow);
        txtField2.setDisable(!lbShow);
        txtField3.setDisable(!lbShow);
        
        if (lbShow){
            txtFieldSearch.setDisable(lbShow);
            txtFieldSearch.clear();
            
            btnCancel.setVisible(lbShow);
            btnSearch.setVisible(lbShow);
            btnSave.setVisible(lbShow);
            btnUpdate.setVisible(!lbShow);
            btnBrowse.setVisible(!lbShow);
            btnNew.setVisible(!lbShow);
            btnActivate.setVisible(lbShow);
            btnDeactivate.setVisible(lbShow);
            btnActivate.setManaged(false);
            btnDeactivate.setManaged(false);
            btnBrowse.setManaged(false);
            btnNew.setManaged(false);
            btnUpdate.setManaged(false);
        }
        else{
            txtFieldSearch.setDisable(lbShow);
            txtFieldSearch.requestFocus();
        }
    }
    public void clearFields(){
        txtField1.clear();
        txtField2.clear();
        txtField3.clear();
    }
    
    private void loadRecord(){
        try {
            txtField1.setText((String) oTrans.getMaster(7));
            txtField2.setText((String) oTrans.getMaster(8));
            txtField3.setText((String) oTrans.getMaster(3));
            
            if (!txtField1.isDisabled()) txtField1.requestFocus();
        } catch (SQLException e) {
            MsgBox.showOk(e.getMessage());
        }
    }
    
    public void setFields(String field1, String field2, String field3){
        txtField1.setText(field1);
        txtField2.setText(field2);
        txtField3.setText(field3);
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
}
