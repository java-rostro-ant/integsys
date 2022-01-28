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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
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
public class CashCountController implements Initializable , ScreenInterface{
    private GRider oApp;
    private LMasDetTrans oListener;
    private Incentive oTrans;

    @FXML
    private TextField txtField01;
    @FXML
    private TextField txtField02;
    @FXML
    private TextField txtField03;
    @FXML
    private TextField txtField04;
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
    private TextField txtField12;
    @FXML
    private TextField txtField13;
    @FXML
    private TextField txtField14;
    @FXML
    private TextField txtField15;
    @FXML
    private TextField txtField16;
    @FXML
    private TextField txtField17;
    @FXML
    private TextField txtField18;
    @FXML
    private TextField txtField19;
    @FXML
    private TextField txtField20;
    @FXML
    private TextField txtField21;
    @FXML
    private TextField txtField22;
    @FXML
    private TextField txtField23;
    @FXML
    private TextField txtField24;
    @FXML
    private TextField txtField25;
    @FXML
    private TextField txtField26;
    @FXML
    private TextField txtSeeks27;
    @FXML
    private TextField txtSeeks28;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnApproved;
    @FXML
    private Button btnDisapproved;
    @FXML
    private Button btnClose;
    @FXML
    private AnchorPane AnchorMainCashCount;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        oListener = new LMasDetTrans() {
            @Override
            public void MasterRetreive(int fnIndex, Object foValue) {
                switch(fnIndex){
                    case 1:
                        txtSeeks27.setText((String) foValue);
                      
                        break;
                    case 2:
                        txtField02.setText((String) foValue); break;
                    case 4:
                        txtField04.setText((String) foValue); break;
                    case 5:
                        txtField05.setText((String) foValue); break;
                    case 17:
                        txtField03.setText((String) foValue); 
                    {
//                        try {
//                            loadIncentives();
//                        } catch (SQLException ex) {
//                            MsgBox.showOk(ex.getMessage());
//                        }
                    }
                        break;

                    case 16:
                        txtSeeks27.setText((String) foValue);
                        txtField16.setText((String) foValue); break;
                }
            }

            @Override
            public void DetailRetreive(int fnRow, int fnIndex, Object foValue) {
            }
        };
        oTrans = new Incentive(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
    }   
    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
     private void unloadForm(){
        StackPane myBox = (StackPane) AnchorMainCashCount.getParent();
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
   private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
//        try {
            switch (lsButton){
                case "btnBrowse":
//                        if (oTrans.SearchTransaction(txtSeeks05.getText(), false)){
//                            loadIncentives();
//                            oTrans.displayDetFields();
//                            pnEditMode = oTrans.getEditMode();
//                        } else 
//                            MsgBox.showOk(oTrans.getMessage());
                    break;
                    
                case "btnApproved":
//                    if (oTrans.CloseTransaction()){
//                            MsgBox.showOk("Transaction success approved");
//                            clearFields();
//                        } else 
//                            MsgBox.showOk(oTrans.getMessage());
                    break;
                case "btnDisapproved":
//                    if (oTrans.CancelTransaction()){
//                            MsgBox.showOk("Transaction success disapproved");
//                            clearFields();
//                        } else 
//                            MsgBox.showOk(oTrans.getMessage());
                    break;
               
                case "btnClose":
                    if(ShowMessageFX.OkayCancel(null, "Employee Bank Info", "Do you want to disregard changes?") == true){
                        unloadForm();
                        break;
                    } else
                        return;
            }
            
//        } catch (SQLException e) {
//            e.printStackTrace();
//            MsgBox.showOk(e.getMessage());
//        }
    } 
   private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
             
//        try{
           switch (event.getCode()){
            case F3:
                switch (lnIndex){
//                    case 27: /*Search*/
//                        if (oTrans.SearchTransaction(txtSeeks27.getText(), false)){
//                                loadIncentives();
//                                pnEditMode = oTrans.getEditMode();
//                            } else 
//                                MsgBox.showOk(oTrans.getMessage());
//                         break;
//                    case 28: /*Search*/
//                        if (oTrans.SearchTransaction(txtSeeks28.getText(), false)){
//                                loadIncentives();
//                                pnEditMode = oTrans.getEditMode();
//                            } else 
//                                MsgBox.showOk(oTrans.getMessage());
//                         break;
                }   
            case ENTER:
                switch (lnIndex){
                    case 27: /*Search*/
//                        if (oTrans.SearchTransaction(txtSeeks27.getText(), false)){
//                                loadIncentives();
//                                pnEditMode = oTrans.getEditMode();
//                            } else 
//                                MsgBox.showOk(oTrans.getMessage());
                         break;
                    case 28: /*Search*/
//                        if (oTrans.SearchTransaction(txtSeeks28.getText(), false)){
//                                loadIncentives();
//                                pnEditMode = oTrans.getEditMode();
//                            } else 
//                                MsgBox.showOk(oTrans.getMessage());
//                         break;
                }   
            case DOWN:
                CommonUtils.SetNextFocus(txtField); break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        } 
//        }catch(SQLException e){
//                MsgBox.showOk(e.getMessage());
//        }
        
    }
    private void clearFields(){
//      PESO QUANTITY
        txtField01.setText("");
        txtField02.setText("");
        txtField03.setText("");
        txtField04.setText("");
        txtField05.setText("");
        txtField06.setText("");
        
//      COINS QUANTITY
        txtField13.setText("");
        txtField14.setText("");
        txtField15.setText("");
        txtField16.setText("");
        txtField17.setText("");
        txtField18.setText("");
        txtField19.setText("");
        
//      PESO TOTAL
        txtField07.setText("0.00");
        txtField08.setText("0.00");
        txtField09.setText("0.00");
        txtField10.setText("0.00");
        txtField11.setText("0.00");
        txtField12.setText("0.00");
//      COINS TOTAL
        txtField20.setText("0.00");
        txtField21.setText("0.00");
        txtField22.setText("0.00");
        txtField23.setText("0.00");
        txtField24.setText("0.00");
        txtField25.setText("0.00");
        txtField26.setText("0.00");
        
        txtSeeks27.setText("");
        txtSeeks28.setText("");
        oTrans = new Incentive(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
    }
}
