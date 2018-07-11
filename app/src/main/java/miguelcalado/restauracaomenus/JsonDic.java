package miguelcalado.restauracaomenus;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static miguelcalado.restauracaomenus.Notification.restaurante;

public class JsonDic implements Serializable {

    private JSONObject myJson;

    public JsonDic(String filename, Context context) {
        String JsonData = "";
        try {
            FileInputStream fis = context.openFileInput(filename);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            JsonData = new String(buffer);
            Log.d(TAG, JsonData);
            myJson = new JSONObject(JsonData);
            int a = 1 + 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sgetString(String Rest, String elem) throws JSONException {
        JSONObject REST = myJson.getJSONObject(Rest);
        String surname = REST.getString(elem);
        return surname;
    }

    public String[] getStringArray(String Rest, String elem) throws JSONException {
        JSONObject REST = myJson.getJSONObject(Rest);
        JSONArray jArr = REST.getJSONArray(elem);
        String[] list = new String[jArr.length()];
        for (int i = 0; i < jArr.length(); i++) {
            list[i] = jArr.getString(i);
        }
        return list;
    }

    public String getWeekId() throws JSONException {
        return myJson.getString("weekId");
    }

    public JSONObject getMyJson() {
        return myJson;
    }

    public String[] getDicArray(String Rest, String Dic, String elem) throws JSONException {
        JSONObject REST = myJson.getJSONObject(Rest);
        JSONObject caft = REST.getJSONObject(Dic);
        JSONArray jArr = caft.getJSONArray(elem);
        String[] list = new String[jArr.length()];
        for (int i = 0; i < jArr.length(); i++) {
            list[i] = jArr.getString(i);
        }
        return list;
    }

    public String[] getNotifications() {
        try {
            ArrayList<String> notificationJson = new ArrayList<>();
            for (int i = 0; i < restaurante.length; i++) {
                try {
                    String[] notificationArray = getStringArray("notifications", restaurante[i]);
                    if (notificationArray != null) {
                        arrayToString(notificationArray, restaurante[i], notificationJson);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (!notificationJson.isEmpty()) {
                String[] notification = new String[notificationJson.size()];
                for (int i = 0; i < notificationJson.size(); i++) {
                    notification[i] = notificationJson.get(i);
                }
                return notification;
            } else
                return null;
        } catch (NullPointerException e) {
            Log.i("JSON", "GET NOTIFICATION == NULL");
            return null;
        }
    }

    private void arrayToString(String[] notificationArray, String ResName, ArrayList<String> notificationJson) {
        for (int i = 0; i < notificationArray.length; i++) {
            if (i % 2 == 0)
                notificationJson.add(ResName + " - " + notificationArray[i]);
            else
                notificationJson.add(notificationArray[i]);
        }
    }

}