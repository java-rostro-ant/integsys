/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.StringUtil;
import org.rmj.appdriver.agent.MsgBox;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.fund.manager.base.LMasDetTrans;
import org.rmj.fund.manager.parameters.MCBranchPerformance;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 * FXML Controller class
 *
 * @author user
 */
public class McBranchPerformanceController implements Initializable , ScreenInterface{
     private GRider oApp;
    private MCBranchPerformance oTrans;
    private LMasDetTrans oListener;
    
    private int pnIndex = -1;
    private int pnEditMode;
    private int pnRow = 0;
    private int pnSubItems = 0;
    private boolean pbLoaded = false;
    private String psOldRec;
    private String psBarcode = "";
    private String psDescript = "";
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtSeeks07;
    @FXML
    private TextField txtSeeks08;
    @FXML
    private TextField txtField01;
    @FXML
    private TextField txtField02;
    @FXML
    private TextField txtField03;
    @FXML
    private TextField txtField04;
    @FXML
    private TextField txtField05;
    @FXML
    private TextField txtField06;
   
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnImport;
    @FXML
    private AnchorPane AnchorMainMcBranchInfo;
    @FXML
    private HBox hbButtons;
    @FXML
    private Label lblHeader;
    private FXMLLoader fxmlLoader;
   

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        oListener = new LMasDetTrans() {
            @Override
            public void MasterRetreive(int i, Object o) {
                switch (i){
                    case 12: //sBranchCd
                        txtField01.setText((String) o);
                        break;
                    case 2: //sPeriodxx
                        txtField02.setText((String) o);
                        break;
                    case 3: //nMCGoalxx
                        txtField03.setText((String) o);
                        break;
                    case 4: //nSPGoalxx
                        txtField04.setText((String) o);
                        break;
                    case 5: //nJOGoalxx
                        txtField05.setText((String) o);
                        break;
                    case 6://nLRGoalxx
                        txtField06.setText((String) o);
                         break;
                }
            }

            @Override
            public void DetailRetreive(int i, int i1, Object o) {
            }
        };
        
        oTrans = new MCBranchPerformance(oApp, oApp.getBranchCode(), false);
        oTrans.setListener(oListener);
        oTrans.setWithUI(true);
        
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnNew.setOnAction(this::cmdButton_Click);
        btnSave.setOnAction(this::cmdButton_Click);
        btnUpdate.setOnAction(this::cmdButton_Click);
        btnCancel.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        btnImport.setOnAction(this::cmdButton_Click);
//      text field focus
        txtField01.focusedProperty().addListener(txtField_Focus);
        txtField02.focusedProperty().addListener(txtField_Focus);
        txtField03.focusedProperty().addListener(txtField_Focus);
        txtField04.focusedProperty().addListener(txtField_Focus);
        txtField05.focusedProperty().addListener(txtField_Focus);
        txtField06.focusedProperty().addListener(txtField_Focus);
//      text field  key pressed
        txtSeeks07.setOnKeyPressed(this::txtField_KeyPressed);
        txtSeeks08.setOnKeyPressed(this::txtField_KeyPressed);
        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
        txtField02.setOnKeyPressed(this::txtField_KeyPressed);
        txtField03.setOnKeyPressed(this::txtField_KeyPressed);
        txtField04.setOnKeyPressed(this::txtField_KeyPressed);
        txtField05.setOnKeyPressed(this::txtField_KeyPressed);
        txtField06.setOnKeyPressed(this::txtField_KeyPressed);
        txtField02.textProperty().addListener((final ObservableValue<? extends String> ov, final String oldValue, final String newValue) -> {
            if (txtField02.getText().length() > 6) {
                String s = txtField02.getText().substring(0, 6);
                txtField02.setText(s);
            }
        });
        txtSeeks07.textProperty().addListener((final ObservableValue<? extends String> ov, final String oldValue, final String newValue) -> {
            if (txtSeeks07.getText().length() > 6) {
                String s = txtSeeks07.getText().substring(0, 6);
                txtSeeks07.setText(s);
            }
        });
        
        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);
        
        pbLoaded = true;
    }    

    @Override
    public void setGRider(GRider foValue) {
         oApp = foValue;
    }
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
//        try {
            switch (lsButton){
                 case "btnBrowse":
                    {
                        try {
                            if (oTrans.SearchRecord(txtSeeks07.getText(), false)){
                             
                                loadRecord();
                                pnEditMode = oTrans.getEditMode();
                            }else {
                                MsgBox.showOk(oTrans.getMessage());
                            }
                        } catch (SQLException e) {
                                MsgBox.showOk(e.getMessage());
                        }
                    }
                    break;

                case "btnNew": //create new transaction
                          try {
                            if (oTrans.NewRecord()){
                                clearFields();
                                loadRecord();
                                pnEditMode = oTrans.getEditMode();
                                } else 
                                    MsgBox.showOk(oTrans.getMessage());
                            } catch (SQLException e) {
                                MsgBox.showOk(e.getMessage());
                            }
                       
                    break;
                 case "btnSave":
                    {
                        try {
                            if (oTrans.SaveRecord()){
                                clearFields();
                                MsgBox.showOk("Record Save Successfully.");
                                pnEditMode = EditMode.UNKNOWN;
                            }else{
                                 MsgBox.showOk(oTrans.getMessage());
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(McBranchPerformanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;

                case "btnUpdate":
                   {
                        try {
                            if (oTrans.UpdateRecord()){
                                loadRecord();
                                pnEditMode = oTrans.getEditMode();
                            } else {
                                MsgBox.showOk(oTrans.getMessage());
                            }
                        } catch (SQLException e) {
                                MsgBox.showOk(e.getMessage());
                        }
                    }
                    break;
                case "btnImport":
                    {
                        try {
                            insertFile();
                        } catch (IOException ex) {
                            Logger.getLogger(McBranchPerformanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                   break;

                case "btnCancel":
                    
                    clearFields();
                    oTrans = new MCBranchPerformance(oApp, oApp.getBranchCode(), false);
                    oTrans.setListener(oListener);
                    oTrans.setWithUI(true);
                    pnEditMode = EditMode.UNKNOWN;
                    //reload detail
                    break;
                
                case "btnClose":
                    if(ShowMessageFX.OkayCancel(null, "Employee Bank Info", "Do you want to disregard changes?") == true){
                        unloadForm();
                        break;
                    } else
                        return;
            }
            
            initButton(pnEditMode);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            MsgBox.showOk(e.getMessage());
//        }
    } 
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        
        btnCancel.setVisible(lbShow);
        btnSave.setVisible(lbShow);
        
        btnSave.setManaged(lbShow);
        btnCancel.setManaged(lbShow);
        btnUpdate.setVisible(!lbShow);
        btnBrowse.setVisible(!lbShow);
        btnNew.setVisible(!lbShow);
        
        txtSeeks07.setDisable(lbShow);
        txtSeeks08.setDisable(lbShow);
        txtField01.setDisable(!lbShow);
        txtField02.setDisable(!lbShow);
        txtField03.setDisable(!lbShow);
        txtField04.setDisable(!lbShow);
        txtField05.setDisable(!lbShow);
        txtField06.setDisable(!lbShow);
        if(fnValue == EditMode.UPDATE){
            txtField01.setDisable(lbShow);
            txtField02.setDisable(lbShow);
            txtField03.requestFocus();
        }
        if(fnValue == EditMode.ADDNEW){
            txtField02.requestFocus();
        }
        if (lbShow){
            txtSeeks07.setDisable(lbShow);
            txtSeeks08.setDisable(lbShow);
            btnCancel.setVisible(lbShow);
            btnSave.setVisible(lbShow);
            btnUpdate.setVisible(!lbShow);
            btnBrowse.setVisible(!lbShow);
            btnNew.setVisible(!lbShow);
            btnBrowse.setManaged(false);
            btnNew.setManaged(false);
            btnUpdate.setManaged(false);
        }
        else{
            txtSeeks07.setDisable(lbShow);
            txtSeeks07.requestFocus();
        }
    }
    public void clearFields(){
        txtField01.clear();
        txtField02.clear();
        txtField03.clear();
        txtField04.clear();
        txtField05.clear();
        txtField06.clear();
        txtSeeks07.clear();
        txtSeeks08.clear();
        
                
    }
    
    private void unloadForm(){
        StackPane myBox = (StackPane) AnchorMainMcBranchInfo.getParent();
        myBox.getChildren().clear();
        myBox.getChildren().add(getScene("MainScreenBG.fxml"));
    }
    private AnchorPane getScene(String fsFormName){
         ScreenInterface fxObj = new MainScreenBGController();
         fxObj.setGRider(oApp);
        
        fxmlLoader = new FXMLLoader();
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
    
    private void loadRecord(){
        try {
            
            txtField02.setText(oTrans.getMaster(2).toString());
            txtField01.setText(oTrans.getMaster(12).toString());
            txtField03.setText(oTrans.getMaster(3).toString());
            txtField04.setText(oTrans.getMaster(4).toString());
            txtField05.setText(oTrans.getMaster(5).toString());
            txtField06.setText(oTrans.getMaster(6).toString());
            txtSeeks07.setText(oTrans.getMaster(2).toString());
            txtSeeks08.setText(oTrans.getMaster(12).toString());
        } catch (SQLException e) {
            MsgBox.showOk(e.getMessage());
        }
    }
    
    private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
             
        switch (event.getCode()){
            case F3:
                switch (lnIndex){
                     case 1: /*Search*/
                        try {
                            if (oTrans.searchBranch(txtField01.getText(), false)){
                                pnEditMode = oTrans.getEditMode();
                            } else {
                                MsgBox.showOk(oTrans.getMessage());
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(McBranchPerformanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case 7: /*Search*/
                        try {
                            if (oTrans.SearchRecord(txtSeeks07.getText(), true)){
                                loadRecord();
                                pnEditMode = oTrans.getEditMode();
                            } else {
                                MsgBox.showOk(oTrans.getMessage());
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(McBranchPerformanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case 8: /*Search*/
                    {
                         try {
                            if (oTrans.SearchRecord(txtSeeks08.getText(), false)){
                                loadRecord();
                                pnEditMode = oTrans.getEditMode();
                            } else {
                                MsgBox.showOk(oTrans.getMessage());
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(McBranchPerformanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                        break;
                }   
            case ENTER:
            case DOWN:
                CommonUtils.SetNextFocus(txtField); break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        }
        
    }
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
       
        String lsValue = txtField.getText();
        if (lsValue == null) return;
            
        if(!nv){ //Lost Focus
            try {
                switch (lnIndex){
                        case 2: //sPeriodxxx
                            oTrans.setMaster(lnIndex, lsValue);
                            break;
                        case 3: //
                              if (StringUtil.isNumeric(lsValue))
                                oTrans.setMaster(lnIndex, Integer.parseInt(lsValue));
                            else
                                oTrans.setMaster(lnIndex,lsValue);
                            break;
                        case 4: //
                            if (StringUtil.isNumeric(lsValue))
                                oTrans.setMaster(lnIndex, Double.parseDouble(lsValue));
                            else
                                oTrans.setMaster(lnIndex, 0.00);
                            break;
                          
                        case 5: //
                            if (StringUtil.isNumeric(lsValue))
                                oTrans.setMaster(lnIndex, Integer.parseInt(lsValue));
                            else
                                oTrans.setMaster(lnIndex,0);
                            
                            break;
                        case 6: //
                            if (StringUtil.isNumeric(lsValue))
                                oTrans.setMaster(lnIndex, Double.parseDouble(lsValue));
                            else
                                oTrans.setMaster(lnIndex, 0.00);
                            break;
                    }
            } catch (SQLException e) {
                MsgBox.showOk(e.getMessage());
            }
            
        } else{ //Focus
            pnIndex = lnIndex;
            txtField.selectAll();
        }
    }; 
    public void insertFile() throws IOException {
       
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File("d:\\"));
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("Excel Files", "*.xls", "*.xlsx"));
        File selectedFile = fileChooser.showOpenDialog((Stage) AnchorMainMcBranchInfo.getScene().getWindow());

        String fileName = selectedFile.getName();           
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, selectedFile.getName().length());

        if(fileExtension.equalsIgnoreCase("xlsx")){
            try {
                xlsxFile(selectedFile);
            } catch (IOException ex) {
                Logger.getLogger(McBranchPerformanceController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
             try {
                
                xlsFile(selectedFile);
            } catch (IOException ex) {
                Logger.getLogger(McBranchPerformanceController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
       
    }  
    
    public void xlsxFile(File selectedFile) throws FileNotFoundException, IOException{
        File file = new File(selectedFile.getAbsolutePath());   //creating a new file instance  
        FileInputStream fis = new FileInputStream(file);  
        XSSFWorkbook wb = new XSSFWorkbook(fis);   
        XSSFSheet sheet = wb.getSheetAt(0);

        for(int rows = 1; rows < sheet.getPhysicalNumberOfRows(); rows++){
            Row currentRow = sheet.getRow(rows);
            
            System.out.print((currentRow.getCell(0) == null)? "" : currentRow.getCell(0).getStringCellValue()+ "\t");
            System.out.print((currentRow.getCell(1) == null)? 0.0 + "\t" : currentRow.getCell(1).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(2) == null)? 0.0 + "\t" : currentRow.getCell(2).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(3) == null)? 0.0 + "\t" : currentRow.getCell(3).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(4) == null)? 0.0 + "\t" : currentRow.getCell(4).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(5) == null)? 0.0 + "\t" : currentRow.getCell(5).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(6) == null)? 0.0 + "\t" : currentRow.getCell(6).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(7) == null)? 0.0 + "\t" : currentRow.getCell(7).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(8) == null)? 0.0 + "\t" : currentRow.getCell(8).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(9) == null)? 0.0 + "\t" : currentRow.getCell(9).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(10) == null)? 0.0 + "\t" : currentRow.getCell(10).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(11) == null)? 0.0 + "\t" : currentRow.getCell(11).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(12) == null)? 0.0 + "\t" : currentRow.getCell(12).getNumericCellValue() + "\t");
          System.out.println();
        }
    }
     public void xlsFile(File selectedFile) throws FileNotFoundException, IOException{
        FileInputStream fis  = new FileInputStream(new File(selectedFile.getAbsolutePath()));
        HSSFWorkbook wb=new HSSFWorkbook(fis);   
    //creating a Sheet object to retrieve the object  
        HSSFSheet sheet=wb.getSheetAt(0); 
        for(int rows = 1; rows < sheet.getPhysicalNumberOfRows(); rows++){
            Row currentRow = sheet.getRow(rows);
              
            System.out.print((currentRow.getCell(0) == null)? "" : currentRow.getCell(0).getStringCellValue()+ "\t"); 
            System.out.print((currentRow.getCell(1) == null)? 0.0 + "\t" : currentRow.getCell(1).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(2) == null)? 0.0 + "\t" : currentRow.getCell(2).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(3) == null)? 0.0 + "\t" : currentRow.getCell(3).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(4) == null)? 0.0 + "\t" : currentRow.getCell(4).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(5) == null)? 0.0 + "\t" : currentRow.getCell(5).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(6) == null)? 0.0 + "\t" : currentRow.getCell(6).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(7) == null)? 0.0 + "\t" : currentRow.getCell(7).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(8) == null)? 0.0 + "\t" : currentRow.getCell(8).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(9) == null)? 0.0 + "\t" : currentRow.getCell(9).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(10) == null)? 0.0 + "\t" : currentRow.getCell(10).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(11) == null)? 0.0 + "\t" : currentRow.getCell(11).getNumericCellValue() + "\t");
            System.out.print((currentRow.getCell(12) == null)? 0.0 + "\t" : currentRow.getCell(12).getNumericCellValue() + "\t");


          System.out.println();
        }
    }
    
}
