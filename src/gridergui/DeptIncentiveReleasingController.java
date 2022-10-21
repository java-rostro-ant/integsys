/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gridergui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import org.rmj.fund.manager.base.DeptIncentive;
import org.rmj.fund.manager.base.DeptIncentiveRelease;
import org.rmj.fund.manager.base.LMasDetTrans;
import org.rmj.fund.manager.parameters.IncentiveBankInfo;

/**
 * FXML Controller class
 *
 * @author User
 */
public class DeptIncentiveReleasingController implements Initializable, ScreenInterface {
    private final String pxeModuleName = "Department Incentives";
    private double xOffset = 0;
    private double yOffset = 0;
    private double lnTotal = 0;
    
    private GRider oApp;
    private DeptIncentiveRelease oTrans;
    private IncentiveBankInfo trans;
   
    private LMasDetTrans oListener;
    
    private int pnEditMode;
    private int pnRow = 0;
    private boolean pbLoaded = false;
    @FXML
    private TextField txtField01;
    @FXML
    private TextField txtField02;
    @FXML
    private TextField txtSeeks05;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnRelease;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnApproved;
    @FXML
    private Button btnDisapproved;
    @FXML
    private Button btnClose;
    @FXML
    private HBox hbButtons;
    @FXML
    private AnchorPane AnchorMainIncentiveRelease;
//    EMPLOYEE TABLE
    @FXML
    private TableView tblemployee;
    
    @FXML
    private TableColumn empIndex01;
    @FXML
    private TableColumn empIndex02;
    @FXML
    private TableColumn empIndex03;
    @FXML
    private TableColumn empIndex04;
    @FXML
    private TableColumn empIndex05;
    @FXML
    private TableColumn empIndex06;
    @FXML
    private TableColumn empIndex07;
//    INCENTIVES TABLE
    @FXML
    private TableView tblincetives;
    @FXML
    private TableColumn incIndex01;
    @FXML
    private TableColumn incIndex02;
    @FXML
    private TableColumn incIndex03;
    @FXML
    private TableColumn incIndex04;
    @FXML
    private TableColumn incIndex05;
    @FXML
    private TableColumn incIndex06;
    @FXML
    private TableColumn incIndex07;
    
    private final ObservableList<Release> data = FXCollections.observableArrayList();
    private final ObservableList<Release> emp_data = FXCollections.observableArrayList();
    private Stage getStage(){
	return (Stage) txtField01.getScene().getWindow();
    }
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        oListener = new LMasDetTrans() {
            @Override
            public void MasterRetreive(int i, Object o) {
//                loadRecord();
                switch (i){
                    case 3: //sBackAcct
                        txtField01.setText((String) o);
                        break;
                    case 7: //xEmployNm
                        txtField02.setText((String) o);
                        break;
                    
                }
            }

            @Override
            public void DetailRetreive(int i, int i1, Object o) {
                System.out.println((String) o);
            }
        };
        
        oTrans = new DeptIncentiveRelease(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setTranStat(102);
        oTrans.setWithUI(true);
        btnNew.setOnAction(this::cmdButton_Click);
        btnSave.setOnAction(this::cmdButton_Click);
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnCancel.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        btnApproved.setOnAction(this::cmdButton_Click);
        btnDisapproved.setOnAction(this::cmdButton_Click);
        btnRelease.setOnAction(this::cmdButton_Click);
        txtSeeks05.setOnKeyPressed(this::txtField_KeyPressed);
        
        pnEditMode = EditMode.UNKNOWN;
        
        txtField01.setDisable(true);
        txtField02.setDisable(true);
        initButton(pnEditMode);
    }    
    
    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
    private void loadRecord() {
        try { 
            txtField01.setText((String)oTrans.getMaster("sTransNox"));
            txtField02.setText(CommonUtils.xsDateMedium((Date) oTrans.getMaster("dTransact")));
            data.clear();
            for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
                
                    System.out.println(oTrans.getDeptMaster(lnCtr,"sTransNox"));
                    CheckBox select = new CheckBox();
                    int post = lnCtr -1;
                    if(oTrans.getTag(post)){
                        select.setSelected(true);
                        select.setDisable(true);
                    }
                    Date  date = new SimpleDateFormat("yyyy-MM-dd").parse(oTrans.getDeptMaster(lnCtr,"dTransact").toString());  
                    SimpleDateFormat formatter =  new SimpleDateFormat("MMMMM dd, yyyy");
                    String dTransact = formatter.format(date);
                    Date  date1 = new SimpleDateFormat("yyyy-MM-dd").parse(oTrans.getDeptMaster(lnCtr,"dEffctive").toString());  
                    SimpleDateFormat month_formatter =  new SimpleDateFormat("MMMMM yyyy");
                    String sMonth = month_formatter.format(date1);
                    data.add(new Release(oTrans.getDeptMaster(lnCtr,"sTransNox").toString(),
                    oTrans.getDeptMaster(lnCtr,"xBranchNm").toString(),
                    oTrans.getDeptMaster(lnCtr,"xDeptName").toString(),
                    sMonth,
                    dTransact,
                    oTrans.getDeptMaster(lnCtr,"sRemarksx").toString(),
                    select,
                    oTrans.getTag(post)));
                    select.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                        try {
                            if(newValue){
                                oTrans.setTag(post, true);
                            }else{
                                oTrans.setTag(post, false);
                            }
                        } catch (SQLException ex) {
                            ShowMessageFX.Warning(getStage(),ex.getMessage(), "Warning", null);
                        }
                    });
//                    
                                
                }
            initGrid();
//             | ParseException
        } catch (SQLException e) {
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        } catch (ParseException ex) {
            Logger.getLogger(DeptIncentiveReleasingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        btnBrowse.setVisible(!lbShow);
        btnApproved.setVisible(!lbShow);
        btnRelease.setVisible(!lbShow);
        btnDisapproved.setVisible(!lbShow);
        btnNew.setVisible(!lbShow);
        
        btnCancel.setVisible(lbShow);
        btnSave.setVisible(lbShow);
        btnSave.setManaged(lbShow);
        
        btnBrowse.setManaged(!lbShow);
        btnApproved.setManaged(!lbShow);
        btnDisapproved.setManaged(!lbShow);
        btnNew.setManaged(!lbShow);
        if (lbShow){
            btnSave.setVisible(lbShow);
            btnBrowse.setVisible(!lbShow);  
            btnNew.setVisible(!lbShow);
            btnRelease.setVisible(!lbShow);
            btnApproved.setVisible(!lbShow);
            btnDisapproved.setVisible(!lbShow);
            btnNew.setManaged(!lbShow);
            btnBrowse.setManaged(!lbShow);
            btnRelease.setManaged(!lbShow);
            btnDisapproved.setManaged(!lbShow);
            btnSave.setManaged(lbShow);
            btnCancel.setManaged(lbShow);
        }else{
            txtSeeks05.requestFocus();
        }
       
    }
    public void initGrid() {
        incIndex01.setCellValueFactory(new PropertyValueFactory<Release,String>("incIndex01"));
        incIndex02.setCellValueFactory(new PropertyValueFactory<Release,String>("incIndex02"));
        incIndex03.setCellValueFactory(new PropertyValueFactory<Release,String>("incIndex03"));
        incIndex04.setCellValueFactory(new PropertyValueFactory<Release,String>("incIndex04"));
        incIndex05.setCellValueFactory(new PropertyValueFactory<Release,String>("incIndex05"));
        incIndex06.setCellValueFactory(new PropertyValueFactory<Release,String>("incIndex06"));
        incIndex07.setCellValueFactory(new PropertyValueFactory<Release,CheckBox>("incIndex07"));
        
         /*making column's position uninterchangebale*/
        tblincetives.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblincetives.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });

        /*Assigning data to table*/
        
        tblincetives.setItems(data);
        tblincetives.autosize();
        
    }
    public void initEmployeeGrid() {
        empIndex01.setCellValueFactory(new PropertyValueFactory<Release,String>("empIndex01"));
        empIndex02.setCellValueFactory(new PropertyValueFactory<Release,String>("empIndex02"));
        empIndex03.setCellValueFactory(new PropertyValueFactory<Release,String>("empIndex03"));
        empIndex04.setCellValueFactory(new PropertyValueFactory<Release,String>("empIndex04"));
        empIndex05.setCellValueFactory(new PropertyValueFactory<Release,String>("empIndex05"));
        empIndex06.setCellValueFactory(new PropertyValueFactory<Release,String>("empIndex06"));
        empIndex07.setCellValueFactory(new PropertyValueFactory<Release,CheckBox>("empIndex07"));
         /*making column's position uninterchangebale*/
        tblemployee.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblemployee.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
            header.prefWidthProperty().bind(tblemployee.widthProperty().divide(7));
        });
        /*Assigning data to table*/
        
        tblemployee.setItems(emp_data);
        tblemployee.autosize();
        
    }
    private void unloadForm(){
        StackPane myBox = (StackPane) AnchorMainIncentiveRelease.getParent();
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
                case "btnBrowse": //browse transaction
                    pbLoaded = true;
                     if (oTrans.SearchTransaction(txtSeeks05.getText(), true)){
                         loadRecord();
                         txtSeeks05.setText((String)oTrans.getMaster("sTransNox"));
                         pnEditMode = oTrans.getEditMode();
                     } else 
                         ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                    break;
                case "btnNew": //create new transaction
                    pbLoaded = true;
                    if (oTrans.NewTransaction()){
                        emp_data.clear();
                        txtSeeks05.clear();
                        loadRecord();
                        pnEditMode = oTrans.getEditMode();
                    } else 
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                    break;
                case "btnRelease": //release transaction
                  if (oTrans.ReleaseTransaction()){
                        ShowMessageFX.Warning(getStage(), "Transaction releasing success!","Warning", null);
                        clearFields();
                        pnEditMode = oTrans.getEditMode();
                    } else 
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                    break;
                 case "btnSave": //save transaction
                    if (oTrans.SaveTransaction()){
                        ShowMessageFX.Warning(getStage(), "Transaction Successfully Saved.","Warning", null);
                        clearFields();
                    } else {
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                    }  
                    break;    
                case "btnApproved": //approve transaction
                    if(!oTrans.getMaster("sTransNox").toString().trim().isEmpty()){
                        if(ShowMessageFX.OkayCancel(null, "Aprroved Incentive Releasing", "Are you sure, do you want to approve this transaction?") == true){
                           if (oTrans.ConfirmTransaction()){
                               ShowMessageFX.Warning(getStage(), "Transaction successfully approved.","Warning", null);
                               clearFields();
                               pnEditMode = oTrans.getEditMode();
                           } else 
                               ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                        } 
                    }
                    break;
                case "btnDisapproved": //disapprove transaction
                    if(!oTrans.getMaster("sTransNox").toString().trim().isEmpty()){
                        if(ShowMessageFX.OkayCancel(null, "Cancel Incentive Releasing", "Are you sure, do you want to cancel this transaction?") == true){
                          if (oTrans.CancelTransaction()){
                               ShowMessageFX.Warning(getStage(),"Transaction successfully disapproved.", "Warning", null);
                               clearFields();
                               pnEditMode = oTrans.getEditMode();
                           } else 
                               ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                        }
                    }
                    break;
                case "btnCancel": //cancel transaction
                    clearFields();
                    //reload detail
                    break;
               
                case "btnClose": //close releasing form
                    if(ShowMessageFX.OkayCancel(null, "Incentive Releasing", "Are you sure, do you want to close?") == true){
                        unloadForm();
                        break;
                    } else
                        return;
            }
            
            initButton(pnEditMode);
        } catch (SQLException e) {
            e.printStackTrace();
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }
    } 
    
    @FXML
    private void tblIncentivesDetail_Click(MouseEvent event) {
       
        try {
            pnRow = tblincetives.getSelectionModel().getSelectedIndex();
            String lsBankName;
            String lsBankAcct;
            emp_data.clear();
          
            if(oTrans.OpenDeptTransaction(oTrans.getDeptMaster(pnRow + 1, "sTransNox").toString())){
                for(int lnCtr  = 1; lnCtr  <= oTrans.getDetailItemCount(); lnCtr ++){       
                    emp_data.add(new Release(String.valueOf(lnCtr),
                        (String)oTrans.getDeptDetail(lnCtr , "xEmployNm"),
                        (String)oTrans.getDeptDetail(lnCtr , "xPositnNm"),
                        (String)oTrans.getDeptDetail(lnCtr, 11),
                        (String)oTrans.getDeptDetail(lnCtr , 10),
                        CommonUtils.NumberFormat((double) oTrans.getDeptDetail(lnCtr, 5), "#,##0.00"),
                        CommonUtils.NumberFormat((double) oTrans.getDeptDetail(lnCtr, 6), "#,##0.00")));
                }
                initEmployeeGrid();
            }
        } catch (SQLException | NullPointerException ex) {
            ex.printStackTrace();
        }
        
    }
     private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
             
        try{
           switch (event.getCode()){
            case F3:
            case ENTER:
                switch (lnIndex){
              
                    case 5: /*Search*/
                          pbLoaded = true;
                        if (oTrans.SearchTransaction(txtSeeks05.getText(), true)){
                               loadRecord();
                               txtSeeks05.setText((String)oTrans.getMaster("sTransNox"));
                               pnEditMode = oTrans.getEditMode();
                           } else 
                               ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                    
                    break;
                }   
            case DOWN:
                CommonUtils.SetNextFocus(txtField); break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        } 
        }catch(SQLException e){
                ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }
        
    }
    public static String priceWithDecimal (String price) {
        try{
           
            if(price == null){
                price = "0";
            }
            Double dPrice = Double.parseDouble(price);
            DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
            
            return formatter.format(dPrice);
        }catch(NullPointerException e){
            e.printStackTrace();
            return "";
        }
    }

    private void clearFields() {
        oTrans = new DeptIncentiveRelease(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setTranStat(102);
        oTrans.setWithUI(true);
        pnEditMode = EditMode.UNKNOWN;
        data.clear();
        emp_data.clear();
        txtField01.clear();
        txtField02.clear();
        txtSeeks05.clear();
    }
}