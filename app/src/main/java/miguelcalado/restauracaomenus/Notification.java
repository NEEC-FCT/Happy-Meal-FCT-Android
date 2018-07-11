package miguelcalado.restauracaomenus;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import static miguelcalado.restauracaomenus.Promocao.month;

/**
 * Created by Diogo on 03/03/2018.
 */

public class Notification {

    private TextView notificationTxt;
    private RelativeLayout notificationRL, notificationIcon;
    private Activity activity;
    private JsonDic myJson;
    String notificationArrayDownload[];
    HashMap<String, ArrayList<Promocao>> notificationHash = new HashMap<>();
    String notificationArrayPrevius[];

    boolean firstEntry,returnActivity;
    int totalNotification;//number of notifications not view;

    private static final String TAG = "NOTIF";

    public static final String restaurante[] = {"Bar Campus", "Teresa", "Cantina", "Casa do P.", "C@m. Come", "Girassol", "Sector + Dep", "Sector + Ed.7", "My Spot", "Bar D. Lídia", "Mininova"};

    public Notification(TextView notificationTxt, RelativeLayout notificationRL, RelativeLayout notificationIcon, Activity activity) {
        this.notificationIcon = notificationIcon;
        this.notificationRL = notificationRL;
        this.notificationTxt = notificationTxt;
        this.activity = activity;
        this.myJson = new JsonDic(DataHolder.getInstance().getDataFilename(), activity);
        this.firstEntry = true;
        this.returnActivity=false;
        notificationRL.setVisibility(View.GONE);
    }

    public void updateNotification() {
        String[] newNotification = myJson.getNotifications();
        String []notification=newNotification(notificationArrayPrevius, newNotification);
        int prevSizeHash=totalNotification;
        int newNotifications=0;
        if(newNotification!=null)
            notificationArrayDownload= removeOld(newNotification);
        if(notification!=null) {
            notificationHash = StringToHashMap(notification);
            newNotifications= getNotificationHashSize(notificationHash);
            totalNotification+=newNotifications;
        }
        if(totalNotification>0) {
            if(prevSizeHash!=totalNotification&&!firstEntry)
                Notify(newNotification,notificationArrayDownload,this.activity);
            if(totalNotification!=prevSizeHash) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notificationIcon.setEnabled(true);
                        notificationRL.setVisibility(View.VISIBLE);
                        notificationTxt.setText(new Integer(totalNotification).toString());
                    }
                });
            }
        } else
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notificationRL.setVisibility(View.GONE);
                }
            });
    }

    private int getNotificationHashSize(HashMap<String, ArrayList<Promocao>> notificationHash) {
        int size=0;
        Iterator<ArrayList<Promocao>>iterator=notificationHash.values().iterator();
        while (iterator.hasNext())
            size+=iterator.next().size();
        return size;
    }

    public static String[] removeDoubles(String[] newNotification, String[] notificationArrayPrevius) {
        ArrayList<String> noDoublesAL = new ArrayList<>();
        for (int i = 0; i < newNotification.length; i = i + 2) {
            String newNotif = newNotification[i].toLowerCase();
            newNotif = Normalizer.normalize(newNotif, Normalizer.Form.NFD);
            newNotif.replaceAll("[^\\p{ASCII}]", "");
            boolean repeat = false;
            for (int j = 0; j < notificationArrayPrevius.length && !repeat; j = j + 2) {
                String oldNotif = notificationArrayPrevius[j].toLowerCase();
                oldNotif = Normalizer.normalize(oldNotif, Normalizer.Form.NFD);
                if (newNotif.equals(oldNotif)) {
                    String weekIdOld = notificationArrayPrevius[j + 1];
                    String weekIdNew = newNotification[i + 1];
                    if (weekIdOld.equals(weekIdNew))
                        repeat = true;
                }
            }
            if (!repeat) {
                noDoublesAL.add(newNotification[i]);
                noDoublesAL.add(newNotification[i + 1]);
            }
        }
        if (noDoublesAL.size() != 0) {
            String[] noDoubles = new String[noDoublesAL.size()];
            for (int i = 0; i < noDoublesAL.size(); i++) {
                noDoubles[i] = noDoublesAL.get(i);
            }
            /*notificationHash.clear();
            notificationHash = StringToHashMap(noDoubles);*/
            return noDoubles;
        } else
            return null;
        /*notificationHash.clear();*/
    }

    public static HashMap<String, ArrayList<Promocao>> StringToHashMap(String[] newNotification) {
        HashMap<String, ArrayList<Promocao>> notification = new HashMap<>();
        for (int i = 0; i < newNotification.length; i = i + 2) {
            for (int j = 0; j < restaurante.length; j++) {
                if (newNotification[i].contains(restaurante[j])) {
                    String promocao = newNotification[i].replace(restaurante[j] + " - ", "");
                    String weekID = newNotification[i + 1];
                    ArrayList<Promocao> promocaoAL;
                    if (notification.containsKey(restaurante[j]))
                        promocaoAL = notification.get(restaurante[j]);
                    else
                        promocaoAL = new ArrayList<>();
                    promocaoAL.add(new Promocao(weekID, promocao));
                    notification.put(restaurante[j], promocaoAL);
                    break;
                }
            }
        }
        return notification;
    }

    public static String[] removeOld(String[] notification) {
        String day = setDay();
        HashMap<String, ArrayList<Promocao>> notificationHash = StringToHashMap(notification);
        Iterator<String> iterator = notificationHash.keySet().iterator();
        while (iterator.hasNext()) {
            String resName = iterator.next();
            ArrayList<Promocao> promocaoAL = notificationHash.get(resName);
            ArrayList<Promocao> newPromocao = new ArrayList<>();
            for (int i = 0; i < promocaoAL.size(); i++) {
                Promocao promocao = promocaoAL.get(i);
                Log.i("DAY", promocao.getDay());
                if (promocao.getDay().contains(day)) {
                    newPromocao.add(promocao);
                }
            }
            if (newPromocao.size() != 0) {
                promocaoAL.clear();
                promocaoAL.addAll(newPromocao);
            } else
                promocaoAL.clear();
        }
        return HashMapToString(notificationHash);
    }

    private static String[] HashMapToString(HashMap<String, ArrayList<Promocao>> notificationHash) {
        ArrayList<String> notificationAL = new ArrayList<>();
        Iterator<String> iterator = notificationHash.keySet().iterator();
        while (iterator.hasNext()) {
            String resName = iterator.next();
            ArrayList<Promocao> promocaoAL = notificationHash.get(resName);
            for (int i = 0; i < promocaoAL.size(); i++) {
                Promocao promocao = promocaoAL.get(i);
                notificationAL.add(resName + " - " + promocao.promocao);
                notificationAL.add(promocao.weekID);
            }
        }
        String notification[] = new String[notificationAL.size()];
        for (int i = 0; i < notificationAL.size(); i++) {
            notification[i] = notificationAL.get(i);
        }
        return notification;
    }

    private static String setDay() {
        Date date = new Date();   // given date
        Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        int minutes = calendar.get(Calendar.MINUTE);
        Integer day = new Integer(calendar.get(Calendar.DAY_OF_MONTH));
        Integer moth = new Integer(calendar.get(Calendar.MONTH));
        Integer year = new Integer(calendar.get(Calendar.YEAR));
        return day.toString() + " " + month[moth] + " " + year.toString();
    }

    public void restartNotification(boolean returnToActivity) {
        firstEntry = false;
        this.returnActivity=returnToActivity;
        //String [] notificationPrevius = new String[notificationArrayPrevius.length];
        if (returnToActivity)
            totalNotification=0;
        //notificationPrevius=notificationArrayPrevius;//guarda a as notificacoes anteriores que ainda nao foram vistas para ver se as anteriores sao iguais as novas*/
        this.notificationArrayPrevius = notificationArrayDownload;
        try {
            downloadThatShit();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
/*        if(!returnToActivity) {
            this.notificationArrayPrevius = notificationPrevius;
        }*/
    }

    public void setNotificationArrayPrevius(String[] notificationArrayPrevius) {
        this.notificationArrayPrevius = notificationArrayPrevius;
    }

    public String[] getNotificationArrayDownload() {
        notificationRL.setVisibility(View.GONE);
        return notificationArrayDownload;
    }

    public String[] getAllNotifications() {
        String[] notification;
        if (notificationArrayDownload != null) {
            if (notificationArrayDownload.length != 0)
                notification = notificationArrayDownload;
            else
                notification = notificationArrayPrevius;
        } else
            notification = null;
        return notification;
    }

    public String[] getNotificationArrayPrevius() {
        return notificationArrayPrevius;
    }

    private static final String signature = "\nEOF\n";

    public static String toFile(String[] notification) {
        String toFile = new String();
        if (notification != null) {
            notification = removeOld(notification);
            for (int i = 0; i < notification.length; i++) {
                toFile += notification[i] + signature;
            }
        } else toFile = null;

        return toFile;
    }

    public static String[] fileToStringArray(String read) {
        if (read == null)
            return null;
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < read.length(); i++) {
            int nxtCut = read.indexOf(signature);
            if (nxtCut != -1) {
                String toAdd = read.substring(0, nxtCut);
                read = read.substring(nxtCut + signature.length());
                strings.add(toAdd);
            }
        }
        String[] notification = new String[strings.size()];
        for (int i = 0; i < notification.length; i++) {
            notification[i] = strings.get(i);
        }
        return notification;
    }

    String filename="pereira";
    String serverFilename;
    String bucket;
    AmazonS3 s3;
    TransferUtility transferUtility;
    Date now;


    public void downloadThatShit() throws FileNotFoundException {
        serverFilename = DataHolder.getInstance().getDataServerFilename();
        bucket = DataHolder.getInstance().getDataBucket();
        //gravar no inicio da app qual o nome do ficheiro interno para ter o json
        //para nas outras classes aceder diretamente em vez de criar strings com o mesmo nome
        //que dá trabalho para se quisermos mudar o nome ou para debugging
        //DataHolder.getInstance().setDatafilename(this.filename);

        // callback method to call credentialsProvider method.
        credentialsProvider();

        // callback method to call the setTransferUtility method
        setTransferUtility();

        setFileToDownload();
    }

    public File createFile() {
        File fileToDownload = new File(activity.getFilesDir(), filename);
        return fileToDownload;
    }

    public static String valueOf(Object obj) {
        return (obj == null) ? "null" : obj.toString();
    }

    public void credentialsProvider() {

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                activity.getApplicationContext(),
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
        transferUtility = new TransferUtility(s3, activity.getApplicationContext());
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
                if (percentage == 100&&!downloadComplete) {
                    Log.d("NOW", "DONE");
                    downloadComplete=true;
                    myJson = new JsonDic(filename, activity.getApplicationContext());
                    updateNotification();
                }

            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("error", "error");
                Log.d("state", "UAUAUAUAUAUAUAUAUAUAUAUAUAUAUAUAUAUAUAUAUAUA");
            }
        });
    }

    private static String makeNotificationText(String[] notArray) {
        int i=0;
        StringBuilder SB = new StringBuilder();
        for (String s : notArray) {
            if(i%2==0)
                SB.append(s);
            else
                SB.append(" - "+s+"\n");
            i++;
        }
        return SB.toString();
    }

    private static android.app.Notification notificationSetup(String notText, NotificationCompat.Builder NCB, Intent intent, Context context) {
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        PendingIntent PI = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        style.bigText(notText);
        NCB.setStyle(style);
        NCB.setSmallIcon(R.drawable.xxhdpi);
        NCB.setAutoCancel(true);
        NCB.setContentTitle("Happy Meal FCT");
        //NCB.setColor(getResources().getColor(R.color.colorPrimaryDark));
        NCB.setContentText(notText);
        NCB.setPriority(NotificationCompat.PRIORITY_MAX);
        NCB.setContentIntent(PI);
        NCB.setVibrate(new long[]{500, 250, 250, 250, 500});
        return NCB.build();
    }

    private static void Notify(String[] newNotification, String[] todayNotification, Activity activity) {
        String notText = makeNotificationText(notificationToMiguel(newNotification));
        NotificationCompat.Builder NCB = new NotificationCompat.Builder(activity, "123");
        NotificationManager NM = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        android.app.Notification notification;
        Intent intent = new Intent(activity, NotificationActivity.class);
        intent.putExtra("notificacao", todayNotification);
        notification = notificationSetup(notText, NCB, intent, activity);
        NM.notify(123, notification);
    }//stop depois mostra a app a correr

    private static String[] notificationToMiguel(String[] newNotification) {
        for(int i =1;i<newNotification.length;i=i+2){
            newNotification[i]=newNotification[i].substring(0,5);
        }
        return newNotification;
    }//stop depois mostra a app a correr
    //isso esta ao contrario as letras
    //isso nao aprece no meu

    public static void makeNotification(String[] newNotification, Activity activity) {
        String fileNotification = "notification.txt";
        String[] oldNotification = fileToStringArray(new ReadWriteFile(fileNotification, activity.getApplicationContext()).readFile());
        String []notification=newNotification(oldNotification, newNotification);
        if(notification!=null)
            if(notification.length!=0)
                Notify(notification,removeOld(newNotification),activity);
    }

    private static String [] newNotification(String[] oldNotification, String[] newNotification) {
        if (newNotification != null) {
            if (newNotification.length != 0) {
                String[] noOld = removeOld(newNotification);
                if (noOld.length != 0) {
                    if (oldNotification != null)
                        return removeDoubles(noOld, oldNotification);
                    else
                        return noOld;
                }
            }
        }
        return null;
    }
}
