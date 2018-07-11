package miguelcalado.restauracaomenus;

import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by snovaisg on 1/31/18.
 */

public class checkAtualizadoPorRestClass {
    private String restaurante;

    String timeStamp;

    // é a lista que está na app myUpload. Não mexer nos nomes
    String[] JsonRest = {"Teresa","My Spot","Cantina","Sector + Dep","Sector + Ed.7","Bar D. Lídia","Girassol","Casa do P.","Bar Campus","C@m. Come"};


    public Boolean setRest(String rest){
        restaurante = rest;
        Boolean val = false;
        for (int e = 0;e < JsonRest.length;e++){
            if (JsonRest[e].equals(restaurante))
                val = true;
        }
        return val;
    }

    public int isAtualizado(JSONObject Obj){
        try {
            if (restaurante == null)
                return -1;

            timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
            String restWeekId = Obj.getJSONObject(restaurante).getString("weekId");

            if (!timeStamp.equals(restWeekId))
                return 0;
            return 1;
        }
        catch(Exception e){
            Log.e("error","error bas");
            return -2;
        }
    }

}
