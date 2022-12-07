/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author user
 */
public class PanaloInfoRedeemModel {
    private SimpleStringProperty PInfoRedeemIndex01;
    private SimpleStringProperty PInfoRedeemIndex02;
    private SimpleStringProperty PInfoRedeemIndex03;
    private SimpleStringProperty PInfoRedeemIndex04; 
    private SimpleStringProperty PInfoRedeemIndex05;
    private SimpleStringProperty PInfoRedeemIndex06; 
    private SimpleStringProperty PInfoRedeemIndex07;
    private SimpleStringProperty PInfoRedeemIndex08; 


    PanaloInfoRedeemModel(String PInfoRedeemIndex01,
               String PInfoRedeemIndex02,
               String PInfoRedeemIndex03,
               String PInfoRedeemIndex04,
               String PInfoRedeemIndex05,
               String PInfoRedeemIndex06,
               String PInfoRedeemIndex07,
               String PInfoRedeemIndex08){
        
        this.PInfoRedeemIndex01 = new SimpleStringProperty(PInfoRedeemIndex01);
        this.PInfoRedeemIndex02 = new SimpleStringProperty(PInfoRedeemIndex02);
        this.PInfoRedeemIndex03 = new SimpleStringProperty(PInfoRedeemIndex03);
        this.PInfoRedeemIndex04 = new SimpleStringProperty(PInfoRedeemIndex04);
        this.PInfoRedeemIndex05 = new SimpleStringProperty(PInfoRedeemIndex05);
        this.PInfoRedeemIndex06 = new SimpleStringProperty(PInfoRedeemIndex06);
        this.PInfoRedeemIndex07 = new SimpleStringProperty(PInfoRedeemIndex07);
        this.PInfoRedeemIndex08 = new SimpleStringProperty(PInfoRedeemIndex08);

    }
    public String getPInfoRedeemIndex01() {
        return PInfoRedeemIndex01.get();
    }

    public String getPInfoRedeemIndex02() {
        return PInfoRedeemIndex02.get();
    }

    public String getPInfoRedeemIndex03() {
        return PInfoRedeemIndex03.get();
    }

    public String getPInfoRedeemIndex04() {
        return PInfoRedeemIndex04.get();
    }

    public String getPInfoRedeemIndex05() {
        return PInfoRedeemIndex05.get();
    }

    public String getPInfoRedeemIndex06() {
        return PInfoRedeemIndex06.get();
    }

    public String getPInfoRedeemIndex07() {
        return PInfoRedeemIndex07.get();
    }

    public String getPInfoRedeemIndex08() {
        return PInfoRedeemIndex08.get();
    }
}
