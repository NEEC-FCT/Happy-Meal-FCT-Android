package miguelcalado.restauracaomenus.TeresaFile;

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

public class TeresaCafetaria extends AppCompatActivity {

    checkAtualizadoPorRestClass checkAtualiz;
    String filename;
    String restaurante = "Teresa";

    /*
    int [] mResources = { R.drawable.campusfinaliss,
            R.drawable.barcampus5,
            R.drawable.barcampus6,
            R.drawable.barcampus7,
    };*/
    String [] mResources = {"https://i.imgur.com/u02FQpq.png", "https://i.imgur.com/cXMiGkY.png", "https://i.imgur.com/SJkSz9K.png",
            "https://i.imgur.com/t7fuyxf.png"
    };

    private static final int MAX_TO_SEE = 3;


    private boolean counter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teresa_cafe);

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
                Intent start = new Intent(TeresaCafetaria.this, MapsActivity.class);
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
            JsonDic myJson = new JsonDic(filename, TeresaCafetaria.this);

            TextView menu = (TextView) findViewById(R.id.menu_textView);
            menu.setText("Ementa");

            ArrayList<CafetariaSet> cafetariaSets = new ArrayList<>();

            TextView bebidaTitle = (TextView) findViewById(R.id.bebidaTitle);
            TextView bebidaText = (TextView) findViewById(R.id.bebidaText);
            TextView bebidaPrice = (TextView) findViewById(R.id.bebidaPrice);
            ImageView bebidaAll = (ImageView) findViewById(R.id.bebidaAll);
            cafetariaSets.add(new CafetariaSet("Teresa", myJson, bebidaTitle, bebidaAll, bebidaText, bebidaPrice, "bebida", MAX_TO_SEE));

            TextView cafetariaTitle = (TextView) findViewById(R.id.cafetariaTitle);
            TextView cafetariaText = (TextView) findViewById(R.id.cafetariaText);
            TextView cafetariaPrice = (TextView) findViewById(R.id.cafetariaPrice);
            ImageView cafetariaAll = (ImageView) findViewById(R.id.cafetariaAll);
            cafetariaSets.add(new CafetariaSet("Teresa", myJson, cafetariaTitle, cafetariaAll, cafetariaText, cafetariaPrice, "cafetaria", MAX_TO_SEE));

            TextView salgadoTitle = (TextView) findViewById(R.id.salgadoTitle);
            TextView salgadoText = (TextView) findViewById(R.id.salgadoText);
            TextView salgadoPrice = (TextView) findViewById(R.id.salgadoPrice);
            ImageView salgadoAll = (ImageView) findViewById(R.id.salgadoAll);
            cafetariaSets.add(new CafetariaSet("Teresa", myJson, salgadoTitle, salgadoAll, salgadoText, salgadoPrice, "salgados", MAX_TO_SEE));

            TextView boloTitle = (TextView) findViewById(R.id.boloTitle);
            TextView boloText = (TextView) findViewById(R.id.boloText);
            TextView boloPrice = (TextView) findViewById(R.id.boloPrice);
            ImageView boloAll = (ImageView) findViewById(R.id.boloAll);
            cafetariaSets.add(new CafetariaSet("Teresa", myJson, boloTitle, boloAll, boloText, boloPrice, "bolo", MAX_TO_SEE));

            TextView outrosTitle = (TextView) findViewById(R.id.outrosTitle);
            TextView outrosText = (TextView) findViewById(R.id.outrosText);
            TextView outrosPrice = (TextView) findViewById(R.id.outrosPrice);
            ImageView outrosAll = (ImageView) findViewById(R.id.outrosAll);
            cafetariaSets.add(new CafetariaSet("Teresa", myJson, outrosTitle, outrosAll, outrosText, outrosPrice, "outro", MAX_TO_SEE));

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
