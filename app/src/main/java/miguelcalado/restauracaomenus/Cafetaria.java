package miguelcalado.restauracaomenus;

/**
 * Created by Diogo on 17/01/2018.
 */

public class Cafetaria {

    private String openTag;

    private int openImage = -1;

    private double open, close;

    private String cafePrice;

    private String priceTag;


    public Cafetaria(String openTag, int openImage, double open, double close, String cafePrice, String priceTag) {
        this.openTag = openTag;
        this.openImage=openImage;
        this.open = open;
        this.close = close;
        this.cafePrice = cafePrice;
        this.priceTag = priceTag;
    }

    public String getOpenTag() {
        return openTag;
    }

    public void setOpenTag(String openTag) {
        this.openTag = openTag;
    }

    public void setOpenImage(int openImage) {
        this.openImage = openImage;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public String getCafePrice() {
        return cafePrice;
    }

    public void setMediumPrice(String cafePrice) {
        this.cafePrice = cafePrice;
    }

    public String getPriceTag() {
        return priceTag;
    }

    public void setPriceTag(String priceTag) {
        this.priceTag = priceTag;
    }

    public int getOpenImage() {
        return openImage;
    }

    public boolean diffTime(double time) {
        if ((time >= open&& time <= close))
            return true;
        return false;
    }
}