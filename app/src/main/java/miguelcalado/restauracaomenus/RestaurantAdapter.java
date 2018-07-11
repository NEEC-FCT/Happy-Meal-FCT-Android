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
import static android.support.v7.appcompat.R.attr.thickness;

/**
 * Created by Miguel-PC on 25/08/2017.
 */

public class RestaurantAdapter extends ArrayAdapter<Loja> {

    Context context;
    ArrayList<Character> opcao;// r||R=restaurant c||C=cafetaria
    ArrayList<Loja> lojas;

    public RestaurantAdapter(Activity context, ArrayList<Loja> lojas, ArrayList<Character> opcao) {
        super(context, 0, lojas);
        this.context = context;
        this.opcao=opcao;
        this.lojas=lojas;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_main, parent, false);
        }

        Loja currentLoja = getItem(position);

        Date date = new Date();   // given date
        Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        int minutes = calendar.get(Calendar.MINUTE);

        if((opcao.get(position).toString().toLowerCase().equals("c"))&&currentLoja.getCafetaria()!=null) {
            TextView restaurantName = listItemView.findViewById(R.id.rest_name);
            restaurantName.setText(currentLoja.getRestaurantName());
            ImageView restaurantPicture = listItemView.findViewById(R.id.RestaurantePicture);
            if(currentLoja.getRestaurantPicture()==-1) {
                restaurantPicture.setVisibility(View.GONE);
            }
            else {
                Glide.with(context).load(currentLoja.getRestaurantPicture()).fitCenter().centerCrop().into(restaurantPicture);
                //restaurantPicture.setImageResource(currentRestaurant.getRestaurantPicture());
                restaurantPicture.setVisibility(View.VISIBLE);
            }
            setLayoutCafetaria(currentLoja.getCafetaria(), listItemView);
        }else if((opcao.get(position).toString().toLowerCase().equals("r"))&&currentLoja.getRestaurant()!=null) {
            TextView restaurantName = listItemView.findViewById(R.id.rest_name);
            restaurantName.setText(currentLoja.getRestaurantName());
            ImageView restaurantPicture = listItemView.findViewById(R.id.RestaurantePicture);
            if(currentLoja.getRestaurantPicture()==-1) {
                restaurantPicture.setVisibility(View.GONE);
            }
            else {
                Glide.with(context).load(currentLoja.getRestaurantPicture()).fitCenter().centerCrop().into(restaurantPicture);
                //restaurantPicture.setImageResource(currentRestaurant.getRestaurantPicture());
                restaurantPicture.setVisibility(View.VISIBLE);
            }
            setLayoutRestaurant(currentLoja.getRestaurant(), listItemView);
        }
        else{
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main, parent, false);
        }

        return listItemView;
    }

    private void setLayoutCafetaria(Cafetaria currentCafetaria, View listItemView) {
        TextView openTag = listItemView.findViewById(R.id.open_textView);
        openTag.setText(currentCafetaria.getOpenTag());

        TextView mediumPrice = listItemView.findViewById(R.id.price_textView);
        mediumPrice.setText(currentCafetaria.getCafePrice());

        TextView priceTag = listItemView.findViewById(R.id.price_Tag);
        priceTag.setText(currentCafetaria.getPriceTag());

        ImageView openImage = listItemView.findViewById(R.id.open_Image);
        if(currentCafetaria.getOpenImage()==-1) {
            openImage.setVisibility(View.GONE);
        }

        else {
            openImage.setImageResource(currentCafetaria.getOpenImage());
            openImage.setVisibility(View.VISIBLE);
        }
    }

    private void setLayoutRestaurant(Restaurant currentRestaurant, View listItemView) {
        TextView openTag = listItemView.findViewById(R.id.open_textView);
        openTag.setText(currentRestaurant.getOpenTag());

        TextView mediumPrice = listItemView.findViewById(R.id.price_textView);
        mediumPrice.setText(currentRestaurant.getMediumPrice());

        TextView priceTag = listItemView.findViewById(R.id.price_Tag);
        priceTag.setText(currentRestaurant.getPriceTag());

        ImageView openImage = listItemView.findViewById(R.id.open_Image);
        if(currentRestaurant.getOpenImage()==-1) {
            openImage.setVisibility(View.GONE);
        }

        else {
            openImage.setImageResource(currentRestaurant.getOpenImage());
            openImage.setVisibility(View.VISIBLE);
        }
    }

    public char getOpcao(int position) {
        return opcao.get(position);
    }

    public ArrayList<Loja> getLojas() {
        return lojas;
    }
}
