/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import java.io.FileNotFoundException;
import java.net.URL;
import javafx.scene.layout.Pane;

/**
 *
 * @author user
 */
public class FxmlLoader {
private Pane view;
    
    public Pane getPage(String filename){
        try {
            URL fileUrl = GriderGui.class.getResource("/gridergui/" + filename + ".fxml");
                System.out.println(filename + ".fxml was selected");
            if (fileUrl == null){
                throw new java.io.FileNotFoundException("FXML File can't be found");
            }
            return new FxmlLoader().load(fileUrl);
        }
        catch(FileNotFoundException e){
            System.out.println("No Page " + filename + " Please Check FxmlLoader.");
        }
    return  null;
    }

    private Pane load(URL fileUrl) {
        return view;
    }

    private static class GriderGui {

        public GriderGui() {
        }
    }
}
