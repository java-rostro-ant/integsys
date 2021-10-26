/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agent.MsgBox;
import org.rmj.fund.manager.base.Incentive;

/**
 * FXML Controller class
 *
 * @author user
 */
public class AddIncentivesController implements Initializable, ScreenInterface {     
    private GRider oApp;
    private Incentive oTrans;
    @FXML
    private VBox VBoxForm;
    @FXML
    private Button btnExit;
    @FXML
    private Label lblHeader;
    @FXML
    private TextField txtField1;
    @FXML
    private TextField txtField2;
    @FXML
    private TextField txtField3;
    @FXML
    private TextField txtField4;
    @FXML
    private TextField txtField5;
    @FXML
    private TextArea txtField6;
    @FXML
    private TextField txtField31;
    @FXML
    private TextField txtField41;
    @FXML
    private TextField txtField21;
    @FXML
    private TextArea txtField61;
    @FXML
    private TextField txtField211;
    @FXML
    private TextField txtField2112;
    @FXML
    private Button btnOk12;
    
    AddIncentivesController() {
    
    }
    
    @Override
    public void setGRider(GRider foValue) {
    }
    
    public void setIncentiveObject(Incentive foValue){
        oTrans = foValue;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    private void unloadForm(){
        VBox myBox = (VBox) VBoxForm.getParent();
        myBox.getChildren().clear();
    }    

    @FXML
    
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        
        switch (lsButton){                         
            case "btnExit":
                MsgBox.showOk("hello");
                unloadForm();
                break;
        }
    }
}
