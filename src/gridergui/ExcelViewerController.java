/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gridergui;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;

/**
 * FXML Controller class
 *
 * @author Maynard
 */
public class ExcelViewerController implements Initializable, ScreenInterface{
    private final String pxeModuleName = "ExcelViewerController";
    private GRider oApp;
    
    private boolean pbLoaded = false;
    private boolean state = false;
    private LResult oListener;
    
    private int SheetNo =0;
    private int ColumnTo =0 ;
    private int ColumnFrom =0;
    private int RowTo =0;
    private int RowFrom =0;
    private int pnIndex =0;
    
    private String psFormType,psOList;
    @FXML
    private Button btnExit;
    @FXML
    private TextField txtField01,txtField02,txtField03,txtField04,txtField05;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnClose;
    @FXML
    private ComboBox ExcelType;
    
    private ObservableList<String> ExcelTypelbMC = FXCollections.observableArrayList("MOTORCYCLE","SPARE PARTS","JOB ORDER","COLLECTION");;
    private ObservableList<String> ExcelTypelbMP = FXCollections.observableArrayList("UNIT","ACCESSORIES");;
   
            
    @Override
        public void setGRider(GRider foValue) {
        oApp = foValue;
    }
    public void setListener(LResult foValue){
        oListener = foValue;
    }
        public void setFormType(String fsValue){
        psFormType = fsValue;
    }
        public void setObservableList(String foList){
        psOList = foList;
    }
    
    public int getSheetNo(){
        return SheetNo ;
    }
    public int getRowTo(){
        return RowTo ;
    }
     public int getRowFrom(){
        return RowFrom;
    }
     public int getColumnTo(){
        return ColumnTo;
    }
     public int getColumnFrom(){
         return ColumnFrom;
    }
    public int getTransactionIndex(){
         return pnIndex;
    }

    
    public boolean getState(){
       return state ;
    }

    private Stage getStage(){
	return (Stage) txtField01.getScene().getWindow();
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (psFormType == "MC"){
            ExcelType.setItems(ExcelTypelbMC);
        }else
            ExcelType.setItems(ExcelTypelbMP);
            btnOk.setOnAction(this::cmdButton_Click);
            btnClose.setOnAction(this::cmdButton_Click);
            btnExit.setOnAction(this::cmdButton_Click);
            
            pbLoaded = true;
    }
    
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        switch (lsButton){
            
            case "btnClose":
                if (oListener != null) oListener.OnCancel("Transaction cancelled successfully.");
                state = false;
                CommonUtils.closeStage(btnClose);
                
                break;
            case "btnOk":
                if (ExcelType.getSelectionModel().getSelectedIndex() >= 0){
                SheetNo =  Integer.parseInt(txtField01.getText());
                RowTo =  Integer.parseInt(txtField02.getText());
                RowFrom =  Integer.parseInt(txtField03.getText());
                ColumnTo =  Integer.parseInt(txtField04.getText());
                ColumnFrom =  Integer.parseInt(txtField05.getText());
                pnIndex = ExcelType.getSelectionModel().getSelectedIndex();
                state = true;
                CommonUtils.closeStage(btnOk);
                }else{
                    ShowMessageFX.Warning(getStage(), "Please Choose Entry Type","Warning", null);
                }
                break;
            case "btnExit":
                if (oListener != null) oListener.OnCancel("Transaction cancelled successfully.");
                state = false;
                CommonUtils.closeStage(btnExit);
                break;
                
            default:
                ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                return;
        }
    } 

    
}

