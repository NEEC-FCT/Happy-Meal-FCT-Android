package miguelcalado.restauracaomenus;

/**
 * Created by Diogo on 08/03/2018.
 */

public class Promocao {
    String weekID, promocao;
    public static final String month[] = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};

    public Promocao(String weekID, String promocao) {
        this.weekID = weekID;
        this.promocao = promocao;
    }

    public String getHora() {
        int cut = weekID.indexOf('/');
        String time=weekID.substring(0, cut);
        cut=time.indexOf(':');
        String hora=time.substring(0,cut);
        time=time.substring(cut+1);
        cut=time.indexOf(':');
        String minutos;
        if(cut!=-1)
            minutos=time.substring(0,cut);
        else
            minutos=time;
        return hora+":"+minutos;
    }

    public String getDay() {
        int cut = weekID.indexOf('/');
        String data = weekID.substring(cut + 1);
        int cutDay = data.indexOf('-');
        String day = data.substring(0, cutDay);
        data = data.replaceFirst("-", " ");
        int cutMonth = data.indexOf('-');
        Integer mes = new Integer(data.substring(cutDay + 1, cutMonth));
        String year = data.substring(cutMonth + 1);
        return day + " " + month[mes - 1] + " " + year;
    }

    public String getPromocao() {
        return promocao;
    }

}
