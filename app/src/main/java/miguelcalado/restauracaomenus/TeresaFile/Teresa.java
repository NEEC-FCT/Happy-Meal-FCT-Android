package miguelcalado.restauracaomenus.TeresaFile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import miguelcalado.restauracaomenus.alignPrices;
import miguelcalado.restauracaomenus.checkAtualizadoPorRestClass;


/**
 * Created by Miguel-PC on 26/08/2017.
 */

public class Teresa extends AppCompatActivity {

    checkAtualizadoPorRestClass checkAtualiz;

    String filename;
    String restaurante = "Teresa";

    private boolean counter = false;

    String [] mResources = {"https://i.imgur.com/u02FQpq.png", "https://i.imgur.com/cXMiGkY.png", "https://i.imgur.com/SJkSz9K.png",
            "https://i.imgur.com/t7fuyxf.png"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teresa);

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
                Intent start = new Intent(Teresa.this, MapsActivity.class);
                start.putExtra("placeToSee","Teresa");
                startActivity(start);
            }
        });


        TextView restaurantName= (TextView) findViewById(R.id.rest_name);
        restaurantName.setText("Teresa");

        TextView localizacao = (TextView) findViewById(R.id.location_textView);
        localizacao.setText("Hangar I");

        TextView horario = (TextView) findViewById(R.id.horarioCTextview);
        horario.setText("Seg|Sex 8:30 às 21");
        //Ta aberto desde as 9->18
        TextView almocoTime = (TextView) findViewById(R.id.almoçoTextView);
        almocoTime.setText("Almoço 11:30 às 14:30");

        TextView jantarTime = (TextView) findViewById(R.id.jantarTextView);
        jantarTime.setText("Jantar 18:30 às 20:30");

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
            JsonDic myJson = new JsonDic(filename,Teresa.this);

                try {
                    LinearLayout peixeLayout = (LinearLayout) findViewById(R.id.sopaLayout) ;
                    String sopa[] = myJson.getStringArray("Teresa","sopa");
                    alignPrices.getInstance().alignOne(sopa, this, aa, peixeLayout);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    LinearLayout carneLayout = (LinearLayout) findViewById(R.id.carneLayout);
                    String carne[] = myJson.getStringArray("Teresa", "carne");
                    alignPrices.getInstance().alignOne(carne, this, aa, carneLayout);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    LinearLayout peixeLayout = (LinearLayout) findViewById(R.id.peixeLayout);
                    String peixe[] = myJson.getStringArray("Teresa", "peixe");
                    alignPrices.getInstance().alignOne(peixe, this, aa, peixeLayout);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    LinearLayout vegetarianoLayout = (LinearLayout) findViewById(R.id.vegetarianoLayout);
                    String vegetariano[] = myJson.getStringArray("Teresa", "vegetariano");
                    alignPrices.getInstance().alignOne(vegetariano, this, aa, vegetarianoLayout);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    LinearLayout sobremesaLayout = (LinearLayout) findViewById(R.id.sobremesaLayout);
                    String sobremesa[] = myJson.getStringArray("Teresa", "sobremesa");
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
        String a="9999";
        for (int i=0; i<pratos.length; i=i+2) {
            if(a.equals(pratos[i+1])) {
                pratos[i + 1] = "\n";
            }
            preço_final = preço_final+pratos[i+1];
            if(i<pratos.length-2) {
                preço_final = preço_final+"\n";
            }
        }
        return preço_final;

    }
}