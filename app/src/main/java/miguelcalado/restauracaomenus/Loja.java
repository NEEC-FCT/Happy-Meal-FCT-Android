package miguelcalado.restauracaomenus;

/**
 * Created by Miguel-PC on 25/08/2017.
 */

public class Loja {

    private String restaurantName;

    private int restaurantPicture = -1;

    private String restaurantPlace;

    private Restaurant restaurant;

    private Cafetaria cafetaria;


    public Loja(String restaurantName, String restaurantPlace, int restaurantPicture) {
        this.restaurantName = restaurantName;
        this.restaurantPlace=restaurantPlace;
        this.restaurantPicture = restaurantPicture;

    }

    public void Cafetaria( String openTag, int openImage, double open, double close, String mediumPrice, String priceTag){
        cafetaria = new Cafetaria(openTag, openImage, open, close, mediumPrice, priceTag);
    }

    public void Restaurant(String openTag, int openImage, Boolean Vegetariano,double lunchOpen, double lunchClose, double dinnerOpen, double dinnerClose, String mediumPrice, String priceTag) {
        restaurant = new Restaurant(openTag, openImage, Vegetariano,lunchOpen, lunchClose, dinnerOpen, dinnerClose, mediumPrice, priceTag);
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public int getRestaurantPicture() {
        return restaurantPicture;
    }

    public Restaurant getRestaurant(){
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant){
        this.restaurant=restaurant;
    }

    public void setCafetaria(Cafetaria cafetaria){
        this.cafetaria=cafetaria;
    }

    public Cafetaria getCafetaria(){
        return cafetaria;
    }

    public String getRestaurantPlace() {
        return restaurantPlace;
    }
}
