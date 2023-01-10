/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gridergui;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.LMasDetTrans;
import org.rmj.gcamera.app.Capture;
import org.rmj.appdriver.agentfx.CommonUtils;
import java.util.HashMap;

/**
 * FXML Controller class
 *
 * @author user
 */
public class PanaloRedemptionController implements Initializable, ScreenInterface{
    private GRider oApp;
    private int pnEditMode;
    private boolean pbLoaded = false;
    private LMasDetTrans oListener;
    private JSONArray QRArray;
    public JSONObject jsonData;
    
    @FXML
    private AnchorPane AnchorMainPanaloRedeem;
    
    @FXML
    private TextField 
              txtField01, txtField02, txtField03, txtField04
            , txtField05, txtField06, txtField07, txtField08
            , txtField09, txtField10, txtField11;
    
    @FXML
    private Button btnIssue, btnClose, btnScan, btnRefresh;
    
    @FXML
    private Label lblStatus,lblissueqty;
    private Stage getStage(){
	return (Stage) txtField01.getScene().getWindow();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
                    JSONObject loJSON = getQRValue();
                    
                    if (loJSON.get("result").equals("success")){
                        //ShowMessageFX.Warning(getStage(), String.valueOf(loJSON.get("payload")), "Warning", null);
                        pnEditMode = EditMode.READY;
                        initButton(pnEditMode);
                        
                        QRArray = (JSONArray) loJSON.get("payload");
                        loadField();
                        
                    } else {
                        loJSON = (JSONObject) loJSON.get("error");
                        
                        ShowMessageFX.Warning(getStage(), (String) loJSON.get("message"), "Warning", null);
                    }
                    
                    break;
                case "btnIssue": //create new transaction
                    if(!"".equals(txtField01.getText()) ){
                        initButton(pnEditMode);
                        IssueQty();
                        ShowMessageFX.Warning(getStage(), "Panalo Redemption Issued Successfully!", "Warning", null);
                        clearFields();
                    }else{
                        ShowMessageFX.Warning(getStage(), "No transaction was loaded.", "Warning", null);
                    }
                    break;
                case "btnClose":
                    if(ShowMessageFX.OkayCancel(null, "Panalo Redemption", "Are you sure, do you want to close?") == true){
                        unloadForm();
                    }
                    break;
                case "btnRefresh":
                    pnEditMode = EditMode.UNKNOWN;
                    clearFields();
                    initButton(pnEditMode);

                    break;
            }
        }catch (NullPointerException e) {
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }
    } 
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.READY || fnValue == EditMode.UPDATE);
            if (fnValue == EditMode.READY) {
                btnScan.setVisible(true);
                btnIssue.setVisible(true);
                btnRefresh.setVisible(true);
                lblStatus.setVisible(lbShow);
                lblissueqty.setVisible(!lbShow);
                txtField11.setVisible(lbShow);
            }
        
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
 
        pbLoaded = true;
    }

    private JSONObject getQRValue(){                
        try {
            Capture instance = new Capture();
            CommonUtils.showModal(instance);
        
            String lsValue = instance.getQRResult();
            
            if (!lsValue.isEmpty()){
                JSONParser loParse = new JSONParser();
                JSONObject loJSON = (JSONObject) loParse.parse(lsValue);
                return loJSON;
            }
        } catch (Exception e) {
            JSONObject err_detl = new JSONObject();
            err_detl.put("code", "250");
            err_detl.put("message", e.getMessage());

            JSONObject err_mstr = new JSONObject();
            err_mstr.put("result", "error");
            err_mstr.put("error", err_detl);
            return err_mstr;
        }
        
        JSONObject err_detl = new JSONObject();
        err_detl.put("code", "250");
        err_detl.put("message", "No record to processed.");

        JSONObject err_mstr = new JSONObject();
        err_mstr.put("result", "error");
        err_mstr.put("error", err_detl);
        return err_mstr;
    }
    
    public void loadField(){
           
         JSONArray jsonArray =  QRArray;
         JSONObject foJSON = (JSONObject)jsonArray.get(0);

            txtField01.setText((String) foJSON.get("sPanaloQC"));
            txtField02.setText((String) foJSON.get("dTransact"));
            txtField03.setText((String) foJSON.get("sPanaloDs"));
            txtField04.setText((String) foJSON.get("sSourceNm"));
            txtField05.setText((String) foJSON.get("sAcctNmbr"));
            txtField06.setText((String) foJSON.get("sPanaloQC"));            
            txtField07.setText((String) foJSON.get("sPanaloCD"));
            
            txtField08.setText((String) foJSON.get("nItemQtyx"));
            txtField09.setText((String) foJSON.get("nRedeemxx"));
            txtField10.setText((String) foJSON.get("dRedeemxx"));            
            txtField11.setText((String) foJSON.get("nRedeemxx"));
    } 

    public void IssueQty(){
           String sURL = "https://restgk.guanzongroup.com.ph/gconnect/redeem_my_panalo.php";
           Map<String, String> headers = APIParam.getHeader1();
        
        JSONObject param = new JSONObject();
        
        param.put("transnox", txtField01.getText());
        param.put("quantity", txtField11.getText());
       
        String response;
        try {
            response = WebClient.sendHTTP(sURL, param.toJSONString(), (HashMap<String, String>) headers);
            if(response == null){
                System.out.println("No Response");
                System.exit(1);
            } 
            System.out.println(response);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    } 
            
}
    
    

