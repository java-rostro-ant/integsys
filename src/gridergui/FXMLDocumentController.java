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
import org.rmj.appdriver.GSec;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.constants.UserRight;

/**
 * FXML Controller class
 *
 * @author user
 */
public class FXMLDocumentController implements Initializable, ScreenInterface {

    private GRider oApp;
    private String reportName;

    @FXML
    private Pane btnMin;
    @FXML
    private Pane btnClose;
    @FXML
    private StackPane workingSpace;
    @FXML
    private Menu mnuTransaction;
    @FXML
    private Menu mnuAudit;
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
    private MenuItem mnuCashCountEntry;
    @FXML
    private MenuItem mnuAuditIncentiveReport,  mnuAuditIncentiveReportNew, mnuRaffleReport;
    @FXML
    private MenuItem mnuAuditDeptIncentiveReport;
    @FXML
    private Pane view;
    @FXML
    private MenuItem mnuEmployeeIncentivesBank;
    @FXML
    private MenuItem mnuIncentiveConfirmation;
    @FXML
    private MenuItem mnuStandardReport;

    @FXML
    private MenuItem mnuMCBranch;
    @FXML
    private MenuItem mnuMCArea;
    @FXML
    private MenuItem mnuMPBranch;
    @FXML
    private MenuItem mnuMPArea;
    @FXML
    private MenuItem mnuIncentiveReleaseHistory; 
    @FXML
    private MenuItem mnuIncentiveReleasing;

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
    private Menu mnuTransactionIncentives, mnuTransactionIncDptmnt;
    @FXML
    private Menu mnuTransactionCashCount;
    @FXML
    private Menu mnuInventory01;
    @FXML
    private MenuItem mnuInventoryReq;
    @FXML
    private MenuItem mnuInventoryHistory;
    @FXML
    private MenuItem mnuExit;
    @FXML
    private MenuItem mnuCashCountRequest, mnuDepartmentIncentives, mnuDeptIncentivesApproval, mnuDeptIncentiveReleasing, mnuDeptIncentivesHistory;
    @FXML
    private MenuItem mnuAuditBankReport;
    @FXML
    private MenuItem mnuPayrollReport;
    @FXML
    private MenuItem mnuAuditReport;
    @FXML
    private MenuItem mnuPanaloInfo, mnuMCImage;
    @FXML
    private MenuItem mnuPanaloRedeem;
    @FXML
    private MenuItem mnuPacitaEvaluationHis;
    @FXML
    private MenuItem mnuPacitaSumReport, mnuPacitaDetReport,
            mnuPacitaTop10Report;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setScene(loadAnimate("MainScreenBG.fxml"));
        getTime();

        ResultSet name;
        String lsQuery = "SELECT b.sCompnyNm "
                + " FROM xxxSysUser a"
                + " LEFT JOIN Client_Master b"
                + " ON a.sEmployNo  = b.sClientID"
                + " WHERE a.sUserIDxx = " + SQLUtil.toSQL(oApp.getUserID());
        name = oApp.executeQuery(lsQuery);
        try {
            if (name.next()) {
                AppUser.setText(name.getString("sCompnyNm") + " || " + oApp.getBranchName());
                System.err.println(name.getString("sCompnyNm"));
                System.setProperty("user.name", name.getString("sCompnyNm"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        initMenu();
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

    private AnchorPane loadAnimate(String fsFormName) {
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

    private ScreenInterface getController(String fsValue) {
        switch (fsValue) {
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
            case "IncentiveReleasingNew.fxml":
                return new IncentiveReleasingNewController();
            case "IncentiveReleasingHistory.fxml":
                return new IncentiveReleasingHistoryController();
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
            case "CashCountEntry.fxml":
                return new CashCountEntryController();
            case "CashCount.fxml":
                return new CashCountController();
            case "CashCountHistory.fxml":
                return new CashCountHistoryController();
//
            case "DeptIncentivesApproval.fxml":
                return new DeptIncentivesApprovalController();
            case "DeptIncentives.fxml":
                return new DeptIncentivesController();
            case "DeptIncentiveReleasing.fxml":
                return new DeptIncentiveReleasingController();
            case "DeptIncentivesHist.fxml":
                return new DeptIncentivesHistController();
//
            case "InventoryHistory.fxml":
                return new InventoryHistoryController();
            case "IncentiveReports.fxml":
                IncentiveReportsController inc_reports = new IncentiveReportsController();
                inc_reports.setReportCategory(reportName);
                return inc_reports;
            case "IncentiveReportsNew.fxml":
                IncentiveReportsNewController inc_reportsnew = new IncentiveReportsNewController();
                inc_reportsnew.setReportCategory(reportName);
                return inc_reportsnew;
            case "Reports.fxml":
                ReportsController reports = new ReportsController();
                reports.setReportCategory(reportName);
                return reports;
            case "DeptIncentiveReports.fxml":
                DeptIncentiveReportsController inc_reports_Dept = new DeptIncentiveReportsController();
                inc_reports_Dept.setReportCategory(reportName);
                return inc_reports_Dept;

            case "PanaloParameter.fxml":
                return new PanaloParameterController();

            case "PanaloInfo.fxml":
                return new PanaloInfoController();
            case "PanaloRedemption.fxml":
                return new PanaloRedemptionController();
            case "RaffleParameter.fxml":
                return new RaffleParameterController();
            case "RaffleReports.fxml":
                return new RaffleReportsController();
            case "MCImages.fxml":
                return new MCImagesController();
            case "PacitaEvaluationHis.fxml":
                return new PacitaEvaluationHisController();
            case "PacitaEvalSummarizedReport.fxml":
                return new PacitaEvalSummarizedReportController();
            case "PacitaEvalDetailedReport.fxml":
                return new PacitaEvalDetailedReportController();
            case "PacitaEvalDetailedRulesReport.fxml":
                return new PacitaEvalDetailedRulesReportController();
            case "PacitaEvalTop10Report.fxml":
                return new PacitaEvalTop10ReportController();
            default:
                return null;
        }
    }

    private void setScene(AnchorPane foPane) {
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
        setScene(loadAnimate("IncentiveReleasingNew.fxml"));
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
    private void mnuIncentiveReleaseHistoryClick(ActionEvent event) {
        setScene(loadAnimate("IncentiveReleasingHistory.fxml"));
    }

    @FXML
    private void mnuCashCountClick(ActionEvent event) {
        setScene(loadAnimate("CashCount.fxml"));
    }

    @FXML
    private void mnuCashCountEntryClick(ActionEvent event) {
        setScene(loadAnimate("CashCountEntry.fxml"));
    }

    @FXML
    private void mnuCashCountHistoryClick(ActionEvent event) {
        setScene(loadAnimate("CashCountHistory.fxml"));
    }

    @FXML
    private void mnuInventoryHistoryClick(ActionEvent event) {
        setScene(loadAnimate("InventoryHistory.fxml"));

    }

    @FXML
    private void mnuStandardReportClick(ActionEvent event) {
        reportName = "STANDARD";
        setScene(loadAnimate("Reports.fxml"));
    }

    @FXML
    private void mnuAuditIncentiveReportClicked(ActionEvent event) {
        reportName = "AUDIT BRANCH INCENTIVES";
        setScene(loadAnimate("IncentiveReports.fxml"));
    }

    @FXML
    private void mnuAuditIncentiveReportNewClicked(ActionEvent event) {
        reportName = "AUDIT BRANCH INCENTIVES";
        setScene(loadAnimate("IncentiveReportsNew.fxml"));
    }

    @FXML
    private void mnuAuditDeptIncentiveReportClicked(ActionEvent event) {
        reportName = "AUDIT DEPARTMENT INCENTIVES";
        setScene(loadAnimate("DeptIncentiveReports.fxml"));
    }

    @FXML
    private void mnuAuditReportClick(ActionEvent event) {
        reportName = "AUDIT";
        setScene(loadAnimate("Reports.fxml"));
    }

    @FXML
    private void mnuAuditBankReportClick(ActionEvent event) {
        reportName = "AUDIT";
        setScene(loadAnimate("Reports.fxml"));
    }

    @FXML
    private void mnuPayrollReportClick(ActionEvent event) {
        reportName = "PAYROLL";
        setScene(loadAnimate("Reports.fxml"));
    }

    @FXML
    private void mnuDepartmentIncentivesClick(ActionEvent event) {
        setScene(loadAnimate("DeptIncentives.fxml"));
    }

    @FXML
    private void mnuDeptIncentivesApprovalClick(ActionEvent event) {
        setScene(loadAnimate("DeptIncentivesApproval.fxml"));
    }

    @FXML
    private void mnuDeptIncentiveReleasingClick(ActionEvent event) {
        setScene(loadAnimate("DeptIncentiveReleasing.fxml"));
    }

    @FXML
    private void mnuDeptIncentivesHistoryClick(ActionEvent event) {
        setScene(loadAnimate("DeptIncentivesHist.fxml"));
    }

    @FXML
    private void mnuPanaloParameterClick(ActionEvent event) {
        setScene(loadAnimate("PanaloParameter.fxml"));
    }

    @FXML
    private void mnuPanaloEntryClick(ActionEvent event) {
        setScene(loadAnimate("PanaloInfo.fxml"));
    }

    @FXML
    private void mnuPanaloRedeemClick(ActionEvent event) {
        setScene(loadAnimate("PanaloRedemption.fxml"));
    }

    @FXML
    private void mnuMCBranchPerformanceClick(ActionEvent event) {
        setScene(loadAnimate("McBranchPerformance.fxml"));
    }

    @FXML
    private void mnuMPBranchPerformanceClick(ActionEvent event) {
        setScene(loadAnimate("MpBranchPerformance.fxml"));
    }

    @FXML
    private void mnuMCAreaPerformanceClick(ActionEvent event) {
        setScene(loadAnimate("McAreaPerformance.fxml"));
    }

    @FXML
    private void mnuMPAreaPerformanceClick(ActionEvent event) {
        setScene(loadAnimate("MpAreaPerformance.fxml"));
    }

    @FXML
    private void mnuRaffleParameterClick(ActionEvent event) {
        setScene(loadAnimate("RaffleParameter.fxml"));
    }

    @FXML
    private void mnuRaffleReportClick(ActionEvent event) {
        setScene(loadAnimate("RaffleReports.fxml"));
    }

    @FXML
    private void mnuMCImagesClick(ActionEvent event) {
        setScene(loadAnimate("MCImages.fxml"));
    }

    @FXML
    private void mnuPacitaEvaluationHisClick(ActionEvent event) {
        setScene(loadAnimate("PacitaEvaluationHis.fxml"));
    }

    @FXML
    private void mnuPacitaSumReportClick(ActionEvent event) {
        setScene(loadAnimate("PacitaEvalSummarizedReport.fxml"));
    }

    @FXML
    private void mnuPacitaDetReportClick(ActionEvent event) {
        setScene(loadAnimate("PacitaEvalDetailedReport.fxml"));
    }

    @FXML
    private void mnuPacitaDetRuleReportClick(ActionEvent event) {
        setScene(loadAnimate("PacitaEvalDetailedRulesReport.fxml"));
    }

    @FXML
    private void mnuPacitaTop10ReportClick(ActionEvent event) {
        setScene(loadAnimate("PacitaEvalTop10Report.fxml"));
    }

    private void getTime() {
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

            String formattedTime = timeFormat.format(date);
            String formattedDate = dateFormat.format(date);

            DateAndTime.setText(formattedDate + " || " + formattedTime);

        }),
                new KeyFrame(Duration.seconds(1))
        );

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void initMenu() {
        //control menus to show based on user level or department
        mnuFiles02.setVisible(false);

        //compliance management;
        mnuIncentiveParameter.setVisible("034;026;".contains(oApp.getDepartment()));

        mnuTransactionCashCount.setVisible("034;026;".contains(oApp.getDepartment()));
        mnuInventory01.setVisible("034;026;".contains(oApp.getDepartment()));

        //finance management;
        mnuEmployeeIncentivesBank.setVisible("021;028;026".contains(oApp.getDepartment()));
        mnuTransactionCashCount.setVisible("028;026;".contains(oApp.getDepartment()));
        mnuInventory01.setVisible("028;026;".contains(oApp.getDepartment()));

        if (!mnuIncentiveParameter.isVisible()
                && !mnuEmployeeIncentivesBank.isVisible()) {
            mnuFiles01.setVisible(false);
        } else {
            mnuFiles01.setVisible(true);
        }

//      mnuAuditIncentiveReport.setVisible("015;034;022;028;026;036;038".contains(oApp.getDepartment()));
        mnuAuditIncentiveReportNew.setVisible("015;034;022;028;026;036;038".contains(oApp.getDepartment()));
        mnuAuditDeptIncentiveReport.setVisible("026;028;".contains(oApp.getDepartment()));
        mnuAuditBankReport.setVisible("026;028;021;".contains(oApp.getDepartment()));

        mnuTransactionIncentives.setVisible("015;022;026;028;034;036;038".contains(oApp.getDepartment()));;

        System.out.println(oApp.getDepartment() + oApp.getUserLevel());
        mnuIncentiveReleasing.setVisible("028;026".contains(oApp.getDepartment())
                && oApp.getUserLevel() > UserRight.ENCODER);
        mnuIncentiveReleaseHistory.setVisible("028;026".contains(oApp.getDepartment())
                && oApp.getUserLevel() > UserRight.ENCODER);

    }
}
