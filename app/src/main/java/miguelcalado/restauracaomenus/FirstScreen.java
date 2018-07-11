package miguelcalado.restauracaomenus;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.github.javiersantos.appupdater.AppUpdater;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Arrays;

import miguelcalado.restauracaomenus.BarCampusFile.BarCampusFile.BarCampusCafetaria;
import miguelcalado.restauracaomenus.CasaPessoalFile.CasaPessoalCafetaria;
import miguelcalado.restauracaomenus.GirassolFile.GirassolCafetaria;
import miguelcalado.restauracaomenus.TeresaFile.Teresa;

import static android.content.Context.WIFI_SERVICE;
import static java.lang.Thread.sleep;
import static miguelcalado.restauracaomenus.MainActivity.optionCafetaria;
import static miguelcalado.restauracaomenus.MainActivity.optionRefeicao;
import static miguelcalado.restauracaomenus.Notification.makeNotification;

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
    AmazonS3 s3;
    TransferUtility transferUtility;
    Date now;
    Boolean isDownloading = false;
    private Context puta = FirstScreen.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstscreen);

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

    public void downloadThatShit() throws FileNotFoundException {
        // callback method to call credentialsProvider method.
        credentialsProvider();

        // callback method to call the setTransferUtility method
        setTransferUtility();

        setFileToDownload();
    }

    public File createFile() {
        File fileToDownload = new File(this.getFilesDir(), filename);
        return fileToDownload;
    }

    public void fileStarter() throws JSONException, ParseException, FileNotFoundException {

            fileExists();
            downloadThatShit();
    }

    /*public void menuAtualizado(String weekId, String timeStamp) {
        if (swipe_active) {
            //mySwipeRefreshLayout.setRefreshing(false);
            swipe_active = false;
        }
        if (!weekId.equals(timeStamp)) { //estamos no dia de hoje mas ainda n são 9h

            Toast.makeText(FirstScreen.this, "Menu de hoje disponível a partir das 9h",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(FirstScreen.this, "Menu de hoje atualizado",
                    Toast.LENGTH_SHORT).show();
        }
    }*/

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

    public static String valueOf(Object obj) {
        return (obj == null) ? "null" : obj.toString();
    }

    public void credentialsProvider() {

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "eu-west-1:2fe2b2a9-0d04-4d16-8fb4-51a61d9e92fa", // Identity pool ID
                Regions.EU_WEST_1 // Region
        );

        setAmazonS3Client(credentialsProvider);
    }
    // Create an S3 client

    /**
     * Create a AmazonS3Client constructor and pass the credentialsProvider.
     *
     * @param credentialsProvider
     */
    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider) {
        // Create an S3 client
        s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.EU_WEST_1));
    }

    public void setTransferUtility() {
        transferUtility = new TransferUtility(s3, getApplicationContext());
    }

    /**
     * This method is used to Download the file to S3 by using transferUtility class
     *
     * @param //view
     **/
    public void setFileToDownload() throws FileNotFoundException {
        File fileToDownload = createFile();

        TransferObserver transferObserver = transferUtility.download(
                bucket,     /* The bucket to download from */
                serverFilename,    /* The key for the object to download */
                fileToDownload        /* The file to download the object to */
        );


        //função para ler o estado da transferencia
        transferObserverListener(transferObserver);

    }

    /**
     * This is listener method of the TransferObserver
     * Within this listener method, we got status of uploading and downloading file,
     * to diaplay percentage of the part of file to be uploaded or downloaded to S3
     * It display error, when there is problem to upload and download file to S3.
     *
     * @param transferObserver
     */


    boolean downloadComplete;
    public void transferObserverListener(TransferObserver transferObserver) {
        now = new Date();
        downloadComplete=false;

        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.e("statechange", state + "");
                //DisconnectMaybe(id);
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent / bytesTotal * 100);
                    Log.e("percentage", percentage + "");
                    if (percentage == 100 && !downloadComplete) {
                        Log.d("NOW", "DONE");
                        downloadComplete = true;
                        JsonDic myDic = new JsonDic(filename, puta);
                        String[] notifications = myDic.getNotifications();
                        if (notifications!=null) {
                            makeNotification(notifications, FirstScreen.this);
                        }
                    }

            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("error", "error");
                Log.d("state", "UAUAUAUAUAUAUAUAUAUAUAUAUAUAUAUAUAUAUAUAUAUA");
            }
        });
    }
/*
    public void DisconnectMaybe(int id){
        Log.d("state","CRYCRYCRYCRYCRYCRYCRYCRYCRYCRYCRYCRYCRYCRYCRYCRYCRYCRYCRYCRY");
        Date then = new Date();
        long a =  (long) then.getTime() - now.getTime();
        Log.d("state",String.valueOf(a));
        if (a > 4000 ){
            Log.d("state","MAMAMAMAMAMAMAMAMAMAMAMAMAMAMAMAMAMAMAMAMAMAMAMA");
            if (swipe_active) {
                mySwipeRefreshLayout.setRefreshing(false);
                swipe_active = false;
            }
            Toast.makeText(FirstScreen.this, "Verifica a tua conexão à internet", Toast.LENGTH_SHORT).show();
            transferUtility.cancel(id);
        }
    }
    */
    /*
    public void downloadCompletedSwipeRefresh() {
        if (swipe_active) {
            //mySwipeRefreshLayout.setRefreshing(false);
            swipe_active = false;
        }
        try {
            showMessageRefresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    /*
    public void showMessageRefresh() throws JSONException {
        JsonDic Dic = new JsonDic(filename, this);
        String date = Dic.getWeekId();
        Toast.makeText(FirstScreen.this, "Atualizado com o dia: " + date,
                Toast.LENGTH_SHORT).show();
    }*/

    @Override
    protected void onRestart() {
        playVideo();
        super.onRestart();

    }
}
