/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.SQLUtil;

/**
 * FXML Controller class
 *
 * @author user
 */
public class FXMLDocumentController implements Initializable, ScreenInterface {
    private GRider oApp;
    
    @FXML
    private Pane btnMin;
    @FXML
    private Pane btnClose;
    @FXML
    private StackPane workingSpace;
    @FXML
    private Menu mnuTransaction;
    @FXML
    private MenuItem mnuEmployeeIncentives;
    @FXML
    private MenuItem mnuIncentiveParameter;
    @FXML
    private MenuItem mnuIncentiveHistory;
    @FXML
    private MenuItem mnuCashCount;
    @FXML
    private MenuItem mnuCashCountHistory;
    @FXML
    private Pane view;
    @FXML
    private MenuItem mnuEmployeeIncentivesBank;
    @FXML
    private MenuItem mnuIncentiveReleasing;
    @FXML
    private MenuItem mnuIncentiveConfirmation;
    
    @FXML
    private MenuItem mnuMCBranch;
    @FXML
    private MenuItem mnuMCArea;
    @FXML
    private MenuItem mnuMPBranch;
    @FXML
    private MenuItem mnuMPArea;
    
    @FXML
    private Label DateAndTime;
    @FXML
    private Label AppUser;
    @FXML
    private Menu mnuFiles;
    @FXML
    private Menu mnuFiles01;
    @FXML
    private Menu mnuFiles02;
    @FXML
    private Menu mnuTransaction11;
    @FXML
    private Menu mnuTransaction111;
    @FXML
    private Menu mnuInventory01;
    @FXML
    private MenuItem mnuInventoryReq;
    @FXML
    private MenuItem mnuInventoryHistory;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setScene(loadAnimate("MainScreenBG.fxml"));
        getTime();
        
        ResultSet name;
        String lsQuery = "SELECT b.sCompnyNm " +
                            " FROM xxxSysUser a" +
                            " LEFT JOIN Client_Master b" +  
                                " ON a.sEmployNo  = b.sClientID" +
                            " WHERE a.sUserIDxx = " + SQLUtil.toSQL(oApp.getUserID());
        name = oApp.executeQuery(lsQuery);
        try {
            if(name.next()){
                AppUser.setText(name.getString("sCompnyNm") + " || " + oApp.getBranchName());
                System.setProperty("user.name", name.getString("sCompnyNm"));   
            }             
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }

    @FXML
    private void handleButtonCloseClick(MouseEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleButtonMinimizeClick(MouseEvent event) {
        Stage stage = (Stage) btnMin.getScene().getWindow();
        stage.setIconified(true);
    }
    
    private AnchorPane loadAnimate(String fsFormName){
        ScreenInterface fxObj = getController(fsFormName);
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
    
    private ScreenInterface getController(String fsValue){
        System.out.println(fsValue);
        switch (fsValue){
            case "MainScreenBG.fxml":
                return new MainScreenBGController();
            case "EmployeeIncentives.fxml":
                return new EmployeeIncentivesController();
            case "EmployeeBankInfo.fxml":
                return new EmployeeBankInfoController();
            case "IncentiveParameter.fxml":
                return new IncentiveParameterController();
            case "AddIncentives.fxml":
                return new AddIncentivesController();
            case "IncentiveReleasing.fxml":
                return new IncentiveReleasingController();
            case "IncentiveConfirmation.fxml":
                return new IncentiveConfirmationController();
            case "McBranchPerformance.fxml":
                return new McBranchPerformanceController();
            case "McAreaPerformance.fxml":
                return new McAreaPerformanceController();
            case "MpBranchPerformance.fxml":
                return new MpBranchPerformanceController();
            case "MpAreaPerformance.fxml":
                return new MpAreaPerformanceController();
            case "IncentiveHistory.fxml":
                return new IncentiveHistoryController();
            case "CashCount.fxml":
                return new CashCountController();
            case "CashCountHistory.fxml":
                return new CashCountHistoryController();
            case "InventoryHistory.fxml":
                return new InventoryHistoryController();
            default:
                return null;
        }
    }
 
    private void setScene(AnchorPane foPane){
        workingSpace.getChildren().clear();
        workingSpace.getChildren().add(foPane);
    }

    @FXML
    private void mnuEmployeeIncentivesClick(ActionEvent event) {
        setScene(loadAnimate("EmployeeIncentives.fxml"));
    }
    @FXML
    private void mnuEmployeeBankInfoClick(ActionEvent event) {
        setScene(loadAnimate("EmployeeBankInfo.fxml"));
    }
    
    @FXML
    private void mnuIncentiveParameterClick(ActionEvent event) {
        setScene(loadAnimate("IncentiveParameter.fxml"));
    } 
    @FXML
    private void mnuIncentiveReleasingClick(ActionEvent event) {
        setScene(loadAnimate("IncentiveReleasing.fxml"));
    } 
    @FXML
    private void mnuIncentiveConfirmationClick(ActionEvent event) {
        setScene(loadAnimate("IncentiveConfirmation.fxml"));
    }
    @FXML
    private void mnuMCBranchClick(ActionEvent event) {
        setScene(loadAnimate("McBranchPerformance.fxml"));
    }
    @FXML
    private void mnuMCAreaClick(ActionEvent event) {
        setScene(loadAnimate("McAreaPerformance.fxml"));
    }
    @FXML
    private void mnuMPBranchClick(ActionEvent event) {
        setScene(loadAnimate("MpBranchPerformance.fxml"));
    }
    @FXML
    private void mnuMPAreaClick(ActionEvent event) {
        setScene(loadAnimate("MpAreaPerformance.fxml"));
    }
    
    @FXML
    private void mnuIncentiveHistoryClick(ActionEvent event) {
        setScene(loadAnimate("IncentiveHistory.fxml"));
    }
    @FXML
    private void mnuCashCountClick(ActionEvent event) {
        setScene(loadAnimate("CashCount.fxml"));
    }
    @FXML
    private void mnuCashCountHistoryClick(ActionEvent event) {
        setScene(loadAnimate("CashCountHistory.fxml"));
    }

    @FXML
    private void mnuInventoryHistoryClick(ActionEvent event) {
        setScene(loadAnimate("InventoryHistory.fxml"));
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
        
        DateAndTime.setText(formattedDate+ " || " + formattedTime);
        
        }),
         new KeyFrame(Duration.seconds(1))
        );
        
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    
 
}
