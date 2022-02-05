/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import org.rmj.fund.manager.base.CashCount;
import org.rmj.fund.manager.base.Incentive;
import org.rmj.fund.manager.base.LMasDetTrans;
import org.rmj.fund.manager.base.LTransaction;

/**
 * FXML Controller class
 *
 * @author user
 */
public class CashCountController implements Initializable , ScreenInterface{
    private GRider oApp;
    
    static CashCount oTrans;
    static LTransaction listener;
    
    @FXML
    private Label lbl1000p ;
    @FXML
    private Label lbl500p ;
    @FXML
    private Label lbl200p ;
    @FXML
    private Label lbl100p ;
    @FXML
    private Label lbl50p ;
    @FXML
    private Label lbl20p ;
    
    @FXML
    private Label lbl20px ;
    @FXML
    private Label lbl10px ;
    @FXML
    private Label lbl5px ;
    @FXML
    private Label lbl1px ;
    @FXML
    private Label lbl25cx ;
    @FXML
    private Label lbl10cx ;
    @FXML
    private Label lbl5cx ;
    @FXML
    private Label lbl1cx ;
    @FXML
    private Label lblPesoTotal;
    @FXML
    private Label lblCoinTotal;
    @FXML
    private Label lblGrandTotal;
    
    @FXML
    private Label lblStatus;

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
    private TextField txtField27;
    @FXML
    private TextField txtField28;
    @FXML
    private TextField txtField29;
    @FXML
    private TextField txtField30;
    @FXML
    private TextField txtField31;
    @FXML
    private TextField txtField32;
    @FXML
    private TextField txtField33;
    @FXML
    private TextField txtField34;
    @FXML
    private TextField txtField35;
    @FXML
    private TextField txtField36;
    @FXML
    private TextField txtField37;
    @FXML
    private TextField txtSeeks38;
    @FXML
    private TextField txtSeeks39;
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
        listener = new LTransaction() {
            @Override
            public void MasterRetreive(int fnIndex, Object foValue) {
                    switch(fnIndex){
                      case 1:
                          txtField29.setText((String) foValue);
                          break;
                      case 3:
                          txtField37.setText((String) foValue);
                          break;
                      case 4:
                          txtField19.setText((String) foValue);
                          break;
                      case 5:
                          txtField18.setText((String) foValue);
                          break;
                      case 6:
                          txtField17.setText((String) foValue);
                          break;
                      case 7:
                          txtField15.setText((String) foValue);
                          break;
                      case 8:
                          txtField14.setText((String) foValue);
                          break;
                      case 9:
                          txtField13.setText((String) foValue);
                          break;
                      case 10:
                          break;
                      case 11:
                          break;
                      case 12:
                          break;
                      case 13:
                          break;
                      case 14:
                          break;
                      case 15:
                          break;
                      case 16:
                          txtField33.setText((String) foValue);
                          break;
                      case 17:
                          txtField34.setText((String) foValue);
                          break;
                      case 18:
                          txtField35.setText((String) foValue);
                          break;
                      case 19:
                          txtField36.setText((String) foValue);
                          break;
                      case 21:
                          break;
                      case 22:
                          break;
                      case 23:
                          txtField31.setText((String) foValue);
                          break;
                      case 24:
                          break;
                      case 25:
                          break;
                      case 26:
                          break;
                      case 27:
                          break;
                      case 28:
                          break;
                      case 29:
                          break;
                      case 30:
                          txtField30.setText((String) foValue);
                          txtSeeks38.setText((String) foValue);
                          break;
                      case 32:
                          break;
                  }
            }
        
        };
        
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        btnApproved.setOnAction(this::cmdButton_Click);
        btnDisapproved.setOnAction(this::cmdButton_Click);
        txtSeeks38.setOnKeyPressed(this::txtField_KeyPressed);
        txtSeeks39.setOnKeyPressed(this::txtField_KeyPressed);
        oTrans = new CashCount(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(listener);
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
        System.out.println(lsButton);
        try {
            switch (lsButton){
                case "btnBrowse":
                        if (oTrans.SearchTransaction(txtSeeks38.getText(), true)){
//                            loadCashCount();
                            loadCasCount();
                        } else if(oTrans.SearchTransaction(txtSeeks39.getText(), false)){
                            loadCasCount();
                        }else 
                            MsgBox.showOk(oTrans.getMessage());
                    break;
                    
                case "btnApproved":
                    if (oTrans.CloseTransaction()){
                            MsgBox.showOk("Transaction successfully approved.");
                            clearFields();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                    break;
                case "btnDisapproved":
                    if (oTrans.CancelTransaction()){
                            MsgBox.showOk("Transaction successfully disapproved.");
                            clearFields();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                    break;
               
                case "btnClose":
                     if(ShowMessageFX.OkayCancel(null, "Cash Count", "Are you sure, do you want to close?") == true){
                        unloadForm();
                        break;
                    } else
                        return;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            MsgBox.showOk(e.getMessage());
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
                        if (oTrans.SearchTransaction(txtSeeks38.getText(), true)){
                            loadCasCount();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                        break;
                    case 39: /*Search*/
                        if (oTrans.SearchTransaction(txtSeeks39.getText(), false)){
                            loadCasCount();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                        break;
                }   
                break;
            case ENTER:
                switch (lnIndex){
                    case 38: /*Search*/
                        if (oTrans.SearchTransaction(txtSeeks38.getText(), true)){
                            loadCasCount();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                        break;
                    case 39: /*Search*/
                        if (oTrans.SearchTransaction(txtSeeks39.getText(), false)){
                            loadCasCount();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                        break;
                }   
                break;
            case DOWN:
                CommonUtils.SetNextFocus(txtField); break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);break;
        } 
        }catch(SQLException e){
                MsgBox.showOk(e.getMessage());
        }
        
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
        txtField20.setText("");
        
//      PESO TOTAL
        txtField07.setText("0.00");
        txtField08.setText("0.00");
        txtField09.setText("0.00");
        txtField10.setText("0.00");
        txtField11.setText("0.00");
        txtField12.setText("0.00");
//      COINS TOTAL
        txtField21.setText("0.00");
        txtField22.setText("0.00");
        txtField23.setText("0.00");
        txtField24.setText("0.00");
        txtField25.setText("0.00");
        txtField26.setText("0.00");
        txtField27.setText("0.00");
        txtField28.setText("0.00");
//      OTHER DETAILS
        txtField29.setText("");
        txtField30.setText("");
        txtField31.setText("");
        txtField32.setText("");
        txtField33.setText("");
        txtField34.setText("");
        txtField35.setText("");
        txtField36.setText("");
        txtField37.setText("");
        
        txtSeeks38.setText("");
        txtSeeks39.setText("");
        lblPesoTotal.setText("₱ 0.00");
        lblCoinTotal.setText("₱ 0.00");
        lblGrandTotal.setText("₱ 0.00");
        oTrans = new CashCount(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(listener);
        oTrans.setWithUI(true);
    }

    private void loadCasCount() {
          try {
            if(oTrans.getMaster(29).toString().equalsIgnoreCase("0")){
                lblStatus.setVisible(true);
                lblStatus.setText("OPEN");
            }else if(oTrans.getMaster(29).toString().equalsIgnoreCase("1")){
                lblStatus.setVisible(true);
                lblStatus.setText("CLOSED");
            }
            else if(oTrans.getMaster(29).toString().equalsIgnoreCase("2")){
                lblStatus.setVisible(true);
                lblStatus.setText("POSTED");
            }
            else if(oTrans.getMaster(29).toString().equalsIgnoreCase("3")){
                lblStatus.setVisible(true);
                lblStatus.setText("CANCELLED");
            }else{
                lblStatus.setVisible(false);
            }
         
            lblStatus.setStyle("-fx-background-color: #ffd9b3;");
            txtField37.setText(oTrans.getMaster(3).toString());
            txtSeeks39.setText((String) oTrans.getMaster(32));
            txtSeeks38.setText((String) oTrans.getMaster(1));
            txtField29.setText((String) oTrans.getMaster(1));
            txtField30.setText((String) oTrans.getMaster(32));
            txtField01.setText(oTrans.getMaster(17).toString());
            txtField02.setText(oTrans.getMaster(16).toString());
            txtField03.setText(oTrans.getMaster(15).toString());
            txtField04.setText(oTrans.getMaster(14).toString());
            txtField05.setText(oTrans.getMaster(13).toString());
            txtField06.setText(oTrans.getMaster(12).toString());
            
            computePeso(txtField01,lbl1000p,txtField07);
            computePeso(txtField02,lbl500p,txtField08);
            computePeso(txtField03,lbl200p,txtField09);
            computePeso(txtField04,lbl100p,txtField10);
            computePeso(txtField05,lbl50p,txtField11);
            computePeso(txtField06,lbl20p,txtField12);
            txtField13.setText(oTrans.getMaster(11).toString());
            txtField14.setText(oTrans.getMaster(10).toString());
            txtField15.setText(oTrans.getMaster(9).toString());
            txtField16.setText(oTrans.getMaster(8).toString());
            txtField17.setText(oTrans.getMaster(7).toString());
            txtField18.setText(oTrans.getMaster(6).toString());
            txtField19.setText(oTrans.getMaster(5).toString());
            txtField20.setText(oTrans.getMaster(4).toString());
            
            computeCoins(txtField13,lbl20px,txtField21);
            computeCoins(txtField14,lbl10px,txtField22);
            computeCoins(txtField15,lbl5px,txtField23);
            computeCoins(txtField16,lbl1px,txtField24);
            computeCoins(txtField17,lbl25cx,txtField25);
            computeCoins(txtField18,lbl10cx,txtField26);
            computeCoins(txtField19,lbl5cx,txtField27);
            computeCoins(txtField20,lbl1cx,txtField28);
            txtField31.setText(oTrans.getMaster(34).toString());
            txtField32.setText(oTrans.getMaster(33).toString());
            txtField33.setText(oTrans.getMaster(18).toString());
            txtField34.setText(oTrans.getMaster(19).toString());
            txtField35.setText(oTrans.getMaster(20).toString());
            txtField36.setText(oTrans.getMaster(21).toString());
            
            computeTotalPeso();
            computeTotalCoins();
            computeGrandTotal();
        } catch (SQLException e) {
            MsgBox.showOk(e.getMessage());
        }
    }
    private void computePeso(TextField txtQty, Label value, TextField txtTotal){
        double total = Integer.parseInt(txtQty.getText()) * Integer.parseInt(value.getText().replaceAll("₱ ", ""));
        txtTotal.setText("₱ " + CommonUtils.NumberFormat((Number)total, "###,###,##0.00"));
    }
    private void computeCoins(TextField txtQty, Label value, TextField txtTotal){
        double total = 0.0;
        if(value.getText().contains("₱")){
            total = Integer.parseInt(txtQty.getText()) * Integer.parseInt(value.getText().replaceAll("₱ ", ""));
        }else{
           double sub_total = Integer.parseInt(txtQty.getText()) * Integer.parseInt(value.getText().replaceAll("¢ ", ""));
          
           total = sub_total / 100;
        }
        txtTotal.setText("₱ " + CommonUtils.NumberFormat((Number)total, "###,###,##0.00"));
    }
    private void computeTotalPeso(){
        double subtotal = 0.0;
        try{
            double  p1000 = Double.valueOf(txtField07.getText().replaceAll("[₱, ]", ""));
            double  p500 = Double.valueOf(txtField08.getText().replaceAll("[₱, ]", ""));
            double  p200 = Double.valueOf(txtField09.getText().replaceAll("[₱, ]", ""));
            double  p100 = Double.valueOf(txtField10.getText().replaceAll("[₱, ]", ""));
            double  p50 = Double.valueOf(txtField11.getText().replaceAll("[₱, ]", ""));
            double  p20 = Double.valueOf(txtField12.getText().replaceAll("[₱, ]", ""));
            subtotal = p1000 + p500 + p200 + p100 + p50 + p20;
           
        lblPesoTotal.setText("₱ " + priceWithDecimal(subtotal));
        }catch(NumberFormatException e){
//            MsgBox.showOk(e.getMessage());
            e.printStackTrace();
        }
    }
    private void computeTotalCoins(){
        double subtotal = 0.0;
        try{
            double p20 = Double.valueOf(txtField21.getText().replaceAll("[₱, ]", ""));
            double p10 = Double.valueOf(txtField22.getText().replaceAll("[₱, ]", ""));
            double p5 = Double.valueOf(txtField23.getText().replaceAll("[₱, ]", ""));
            double p1 = Double.valueOf(txtField24.getText().replaceAll("[₱, ]", ""));
            double c25 = Double.valueOf(txtField25.getText().replaceAll("[₱, ]", ""));
            double c10 = Double.valueOf(txtField26.getText().replaceAll("[₱, ]", ""));
            double c5 = Double.valueOf(txtField27.getText().replaceAll("[₱, ]", ""));
            double  c1 = Double.valueOf(txtField28.getText().replaceAll("[₱, ]", ""));
            subtotal = p20 + p10 + p5 + p1 + c25 + c10 + c5 + c1;
           
        lblCoinTotal.setText("₱ " + priceWithDecimal(subtotal));
        }catch(NumberFormatException e){
//            MsgBox.showOk(e.getMessage());
            e.printStackTrace();
        }
    }
    private void computeGrandTotal(){
         double total = 0.0;
          try{
            double peso_total = Double.valueOf(lblPesoTotal.getText().replaceAll("[₱, ]", ""));
            double coin_total = Double.valueOf(lblCoinTotal.getText().replaceAll("[₱, ]", ""));
            total = peso_total + coin_total;
           
            lblGrandTotal.setText("₱ " + priceWithDecimal(total));
        }catch(NumberFormatException e){
//            MsgBox.showOk(e.getMessage());
            e.printStackTrace();
        }
    }
    public static String priceWithDecimal (Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(price);
    }
}
