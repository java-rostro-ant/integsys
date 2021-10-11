package app;

import gridergui.GriderGui;
import javafx.application.Application;

public class Login {
    public static void main(String [] args){
        GriderGui instance = new GriderGui();
        Application.launch(instance.getClass());
    }
}