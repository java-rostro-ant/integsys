/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gridergui;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.rmj.appdriver.GRider;
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
    private int pnRow = 0;
    private int pnSubItems = 0;
    private boolean pbLoaded = false;
    private String psOldRec;
    private String psBarcode = "";
    private String psDescript = "";
    private final String pxeDateFormat = "yyyy-MM-dd";
    private final String pxeDateDefault = java.time.LocalDate.now().toString();
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtSeeks05;
    @FXML
    private TextField txtField01,txtField02,txtField03,txtField04;
    @FXML
    private Button btnBrowse,btnNew,btnSave,btnUpdate,btnCancel,
            btnClose,btnActivate,btnDeactivate;
    @FXML
    private AnchorPane AnchorMainPanaloParameter;

    private Stage getStage(){
	return (Stage) txtField01.getScene().getWindow();
    }

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
        btnActivate.setOnAction(this::cmdButton_Click);
        btnDeactivate.setOnAction(this::cmdButton_Click);
//      text field focus
        txtField01.focusedProperty().addListener(txtField_Focus);
        txtField02.focusedProperty().addListener(txtField_Focus);
        txtField03.focusedProperty().addListener(txtField_Focus);
        txtField04.focusedProperty().addListener(txtField_Focus);
//      text field  key pressed
        txtSeeks05.setOnKeyPressed(this::txtField_KeyPressed);
//        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
//        txtField02.setOnKeyPressed(this::txtField_KeyPressed);
       
        
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
//                 case "btnBrowse":
//                        if (oTrans.SearchRecord(txtSeeks05.getText(), false)){
//                            loadRecord();
//                            pnEditMode = EditMode.READY;
//                        } else{
//                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
//                        }
//                    break;
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
                    
                    clearFields();
                    oTrans = new Raffle(oApp, oApp.getBranchCode(), false);
                    oTrans.setListener(oListener);
                    oTrans.setWithUI(true);
                    pnEditMode = EditMode.UNKNOWN;
                    //reload detail
                    break;
                case "btnActivate":
                    if (oTrans.ActivateRecord()){
                        ShowMessageFX.Warning(getStage(),"Panalo Parameter successfully activated.","Warning", null);
                        clearFields();
                    }else{
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                    }
                    break;
//                case "btnDeactivate":
//                    if (oTrans.DeactivateRecord()){
//                        ShowMessageFX.Warning(getStage(),"Panalo Parameter successfully deactivated.","Warning", null);
//                        clearFields();
//                    }else{
//                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
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
        } catch (SQLException e) {
            e.printStackTrace();
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }catch (NullPointerException e) {
            e.printStackTrace();
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }
    } 
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        
        btnCancel.setVisible(lbShow);
        btnSave.setVisible(lbShow);
        btnActivate.setVisible(!lbShow);
        btnDeactivate.setVisible(!lbShow);
        
        btnSave.setManaged(lbShow);
        btnCancel.setManaged(lbShow);
        btnUpdate.setVisible(!lbShow);
        btnBrowse.setVisible(!lbShow);
        btnNew.setVisible(!lbShow);
        
        txtSeeks05.setDisable(!lbShow);
        txtField01.setDisable(true);
        txtField02.setDisable(!lbShow);
        
       
        if (lbShow){
            txtSeeks05.setDisable(lbShow);
            txtSeeks05.clear();
            txtField02.requestFocus();
            btnCancel.setVisible(lbShow);
            btnSave.setVisible(lbShow);
            btnUpdate.setVisible(!lbShow);
            btnBrowse.setVisible(!lbShow);
            btnNew.setVisible(!lbShow);
            btnBrowse.setManaged(false);
            btnNew.setManaged(false);
            btnUpdate.setManaged(false);
            btnActivate.setManaged(false);
            btnDeactivate.setManaged(false);
        }
        else{
            txtSeeks05.setDisable(lbShow);
            txtSeeks05.requestFocus();
        }
    }
    public void clearFields(){
        txtField01.clear();
        txtField02.clear();
        txtField03.clear();
        txtField04.clear();

        txtSeeks05.clear();
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
            txtField02.setText(CommonUtils.xsDateLong((Date) oTrans.getMaster(2)));
            txtField03.setText((String) oTrans.getMaster(3));
            txtField04.setText((String) oTrans.getMaster(4));
            txtSeeks05.setText((String) oTrans.getMaster(1));
        } catch (SQLException e) {
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }
    }
    

    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
       
        String lsValue = txtField.getText();
        if (lsValue == null) return;
            
        if(!nv){ //Lost Focus
            try {
                switch (lnIndex){
                    case 3: //date
                       if (CommonUtils.isDate(txtField.getText(), pxeDateFormat)){
                        oTrans.setMaster(3, CommonUtils.toDate(txtField.getText()));
                       txtField.setText(CommonUtils.xsDateLong((Date)oTrans.getMaster(3)));
                    } else{
                        ShowMessageFX.Warning("Invalid date entry.", "", "Date format must be yyyy-MM-dd (e.g. 1991-07-07)");
                        oTrans.setMaster(3, CommonUtils.toDate(pxeDateDefault));
                        txtField.setText(CommonUtils.xsDateLong((Date)oTrans.getMaster(3)));
                    }
//                        oTrans.setMaster(lnIndex, lsValue);
//                        txtField03.setText(CommonUtils.xsDateLong((Date) oTrans.getMaster(3)));
                        break;
                    case 4: //remarks
                        oTrans.setMaster(lnIndex, lsValue);
                        break;
                }
            } catch (SQLException e) {
                ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
            }
            
        } else{ //Focus
            switch (lnIndex){
                case 3: /*date*/
                    try{
                        txtField.setText(CommonUtils.xsDateShort(lsValue));
                    }catch(ParseException e){
                        ShowMessageFX.Error(e.getMessage(), "", null);
                    }
                    txtField.selectAll();
                    break;
                    default:
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
              
                case 5: /*Search*/
                    if (oTrans.SearchRecord(txtSeeks05.getText(), false)){
                        loadRecord();
                        pnEditMode = EditMode.READY;
                    } else 
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
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

}
