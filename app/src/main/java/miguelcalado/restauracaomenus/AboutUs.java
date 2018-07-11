package miguelcalado.restauracaomenus;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Miguel-PC on 14/01/2018.
 */

public class AboutUs extends AppCompatActivity {

    Boolean swipe_active = false;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);

        ImageView faceImage = (ImageView) findViewById(R.id.FaceImage);
        ImageView GmailImage = (ImageView) findViewById(R.id.GmailImagem);

        faceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent FaceBookIntent = newFacebookIntent(getPackageManager(), "https://www.facebook.com/HappyMealFCT");
                startActivity(FaceBookIntent);
            }
        });

        GmailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "happymealfct@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Gostamos muito da vossa APP!!");
                startActivity(emailIntent);
            }
        });


    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

}
