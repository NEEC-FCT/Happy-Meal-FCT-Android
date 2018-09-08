package miguelcalado.restauracaomenus;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.javiersantos.appupdater.AppUpdater;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static miguelcalado.restauracaomenus.MainActivity.optionCafetaria;
import static miguelcalado.restauracaomenus.MainActivity.optionRefeicao;

/**
 * Created by Miguel-PC on 11/01/2018.
 */

public class FirstScreen extends AppCompatActivity {
    Animation aniFade;
    TextView refeicaoBtn, cafetariaBtn;
    TextView wifi;
    TextView date, happymeal;
    TextView aboutUs;
    VideoView logo;
    FrameLayout placeholder;

    String filename;
    String serverFilename;
    String bucket;

    Date now;
    Boolean isDownloading = false;
    private Context puta = FirstScreen.this;

    public static String valueOf(Object obj) {
        return (obj == null) ? "null" : obj.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstscreen);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.start();

        filename = DataHolder.getInstance().getDataFilename();
        serverFilename = DataHolder.getInstance().getDataServerFilename();
        bucket = DataHolder.getInstance().getDataBucket();
        //gravar no inicio da app qual o nome do ficheiro interno para ter o json
        //para nas outras classes aceder diretamente em vez de criar strings com o mesmo nome
        //que dá trabalho para se quisermos mudar o nome ou para debugging
        DataHolder.getInstance().setDatafilename(filename);

        wifi = (TextView) findViewById(R.id.WIFI);
        aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        refeicaoBtn = (TextView) findViewById(R.id.refeicao);
        refeicaoBtn.setVisibility(View.INVISIBLE);
        cafetariaBtn = (TextView) findViewById(R.id.cafetaria);
        cafetariaBtn.setVisibility(View.INVISIBLE);
        aboutUs = (TextView) findViewById(R.id.aboutus);
        aboutUs.setVisibility(View.INVISIBLE);
        happymeal = (TextView) findViewById(R.id.happymealfct);
        happymeal.setVisibility(View.INVISIBLE);
        date = (TextView) findViewById(R.id.date);
        date.setVisibility(View.INVISIBLE);
        setDate(date);

        playVideo();

        refeicaoBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                logo.stopPlayback();
                placeholder.setVisibility(View.VISIBLE);
                Intent start = new Intent(FirstScreen.this, MainActivity.class);
                start.putExtra("option", optionRefeicao);
                //start.putExtra("")
                startActivity(start);
            }
        });
        cafetariaBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                logo.stopPlayback();
                placeholder.setVisibility(View.VISIBLE);
                Intent start = new Intent(FirstScreen.this, MainActivity.class);
                start.putExtra("option", optionCafetaria);
                startActivity(start);
            }
        });
        aboutUs.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                logo.stopPlayback();
                placeholder.setVisibility(View.VISIBLE);
                Intent start = new Intent(FirstScreen.this, AboutUs.class);
                startActivity(start);
            }
        });


        if (!isConnectedMobileData() && !isConnectedWIFI()) {
            turnOnNet();
        } else {
            wifi.setVisibility(View.INVISIBLE);
            fadeInButton();
            readJson();
        }
    }

    private void readJson() {
        try {
            fileStarter();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void playVideo() {
        placeholder = (FrameLayout) findViewById(R.id.placeholder);
        logo = (VideoView) findViewById(R.id.Logo1);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.logo;
        Uri uri = Uri.parse(videoPath);
        logo.setVideoURI(uri);
        logo.requestFocus();
        logo.start();

        logo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            // video started; hide the placeholder.
                            placeholder.setVisibility(View.GONE);
                            mp.setLooping(true);
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
    }

    private void turnOnNet() {
        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifi.setText("Conectando");
                wifi.setBackgroundColor(getResources().getColor(R.color.ButtonDayTransperanceColor));
                wifi.setEnabled(false);
                if (!buttonWIFIOn())
                    enableButtonWiFI();

                Thread connectWIFI = new Thread() {
                    @Override
                    public void run() {
                        int wait = 0;
                        boolean connect = false;
                        while (!connect) {
                            wait++;
                            try {
                                sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (isConnectedMobileData() || isConnectedWIFI()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        readJson();
                                        fadeInButton();
                                    }
                                });//ja esta corrigido
                                connect = true;
                            } else if (wait == 10) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        wifi.setText("Verifica a tua conexão à internet");
                                    }
                                });
                            }
                        }
                      /*  runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });*/

                    }
                };
                connectWIFI.start();
            }
        });
    }

    void fadeInButton() {
        wifi.setVisibility(View.INVISIBLE);
        cafetariaBtn.setVisibility(View.VISIBLE);
        cafetariaBtn.startAnimation(aniFade);
        refeicaoBtn.setVisibility(View.VISIBLE);
        refeicaoBtn.startAnimation(aniFade);
        aboutUs.setVisibility(View.VISIBLE);
        aboutUs.startAnimation(aniFade);
        date.setVisibility(View.VISIBLE);
        date.startAnimation(aniFade);
        happymeal.setVisibility(View.VISIBLE);
        happymeal.startAnimation(aniFade);
    }

    private void enableButtonWiFI() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    private boolean buttonWIFIOn() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Log.i("NET", "WIFI STATE: " + new Integer(wifiManager.getWifiState()).toString());
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
            return true;
        return false;
    }

    private boolean isConnectedWIFI() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            return true;
        }
        return false;
    }

    private boolean isConnectedMobileData() {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        Log.i("NET", "Mobile Network" + new Boolean(mobileDataEnabled).toString());
        return mobileDataEnabled;
    }

    private void setDate(TextView dateTxt) {
        Date date = new Date();   // given date
        Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        int minutes = calendar.get(Calendar.MINUTE);
        Integer day = new Integer(calendar.get(Calendar.DAY_OF_MONTH));
        Integer moth = new Integer(calendar.get(Calendar.MONTH) + 1);
        Integer year = new Integer(calendar.get(Calendar.YEAR));
        dateTxt.setText(day.toString() + "/" + moth.toString() + "/" + year.toString());
    }

    public void downloadThatShit() {
        // callback method to call credentialsProvider method.

        System.out.println("Entrou");
        try {

            String json = getInternetData();
            FileOutputStream fOut = new FileOutputStream(createFile());
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(json);

            myOutWriter.close();

            fOut.flush();
            fOut.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getInternetData() throws Exception {


        BufferedReader in = null;
        String data = null;

        try {
            HttpClient client = new DefaultHttpClient();
            client.getConnectionManager().getSchemeRegistry();

            URI website = new URI("http://fcthub.neec-fct.com/JSON/maLucasNotification.json");
            HttpGet request = new HttpGet();
            request.setURI(website);
            HttpResponse response = client.execute(request);
            response.getStatusLine().getStatusCode();

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String l = "";
            String nl = System.getProperty("line.separator");
            while ((l = in.readLine()) != null) {
                sb.append(l + nl);
            }
            in.close();
            data = sb.toString();
            return data;
        } finally {
            if (in != null) {
                try {
                    in.close();
                    return data;
                } catch (Exception e) {
                    Log.e("GetMethodEx", e.getMessage());
                }
            }
        }
    }

    public File createFile() {
        File fileToDownload = new File(this.getFilesDir(), filename);
        return fileToDownload;
    }

    public void fileStarter() throws JSONException, ParseException, FileNotFoundException {

        fileExists();
        downloadThatShit();
    }

    public boolean fileExists() {
        //File file = getFileStreamPath(filename);
        if (createFile().exists() && createFile().length() > 10) {
            Log.d("state", valueOf(createFile().exists()));
            return true;
        }
        Log.d("state", "false");
        Log.d("state", valueOf(createFile().exists()));
        return false;
    }

    @Override
    protected void onRestart() {
        playVideo();
        super.onRestart();

    }
}
