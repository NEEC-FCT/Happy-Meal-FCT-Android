package miguelcalado.restauracaomenus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static miguelcalado.restauracaomenus.Notification.StringToHashMap;
import static miguelcalado.restauracaomenus.Notification.toFile;

public class NotificationActivity extends AppCompatActivity {

    String notificacaoArray[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        TextView title[]={findViewById(R.id.Res1Title),findViewById(R.id.Res2Title),findViewById(R.id.Res3Title),findViewById(R.id.Res4Title),findViewById(R.id.Res5Title),findViewById(R.id.Res6Title),findViewById(R.id.Res7Title),findViewById(R.id.Res8Title),findViewById(R.id.Res9Title),findViewById(R.id.Res10Title),findViewById(R.id.Res11Title)};
        TextView promocao[]={findViewById(R.id.Res1Promocao),findViewById(R.id.Res2Promocao),findViewById(R.id.Res3Promocao),findViewById(R.id.Res4Promocao),findViewById(R.id.Res5Promocao),findViewById(R.id.Res6Promocao),findViewById(R.id.Res7Promocao),findViewById(R.id.Res8Promocao),findViewById(R.id.Res9Promocao),findViewById(R.id.Res10Promocao),findViewById(R.id.Res11Promocao)};
        TextView hora[]={findViewById(R.id.Res1Hora),findViewById(R.id.Res2Hora),findViewById(R.id.Res3Hora),findViewById(R.id.Res4Hora),findViewById(R.id.Res5Hora),findViewById(R.id.Res6Hora),findViewById(R.id.Res7Hora),findViewById(R.id.Res8Hora),findViewById(R.id.Res9Hora),findViewById(R.id.Res10Hora),findViewById(R.id.Res11Hora)};

        Intent intent = getIntent();
         notificacaoArray = intent.getStringArrayExtra("notificacao");
        HashMap<String, ArrayList<Promocao>> notificacaoHash=new HashMap<>();
        ArrayList<String> restaurante=new ArrayList<>();
        try {
            if (notificacaoArray != null && notificacaoArray.length > 0) {
                notificacaoHash = StringToHashMap(notificacaoArray);
                restaurante = new ArrayList<>(notificacaoHash.keySet());

                for (int i = 0; i < title.length; i++) {
                    if (i >= notificacaoHash.size()) {
                        title[i].setVisibility(View.GONE);
                        promocao[i].setVisibility(View.GONE);
                        hora[i].setVisibility(View.GONE);
                    } else {
                        String ResName = restaurante.get(i);
                        SetNotificationToView setNotificationToView = new SetNotificationToView(title[i], promocao[i], hora[i], ResName, notificacaoHash.get(ResName));
                    }
                }
            } else {
                for(int i=0;i<title.length;i++){
                    if(i!=0)
                        title[i].setVisibility(View.GONE);
                    promocao[i].setVisibility(View.GONE);
                    hora[i].setVisibility(View.GONE);
                }
                title[0].setText("Não há notificações");
                title[0].setGravity(Gravity.CENTER);
            }
        }catch(NullPointerException e){
            for(int i=0;i<title.length;i++){
                if(i!=0)
                    title[i].setVisibility(View.GONE);
                promocao[i].setVisibility(View.GONE);
                hora[i].setVisibility(View.GONE);
            }
            title[0].setText("Não há notificações");
            title[0].setGravity(Gravity.CENTER);
        }
    }

    String fileNotification = "notification.txt";

    @Override
    protected void onStop() {
        new ReadWriteFile(fileNotification,getApplicationContext()).saveFile(toFile(notificacaoArray));
        super.onStop();
    }

    class SetNotificationToView{
        TextView titleTxt,promocaoTxt,horaTxt;
        String title,promocoes,horas;
        ArrayList<Promocao> notificacao;

        public SetNotificationToView(TextView titleTxt, TextView promocaoTxt, TextView horaTxt, String title, ArrayList<Promocao> notificacao) {
            this.titleTxt = titleTxt;
            this.promocaoTxt = promocaoTxt;
            this.horaTxt = horaTxt;
            this.title = title;
            this.notificacao = notificacao;
            promocoes="";
            horas="";
            setText();
        }

        private void setText(){
            arrayToString();
            titleTxt.setText(title);
            promocaoTxt.setText(promocoes);
            horaTxt.setText(horas);
        }

        private void arrayToString() {
            for(int i=0;i<notificacao.size();i++){
                Promocao promocao=notificacao.get(i);
                promocoes+=promocao.getPromocao()+"\n";
                horas+=promocao.getHora()+"\n";
            }
        }
    }
}