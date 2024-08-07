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
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    private String transNox = "";
    
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
    private TextField txtSeeks06;
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
    private Button btnUpdate;
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
    private final ObservableList<IncentiveConfirmationModel> emp_data = FXCollections.observableArrayList();
   @FXML
    private Label lblHeader;    
    @FXML
    private AnchorPane searchBar;
    
    public void setTransaction(String fsValue){
        transNox = fsValue;
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
                        txtSeeks05.setText((String) foValue);
                        txtField01.setText((String) foValue); 
                        transNox = (String) foValue;
                        setTransaction((String) foValue);
                        break;
                    case 2:
                        txtField02.setText((String) foValue); break;
                    case 4:
                        txtField04.setText((String) foValue); break;
                    case 5:
                        txtField05.setText((String) foValue); break;
                    case 17:
                        txtField03.setText((String) foValue); 
                        break;

                    case 16:
                        txtSeeks06.setText((String) foValue);
                        txtField16.setText((String) foValue); break;
                }
            }

            @Override
            public void DetailRetreive(int fnRow, int fnIndex, Object foValue) {
              System.out.println("loaded");
            }
        };
        oTrans = new Incentive(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setTranStat(10);
        oTrans.setWithUI(true);
         
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        btnApproved.setOnAction(this::cmdButton_Click);
        btnDisapproved.setOnAction(this::cmdButton_Click);
        btnUpdate.setOnAction(this::cmdButton_Click);
        tblincentives_column();
        tblemployee_column();
        txtSeeks05.setOnKeyPressed(this::txtField_KeyPressed); 
        txtSeeks06.setOnKeyPressed(this::txtField_KeyPressed); 
        try { 
            if(getTransNox().isEmpty()){
                pnEditMode = EditMode.UNKNOWN;
                initButton(pnEditMode);
            } else {
                if (oTrans.SearchTransaction(getTransNox(), true)){
                    loadIncentives();
                    pnEditMode = EditMode.READY;
                    initButton(pnEditMode);
                } else
                    ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeIncentivesController.class.getName()).log(Level.SEVERE, null, ex);
        }  
        pbLoaded = true;
        
    }    
    public void setTransNox(String transVal){
        transNox = transVal;
    }  
    public String getTransNox(){
        return transNox;
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
        Check01.setDisable(true);
        Check02.setDisable(true);
        txtField01.setDisable(true);
        txtField02.setDisable(true);  
    }
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        try {
            switch (lsButton){
                case "btnBrowse":
                    if (oTrans.SearchTransaction(txtSeeks05.getText(), true)){
                        loadIncentives();
                    } else if(oTrans.SearchTransaction(txtSeeks06.getText(), false)){
                        loadIncentives();
                    } else 
                        ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);   
                    break;
                case "btnApproved": 
                    if (oTrans.CloseTransaction()){
                            ShowMessageFX.Warning(getStage(), "Transaction successfully approved.","Warning", null);
                            clearFields();
                        } else 
                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                    break;
                case "btnDisapproved":
                    if (oTrans.CancelTransaction()){
                            ShowMessageFX.Warning(getStage(),"Transaction successfully disapproved.", "Warning", null);
                            clearFields();
                        } else 
                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                    break;
                case "btnUpdate":
                    loadIncentives(); 
                    if(getTransNox() != null || !getTransNox().isEmpty()){
                        if("1".equals((String) oTrans.getMaster("cTranStat")) && !oApp.getDepartment().equals("034")){
                            ShowMessageFX.Warning(getStage(),"Only CM can update confirmed transactions.", "Warning", null);
                            return;
                        }
                        
                        if ("1".equals((String) oTrans.getMaster("cApprovd2"))){
                            ShowMessageFX.Warning(getStage(),"This transaction was already CM Confirmed. Unable to update.", "Warning", null);
                            return;
                        }
                        
                        pnEditMode = oTrans.getEditMode();
                        loadUpdate();
                    }else{
                        ShowMessageFX.Warning(getStage(),"No record was loaded!", "Warning", null);
                    }
                    break;
                case "btnClose":
                    if(ShowMessageFX.OkayCancel(null, "Confirm", "Are you sure, do you want to close?") == true){
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
     private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        System.out.println(lnIndex);
        try{
           switch (event.getCode()){
            case F3:
            case ENTER:
                switch (lnIndex){
                    case 5: /*Search*/
                        if (oTrans.SearchTransaction(txtSeeks05.getText(), true)){
                                loadIncentives();
                                pnEditMode = oTrans.getEditMode();
                            } else 
                                ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                        break;
                    case 6: /*Search*/
                        if (oTrans.SearchTransaction(txtSeeks06.getText(), false)){
                                loadIncentives();
                                pnEditMode = oTrans.getEditMode();
                            } else 
                                ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                        break;
                }
            
        } 
        }catch(SQLException e){
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
   
    public void initGrid() {
        incIndex02.setStyle("-fx-alignment: CENTER-LEFT");
        incIndex03.setStyle("-fx-alignment: CENTER-RIGHT");
        incIndex04.setStyle("-fx-alignment: CENTER-RIGHT");
        incIndex05.setStyle("-fx-alignment: CENTER-RIGHT");
        incIndex06.setStyle("-fx-alignment: CENTER-RIGHT");
        incIndex01.setCellValueFactory(new PropertyValueFactory<IncentiveConfirmationModel,String>("incindex02"));
        incIndex02.setCellValueFactory(new PropertyValueFactory<IncentiveConfirmationModel,String>("incindex03"));
        incIndex03.setCellValueFactory(new PropertyValueFactory<IncentiveConfirmationModel,String>("incindex04"));
        incIndex04.setCellValueFactory(new PropertyValueFactory<IncentiveConfirmationModel,String>("incindex05"));
        incIndex05.setCellValueFactory(new PropertyValueFactory<IncentiveConfirmationModel,String>("incindex06"));
        incIndex06.setCellValueFactory(new PropertyValueFactory<IncentiveConfirmationModel,String>("incindex07"));
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
        empIndex05.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 0;");
        empIndex06.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 0;");
        empIndex07.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 0;");
        empIndex08.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 0;");
        empIndex02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        empIndex03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        empIndex04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");

        empIndex01.setCellValueFactory(new PropertyValueFactory<IncentiveConfirmationModel,String>("empIndex01"));
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
        empIndex02.setCellValueFactory(new PropertyValueFactory<IncentiveConfirmationModel,String>("empIndex02"));
        empIndex03.setCellValueFactory(new PropertyValueFactory<IncentiveConfirmationModel,String>("empIndex03"));
        empIndex04.setCellValueFactory(new PropertyValueFactory<IncentiveConfirmationModel,String>("empIndex04"));
        empIndex05.setCellValueFactory(new PropertyValueFactory<IncentiveConfirmationModel,String>("empIndex05"));
        empIndex06.setCellValueFactory(new PropertyValueFactory<IncentiveConfirmationModel,String>("empIndex06"));
        empIndex07.setCellValueFactory(new PropertyValueFactory<IncentiveConfirmationModel,String>("empIndex07"));
        empIndex08.setCellValueFactory(new PropertyValueFactory<IncentiveConfirmationModel,String>("empIndex08"));
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
        setTransaction((String)oTrans.getMaster(1));
        transNox = (String)oTrans.getMaster(1);
         
        System.out.println("INCENTIVES");
        txtField01.setText((String)oTrans.getMaster(1));
        txtSeeks05.setText((String)oTrans.getMaster(1));
        txtSeeks06.setText((String)oTrans.getMaster(16));
        txtField02.setText(oTrans.getMaster(2).toString());
        txtField03.setText((String)oTrans.getMaster(17));
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
                oTrans.getIncentiveInfo(lnCtr, "sRemarksx").toString(),
                oTrans.getIncentiveInfo(lnCtr, "sInctveCD").toString()));

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
                     oTrans.getDeductionInfo(lnCtr, "nDedctAmt").toString(),
                     "",
                     ""));

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
                
                /*DecimalFormat*/ 
                
                emp_data.add(new IncentiveConfirmationModel(String.valueOf(lnCtr),
                        oTrans.getDetail(lnCtr , "xEmployNm").toString(),
                        oTrans.getDetail(lnCtr , "xEmpLevNm").toString(),
                        oTrans.getDetail(lnCtr , "xPositnNm").toString(),
                        oTrans.getDetail(lnCtr , "xSrvcYear").toString(),
                        (CommonUtils.NumberFormat((Number)oTrans.getDetail(lnCtr, "xIncentve"), "#,##0.00")),
                        (CommonUtils.NumberFormat((Number)oTrans.getDetail(lnCtr, "xDeductnx"), "#,##0.00")),
                        (CommonUtils.NumberFormat((Number)oTrans.getDetail(lnCtr, "nTotalAmt"), "#,##0.00"))));
               
            }
            initEmployeeGrid();
    }
    private void unloadForm(){
        StackPane myBox = (StackPane) AnchorMainIncentiveConfirmation.getParent();
        myBox.getChildren().clear();
        myBox.getChildren().add(getScene("MainScreenBG.fxml"));
        
    }
    
    private void loadUpdate(){
        StackPane myBox = (StackPane) AnchorMainIncentiveConfirmation.getParent();
        myBox.getChildren().clear();
        myBox.getChildren().add(setScene());
          
    }
    private AnchorPane setScene(){
        
        try {    
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("EmployeeIncentives.fxml"));

            EmployeeIncentivesController loControl = new EmployeeIncentivesController();
            loControl.setGRider(oApp);
            loControl.setState(true);
            loControl.setTransaction((String)oTrans.getMaster(1));
            
            fxmlLoader.setController(loControl);
            
            //load the main interface
                
          AnchorPane root;
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
        } catch (SQLException ex) {
                Logger.getLogger(IncentiveConfirmationController.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        return null;
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
         empIndex01.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.035));
         empIndex02.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.210));
         empIndex03.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.195));
         empIndex04.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.195));
         empIndex05.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.08));
         empIndex06.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.09));
         empIndex07.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.09));
         empIndex08.prefWidthProperty().bind(tblemployee.widthProperty().multiply(0.09));
         
    }
    
     @FXML
    private void tblIncentives_Clicked(MouseEvent event) {
        try {
            
            pnRow = tblincetives.getSelectionModel().getSelectedIndex(); 
            if(pnRow >= 0){
            TableIncentives ti = (TableIncentives) tblincetives.getItems().get(pnRow);
            
            if(ti.getIncindex02().contains("Deduction")){
                
                AddDeductionController.setData(ti);
                loadDeductionDetail(pnRow + 1 - (oTrans.getIncentiveCount())); 
                
            } else{
                loadIncentiveDetail((String) oTrans.getIncentiveInfo(pnRow + 1, "sInctveCD"), pnRow + 1); 
            }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            ShowMessageFX.Warning(getStage(),ex.getMessage(), "Warning", null);
        }
    }
    private void loadDeductionDetail(int fnRow) throws SQLException{
        try {
            Stage stage = new Stage();
            
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("AddDeduction.fxml"));

            AddDeductionController loControl1 = new AddDeductionController();
            loControl1.setGRider(oApp);
            loControl1.setDeductionObject(oTrans);
            loControl1.setState(true);
            loControl1.setTableRows(fnRow);
            
            fxmlLoader.setController(loControl1);
            
            //load the main interface
            Parent parent = fxmlLoader.load();
                
            parent.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });
            
            //set the main interface as the scene
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("");
            stage.showAndWait();
            
            loadIncentives();
        } catch (IOException e) {
            e.printStackTrace();
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
            System.exit(1);
        }
    }
    private void loadIncentiveDetail(String fsCode, int fnRow) throws SQLException{
        try {
            Stage stage = new Stage();
            
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("AddIncentives.fxml"));

            AddIncentivesController loControl = new AddIncentivesController();
            loControl.setGRider(oApp);
            loControl.setIncentiveObject(oTrans);
            loControl.setIncentiveCode(fsCode);
            loControl.setState(true);
            loControl.setTableRow(fnRow);
            
            fxmlLoader.setController(loControl);
            
            //load the main interface
            Parent parent = fxmlLoader.load();
                
            parent.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });
            
            //set the main interface as the scene
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("");
            stage.showAndWait();
            
            loadIncentives();
        } catch (IOException e) {
            e.printStackTrace();
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
            System.exit(1);
        }
    }
    
    private void clearFields(){
        txtField01.setText("");
        txtField02.setText("");
        txtField03.setText("");
        txtField04.setText("");
        txtField05.setText("");
        txtField16.setText("");
        txtSeeks05.setText("");
        txtSeeks06.setText("");
        Check01.selectedProperty().setValue(false);
        Check02.selectedProperty().setValue(false);
        lblStatus.setVisible(false);
        data.clear();
        emp_data.clear();
        oTrans = new Incentive(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setTranStat(10);
        oTrans.setWithUI(true);
        pnEditMode = EditMode.UNKNOWN;
    }
}