/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;


/**
 *
 * @author User
 */
public class RaffleDrawController implements Initializable , ScreenInterface{
    private GRider oApp;
//    private RafflePromo oTrans;

    
    private int pnIndex = -1;
    private int pnEditMode;
    private int pnRow = 0;

    private boolean pbLoaded = false;



    @FXML
    private Button btnStart,btnStop,btnClear,btnClose;
    @FXML
    private AnchorPane AnchorMainPanaloParameter;
    @FXML
    private Label lblField06,lblField05,
            lblField04,lblField03,lblField02,lblField01;

    private Stage getStage(){
	return (Stage) lblField01.getScene().getWindow();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
        
//        oTrans = new RafflePromo(oApp, oApp.getBranchCode(), false);
//        oTrans.setWithUI(true);
        
        btnStart.setOnAction(this::cmdButton_Click);
        btnStop.setOnAction(this::cmdButton_Click);
        btnClear.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        
 
        
        pnEditMode = EditMode.UNKNOWN;
    }    

    @Override
    public void setGRider(GRider foValue) {
         oApp = foValue;
    }
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
//        try {
            switch (lsButton){
                 case "btnStart":
                    break;
                case "btnStop": 
                    break;
                 case "btnSave":                    
                    break;
                case "btnClose":
                    if(ShowMessageFX.OkayCancel(null, "Panalo Parameter", "Are you sure, do you want to close?") == true){
                        unloadForm();
                        break;
                    } else
                        return;
            }
            

//        } catch (SQLException e) {
//            e.printStackTrace();
//            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
//        }catch (NullPointerException e) {
//            e.printStackTrace();
//            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
//        }
    } 

    public void clearFields(){
        lblField01.setText("");
        lblField02.setText("");
        lblField03.setText("");
        lblField04.setText("");
        lblField05.setText("");
        lblField06.setText("");
    }
    
    private void unloadForm(){
        StackPane myBox = (StackPane) AnchorMainPanaloParameter.getParent();
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
        
    }
    
      
  
}
