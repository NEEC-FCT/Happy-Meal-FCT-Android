package miguelcalado.restauracaomenus;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Miguel-PC on 02/03/2018.
 */

public class alignPrices extends AppCompatActivity {

    public String nine ="9999";

    private static final alignPrices Align = new alignPrices();
    public static alignPrices getInstance() {return Align;}

    public void alignOne (String [] prato, Context mContext, int color, LinearLayout categ) {

        for(int i=0;i<=prato.length-2 ;i=i+2) {

            LinearLayout linearLayout = new LinearLayout(mContext);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView ProgrammaticallyTextView = new TextView(mContext);

            FrameLayout ladderFL = new FrameLayout(mContext);
            if(prato[i+1].equals(nine)) {
                LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                ladderFL.setLayoutParams(ladderFLParams);
                ProgrammaticallyTextView.setPadding(0, 0, 6, 0);

            } else {
                LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                ladderFLParams.weight = 6;
                ladderFL.setLayoutParams(ladderFLParams);
            }

            ProgrammaticallyTextView.setText(prato[i]);
            ProgrammaticallyTextView.setTextSize(17);
            ProgrammaticallyTextView.setTextColor(color);
            linearLayout.addView(ladderFL);


            ladderFL.addView(ProgrammaticallyTextView);
            if(!prato[i+1].equals(nine)) {
                FrameLayout ladderFL1 = new FrameLayout(mContext);
                LinearLayout.LayoutParams ladderFLParams1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                ladderFLParams1.weight = 1;
                ladderFL1.setLayoutParams(ladderFLParams1);

                TextView ProgrammaticallyTextView1 = new TextView(mContext);
                ProgrammaticallyTextView1.setText(prato[i + 1]);
                ProgrammaticallyTextView1.setPadding(0, 0, 6, 0);
                ProgrammaticallyTextView1.setTextSize(17);
                ProgrammaticallyTextView1.setTextColor(color);

                ladderFL1.addView(ProgrammaticallyTextView1);

                linearLayout.addView(ladderFL1);
            }

            categ.addView(linearLayout);
        }

    }

}
