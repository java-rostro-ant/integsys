/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gridergui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agent.MsgBox;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.LMasDetTrans;
import org.rmj.fund.manager.parameters.Raffle;


/**
 *
 * @author User
 */
public class RaffleParameterController implements Initializable , ScreenInterface{
    private GRider oApp;
    private Raffle oTrans;
    private LMasDetTrans oListener;
    
    private int pnIndex = -1;
    private int pnEditMode;
    private int pnRow = -1;
    private int pnSubItems = 0;
    private boolean pbLoaded = false;
    private String psOldRec;
    private String psBarcode = "";
    private String psDescript = "";
    private String oldTransNox = "";
    private String transNox = "";
    private final String pxeDateFormat = "yyyy-MM-dd HH:mm:ss";
    private final String pxeDateDefault = java.time.LocalDate.now().toString();
    
    private final String[] LblWinner = {"OFFICER","ASSOCIETE","CUSTOMER","SELLING BRANCH"};
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtSeeks99;
    @FXML
    private TextField txtField01,txtField02,txtField03,txtField04,txtField05,txtField06
            ,txtField07,txtField08;
    @FXML
    private Button btnBrowse,btnNew,btnSave,btnUpdate,btnCancel,
            btnClose,btnDelete,btnAdd;
    @FXML
    private ComboBox<String> cmbWinner;

    @FXML
    private TableView tblDetail;

    @FXML
    private TableColumn index00,index01,index02,index03,index04,index05;
    @FXML
    private AnchorPane AnchorMainPanaloParameter;

    private Stage getStage(){
	return (Stage) txtField01.getScene().getWindow();
    }
    private ObservableList<RaffleParmModel> WinnerData = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        oListener = new LMasDetTrans() {
            @Override
            public void MasterRetreive(int i, Object o) {
                switch (i){
                    case 1: //xEmployNm
                        txtField01.setText((String) o);
                        break;
                    case 2: 
                        txtField02.setText(CommonUtils.xsDateMedium((Date) o));
                        break;
                    case 3: 
                        txtField03.setText((String) o);
                        break;
                    case 4: 
                        txtField04.setText((String) o);
                        break;
                    case 6:
                         try {
                            lblStatus.setVisible(true);
                            if(oTrans.getMaster(6).toString().equalsIgnoreCase("1")){
                                lblStatus.setText("CLOSED");
                                lblStatus.setStyle("-fx-background-color: green;");
                            }else if(oTrans.getMaster(6).toString().equalsIgnoreCase("0")){
                                lblStatus.setText("OPEN");
                                lblStatus.setStyle("-fx-background-color: red;");
                            }else if(oTrans.getMaster(6).toString().equalsIgnoreCase("2")){
                                lblStatus.setText("POSTED");
                                lblStatus.setStyle("-fx-background-color: gray;");
                            }else{
                               lblStatus.setVisible(false); 
                            }
                        } catch (SQLException ex) {
                             ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                        }
                         break;
                }
            }

            @Override
            public void DetailRetreive(int i, int i1, Object o) {
                loadWinnerDetail();
            }
        };
        
        oTrans = new Raffle(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnNew.setOnAction(this::cmdButton_Click);
        btnSave.setOnAction(this::cmdButton_Click);
        btnUpdate.setOnAction(this::cmdButton_Click);
        btnCancel.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
//        btnAdd.setOnAction(this::cmdButton_Click);
//        btnDelete.setOnAction(this::cmdButton_Click);
//      text field focus
        txtField03.focusedProperty().addListener(txtField_Focus);
        txtField04.focusedProperty().addListener(txtField_Focus);
        txtField06.focusedProperty().addListener(txtField_Focus);
        txtField08.focusedProperty().addListener(txtField_Focus);
        
//      text field  key pressed
        txtSeeks99.setOnKeyPressed(this::txtField_KeyPressed);
        txtField03.setOnKeyPressed(this::txtField_KeyPressed);
        txtField04.setOnKeyPressed(this::txtField_KeyPressed);
        txtField05.setOnKeyPressed(this::txtField_KeyPressed);
        txtField06.setOnKeyPressed(this::txtField_KeyPressed);
        txtField07.setOnKeyPressed(this::txtField_KeyPressed);
        txtField08.setOnKeyPressed(this::txtField_KeyPressed);
        cmbWinner.getItems().addAll(LblWinner);
//        cmbWinner.setOnMousePressed(this::LblWinner_Click);
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
                        if (oTrans.SearchRecord(txtSeeks99.getText(), false)){
                            loadRecord();
                            pnEditMode = EditMode.READY;
                        } else{
                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                        }
                    break;
                case "btnNew": //create new transaction
                        pbLoaded = true;
                        if (oTrans.NewRecord()){
                            clearFields();
                            loadRecord();
                            pnEditMode = oTrans.getEditMode();
                        } else{
                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                        }
                    break;
                 case "btnSave":
                       
                             if (oTrans.SaveRecord()){
                                clearFields();
                                ShowMessageFX.Warning(getStage(), "Record Save Successfully.","Warning", null);
                                pnEditMode = EditMode.UNKNOWN;
                            } else{
                                ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                            }
                        
                       
                    break;
                case "btnUpdate":
                        if (oTrans.UpdateRecord()){
                            pnEditMode = oTrans.getEditMode();
                        } else 
                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                    break;
                case "btnCancel":
                    
                    
                    oTrans = new Raffle(oApp, oApp.getBranchCode(), false);
                    oTrans.setListener(oListener);
                    oTrans.setWithUI(true);
                    clearFields();
                    pnEditMode = EditMode.UNKNOWN;
                    //reload detail
                    break;
//                case "btnAdd":
//                    if (!oTrans.addDetail("", true)){
//                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
//                    }
//                    break;
//                case "btnDelete":
//                    if (!oTrans.delDescription(pnRow)){
//                       ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
//                    }
//                    break;

                case "btnClose":
                    if(ShowMessageFX.OkayCancel(null, "Panalo Parameter", "Are you sure, do you want to close?") == true){
                        unloadForm();
                        break;
                    } else
                        return;
            }
            
            initButton(pnEditMode);
        } catch (SQLException | NullPointerException  e) {
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }
    } 
//    private void LblWinner_Click(MouseEvent event) {
//        
//       int lnWinner = cmbWinner.getSelectionModel().getSelectedIndex();
//        System.out.println ("value click :" + (String.valueOf(lnWinner)));
//        try {
////            if(event.getClickCount()>0){
//            System.out.println("rowlbwinner : " + pnRow);
//        oTrans.setDetail(pnRow, "cWinnerxx", (String.valueOf(lnWinner)));
////            }
//        }catch (SQLException | NullPointerException e) {
//            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
//        }
//    
//    }
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        
        btnCancel.setVisible(lbShow);
        btnSave.setVisible(lbShow);
//        btnAdd.setVisible(lbShow);
//        btnDelete.setVisible(lbShow);
        
        btnSave.setManaged(lbShow);
        btnCancel.setManaged(lbShow);
        btnUpdate.setVisible(!lbShow);
        btnBrowse.setVisible(!lbShow);
        btnNew.setVisible(!lbShow);
        
        txtSeeks99.setDisable(!lbShow);
        txtField01.setDisable(true);
        txtField02.setDisable(!lbShow);
        txtField03.setDisable(!lbShow);
        txtField04.setDisable(!lbShow);
        txtField05.setDisable(true);
        txtField06.setDisable(true);
        txtField07.setDisable(true);
        txtField08.setDisable(true);
        
       
        if (lbShow){
            txtSeeks99.setDisable(lbShow);
            txtSeeks99.clear();
            txtField03.requestFocus();
            btnCancel.setVisible(lbShow);
            btnSave.setVisible(lbShow);
            btnUpdate.setVisible(!lbShow);
            btnBrowse.setVisible(!lbShow);
            btnNew.setVisible(!lbShow);
            btnBrowse.setManaged(false);
            btnNew.setManaged(false);
            btnUpdate.setManaged(false);
//            btnAdd.setManaged(lbShow);
//            btnDelete.setManaged(lbShow);
        }
        else{
            txtSeeks99.setDisable(lbShow);
            txtSeeks99.requestFocus();
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
        cmbWinner.getSelectionModel().clearSelection();
        loadWinnerDetail();
        

        txtSeeks99.clear();
        lblStatus.setVisible(false);
        
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
        SimpleDateFormat date = new SimpleDateFormat(pxeDateFormat);
        SimpleDateFormat date1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if(oTrans.getMaster(5).toString().equalsIgnoreCase("1")){
            lblStatus.setVisible(true);
                lblStatus.setText("CLOSED");
                lblStatus.setStyle("-fx-background-color: green;");
            }else if(oTrans.getMaster(5).toString().equalsIgnoreCase("0")){
                lblStatus.setVisible(true);
                lblStatus.setText("OPEN");
                lblStatus.setStyle("-fx-background-color: red;");
            }else if(oTrans.getMaster(5).toString().equalsIgnoreCase("2")){
                lblStatus.setVisible(true);
                lblStatus.setText("POSTED");
                lblStatus.setStyle("-fx-background-color: GREEN;");
            }else{
                lblStatus.setVisible(false);
            }
            txtField01.setText((String) oTrans.getMaster(1));
            Date dateTime1 =  date1.parse(oTrans.getMaster(2).toString());
            txtField02.setText(CommonUtils.dateFormat(dateTime1, "MMMM dd, yyyy"));
            txtField04.setText((String) oTrans.getMaster(4));
            txtField03.setText((String) oTrans.getMaster(3));
            txtSeeks99.setText((String) oTrans.getMaster(1));
            System.out.println(oTrans.getMaster(3));
            if (CommonUtils.isDate(txtField03.getText(), pxeDateFormat)){
                Date dateTime =  date.parse(oTrans.getMaster(3).toString());
                txtField03.setText(CommonUtils.dateFormat(dateTime, "MMMM dd, yyyy HH:mm:ss"));
            }
            //Loading Details
             loadWinnerDetail();
             
        } catch (ParseException | SQLException e) {
                ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }
        
       
    }
    private void loadWinnerDetail(){
        int lnCtr;
        
        try {
            WinnerData.clear();
            oldTransNox = transNox;
//            if (oTrans.NewRecord()){//true if by barcode; false if by description
                for (lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
                    WinnerData.add(new RaffleParmModel(String.valueOf(lnCtr),
                            (String) oTrans.getDetail(lnCtr, "sTransNox"),
                            (String) oTrans.getDetail(lnCtr, "cWinnerxx"),
                            (String) oTrans.getDetail(lnCtr, "sPanaloDs"),
                            "₱ " + oTrans.getDetail(lnCtr, "nAmountxx").toString(),
                            (String) oTrans.getDetail(lnCtr, "sDescript"),
                            oTrans.getDetail(lnCtr, "nItemQtyx").toString() 
                    )); 
                }
                initGrid();
                
//            }  else{
//                MsgBox.showOk(oTrans.getMessage());
//            }  
        } catch (SQLException ex) {
            System.out.println("SQLException" + ex.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("NullPointerException loadOrderDetail "  + ex.getMessage());
        } catch (DateTimeException ex) {
//            MsgBox.showOk(ex.getMessage());
            System.out.println("DateTimeException" + ex.getMessage());
        } 
    }
    private void initGrid() {
    index00.setStyle("-fx-alignment: CENTER;");
    index01.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
    index02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
    index03.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 0 0 5;");
    index04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
    index05.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
    index00.setCellValueFactory(new PropertyValueFactory<>("DetIndex01"));
    index01.setCellValueFactory(new PropertyValueFactory<>("DetIndex03"));
    index02.setCellValueFactory(new PropertyValueFactory<>("DetIndex04"));
    index03.setCellValueFactory(new PropertyValueFactory<>("DetIndex05"));
    index04.setCellValueFactory(new PropertyValueFactory<>("DetIndex06"));
    index05.setCellValueFactory(new PropertyValueFactory<>("DetIndex07"));

    tblDetail.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
                TableHeaderRow header = (TableHeaderRow) tblDetail.lookup("TableHeaderRow");
                header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    header.setReordering(false);
                });
            });

            tblDetail.setItems(WinnerData);
            if (!tblDetail.getItems().isEmpty()){
                tblDetail.getSelectionModel().select(pnRow -1);
            }
}
    
    @FXML
private void tblDetail_Clicked(MouseEvent event) {
        pnRow = tblDetail.getSelectionModel().getSelectedIndex() + 1;

        if(event.getClickCount()>0){

            if(!tblDetail.getItems().isEmpty()){
                txtField05.setDisable(false);
                txtField06.setDisable(false);
                txtField07.setDisable(false);
                txtField08.setDisable(false);
            getSelectedRow();
          }
      }
       
}
    private void getSelectedRow() {
     try {                                    
            txtField05.setText((String) oTrans.getDetail(pnRow , "sPanaloDs"));
            txtField06.setText("₱ " + oTrans.getDetail(pnRow , "nAmountxx").toString());
            txtField07.setText((String) oTrans.getDetail(pnRow , "sDescript"));
            txtField08.setText( oTrans.getDetail(pnRow , "nItemQtyx").toString());
            cmbWinner.getSelectionModel().select(Integer.parseInt((String) oTrans.getDetail(pnRow , "cWinnerxx")));
//            txtField041.requestFocus();
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }

} 

    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        SimpleDateFormat date = new SimpleDateFormat(pxeDateFormat);
        String lsValue = txtField.getText();
        if (lsValue == null) return;
            
        if(!nv){ //Lost Focus
            try {
                switch (lnIndex){
                    case 3: //date
                       if (CommonUtils.isDate(txtField.getText(), pxeDateFormat)){
                        oTrans.setMaster(3, txtField.getText());
                        Date dateTime =  date.parse(oTrans.getMaster(3).toString());
                       txtField.setText(CommonUtils.dateFormat(dateTime, "MMMM dd, yyyy HH:mm:ss"));
                    } else{
                        ShowMessageFX.Warning("Invalid date entry.", "", "Date format must be yyyy-MM-dd HH:mm:ss (e.g. 1991-07-07 08:30:00)");
                        oTrans.setMaster(3, CommonUtils.toDate(pxeDateDefault));
                        txtField.setText(CommonUtils.dateFormat(oTrans.getMaster(3),"MMMM dd, yyyy HH:mm:ss"));
                    }
//                        oTrans.setMaster(lnIndex, lsValue);
//                        txtField03.setText(CommonUtils.xsDateLong((Date) oTrans.getMaster(3)));
                        break;
                    case 4://remarks
                        oTrans.setMaster(lnIndex, lsValue);
                        break;
                        
                    case  6: //amount
                        if (oTrans.getDetail(pnRow, "sItemIDxx").equals("")){
                            if (lsValue.contains("₱")){
                               if (isNumeric(lsValue.substring(1).trim(), "Double")){
                                
                                oTrans.setDetail(pnRow, "nItemQtyx", "1");
                                oTrans.setDetail(pnRow ,"nAmountxx", lsValue.substring(1).trim());
                                txtField.setText("₱ " + CommonUtils.NumberFormat((Double) oTrans.getDetail(pnRow, "nAmountxx"), "###0.00"));
                                
                               }else{
                                ShowMessageFX.Warning("Invalid Amount Value.", "", "IT SHOULD BE NUMBER ONLY! EXCEPTION `₱` THANK YOU");
                                oTrans.setDetail(pnRow ,"nAmountxx", "0");
                                txtField.setText("₱ " + "0.00");
                                }
                            }else 
                                if (isNumeric(lsValue.trim(), "Double")){
                                oTrans.setDetail(pnRow ,"nAmountxx", lsValue.trim());
                                oTrans.setDetail(pnRow, "nItemQtyx", "1");
                                txtField.setText("₱ " + CommonUtils.NumberFormat((Double) oTrans.getDetail(pnRow, "nAmountxx"), "###0.00"));
                                }else{
                                ShowMessageFX.Warning("Invalid Amount Value.", "", "IT SHOULD BE NUMBER ONLY! EXCEPTION `₱` THANK YOU");
                                oTrans.setDetail(pnRow ,"nAmountxx", "0");
                                txtField.setText("₱ " + "0.00");
                                }
                        }
                        getSelectedRow();
                                
                        break;
                    case  8: //qty
//                        if (Double.parseDouble(oTrans.getDetail(pnRow, "nAmountxx").toString()) != 0.0 ){
                        if (Integer.parseInt(lsValue) > 0){
                            if (!oTrans.getDetail(pnRow, "sItemIDxx").equals("")){
                                if (isNumeric(lsValue, "Int")){
                                    oTrans.setDetail(pnRow ,"nItemQtyx", lsValue);
                                }else{
                                    ShowMessageFX.Warning("Invalid Quantity Value.", "", "IT SHOULD BE NUMBER ONLY!");
                                    oTrans.setDetail(pnRow ,"nItemQtyx", "1");
                                    txtField.setText("1");
                                    txtField.requestFocus();
                                            }
                            }else {
                            ShowMessageFX.Warning("ONLY ITEM REWARD CAN BE MODIFIED.", "RAFFY QTY FIELD", "MODIFICATION OF ITEM QTY WITH AMOUNT IS NOT ALLOWED");
                               txtField.setText("1");
                            }
                            }     
                        
                        break;
                                        
                }
            } catch (ParseException | SQLException e) {
                ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
            }
            
        } else{ //Focus
            try {
            switch (lnIndex){
                case 3: /*date*/
//                    if (CommonUtils.isDate((String) oTrans.getMaster(3), pxeDateFormat)){
                    Date dateTime =  date.parse(oTrans.getMaster(3).toString());
                    txtField.setText(CommonUtils.dateFormat(dateTime, pxeDateFormat));
//                    }
                    txtField.selectAll();
                    
                    break;
                    
                case 6:
                    
                    if (!oTrans.getDetail(pnRow, "sItemIDxx").equals("")){
                        txtField07.requestFocus();
                        if(ShowMessageFX.OkayCancel("ITEM WILL BE CLEAR!!", "Raffle Amount Field", "Are you sure, You want to fill-up Amount?") == true){
                            oTrans.setDetail(pnRow, "sItemIDxx", "");
                            oTrans.setDetail(pnRow, "sDescript", "");
                            oTrans.setDetail(pnRow, "nItemQtyx", "0");
                            
                            getSelectedRow();
                            txtField07.requestFocus();
                            txtField06.requestFocus();
                        }else {
                            txtField07.requestFocus();
                        }
                        
                    }
                    
                    break;
                    default:
                        
            }
            }catch (ParseException | SQLException e) {
                ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
            }
            
            pnIndex = lnIndex;
            txtField.selectAll();
        }
    };   
    private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
             
        try{
           switch (event.getCode()){
            case F3:
                switch (lnIndex){
              
                case 99: /*Search*/
                    if (oTrans.SearchRecord(txtSeeks99.getText(), false)){
                        loadRecord();
                        pnEditMode = EditMode.READY;
                    } else 
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                     break;
                case 5: /*Search*/
                    if (oTrans.searchPanalo(pnRow,txtField05.getText(), false )){
                        txtField05.setText((String) oTrans.getDetail(pnRow, "sPanaloDs"));
                        
                        
                    } else 
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                     break;
                case 7: /*Search*/
                    if (Double.parseDouble(oTrans.getDetail(pnRow, "nAmountxx").toString()) <= 0.0){
                        
                        if (oTrans.searchItems(pnRow,txtField07.getText(), false)){
                        txtField07.setText((String) oTrans.getDetail(pnRow, "sDescript"));
                        }else 
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                        
                    }else {
                        ShowMessageFX.Warning("Notice Search Item Not Allowed", "Search Item", "Amount already has Value!");
                    }
                     break;
                }   
            case ENTER:
            case DOWN:
                CommonUtils.SetNextFocus(txtField); break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        } 
        }catch(SQLException e){
                ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }
        
    }
public static boolean isNumeric(String strNum ,String Parseby) {
    if (strNum == null) {
        return false;
    }
    try {
        switch (Parseby){
            case "Double":
            double d = Double.parseDouble(strNum);
            break;
            case "Int":
            int i = Integer.parseInt(strNum);
            break;
            case "Float":
            float f = Float.parseFloat(strNum);
            break;
            case "Long":
            long l = Long.parseLong(strNum);
            break;
        }
        
    } catch (NumberFormatException nfe) {
        return false;
    }
    return true;
}
}
