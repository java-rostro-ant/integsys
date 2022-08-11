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
public class DeptIncentivesModel {
    private SimpleStringProperty DetIndex01;   
    private SimpleStringProperty DetIndex02;  
    private SimpleStringProperty DetIndex03;  
    private SimpleStringProperty DetIndex04;    
    private SimpleStringProperty DetIndex05;   
    private SimpleStringProperty DetIndex06;  
    private SimpleStringProperty DetIndex07;  
    private SimpleStringProperty DetIndex08;    
 


    public DeptIncentivesModel(String DetIndex01, String DetIndex02, String DetIndex03, 
            String DetIndex04,String DetIndex05,String DetIndex06,
            String DetIndex07,String DetIndex08){
        this.DetIndex01 = new SimpleStringProperty(DetIndex01);
        this.DetIndex02 = new SimpleStringProperty(DetIndex02);
        this.DetIndex03 = new SimpleStringProperty(DetIndex03);
        this.DetIndex04 = new SimpleStringProperty(DetIndex04);
        this.DetIndex05 = new SimpleStringProperty(DetIndex05);
        this.DetIndex06 = new SimpleStringProperty(DetIndex06);
        this.DetIndex07 = new SimpleStringProperty(DetIndex07);
        this.DetIndex08 = new SimpleStringProperty(DetIndex08);

    }
    public String getDetIndex01() {
        return DetIndex01.get();
    }

    public String getDetIndex02() {
        return DetIndex02.get();
    }

    public String getDetIndex03() {
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

    public String getDetIndex08() {
        return DetIndex08.get();
    }


}
