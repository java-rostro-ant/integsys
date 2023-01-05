/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gridergui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.LMasDetTrans;
import org.rmj.fund.manager.parameters.Panalo;
import org.rmj.gcamera.app.Capture;
import org.rmj.gcamera.view.APITrans;

/**
 * FXML Controller class
 *
 * @author user
 */
public class PanaloRedemptionController implements Initializable, ScreenInterface{
    private GRider oApp;
    private Panalo oTrans;
    private int pnEditMode;
    private boolean pbLoaded = false;
    private LMasDetTrans oListener;
    @FXML
    private AnchorPane AnchorMainPanaloRedeem;
    @FXML
    private TextField txtField01,txtField02,txtField03,txtField04
                    ,txtField05,txtField06,txtField07,txtField08,
                    txtField09, txtField10,txtField11;
    @FXML
    private Button btnIssue ,btnClose, btnScan ,btnRefresh;
    @FXML
    private Label lblStatus,lblissueqty;
    private Stage getStage(){
	return (Stage) txtField01.getScene().getWindow();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);
        pbLoaded = true;
//        initTxtField(pnEditMode);
        
        
        btnScan.setOnAction(this::cmdButton_Click);
        btnIssue.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        btnRefresh.setOnAction(this::cmdButton_Click);
    }    
     @Override
    public void setGRider(GRider foValue) {
         oApp = foValue;
    }
    private void unloadForm(){
        StackPane myBox = (StackPane) AnchorMainPanaloRedeem.getParent();
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
        try {
            switch (lsButton){
                case "btnScan": 
                    pnEditMode = EditMode.UPDATE;
                   initButton(pnEditMode);
                   //capture code
                    break;
                case "btnIssue": //create new transaction
                        if(txtField01.getText() != "" ){
                            initButton(pnEditMode);
                        }else{
                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                        }
                    break;
                case "btnClose":
                    if(ShowMessageFX.OkayCancel(null, "Panalo Redemption", "Are you sure, do you want to close?") == true){
                        unloadForm();
                        break;
                    } else {
            }
                case "btnRefresh":
                    pnEditMode = EditMode.UNKNOWN;
                    clearFields();
                    initButton(pnEditMode);

                    break;
            }
            
//            initButton(pnEditMode);
        }catch (NullPointerException e) {
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }
    } 
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
//        btnScan.setVisible(!lbShow);
//        btnIssue.setVisible(!lbShow);
//        btnRefresh.setVisible(!lbShow);
        lblStatus.setVisible(lbShow);
        lblissueqty.setVisible(lbShow);
        txtField11.setVisible(lbShow);
        
       
    }
     public void clearFields(){
        txtField01.clear();
        txtField02.clear();
        txtField03.clear();
        txtField04.clear();
        txtField05.clear();
        txtField06.clear();
        txtField07.clear();
        txtField08.clear();
        txtField09.clear();
        txtField10.clear();
        txtField11.clear();
 
        oTrans = new Panalo(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        pbLoaded = true;
    }
    
}
//    private void initTxtField(int fnValue){
//        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
//        txtField01.setEditable (!lbShow);
//        txtField02.setDisable(!lbShow);
//        txtField03.setDisable(!lbShow);
//        txtField04.setDisable (!lbShow);
//        txtField05.setDisable(!lbShow);
//        txtField06.setDisable(!lbShow);
//        txtField07.setDisable (!lbShow);
//        txtField08.setDisable(!lbShow);
//        txtField09.setDisable(!lbShow);
//        txtField10.setDisable(!lbShow);
//    }
    
    

