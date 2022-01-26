/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agent.MsgBox;
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
    private TextField txtSeeks26;
    @FXML
    private TextField txtSeeks27;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnApproved;
    @FXML
    private Button btnDisapproved;
    @FXML
    private Button btnClose;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        oListener = new LMasDetTrans() {
            @Override
            public void MasterRetreive(int fnIndex, Object foValue) {
                switch(fnIndex){
                    case 1:
                        txtSeeks26.setText((String) foValue);
                      
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
     
   
}
