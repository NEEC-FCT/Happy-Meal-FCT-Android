package miguelcalado.restauracaomenus;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Miguel-PC on 08/09/2017.
 */

public class MenuAdapterSector extends ArrayAdapter<Menu> {

    public MenuAdapterSector(Activity context, ArrayList<Menu> menus) {
        super(context, 0, menus);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.sectormais_menu, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        Menu currentMenu = getItem(position);

        //View textContainer = listItemView.findViewById(R.id.text_container);

        //int color = ContextCompat.getColor(getContext(), mcolor);
        //textContainer.setBackgroundColor(color);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView prato = listItemView.findViewById(R.id.pratos);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        prato.setText(currentMenu.getPrato());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView prato_principal = listItemView.findViewById(R.id.prato_principal);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        prato_principal.setText(currentMenu.getPrato_principal());

        TextView price = listItemView.findViewById(R.id.price);
        price.setText(currentMenu.getPrice());
        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }

}
