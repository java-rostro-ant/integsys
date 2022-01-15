package app;

import gridergui.GriderGui;
import javafx.application.Application;
import org.rmj.appdriver.GRider;

public class Login {
    public static void main(String [] args){        
        if (args.length != 2){
            System.out.println("Invalid parameter detected.");
            System.exit(1);
        }
        
        String path;
        
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            path = "D:/GGC_Java_Systems";
        }
        else{
            path = "/srv/GGC_Java_Systems";
        }
        
        System.setProperty("sys.default.path.config", path); 
        
        GRider oApp = new GRider();
        
        if (!oApp.loadEnv(args[0])) {
            System.err.println(oApp.getErrMsg());
            System.exit(1);
        }
        
        if (!oApp.logUser(args[0], args[1])) {
            System.err.println(oApp.getErrMsg());
            System.exit(1);
        }   
        
        GriderGui instance = new GriderGui();
        instance.setGRider(oApp);
        
        Application.launch(instance.getClass());
    }
}