/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
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
public class AddIncentivesController implements Initializable, ScreenInterface {  
    private GRider oApp;
    private int pnIndex = -1;
    private int pdIndex = -1;
    private int pnOldRow = -1;
    private int pnRow = 0;
    private int pninc = -1;
    
    private boolean pbLoaded = false;
    private boolean state = false;
    private LMasDetTrans oListener;
    
    private String psOldRec;
    private String psCode;
    private Incentive oTrans;
    private final String pxeModuleName = "AddIncentivesController";
    private int pnEditMode;
    private int pnSubItems = 0;
    public int tbl_row = 0;
    private double lastValue = (double)0;
    private double lastpercValue = (double)0;
    
    private ObservableList<TableIncentives> inc_data = FXCollections.observableArrayList();
    private ObservableList<TableModel> data = FXCollections.observableArrayList();
    private ObservableList<TableEmployeeIncentives> incEmp_data = FXCollections.observableArrayList();
    public static TableIncentives incModel;
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
    private TextField txtField7;
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
  
    public void setGRider(GRider foValue) {
        
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

            btnOk.setOnAction(this::cmdButton_Click);
            btnDelEmp.setOnAction(this::cmdButton_Click);
            btnExit.setOnAction(this::cmdButton_Click);
            
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
            
            if (incEmp_data.isEmpty()){
                tblemployee.getSelectionModel().selectFirst();
                pnRow = 1;
                getSelectedItem();
            } 
            pnEditMode = oTrans.getEditMode();
            pbLoaded = true;
            loadEmployee();
            loadIncentives();      
            initFields(pnEditMode);
            initButton(pnEditMode);
            
    }
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW);
        btnDelEmp.setVisible(lbShow);
     }
    public static void setData(TableIncentives incdata){
        incModel = incdata;    
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
                           oTrans.setIncentiveInfo(tbl_row, "sRemarksx", lsValue); break;
                           
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
        System.out.println(txtField.getId());
        int lnIndex = Integer.parseInt(txtField.getId().substring(8,10));
        String lsValue = txtField.getText();
        if (lsValue == null) return;
            
        if(!nv){ /*Lost Focus*/
            try {
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
                            oTrans.setIncentiveInfo(tbl_row, "nAmtGoalx", Double.valueOf(lsValue));
                        else    
                            oTrans.setIncentiveInfo(tbl_row, "nAmtGoalx", 0.00);
                        
                            txtField.setText(CommonUtils.NumberFormat((Number)oTrans.getIncentiveInfo(tbl_row, "nAmtActlx"), "#,##0.00"));
                            loadEmployee();
                            break;
                    case 06:
                        if (StringUtil.isNumeric(lsValue)) 
                            oTrans.setIncentiveInfo(tbl_row, "nAmtActlx", Double.valueOf(lsValue));
                        else    
                            oTrans.setIncentiveInfo(tbl_row, "nAmtActlx", 0.00);
                             txtField.setText(CommonUtils.NumberFormat((Number)oTrans.getIncentiveInfo(tbl_row, "nAmtActlx"), "##0.00"));
                        break;
                        
                    case 07:
                        if (StringUtil.isNumeric(lsValue)) 
                            oTrans.setIncentiveInfo(tbl_row, "nInctvAmt", Double.valueOf(lsValue));
                        else 
                            oTrans.setIncentiveInfo(tbl_row, "nInctvAmt",  Double.valueOf(lsValue.replace(",","")));
                        
                            txtField.setText(CommonUtils.NumberFormat((Number)oTrans.getIncentiveInfo(tbl_row, "nInctvAmt"), "#,##0.00"));
                            loadEmployee();
                            break;

                    case 11:
                        if (StringUtil.isNumeric(lsValue)) {                       
                            oTrans.setIncentiveEmployeeAllocationInfo("nAllcPerc", psCode, (String) oTrans.getDetail(pnRow, "sEmployID"), Double.valueOf(lsValue));
                        }else{
                            oTrans.setIncentiveEmployeeAllocationInfo("nAllcPerc", psCode, (String) oTrans.getDetail(pnRow, "sEmployID"), lsValue);
                        }
                            double maxalloc = 100.00;
                            double allocperc = Double.parseDouble(String.valueOf(lsValue));
                            double alloc = Double.parseDouble(String.valueOf(txtField101.getText().replace("%", "")));
                            
                            if (maxalloc >= (allocperc + alloc) - lastpercValue){
                                txtField.setText(CommonUtils.NumberFormat((Number) oTrans.getIncentiveEmployeeAllocationInfo("nAllcPerc", psCode, (String) oTrans.getDetail(pnRow, "sEmployID")), "#,##0.00"));                        
                                loadEmployee();
                            }else{
                                MsgBox.showOk("Amount entered exceeds the amount allocated.");
                                oTrans.setIncentiveEmployeeAllocationInfo("nAllcPerc", psCode, (String) oTrans.getDetail(pnRow, "sEmployID"), lastpercValue); 
                                loadEmployee();
                                txtField.requestFocus();
                            }
                        break;
                        
                    case 12:
                        if (StringUtil.isNumeric(lsValue))  {                      
                            oTrans.setIncentiveEmployeeAllocationInfo("nAllcAmtx", psCode, (String) oTrans.getDetail(pnRow, "sEmployID"), Double.valueOf(lsValue)); 
                        }else{
                            oTrans.setIncentiveEmployeeAllocationInfo("nAllcAmtx", psCode, (String) oTrans.getDetail(pnRow, "sEmployID"), lsValue); 
                        } 
                        double x = Double.parseDouble(String.valueOf(oTrans.getIncentiveInfo(tbl_row, "nInctvAmt")));
                        double y = Double.parseDouble(String.valueOf(txtField102.getText().replace(",","")));
                        double z = Double.parseDouble(String.valueOf(lsValue));

                        if (x >= ((z+y)-lastValue)){ 
                           txtField.setText(CommonUtils.NumberFormat((Number) oTrans.getIncentiveEmployeeAllocationInfo("nAllcAmtx", psCode, (String) oTrans.getDetail(pnRow, "sEmployID")), "#,##0.00"));
                           loadEmployee();
                        }
                        else{
                            MsgBox.showOk("Amount entered exceeds the amount allocated.");
                            oTrans.setIncentiveEmployeeAllocationInfo("nAllcAmtx", psCode, (String) oTrans.getDetail(pnRow, "sEmployID"), lastValue); 
                            loadEmployee();
                            txtField.requestFocus();                   
                        }
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
    
    public void loadIncentives(){
        try { 
                txtField01.setText((String) oTrans.getMaster(1));
                
                txtField02.setText(incModel.getIncindex02());
                txtField03.setText(incModel.getIncindex03());
                txtField04.setText(incModel.getIncindex04());
                txtField05.setText(incModel.getIncindex05());
                txtField06.setText(incModel.getIncindex06());
                txtField08.setText(incModel.getIncindex08());

                txtField01.setDisable(state);
                txtField02.setDisable(state);
                txtField03.setDisable(state);
                txtField04.setDisable(state);
                txtField05.setDisable(state);
                txtField06.setDisable(state);
                txtField07.setDisable(state);
                txtField08.setDisable(state);
                txtField10.setDisable(state);
                txtField11.setDisable(state);
                txtField12.setDisable(state);
                
            } catch (SQLException ex) {
                Logger.getLogger(AddIncentivesController.class.getName()).log(Level.SEVERE, null, ex);
            }
            pnEditMode = oTrans.getEditMode();
            pbLoaded = true;
    }
 
    private void loadEmployee(){
        try {
            incEmp_data.clear();
            int lnRow = oTrans.getItemCount(); 
            for (int lnCtr = 1; lnCtr <= lnRow; lnCtr++){
                //get
                incEmp_data.add(new TableEmployeeIncentives(String.valueOf(lnCtr),
                (String) oTrans.getDetail(lnCtr, "xEmployNm"),
                 oTrans.getIncentiveEmployeeAllocationInfo("nAllcPerc", psCode, (String) oTrans.getDetail(lnCtr, "sEmployID")).toString(),
                 oTrans.getIncentiveEmployeeAllocationInfo("nAllcAmtx", psCode, (String) oTrans.getDetail(lnCtr, "sEmployID")).toString()));

                pnEditMode = oTrans.getEditMode();
                pbLoaded = true;
            }     
                txtField07.setText((CommonUtils.NumberFormat((Number)oTrans.getIncentiveInfo(tbl_row, "nInctvAmt"), "#,##0.00")));
                if (oTrans.getIncentiveInfo(tbl_row, 101).toString().equals("NaN")){
                    txtField101.setText("0.00 %");
                }else{
                    txtField101.setText(CommonUtils.NumberFormat((Number) oTrans.getIncentiveInfo(tbl_row, 101), "#,##0.00" ) + "%");
                    txtField102.setText(CommonUtils.NumberFormat((Number)oTrans.getIncentiveInfo(tbl_row, 102),"#,##0.00"));
                }
                
                initGrid();
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
                    if (oTrans.removeIncentive(incModel.getxInctvCde())){
                        CommonUtils.closeStage(btnDelEmp);
                        MsgBox.showOk("Incentives succesfully remove");
                    }else{
                        MsgBox.showOk(oTrans.getMessage());
                    }
                    
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
        tblemployee.setItems(incEmp_data);
    }

    @FXML
    private void tblemployee_Clicked(MouseEvent event) {
        if (event.getClickCount()== 2){
            pnRow = tblemployee.getSelectionModel().getSelectedIndex() + 1;  
            getSelectedItem();
        }
    }
    private void getSelectedItem(){

            try {
                txtField10.setText((String) oTrans.getDetail(pnRow, "xEmployNm"));
                txtField11.setText(CommonUtils.NumberFormat((Number) oTrans.getIncentiveEmployeeAllocationInfo("nAllcPerc", psCode, (String) oTrans.getDetail(pnRow, "sEmployID")), "##0.00"));
                txtField12.setText(CommonUtils.NumberFormat((Number) oTrans.getIncentiveEmployeeAllocationInfo("nAllcAmtx", psCode, (String) oTrans.getDetail(pnRow, "sEmployID")), "#,##0.00"));
                lastValue = (double) oTrans.getIncentiveEmployeeAllocationInfo("nAllcAmtx", psCode, (String) oTrans.getDetail(pnRow, "sEmployID"));
                
                txtField11.requestFocus();
                
            } 
            catch (SQLException ex) {
                Logger.getLogger(AddIncentivesController.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

}
