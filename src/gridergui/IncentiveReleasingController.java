/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import java.util.ResourceBundle;;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agent.MsgBox;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.IncentiveRelease;
import org.rmj.fund.manager.base.LMasDetTrans;
import org.rmj.fund.manager.parameters.IncentiveBankInfo;

/**
 * FXML Controller class
 *
 * @author user
 */
public class IncentiveReleasingController implements Initializable, ScreenInterface {
    private GRider oApp;
    private IncentiveRelease oTrans;
    private IncentiveBankInfo trans;
   
    private LMasDetTrans oListener;
    
    double xOffset = 0;
    double yOffset = 0;
    
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
    @FXML
    private TableColumn empIndex08;
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
                loadRecord();
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
            }
        };
        
        oTrans = new IncentiveRelease(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
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
    private void loadRecord(){
        try { 
            txtField01.setText((String)oTrans.getMaster("sTransNox"));
            txtField02.setText((String)new SimpleDateFormat("yyyy-MM-dd").format(oTrans.getMaster("dTransact")));
            data.clear();
            for (int lnCtr = 0; lnCtr <= oTrans.getItemCount()-1; lnCtr++){
                    CheckBox select = new CheckBox();
                    Date  date = new SimpleDateFormat("yyyy-MM-dd").parse(oTrans.getDetail(lnCtr).getMaster("dTransact").toString());  
                    SimpleDateFormat formatter =  new SimpleDateFormat("MMMMM dd, yyyy");
                    String dTransact = formatter.format(date);
                    Date  date1 = new SimpleDateFormat("yyyyMM").parse(oTrans.getDetail(lnCtr).getMaster("sMonthxxx").toString());  
                    SimpleDateFormat month_formatter =  new SimpleDateFormat("MMMMM yyyy");
                    String sMonth = month_formatter.format(date1);
                    data.add(new Release(oTrans.getDetail(lnCtr).getMaster("sTransNox").toString(),
                    oTrans.getDetail(lnCtr).getMaster("xBranchNm").toString(),
                    oTrans.getDetail(lnCtr).getMaster("xDeptName").toString(),
                    sMonth,
                    dTransact,
                    oTrans.getDetail(lnCtr).getMaster("sRemarksx").toString(),
                    select,
                    oTrans.getTag(lnCtr)));
                    int post = lnCtr;
                    System.out.println(oTrans.getTag(post));
                    select.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                        try {
                            if(newValue){
                                oTrans.setTag(post, true);
                            }else{
                                oTrans.setTag(post, false);
                            }
                            System.out.println(oTrans.getTag(post));
                        } catch (SQLException ex) {
                            MsgBox.showOk(ex.getMessage());
                        }
                    });
                    
                                
                }
            initGrid();
        } catch (SQLException | ParseException e) {
            MsgBox.showOk(e.getMessage());
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
        empIndex08.setStyle("-fx-alignment: CENTER-RIGHT");
        empIndex01.setCellValueFactory(new PropertyValueFactory<Release,String>("empIndex01"));
        empIndex02.setCellValueFactory(new PropertyValueFactory<Release,String>("empIndex02"));
        empIndex03.setCellValueFactory(new PropertyValueFactory<Release,String>("empIndex03"));
        empIndex04.setCellValueFactory(new PropertyValueFactory<Release,String>("empIndex04"));
        empIndex05.setCellValueFactory(new PropertyValueFactory<Release,String>("empIndex05"));
        empIndex06.setCellValueFactory(new PropertyValueFactory<Release,String>("empIndex06"));
        empIndex07.setCellValueFactory(new PropertyValueFactory<Release,CheckBox>("empIndex07"));
        empIndex08.setCellValueFactory(new PropertyValueFactory<Release,CheckBox>("empIndex08"));
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
                        if (oTrans.SearchTransaction(txtSeeks05.getText(), false)){
                            loadRecord();
                            pnEditMode = oTrans.getEditMode();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                    break;
                case "btnNew": //create new transaction
                        pbLoaded = true;
                        if (oTrans.NewTransaction()){
                            loadRecord();
                            pnEditMode = oTrans.getEditMode();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                    break;
                case "btnRelease": //release transaction
//                  if (oTrans.ReleaseTransaction()){
//                            MsgBox.showOk("Transaction releasing success!");
//                            clearFields();
//                            pnEditMode = oTrans.getEditMode();
//                        } else 
//                            MsgBox.showOk(oTrans.getMessage());
//                    break;
                 case "btnSave": //save transaction
                        if (oTrans.SaveTransaction()){
                            MsgBox.showOk("Transaction Successfully Saved.");
                            clearFields();
                        } else {
                            MsgBox.showOk(oTrans.getMessage());
                        }
                       
                    break;    
                case "btnApproved": //approve transaction
                    if (oTrans.ConfirmTransaction()){
                            MsgBox.showOk("Transaction success approved.");
                            clearFields();
                            pnEditMode = oTrans.getEditMode();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                    break;
                case "btnDisapproved": //disapprove transaction
                    if (oTrans.CancelTransaction()){
                            MsgBox.showOk("Transaction success disapproved.");
                            clearFields();
                            pnEditMode = oTrans.getEditMode();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
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
            MsgBox.showOk(e.getMessage());
        }
    } 
    
    @FXML
    private void tblIncentivesDetail_Click(MouseEvent event) {
       
        try {
            pnRow = tblincetives.getSelectionModel().getSelectedIndex();
            String lsBankName;
            String lsBankAcct;
            emp_data.clear();
            
            for(int lnCtr  = 1; lnCtr  <= oTrans.getDetail(pnRow).getItemCount(); lnCtr ++){
                trans = oTrans.getBankInfo((String) oTrans.getDetail(pnRow).getDetail(lnCtr, "sEmployID"));
                
                if(trans != null){
                    lsBankName = trans.getMaster(8).toString();
                    lsBankAcct = trans.getMaster(3).toString();
                }else{
                    lsBankName = "";
                    lsBankAcct = "";
                }
                /*DecimalFormat*/ 
                
                emp_data.add(new Release(String.valueOf(lnCtr),
                        oTrans.getDetail(pnRow).getDetail(lnCtr , "xEmployNm").toString(),
                        oTrans.getDetail(pnRow).getDetail(lnCtr , "xEmpLevNm").toString(),
                        oTrans.getDetail(pnRow).getDetail(lnCtr , "xPositnNm").toString(),
                        oTrans.getDetail(pnRow).getDetail(lnCtr , "xSrvcYear").toString(),
                        lsBankName,
                        lsBankAcct,
                        priceWithDecimal((Double)(oTrans.getDetail(pnRow).getDetail(lnCtr , "nTotalAmt")))));
               
            }
            initEmployeeGrid();
           
        } catch (SQLException ex) {
            MsgBox.showOk(ex.getMessage());
        }
        
    }
     private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
             
        try{
           switch (event.getCode()){
            case F3:
                switch (lnIndex){
              
                case 5: /*Search*/
                      pbLoaded = true;
                     if (oTrans.SearchTransaction(txtSeeks05.getText(), false)){
                            loadRecord();
                            pnEditMode = oTrans.getEditMode();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                     break;
                }   
            case ENTER:
            case DOWN:
                CommonUtils.SetNextFocus(txtField); break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        } 
        }catch(SQLException e){
                MsgBox.showOk(e.getMessage());
        }
        
    }
    public static String priceWithDecimal (Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(price);
    }

    private void clearFields() {
        oTrans = new IncentiveRelease(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        pnEditMode = EditMode.UNKNOWN;
        trans = new IncentiveBankInfo(oApp, oApp.getBranchCode(), false);
        data.clear();
        emp_data.clear();
        txtField01.clear();
        txtField02.clear();
    }
}
