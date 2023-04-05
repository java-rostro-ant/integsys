/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author user
 */
public class RaffleReportsModel {
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
    private final String[] LblStatus = {"Answered","Customer Not Around","Invalid Number","Wrong Number",
                                        "Can't Be Reached"};
     private final String[] cStatus = {"NON-WINNER","WINNER"};

    public RaffleReportsModel(String index01, String index02, String index03, 
            String index04,String index05,String index06,
            String index07,String index08,String index09,
            String index10,String index11,String index12){
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
        

    }
    
    public RaffleReportsModel(String index01, String index02, String index03, 
            String index04,String index05,String index06,
            String index07,String index08,String index09,
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
 
    public String getindex01() {
        return index01.get();
    }

    public String getindex02() {
        return index02.get();
    }

    public String getindex03() {
        if (index03.get().equalsIgnoreCase("SC")){
            index03.set(String.valueOf(LblStatus[0]));
        }else if (index03.get().equalsIgnoreCase("CA")){
            index03.set(String.valueOf(LblStatus[1]));
        }else if (index03.get().equalsIgnoreCase("IN")){
            index03.set(String.valueOf(LblStatus[2]));
        }else if (index03.get().equalsIgnoreCase("WN")){
            index03.set(String.valueOf(LblStatus[3]));
        }else if (index03.get().equalsIgnoreCase("")){
            index03.set(String.valueOf(LblStatus[4]));
        }
        return index03.get();
    }

    public String getindex04() {
        return index04.get();
    }

    public String getindex05() {
        return index05.get();
    }

    public String getindex06() {
        return index06.get();
    }

    public String getindex07() {
        if (index07.get().equalsIgnoreCase("0")){
            index07.set(String.valueOf(cStatus[0]));
        }else if (index07.get().equalsIgnoreCase("1")){
            index07.set(String.valueOf(cStatus[1]));
        }
        return index07.get();
    }
    
    public String getindex08() {
        return index08.get();
    }
    public String getindex09() {
        return index09.get();
    }
        public String getindex10() {
        return index10.get();
    }
        
    public String getindex11() {
        return index11.get();
    }
        
    public String getindex12() {
        return index12.get();
    }
    

}

