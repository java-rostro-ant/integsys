package gridergui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
import javafx.scene.layout.VBox;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.StringUtil;
import org.rmj.appdriver.agent.MsgBox;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.Incentive;
import org.rmj.fund.manager.base.LMasDetTrans;

public class AddIncentivesController implements Initializable, ScreenInterface {  
    private final String pxeModuleName = "AddIncentivesController";
    
    private GRider oApp;
    private int pnIndex = -1;
    private int pnRow = 0;
    
    private boolean pbLoaded = false;
    private boolean state = false;
    private LMasDetTrans oListener;
    
    private String psCode;
    private Incentive oTrans;
    
    private int pnEditMode;
    public int tbl_row = 0;
    
    private double lastValue = (double)0;
    private double total_alloc = (double) 0;
    private double lastpercValue = (double)0;
    
    private ObservableList<TableModel> data = FXCollections.observableArrayList();
    private ObservableList<TableEmployeeIncentives> incEmp_data = FXCollections.observableArrayList();
    
    public static TableModel empModel;
    

    @FXML
    private VBox VBoxForm;
    @FXML
    private Button btnExit;
    @FXML
    private Label lblHeader;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnReset;
    @FXML
    private Label label;
    @FXML
    private FontAwesomeIconView glyphExit;
    @FXML
    private TableView tblemployee;
    @FXML
    private TableColumn<?, ?> empIncindex01;
    @FXML
    private TableColumn<?, ?> empIncindex02;
    @FXML
    private TableColumn<?, ?> empIncindex03;
    @FXML
    private TableColumn<?, ?> empIncindex04;
    @FXML
    private TextField txtField10;
    @FXML
    private TextField txtField11;
    @FXML
    private TextField txtField12;
    @FXML
    private TextField txtField01;
    @FXML
    private TextField txtField02;
    @FXML
    private TextField txtField03;
    @FXML
    private TextField txtField05;
    @FXML
    private TextField txtField07;
    @FXML
    private TextField txtField04;
    @FXML
    private TextField txtField06;
    @FXML
    private TextArea txtField08;
    @FXML
    private Label txtField101;
    @FXML
    private Label txtField102;
    @FXML
    private Button btnDelEmp;
  
    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
    public void setIncentiveObject(Incentive foValue){
        oTrans = foValue;
    }
    
    public void setIncentiveCode(String fsValue){
        psCode = fsValue;
    }
    public void setTableRow(int row){
        tbl_row = row;
    }
    
    public void setState(boolean fsValue){
        state = fsValue;
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
            oListener = new LMasDetTrans() {
                @Override
                public void MasterRetreive(int fnIndex, Object foValue) {
                }
                @Override
                public void DetailRetreive(int fnRow, int fnIndex, Object foValue) {
                    loadEmployee();                    
                }
            };
            
            oTrans.setListener(oListener);
            
            btnOk.setOnAction(this::cmdButton_Click);
            btnDelEmp.setOnAction(this::cmdButton_Click);
            btnExit.setOnAction(this::cmdButton_Click);
            btnReset.setOnAction(this::cmdButton_Click);
            
            txtField03.setOnKeyPressed(this::txtField_KeyPressed);
            txtField04.setOnKeyPressed(this::txtField_KeyPressed);
            txtField05.setOnKeyPressed(this::txtField_KeyPressed);
            txtField06.setOnKeyPressed(this::txtField_KeyPressed);
            txtField07.setOnKeyPressed(this::txtField_KeyPressed);
            txtField08.setOnKeyPressed(this::txtArea_KeyPressed);
            txtField10.setOnKeyPressed(this::txtField_KeyPressed);
            txtField11.setOnKeyPressed(this::txtField_KeyPressed);
            txtField12.setOnKeyPressed(this::txtField_KeyPressed);
            
            txtField11.focusedProperty().addListener(txtField_Focus);
            txtField12.focusedProperty().addListener(txtField_Focus);
            
            txtField03.focusedProperty().addListener(txtField_Focus);
            txtField04.focusedProperty().addListener(txtField_Focus);
            txtField05.focusedProperty().addListener(txtField_Focus);
            txtField06.focusedProperty().addListener(txtField_Focus);
            txtField07.focusedProperty().addListener(txtField_Focus);
            txtField08.focusedProperty().addListener(txtArea_Focus);
            
            pnEditMode = oTrans.getEditMode();
            
            try {
                if (oTrans.getItemCount() > 0) pnRow = 1;
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(1);
            }
            
            initFields(pnEditMode);
            initButton(pnEditMode);
            
            initGrid();
            loadEmployee();
            loadIncentives();     
            getSelectedItem();
            
            pbLoaded = true;
    }
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        btnDelEmp.setVisible(lbShow);
        btnReset.setVisible(lbShow);
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

    private void initFields(int fnValue){
        boolean lbShow = (fnValue == EditMode.UPDATE || fnValue == EditMode.ADDNEW);
        if(lbShow){
            txtField03.setDisable(false);
            txtField04.setDisable(false);
            txtField05.setDisable(false);
            txtField06.setDisable(false);
            txtField07.setDisable(false);
            txtField08.setDisable(false);
            txtField11.setDisable(false);
            txtField12.setDisable(false);
        }else {
            txtField03.setDisable(true);
            txtField04.setDisable(true);
            txtField05.setDisable(true);
            txtField06.setDisable(true);
            txtField07.setDisable(true);
            txtField08.setDisable(true);
            txtField11.setDisable(true);
            txtField12.setDisable(true);
        }
    }

    private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();
        int lnIndex = Integer.parseInt(((TextField)event.getSource()).getId().substring(8,10));
        String lsValue = txtField.getText().replace(",", "");
        
        switch (event.getCode()){
            case F3:
                break;
            case TAB:
                if (lnIndex == 11 || lnIndex == 12){
                    event.consume();
                    return;
                }
            case ENTER:
            case DOWN:
                try {
                    double lnIncAmt = Double.valueOf(String.valueOf(oTrans.getIncentiveInfo(tbl_row, "nInctvAmt")));
                            
                    switch (lnIndex){
                        case 11:
                            double lnOldpercent = Double.parseDouble(oTrans.getIncentiveEmployeeAllocationInfo("nAllcPerc", psCode, (String) oTrans.getDetail(pnRow, "sEmployID")).toString());
                            if(lnIncAmt <= 0){
                                 MsgBox.showOk("Please specify incentive amount value!");
                                
                                txtField.setText(String.valueOf(lastValue));
                                txtField07.requestFocus();
                            }else{
                                 if (lastpercValue - lnOldpercent + Double.valueOf(lsValue) > 100){
                                    MsgBox.showOk("The specified percentage will exceed the incentive allocation.");
                                
                                    txtField.setText(String.valueOf(lastValue));
                                    txtField.requestFocus();
                                 }else{
                                     oTrans.setIncentiveEmployeeAllocationInfo("nAllcPerc", psCode, (String) oTrans.getDetail(pnRow, "sEmployID"), Double.valueOf(lsValue));
                                 }
                            }
                            
                            
                             break;
                        case 12:
                            double lnOldAmt = Double.valueOf(String.valueOf(oTrans.getIncentiveEmployeeAllocationInfo("nAllcAmtx", psCode, (String) oTrans.getDetail(pnRow, "sEmployID"))));

                            if (total_alloc - lnOldAmt + Double.valueOf(lsValue) > lnIncAmt){
                                MsgBox.showOk("The specified amount will exceed the incentive allocation.");
                            } else{
                                oTrans.setIncentiveEmployeeAllocationInfo("nAllcAmtx", psCode, (String) oTrans.getDetail(pnRow, "sEmployID"), Double.valueOf(lsValue));

                                if (pnRow <= oTrans.getItemCount())
                                    pnRow++;
                                else
                                    pnRow = 1;
                                
                                
                                int max = tblemployee.getItems().size();
                                pnRow = Math.min(pnRow, max);
                                if((tblemployee.getSelectionModel().getSelectedIndex()) == max-1){
                                    pnRow = 1;
                                }
                                tblemployee.scrollTo(pnRow-1);
                            }

                            loadEmployee();
                            getSelectedItem();

                            txtField11.requestFocus();
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
            }
    }
    
    final ChangeListener<? super Boolean> txtArea_Focus = (o,ov,nv)->{ 
        if (!pbLoaded) return;
        
        TextArea txtField = (TextArea)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
            try {
                if(!nv){ /*Lost Focus*/
                    switch (lnIndex){
                        case 8:
                           oTrans.setIncentiveInfo(tbl_row, "sRemarksx", lsValue); 
                           txtField.setText((String) oTrans.getIncentiveInfo(tbl_row, "sRemarksx"));
                           break;
                    }
                } else
                    txtField.selectAll();

                pnIndex = lnIndex;
            } catch (SQLException e) {
                MsgBox.showOk(e.getMessage());
                System.exit(1);
            }
    };

    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{ 
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8,10));
        
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
        if (lsValue.isEmpty()) return;    
        
        try {
            if(!nv){ /*Lost Focus*/
                switch (lnIndex){
                    case 03:
                        if (StringUtil.isNumeric(lsValue)) 
                            oTrans.setIncentiveInfo(tbl_row, "nQtyGoalx", Integer.parseInt(lsValue));
                        else    
                            oTrans.setIncentiveInfo(tbl_row, "nQtyGoalx", 0);

                        txtField.setText(CommonUtils.NumberFormat((Number)oTrans.getIncentiveInfo(tbl_row, "nQtyGoalx"), "0"));
                        break;
                    case 04:
                        if (StringUtil.isNumeric(lsValue)) 
                            oTrans.setIncentiveInfo(tbl_row, "nQtyActlx", Integer.parseInt(lsValue));
                        else    
                            oTrans.setIncentiveInfo(tbl_row, "nQtyActlx", 0);

                        txtField.setText(CommonUtils.NumberFormat((Number)oTrans.getIncentiveInfo(tbl_row, "nQtyActlx"), "0"));
                        break;
                    case 05:
                        if (StringUtil.isNumeric(lsValue)) 
                            oTrans.setIncentiveInfo(tbl_row, "nAmtGoalx", Double.parseDouble(lsValue));
                        else    
                            oTrans.setIncentiveInfo(tbl_row, "nAmtGoalx", 0.00);

                        txtField.setText(CommonUtils.NumberFormat((Number)oTrans.getIncentiveInfo(tbl_row, "nAmtGoalx"), "#,##0.00"));    
                        break;
                    case 06:
                        if (StringUtil.isNumeric(lsValue)) 
                            oTrans.setIncentiveInfo(tbl_row, "nAmtActlx", Double.parseDouble(lsValue));
                        else    
                            oTrans.setIncentiveInfo(tbl_row, "nAmtActlx", 0.00);

                        txtField.setText(CommonUtils.NumberFormat((Number)oTrans.getIncentiveInfo(tbl_row, "nAmtActlx"), "#,##0.00"));
                        break;
                    case 07:     
                        if (total_alloc > Double.parseDouble(lsValue)){
                            if (MsgBox.showOkCancel("Incentive amount is less than the total cash that is already allocated." +
                                    "\n\nDo you want to reset allocation and continue?") == MsgBox.RESP_YES_OK){
                                oTrans.resetIncentiveEmployeeAllocation(psCode);
                            } else break;
                        }

                        oTrans.setIncentiveInfo(tbl_row, "nInctvAmt", Double.parseDouble(lsValue));
                        txtField.setText(CommonUtils.NumberFormat((Number) oTrans.getIncentiveInfo(tbl_row, "nInctvAmt"), "#,##0.00"));
                        break;
                }
            } else{ //Focus
                switch (lnIndex){
                    case 05:
                        txtField.setText(String.valueOf(oTrans.getIncentiveInfo(tbl_row, "nAmtGoalx"))); break;
                    case 06:
                        txtField.setText(String.valueOf(oTrans.getIncentiveInfo(tbl_row, "nAmtActlx"))); break;
                    case 07:
                        txtField.setText(String.valueOf(oTrans.getIncentiveInfo(tbl_row, "nInctvAmt"))); break;
                    case 11:
                        txtField.setText(String.valueOf((Number) oTrans.getIncentiveEmployeeAllocationInfo("nAllcPerc", psCode, (String) oTrans.getDetail(pnRow, "sEmployID")))); break;
                    case 12:
                        txtField.setText(String.valueOf((Number) oTrans.getIncentiveEmployeeAllocationInfo("nAllcAmtx", psCode, (String) oTrans.getDetail(pnRow, "sEmployID"))));
                }
                txtField.selectAll();
                pnIndex = lnIndex;
            } 
        } catch (SQLException e) {
            MsgBox.showOk(e.getMessage());
        }
    };
    
    public void loadIncentives(){
        try { 
                txtField01.setText((String) oTrans.getMaster(1));
                
                txtField02.setText((String) oTrans.getIncentiveInfo(tbl_row, "xInctvNme"));
                txtField03.setText(String.valueOf(oTrans.getIncentiveInfo(tbl_row, "nQtyGoalx")));
                txtField04.setText(String.valueOf(oTrans.getIncentiveInfo(tbl_row, "nQtyActlx")));
                txtField05.setText(CommonUtils.NumberFormat((Number) oTrans.getIncentiveInfo(tbl_row, "nAmtGoalx"), "#,##0.00"));
                txtField06.setText(CommonUtils.NumberFormat((Number) oTrans.getIncentiveInfo(tbl_row, "nAmtActlx"), "#,##0.00"));
                txtField07.setText(CommonUtils.NumberFormat((Number) oTrans.getIncentiveInfo(tbl_row, "nInctvAmt"), "#,##0.00"));
                txtField08.setText((String) oTrans.getIncentiveInfo(tbl_row, "sRemarksx"));

                txtField01.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
                txtField02.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
                txtField03.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
                txtField04.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
                txtField05.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
                txtField06.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
                txtField07.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
                txtField08.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
                txtField10.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
                txtField11.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
                txtField12.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
    }
 
    private void loadEmployee(){
        System.out.println("loadEmployee");
        try {
            total_alloc = (double) 0;
            lastpercValue = (double) 0;
            int lnRow = oTrans.getItemCount(); 
            int lnCtr;
            
            if (incEmp_data.isEmpty()){
                for (lnCtr = 1; lnCtr <= lnRow; lnCtr++){
                    incEmp_data.add(new TableEmployeeIncentives(String.valueOf(lnCtr),
                                        (String) oTrans.getDetail(lnCtr, "xEmployNm"),
                                        (CommonUtils.NumberFormat((Number) oTrans.getIncentiveEmployeeAllocationInfo("nAllcPerc", psCode, (String) oTrans.getDetail(lnCtr, "sEmployID")), "#,##0.00")),
                                        (CommonUtils.NumberFormat((Number) oTrans.getIncentiveEmployeeAllocationInfo("nAllcAmtx", psCode, (String) oTrans.getDetail(lnCtr, "sEmployID")), "#,##0.00"))));

                    double allAmt = Double.parseDouble(oTrans.getIncentiveEmployeeAllocationInfo("nAllcAmtx", psCode, (String) oTrans.getDetail(lnCtr, "sEmployID")).toString());
                    total_alloc += allAmt;
                    double allPer= Double.parseDouble(oTrans.getIncentiveEmployeeAllocationInfo("nAllcPerc", psCode, (String) oTrans.getDetail(lnCtr, "sEmployID")).toString());
                    lastpercValue += allPer;
                }  
            } else{
                for (lnCtr = 1; lnCtr <= lnRow; lnCtr++){
                    incEmp_data.set(lnCtr -1 , new TableEmployeeIncentives(String.valueOf(lnCtr),
                                            (String) oTrans.getDetail(lnCtr, "xEmployNm"),
                                            (CommonUtils.NumberFormat((Number) oTrans.getIncentiveEmployeeAllocationInfo("nAllcPerc", psCode, (String) oTrans.getDetail(lnCtr, "sEmployID")), "#,##0.00")),
                                            (CommonUtils.NumberFormat((Number) oTrans.getIncentiveEmployeeAllocationInfo("nAllcAmtx", psCode, (String) oTrans.getDetail(lnCtr, "sEmployID")), "#,##0.00"))));

                    double allAmt = Double.parseDouble(oTrans.getIncentiveEmployeeAllocationInfo("nAllcAmtx", psCode, (String) oTrans.getDetail(lnCtr, "sEmployID")).toString());
                    total_alloc += allAmt;
                    double allPer= Double.parseDouble(oTrans.getIncentiveEmployeeAllocationInfo("nAllcPerc", psCode, (String) oTrans.getDetail(lnCtr, "sEmployID")).toString());
                    lastpercValue += allPer;
                }
            }
            
            if (oTrans.getIncentiveInfo(tbl_row, 101).toString().equals("NaN")){
                txtField101.setText("0.00%");
                txtField102.setText(CommonUtils.NumberFormat((Number) oTrans.getIncentiveInfo(tbl_row, 102),"#,##0.00"));
            }else{
                txtField101.setText(CommonUtils.NumberFormat((Number) oTrans.getIncentiveInfo(tbl_row, 101), "#,##0.00" ) + "%");
                txtField102.setText(CommonUtils.NumberFormat((Number) oTrans.getIncentiveInfo(tbl_row, 102),"#,##0.00"));
            }           
            
            tblemployee.getSelectionModel().select(pnRow - 1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            MsgBox.showOk(ex.getMessage()); 
            System.exit(1);
        }   
    }
    
    private void cmdButton_Click(ActionEvent event) {
        try {
            String lsButton = ((Button)event.getSource()).getId();
            switch (lsButton){
                case "btnDelEmp":
                    if (oTrans.removeIncentive(psCode)){
                        CommonUtils.closeStage(btnDelEmp);
                        MsgBox.showOk("Incentives removed succesfully.");
                    }else{
                        MsgBox.showOk(oTrans.getMessage());
                    }
                    break;
                    
                case "btnReset":
                    if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Reset incentive allocation?") == true){  
                        oTrans.resetIncentiveEmployeeAllocation(psCode);
                        MsgBox.showOk("Allocation reset successful.");
                        loadEmployee();
                        getSelectedItem();
                    }
                    break;
                case "btnOk":
                    CommonUtils.closeStage(btnOk);
                    break;
                case "btnExit":
                    CommonUtils.closeStage(btnExit);
                    break;
                    
                default:
                    ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                    return;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddIncentivesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
    public void initGrid() {   
        empIncindex01.setStyle("-fx-alignment: CENTER;");
        empIncindex02.setStyle("-fx-alignment: CENTER-LEFT;");
        empIncindex03.setStyle("-fx-alignment: CENTER-LEFT;");
        empIncindex04.setStyle("-fx-alignment: CENTER-LEFT;");
        
        empIncindex01.setCellValueFactory(new PropertyValueFactory<>("empIncindex01"));
        empIncindex02.setCellValueFactory(new PropertyValueFactory<>("empIncindex02")); 
        empIncindex03.setCellValueFactory(new PropertyValueFactory<>("empIncindex03"));
        empIncindex04.setCellValueFactory(new PropertyValueFactory<>("empIncindex04")); 
        
        tblemployee.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblemployee.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });

        incEmp_data.clear();
        tblemployee.setItems(incEmp_data);  
    }
//    
    @FXML
    private void tblemployee_Clicked(MouseEvent event) {
        pnRow = tblemployee.getSelectionModel().getSelectedIndex() + 1;  
        
        getSelectedItem();
        txtField11.requestFocus();
    }
    private void getSelectedItem(){
        try {                                    
            txtField10.setText((String) oTrans.getDetail(pnRow, "xEmployNm"));
            txtField11.setText(CommonUtils.NumberFormat((Number) oTrans.getIncentiveEmployeeAllocationInfo("nAllcPerc", psCode, (String) oTrans.getDetail(pnRow, "sEmployID")), "##0.00"));
            txtField12.setText(CommonUtils.NumberFormat((Number) oTrans.getIncentiveEmployeeAllocationInfo("nAllcAmtx", psCode, (String) oTrans.getDetail(pnRow, "sEmployID")), "#,##0.00"));
            lastValue = Double.parseDouble(oTrans.getIncentiveEmployeeAllocationInfo("nAllcPerc", psCode, (String) oTrans.getDetail(pnRow, "sEmployID")).toString());
       
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
