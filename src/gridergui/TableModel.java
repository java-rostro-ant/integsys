/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author user
 */
public class TableModel {
    private SimpleStringProperty index01;
    private SimpleStringProperty index02;
    private SimpleStringProperty index03;
    private SimpleStringProperty index04; 
    private SimpleStringProperty index05;
    private SimpleStringProperty index06; 
    private SimpleStringProperty index07;
    private SimpleStringProperty index08; 
    private SimpleStringProperty index09;
    private SimpleStringProperty index10; 
    private SimpleStringProperty index11;
    private SimpleStringProperty index12;
    private SimpleStringProperty index13;
    private SimpleStringProperty index14;
    private SimpleStringProperty index15;
    
    private SimpleObjectProperty nField01;
    private SimpleObjectProperty nField02;
    private SimpleObjectProperty nField03;
    private Boolean isImagePriority;
    
    TableModel(String index01, String index02, String index03) {
         this.index01 = new SimpleStringProperty(index01);
         this.index02 = new SimpleStringProperty(index02);
         this.index03 = new SimpleStringProperty(index03);
        }
    TableModel(String index01,
               String index02,
               String index03,
               String index04,
               String index05,
               String index06,
               String index07,
               String index08){
        
        this.index01 = new SimpleStringProperty(index01);
        this.index02 = new SimpleStringProperty(index02);
        this.index03 = new SimpleStringProperty(index03);
        this.index04 = new SimpleStringProperty(index04);
        this.index05 = new SimpleStringProperty(index05);
        this.index06 = new SimpleStringProperty(index06);
        this.index07 = new SimpleStringProperty(index07);
        this.index08 = new SimpleStringProperty(index08);
    }
    TableModel(String index01,
               String index02,
               String index03,
               String index04,
               String index05){
        
        this.index01 = new SimpleStringProperty(index01);
        this.index02 = new SimpleStringProperty(index02);
        this.index03 = new SimpleStringProperty(index03);
        this.index04 = new SimpleStringProperty(index04);
        this.index05 = new SimpleStringProperty(index05);
    }
    
    
    TableModel(String index01,
               String index02,
               String index03,
               Number nField01,
               Number nField02,
               Number nField03){
        
        this.index01 = new SimpleStringProperty(index01);
        this.index02 = new SimpleStringProperty(index02);
        this.index03 = new SimpleStringProperty(index03);
        this.nField01 = new SimpleObjectProperty(nField01);
        this.nField02 = new SimpleObjectProperty(nField02);
        this.nField03 = new SimpleObjectProperty(nField03);
    }
    
        TableModel(String index01,
               String index02,
               String index03,
               String index04,
               String index05,
               String index06,
               String index07,
               String index08,
               String index09,
               String index10){
        
        this.index01 = new SimpleStringProperty(index01);
        this.index02 = new SimpleStringProperty(index02);
        this.index03 = new SimpleStringProperty(index03);
        this.index04 = new SimpleStringProperty(index04);
        this.index05 = new SimpleStringProperty(index05);
        this.index06 = new SimpleStringProperty(index06);
        this.index07 = new SimpleStringProperty(index07);
        this.index08 = new SimpleStringProperty(index08);
        this.index09 = new SimpleStringProperty(index09);
        this.index10 = new SimpleStringProperty(index10);
    }
        
                TableModel(String index01,
               String index02,
               String index03,
               String index04,
               String index05,
               String index06,
               String index07,
               String index08,
               String index09,
               String index10,
               String index11,
               String index12,
               String index13,
               String index14,
               String index15){
        
        this.index01 = new SimpleStringProperty(index01);
        this.index02 = new SimpleStringProperty(index02);
        this.index03 = new SimpleStringProperty(index03);
        this.index04 = new SimpleStringProperty(index04);
        this.index05 = new SimpleStringProperty(index05);
        this.index06 = new SimpleStringProperty(index06);
        this.index07 = new SimpleStringProperty(index07);
        this.index08 = new SimpleStringProperty(index08);
        this.index09 = new SimpleStringProperty(index09);
        this.index10 = new SimpleStringProperty(index10);
        this.index11 = new SimpleStringProperty(index11);
        this.index12 = new SimpleStringProperty(index12);
        this.index13 = new SimpleStringProperty(index13);
        this.index14 = new SimpleStringProperty(index14);
        this.index15 = new SimpleStringProperty(index15);
    }

    public String getIndex01(){return index01.get();}
    public void setIndex01(String index01){this.index01.set(index01);}
    
    public String getIndex02(){return index02.get();}
    public void setIndex02(String index02){this.index02.set(index02);}
    
    public String getIndex03(){return index03.get();}
    public void setIndex03(String index03){this.index03.set(index03);}
    
    public String getIndex04(){return index04.get();}
    public void setIndex04(String index04){this.index04.set(index04);}
    
    public String getIndex05(){return index05.get();}
    public void setIndex05(String index05){this.index05.set(index05);}
    
    public String getIndex06(){return index06.get();}
    public void setIndex06(String index06){this.index06.set(index06);}
    
    public String getIndex07(){return index07.get();}
    public void setIndex07(String index07){this.index07.set(index07);}
    
    public String getIndex08(){return index08.get();}
    public void setIndex08(String index08){this.index08.set(index08);}
    
    public String getIndex09(){return index09.get();}
    public void setIndex09(String index09){this.index09.set(index09);}
    
    public String getIndex10(){return index10.get();}
    public void setIndex10(String index10){this.index10.set(index10);}
    
    public String getIndex11(){return index11.get();}
    public void setIndex11(String index11){this.index11.set(index11);}
    
    public String getIndex12(){return index12.get();}
    public void setIndex12(String index12){this.index12.set(index12);}
    
    public String getIndex13(){return index13.get();}
    public void setIndex13(String index13){this.index13.set(index13);}
    
    public String getIndex14(){return index14.get();}
    public void setIndex14(String index14){this.index14.set(index14);}
    
    public String getIndex15(){return index15.get();}
    public void setIndex15(String index15){this.index15.set(index15);}
        
    
        public StringProperty index01Property() {
        return index01;
    }
    
    public StringProperty index02Property() {
        return index02;
    }
    
    public StringProperty index03Property() {
        return index03;
    }
    
    public StringProperty index04Property() {
        return index04;
    }    
    public StringProperty index05Property() {
        return index05;
    }
    
    public StringProperty index06Property() {
        return index06;
    }
    
    
    public Object getnField01(){return nField01.get();}
    public void setnField01(Number nField01){this.nField01.set(nField01);}
    
    public Object getnField02(){return nField02.get();}
    public void setnField02(Number nField02){this.nField02.set(nField02);}
    public Object getnField03(){return nField03.get();}
    public void setnField03(Number nField03){this.nField03.set(nField03);}
    
}
