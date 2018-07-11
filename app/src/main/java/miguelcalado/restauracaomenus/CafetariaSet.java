package miguelcalado.restauracaomenus;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Diogo on 22/02/2018.
 */

public class CafetariaSet {
    String ResName;
    JsonDic myJson;
    ImageView plusIcon;
    TextView title,textToPut, priceToPut;
    String tag;
    boolean moreToSee;
    ArrayList<String> text, price;
    int maxToSee;

    boolean showall;

    private static int PLUS_ICON =R.drawable.square_up;
    private static int LESS_ICON =R.drawable.squar_down;

    public CafetariaSet(String ResName,JsonDic myJson, TextView title,ImageView plusIcon, TextView textToPut, TextView priceToPut, String tag, int maxToSee) {
        this.title=title;
        this.ResName=ResName;
        this.myJson = myJson;
        this.plusIcon = plusIcon;
        this.textToPut = textToPut;
        this.priceToPut = priceToPut;
        this.tag = tag;
        this.maxToSee = maxToSee;
        this.moreToSee = false;
        text = new ArrayList<>();
        price = new ArrayList<>();
        showall=false;
        downloadAndSetText();
    }

    private void downloadAndSetText() {
        try {
            String download[] = myJson.getDicArray(ResName,"Cafetaria", tag);

            getStrings(download);
            textToPut.setText(putTextInicial(text));
            if (price.isEmpty())
                priceToPut.setVisibility(View.GONE);
            else
                priceToPut.setText(putTextInicial(price));
            if (moreToSee)
                plusIcon.setVisibility(View.VISIBLE);
            else
                plusIcon.setVisibility(View.GONE);
        } catch (JSONException e) {
            title.setVisibility(View.GONE);
            textToPut.setVisibility(View.GONE);
            priceToPut.setVisibility(View.GONE);
            plusIcon.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private String putTextInicial(ArrayList<String> text) {
        int max;
        if(moreToSee) {
            if (showall)
                max = text.size();
            else
                max = maxToSee;
        }
        else
            max= text.size();
        return maxToPrint(text,max);
    }

    private void getStrings(String[] download) {
        int toSeePrice = 0, toSeeText = 0;
        for (int i = 0; i < download.length; i++) {
            if (!download[i].contains("€")) {
                this.text.add(download[i]);
                toSeeText++;
            } else {
                this.price.add(download[i]);
                toSeePrice++;
            }
        }
        if (toSeeText > maxToSee || toSeePrice > maxToSee)
            moreToSee = true;
    }

    private String maxToPrint(ArrayList textArray,int max) {
        String text=new String();
        for(int i=0;i<max;i++){
            text+=textArray.get(i)+"\n";
        }
        return text;
    }

    public ImageView getPlusIcon() {
        return plusIcon;
    }

    public void showTextOnClick(){
        if(moreToSee) {
            showall=!showall;
            textToPut.setText(putTextInicial(text));
            priceToPut.setText(putTextInicial(price));
        }
        if(showall)
            plusIcon.setImageResource(PLUS_ICON);
        else
            plusIcon.setImageResource(LESS_ICON);
    }
}


