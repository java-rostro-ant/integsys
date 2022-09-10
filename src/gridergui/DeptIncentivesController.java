package gridergui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import static gridergui.DeptIncentivesHistController.priceWithDecimal;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

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
import javafx.scene.control.DateCell;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.constants.EditMode;
import static javafx.scene.input.KeyCode.F3;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.Duration;
import org.rmj.appdriver.StringUtil;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.fund.manager.base.DeptIncentive;
import org.rmj.fund.manager.base.LMasDetTrans;

public class DeptIncentivesController implements Initializable, ScreenInterface {
private final String pxeModuleName = "Department Incentives";
private double xOffset = 0;
private double yOffset = 0;
private double lnTotal = 0;
private GRider oApp;
private DeptIncentive oTrans;
private LMasDetTrans oListener;

private String oTransnox = "";
private boolean state = false;

private int pnIndex = -1;    
private int pnRow = -1;
private int pnEmp = 0;
private int oldPnEmp = -1;
private int pnEditMode;


private boolean pbLoaded = false;

private ObservableList<DeptIncentivesModel> deptincdata = FXCollections.observableArrayList();

@FXML
private AnchorPane AnchorMain;
@FXML
private Button btnNew,btnSave,btnSearch,btnCancel,btnClose;
@FXML
private TextField txtField01,txtField02,txtField03,txtField04;
@FXML
private TextArea txtField06;
@FXML
private DatePicker txtField05;
@FXML
private TextField txtField011,txtField021,txtField031,txtField041,txtField051;
@FXML
private TableView tblemployee;
@FXML
private TableColumn index01,index02,index03,index04,index05,index06,index07;
@FXML
private Label lblTotal;


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
            switch(fnIndex){
                case 1:
                    txtField01.setText((String) foValue); break;
                case 2:
                    txtField02.setText((String) foValue); break;
                case 4:
                    txtField04.setText((String) foValue); break;

            }
        }
        @Override
        public void DetailRetreive(int fnRow, int fnIndex, Object foValue) {
                loadDetail();
                getSelectedEmployee();

        }
     };   

    btnNew.setOnAction(this::cmdButton_Click);
    btnClose.setOnAction(this::cmdButton_Click);
    btnSave.setOnAction(this::cmdButton_Click);
    btnCancel.setOnAction(this::cmdButton_Click);
    btnSearch.setOnAction(this::cmdButton_Click);

    //initialize class
    oTrans  = new DeptIncentive(oApp, oApp.getBranchCode(), false);
    oTrans.setListener(oListener);
    oTrans.setTranStat(0);
    oTrans.setWithUI(true);

    /*Add listener to text fields*/


    txtField06.focusedProperty().addListener(txtArea_Focus);
    txtField041.focusedProperty().addListener(txtField_Focus);
    txtField051.focusedProperty().addListener(txtField_Focus);
    txtField041.setOnKeyPressed(this::txtFieldDetail_KeyPressed);
    txtField051.setOnKeyPressed(this::txtFieldDetail_KeyPressed);
    txtField03.setOnKeyPressed(this::txtField_KeyPressed);
    txtField04.setOnKeyPressed(this::txtField_KeyPressed);
    initButton(EditMode.UNKNOWN);
    pbLoaded = true;
    txtField05.setDayCellFactory(callB);
    tblemployee.setDisable(true);
        clearFields();
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
    int lnIndex = Integer.parseInt(((TextField)event.getSource()).getId().substring(8,10));
    String lsValue = txtField.getText();

   try {
        switch (event.getCode()) {
            case F3:
            case ENTER:

                switch (lnIndex){
                    case 3: /*search department*/
                        if (txtField01.getText() != ""){
                        if(oTrans.searchDepartment(lsValue, false)) {
                            txtField.setText((String) oTrans.getMaster("xDeptName")); 
                            clearDetail();
                            loadDetail();



                        }else{
                            txtField.setText((String) oTrans.getMaster("xDeptName")); 
                            ShowMessageFX.Warning(getStage(), "Unable to update department.", "Warning", null);

                        }
}

                        break;
                        case 4: /*search incentive*/
                        if (txtField01.getText() != ""){
                        if(oTrans.searchIncentive(lsValue, false)) {
                            txtField.setText((String) oTrans.getMaster("xInctvNme")); 
//                                loadDetail();

                        }else{
                            txtField.setText((String) oTrans.getMaster("xInctvNme")); 
                            ShowMessageFX.Warning(getStage(), "Unable to update incentive.", "Warning", null);

                        }
                        }
                        break;
                }

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
private void txtFieldDetail_KeyPressed(KeyEvent event){
    TextField txtField = (TextField)event.getSource();
    int lnIndex = Integer.parseInt(((TextField)event.getSource()).getId().substring(8,11));
    String lsValue = txtField.getText();

    switch (event.getCode()){
        case F3:
            break;
        case TAB:
        case ENTER:
        case DOWN:
            try {

                switch (lnIndex){
                    case 41:
                    if (txtField011.getText() != ""){
                        if (StringUtil.isNumeric(lsValue)){ 
                            if((Double.parseDouble(lsValue) <= 999999.99) &&(Double.parseDouble(lsValue) >= 0)){
                               oTrans.setDetail(pnEmp, "sNewAmtxx", Double.parseDouble(lsValue));
                        txtField041.setText(CommonUtils.NumberFormat((Double) Double.parseDouble(lsValue), "###0.00"));
                        System.out.println("Set Row =" + pnEmp );
                            }else{
                            ShowMessageFX.Warning(null, pxeModuleName, "Max Amount for 'New Incentive Amount' exceeds the maximum limit.");
                            txtField041.requestFocus();
                            return;
                            }
                        }else{    
                        oTrans.setDetail(pnEmp, "sNewAmtxx", 0.00);
                         txtField041.setText("0.00");
                        }

                    }

                    break;

                    case 51:
                        if (txtField011.getText() != ""){
                          if (lsValue.length() > 64){
                        ShowMessageFX.Warning(null, pxeModuleName, "Max length for 'Remarks' exceeds the maximum limit.");
                        txtField051.requestFocus();
                        return;
                        }
                       else{
                       oTrans.setDetail(pnEmp,"sRemarksx", lsValue);
                        }
                        }

                            if (pnEmp <= oTrans.getItemCount())
                                pnEmp++;
                            else
                                pnEmp = 1;


                            tblemployee.getVisibleLeafColumns();
                            int max = tblemployee.getItems().size();
                            pnEmp = Math.min(pnEmp, max);
                            if((tblemployee.getSelectionModel().getSelectedIndex()) == max-1){
                                pnEmp = 1;
                            }

                            tblemployee.scrollTo(pnEmp -1);

                        loadDetail();
                        getSelectedEmployee();

                        txtField041.requestFocus();
                        event.consume();
                        return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            CommonUtils.SetNextFocus(txtField);
            break;
        case UP:
            CommonUtils.SetPreviousFocus(txtField);
            break;
        }
}

private void initButton(int fnValue){
    boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);

    btnCancel.setVisible(lbShow);
    btnSearch.setVisible(lbShow);
    btnSave.setVisible(lbShow);

    btnNew.setManaged(!lbShow);
    btnSave.setManaged(lbShow);
    btnCancel.setManaged(lbShow);
    btnSearch.setManaged(lbShow);

    btnNew.setVisible(!lbShow);

    txtField06.setDisable(!lbShow);




}
@Override
public void setGRider(GRider foValue) {
    oApp = foValue;
}    
    private void loadMaster() throws SQLException {

    txtField01.setText((String) oTrans.getMaster("sTransNox"));
    txtField02.setText(CommonUtils.xsDateMedium((Date) oTrans.getMaster("dTransact")));
    txtField03.setText((String) oTrans.getMaster("xDeptName"));
    txtField04.setText((String) oTrans.getMaster("sInctveCD"));
    txtField05.setValue(strToDate(CommonUtils.xsDateShort((Date) oTrans.getMaster(5))));
    txtField06.setText((String) oTrans.getMaster("sRemarksx"));
    pnRow = 0;
    pnEmp = 0;
//        loadDetail();

}
private void loadDetail(){
    try {

        if (txtField03.getText() != ""){
            deptincdata.clear();
            lnTotal = 0;
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

        }
    } catch (SQLException e) {
        e.printStackTrace();
        ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
          System.exit(1);
    }
}


private LocalDate strToDate(String val){
      DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate localDate = LocalDate.parse(val, date_formatter);
      return localDate;
}

final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{ 
    if (!pbLoaded) return;

        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 11));
        String lsValue = txtField.getText();

    if (lsValue == null) return;
        try {
            if(!nv){ /*Lost Focus*/
                switch (lnIndex){
                    case 41:

                    if (txtField011.getText() != ""){
                        if (StringUtil.isNumeric(lsValue)){ 
                            if((Double.parseDouble(lsValue) <= 999999.99) &&(Double.parseDouble(lsValue) >= 0)){
                        oTrans.setDetail(pnEmp , "sNewAmtxx", Double.parseDouble(lsValue));
                        txtField041.setText(CommonUtils.NumberFormat((Double) Double.parseDouble(lsValue), "#,##0.00"));
                        System.out.println("Set . =" + pnEmp );

                            }else{
                            ShowMessageFX.Warning(null, pxeModuleName, "Max Amount for 'New Incentive Amount' exceeds the maximum limit.");
                            txtField041.requestFocus();
                            return;
                            }
                        }else{    
                        oTrans.setDetail(pnEmp, "sNewAmtxx", 0.00);
                         txtField041.setText("0.00");
                        }
                    }

                    break;
                    case 51:
                        if (txtField011.getText() != ""){
                          if (lsValue.length() > 64){
                        ShowMessageFX.Warning(null, pxeModuleName, "Max length for 'Remarks' exceeds the maximum limit.");
                        txtField051.requestFocus();
                        return;
                        }
                       else{
                       oTrans.setDetail(pnEmp ,"sRemarksx", lsValue);
                        }
                        }
                    break;
                default:
                    ShowMessageFX.Warning(null, pxeModuleName, "Text field with name " + txtField.getId() + " not registered.");
                    return;

                }

               } 
             else{
                txtField.selectAll();

}

            pnIndex = lnIndex;
        } catch (SQLException e) {
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);

        }
};

final ChangeListener<? super Boolean> txtArea_Focus = (o,ov,nv)->{ 
    if (!pbLoaded) return;

    TextArea txtArea = (TextArea)((ReadOnlyBooleanPropertyBase)o).getBean();
    int lnIndex = Integer.parseInt(txtArea.getId().substring(8, 10));
    String lsValue = txtArea.getText();

    if (lsValue == null) return;

    try {   
        if(!nv){ /*Lost Focus*/
            switch (lnIndex){
                case 6:
                    if (txtField01.getText() != ""){
                        if (lsValue.length() > 64){
                        ShowMessageFX.Warning(null, pxeModuleName, "Max length for 'Remarks' exceeds the maximum limit.");
                        txtField051.requestFocus();
                        return;
                        }
                       else{
                    oTrans.setMaster(6, txtField06.getText());
                        }
                    }
                    break;

            }
        } else
            txtArea.selectAll();


        pnIndex = lnIndex;
    } catch (SQLException e) {
        ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        System.exit(1);
    }
};

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

            case "btnNew": 
                    if (oTrans.NewTransaction() ){
                        clearFields();
                        loadMaster();
                        pnEditMode = EditMode.ADDNEW;
                    } else
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
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
            case "btnSave":

                    if (lnTotal > 0){
                    if (oTrans.SaveTransaction()){

                        ShowMessageFX.Warning(getStage(), "Transaction save successfully.", "Warning", null);
                        if(state){
                           onsuccessUpdate();
                        }else{
                            clearFields();
                            oTrans = new DeptIncentive(oApp, oApp.getBranchCode(), false);
                            oTrans.setListener(oListener);
                            oTrans.setWithUI(true);
                            pnEditMode = EditMode.UNKNOWN;
                        }
                    } else {
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                    }
                    }else {
                        ShowMessageFX.Warning(getStage(), "Please Edit Transaction First", "Warning", null);
                    }
                break;

            case "btnCancel":
                    if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to disregard changes?") == true){  
                        if(state){
                           onsuccessUpdate();
                        }else{
                            clearFields();
                            oTrans = new DeptIncentive(oApp, oApp.getBranchCode(), false);
                            oTrans.setListener(oListener);
                            oTrans.setWithUI(true);
                            pnEditMode = EditMode.READY;
                        }
                    }
                break;
            case "btnSearch":
                    if (txtField01.getText() != ""){
                        if(oTrans.searchDepartment(txtField03.getText(), false)) {
                            txtField03.setText((String) oTrans.getMaster("xDeptName")); 
                            clearDetail();
                            loadDetail();



                        }else{
                            txtField03.setText((String) oTrans.getMaster("xDeptName")); 
                            ShowMessageFX.Warning(getStage(), "Unable to update department.", "Warning", null);

                        }
}


                    break;
        }

        initButton(pnEditMode);
    } catch (SQLException e) {
                e.printStackTrace();
                ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
            } 
}

private void clearFields(){
    txtField01.setText("");
    txtField02.setText("");
    txtField03.setText("");
    txtField04.setText("");
    txtField06.setText("");

    clearDetail();
}
    private void clearDetail(){
    txtField011.setText("");
    txtField021.setText("");
    txtField031.setText("");
    txtField041.setText("");
    txtField051.setText("");
    tblemployee.setDisable(true);
    txtField041.setDisable(true);
    txtField051.setDisable(true);
    deptincdata.clear();
    lblTotal.setText("0.0");
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
   tblemployee.setDisable(false);
    tblemployee.getSelectionModel().select(pnEmp - 1);



}

 private void getSelectedEmployee() {
   try{
      if(!deptincdata.isEmpty()){
    txtField011.setText((String) oTrans.getDetail(pnEmp, "xEmployNm"));
    txtField021.setText((String) oTrans.getDetail(pnEmp, "xPositnNm"));
    txtField031.setText( oTrans.getDetail(pnEmp, "sOldAmtxx").toString());
    txtField041.setText( oTrans.getDetail(pnEmp, "sNewAmtxx").toString());
    txtField051.setText((String) oTrans.getDetail(pnEmp, "sRemarksx"));
    txtField041.setDisable(false);
    txtField051.setDisable(false);
            oldPnEmp = pnEmp;
    }
    }catch (SQLException ex) {
        ex.printStackTrace();

    }


} 
@FXML
private void tblemployee_Clicked(MouseEvent event) {
    pnEmp = tblemployee.getSelectionModel().getSelectedIndex() +1;

        if(event.getClickCount()>0){

            if(!tblemployee.getItems().isEmpty()){
            getSelectedEmployee();



    }
}
}
public void getDate(ActionEvent event) { 
    try {
                oTrans.setMaster(5,txtField05.getValue().toString());
            }catch (SQLException ex) {
                Logger.getLogger(DeptIncentivesController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

private Callback<DatePicker, DateCell> callB = new Callback<DatePicker, DateCell>() {
        @Override
        public DateCell call(final DatePicker param) {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                    LocalDate today = LocalDate.now();
                    setDisable(empty || item.compareTo(today) < 0);
                }

            };
        }

    };



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