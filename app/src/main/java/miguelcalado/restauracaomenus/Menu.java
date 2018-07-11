package miguelcalado.restauracaomenus;

/**
 * Created by Miguel-PC on 08/09/2017.
 */

public class Menu {

    private String mPrato;

    private String mPrato_principal;

    private String mPrice;

    public Menu (String prato, String prato_principal, String price) {
        mPrato=prato;
        mPrato_principal=prato_principal;
        mPrice=price;
    }

    public Menu (String prato, String prato_principal) {
        mPrato=prato;
        mPrato_principal=prato_principal;
    }

    public String getPrato() {
        return mPrato;
    }

    public String getPrato_principal() {
        return mPrato_principal;
    }

    public String getPrice() {
        return mPrice;
    }
}
