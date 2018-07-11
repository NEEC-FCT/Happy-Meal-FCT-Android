package miguelcalado.restauracaomenus;

/**
 * Created by Miguel-PC on 25/08/2017.
 */

public class Restaurant {

    private String openTag;

    private int openImage = -1;

    private Boolean Vegetariano;

    private double lunchOpen, lunchClose;

    private double dinnerOpen, dinnerClose;

    private String mediumPrice;

    private String priceTag;

    public Restaurant(String openTag, int openImage,Boolean Vegetariano,double lunchOpen, double lunchClose, double dinnerOpen, double dinnerClose, String mediumPrice, String priceTag) {
        this.openTag = openTag;
        this.openImage = openImage;
        this.lunchOpen = lunchOpen;
        this.lunchClose = lunchClose;
        this.dinnerOpen = dinnerOpen;
        this.dinnerClose = dinnerClose;
        this.mediumPrice = mediumPrice;
        this.priceTag = priceTag;
        this.Vegetariano=Vegetariano;
    }

    public String getOpenTag() {
        return openTag;
    }

    public String getMediumPrice() {
        return mediumPrice;
    }

    public int getOpenImage() {
        return openImage;
    }

    public void changeOpenImage(int openImage) {
        this.openImage = openImage;
    }

    public String getPriceTag() {
        return priceTag;
    }

    public void changeOpenTag(String otherTag) {
        openTag = otherTag;
    }

    public double getLunchOpen() {
        return lunchOpen;
    }

    public double getLunchClose() {
        return lunchClose;
    }

    public double getDinnerOpen() {
        return dinnerOpen;
    }

    public double getDinnerClose() {
        return dinnerClose;
    }

    public boolean diffTime(double time) {
        if ((time >= lunchOpen && time <= lunchClose) || (time >= dinnerOpen && time <= dinnerClose))
            return true;
        return false;
    }

    public boolean hasVegetariano() {
        return Vegetariano;
    }
}