/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rmj.integsys.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GriderGui extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {        
        FXMLLoader view = new FXMLLoader();
        view.setLocation(getClass().getResource("FXMLDocument.fxml"));
        
        FXMLDocumentController controller = new FXMLDocumentController();
        
        view.setController(controller);
        
        Parent parent = view.load();
        Scene scene = new Scene(parent);
        
        //get the screen size
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        //stage.getIcons().add(new Image(pxeStageIcon));
        stage.setTitle("GhostRider");
        
        // set stage as maximized but not full screen
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
