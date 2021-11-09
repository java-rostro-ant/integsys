/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agent.MsgBox;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.fund.manager.base.Incentive;

/**
 * FXML Controller class
 *
 * @author user
 */
public class AddIncentivesController implements Initializable, ScreenInterface {  

    private static void MsgBox(int x) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private GRider oApp;
    private int pnIndex = -1;
    private int pdIndex = -1;
    private int pnOldRow = -1;
    private int pnRow = 0;
    private boolean pbLoaded = false;
     

    
    private String psOldRec;
    private Incentive oTrans;
    
    @FXML
    private VBox VBoxForm;
    @FXML
    private Button btnExit;
    @FXML
    private Label lblHeader;
    @FXML
    private TextField txtField1;
    @FXML
    private TextField txtField2;
    @FXML
    private TextField txtField3;
    @FXML
    private TextField txtField4;
    @FXML
    private TextField txtField5;
    @FXML
    private TextArea txtField6;
    @FXML
    private TextField txtField31;
    @FXML
    private TextField txtField41;
    @FXML
    private Button btnOk12;
    @FXML
    private Label label;
    
    private ObservableList<TableModel> data = FXCollections.observableArrayList();
    
    @FXML
    private FontAwesomeIconView glyphExit;
    @FXML
    private TableView tblemployee;
    @FXML
    private TableColumn<?, ?> index01;
    @FXML
    private TableColumn<?, ?> index02;
   
    private int pnEditMode;
    private int pnSubItems = 0;
    @FXML
    private Label allocperc;
    @FXML
    private Label allocamt;
    @FXML
    private TextField txtField10;
    @FXML
    private TextArea txtField13;
    @FXML
    private TextField txtField11;
    @FXML
    private TextField txtField12;
    @FXML
    private TableColumn<?, ?> index03;
    @FXML
    private TableColumn<?, ?> index04;
    @Override
    public void setGRider(GRider foValue) {
        
    }
    
    public void setIncentiveObject(Incentive foValue){
        oTrans = foValue;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
            txtField2.focusedProperty().addListener(txtField_Focus);
            
            txtField2.setOnKeyPressed(this::txtField_KeyPressed);
            txtField11.setOnKeyPressed(this::txtField_KeyPressed);
            txtField12.setOnKeyPressed(this::txtField_KeyPressed);
            
            txtField2.requestFocus();
            initGrid();
    }
    @FXML
    private void tblemployee_Clicked(MouseEvent event) throws SQLException { 
        pnRow = tblemployee.getSelectionModel().getSelectedIndex()+ 1;
              
            txtField11.requestFocus();
            txtField11.selectAll();
            mainoTransaction(); 
            
    }
    
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{ 
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 9));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
            
        if(!nv){ /*Lost Focus*/
            switch (lnIndex){
                case 11:
                   if(txtField.getText().equals("") || txtField.getText().equals("%"))
                       txtField.setText("");
                       return;
                case 3:
                   if(txtField.getText().equals("") || txtField.getText().equals("%"))
                       txtField.setText("");
                       return;
            }
        }
    };
    public void mainoTransaction() throws SQLException{
        String lsStockIDx = (String) oTrans.getDetail(pnRow, "xEmployNm");
    
        if (pnRow >= 0){                   
            txtField10.setText((String) oTrans.getDetail(pnRow, "xEmployNm"));
            
            
        }
        txtField11.setText("");
        txtField12.setText("");
    }
    private void unloadForm(){
        VBox myBox = (VBox) VBoxForm.getParent();
        myBox.getChildren().clear();  
    }    
    private void incallo(){
        try {     
            txtField1.setText((String) oTrans.getMaster(1));
            for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
                data.add(new TableModel(String.valueOf(lnCtr),
                (String) oTrans.getIncentiveEmployeeAllocationInfo("xEmployNm", (String) oTrans.getIncentiveInfo(oTrans.getIncentiveCount(), "sInctveCD"), (String) oTrans.getIncentiveInfo(oTrans.getIncentiveCount(), "sEmployID"))));
//                System.out.print(oTrans.getDetail(lnCtr, "sEmployID"));
//                System.out.print("\t");
//                System.out.print(oTrans.getDetail(lnCtr, "nTotalAmt"));
//                System.out.print("\t");
//                System.out.print(oTrans.getDetail(lnCtr, "xEmployNm"));
//                System.out.println("");                
            }
    
            initGrid();
        } catch (SQLException ex) {
            //Logger.getLogger(AddIncentivesController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        
        switch (lsButton){                         
            case "btnExit":
                unloadForm();
                break;
        }
    }
    private void loadMaster() throws SQLException {
        txtField1.setText((String) oTrans.getMaster(1));       
    }

    
    private void txtField_KeyPressed(KeyEvent event){
        try {
            TextField txtField = (TextField)event.getSource();
            int lnIndex = Integer.parseInt(((TextField)event.getSource()).getId().substring(8,9));
            String lsValue = txtField.getText();
            try {
                switch (event.getCode()) {
                    case F3:
                        switch (lnIndex){
                            case 2:
                                if(oTrans.searchIncentive(txtField2.getText(), false)){
                                    txtField2.setText((String)oTrans.getIncentiveInfo(oTrans.getIncentiveCount(),"xInctvNme"));
                                    txtField3.setText(String.valueOf(oTrans.getIncentiveInfo(oTrans.getIncentiveCount(),"nQtyGoalx")));
                                    txtField31.setText(String.valueOf(oTrans.getIncentiveInfo(oTrans.getIncentiveCount(),"nQtyActlx")));
                                    txtField4.setText(String.valueOf(oTrans.getIncentiveInfo(oTrans.getIncentiveCount(),"nAmtGoalx")));
                                    txtField41.setText(String.valueOf(oTrans.getIncentiveInfo(oTrans.getIncentiveCount(),"nAmtGoalx")));
                                    txtField5.setText(String.valueOf(oTrans.getIncentiveInfo(oTrans.getIncentiveCount(),"nInctvAmt")));
                                    txtField6.setText((String) oTrans.getIncentiveInfo(oTrans.getIncentiveCount(),"sRemarksx"));
                                    
                                    incallo();
                                }
                                break;
                        }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                MsgBox.showOk(e.getMessage());
            }
            
            switch (event.getCode()){
                case ENTER:
                    //allocamt.setText(CommonUtils.NumberFormat(Double.valueOf(txtField12.getText().toString()), "#,##0.00"));
                    switch (lnIndex){
                        case 12:
                            index04.setText(oTrans.getDetail(pnRow, "xAllocAmt").toString());
                            break;
                    }       
                case DOWN:
                    CommonUtils.SetNextFocus(txtField);
                    break;
                case UP:
                    CommonUtils.SetPreviousFocus(txtField);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddIncentivesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void initGrid() {   
        
        index01.setStyle("-fx-alignment: CENTER;");
        index02.setStyle("-fx-alignment: CENTER-LEFT;");
        index03.setStyle("-fx-alignment: CENTER-LEFT;");
        
        index01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        index02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        index03.setCellValueFactory(new PropertyValueFactory<>("index03"));  
        tblemployee.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblemployee.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            
            });
        });
        tblemployee.setItems(data);
        
    }

}
