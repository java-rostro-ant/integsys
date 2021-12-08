/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridergui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author user
 */
public class Util {
     public static String resourcesFolder() {
        Path folder = Paths.get(System.getProperty("user.dir"));
        while (folder != null) {
            Path potentialResourcesFolder = folder.resolve("resources");
            if (Files.exists(potentialResourcesFolder))
                return potentialResourcesFolder.toString() + "/";
            folder = folder.getParent();
        }

        throw new IllegalStateException("Resources folder was not found");
    }
}
