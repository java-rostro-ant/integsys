/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gridergui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import static junit.framework.Assert.fail;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.LMasDetTrans;
import org.rmj.fund.manager.base.Panalo;

/**
 * FXML Controller class
 *
 * @author User
 */
public class PanaloInfoController implements Initializable, ScreenInterface {

    @FXML
    private AnchorPane AnchorMainPanaloInfo,searchBar;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnBrowse,btnNew,btnSave,btnUpdate,btnCancel,btnActivate,btnDeactivate,
            btnClose;
    @FXML
    private TableView tblClients;

    @FXML
    private TableColumn clientsIndex01,clientsIndex02,clientsIndex03;
    @FXML
    private Pagination pagination;
    @FXML
    private TextField txtField01,txtField02,txtField03,txtField04,txtSeeks05;
    @FXML
    private Label lblStatus;
    
    private GRider oApp;
    private Panalo oTrans;
    private LMasDetTrans oListener;
    
    private boolean pbLoaded = false;
    private int pnRow = -1;
    private int oldPnRow = -1;
    private int pnEditMode;
    private int pagecounter;
    private String oldTransNo = "";
    private String TransNo = "";
    
    private static final int ROWS_PER_PAGE = 30;
  
    private FilteredList<PanaloInfoModel> filteredData;

    private final ObservableList<PanaloInfoModel> panaloinfo_data= FXCollections.observableArrayList();
 
        private Stage getStage(){
	return (Stage) txtField01.getScene().getWindow();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        oTrans = new Panalo(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        pbLoaded = true;
        loadClient();
    }    

    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }

        private void loadClient(){
        int lnCtr;
        try {
            panaloinfo_data.clear();
           
            if (oTrans.LoadList("")){
                for (lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
                    panaloinfo_data.add(new PanaloInfoModel(String.valueOf(lnCtr),
                            (String) oTrans.getMaster("sTransNox"),
                            oTrans.getMaster("dTransact").toString(),
                            oTrans.getMaster("sPanaloCD").toString(),
                            oTrans.getMaster("sAcctNmbr").toString(),
                            oTrans.getMaster("sSourceNo").toString(),
                            oTrans.getMaster("nAmountxx").toString(),
                            oTrans.getMaster("sDescript").toString(),
                            oTrans.getMaster("nItemQtyx").toString(),
                            (String) oTrans.getMaster("nRedeemxx"),
                            (String) oTrans.getMaster("dRedeemxx"),
                            (String) oTrans.getMaster("sUserIDxx"),
                            (String) oTrans.getMaster(""),
                            (String) oTrans.getMaster(""),
                            oTrans.getMaster("dExpiryDt").toString(),
                            (String) oTrans.getMaster("cTranStat"),
                            (String) oTrans.getMaster("sUserName")));
     
                   
                }
                
            }
                 loadTab();

        } catch (SQLException ex) {
            System.out.println("SQLException" + ex.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("NullPointerException" + ex.getMessage());
        } catch (DateTimeException ex) {
//            MsgBox.showOk(ex.getMessage());
            System.out.println("DateTimeException" + ex.getMessage());
        } 
    }
    private void initGrid() {
        
        clientsIndex01.setStyle("-fx-alignment: CENTER;");
        clientsIndex02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        clientsIndex03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");

       
        clientsIndex01.setCellValueFactory(new PropertyValueFactory<>("PanaloInfoIndex01"));
        clientsIndex02.setCellValueFactory(new PropertyValueFactory<>("PanaloInfoIndex01"));
        clientsIndex03.setCellValueFactory(new PropertyValueFactory<>("PanaloInfoIndex01"));

        
        tblClients.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblClients.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        filteredData = new FilteredList<>(panaloinfo_data, b -> true);
        autoSearch(txtSeeks05);
        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<PanaloInfoModel> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        // 	  Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tblClients.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tblClients.setItems(sortedData);
    }
    private void autoSearch(TextField txtField){
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        boolean fsCode = true;
        txtField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(clients-> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();
                if(lnIndex == 10){
                    return (clients.getPanaloInfoIndex01().toLowerCase().contains(lowerCaseFilter)); // Does not match.   
                }else {
                    return (clients.getPanaloInfoIndex01().toLowerCase().contains(lowerCaseFilter)); // Does not match.
                }
            });
            changeTableView(0, ROWS_PER_PAGE);
        });
        if(lnIndex == 98){  
        }
        if(lnIndex == 99){  
        }
        loadTab();
} 
    private void loadTab(){
                int totalPage = (int) (Math.ceil(panaloinfo_data.size() * 1.0 / ROWS_PER_PAGE));
                pagination.setPageCount(totalPage);
                pagination.setCurrentPageIndex(0);
                changeTableView(0, ROWS_PER_PAGE);
                pagination.currentPageIndexProperty().addListener(
                        (observable, oldValue, newValue) -> changeTableView(newValue.intValue(), ROWS_PER_PAGE));
            
    }   
    private void changeTableView(int index, int limit) {
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, panaloinfo_data.size());

            int minIndex = Math.min(toIndex, filteredData.size());
            SortedList<PanaloInfoModel> sortedData = new SortedList<>(
                    FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
            sortedData.comparatorProperty().bind(tblClients.comparatorProperty());
            tblClients.setItems(sortedData); 
}
            @FXML
    void tblClients_Clicked(MouseEvent event) {
pnRow = tblClients.getSelectionModel().getSelectedIndex(); 
        pagecounter = pnRow + pagination.getCurrentPageIndex() * ROWS_PER_PAGE;
           if (pagecounter >= 0){
             if(event.getClickCount() > 0){

//                clear();
                        getSelectedItem(filteredData.get(pagecounter).getPanaloInfoIndex02());


    tblClients.setOnKeyReleased((KeyEvent t)-> {
                KeyCode key = t.getCode();
                switch (key){
                    case DOWN:
                        pnRow = tblClients.getSelectionModel().getSelectedIndex();
                        pagecounter = pnRow + pagination.getCurrentPageIndex() * ROWS_PER_PAGE;
                        if (pagecounter == tblClients.getItems().size()) {
                            pagecounter = tblClients.getItems().size();
//                            getSelectedItem(filteredData.get(pagecounter).getPanaloInfoIndex02());
                        }else {
//                            int y = 1;
//                            pnRow = pnRow + y;
//                            getSelectedItem(filteredData.get(pagecounter).getPanaloInfoIndex02());
                        }
                        break;
                    case UP:
                        pnRow = tblClients.getSelectionModel().getSelectedIndex();
                        pagecounter = pnRow + pagination.getCurrentPageIndex() * ROWS_PER_PAGE;
//                        getSelectedItem();
                        break;
                    default:
                        return; 
                }
            });
        }  
        }      
    }
        private void getSelectedItem(String TransNo){
            oldTransNo = TransNo;
            try {
            if (oTrans.OpenRecord(TransNo)){
                txtField01.setText(panaloinfo_data.get(pagecounter).getPanaloInfoIndex01());
                txtField02.setText(panaloinfo_data.get(pagecounter).getPanaloInfoIndex01());
                txtField03.setText(panaloinfo_data.get(pagecounter).getPanaloInfoIndex01());
                txtField04.setText(panaloinfo_data.get(pagecounter).getPanaloInfoIndex01());
                pnEditMode = EditMode.UNKNOWN;
//                initButton(pnEditMode);
                         oldPnRow = pagecounter;   
            }
              } catch (SQLException e) {
            fail(e.getMessage());
        }   
    
        }
    private void unloadForm(){
        StackPane myBox = (StackPane) AnchorMainPanaloInfo.getParent();
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
}
