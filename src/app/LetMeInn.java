package app;

import gridergui.GriderGui;
import javafx.application.Application;
import org.rmj.appdriver.GRider;

public class LetMeInn {
    public static void main(String [] args){                
        String path;
        
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            path = "D:/GGC_Java_Systems";
        }
        else{
            path = "/srv/GGC_Java_Systems";
        }
        
        System.setProperty("sys.default.path.config", path); 
        
        GRider oApp = new GRider();
        
        if (!oApp.loadEnv("IntegSys")) {
            System.err.println(oApp.getErrMsg());
            System.exit(1);
        }
        // Buclares, Loraine Lim M001240020
        //Cuison, Michael Torres M001111122
        if (!oApp.logUser("IntegSys", "M001240020")) { //M001080006
            System.err.println(oApp.getErrMsg());
            System.exit(1);
        }   
        
        GriderGui instance = new GriderGui();
        instance.setGRider(oApp);
        
        Application.launch(instance.getClass());
    }
}