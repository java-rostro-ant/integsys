/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-dedamtsault.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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
    private int pnRow = 0;
    private boolean pbLoaded = false;
    private LMasDetTrans oListener;
    
    private static final String REGEX = "^[0-9]{1,7}([,.][0-9]{1,2})?$";
    private String psOldRec;
    private String psCode;
    private Incentive oTrans;
    private final String pxeModuleName = "AddDeductionController";
    private int pnEditMode;
    private int pnSubItems = 0;
    public int tbl_row = 0;
    private double lastValue = (double)0;
    private double total_alloc = (double)0;
    
    private double lastpercValue = (double)0;
    private boolean state = false;
    
    private ObservableList<TableModel> data = FXCollections.observableArrayList();
    private ObservableList<TableEmployeeIncentives> incEmp_data = FXCollections.observableArrayList();
    public static TableIncentives incModel;
    
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
    private TableColumn<?, ?> empIncindex05;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnExit;
    @FXML
    private Button btnReset;
    @FXML
    private TextField txtField06;
    @FXML
    private Label txtField101;
    @FXML
    private Label txtField102;
    @FXML
    private Button btnDelEmp;

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
                public void MasterRetreive(int fnIndex, Object foValue) {
                }
                @Override
                public void DetailRetreive(int fnRow, int fnIndex, Object foValue) {
                    loadEmployee();                    
                }
            };
        oTrans.setListener(oListener);
        btnOk.setOnAction(this::cmdButton_Click);
        btnExit.setOnAction(this::cmdButton_Click);
        btnDelEmp.setOnAction(this::cmdButton_Click);
        btnReset.setOnAction(this::cmdButton_Click);

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
       loadDeductionDetail();
       getSelectedItems();
       pbLoaded = true;
            
    }
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        btnDelEmp.setVisible(lbShow);
        btnReset.setVisible(lbShow);
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
                    double lnIncAmt = Double.valueOf(String.valueOf(oTrans.getDeductionInfo(tbl_row, "nDedctAmt")));
                            
                    switch (lnIndex){
                        case 04:
                            double lnOldAmt = Double.parseDouble(oTrans.getDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID")).toString());

                            if (total_alloc - lnOldAmt + Double.valueOf(lsValue) > lnIncAmt){
                                ShowMessageFX.Warning(getStage(), "The specified amount will exceed the deduction allocation.", "Warning", null);
                                
                                txtField.setText(String.valueOf(lastValue));
                                txtField04.requestFocus();
                            } else{
                                oTrans.setDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID"), Double.valueOf(lsValue));
                             
                            }
                             break;   
                        case 05:
                            
                           double lnOldpercent = Double.parseDouble(oTrans.getDeductionEmployeeAllocationInfo("nAllcPerc", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID")).toString());

                            if(lnIncAmt <= 0){
                                 ShowMessageFX.Warning(getStage(), "Please specify deduction amount value!", "Warning", null);
                                
                                txtField.setText(String.valueOf(lastValue));
                                txtField.requestFocus();
                            } else{
                                 if (lastpercValue - lnOldpercent + Double.valueOf(lsValue) > 100){
                                    ShowMessageFX.Warning(getStage(), "The specified percentage will exceed the deduction allocation.", "Warning", null);

                                    txtField.setText(String.valueOf(lastValue));
                                    txtField04.requestFocus();
                                } else{
                                    oTrans.setDeductionEmployeeAllocationInfo("nAllcPerc", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID"), Double.valueOf(lsValue));
                                    if (pnRow <= oTrans.getItemCount())
                                      pnRow++;
                                  else
                                      pnRow = 1;


                                  tblemployee.getVisibleLeafColumns();
                                  int max = tblemployee.getItems().size();
                                  pnRow = Math.min(pnRow, max);
                                  if((tblemployee.getSelectionModel().getSelectedIndex()) == max-1){
                                      pnRow = 1;
                                    }
                                   tblemployee.scrollTo(pnRow-1);
                                 
                                 }
                            } 

                            loadEmployee();
                            getSelectedItems();
                            txtField04.requestFocus();
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
                    case 06:     
                        if (total_alloc > Double.parseDouble(lsValue)){
                            if (MsgBox.showOkCancel("Incentive amount is less than the total cash that is already allocated." +
                                    "\n\nDo you want to reset allocation and continue?") == MsgBox.RESP_YES_OK){
                                oTrans.resetIncentiveEmployeeAllocation(psCode);
                            } else break;
                        }

                        oTrans.setDeductionInfo(tbl_row, "nDedctAmt", Double.parseDouble(lsValue));
                        txtField.setText(CommonUtils.NumberFormat((Number) oTrans.getDeductionInfo(tbl_row, "nDedctAmt"), "#,##0.00"));
                        break;
                }
            } else{ //Focus
                switch (lnIndex){
                    case 04:
                         txtField.setText(String.valueOf((Number) oTrans.getDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID"))));break;
                    case 05:
                        txtField.setText(String.valueOf((Number) oTrans.getDeductionEmployeeAllocationInfo("nAllcPerc", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID")))); break;
                    case 06:
                        txtField.setText(String.valueOf(oTrans.getDeductionInfo(tbl_row, "nDedctAmt"))); break;
                }
                txtField.selectAll();
                pnIndex = lnIndex;
            } 
        } catch (SQLException e) {
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }
    };
    
    public void loadDeductionDetail(){
        try { 
                txtField01.setText((String) oTrans.getMaster(1));
                txtField06.setText(CommonUtils.NumberFormat((Number) oTrans.getDeductionInfo(tbl_row, "nDedctAmt"), "#,##0.00"));
                        
               
                txtField02.setText(incModel.getIncindex02());
                txtField01.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
                txtField02.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
                txtField03.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
                txtField04.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
                txtField05.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
                txtField06.setDisable(pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE);
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
                                            (CommonUtils.NumberFormat((Number)oTrans.getDeductionEmployeeAllocationInfo("nAllcPerc", tbl_row, (String) oTrans.getDetail(lnCtr, "sEmployID")), "#,##0.00")),
                                            (CommonUtils.NumberFormat((Number)oTrans.getDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(lnCtr, "sEmployID")), "#,##0.00")),
                                            (CommonUtils.NumberFormat((Number)oTrans.getDeductionEmployeeAllocationInfo("nTotalAmt", tbl_row, (String) oTrans.getDetail(lnCtr, "sEmployID")), "#,##0.00"))));

                    double allAmt = Double.parseDouble(oTrans.getDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(lnCtr, "sEmployID")).toString());
                    total_alloc += allAmt;
                    double allPerc = Double.parseDouble(oTrans.getDeductionEmployeeAllocationInfo("nAllcPerc", tbl_row, (String) oTrans.getDetail(lnCtr, "sEmployID")).toString());
                    lastpercValue += allPerc;
                }  
               
            } else{
                for (lnCtr = 1; lnCtr <= lnRow; lnCtr++){
                    incEmp_data.set(lnCtr -1 , new TableEmployeeIncentives(String.valueOf(lnCtr),
                                            (String) oTrans.getDetail(lnCtr, "xEmployNm"),
                                            (CommonUtils.NumberFormat((Number)oTrans.getDeductionEmployeeAllocationInfo("nAllcPerc", tbl_row, (String) oTrans.getDetail(lnCtr, "sEmployID")), "#,##0.00")),
                                            (CommonUtils.NumberFormat((Number)oTrans.getDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(lnCtr, "sEmployID")), "#,##0.00")),
                                            (CommonUtils.NumberFormat((Number)oTrans.getDeductionEmployeeAllocationInfo("nTotalAmt", tbl_row, (String) oTrans.getDetail(lnCtr, "sEmployID")), "#,##0.00"))));

                    double allAmt = Double.parseDouble(oTrans.getDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(lnCtr, "sEmployID")).toString());
                    total_alloc += allAmt;
                    double allPerc = Double.parseDouble(oTrans.getDeductionEmployeeAllocationInfo("nAllcPerc", tbl_row, (String) oTrans.getDetail(lnCtr, "sEmployID")).toString());
                    lastpercValue += allPerc;
                }
            }
            
            if (oTrans.getDeductionInfo(tbl_row, 101).toString().equals("NaN")){
                txtField101.setText("0.00%");
                txtField102.setText(CommonUtils.NumberFormat((Number) oTrans.getDeductionInfo(tbl_row, 102),"#,##0.00"));
            }else{
                txtField101.setText(CommonUtils.NumberFormat((Number) oTrans.getDeductionInfo(tbl_row, 101), "#,##0.00" ) + "%");
                txtField102.setText(CommonUtils.NumberFormat((Number) oTrans.getDeductionInfo(tbl_row, 102),"#,##0.00"));
            }           
            
            tblemployee.getSelectionModel().select(pnRow - 1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            ShowMessageFX.Warning(getStage(),ex.getMessage(), "Warning", null);
            System.exit(1);
        }   
    }

    private void cmdButton_Click(ActionEvent event) {
        try{
            String lsButton = ((Button)event.getSource()).getId();
             switch (lsButton){
               case "btnDelEmp":
                    {
                            if (oTrans.removeDeduction(tbl_row)){
                                CommonUtils.closeStage(btnDelEmp);
                                ShowMessageFX.Warning(getStage(),"Deduction succesfully remove ", "Warning", null);
                            }else{
                                ShowMessageFX.Warning(oTrans.getMessage(),"Warning", null);
                            }
                       
                    }
                    break;
                case "btnReset":
                    if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Are you sure you want to reset all deductions?") == true){  
                        
                        oTrans.resetDeductionEmployeeAllocation(tbl_row);
                        ShowMessageFX.Warning(getStage(), "Allocation reset successful.", "Warning", null);
                        loadEmployee();
                        getSelectedItems();
                       
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
                     break;
             }
        }catch (SQLException ex) {
             Logger.getLogger(AddDeductionController.class.getName()).log(Level.SEVERE, null, ex);
         }   
    } 
    public void initGrid() {   
        empIncindex01.setStyle("-fx-alignment: CENTER;");
        empIncindex02.setStyle("-fx-alignment: CENTER-LEFT;");
        empIncindex03.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 0;");
        empIncindex04.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 0;");
        empIncindex05.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 0;");
        
        empIncindex01.setCellValueFactory(new PropertyValueFactory<>("empIncindex01"));
        empIncindex02.setCellValueFactory(new PropertyValueFactory<>("empIncindex02")); 
        empIncindex03.setCellValueFactory(new PropertyValueFactory<>("empIncindex03"));
        empIncindex04.setCellValueFactory(new PropertyValueFactory<>("empIncindex04")); 
        empIncindex05.setCellValueFactory(new PropertyValueFactory<>("empIncindex05")); 
        
        tblemployee.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblemployee.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });

        incEmp_data.clear();
        tblemployee.setItems(incEmp_data);  
    }
      
    @FXML
    private void tblemployee_Clicked(MouseEvent event) {
            pnRow = tblemployee.getSelectionModel().getSelectedIndex() + 1;  
            getSelectedItems();  
            txtField04.requestFocus();
    }           
    
    private void getSelectedItems(){
        try {
            txtField03.setText((String) oTrans.getDetail(pnRow, "xEmployNm"));
            txtField05.setText(CommonUtils.NumberFormat((Number) oTrans.getDeductionEmployeeAllocationInfo("nAllcPerc", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID")), "##0.00"));
            txtField04.setText(CommonUtils.NumberFormat((Number) oTrans.getDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID")), "#,##0.00"));
            lastValue = (double) oTrans.getDeductionEmployeeAllocationInfo("nAllcAmtx", tbl_row, (String) oTrans.getDetail(pnRow, "sEmployID"));
        }   
        catch (SQLException ex) {
            ex.printStackTrace();
            ShowMessageFX.Warning(getStage(), ex.getMessage(), "Warning", null);
 
        }
    }
    
    
}