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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
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
    private Label lblStatus;
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
    private TextField txtSeeks02;
    @FXML
    private TextField txtSeeks05;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnApproved;
    @FXML
    private Button btnDisapproved;
    @FXML
    private Button btnClose;
    @FXML
    private HBox hbButtons;
    
    @FXML
    private CheckBox Check01;
    @FXML
    private CheckBox Check02;
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
    private final ObservableList<Release> emp_data = FXCollections.observableArrayList();
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
                            MsgBox.showOk(ex.getMessage());
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
        btnClose.setOnAction(this::cmdButton_Click);
        btnApproved.setOnAction(this::cmdButton_Click);
        btnDisapproved.setOnAction(this::cmdButton_Click);
        tblincentives_column();
        tblemployee_column();
        txtSeeks05.setOnKeyPressed(this::txtField_KeyPressed); 
        pnEditMode = EditMode.UNKNOWN;
        Check01.setDisable(true);
        Check02.setDisable(true);
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
        btnBrowse.setManaged(!lbShow);
        btnApproved.setManaged(!lbShow);
        btnDisapproved.setManaged(!lbShow);
     
       
    }
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        try {
            switch (lsButton){
                case "btnBrowse":
                        if (oTrans.SearchTransaction(txtSeeks05.getText(), false)){
                            loadIncentives();
                            pnEditMode = oTrans.getEditMode();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                    break;
                    
                case "btnApproved":
                    if (oTrans.CloseTransaction()){
                            MsgBox.showOk("Transaction success approved");
                            loadIncentives();
                            pnEditMode = oTrans.getEditMode();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                    break;
                case "btnDisapproved":
                    if (oTrans.CancelTransaction()){
                            MsgBox.showOk("Transaction success disapproved");
                            loadIncentives();
                            pnEditMode = oTrans.getEditMode();
                        } else 
                            MsgBox.showOk(oTrans.getMessage());
                    break;
               
                case "btnClose":
                    if(ShowMessageFX.OkayCancel(null, "Employee Bank Info", "Do you want to disregard changes?") == true){
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
     private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
             
        try{
           switch (event.getCode()){
            case F3:
                switch (lnIndex){
              
                case 5: /*Search*/
                   if (oTrans.SearchTransaction(txtSeeks05.getText(), false)){
                            loadIncentives();
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
   
    public void initGrid() {
        incIndex02.setStyle("-fx-alignment: CENTER-LEFT");
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
        
         System.out.println("table width = " + tblincetives.getWidth());
//         System.out.println("incIndex01 width = " + (tblincetives.widthProperty().divide(tblincetives.getWidth())));
         
//        incIndex06.set
        /*Assigning data to table*/
        
        tblincetives.setItems(data);
        tblincetives.autosize();
       
        
        
    }
    public void initEmployeeGrid() {
        empIndex08.setStyle("-fx-alignment: CENTER-RIGHT");
        empIndex02.setStyle("-fx-alignment: CENTER-LEFT");
        empIndex06.setStyle("-fx-alignment: CENTER-LEFT");
//        empIndex01.setCellValueFactory(new PropertyValueFactory<Release,String>("empIndex01"));
        incIndex01.setCellFactory(column -> {
                      return new TableCell<TableIncentives, String>() {
                          @Override
                          protected void updateItem(String item, boolean empty) {
                              super.updateItem(item, empty);
                              setText(empty ? "" : getItem().toString());
                              setGraphic(null);

                              TableRow<TableIncentives> currentRow = getTableRow();

                              if (!isEmpty()) {

                                  if(item.contains("Deduction")) 
                                      currentRow.setStyle(" -fx-background-color: -fx-table-cell-border-color, #ff3333;");
                              }
                          }
                      };
                  });
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
        if(oTrans.getMaster("cApprovd1").toString().equalsIgnoreCase("1")){
            Check01.selectedProperty().setValue(true);
        }else{
            Check01.selectedProperty().setValue(false);
        }
        if(oTrans.getMaster("cApprovd2").toString().equalsIgnoreCase("1")){
            Check02.selectedProperty().setValue(true);
        }else{
            Check02.selectedProperty().setValue(false);
        }
        if(oTrans.getMaster(15).toString().equalsIgnoreCase("0")){
        lblStatus.setVisible(true);
            lblStatus.setText("OPEN");
        }else if(oTrans.getMaster(15).toString().equalsIgnoreCase("1")){
            lblStatus.setVisible(true);
            lblStatus.setText("CLOSED");
        }
        else if(oTrans.getMaster(15).toString().equalsIgnoreCase("2")){
            lblStatus.setVisible(true);
            lblStatus.setText("POSTED");
        }
        else if(oTrans.getMaster(15).toString().equalsIgnoreCase("3")){
            lblStatus.setVisible(true);
            lblStatus.setText("CANCELLED");
        }else{
            lblStatus.setVisible(false);
        }
         
         lblStatus.setStyle("-fx-background-color: #ffd9b3;");
         for (lnCtr = 1; lnCtr <= oTrans.getIncentiveCount(); lnCtr++){
             data.add(new TableIncentives(String.valueOf(lnCtr),
                oTrans.getIncentiveInfo(lnCtr, "xInctvNme").toString(),
                priceWithDecimal(Double.valueOf(oTrans.getIncentiveInfo(lnCtr, "nQtyGoalx").toString())),
                priceWithDecimal(Double.valueOf(oTrans.getIncentiveInfo(lnCtr, "nQtyActlx").toString())),
                priceWithDecimal(Double.valueOf(oTrans.getIncentiveInfo(lnCtr, "nAmtGoalx").toString())),
                priceWithDecimal(Double.valueOf(oTrans.getIncentiveInfo(lnCtr, "nAmtActlx").toString())),
                priceWithDecimal(Double.valueOf(oTrans.getIncentiveInfo(lnCtr, "nInctvAmt").toString())),
                oTrans.getIncentiveInfo(lnCtr, "sRemarksx").toString()));

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
                     (oTrans.getDeductionInfo(lnCtr, "sRemarksx").toString() + " (Deduction)"),
                     "",
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
            String lsBankName;
            String lsBankAcct;
            emp_data.clear();
                for(lnCtr  = 1; lnCtr  <= oTrans.getItemCount(); lnCtr ++){
                trans = oTrans.getBankInfo((String) oTrans.getDetail(lnCtr, "sEmployID"));
                
                if(trans != null){
                    lsBankName = trans.getMaster(8).toString();
                    lsBankAcct = trans.getMaster(3).toString();
                }else{
                    lsBankName = "";
                    lsBankAcct = "";
                }
                /*DecimalFormat*/ 
                
                emp_data.add(new Release(String.valueOf(lnCtr),
                        oTrans.getDetail(lnCtr , "xEmployNm").toString(),
                        oTrans.getDetail(lnCtr , "xEmpLevNm").toString(),
                        oTrans.getDetail(lnCtr , "xPositnNm").toString(),
                        oTrans.getDetail(lnCtr , "xSrvcYear").toString(),
                        lsBankName,
                        lsBankAcct,
                        priceWithDecimal((Double)(oTrans.getDetail(lnCtr , "nTotalAmt")))));
               
            }
            initEmployeeGrid();
    }
    private void unloadForm(){
        StackPane myBox = (StackPane) AnchorMainIncentiveConfirmation.getParent();
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
    public static String priceWithDecimal (Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(price);
    }
    public void tblincentives_column(){
         incIndex01.prefWidthProperty().bind(tblincetives.widthProperty().multiply(0.268));
         incIndex02.prefWidthProperty().bind(tblincetives.widthProperty().multiply(0.11));
         incIndex03.prefWidthProperty().bind(tblincetives.widthProperty().multiply(0.11));
         incIndex04.prefWidthProperty().bind(tblincetives.widthProperty().multiply(0.11));
         incIndex05.prefWidthProperty().bind(tblincetives.widthProperty().multiply(0.11));
         incIndex06.prefWidthProperty().bind(tblincetives.widthProperty().multiply(0.268));
         incIndex01.setResizable(false);  
         incIndex02.setResizable(false);  
         incIndex03.setResizable(false);  
         incIndex04.setResizable(false);  
         incIndex05.setResizable(false);  
         incIndex06.setResizable(false);   
        
    }
    public void tblemployee_column(){
         empIndex01.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.03));
         empIndex02.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.167));
         empIndex03.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.14));
         empIndex04.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.14));
         empIndex05.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.07));
         empIndex06.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.14));
         empIndex07.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.129));
         empIndex08.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.167));
         
         empIndex01.setResizable(false);  
         empIndex02.setResizable(false);  
         empIndex03.setResizable(false);  
         empIndex04.setResizable(false);  
         empIndex05.setResizable(false);  
         empIndex06.setResizable(false); 
         empIndex07.setResizable(false); 
         empIndex08.setResizable(false);  
    }
}
