/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agent.MsgBox;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.Incentive;
import org.rmj.fund.manager.base.LMasDetTrans;
import org.rmj.fund.manager.parameters.IncentiveBankInfo;

/**
 * FXML Controller class
 *
 * @author user
 */
public class IncentiveConfirmationController implements Initializable, ScreenInterface {
    private GRider oApp;
    private Incentive oTrans;
    private IncentiveBankInfo trans;
   
    private LMasDetTrans oListener;
    
    double xOffset = 0;
    double yOffset = 0;
    
    private int pnIndex = -1;
    private int pnEditMode;
    private int pnRow = 0;
    private int pnSubItems = 0;
    private boolean pbLoaded = false;
    @FXML
    private TextField txtField01;
    @FXML
    private TextField txtField02;
    @FXML
    private TextField txtField03;
    @FXML
    private TextField txtField04;
    @FXML
    private TextArea txtField05;
    @FXML
    private TextField txtField16;
    @FXML
    private TextField txtSeeks01;
    @FXML
    private TextField txtSeeks02;
    @FXML
    private TextField txtSeeks05;
    @FXML
    private Button btnBrowse;
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
    private AnchorPane AnchorMainIncentiveConfirmation;
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
    
    private final ObservableList<TableIncentives> data = FXCollections.observableArrayList();
    private final ObservableList<TableIncentives> emp_data = FXCollections.observableArrayList();
   @FXML
    private Label lblHeader;    
    @FXML
    private AnchorPane searchBar;
    
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
                    case 5:
                        txtField05.setText((String) foValue); break;
                    case 17:
                        txtField03.setText((String) foValue); 
                    {
                        try {
                            loadIncentives();
                        } catch (SQLException ex) {
                            Logger.getLogger(IncentiveConfirmationController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                        break;

                    case 16:
                        txtField16.setText((String) foValue); break;
                }
            }

            @Override
            public void DetailRetreive(int fnRow, int fnIndex, Object foValue) {
            }
        };
        oTrans = new Incentive(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
         
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnCancel.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        btnApproved.setOnAction(this::cmdButton_Click);
        btnDisapproved.setOnAction(this::cmdButton_Click);
        
        pnEditMode = EditMode.UNKNOWN;
        
        txtField01.setDisable(true);
        txtField02.setDisable(true);
        initButton(pnEditMode);
    }    

    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        btnBrowse.setVisible(!lbShow);
        btnApproved.setVisible(!lbShow);
        btnDisapproved.setVisible(!lbShow);
        
        btnCancel.setVisible(lbShow);
        
        btnBrowse.setManaged(!lbShow);
        btnApproved.setManaged(!lbShow);
        btnDisapproved.setManaged(!lbShow);
        if (lbShow){
            txtField02.requestFocus();
            btnBrowse.setVisible(!lbShow);  
            btnApproved.setVisible(!lbShow);
            btnDisapproved.setVisible(!lbShow);
            btnBrowse.setManaged(!lbShow);
            btnDisapproved.setManaged(!lbShow);
            btnCancel.setManaged(lbShow);
        }
       
    }
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        try {
            switch (lsButton){
                case "btnBrowse":
                        if (oTrans.SearchTransaction(txtSeeks01.getText(), false)){
                            loadIncentives();
                            pnEditMode = oTrans.getEditMode();
                        } else 
                            MsgBox.showOk("error " + oTrans.getMessage());
                    break;
                
                case "btnCancel":
                    //reload detail
                    break;
               
                case "btnClose":
                    if(ShowMessageFX.OkayCancel(null, "Employee Bank Info", "Do you want to disregard changes?") == true){
                      
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
     private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
             
        try{
           switch (event.getCode()){
            case F3:
                switch (lnIndex){
              
                case 1: /*Search*/
                    if (oTrans.SearchTransaction(txtSeeks01.getText(), false)){
                        loadIncentives();
                        pnEditMode = EditMode.READY;
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
   
    public void initGrid() {
        incIndex02.setStyle("-fx-alignment: CENTER-RIGHT");
        incIndex03.setStyle("-fx-alignment: CENTER-RIGHT");
        incIndex04.setStyle("-fx-alignment: CENTER-RIGHT");
        incIndex05.setStyle("-fx-alignment: CENTER-RIGHT");
        incIndex06.setStyle("-fx-alignment: CENTER-RIGHT");
        incIndex01.setCellValueFactory(new PropertyValueFactory<Release,String>("incindex02"));
        incIndex02.setCellValueFactory(new PropertyValueFactory<Release,String>("incindex03"));
        incIndex03.setCellValueFactory(new PropertyValueFactory<Release,String>("incindex04"));
        incIndex04.setCellValueFactory(new PropertyValueFactory<Release,String>("incindex05"));
        incIndex05.setCellValueFactory(new PropertyValueFactory<Release,String>("incindex06"));
        incIndex06.setCellValueFactory(new PropertyValueFactory<Release,String>("incindex07"));
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
    private void loadIncentives() throws SQLException{
            //load to grid the incentives.
         data.clear();
         int lnCtr; 
         System.out.println("INCENTIVES");
         txtField01.setText((String)oTrans.getMaster(1));
         txtField02.setText(oTrans.getMaster(2).toString());
         txtField03.setText((String)oTrans.getMaster(3));
         txtField04.setText((String)oTrans.getMaster(4));
         txtField05.setText((String)oTrans.getMaster(5));
         txtField16.setText((String)oTrans.getMaster(16));
         for (lnCtr = 1; lnCtr <= oTrans.getIncentiveCount(); lnCtr++){
             data.add(new TableIncentives(String.valueOf(lnCtr),
                oTrans.getIncentiveInfo(lnCtr, "xInctvNme").toString(),
                priceWithDecimal(Double.valueOf(oTrans.getIncentiveInfo(lnCtr, "nQtyGoalx").toString())),
                priceWithDecimal(Double.valueOf(oTrans.getIncentiveInfo(lnCtr, "nQtyActlx").toString())),
                priceWithDecimal(Double.valueOf(oTrans.getIncentiveInfo(lnCtr, "nAmtGoalx").toString())),
                priceWithDecimal(Double.valueOf(oTrans.getIncentiveInfo(lnCtr, "nAmtActlx").toString())),
                priceWithDecimal(Double.valueOf(oTrans.getIncentiveInfo(lnCtr, "nInctvAmt").toString()))));

             System.out.println(oTrans.getIncentiveInfo(lnCtr, "xInctvNme"));
             System.out.println(oTrans.getIncentiveInfo(lnCtr, "nQtyGoalx"));
             System.out.println(oTrans.getIncentiveInfo(lnCtr, "nQtyActlx"));
             System.out.println(oTrans.getIncentiveInfo(lnCtr, "nAmtGoalx"));
             System.out.println(oTrans.getIncentiveInfo(lnCtr, "nAmtActlx"));
             System.out.println(oTrans.getIncentiveInfo(lnCtr, "nInctvAmt"));
         }

             System.out.println("DEDUCTIONS");
             for (lnCtr = 1; lnCtr <= oTrans.getDeductionCount(); lnCtr++){
                 data.add(new TableIncentives(String.valueOf(lnCtr), 
                     oTrans.getDeductionInfo(lnCtr, "sRemarksx").toString(),
                     "",
                     "",
                     "",
                     "",
                     oTrans.getDeductionInfo(lnCtr, "nDedctAmt").toString()));

                 //to display these fields on grid.
                 System.out.println(oTrans.getDeductionInfo(lnCtr, "sRemarksx"));
                 System.out.println("");
                 System.out.println("");
                 System.out.println("");
                 System.out.println("");
                 System.out.println(oTrans.getDeductionInfo(lnCtr, "nDedctAmt"));
             }
         initGrid();
    }
    public static String priceWithDecimal (Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(price);
    }
}
