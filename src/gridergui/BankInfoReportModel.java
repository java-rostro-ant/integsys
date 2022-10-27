/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gridergui;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author User
 */
public class BankInfoReportModel {
    private SimpleStringProperty bankInfo05;
    private SimpleStringProperty bankInfo02;
    private SimpleStringProperty bankInfo03;
    private SimpleStringProperty bankInfo04; 
    private SimpleStringProperty bankInfo01; 
    
    BankInfoReportModel(String bankInfo05,
               String bankInfo02,
               String bankInfo03,
               String bankInfo04,
               String bankInfo01){
        
        this.bankInfo05 = new SimpleStringProperty(bankInfo05);
        this.bankInfo02 = new SimpleStringProperty(bankInfo02);
        this.bankInfo03 = new SimpleStringProperty(bankInfo03);
        this.bankInfo04 = new SimpleStringProperty(bankInfo04);
        this.bankInfo01 = new SimpleStringProperty(bankInfo01);
    }

    public String getBankInfo05() {
        return bankInfo05.get();
    }

    public String getBankInfo02() {
        return bankInfo02.get();
    }

    public String getBankInfo03() {
        return bankInfo03.get();
    }

    public String getBankInfo04() {
        return bankInfo04.get();
    }
    public String getBankInfo01() {
        return bankInfo01.get();
    }
}
