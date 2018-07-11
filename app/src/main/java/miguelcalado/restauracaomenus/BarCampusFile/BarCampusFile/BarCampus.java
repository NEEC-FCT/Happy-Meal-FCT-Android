package miguelcalado.restauracaomenus.BarCampusFile.BarCampusFile;

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

/**
 * Created by Miguel-PC on 09/09/2017.
 */

public class BarCampus extends AppCompatActivity {

    checkAtualizadoPorRestClass checkAtualiz;
    String filename;
    String restaurante = "Bar Campus";

    /*
    int [] mResources = { R.drawable.campusfinaliss,
            R.drawable.barcampus5,
            R.drawable.barcampus6,
            R.drawable.barcampus7,
    };*/
    String[] mResources = {"https://i.imgur.com/ZYW3j9F.png", "https://i.imgur.com/7QQLbCq.png", "https://i.imgur.com/q9kAtjB.png",
            "https://i.imgur.com/xJhyPTw.png"
    };


    private boolean counter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcampus);

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
                Intent start = new Intent(BarCampus.this, MapsActivity.class);
                start.putExtra("placeToSee","Bar Campus");
                startActivity(start);
            }
        });

        TextView restaurantName= (TextView) findViewById(R.id.rest_name);
        restaurantName.setText("Bar Campus");

        TextView localizacao = (TextView) findViewById(R.id.location_textView);
        localizacao.setText("Edifício da Biblioteca - Piso de baixo");

        TextView horario = (TextView) findViewById(R.id.horarioCTextview);
        horario.setText("Seg|Sex 9:30 às 18");
        //Ta aberto desde as 9->18
        TextView almocoTime = (TextView) findViewById(R.id.almoçoTextView);
        almocoTime.setText("Almoço das 12 às 18");

        TextView pratoPrincipal = (TextView) findViewById(R.id.jantarTextView);
        pratoPrincipal.setText("Dá para repetir!");

        TextView ppPrice = (TextView) findViewById(R.id.vegetariano);
        ppPrice.setText("Vegetariano");
        ppPrice.setTextColor(getResources().getColor(R.color.white));

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
            JsonDic myJson = new JsonDic(filename, BarCampus.this);

            try {
                String sopa[] = myJson.getStringArray("Bar Campus","sopa");
                LinearLayout sopaLayout = (LinearLayout) findViewById(R.id.sopaLayout) ;
                if(sopa.length==0) {
                    TextView sopas = (TextView) findViewById(R.id.sopaTitle);
                    sopas.setVisibility(View.GONE);
                    sopaLayout.setVisibility(View.GONE);
                } else {
                    alignPrices.getInstance().alignOne(sopa, this, aa, sopaLayout);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                LinearLayout pratoDiaLayout = (LinearLayout) findViewById(R.id.pratoDiaLayout);
                String carne[] = myJson.getStringArray("Bar Campus", "carne");
                String peixe [] = myJson.getStringArray("Bar Campus", "peixe");
                String vegetariano [] = myJson.getStringArray("Bar Campus", "vegetariano");
                String outra [] = combine (carne, peixe);
                String [] pratoDia = combine(outra, vegetariano);
                alignPrices.getInstance().alignOne(pratoDia, this, aa, pratoDiaLayout);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            LinearLayout sobremesaLayout = (LinearLayout) findViewById(R.id.sobremesaLayout);
            String[] sobremesa = myJson.getStringArray("Bar Campus", "sobremesa");
            if(sobremesa.length==0) {
                TextView sobremesas = (TextView) findViewById(R.id.sobremesaTitle);
                sobremesas.setVisibility(View.GONE);
                sobremesaLayout.setVisibility(View.GONE);
            } else {
                alignPrices.getInstance().alignOne(sobremesa, this, aa, sobremesaLayout);
            }

            try {
                String bebidas[] = myJson.getStringArray("Bar Campus", "Bebidas");
                String bebidaFinal = final_String2( bebidas);
                String bebidaPrice = final_String3(bebidas);
                TextView bebidasText = (TextView) findViewById(R.id.BebidasText);
                bebidasText.setText(bebidaFinal);
                TextView bebidasPrice = (TextView) findViewById(R.id.BebidasPrice);
                bebidasPrice.setText(bebidaPrice);

            } catch (JSONException e) {
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
