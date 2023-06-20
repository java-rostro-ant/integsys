/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.awt.Desktop;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.LMasDetTrans;
import org.rmj.fund.manager.base.MCImage;


public class MCImagesController implements Initializable , ScreenInterface{
    private final String pxeModuleName = "MC Model Images";

    /**
     * Initializes the controller class.
     */
    private GRider oApp;
   
    private MCImage oTrans;
    private LMasDetTrans oListener;
    private int pnEditMode;
    private int pnRow = -1;
    private double xOffset = 0;
    private double yOffset = 0;
   
    private Desktop desktop = Desktop.getDesktop();
    private boolean pbLoaded = false;
  @FXML
    private AnchorPane AnchorMCImages,searchBar;
    @FXML
    private TextField txtField01,txtField21,txtField04,txtSeeks98
                        ,txtSeeks99,txtField03,txtField05;
    @FXML
    private ImageView imgDefault;
    @FXML
    private HBox hbButtons1;
    @FXML
    private TableView tblMCUrl;
    @FXML
    private TableColumn detailIndex01,detailIndex02,detailIndex03;
    @FXML
    private HBox hbButtons2;
    @FXML
    private Button btnUpdate,btnRefresh,btnSearch,btnCancel,btnBrowse
                    ,btnRemoveImg,btnAddImg,btnNew,btnSave;    
    @FXML
    private RadioButton btnImgPrimary;
    
    private final ObservableList<TableModel> img_data = FXCollections.observableArrayList();
         
    private ObservableList<String> stats = FXCollections.observableArrayList(
        "OPEN",
        "CLOSED",
        "POSTED",
        "CANCELLED"
    );
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        oListener = new LMasDetTrans() {
            @Override
            public void MasterRetreive(int i, Object o) {
                //                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//                System.out.println(i);
                    loadDetail();
            }

            @Override
            public void DetailRetreive(int fnRow, int fnIndex, Object foValue) {
//                  loadDetail();
                    loadMaster(fnRow);
                    pnRow = fnRow;
                }
        };
        
        oTrans = new MCImage(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setTranStat(12340);
        oTrans.setWithUI(true);
        
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnNew.setOnAction(this::cmdButton_Click);
        btnSave.setOnAction(this::cmdButton_Click);
        btnCancel.setOnAction(this::cmdButton_Click);
        btnUpdate.setOnAction(this::cmdButton_Click);
        btnSearch.setOnAction(this::cmdButton_Click);
        btnRefresh.setOnAction(this::cmdButton_Click);
        btnAddImg.setOnAction(this::cmdButton_Click);
        btnRemoveImg.setOnAction(this::cmdButton_Click);
        txtField21.setOnKeyPressed(this::txtField_KeyPressed); 
        txtField03.setOnKeyPressed(this::txtField_KeyPressed); 
        txtField01.setOnKeyPressed(this::txtField_KeyPressed); 
        txtField05.setOnKeyPressed(this::txtField_KeyPressed); 
        txtField04.setOnKeyPressed(this::txtField_KeyPressed);        
        txtSeeks98.setOnKeyPressed(this::txtField_KeyPressed);
        txtSeeks99.setOnKeyPressed(this::txtField_KeyPressed);
        
        txtField05.focusedProperty().addListener(txtField_Focus);
        pnEditMode = EditMode.UNKNOWN;
        pnRow = 0;
        initButton(pnEditMode);
        pbLoaded = true;
        
        if(img_data.isEmpty()){
//            imgDefault.setImage(new Image("/images/no-image-available_1.png"));
//            }else{
//            Image image = new Image(img_data.get(0).getIndex01());
//            imgDefault.setImage(image);
        }
       
        btnImgPrimary.selectedProperty().addListener((observable, oldValue, newValue) -> {
         try{
            if (newValue) {
            oTrans.setPrimary(pnRow);
            loadMaster(pnRow);
        }else {
                
        }
    } catch (SQLException ex) {
                Logger.getLogger(MCImagesController.class.getName()).log(Level.SEVERE, null, ex);
            }   

            }
                );
               
    }
    

    public void setGRider(GRider foValue) {
        oApp = foValue;//To change body of generated methods, choose Tools | Templates.
    }
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{
        try{
        
        if (!pbLoaded) return;
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        if (lsValue == null) return;
        if(!nv){ /*Lost Focus*/
            switch (lnIndex){
                //
                
                case 05:
                    oTrans.setDetail(pnRow, "sImageURL", lsValue);
                    break;
                default:
                    ShowMessageFX.Warning(null, pxeModuleName, "Text field with name " + txtField.getId() + " not registered.");
                    return;
            }
            loadDetail();
            
            
        } else
            txtField.selectAll();
        } catch (SQLException ex) {
            Logger.getLogger(MCImagesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        };   

     private void loadMaster(int fnRow)  {
        try {
//        txtSeeks98.setText((String) oTrans.getMaster(1));
//        txtSeeks99.setText((String) json.get("sModelCde"));
        if(fnRow <= 0){
           fnRow = fnRow + 1;
        }
                
            System.out.println("loadmaster row =  "+fnRow);
        txtField01.setText((String) oTrans.getDetail(fnRow, "sModelIDx"));
        txtField21.setText((String) oTrans.getDetail(fnRow, "sModelCde"));
        txtField03.setText((String) oTrans.getDetail(fnRow, "sModelNme"));
        txtField04.setText((String) oTrans.getDetail(fnRow, "sBrandNme"));
        txtField05.setText((String) oTrans.getDetail(fnRow, "sImageURL"));
        txtField21.requestFocus();
        loadDetail();
        
        boolean lnRspd = (oTrans.getDetail(fnRow, "cPrimaryx").equals(1));
            btnImgPrimary.setSelected(lnRspd);
        
        } catch (SQLException ex) {
            Logger.getLogger(MCImagesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
    private void loadDetail(){
            int lnCtr;
            String lnRpl = "";
        try {
        
            img_data.clear();
                System.out.println("count = " + oTrans.getImageCount());
                for (lnCtr = 0; lnCtr < oTrans.getImageCount(); lnCtr++){
                    if(oTrans.getDetail(lnCtr +1,"cPrimaryx").equals(1)){
                        lnRpl = "YES";
                    }else {
                         lnRpl = "NO";
                    }
                    img_data.add(new TableModel(String.valueOf(lnCtr +1) ,
                            (String) oTrans.getDetail(lnCtr +1,"sImageURL")
                            ,lnRpl));
                    
                }
                initGridDetail();
//              loadImage(img_data.size()-1);  
               
        } catch (SQLException ex) {
            Logger.getLogger(MCImagesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void clearFields(){
        txtField01.setText("");
        txtField21.setText("");
        txtField03.setText("");      
        txtField04.setText("");
        txtSeeks98.setText("");
        txtSeeks99.setText("");
        txtField05.setText("");
        imgDefault.setImage(new Image("/images/no-image-available_1.png"));
        img_data.clear();
    }
    private void resetTrans(){
        oTrans = new MCImage(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setTranStat(12340);
        oTrans.setWithUI(true);
        pnEditMode = EditMode.UNKNOWN;
        clearFields();
    }
    
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        try {
            switch (lsButton){
                case "btnBrowse":
                   if (oTrans.SearchTransaction("", true)){
                       loadMaster(1);
                   }
                    break;
                case "btnNew": 
                    if (oTrans.NewTransaction() ){
                        clearFields();
                        pnRow = 1;
//                        loadMaster();
                        pnEditMode = EditMode.ADDNEW;
                    }else
                        ShowMessageFX.Warning(null, pxeModuleName, "Unable to Create New Transaction.");
                     
                    break;
                case "btnSearch":
                        if (oTrans.searchItem(pnRow,txtField21.getText(), false, true)){
                                pnEditMode = oTrans.getEditMode();
                            }else {
                            ShowMessageFX.Information(oTrans.getMessage(),pxeModuleName,"");
                        } 
                    
                        break;
                    

                case "btnSave": 
                     try {
                        if (oTrans.SaveTransaction()){
                            ShowMessageFX.Information("","","Product item successfully saved.");
                            clearFields();
                            resetTrans();
                        } else {
                           ShowMessageFX.Warning(null, pxeModuleName, oTrans.getMessage());
                     }
                        
                    } catch (SQLException e) {
                        e.printStackTrace();
                        ShowMessageFX.Information(oTrans.getMessage(),pxeModuleName,"");
                        
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        ShowMessageFX.Information(oTrans.getMessage(),pxeModuleName,"");
                        
                    }
                    break;
                case "btnAddImg":
                     if(img_data.size() > 0){
                         oTrans.addImage();
                             loadMaster(pnRow);
                         }
                     
                    break;
                case "btnRemoveImg":
                    System.out.println(img_data.size());
                    if(img_data.size() > 0){
                        btnRemoveImg.setDisable(false);
                        if (oTrans.removeImage(pnRow)){
                               loadMaster(pnRow); 
                        } else 
                          ShowMessageFX.Information(oTrans.getMessage(),pxeModuleName,"");
                        
                    }else{
                        btnRemoveImg.setDisable(true);
                    }
                    
                    break;
               
                case "btnUpdate":
//                    if (oTrans.OpenTransaction(txtField01.getText())){
                          if (oTrans.UpdateTransaction()){
                                pnEditMode = oTrans.getEditMode(); 
                            loadMaster(pnRow);
                        } else {
                            ShowMessageFX.Information(oTrans.getMessage(),pxeModuleName,"");
                        }
//                    } else {
//                       ShowMessageFX.Information(oTrans.getMessage(),pxeModuleName,"");
//                    }
//                        if (oTrans.UpdateTransaction()){
//                            pnEditMode = oTrans.getEditMode(); 
//                        } else 
//                          MsgBox.showOk(oTrans.getMessage());
                    break;
                case "btnCancel":
                    if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to disregard changes?") == true){  
                            clearFields();
                            resetTrans();
                        }
                    break;
                    
                
               case "btnRefresh":
                    {
                        txtSeeks98.clear();
                        txtSeeks99.clear();
                        clearFields();
                    }
                    break;
                
            }
            initButton(pnEditMode);
        } catch (SQLException e) {
            e.printStackTrace();
            ShowMessageFX.Information(oTrans.getMessage(),pxeModuleName,"");
                        
//        }
    } }
    
    
    
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);

        btnBrowse.setVisible(!lbShow);
        btnNew.setVisible(!lbShow);
        btnSave.setVisible(lbShow);
        btnUpdate.setVisible(!lbShow);
        btnSearch.setVisible(lbShow);
        btnCancel.setVisible(lbShow);
        
        btnAddImg.setDisable(!lbShow);
        btnRemoveImg.setDisable(!lbShow);
        btnSave.setManaged(lbShow); 
       
        btnAddImg.setDisable(!lbShow);
        btnRemoveImg.setDisable(!lbShow);
//        btnBrowse.setVisible(!lbShow);
//        
        txtField21.setDisable(!lbShow);
        txtField03.setDisable(!lbShow);
        
        txtSeeks98.setDisable(lbShow);
        txtSeeks99.setDisable(lbShow);
        if (lbShow){
            btnNew.setVisible(!lbShow);
            btnBrowse.setManaged(false);
            btnNew.setManaged(false);
            btnUpdate.setManaged(false); 
        }
    }

    
    private void initGridDetail(){
        
        detailIndex01.setStyle("-fx-alignment: CENTER;");
        detailIndex02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");        
        detailIndex03.setStyle("-fx-alignment: CENTER;");
        detailIndex01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        detailIndex02.setCellValueFactory(new PropertyValueFactory<>("index02"));        
        detailIndex03.setCellValueFactory(new PropertyValueFactory<>("index03"));
       
        tblMCUrl.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblMCUrl.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        tblMCUrl.setItems(img_data);
        tblMCUrl.getSelectionModel().select(pnRow -1);
        pnRow = tblMCUrl.getSelectionModel().getSelectedIndex() +1;
        tblMCUrl.autosize();
        
    }

   
    

        
    private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        
        try{
           switch (event.getCode()){
            case F3:
            case ENTER:
                switch (lnIndex){
                    case 3: /*Search model cde*/
                        if (oTrans.searchItem(pnRow,txtField03.getText(), false,false)){
                                pnEditMode = oTrans.getEditMode();
//                                loadMaster();
                            } else {
                            ShowMessageFX.Information(oTrans.getMessage(),pxeModuleName,"");
                        } 
                        break;
                    case 21: /*Search Model*/
                        System.out.println("search row = =" +pnRow );
                        if (oTrans.searchItem(pnRow,txtField21.getText(), false, true)){
                                pnEditMode = oTrans.getEditMode();
                            }else {
                            ShowMessageFX.Information(oTrans.getMessage(),pxeModuleName,"");
                        } 
                        break;
                    case 99: /*Search Modelnm browse*/
                        if (oTrans.SearchTransaction(txtSeeks99.getText(), false)){
                       loadMaster(1);
                    }
                        break;    
                    case 98: /*Search Modelcd Browse*/
                        if (oTrans.SearchTransaction(txtSeeks98.getText(), false)){
                       loadMaster(1);
                    }
                        break;    
                    
                }
            
        } 
        }catch(SQLException e){
                ShowMessageFX.Information(oTrans.getMessage(),pxeModuleName,"");
                        
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
   

    
    @FXML
    private void tblMCImage_Clicked(MouseEvent event) {
        
        if(img_data.size() != 0){
            pnRow = tblMCUrl.getSelectionModel().getSelectedIndex() +1 ;
        System.out.println("count click row = " + pnRow);
            loadImage(pnRow-1);
            loadMaster(pnRow);
           
        }    
       
    }
    private void loadImage(int row){
        Image defaultImage = new Image("/images/no-image-available.png");
     try {
        if (img_data.get(row).getIndex02() != null)
            if(!img_data.get(row).getIndex02().trim().isEmpty()) {
            Image image = new Image(img_data.get(row).getIndex02());
            if (image.isError()) {
                image = new Image("/images/no-image-icon.png");
            }
            imgDefault.setImage(image);

        }else {
            imgDefault.setImage(defaultImage);
        }
        } catch (IllegalArgumentException e) {

        System.err.println("Invalid URL or resource not found: " + img_data.get(row).getIndex02());
        imgDefault.setImage(defaultImage);

        } catch (Exception e) {
            System.err.println("Error on loading images");
            imgDefault.setImage(defaultImage);
        }
    }
}
    
    
   

    

