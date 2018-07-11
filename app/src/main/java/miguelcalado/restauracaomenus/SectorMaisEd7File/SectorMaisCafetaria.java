package miguelcalado.restauracaomenus.SectorMaisEd7File;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;
import miguelcalado.restauracaomenus.CafetariaSet;
import miguelcalado.restauracaomenus.CustomPagerAdapter2;
import miguelcalado.restauracaomenus.DataHolder;
import miguelcalado.restauracaomenus.JsonDic;
import miguelcalado.restauracaomenus.MapsActivity;
import miguelcalado.restauracaomenus.R;
import miguelcalado.restauracaomenus.checkAtualizadoPorRestClass;

/**
 * Created by Miguel-PC on 09/09/2017.
 */

public class SectorMaisCafetaria extends AppCompatActivity {

    checkAtualizadoPorRestClass checkAtualiz;
    String filename;
    String restaurante = "Sector + Ed.7";

    /*
    int [] mResources = { R.drawable.campusfinaliss,
            R.drawable.barcampus5,
            R.drawable.barcampus6,
            R.drawable.barcampus7,
    };*/
    String [] mResources = {"https://i.imgur.com/b3ViTGf.png"
    };

    private static final int MAX_TO_SEE = 3;


    private boolean counter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sector7_cafe);

        filename = DataHolder.getInstance().getDataFilename();

        //restaurantPic.setImageResource(R.drawable.sectormaised7);

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
                Intent start = new Intent(SectorMaisCafetaria.this, MapsActivity.class);
                start.putExtra("placeToSee","Sector + Edíficio 7");
                startActivity(start);
            }
        });

        TextView restaurantName= (TextView) findViewById(R.id.rest_name);
        restaurantName.setText("Sector + Edifício 7");

        TextView localizacao = (TextView) findViewById(R.id.location_textView);
        localizacao.setText("Edifício 7");

        TextView horario = (TextView) findViewById(R.id.horarioCTextview);
        horario.setText("Seg|Sex 9 às 18");
        //Ta aberto desde as 9->18
        TextView almocoTime = (TextView) findViewById(R.id.almoçoTextView);
        almocoTime.setText("Almoço 12 às 15");

        TextView pratoPrincipal = (TextView) findViewById(R.id.jantarTextView);
        pratoPrincipal.setText("Preço Médio 4.05€");

        TextView ppPrice = (TextView) findViewById(R.id.vegetariano);
        ppPrice.setVisibility(View.GONE);

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
        try {
            JsonDic myJson = new JsonDic(filename, SectorMaisCafetaria.this);

            TextView menu = (TextView) findViewById(R.id.menu_textView);
            menu.setText("Ementa");

            ArrayList<CafetariaSet> cafetariaSets = new ArrayList<>();

            TextView pastelariaTitle = (TextView) findViewById(R.id.pastelariaTitle);
            TextView pastelariaText = (TextView) findViewById(R.id.pastelariaText);
            TextView pastelariaPrice = (TextView) findViewById(R.id.pastelariaPrice);
            ImageView pastelariaAll = (ImageView) findViewById(R.id.pastelariaAll);
            cafetariaSets.add(new CafetariaSet("Sector + Ed.7", myJson, pastelariaTitle,pastelariaAll, pastelariaText, pastelariaPrice, "pastelaria", MAX_TO_SEE));

            TextView BebidasTitle = (TextView) findViewById(R.id.bebidaTitle);
            TextView BebidasText = (TextView) findViewById(R.id.bebidaText);
            TextView BebidasPrice = (TextView) findViewById(R.id.bebidaPrice);
            ImageView BebidasAll = (ImageView) findViewById(R.id.bebidaAll);
            cafetariaSets.add(new CafetariaSet("Sector + Ed.7", myJson, BebidasTitle ,BebidasAll, BebidasText, BebidasPrice, "bebida", MAX_TO_SEE));

            TextView padariaTitle = (TextView) findViewById(R.id.padariaTitle);
            TextView padariaText = (TextView) findViewById(R.id.padariaText);
            TextView padariaPrice = (TextView) findViewById(R.id.padariaPrice);
            ImageView padariaAll = (ImageView) findViewById(R.id.padariaAll);
            cafetariaSets.add(new CafetariaSet("Sector + Ed.7", myJson,padariaTitle , padariaAll, padariaText, padariaPrice, "padaria", MAX_TO_SEE));

            TextView cafeTitle = (TextView) findViewById(R.id.cafeTitle);
            TextView cafeText = (TextView) findViewById(R.id.cafeText);
            TextView cafePrice = (TextView) findViewById(R.id.cafePrice);
            ImageView cafeAll = (ImageView) findViewById(R.id.cafeAll);
            cafetariaSets.add(new CafetariaSet("Sector + Ed.7", myJson,cafeTitle , cafeAll, cafeText, cafePrice, "cafetaria", MAX_TO_SEE));

            setOnClickImage(cafetariaSets);

            /*TextView Text=(TextView)findViewById(R.id.Text);
            TextView Price=(TextView)findViewById(R.id.Price);
            ImageView All=(ImageView) findViewById(R.id.All);
            cafetariaSets.add(new CafetariaSet("",myJson, All, Text, Price, "", MAX_TO_SEE));*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOnClickImage(ArrayList<CafetariaSet> cafetariaSets) {
        for (int i = 0; i < cafetariaSets.size(); i++) {
            final CafetariaSet cafetariaSet = cafetariaSets.get(i);
            cafetariaSet.getPlusIcon().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cafetariaSet.showTextOnClick();
                }
            });
        }
    }


    File createFile(String name) {
        File file = new File(this.getFilesDir(), name);
        return file;
    }

    public JSONObject readJsonFromFile(String name) {
        String JsonData = "";
        JSONObject myJson;
        try {
            FileInputStream fis = this.openFileInput(name);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            JsonData = new String(buffer);
            Log.d("Hallo", JsonData);
            myJson = new JSONObject(JsonData);
            int a = 1 + 2;
            return myJson;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MEH", "Something went wrong");
            return null;
        }
    }
}
