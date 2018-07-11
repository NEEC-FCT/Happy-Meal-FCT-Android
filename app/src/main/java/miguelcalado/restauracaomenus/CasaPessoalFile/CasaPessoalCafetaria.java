package miguelcalado.restauracaomenus.CasaPessoalFile;

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

public class CasaPessoalCafetaria extends AppCompatActivity {

    checkAtualizadoPorRestClass checkAtualiz;
    String filename;
    String restaurante = "Casa do P.";

    /*
    int [] mResources = { R.drawable.campusfinaliss,
            R.drawable.barcampus5,
            R.drawable.barcampus6,
            R.drawable.barcampus7,
    };*/
    String[] mResources = {"https://i.imgur.com/p2p9m7H.png", "https://i.imgur.com/VPjhQz5.png", "https://i.imgur.com/AvfcjHX.png",
            "https://i.imgur.com/IXuQYyg.png"
    };

    private static final int MAX_TO_SEE = 3;


    private boolean counter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casapessoal_cafe);

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
                Intent start = new Intent(CasaPessoalCafetaria.this, MapsActivity.class);
                start.putExtra("placeToSee","Casa do Pessoal");
                startActivity(start);
            }
        });

        TextView restaurantName= (TextView) findViewById(R.id.rest_name);
        restaurantName.setText("Casa do Pessoal");

        TextView localizacao = (TextView) findViewById(R.id.location_textView);
        localizacao.setText("Edifício I - Junto ao Mini Nova");

        TextView horario = (TextView) findViewById(R.id.horarioCTextview);
        horario.setText("Seg|Sex 8:30 às 18");
        //Ta aberto desde as 9->18
        TextView almocoTime = (TextView) findViewById(R.id.almoçoTextView);
        almocoTime.setText("Almoço 12 às 15");

        TextView pratoPrincipal = (TextView) findViewById(R.id.jantarTextView);
        pratoPrincipal.setText("Prato 3.50€");

        TextView ppPrice = (TextView) findViewById(R.id.vegetariano);
        ppPrice.setText("Mini Prato 3€");

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
            JsonDic myJson = new JsonDic(filename, CasaPessoalCafetaria.this);

            TextView menu = (TextView) findViewById(R.id.menu_textView);
            menu.setText("Ementa");

            ArrayList<CafetariaSet> cafetariaSets = new ArrayList<>();

            TextView cafeTitle = (TextView) findViewById(R.id.cafeTitle);
            TextView cafeText = (TextView) findViewById(R.id.cafeText);
            TextView cafePrice = (TextView) findViewById(R.id.cafePrice);
            ImageView cafeAll = (ImageView) findViewById(R.id.cafeAll);
            cafetariaSets.add(new CafetariaSet("Casa do P.", myJson, cafeTitle, cafeAll, cafeText, cafePrice, "cafetaria", MAX_TO_SEE));

            TextView pastelariaTitle = (TextView) findViewById(R.id.pastelariaTitle);
            TextView pastelariaText = (TextView) findViewById(R.id.pastelariaText);
            TextView pastelariaPrice = (TextView) findViewById(R.id.pastelariaPrice);
            ImageView pastelariaAll = (ImageView) findViewById(R.id.pastelariaAll);
            cafetariaSets.add(new CafetariaSet("Casa do P.", myJson, pastelariaTitle, pastelariaAll, pastelariaText, pastelariaPrice, "pastelaria", MAX_TO_SEE));

            TextView salgadoTitle = (TextView) findViewById(R.id.salgadoTitle);
            TextView salgadoText = (TextView) findViewById(R.id.salgadoText);
            TextView salgadoPrice = (TextView) findViewById(R.id.salgadoPrice);
            ImageView salgadoAll = (ImageView) findViewById(R.id.salgadoAll);
            cafetariaSets.add(new CafetariaSet("Casa do P.", myJson, salgadoTitle, salgadoAll, salgadoText, salgadoPrice, "salgados", MAX_TO_SEE));

            TextView doceTitle = (TextView) findViewById(R.id.doceTitle);
            TextView doceText = (TextView) findViewById(R.id.doceText);
            TextView docePrice = (TextView) findViewById(R.id.docePrice);
            ImageView doceAll = (ImageView) findViewById(R.id.doceAll);
            cafetariaSets.add(new CafetariaSet("Casa do P.", myJson, doceTitle, doceAll, doceText, docePrice, "doce", MAX_TO_SEE));

            TextView outrosTitle = (TextView) findViewById(R.id.outrosTitle);
            TextView outrosText = (TextView) findViewById(R.id.outrosText);
            TextView outrosPrice = (TextView) findViewById(R.id.outrosPrice);
            ImageView outrosAll = (ImageView) findViewById(R.id.outrosAll);
            cafetariaSets.add(new CafetariaSet("Casa do P.", myJson, outrosTitle, outrosAll, outrosText, outrosPrice, "outro", MAX_TO_SEE));

            TextView bebidaTitle = (TextView) findViewById(R.id.bebidaTitle);
            TextView bebidaText = (TextView) findViewById(R.id.bebidaText);
            TextView bebidaPrice = (TextView) findViewById(R.id.bebidaPrice);
            ImageView bebidaAll = (ImageView) findViewById(R.id.bebidaAll);
            cafetariaSets.add(new CafetariaSet("Casa do P.", myJson, bebidaTitle, bebidaAll, bebidaText, bebidaPrice, "bebida", MAX_TO_SEE));

            TextView iogurteTitle = (TextView) findViewById(R.id.iogurteTitle);
            TextView iogurteText = (TextView) findViewById(R.id.iogurteText);
            TextView iogurtePrice = (TextView) findViewById(R.id.iogurtePrice);
            ImageView iogurteAll = (ImageView) findViewById(R.id.iogurteAll);
            cafetariaSets.add(new CafetariaSet("Casa do P.", myJson, iogurteTitle, iogurteAll, iogurteText, iogurtePrice, "iogurte", MAX_TO_SEE));

            TextView chocolateTitle = (TextView) findViewById(R.id.chocolateTitle);
            TextView chocolateText = (TextView) findViewById(R.id.chocolateText);
            TextView chocolatePrice = (TextView) findViewById(R.id.chocolatePrice);
            ImageView chocolateAll = (ImageView) findViewById(R.id.chocolateAll);
            cafetariaSets.add(new CafetariaSet("Casa do P.", myJson, chocolateTitle, chocolateAll, chocolateText, chocolatePrice, "chocolate", MAX_TO_SEE));

            TextView padariaTitle = (TextView) findViewById(R.id.padariaTitle);
            TextView padariaText = (TextView) findViewById(R.id.padariaText);
            TextView padariaPrice = (TextView) findViewById(R.id.padariaPrice);
            ImageView padariaAll = (ImageView) findViewById(R.id.padariaAll);
            cafetariaSets.add(new CafetariaSet("Casa do P.", myJson, padariaTitle, padariaAll, padariaText, padariaPrice, "padaria", MAX_TO_SEE));

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
