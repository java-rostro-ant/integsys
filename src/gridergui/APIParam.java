/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gridergui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rmj.appdriver.SQLUtil;

/**
 *
 * @author user
 */
public class APIParam {
    public static HashMap getHeader1() {
        try {
            String clientid = "GGC_BM001";
            String productid = "IntegSys";
            String imei = InetAddress.getLocalHost().getHostName();
            String user = "M001111122";
            String log = "";
            
            Calendar calendar = Calendar.getInstance();
            Map<String, String> headers =
                    new HashMap<String, String>();
            headers.put("Accept", "application/json");
            headers.put("Content-Type", "application/json");
            headers.put("g-api-id", productid);
            headers.put("g-api-imei", imei);
            
            headers.put("g-api-key", SQLUtil.dateFormat(calendar.getTime(), "yyyyMMddHHmmss"));
            headers.put("g-api-hash", org.apache.commons.codec.digest.DigestUtils.md5Hex((String)headers.get("g-api-imei") + (String)headers.get("g-api-key")));
            headers.put("g-api-client", clientid);
            headers.put("g-api-user", user);
            headers.put("g-api-log", log);
            headers.put("g-api-mobile", "09260375777");
            headers.put("g-char-request", "UTF-8");
            headers.put("g-api-token", "");
            
            return (HashMap) headers;
        } catch (UnknownHostException ex) {
            Logger.getLogger(APIParam.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
