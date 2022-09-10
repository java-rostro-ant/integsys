/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gridergui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import static gridergui.IncentiveHistoryController.priceWithDecimal;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.DeptIncentive;
import org.rmj.fund.manager.base.LMasDetTrans;

/**
 * FXML Controller class
 *
 * @author user
 */
public class DeptIncentivesHistController implements Initializable, ScreenInterface {
    private final String pxeModuleName = "Department Incentives History";
    private double xOffset = 0;
    private double yOffset = 0;
    
    private GRider oApp;
    private DeptIncentive oTrans;
    private LMasDetTrans oListener;
    
    private String oTransnox = "";
    private boolean state = false;
    
   
    private int pnRow = -1;
    private int pnEmp = 0;
    private int oldPnRow = -1;
    private int pnEditMode;


    private boolean pbLoaded = false;
    
    private ObservableList<DeptIncentivesModel> deptincdata = FXCollections.observableArrayList();

    @FXML
    private AnchorPane AnchorMain,searchBar;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnBrowse,btnClose;
    @FXML
    private TextField txtSearch01;
    @FXML
    private TextField txtField01,txtField02,txtField03,txtField04,txtField05;
    @FXML
    private TextArea txtField06;
    @FXML
    private Label lblStatus,lblTotal;
    @FXML
    private TextField txtField011,txtField021,txtField031,txtField041,txtField051;
    @FXML
    private TableView tblemployee;
    @FXML
    private TableColumn index01,index02,index03,index04,index05,index06,index07;

    
    public void setTransaction(String fsValue){
        oTransnox = fsValue;
    }
    public void setState(boolean fsValue){
        state = fsValue;
    }
    private Stage getStage(){
	return (Stage) txtField01.getScene().getWindow();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {  
      
        oListener = new LMasDetTrans() {
            @Override
            public void MasterRetreive(int fnIndex, Object foValue) {
                
            }
            @Override
            public void DetailRetreive(int fnRow, int fnIndex, Object foValue) {
               
            }
         };
        
  
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        

        
        //initialize class
        oTrans  = new DeptIncentive(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setTranStat(1023);
       
        oTrans.setWithUI(true);
        
        /*Add listener to text fields*/
     

//        txtField06.focusedProperty().addListener(txtArea_Focus);
        txtSearch01.setOnKeyPressed(this::txtField_KeyPressed);
        initButton(EditMode.UNKNOWN);
        pbLoaded = true;
        txtField05.setEditable(false);
        
        
    } 
        
    
    private void txtArea_KeyPressed(KeyEvent event){
        if (event.getCode() == ENTER || event.getCode() == DOWN){ 
            event.consume();
            CommonUtils.SetNextFocus((TextArea)event.getSource());
        }else if (event.getCode() ==KeyCode.UP){
        event.consume();
            CommonUtils.SetPreviousFocus((TextArea)event.getSource());
        }
    }
    private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();
        int lnIndex = Integer.parseInt(((TextField)event.getSource()).getId().substring(9,11));
        String lsValue = txtField.getText();
        
       try {
            switch (event.getCode()) {
                case F3:
                case ENTER:
 
                    switch (lnIndex){
                        case 1: /*search department*/
                        if (oTrans.SearchTransaction(txtSearch01.getText(), true)){
                            loadMaster();
                            loadDetail();
                            getSelectedEmployee();
                        } else {
                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                        }
                        break;
                    }
                break;
            }
            } catch (SQLException e) {
            e.printStackTrace();
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }
               

        
        switch (event.getCode()){
        case ENTER:
        case DOWN:
            CommonUtils.SetNextFocus(txtField);
            break;
        case UP:
            CommonUtils.SetPreviousFocus(txtField);
        }
    } 
    
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
      
//        btnClose.setVisible(!lbShow);
//        btnBrowse.setVisible(!lbShow);
    }
    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }    
    private void loadMaster() throws SQLException {

        txtField01.setText((String) oTrans.getMaster("sTransNox"));
        txtSearch01.setText((String) oTrans.getMaster("sTransNox"));
       
        lblStatus.setVisible(true);
        if(oTrans.getMaster(7).toString().equalsIgnoreCase("0")){
            lblStatus.setText("OPEN");
        }else if(oTrans.getMaster(7).toString().equalsIgnoreCase("1")){
            lblStatus.setText("CLOSED");
        }
        else if(oTrans.getMaster(7).toString().equalsIgnoreCase("2")){
            lblStatus.setText("POSTED");
        }
        else if(oTrans.getMaster(7).toString().equalsIgnoreCase("3")){
            lblStatus.setText("CANCELLED");
        }else{
            lblStatus.setVisible(false);
        }
        txtField02.setText(dateToWord(oTrans.getMaster("dTransact").toString()));
        txtField03.setText((String) oTrans.getMaster("xDeptName"));
        txtField04.setText((String) oTrans.getMaster("xInctvNme"));
        txtField05.setText(dateToWord(oTrans.getMaster("dEffctive").toString()));
        txtField06.setText((String) oTrans.getMaster("sRemarksx"));

//        loadDetail();
        pnRow = 0;
        pnEmp = 0;
    }

    public static String dateToWord (String dtransact) {
       
        SimpleDateFormat dateParser1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
//            Date date = dateParser.parse("2022-04-29 16:59:13");
//            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
//             String str_date="2012-08-11+05:30";
//           
            Date date = new Date();
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            date = (Date)formatter.parse(dtransact);  
            SimpleDateFormat fmt = new SimpleDateFormat("MMM dd, yyyy");
            String todayStr = fmt.format(date);
            
            return todayStr;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void loadDetail(){
        try {
            deptincdata.clear();
            double lnTotal = 0;
            for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
                deptincdata.add(new DeptIncentivesModel(String.valueOf(lnCtr),
                    oTrans.getDetail(lnCtr, "sEmployID").toString(),
                    oTrans.getDetail(lnCtr, "xEmployNm").toString(),
                    oTrans.getDetail(lnCtr, "xPositnNm").toString(),
                    oTrans.getDetail(lnCtr, "dLastUpdt").toString(),
                    oTrans.getDetail(lnCtr, "sRemarksx").toString(),
                    oTrans.getDetail(lnCtr, "xBankName").toString(),
                    oTrans.getDetail(lnCtr, "xBankAcct").toString(),
                    priceWithDecimal(Double.valueOf(oTrans.getDetail(lnCtr, "sOldAmtxx").toString())),
                    priceWithDecimal(Double.valueOf(oTrans.getDetail(lnCtr, "sNewAmtxx").toString()))));
                lnTotal = lnTotal + Double.parseDouble(oTrans.getDetail(lnCtr, "sNewAmtxx").toString());
            }        
            lblTotal.setText((CommonUtils.NumberFormat((Number)lnTotal, "₱ " +"#,##0.00")));  
            initGrid();
        } catch (SQLException e) {
            e.printStackTrace();
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }
    }
    public static String priceWithDecimal (Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(price);
    }
    private void unloadForm(){
        StackPane myBox = (StackPane) AnchorMain.getParent();
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
                
                    case "btnBrowse":
                        if (oTrans.SearchTransaction(txtSearch01.getText(), true)){
                            loadMaster();
                            loadDetail();
                            getSelectedEmployee();
                        } else {
                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                        }
                        break;

               
                        case "btnClose":
                            if(ShowMessageFX.OkayCancel(null, pxeModuleName, "Are you sure, do you want to close?") == true){
                                if(state){
                                    onsuccessUpdate();
                                }else{ 
                                    unloadForm();
                                }
                                break;
                            } 
                            return;
              
                }
            initButton(pnEditMode);
        } catch (SQLException e) {
                    e.printStackTrace();
                    ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
                } 
    }
 
 
        
    private void initGrid() {
        index01.setStyle("-fx-alignment: CENTER;");
        index02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        index03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        index04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        index05.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 0;");
        index06.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 0;");
        index07.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 0;");

        
        index01.setCellValueFactory(new PropertyValueFactory<>("DetIndex01"));
        index02.setCellValueFactory(new PropertyValueFactory<>("DetIndex03"));
        index03.setCellValueFactory(new PropertyValueFactory<>("DetIndex04"));
        index04.setCellValueFactory(new PropertyValueFactory<>("DetIndex07"));
        index05.setCellValueFactory(new PropertyValueFactory<>("DetIndex08"));
        index06.setCellValueFactory(new PropertyValueFactory<>("DetIndex09"));
        index07.setCellValueFactory(new PropertyValueFactory<>("DetIndex10"));

        tblemployee.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblemployee.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        tblemployee.setItems(deptincdata);
        tblemployee.getSelectionModel().select(pnEmp);

    }
    
     private void getSelectedEmployee() {
         if(!deptincdata.isEmpty()){
     txtField011.setText(deptincdata.get(pnEmp).getDetIndex03());
    txtField021.setText(deptincdata.get(pnEmp).getDetIndex04());
    txtField031.setText(deptincdata.get(pnEmp).getDetIndex07());
    txtField041.setText(deptincdata.get(pnEmp).getDetIndex08());
    txtField051.setText(deptincdata.get(pnEmp).getDetIndex06());
        oldPnRow = pnEmp;
    
    }
}
    @FXML
    private void tblemployee_Clicked(MouseEvent event) {
        pnEmp = tblemployee.getSelectionModel().getSelectedIndex();
        getSelectedEmployee();
    }
   
    
   
        
     

    private void onsuccessUpdate(){
        StackPane myBox = (StackPane) AnchorMain.getParent();
        myBox.getChildren().clear();
        myBox.getChildren().add(setScene());
    }
    private AnchorPane setScene(){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("IncentiveConfirmation.fxml"));

            IncentiveConfirmationController loControl = new IncentiveConfirmationController();
            loControl.setGRider(oApp);
            loControl.setTransaction(oTransnox);
            fxmlLoader.setController(loControl);
            
            //load the main interface
                
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
}       