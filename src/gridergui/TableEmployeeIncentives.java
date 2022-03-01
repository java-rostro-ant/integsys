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
public class TableEmployeeIncentives {
    private SimpleStringProperty empIncindex01;
    private SimpleStringProperty empIncindex02; 
    private SimpleStringProperty empIncindex03;
    private SimpleStringProperty empIncindex04;
    private SimpleStringProperty empIncindex05;
    
    TableEmployeeIncentives(String empIncindex01, 
                    String empIncindex02, 
                    String empIncindex03, 
                    String empIncindex04, 
                    String empIncindex05){
        
        this.empIncindex01 = new SimpleStringProperty(empIncindex01);
        this.empIncindex02 = new SimpleStringProperty(empIncindex02);
        this.empIncindex03 = new SimpleStringProperty(empIncindex03);
        this.empIncindex04 = new SimpleStringProperty(empIncindex04);
        this.empIncindex05 = new SimpleStringProperty(empIncindex05);
    }


    
    public String getEmpIncindex01(){return empIncindex01.get();}
    public void setEmpIncindex01(String empIncindex01){this.empIncindex01.set(empIncindex01);}
    
    public String getEmpIncindex02(){return empIncindex02.get();}
    public void setEmpIncindex02(String empIncindex02){this.empIncindex02.set(empIncindex02);}
    
    public String getEmpIncindex03(){return empIncindex03.get();}
    public void setEmpIncindex03(String empIncindex03){this.empIncindex03.set(empIncindex03);}
    
    public String getEmpIncindex04(){return empIncindex04.get();}
    public void setEmpIncindex04(String empIncindex04){this.empIncindex04.set(empIncindex04);}
    
    public String getEmpIncindex05(){return empIncindex05.get();}
    public void setEmpIncindex05(String empIncindex05){this.empIncindex04.set(empIncindex05);}
    
}
