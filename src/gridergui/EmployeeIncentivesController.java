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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agent.MsgBox;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.Incentive; 
import java.util.Calendar;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author user
 */
public class EmployeeIncentivesController implements Initializable, ScreenInterface {
    private GRider oApp;
    private Incentive oTrans;
    private final String pxeModuleName = "EmployeeIncentivesController";
    private String psDescript = "";
    double xOffset = 0;
    double yOffset = 0;
    
    private int pnIndex = -1;
    private int pdIndex = -1;
    private int pnOldRow = -1;
    private int pnRow = 0;
    
    private int pnEditMode;
    private int pnSubItems = 0;
    
    private boolean pbLoaded = false;
    private String psOldRec;
    int x = 5;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnAddIncentives;
    @FXML
    private Button btnAddDeductions;
    @FXML
    private AnchorPane AnchorMain;
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
    private TableView tblemployee;
    @FXML
    private TableView tblincetives;
    @FXML
    private TextField txtField11;
    @FXML
    private TextField txtField12;
    @FXML
    private TableColumn index01;
    @FXML
    private TableColumn index02;
    @FXML
    private AnchorPane searchBar;
    
    /**
     * Initializes the controller class.
     */
    public String  transNo = "";
    private ObservableList<TableModel> data = FXCollections.observableArrayList();
    @FXML
    private Button btnBrowse1;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //set the main anchor pane fit the size of its parent anchor pane
//        AnchorMain.setTopAnchor(AnchorMain, 0.0);
//        AnchorMain.setBottomAnchor(AnchorMain, 0.0);
//        AnchorMain.setLeftAnchor(AnchorMain, 0.0);
//        AnchorMain.setRightAnchor(AnchorMain, 0.0);   
         
        btnNew.setOnAction(this::cmdButton_Click);
        btnAddIncentives.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        
        
        //initialize class
        oTrans  = new Incentive(oApp, oApp.getBranchCode(), false);
        oTrans.setWithUI(true);
        
        
        /*Add listener to text fields*/
        txtField11.focusedProperty().addListener(txtField_Focus);
        txtField3.focusedProperty().addListener(txtField_Focus);
        txtField4.focusedProperty().addListener(txtField_Focus);
        
        txtField4.setOnKeyPressed(this::txtField_KeyPressed);
        
        pnEditMode = EditMode.UNKNOWN;
        pbLoaded = true;
    } 
        private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();
        int lnIndex = Integer.parseInt(((TextField)event.getSource()).getId().substring(8,9));
        String lsValue = txtField.getText();
        try {
            switch (event.getCode()) {
                case F3:
                    switch (lnIndex){
                        case 4:
                            if(oTrans.searchDepartment(txtField4.getText(), false)){
                                txtField4.setText((String)oTrans.getMaster("xDeptName"));
                                //assertEquals("Management Information System", (String) oTrans.getMaster("xDeptName"));
                                
                                 data.clear();
                                for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
                                    if(oTrans.getMaster("xDeptName").toString().equalsIgnoreCase(txtField4.getText())){
                                       data.add(new TableModel(String.valueOf(lnCtr),
                                       oTrans.getDetail(lnCtr, "xEmployNm").toString()));

                                    }

                                }
                                int lnRow = oTrans.getItemCount();
//                                oTrans.setMas
                                initGrid();
                            }
                            break;
                        }
                case ENTER:
                    switch (lnIndex){
                        case 4:
                            if(oTrans.searchDepartment(txtField4.getText(), false)){
                                txtField4.setText((String)oTrans.getMaster("xDeptName"));
                                //assertEquals("Management Information System", (String) oTrans.getMaster("xDeptName"));
                                
                                 data.clear();
                                for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
                                    if(oTrans.getMaster("xDeptName").toString().equalsIgnoreCase(txtField4.getText())){
                                       data.add(new TableModel(String.valueOf(lnCtr),
                                       oTrans.getDetail(lnCtr, "xEmployNm").toString()));

                                    }

                                }
                                int lnRow = oTrans.getItemCount();
//                                oTrans.setMas
                                initGrid();
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
            case DOWN:
                CommonUtils.SetNextFocus(txtField);
                break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
            }
    } 
    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
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
        
        
    private void unloadForm(){
        AnchorPane myBox = (AnchorPane) AnchorMain.getParent();
        myBox.getChildren().clear();
    }
    
    private void loadIncentiveDetail(){
        try {
            Stage stage = new Stage();
            
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("AddIncentives.fxml"));

            AddIncentivesController loControl = new AddIncentivesController();
            loControl.setGRider(oApp);
            loControl.setIncentiveObject(oTrans);
            
            fxmlLoader.setController(loControl);
            
            //loControl.maintransaction(txtField1.getText());
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
        } catch (IOException e) {
            e.printStackTrace();
            MsgBox.showOk(e.getMessage());
            System.exit(1);
        }
    }
    
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
            switch (lsButton){
                case "btnNew": {
                    try {
                        //create new transaction
                        if (oTrans.NewTransaction() ){
                            clearFields();
                            loadMaster();
                            getTime();
                            txtField11.setDisable(true);
                            txtField12.setDisable(true);
                            //    initGrid();
                        } else
                            MsgBox.showOk(oTrans.getMessage());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        MsgBox.showOk(e.getMessage());
                    }
                }
                break;
                case "btnAddIncentives":
                    loadIncentiveDetail();
                    
                    //MsgBox.showOk(txtField1.getText());
                    //reload detail
                    break;
                case "btnClose":
                    Stage stage = new Stage();
                    stage.close();
                    break;
                case "btnSave":
                    break;
            }
    } 

    private void loadMaster() throws SQLException {
        
        txtField1.setText((String) oTrans.getMaster(1));
        
        txtField3.setText((String)  oApp.getBranchName());
       //txtField3.setText("LGK Dagupan - Multi");
        if (!txtField3.getText().equalsIgnoreCase("GMC Dagupan - Honda") || (!txtField3.getText().equalsIgnoreCase("GMC Anolid "
                + ""))){
            
            txtField4.setDisable(false);
            
            for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
                data.add(new TableModel(String.valueOf(lnCtr),
                oTrans.getDetail(lnCtr, "xEmployNm").toString()));
                System.out.print(oTrans.getDetail(lnCtr, "sEmployID"));
                System.out.print("\t");
                System.out.print(oTrans.getDetail(lnCtr, "nTotalAmt"));
                System.out.print("\t");
                System.out.print(oTrans.getDetail(lnCtr, "xEmployNm"));
                System.out.println("");
            }       
            initGrid();
        }

        pnRow = 0;
        pnOldRow = 0;

        psOldRec = txtField1.getText();
    }
    
    private void clearFields(){
        txtField1.setText("");
    }
    private void getMaster(int fnIndex) throws SQLException{
        switch(fnIndex){
            case 3:
                txtField3.setText(CommonUtils.xsDateMedium((Date)oTrans.getMaster(fnIndex)));
                break;
        }
    }    
    private void initGrid() {
        index01.setStyle("-fx-alignment: CENTER;");
        index02.setStyle("-fx-alignment: CENTER-LEFT;");
        
        index01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        index02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblemployee.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblemployee.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
               
            });
        });
        tblemployee.setItems(data);
        
    }
    
    private void getTime(){
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {            
        Calendar cal = Calendar.getInstance();
        int second = cal.get(Calendar.SECOND);        
        String temp = "" + second;
        
        Date date = new Date();
        String strTimeFormat = "hh:mm:";
        String strDateFormat = "MMMM dd, yyyy";
        String secondFormat = "ss";
        
        DateFormat timeFormat = new SimpleDateFormat(strTimeFormat + secondFormat);
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        
        String formattedTime= timeFormat.format(date);
        String formattedDate= dateFormat.format(date);
        
        txtField2.setText(formattedDate);
        }),
         new KeyFrame(Duration.seconds(1))
        );
        
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
    

}