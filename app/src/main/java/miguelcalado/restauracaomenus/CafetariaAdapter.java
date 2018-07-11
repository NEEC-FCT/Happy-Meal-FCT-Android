package miguelcalado.restauracaomenus;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Miguel-PC on 08/09/2017.
 */

public class CafetariaAdapter extends ArrayAdapter<MenuCafetaria> {

    public CafetariaAdapter(Activity context, ArrayList<MenuCafetaria> menuCafetarias) {
        super(context, 0, menuCafetarias);
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
          listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.cafetaria_item, parent, false);
        }

        final MenuCafetaria currentMenuCafetaria = getItem(position);


        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView sandes = listItemView.findViewById(R.id.sandes);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        sandes.setText(currentMenuCafetaria.getSandes());

        final TextView sandes_String = listItemView.findViewById(R.id.sandesString);
        sandes_String.setText(currentMenuCafetaria.getSandes_string());

        final String subString = currentMenuCafetaria.getSandes_subString();
        final View listItemView2 = listItemView;

        final TextView priceSandes = (TextView) listItemView.findViewById(R.id.sandes_price);
        priceSandes.setText(currentMenuCafetaria.getSandesPrice());
        final String subPrice = currentMenuCafetaria.getSandesPrice();

        if(subString!="") {
            final ImageView squareDown = (ImageView) listItemView.findViewById(R.id.square_down);
            squareDown.setImageResource(R.drawable.squar_down);
            squareDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(currentMenuCafetaria.getOption()==false) {
                        String finalSandes = currentMenuCafetaria.getSandes_string() + "\n" + subString;
                        sandes_String.setText(finalSandes);
                        String finalPrice = subPrice + "\n" + currentMenuCafetaria.getSubSandesPrice();
                        priceSandes.setText(finalPrice);
                        currentMenuCafetaria.changeOption(true);
                        squareDown.setImageResource(R.drawable.square_up);
                    }
                    else {
                        sandes_String.setText(currentMenuCafetaria.getSandes_string());
                        priceSandes.setText(currentMenuCafetaria.getSandesPrice());
                        currentMenuCafetaria.changeOption(false);
                        squareDown.setImageResource(R.drawable.squar_down);
                    }
                }
            });
        } else {
            final ImageView squareDown = (ImageView) listItemView.findViewById(R.id.square_down);
            squareDown.setVisibility(View.GONE);
        }
        return listItemView;
    }

}