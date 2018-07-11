package miguelcalado.restauracaomenus;

/**
 * Created by Miguel-PC on 11/09/2017.
 */

public class MenuCafetaria {

    private String sandes;

    private String sandes_string;

    private String sandes_subString="";

    private String sandesPrice;

    private String subSandesPrice;

    private int setaImage;

    private Boolean option = false;

    public MenuCafetaria(String tSandes, String tSandes_string, String tsandesPrice) {
        sandes = tSandes;
        sandes_string=tSandes_string;
        sandesPrice = tsandesPrice;
    }

    public MenuCafetaria(String tSandes, String tSandes_string, String tsandes_subString, String tsandesPrice, String tsubSandesPrice
            , Boolean toption) {
        sandes = tSandes;
        sandes_string=tSandes_string;
        sandes_subString = tsandes_subString;
        sandesPrice=tsandesPrice;
        subSandesPrice=tsubSandesPrice;
        option = toption;
    }

    public String getSandes() {
        return sandes;
    }

    public String getSandes_string() {
        return sandes_string;
    }

    public String getSandes_subString() {
        return sandes_subString;
    }


    public Boolean getOption() {
        return option;
    }

    public void changeOption(Boolean realOption) {
        option=realOption;
    }

    public String getSandesPrice() {
        return sandesPrice;
    }

    public  String getSubSandesPrice() {
        return subSandesPrice;
    }
}
