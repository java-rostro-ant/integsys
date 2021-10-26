/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import gridergui.MainScreenBGController;
import gridergui.ScreenInterface;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.json.simple.JSONObject;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agent.MsgBox;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.Incentive;


/**
 * FXML Controller class
 *
 * @author user
 */
public class EmployeeIncentivesController implements Initializable, ScreenInterface {
    private GRider oApp;
    private Incentive oTrans;
    
    double xOffset = 0;
    double yOffset = 0;
    
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
    private TableView<?> tblemployee;
    @FXML
    private TableView<?> tblincetives;
    
    /**
     * Initializes the controller class.
     */
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
        
        pnEditMode = EditMode.UNKNOWN;
        /*Add listener to text fields*/

    } 
    
    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
    private int pnIndex = -1;
    private int pnEditMode;
    private int pnRow = 0;
    private int pnSubItems = 0;
    private boolean pbLoaded = false;
    private String psOldRec;
    private String psBarcode = "";
    private String psDescript = "";
    
    
    
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
            case "btnNew": //create new transaction
                try {
                    if (oTrans.NewTransaction()){
                        loadRecord();
                        pnEditMode = oTrans.getEditMode();
                        //todo:
                        //loadMaster(); ->>load the field from master table
                        //loadDetail(); ->>load the employees with the corresponding incentive amount
                        //loadDetailEmployeeAllocation(); ->>load incentive allocation                
//                        oTrans.displayMasFields();
//                        oTrans.displayDetFields();
//                        oTrans.displayDetAllocFields();
//                        oTrans.displayDetAllocEmpFields();
//                        oTrans.displayDetDeductionAllocFields();
//                        oTrans.displayDetDeductionAllocEmpFields();
                        
                        
                        
//                        for (int lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
//                            System.out.print(oTrans.getDetail(lnCtr, "sEmployID"));
//                            System.out.print("\t");
//                            System.out.print(oTrans.getDetail(lnCtr, "nTotalAmt"));
//                            System.out.print("\t");
//                            System.out.print(oTrans.getDetail(lnCtr, "xEmployNm"));
//                            System.out.println("");
//                        }
                    } else 
                        MsgBox.showOk(oTrans.getMessage());
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                    MsgBox.showOk(e.getMessage());
                }
                break;
            case "btnAddIncentives":
                loadIncentiveDetail();
                //reload detail
                break;
                
            case "btnClose":
                MsgBox.showOk("hello");
                unloadForm();
                break;
        }
    } 
    private void loadRecord() throws SQLException{
        txtField1.setText((String) oTrans.getMaster(1));
        
        psOldRec = txtField1.getText();
        pnEditMode = EditMode.READY;
    }
    
    private void getMaster(int fnIndex) throws SQLException{
        switch(fnIndex){
        case 2:
           txtField1.setText((String)oTrans.getMaster(fnIndex));
            break;
        
        }
    }
}
