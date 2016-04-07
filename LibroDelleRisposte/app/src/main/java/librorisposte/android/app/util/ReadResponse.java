package librorisposte.android.app.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ReadResponse {

    public String readNewResponse(){
        String response = "";
        try {
            Document doc = Jsoup.connect("http://www.bryonia.it/accessibile/risposte.php").get();
            String textToParse = doc.select("p").html();

            if(StringUtils.isBlank(textToParse)){
                return response;
            }

            String[] splitted = textToParse.split("(<br>)");
            for(int i = 1; i < splitted.length; i++){
                if(StringUtils.isNotBlank(splitted[i])){
                    response = splitted[i].trim();
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void main(String[] args){
        ReadResponse rr = new ReadResponse();
        for(int i = 0; i < 100; i++) {
            System.out.println(rr.readNewResponse());
        }
    }

}
