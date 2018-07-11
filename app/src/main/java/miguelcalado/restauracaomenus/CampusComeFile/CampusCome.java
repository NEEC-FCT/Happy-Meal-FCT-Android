package miguelcalado.restauracaomenus.CampusComeFile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

import me.relex.circleindicator.CircleIndicator;
import miguelcalado.restauracaomenus.CustomPagerAdapter2;
import miguelcalado.restauracaomenus.DataHolder;
import miguelcalado.restauracaomenus.JsonDic;
import miguelcalado.restauracaomenus.MapsActivity;
import miguelcalado.restauracaomenus.R;
import miguelcalado.restauracaomenus.alignPrices;
import miguelcalado.restauracaomenus.checkAtualizadoPorRestClass;
//import com.example.android.RestauracaoFCT.R;

/**
 * Created by Miguel-PC on 26/08/2017.
 */

public class CampusCome extends AppCompatActivity {

    checkAtualizadoPorRestClass checkAtualiz;

    String filename;
    String restaurante = "C@m. Come";

    private boolean counter = false;
    /*
    int [] mResources = {
            R.drawable.come5,
            R.drawable.come6,
            R.drawable.come8,
            R.drawable.come7,
    };*/
    String[] mResources = {"https://i.imgur.com/HfAvvpj.png", "https://i.imgur.com/sCKEL0t.png", "https://i.imgur.com/dgaOMgt.png",
            "https://i.imgur.com/0rUqibY.png"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campuscome);

        filename = DataHolder.getInstance().getDataFilename();

        CustomPagerAdapter2 mCustomPagerAdapter = new CustomPagerAdapter2(this, mResources);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);

        ImageView GeoImage = (ImageView) findViewById(R.id.pinpoint);
        GeoImage.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                Intent start = new Intent(CampusCome.this, MapsActivity.class);
                start.putExtra("placeToSee","C@mpus Come");
                startActivity(start);
            }
        });

        TextView restaurantName= (TextView) findViewById(R.id.rest_name);
        restaurantName.setText("C@mpus Come");

        TextView localizacao = (TextView) findViewById(R.id.location_textView);
        localizacao.setText("Edifício da Cantina - Ao lado do Duplix");

        TextView horario = (TextView) findViewById(R.id.horarioCTextview);
        horario.setText("Seg|Sex 9 às 18");
        //Ta aberto desde as 9->18
        TextView almocoTime = (TextView) findViewById(R.id.almoçoTextView);
        almocoTime.setText("Almoço das 12 às 15:30");

        TextView pratoPrincipal = (TextView) findViewById(R.id.jantarTextView);
        pratoPrincipal.setText("Prato 8€");

        TextView ppPrice = (TextView) findViewById(R.id.vegetariano);
        ppPrice.setText("Pronto a levar - 6.50€");

        try {

            File file = createFile(filename);
            JSONObject test = readJsonFromFile(filename);
            Log.d("testt", test.toString());
            checkAtualiz = new checkAtualizadoPorRestClass();
            if ( !checkAtualiz.setRest(restaurante))
                throw new IllegalArgumentException("INVALID - Nome do restaurante n coincide com a lista do Json"); ;

            int i = checkAtualiz.isAtualizado(test);
            TextView tv = (TextView) findViewById(R.id.tvEstado);
            if (i == 1){
                tv.setText("Atualizado");
                tv.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
            else{
                tv.setText("Desatualizado");
                tv.setTextColor(getResources().getColor(R.color.red));
            }
            Log.d("wagerr",String.valueOf(i));
        } catch (Exception e){
            Log.d("testt", "error");
        }

        TextView menu = (TextView) findViewById(R.id.menu_textView);
        menu.setText("Ementa");
        int aa = menu.getCurrentTextColor();

        try {
            JsonDic myJson = new JsonDic(filename, CampusCome.this);

            try {
                LinearLayout peixeLayout = (LinearLayout) findViewById(R.id.sopaLayout);
                String sopa[] = myJson.getStringArray("C@m. Come", "sopa");
                alignPrices.getInstance().alignOne(sopa, this, aa, peixeLayout);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                LinearLayout pratoDiaLayout = (LinearLayout) findViewById(R.id.pratoDiaLayout);
                String carne[] = myJson.getStringArray("C@m. Come", "carne");
                String peixe[] = myJson.getStringArray("C@m. Come", "peixe");
                String vegetariano[] = myJson.getStringArray("C@m. Come", "vegetariano");
                String outra[] = combine(carne, peixe);
                String[] pratoDia = combine(outra, vegetariano);
                alignPrices.getInstance().alignOne(pratoDia, this, aa, pratoDiaLayout);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                LinearLayout sobremesaLayout = (LinearLayout) findViewById(R.id.menuLayout);
                String menuPrato[] = myJson.getStringArray("C@m. Come", "menu");
                alignPrices.getInstance().alignOne(menuPrato, this, aa, sobremesaLayout);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                LinearLayout sobremesaLayout = (LinearLayout) findViewById(R.id.sobremesaLayout);
                String sobremesa[] = myJson.getStringArray("C@m. Come", "sobremesa");
                alignPrices.getInstance().alignOne(sobremesa, this, aa, sobremesaLayout);

            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    File createFile(String name){
        File file = new File(this.getFilesDir(),name);
        return file;
    }

    public JSONObject readJsonFromFile(String name){
        String JsonData ="";
        JSONObject myJson;
        try {
            FileInputStream fis = this.openFileInput(name);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            JsonData = new String(buffer);
            Log.d("Hallo",JsonData);
            myJson = new JSONObject(JsonData);
            int a = 1 + 2;
            return myJson;
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d("MEH","Something went wrong");
            return null;
        }
    }

    public String final_String (int length, String pratos[]) {
        String prato_final="";
        for (int i=0; i<pratos.length; i++) {
            prato_final = prato_final+pratos[i];
            if(i<length-1) {
                prato_final = prato_final+"\n";
            }
        }
        return prato_final;

    }

    public String final_String2 (String pratos[]) {
        String prato_final="";
        for (int i=0; i<pratos.length; i=i+2) {
            prato_final = prato_final+pratos[i];
            if(i<pratos.length-2) {
                prato_final = prato_final+"\n";
            }
        }
        return prato_final;

    }

    public String final_String3 (String pratos[]) {
        String preço_final="";
        for (int i=0; i<pratos.length; i=i+2) {
            preço_final = preço_final+pratos[i+1];
            if(i<pratos.length-2) {
                preço_final = preço_final+"\n";
            }
        }
        return preço_final;

    }

    public static String[] combine(String[] a, String[] b){
        if(a.length==1) {
            return b;
        }
        if(b.length==1) {
            return a;
        }
        int length = a.length + b.length;
        String [] result = new String[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
