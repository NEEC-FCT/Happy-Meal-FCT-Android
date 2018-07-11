package miguelcalado.restauracaomenus.CantinaFile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

import me.relex.circleindicator.CircleIndicator;
import miguelcalado.restauracaomenus.CustomPagerAdapter2;
import miguelcalado.restauracaomenus.DataHolder;
import miguelcalado.restauracaomenus.JsonDic;
import miguelcalado.restauracaomenus.MapsActivity;
import miguelcalado.restauracaomenus.R;
import miguelcalado.restauracaomenus.checkAtualizadoPorRestClass;

//import com.example.android.RestauracaoFCT.R;

public class Cantina extends AppCompatActivity {

    private boolean counter = false;
    String[] mResources= {
            "https://i.imgur.com/evGslcr.png", "https://i.imgur.com/hPJgQ49.png", "https://i.imgur.com/LVh9xCR.png", "https://i.imgur.com/oNJlBmL.png"
    };
    String filename;
    String restaurante = "Cantina";
    checkAtualizadoPorRestClass checkAtualiz;
    /*
    int [] mResources= {
            R.drawable.cantina5,
             R.drawable.cantina6,
            R.drawable.cantina7,
            R.drawable.cantina8,
    };
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cantina);

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
                    Intent start = new Intent(Cantina.this, MapsActivity.class);
                    start.putExtra("placeToSee","Cantina");
                    startActivity(start);
                }
            });

            TextView restaurantName= (TextView) findViewById(R.id.rest_name);
            restaurantName.setText("Cantina");

            TextView localizacao = (TextView) findViewById(R.id.location_textView);
            localizacao.setText("Edifício 7 - Tenda ao pé dos microondas");

            TextView horario = (TextView) findViewById(R.id.horarioCTextview);
            horario.setText("Aberto Seg|Sex");
            //Ta aberto desde as 9->18
            TextView almocoTime = (TextView) findViewById(R.id.almoçoTextView);
            almocoTime.setText("Almoço 11:30 às 14:30");

            TextView jantarTime = (TextView) findViewById(R.id.jantarTextView);
            jantarTime.setText("Jantar 18:30 às 20:30");

//            TextView pratoPrincipal = (TextView) findViewById(R.id.pratoTextView);
//            pratoPrincipal.setText("2.65€");

            TextView ppPrice = (TextView) findViewById(R.id.vegetariano);
            ppPrice.setText("Não é preciso senha!!");
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

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            try {

                JsonDic myJson = new JsonDic(filename, Cantina.this);

                try {
                    String sopa[] = myJson.getStringArray("Cantina", "sopa");
                    String sopa_final = final_String2(sopa);
                    TextView sopas = (TextView) findViewById(R.id.sopaText);
                    sopas.setText(sopa_final);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    String carne[] = myJson.getStringArray("Cantina", "carne");
                    String carne_final = final_String2(carne);
                    final TextView carnes = (TextView) findViewById(R.id.carneText);
                    carnes.setText(carne_final);
                    carnes.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    String peixe[] = myJson.getStringArray("Cantina", "peixe");
                    String peixe_final = final_String2(peixe);
                    TextView peixes = (TextView) findViewById(R.id.peixeText);
                    peixes.setText(peixe_final);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    String sobremesa[] = myJson.getStringArray("Cantina", "sobremesa");

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

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
}