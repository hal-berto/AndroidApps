package librorisposte.android.app.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ReadResponse {

    public String readNewResponse(){
        String response = "";
        try {
            Document doc = Jsoup.connect("http://www.bryonia.it/accessibile/risposte.php").get();
            response = doc.title();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static void main(String[] args){
        ReadResponse rr = new ReadResponse();
        System.out.println(rr.readNewResponse());
    }

}
