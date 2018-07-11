package miguelcalado.restauracaomenus;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//import com.example.android.RestauracaoFCT.R;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.R.attr.width;
import static android.support.v7.appcompat.R.attr.height;

/**
 * Created by Miguel-PC on 25/08/2017.
 */

public class listAdapter extends ArrayAdapter<Loja> {

    Context context;
    ArrayList<Character> opcao;// r||R=restaurant c||C=cafetaria
    ArrayList<Loja>lojas;

    public listAdapter(Activity context, ArrayList<Loja> lojas, ArrayList<Character> opcao) {
        super(context, 0, lojas);
        this.context = context;
        this.opcao = opcao;
        this.lojas=lojas;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Loja currentLoja = getItem(position);

        if((opcao.get(position).toString().toLowerCase().equals("c"))&&currentLoja.getCafetaria()!=null) {

            TextView restaurantPlace = listItemView.findViewById(R.id.restaurantPlace);
            restaurantPlace.setText(currentLoja.getRestaurantPlace());

            TextView restaurantName = listItemView.findViewById(R.id.rest_name);
            restaurantName.setText(currentLoja.getRestaurantName());
            ImageView restaurantPicture = listItemView.findViewById(R.id.RestaurantePicture);
            if (currentLoja.getRestaurantPicture() == -1) {
                restaurantPicture.setVisibility(View.GONE);
            } else {
                Glide.with(context).load(currentLoja.getRestaurantPicture()).fitCenter().centerCrop().into(restaurantPicture);
                //restaurantPicture.setImageResource(currentRestaurant.getRestaurantPicture());
                //restaurantPicture.setVisibility(View.VISIBLE);
            }
            setLayoutCafetaria(currentLoja.getCafetaria(), listItemView);
        }else if((opcao.get(position).toString().toLowerCase().equals("r"))&&currentLoja.getRestaurant()!=null) {

            TextView restaurantName = listItemView.findViewById(R.id.rest_name);
            restaurantName.setText(currentLoja.getRestaurantName());

            TextView restaurantPlace = listItemView.findViewById(R.id.restaurantPlace);
            restaurantPlace.setText(currentLoja.getRestaurantPlace());

            ImageView restaurantPicture = listItemView.findViewById(R.id.RestaurantePicture);
            if (currentLoja.getRestaurantPicture() == -1) {
                restaurantPicture.setVisibility(View.GONE);
            } else {
                Glide.with(context).load(currentLoja.getRestaurantPicture()).fitCenter().centerCrop().into(restaurantPicture);
                //restaurantPicture.setImageResource(currentRestaurant.getRestaurantPicture());
                restaurantPicture.setVisibility(View.VISIBLE);
            }
            setLayoutRestaurant(currentLoja.getRestaurant(), listItemView);
        } else {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

        }
        return listItemView;
    }

    private void setLayoutCafetaria(Cafetaria currentCafetaria, View listItemView) {
        TextView vegetariano = listItemView.findViewById(R.id.vegetarianoText);
        vegetariano.setVisibility(View.INVISIBLE);

        TextView price_Tag = listItemView.findViewById(R.id.price_Tag);
        price_Tag.setText(currentCafetaria.getPriceTag());

        TextView lunchTime = listItemView.findViewById(R.id.LunchTime);
        TextView lunchClose = listItemView.findViewById(R.id.LunchClose);
        TextView DinnerTime = listItemView.findViewById(R.id.DinnerTime);
        TextView DinnerClose = listItemView.findViewById(R.id.DinnerClose);

        lunchTime.setText(GoodTime(String.valueOf(currentCafetaria.getOpen())));
        lunchClose.setText(GoodTime(String.valueOf(currentCafetaria.getClose())));

        DinnerTime.setVisibility(View.INVISIBLE);
        DinnerClose.setVisibility(View.INVISIBLE);
    }

    private void setLayoutRestaurant(Restaurant currentRestaurant, View listItemView) {

        TextView vegetariano = listItemView.findViewById(R.id.vegetarianoText);
        if (currentRestaurant.hasVegetariano()) {
            vegetariano.setVisibility(View.VISIBLE);
            vegetariano.setText("Vegetariano");
        } else {
            vegetariano.setVisibility(View.GONE);
        }

        TextView price_Tag = listItemView.findViewById(R.id.price_Tag);
        price_Tag.setText(currentRestaurant.getPriceTag());

        TextView lunchTime = listItemView.findViewById(R.id.LunchTime);
        TextView lunchClose = listItemView.findViewById(R.id.LunchClose);
        TextView DinnerTime = listItemView.findViewById(R.id.DinnerTime);
        TextView DinnerClose = listItemView.findViewById(R.id.DinnerClose);

        String aa = String.valueOf(currentRestaurant.getLunchOpen());
        String bb = String.valueOf(currentRestaurant.getLunchClose());
        lunchTime.setText(GoodTime(String.valueOf(currentRestaurant.getLunchOpen())));
        lunchClose.setText(GoodTime(String.valueOf(currentRestaurant.getLunchClose())));

        if (currentRestaurant.getDinnerOpen() != -1) {
            DinnerTime.setVisibility(View.VISIBLE);
            DinnerClose.setVisibility(View.VISIBLE);
            String cc = String.valueOf(currentRestaurant.getDinnerOpen());
            String dd = String.valueOf(currentRestaurant.getDinnerClose());
            DinnerTime.setText(GoodTime(String.valueOf(currentRestaurant.getDinnerOpen())));
            DinnerClose.setText(GoodTime(String.valueOf(currentRestaurant.getDinnerClose())));
        } else {
            DinnerTime.setVisibility(View.GONE);
            DinnerClose.setVisibility(View.GONE);
        }
    }

    public String GoodTime(String something) {
        if (something.length() == 4) {
            something = something + "0";
        }

        something = something.replace(".", ":");
        return something;
    }

    public char getOpcao(int position) {
        return opcao.get(position);
    }

    public ArrayList<Loja> getLojas() {
        return lojas;
    }
}
