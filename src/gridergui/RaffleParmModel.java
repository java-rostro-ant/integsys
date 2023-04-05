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
public class RaffleParmModel {
    private SimpleStringProperty DetIndex01;   
    private SimpleStringProperty DetIndex02;  
    private SimpleStringProperty DetIndex03;  
    private SimpleStringProperty DetIndex04;    
    private SimpleStringProperty DetIndex05;   
    private SimpleStringProperty DetIndex06;  
    private SimpleStringProperty DetIndex07;
    private final String[] LblWinner = {"OFFICER","ASSOCIETE","CUSTOMER","SELLING BRANCH"};


    public RaffleParmModel(String DetIndex01, String DetIndex02, String DetIndex03, 
            String DetIndex04,String DetIndex05,String DetIndex06,
            String DetIndex07){
        this.DetIndex01 = new SimpleStringProperty(DetIndex01);
        this.DetIndex02 = new SimpleStringProperty(DetIndex02);
        this.DetIndex03 = new SimpleStringProperty(DetIndex03);
        this.DetIndex04 = new SimpleStringProperty(DetIndex04);
        this.DetIndex05 = new SimpleStringProperty(DetIndex05);
        this.DetIndex06 = new SimpleStringProperty(DetIndex06);
        this.DetIndex07 = new SimpleStringProperty(DetIndex07);
        

    }
 
    public String getDetIndex01() {
        return DetIndex01.get();
    }

    public String getDetIndex02() {
        return DetIndex02.get();
    }

    public String getDetIndex03() {
        if (DetIndex03.get().equalsIgnoreCase("0")){
            DetIndex03.set(String.valueOf(LblWinner[0]));
        }else if (DetIndex03.get().equalsIgnoreCase("1")){
            DetIndex03.set(String.valueOf(LblWinner[1]));
        }else if (DetIndex03.get().equalsIgnoreCase("2")){
            DetIndex03.set(String.valueOf(LblWinner[2]));
        }else if (DetIndex03.get().equalsIgnoreCase("3")){
            DetIndex03.set(String.valueOf(LblWinner[3]));
        }
        return DetIndex03.get();
    }

    public String getDetIndex04() {
        return DetIndex04.get();
    }

    public String getDetIndex05() {
        return DetIndex05.get();
    }

    public String getDetIndex06() {
        return DetIndex06.get();
    }

    public String getDetIndex07() {
        return DetIndex07.get();
    }


}

