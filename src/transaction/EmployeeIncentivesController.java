/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author user
 */
public class EmployeeIncentivesController implements Initializable {

    @FXML
    private Button btnExit;
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
    private TableView<?> tblincetives;
    @FXML
    private TableView<?> tblemployee;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
