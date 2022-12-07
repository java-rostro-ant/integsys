/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gridergui;

import com.itextpdf.text.pdf.qrcode.Mode;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import static gridergui.DeptIncentivesHistController.dateToWord;
import java.io.IOException;
import static java.lang.Boolean.FALSE;
import java.net.URL;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
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
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
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
    private Button btnPost,btnVoid,btnRefresh,btnClose;
    @FXML
    private TableView tblClients,tblRedemption;

    @FXML
    private TableColumn clientsIndex01,clientsIndex02,clientsIndex03;
    @FXML
    private TableColumn redeemIndex01,redeemIndex02,redeemIndex03,redeemIndex04,redeemIndex05,redeemIndex06;
    @FXML
    private Pagination pagination;
    @FXML
    private TextField txtField01,txtField02,txtField03,txtField04,txtField05,txtField06,txtField07,txtField08,txtSeeks99,txtSeeks98;
    @FXML
    private MenuButton menuStatus;
    @FXML
    private CheckMenuItem itemStat01,itemStat02,itemStat03;

    
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
 
    private final ObservableList<PanaloInfoRedeemModel> pinfoRedeeem_data= FXCollections.observableArrayList();
 
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
        pnEditMode = EditMode.UNKNOWN;
        btnPost.setOnAction(this::cmdButton_Click);
        btnVoid.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        btnRefresh.setOnAction(this::cmdButton_Click);
        pagination.setPageFactory(this::createPage); 
        
        
    }    
     private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, panaloinfo_data.size());
        if(panaloinfo_data.size()>0){
           tblClients.setItems(FXCollections.observableArrayList(panaloinfo_data.subList(fromIndex, toIndex))); 
        }
        return tblClients;
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
                            (String) oTrans.getDetail(lnCtr,"sTransNox"),
                            dateToWord(oTrans.getDetail(lnCtr,"dTransact").toString()),
                            oTrans.getDetail(lnCtr,"sPanaloDs").toString(),
                            oTrans.getDetail(lnCtr,"sAcctNmbr").toString(),
                            oTrans.getDetail(lnCtr,"sSourceNo").toString(),
                            oTrans.getDetail(lnCtr,"nAmountxx").toString(),
                            oTrans.getDetail(lnCtr,"sDescript").toString(),
                            oTrans.getDetail(lnCtr,"nItemQtyx").toString(),
                            oTrans.getDetail(lnCtr,"nRedeemxx").toString(),
                            (String) oTrans.getDetail(lnCtr,"dRedeemxx"),
                            (String) oTrans.getDetail(lnCtr,"sUserIDxx"),
                            "",
                            "",
                            oTrans.getDetail(lnCtr,"dExpiryDt").toString(),
                            oTrans.getDetail(lnCtr,"cTranStat").toString(),
                            (String) oTrans.getDetail(lnCtr,"sUserName")));
                }
                initGrid();
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
        private void loadRedeemDetail(String TransNo){
        int lnCtr;
        try {
            pinfoRedeeem_data.clear();
            if (oTrans.LoadRedemption(TransNo)){
                for (lnCtr = 1; lnCtr <= oTrans.getItemCount(); lnCtr++){
                    pinfoRedeeem_data.add(new PanaloInfoRedeemModel(String.valueOf(lnCtr),
                            dateToWord(oTrans.getRedeem(lnCtr,"dRedeemxx").toString()),
                            oTrans.getRedeem(lnCtr,"sItemCode").toString(),
                            oTrans.getRedeem(lnCtr,"sDescript").toString(),
                            oTrans.getRedeem(lnCtr,"nItemQtyx").toString(),
                            oTrans.getRedeem(lnCtr,"sRemarksx").toString(),
                            oTrans.getRedeem(lnCtr,"cTranStat").toString(),
                            oTrans.getRedeem(lnCtr,"sUserName").toString()));
                }
                initGrid1();
            }


        } catch (SQLException ex) {
            System.out.println("SQLException" + ex.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("NullPointerException" + ex.getMessage());
        } catch (DateTimeException ex) {
//            MsgBox.showOk(ex.getMessage());
            System.out.println("DateTimeException" + ex.getMessage());
        } 
    }
        private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        try {
            switch (lsButton){
                case "btnPost": 
                        pbLoaded = true;
                        if(ShowMessageFX.OkayCancel(null, "Panalo Info Tagging",
                                "Are you sure, do you want to Post this Transaction?") == true){
                        if (oTrans.PostRecord()){
                            ShowMessageFX.Warning(getStage(),"Transaction successfully posted.","Warning", null);
                            clearFields();
                            loadClient();
                            pnEditMode = oTrans.getEditMode();
                        } else{
                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                        }
                        }else
                        return;
                    break;
                case "btnVoid": //create new transaction
                        pbLoaded = true;
                        if(ShowMessageFX.OkayCancel(null, "Panalo Info Tagging",
                                "Are you sure, do you want to Void this Transaction?") == true){
                        if (oTrans.VoidRecord()){
                            ShowMessageFX.Warning(getStage(),"Transaction successfully voided.","Warning", null);
                            clearFields();
                            loadClient();
                            pnEditMode = oTrans.getEditMode();
                        } else{
                            ShowMessageFX.Warning(getStage(), oTrans.getMessage(),"Warning", null);
                        }
                        }else
                        return;
                    break;
                case "btnClose":
                    if(ShowMessageFX.OkayCancel(null, "Panalo Parameter", "Are you sure, do you want to close?") == true){
                        unloadForm();
                        break;
                    } else
                        return;
                case "btnRefresh":
                    clearFields();
                    loadClient();
                    break;
            }
            
//            initButton(pnEditMode);
        } catch (SQLException e) {
            e.printStackTrace();
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }catch (NullPointerException e) {
            e.printStackTrace();
            ShowMessageFX.Warning(getStage(),e.getMessage(), "Warning", null);
        }
    } 
    private void initGrid() {
        
        clientsIndex01.setStyle("-fx-alignment: CENTER;");
        clientsIndex02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        clientsIndex03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");

       
        clientsIndex01.setCellValueFactory(new PropertyValueFactory<>("PanaloInfoIndex01"));
        clientsIndex02.setCellValueFactory(new PropertyValueFactory<>("PanaloInfoIndex03"));
        clientsIndex03.setCellValueFactory(new PropertyValueFactory<>("PanaloInfoIndex17"));

        
        tblClients.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblClients.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        filteredData = new FilteredList<>(panaloinfo_data, b -> true);
        autoSearch(txtSeeks99);
        autoSearch(txtSeeks98);

        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<PanaloInfoModel> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        // 	  Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tblClients.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tblClients.setItems(sortedData);
        tblClients.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblClients.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
            header.setDisable(true);
        });
    }
    private void initGrid1() {
       
        redeemIndex01.setStyle("-fx-alignment: CENTER;");
        redeemIndex02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        redeemIndex03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        redeemIndex04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        redeemIndex05.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 0;");
        redeemIndex06.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 0;");
        
        redeemIndex01.setCellValueFactory(new PropertyValueFactory<>("PInfoRedeemIndex01"));
        redeemIndex02.setCellValueFactory(new PropertyValueFactory<>("PInfoRedeemIndex02"));
        redeemIndex03.setCellValueFactory(new PropertyValueFactory<>("PInfoRedeemIndex03"));
        redeemIndex04.setCellValueFactory(new PropertyValueFactory<>("PInfoRedeemIndex04"));
        redeemIndex05.setCellValueFactory(new PropertyValueFactory<>("PInfoRedeemIndex05"));
        redeemIndex06.setCellValueFactory(new PropertyValueFactory<>("PInfoRedeemIndex06"));

        // 5. Add sorted (and filtered) data to the table.
        tblRedemption.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblRedemption.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
       tblRedemption.setItems(pinfoRedeeem_data);
 
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
                // Compare order no. and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();
                switch (lnIndex){
                        case 99:
                            if(lnIndex == 99){
                                return (clients.getPanaloInfoIndex17().toLowerCase().contains(lowerCaseFilter)); // Does not match.   
                             }else {
                                return (clients.getPanaloInfoIndex17().toLowerCase().contains(lowerCaseFilter)); // Does not match.
                             }        
                                
                        case 98:
                             if(lnIndex == 98){
                               return (clients.getPanaloInfoIndex16().toLowerCase().contains(lowerCaseFilter)); // Does not match.   
                             }else {
                               return (clients.getPanaloInfoIndex16().toLowerCase().contains(lowerCaseFilter)); // Does not match.
                            }
                        default:
                        return true; 
                               
            }
            });
            
            changeTableView(0, ROWS_PER_PAGE);
        });
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
    private void itemStat01_Filter(ActionEvent event) {
         if (itemStat01.isSelected()){
         itemStat02.setSelected(FALSE);
         itemStat03.setSelected(FALSE);
         txtSeeks98.setText("0");
         }else {
             txtSeeks98.setText("");
                 }
         
     
    }

    @FXML
    private void itemStat02_Filter(ActionEvent event) {
         if (itemStat02.isSelected()){
         itemStat01.setSelected(FALSE);
         itemStat03.setSelected(FALSE);
         txtSeeks98.setText("1");
     }else {
             txtSeeks98.setText("");
                 }
    }
        @FXML
    private void itemStat03_Filter(ActionEvent event) {
         if (itemStat03.isSelected()){
         itemStat01.setSelected(FALSE);
         itemStat02.setSelected(FALSE);
         txtSeeks98.setText("4");
     }else {
             txtSeeks98.setText("");
                 }
    }
   @FXML
    void tblClients_Clicked(MouseEvent event) {
        pnRow = tblClients.getSelectionModel().getSelectedIndex(); 
        pagecounter = pnRow + pagination.getCurrentPageIndex() * ROWS_PER_PAGE;
           if (pagecounter >= 0){
             if(event.getClickCount() > 0){
              
//                clear();
                getSelectedItem(filteredData.get(pagecounter).getPanaloInfoIndex02());
                loadRedeemDetail(oldTransNo);
                
                tblClients.setOnKeyReleased((KeyEvent t)-> {
                    KeyCode key = t.getCode();
                    switch (key){
                        case DOWN:
                            pnRow = tblClients.getSelectionModel().getSelectedIndex();
                            pagecounter = pnRow + pagination.getCurrentPageIndex() * ROWS_PER_PAGE;
                            if (pagecounter == tblClients.getItems().size()) {
                                pagecounter = tblClients.getItems().size();
                                getSelectedItem(filteredData.get(pagecounter).getPanaloInfoIndex02());
                            }else {
                               int y = 1;
                              pnRow = pnRow + y;
                                getSelectedItem(filteredData.get(pagecounter).getPanaloInfoIndex02());
                            }
                            break;
                        case UP:
                            pnRow = tblClients.getSelectionModel().getSelectedIndex();
                            pagecounter = pnRow + pagination.getCurrentPageIndex() * ROWS_PER_PAGE;
                            getSelectedItem(filteredData.get(pagecounter).getPanaloInfoIndex02());
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
            txtField01.setText(panaloinfo_data.get(pagecounter).getPanaloInfoIndex02());
            txtField02.setText(panaloinfo_data.get(pagecounter).getPanaloInfoIndex03());
            txtField03.setText(panaloinfo_data.get(pagecounter).getPanaloInfoIndex17());
            txtField04.setText(panaloinfo_data.get(pagecounter).getPanaloInfoIndex05());
            txtField05.setText(panaloinfo_data.get(pagecounter).getPanaloInfoIndex04());
            txtField06.setText(panaloinfo_data.get(pagecounter).getPanaloInfoIndex07());
            txtField07.setText(panaloinfo_data.get(pagecounter).getPanaloInfoIndex08());
            txtField08.setText(panaloinfo_data.get(pagecounter).getPanaloInfoIndex09());
             
                     oldPnRow = pagecounter;   
        }
        
          } catch (SQLException e) {
        fail(e.getMessage());
    }   

    }
    @FXML
    private void tblRedemption_Clicked(MouseEvent event) {

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
    
    public void clearFields(){
        txtField01.clear();
        txtField02.clear();
        txtField03.clear();
        txtField04.clear();
        txtField05.clear();
        txtField06.clear();
        txtField07.clear();
        txtField08.clear();
        txtSeeks99.clear();
        txtSeeks98.clear();
        pinfoRedeeem_data.clear();
//        lblStatus.setVisible(false);
        oTrans = new Panalo(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        pbLoaded = true;
    }
    
}
