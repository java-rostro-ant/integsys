/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

/**
 *
 * @author user
 */
public class Release {

    private SimpleStringProperty empIndex01;
    private SimpleStringProperty empIndex02;
    private SimpleStringProperty empIndex03;
    private SimpleStringProperty empIndex04;
    private SimpleStringProperty empIndex05;
    private SimpleStringProperty empIndex06;
    private SimpleStringProperty empIndex07;
    private SimpleStringProperty empIndex08;
    private SimpleStringProperty empIndex09;
    private SimpleStringProperty empIndex10;
    private SimpleStringProperty empIndex11;

    private SimpleStringProperty incIndex01;
    private SimpleStringProperty incIndex02;
    private SimpleStringProperty incIndex03;
    private SimpleStringProperty incIndex04;
    private SimpleStringProperty incIndex05;
    private SimpleStringProperty incIndex06;
    private CheckBox incIndex07;
    Boolean checked;

    public Release(String incIndex01, String incIndex02, String incIndex03,
            String incIndex04, String incIndex05, String incIndex06, CheckBox incIndex07, boolean check) {
        this.incIndex01 = new SimpleStringProperty(incIndex01);
        this.incIndex02 = new SimpleStringProperty(incIndex02);
        this.incIndex03 = new SimpleStringProperty(incIndex03);
        this.incIndex04 = new SimpleStringProperty(incIndex04);
        this.incIndex05 = new SimpleStringProperty(incIndex05);
        this.incIndex06 = new SimpleStringProperty(incIndex06);
        this.incIndex07 = incIndex07;
        checked = check;
    }

    public Release(String incIndex01, String incIndex02, String incIndex03) {
        this.incIndex01 = new SimpleStringProperty(incIndex01);
        this.incIndex02 = new SimpleStringProperty(incIndex02);
        this.incIndex03 = new SimpleStringProperty(incIndex03);
    }

    public Release(String empIndex01, String empIndex02, String empIndex03,
            String empIndex04, String empIndex05, String empIndex06,
            String empIndex07, String empIndex08) {
        this.empIndex01 = new SimpleStringProperty(empIndex01);
        this.empIndex02 = new SimpleStringProperty(empIndex02);
        this.empIndex03 = new SimpleStringProperty(empIndex03);
        this.empIndex04 = new SimpleStringProperty(empIndex04);
        this.empIndex05 = new SimpleStringProperty(empIndex05);
        this.empIndex06 = new SimpleStringProperty(empIndex06);
        this.empIndex07 = new SimpleStringProperty(empIndex07);
        this.empIndex08 = new SimpleStringProperty(empIndex08);
    }
    
        public Release(String empIndex01, String empIndex02, String empIndex03,
            String empIndex04, String empIndex05, String empIndex06,
            String empIndex07, String empIndex08,String empIndex09,String empIndex10,String empIndex11) {
        this.empIndex01 = new SimpleStringProperty(empIndex01);
        this.empIndex02 = new SimpleStringProperty(empIndex02);
        this.empIndex03 = new SimpleStringProperty(empIndex03);
        this.empIndex04 = new SimpleStringProperty(empIndex04);
        this.empIndex05 = new SimpleStringProperty(empIndex05);
        this.empIndex06 = new SimpleStringProperty(empIndex06);
        this.empIndex07 = new SimpleStringProperty(empIndex07);
        this.empIndex08 = new SimpleStringProperty(empIndex08);
        this.empIndex09 = new SimpleStringProperty(empIndex09);
        this.empIndex10 = new SimpleStringProperty(empIndex10);
        this.empIndex11 = new SimpleStringProperty(empIndex11);
    }

    public Release(String empIndex01, String empIndex02, String empIndex03,
            String empIndex04, String empIndex05, String empIndex06,
            String empIndex07) {
        this.empIndex01 = new SimpleStringProperty(empIndex01);
        this.empIndex02 = new SimpleStringProperty(empIndex02);
        this.empIndex03 = new SimpleStringProperty(empIndex03);
        this.empIndex04 = new SimpleStringProperty(empIndex04);
        this.empIndex05 = new SimpleStringProperty(empIndex05);
        this.empIndex06 = new SimpleStringProperty(empIndex06);
        this.empIndex07 = new SimpleStringProperty(empIndex07);
    }

    public String getIncIndex01() {
        return incIndex01.get();
    }

    public void setIncIndex01(String incIndex01) {
        this.incIndex01.set(incIndex01);
    }

    public String getIncIndex02() {
        return incIndex02.get();
    }

    public void setIncIndex02(String incIndex02) {
        this.incIndex02.set(incIndex02);
    }

    public String getIncIndex03() {
        return incIndex03.get();
    }

    public void setIncIndex03(String incIndex03) {
        this.incIndex03.set(incIndex03);
    }

    public String getIncIndex04() {
        return incIndex04.get();
    }

    public void setIncIndex04(String incIndex04) {
        this.incIndex04.set(incIndex04);
    }

    public String getIncIndex05() {
        return incIndex05.get();
    }

    public void setIncIndex05(String incIndex05) {
        this.incIndex05.set(incIndex05);
    }

    public String getIncIndex06() {
        return incIndex06.get();
    }

    public void setIncIndex06(String incIndex06) {
        this.incIndex06.set(incIndex06);
    }

    public CheckBox getIncIndex07() {
        return incIndex07;
    }

    public void setIncIndex07(CheckBox incIndex07) {
        this.incIndex07 = incIndex07;
    }

    public Boolean getIndexIsChecked() {
        return checked;
    }

//    Employe setter / getter
    public String getEmpIndex01() {
        return empIndex01.get();
    }

    public void setEmpIndex01(String empIndex01) {
        this.empIndex01.set(empIndex01);
    }

    public String getEmpIndex02() {
        return empIndex02.get();
    }

    public void setEmpIndex02(String empIndex02) {
        this.empIndex02.set(empIndex02);
    }

    public String getEmpIndex03() {
        return empIndex03.get();
    }

    public void setEmpIndex03(String empIndex03) {
        this.empIndex03.set(empIndex03);
    }

    public String getEmpIndex04() {
        return empIndex04.get();
    }

    public void setEmpIndex04(String empIndex04) {
        this.empIndex04.set(empIndex04);
    }

    public String getEmpIndex05() {
        return empIndex05.get();
    }

    public void setEmpIndex05(String empIndex05) {
        this.empIndex05.set(empIndex05);
    }

    public String getEmpIndex06() {
        return empIndex06.get();
    }

    public void setEmpIndex06(String empIndex06) {
        this.empIndex06.set(empIndex06);
    }

    public String getEmpIndex07() {
        return empIndex07.get();
    }

    public void setEmpIndex07(String empIndex07) {
        this.empIndex07.set(empIndex07);
    }

    public String getEmpIndex08() {
        return empIndex08.get();
    }

    public void setEmpIndex08(String empIndex08) {
        this.empIndex08.set(empIndex08);
    }
    
        public String getEmpIndex09() {
        return empIndex09.get();
    }

    public void setEmpIndex09(String empIndex09) {
        this.empIndex09.set(empIndex09);
    }
    
        public String getEmpIndex10() {
        return empIndex10.get();
    }

    public void setEmpIndex10(String empIndex10) {
        this.empIndex10.set(empIndex10);
    }
    
          public String getEmpIndex11() {
        return empIndex11.get();
    }

    public void setEmpIndex11(String empIndex11) {
        this.empIndex11.set(empIndex11);
    }
}
