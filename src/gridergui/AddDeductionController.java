/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gridergui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.math.BigDecimal;
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

/**
 * FXML Controller class
 *
 * @author user 
 */
public class AddDeductionController implements Initializable {
     private GRider oApp;
    private int pnIndex = -1;
    private int pdIndex = -1;
    private int pnOldRow = -1;
    private int pnRow = 0;
    private boolean pbLoaded = false;
    private LMasDetTrans oListener;
    
    private String psOldRec;
    private String psCode;
    private Incentive oTrans;
    private final String pxeModuleName = "AddDeductionController";
    private int pnEditMode;
    private int pnSubItems = 0;
    public int tbl_row = 0;
    private double lastValue = (double)0;
    private boolean state = false;
    
    private ObservableList<TableIncentives> inc_data = FXCollections.observableArrayList();
    private ObservableList<TableModel> data = FXCollections.observableArrayList();
    private ObservableList<TableEmployeeIncentives> incEmp_data = FXCollections.observableArrayList();
    public static TableIncentives incModel;
    public static TableModel empModel;
    
    
    @FXML
    private VBox VBoxForm;
    @FXML
    private FontAwesomeIconView glyphExit;
    @FXML
    private Label lblHeader;
    @FXML
    private Label label;
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
    private Button btnOk;
    @FXML
    private Button btnExit;
    @FXML
    private TextField txtField06;
    @FXML
    private Label txtField101;
    @FXML
    private Label txtField102;

    public void setGRider(GRider foValue) {
        
    }
    public static void setData(TableIncentives incdata){
        incModel = incdata;    
    }
    public void setDeductionObject(Incentive foValue){
        oTrans = foValue;
    }
    
    public void setDeductionCode(String fsValue){
        psCode = fsValue;
    }
    public void setTableRows(int row){
        tbl_row = row;
    }

    public void setState(boolean fsValue){
        state = fsValue;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
         btnOk.setOnAction(this::cmdButton_Click);
         btnExit.setOnAction(this::cmdButton_Click);
         
         txtField02.setOnKeyPressed(this::txtField_KeyPressed);
         txtField03.setOnKeyPressed(this::txtField_KeyPressed);
         txtField04.setOnKeyPressed(this::txtField_KeyPressed);
         txtField05.setOnKeyPressed(this::txtField_KeyPressed);
         txtField06.setOnKeyPressed(this::txtField_KeyPressed);
         
         txtField101.setOnKeyPressed(this::txtField_KeyPressed);
         txtField102.setOnKeyPressed(this::txtField_KeyPressed);
         
         txtField03.focusedProperty().addListener(txtField_Focus);
         txtField04.focusedProperty().addListener(txtField_Focus);
         txtField05.focusedProperty().addListener(txtField_Focus);
         txtField06.focusedProperty().addListener(txtField_Focus);
         txtField101.focusedProperty().addListener(txtField_Focus);
         txtField102.focusedProperty().addListener(txtField_Focus);
         
          if (incEmp_data.isEmpty()){
                tblemployee.getSelectionModel().selectFirst();
                pnRow = 1;
                getSelectedItems();
            } 
         
         pnEditMode = oTrans.getEditMode();
         pbLoaded = true;
         loadDeductionDetail();
         loadEmployee();
         initGrid();
         initFields(pnEditMode);     
    }
    
    private void initFields(int fnValue){
        boolean lbShow = (fnValue == EditMode.UPDATE || fnValue == EditMode.ADDNEW);
        if(lbShow){
            txtField03.setDisable(false);
            txtField04.setDisable(false);
            txtField05.setDisable(false);
            txtField06.setDisable(false);
        }else {
            txtField03.setDisable(true);
            txtField04.setDisable(true);
            txtField05.setDisable(true);
            txtField06.setDisable(true);
            txtField04.requestFocus();
        }
    }
    private void loadEmployee(){
        try {
            incEmp_data.clear();
            int lnRow = oTrans.getItemCount(); 
            for (int lnCtr = 1; lnCtr <= lnRow; lnCtr++){
                incEmp_data.add(new TableEmployeeIncentives(String.valueOf(lnCtr),
                        (String)oTrans.getDetail(lnCtr, "xEmployNm"),
                        oTrans.getDeductionEmployeeAllocationInfo("nAllcPerc", tbl_row, (String) oTrans.getDetail(lnCtr, "sEmployID")).toString(),
                        oTrans.getDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(lnCtr, "sEmployID")).toString()));                
            }   
                txtField06.setText((CommonUtils.NumberFormat((Number)oTrans.getDeductionInfo(tbl_row, "nDedctAmt"), "#,##0.00")));
                if (oTrans.getDeductionInfo(tbl_row, 101).toString().equals("NaN")){
                    txtField101.setText("0.00 %");
                }else{
                txtField101.setText((CommonUtils.NumberFormat((Number)oTrans.getDeductionInfo(tbl_row, 101),"##0.00")+ "%"));
                txtField102.setText((CommonUtils.NumberFormat((Number)oTrans.getDeductionInfo(tbl_row, 102),"#,##0.00")));
                }
            initGrid();
        } catch (SQLException e) {
            e.printStackTrace();
            MsgBox.showOk(e.getMessage());
        }
    }
    private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();
        int lnIndex = Integer.parseInt(((TextField)event.getSource()).getId().substring(8,10));
        String lsValue = txtField.getText();
        
        switch (event.getCode()) {
            case F3:
            case ENTER:
                switch (lnIndex){ 
                }
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
    
    public void loadDeductionDetail(){
        try { 
            
                txtField01.setText((String) oTrans.getMaster(1));
                txtField02.setText(incModel.getIncindex02());
               
                txtField02.setDisable(state);
                txtField03.setDisable(state);
                txtField04.setDisable(state);
                txtField05.setDisable(state);
                txtField06.setDisable(state);
                txtField01.setDisable(state);
            } catch (SQLException ex) {
                Logger.getLogger(AddDeductionController.class.getName()).log(Level.SEVERE, null, ex);
            }
            pnEditMode = oTrans.getEditMode();
            pbLoaded = true;
    }

    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        switch (lsButton){
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
        tblemployee.setItems(incEmp_data); 
    }
    
    private void loadDetail(){
        
        try {
            int lnRow = oTrans.getItemCount();             
            for (int lnCtr = 1; lnCtr <= lnRow; lnCtr++){
                //get    
                incEmp_data.add(new TableEmployeeIncentives(String.valueOf(lnCtr),
                        (String)oTrans.getDetail(lnCtr, "xEmployNm"),
                        oTrans.getDeductionEmployeeAllocationInfo("nAllcPerc", tbl_row, (String) oTrans.getDetail(lnCtr, "sEmployID")).toString(),
                        oTrans.getDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(lnCtr, "sEmployID")).toString()));
                
                pnEditMode = oTrans.getEditMode();
                pbLoaded = true;
            }
                txtField101.setText(String.valueOf(oTrans.getDeductionInfo(tbl_row, "xAllocPer") + "%"));
                txtField102.setText(String.valueOf(oTrans.getDeductionInfo(tbl_row, "xAllcAmtx")));
            
            MsgBox.showOk(String.valueOf(oTrans.getDeductionInfo(tbl_row, "nDedctAmt")));
        } catch (SQLException ex) {
            ex.printStackTrace();
            MsgBox.showOk(ex.getMessage()); 
            System.exit(1);
        }
    }
            
    @FXML
    private void tblemployee_Clicked(MouseEvent event) {
        pnRow = tblemployee.getSelectionModel().getSelectedIndex() + 1;  
        getSelectedItems();
    }           
    
    private void getSelectedItems(){
        try {
            txtField03.setText((String) oTrans.getDetail(pnRow, "xEmployNm"));
            txtField04.setText(CommonUtils.NumberFormat((Number) oTrans.getDeductionEmployeeAllocationInfo("nAllcPerc", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID")), "##0.00"));
            txtField05.setText(CommonUtils.NumberFormat((Number) oTrans.getDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID")), "#,##0.00"));
            lastValue = (double) oTrans.getDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID"));
            txtField04.requestFocus();
        }   
        catch (SQLException ex) {
            ex.printStackTrace();
            MsgBox.showOk(ex.getMessage()); 
        }
    }
    
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{ 
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        System.out.println(txtField.getId());
        int lnIndex = Integer.parseInt(txtField.getId().substring(8,10));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
            
        if(!nv){ /*Lost Focus*/
            try {
                switch (lnIndex){
                    case 04:
                        if (StringUtil.isNumeric(lsValue)){                        
                            oTrans.setDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID"), Double.valueOf(lsValue));
                        }else{
                            oTrans.setDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID"), 0.00);
                        }
                            double x = Double.parseDouble(String.valueOf(oTrans.getDeductionInfo(tbl_row, "nDedctAmt")));
                            double y = Double.parseDouble(String.valueOf(txtField102.getText().replace(",","")));
                            double z = Double.parseDouble(String.valueOf(lsValue));
                                    
                        if (x >= ((y+z)-lastValue)){

                            txtField.setText(CommonUtils.NumberFormat((Number) oTrans.getDeductionEmployeeAllocationInfo("nAllcAmtx",tbl_row,(String)oTrans.getDetail(pnRow, "sEmployID")),"#,##0.00"));
                            loadEmployee();
                        }
                        else{
                            MsgBox.showOk("Amount entered exceeds the amount allocated.");
                            oTrans.setDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID"), lastValue);
                            loadEmployee();
                            txtField.requestFocus(); 
                        }
                        break;
                    case 05:
                        if (StringUtil.isNumeric(lsValue))                        
                            oTrans.setDeductionEmployeeAllocationInfo("nAllcPerc", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID"), Double.valueOf(lsValue));
                        else
                            oTrans.setDeductionEmployeeAllocationInfo("nAllcPerc", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID"), 0.00);

                            txtField.setText(CommonUtils.NumberFormat((Number) oTrans.getDeductionEmployeeAllocationInfo("nAllcPerc", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID")), "#,##0.00"));
                            loadEmployee();
                        break;
                    case 06:
                        if (StringUtil.isNumeric(lsValue)) 
                            oTrans.setDeductionInfo(tbl_row, "nDedctAmt", Double.valueOf(lsValue));
                        else    
                            oTrans.setDeductionInfo(tbl_row, "nDedctAmt", Double.valueOf(lsValue.replace(",","")));
                        
                            txtField.setText(CommonUtils.NumberFormat((Number)oTrans.getDeductionInfo(tbl_row, "nDedctAmt"), "#,##0.00"));
                            loadEmployee();
                            break;
                    case 101:
                        oTrans.getDeductionInfo(tbl_row, "xAllocPer");
                        break;
                    case 102:
                        oTrans.getDeductionInfo(tbl_row, "xAllocAmt");
                        break;
                }
            } catch (SQLException e) {
                MsgBox.showOk(e.getMessage());
            }     
        } else{ //Focus
            pnIndex = lnIndex;
            txtField.selectAll();
        }     
    };
    

}