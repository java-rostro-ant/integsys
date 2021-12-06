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
public class TableIncentives {
    private SimpleStringProperty incindex01;
    private SimpleStringProperty incindex02; 
    private SimpleStringProperty incindex03;
    private SimpleStringProperty incindex04;
    private SimpleStringProperty incindex05;
    private SimpleStringProperty incindex06;
    private SimpleStringProperty incindex07;
    private SimpleStringProperty incindex08;
    
    TableIncentives(String incindex01, 
                    String incindex02, 
                    String incindex03, 
                    String incindex04,
                    String incindex05, 
                    String incindex06, 
                    String incindex07, 
                    String incindex08){
        
        this.incindex01 = new SimpleStringProperty(incindex01);
        this.incindex02 = new SimpleStringProperty(incindex02);
        this.incindex03 = new SimpleStringProperty(incindex03);
        this.incindex04 = new SimpleStringProperty(incindex04);
        this.incindex05 = new SimpleStringProperty(incindex05);
        this.incindex06 = new SimpleStringProperty(incindex06);
        this.incindex07 = new SimpleStringProperty(incindex07);
        this.incindex08 = new SimpleStringProperty(incindex08);
    }


    
    public String getIncindex01(){return incindex01.get();}
    public void setIncindex01(String incindex01){this.incindex01.set(incindex01);}
    
    public String getIncindex02(){return incindex02.get();}
    public void setIncindex02(String incindex02){this.incindex02.set(incindex02);}
    
    public String getIncindex03(){return incindex03.get();}
    public void setIncindex03(String incindex03){this.incindex03.set(incindex03);}
    
    public String getIncindex04(){return incindex04.get();}
    public void setIncindex04(String incindex04){this.incindex04.set(incindex04);}
    
    public String getIncindex05(){return incindex05.get();}
    public void setIncindex05(String incindex05){this.incindex05.set(incindex05);}
    
    public String getIncindex06(){return incindex06.get();}
    public void setIncindex06(String incindex06){this.incindex06.set(incindex06);}
    
    public String getIncindex07(){return incindex07.get();}
    public void setIncindex07(String incindex07){this.incindex07.set(incindex07);}
    
    public String getIncindex08(){return incindex08.get();}
    public void setIncindex08(String incindex08){this.incindex08.set(incindex08);}
    
}
