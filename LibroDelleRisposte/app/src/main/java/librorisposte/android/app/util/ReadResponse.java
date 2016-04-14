package librorisposte.android.app.util;

import android.os.AsyncTask;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ReadResponse extends AsyncTask<String, Void, String> {

    private String readNewResponse(){
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

    @Override
    protected String doInBackground(String... params) {
        return readNewResponse();
    }
}
