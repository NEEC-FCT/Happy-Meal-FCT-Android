package miguelcalado.restauracaomenus;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.nineoldandroids.view.*;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import miguelcalado.restauracaomenus.BarCampusFile.BarCampusFile.BarCampus;
import miguelcalado.restauracaomenus.BarCampusFile.BarCampusFile.BarCampusCafetaria;
import miguelcalado.restauracaomenus.CampusComeFile.CampusCome;
import miguelcalado.restauracaomenus.CantinaFile.Cantina;
import miguelcalado.restauracaomenus.CasaPessoalFile.CasaPessoal;
import miguelcalado.restauracaomenus.CasaPessoalFile.CasaPessoalCafetaria;
import miguelcalado.restauracaomenus.GirassolFile.Girassol;
import miguelcalado.restauracaomenus.GirassolFile.GirassolCafetaria;
import miguelcalado.restauracaomenus.LidiaFile.Lidia;
import miguelcalado.restauracaomenus.LidiaFile.LidiaCafetaria;
import miguelcalado.restauracaomenus.MySpotFile.MySpot;
import miguelcalado.restauracaomenus.MySpotFile.MySpotCafetaria;
import miguelcalado.restauracaomenus.SectorMaisDepFile.SectorMaisDep;
import miguelcalado.restauracaomenus.SectorMaisDepFile.SectorMaisDepCafetaria;
import miguelcalado.restauracaomenus.SectorMaisEd7File.SectorMaisCafetaria;
import miguelcalado.restauracaomenus.SectorMaisEd7File.Sector_mais;
import miguelcalado.restauracaomenus.TeresaFile.Teresa;
import miguelcalado.restauracaomenus.TeresaFile.TeresaCafetaria;


import static java.lang.Math.abs;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, ObservableScrollViewCallbacks {

    public ObservableGridView gridView;
    //public ListView lista;
    public ObservableListView lista;
    public ArrayList<Restaurant> restaurants;
    public RestaurantAdapter itemsAdapter, itemsAdapterC, itemsAdapterS, itemsAdapterCS;
    public listAdapter listaAdapter, listaAdapterC, listaAdapterS, listaAdapterCS;
    public Boolean listSelected = true;
    public EditText search;
    public TextView notificationTxt;
    public RelativeLayout notificationRL, notificationIcon;
    public Notification notification;

    public Animation anifade;

    TextView refeicaoBtn, cafetariaBtn;
    ImageView cruzIcon, searchIcon;

    String fileScore = "score.txt";
    String fileNotification = "notification.txt";

    String option;
    public final static String optionRefeicao = "refeição";
    public final static String optionCafetaria = "cafetaria";
    public final static String optionSearch = "search";

    //know if i return from notification_activity
    boolean notification_activity=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_view);


        String listGrid = new ReadWriteFile(fileScore,getApplicationContext()).readFile();
        if (listGrid != null) {
            if (listGrid.equals("list")) {
                listSelected = true;
            } else
                listSelected = false;
        }

        lista = (ObservableListView) findViewById(R.id.list);
        lista.setScrollViewCallbacks(this);

        gridView = (ObservableGridView) findViewById(R.id.gridview);
        gridView.setScrollViewCallbacks(this);

        LinearLayout aa = (LinearLayout) findViewById(R.id.ImaButtonRL);
        aa.setVisibility(View.GONE);

        anifade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in2);

        final ArrayList<Loja> lojas = new ArrayList<Loja>();
        addLojas(lojas);

        option = getIntent().getStringExtra("option");
        refeicaoBtn = (TextView) findViewById(R.id.refeicao);
        cafetariaBtn = (TextView) findViewById(R.id.cafetaria);

        setButtonsToOption(option);

        cafetariaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                option = optionCafetaria;
                lojasToGridOrList(option, itemsAdapterC, listaAdapterC);

            }
        });
        refeicaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                option = optionRefeicao;
                lojasToGridOrList(option, itemsAdapter, listaAdapter);
            }
        });


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//nao mostrar o teclado no inicio
        search = (EditText) findViewById(R.id.search);
        search.setCursorVisible(false);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setCursorVisible(true);
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String toSearch = s.toString();
                if (!toSearch.isEmpty()) {
                    cruzIcon.setVisibility(View.VISIBLE);
                    cruzIcon.setEnabled(true);
                    if (toSearch.contains("\n")) {
                        toSearch = toSearch.replace("\n", "");
                        search.setText("");
                        search.setCursorVisible(false);
                        cruzIcon.setEnabled(false);
                        cruzIcon.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Procurando: " + toSearch, Toast.LENGTH_SHORT).show();

                        InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        input.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    }
                    searchComponent(lojas, toSearch);
                }
                if ((before > count) && (count == 0)) {
                    search.setText("");
                    cruzIcon.setEnabled(false);
                    cruzIcon.setVisibility(View.INVISIBLE);
                    search.setCursorVisible(false);
                    option = optionRefeicao;
                    lojasToGridOrList(option, itemsAdapter, listaAdapter);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        searchIcon = (ImageView) findViewById(R.id.lupa);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toSearch = search.getText().toString();
                if (!toSearch.isEmpty()) {
                    searchComponent(lojas, toSearch);
                    Toast.makeText(MainActivity.this, "Procurando: " + toSearch, Toast.LENGTH_SHORT).show();
                    InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    input.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    search.setText("");
                    cruzIcon.setEnabled(false);
                    cruzIcon.setVisibility(View.INVISIBLE);
                    search.setCursorVisible(false);
                }
            }
        });

        cruzIcon = (ImageView) findViewById(R.id.cruz);
        cruzIcon.setVisibility(View.INVISIBLE);
        cruzIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toSearch = search.getText().toString();
                if (!toSearch.isEmpty()) {
                    search.setText("");
                    cruzIcon.setEnabled(false);
                    cruzIcon.setVisibility(View.INVISIBLE);
                    search.setCursorVisible(false);
                    option = optionRefeicao;
                    lojasToGridOrList(option, itemsAdapter, listaAdapter);
                    InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    input.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        //final GridView gridView = (GridView) findViewById(R.id.gridview);

        //final ListView lista = (ListView) findViewById(R.id.list_test);

        ArrayList<Loja> lojasR = lojaWithTheOption(lojas, 'r');
        ArrayList<Character> characterR = new ArrayList<>();
        for (int i = 0; i < lojasR.size(); i++)
            characterR.add('r');
        itemsAdapter = new RestaurantAdapter(this, lojasR, characterR);
        listaAdapter = new listAdapter(this, lojasR, characterR);

        ArrayList<Loja> lojasC = lojaWithTheOption(lojas, 'c');
        ArrayList<Character> characterC = new ArrayList<>();
        lojasC = lojaWithTheOption(lojas, 'c');
        for (int i = 0; i < lojasC.size(); i++)
            characterC.add('c');
        itemsAdapterC = new RestaurantAdapter(this, lojasC, characterC);
        listaAdapterC = new listAdapter(this, lojasC, characterC);

        if (option.equals(optionRefeicao))
            gridView.setAdapter(itemsAdapter);
        else if (option.equals(optionCafetaria))
            gridView.setAdapter(itemsAdapterC);
        gridView.setScrollingCacheEnabled(false);

        if (option.equals(optionRefeicao))
            lista.setAdapter(listaAdapter);
        else if (option.equals(optionCafetaria))
            lista.setAdapter(listaAdapterC);

        if (listSelected) {
            gridView.setVisibility(View.GONE);
            lista.setVisibility(View.VISIBLE);
        } else {
            lista.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        }


        //just to set everything up
        updateTextView(lojas, itemsAdapter);

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000 * 60 * 2);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTextView(lojas, itemsAdapter);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RestaurantAdapter restaurantAdapter = (RestaurantAdapter) gridView.getAdapter();
                char opcao = restaurantAdapter.getOpcao(i);
                ArrayList<Loja> lojaArrayList = restaurantAdapter.getLojas();
                switch (lojaArrayList.get(i).getRestaurantName()) {
                    case "Cantina":
                        Intent cantina = new Intent(MainActivity.this, Cantina.class);
                        startActivity(cantina);
                        break;
                    case "Teresa":
                        Intent teresa;
                        if (opcao == 'r')
                            teresa = new Intent(MainActivity.this, Teresa.class);
                        else
                            teresa = new Intent(MainActivity.this, TeresaCafetaria.class);

                        startActivity(teresa);
                        break;
                    case "Girassol":
                        Intent tia;
                        if (opcao == 'r')
                            tia = new Intent(MainActivity.this, Girassol.class);
                        else
                            tia = new Intent(MainActivity.this, GirassolCafetaria.class);
                        startActivity(tia);
                        break;
                    case "Casa do P.":
                        Intent casaPovo;
                        if (opcao == 'r')
                            casaPovo = new Intent(MainActivity.this, CasaPessoal.class);
                        else
                            casaPovo = new Intent(MainActivity.this, CasaPessoalCafetaria.class);
                        startActivity(casaPovo);
                        break;
                    case "My Spot":
                        Intent mySpot;
                        if (opcao == 'r')
                            mySpot = new Intent(MainActivity.this, MySpot.class);
                        else
                            mySpot = new Intent(MainActivity.this, MySpotCafetaria.class);
                        startActivity(mySpot);
                        break;
                    case "C@m. Come":
                        Intent camCome;
                        //if (opcao == 'r')
                        camCome = new Intent(MainActivity.this, CampusCome.class);
/*                        else
                            camCome = new Intent(MainActivity.this, CampusComeCafetaria.class);*/
                        startActivity(camCome);
                        break;
                    case "Sector + Ed.7":
                        Intent sector_mais;
                        if (opcao == 'r')
                            sector_mais = new Intent(MainActivity.this, Sector_mais.class);
                        else
                            sector_mais = new Intent(MainActivity.this, SectorMaisCafetaria.class);
                        startActivity(sector_mais);
                        break;
                    case "Bar Campus":
                        Intent BarCampus;
                        if (opcao == 'r')
                            BarCampus = new Intent(MainActivity.this, BarCampus.class);
                        else
                            BarCampus = new Intent(MainActivity.this, BarCampusCafetaria.class);
                        startActivity(BarCampus);
                        break;
                    case "Sector + Dep":
                        Intent sector_maisDep;
                        if (opcao == 'r')
                            sector_maisDep = new Intent(MainActivity.this, SectorMaisDep.class);
                        else
                            sector_maisDep = new Intent(MainActivity.this, SectorMaisDepCafetaria.class);
                        startActivity(sector_maisDep);
                        break;
                    case "Bar D. Lídia":
                        Intent Dlidia;
                        if (opcao == 'r')
                            Dlidia = new Intent(MainActivity.this, Lidia.class);
                        else
                            Dlidia = new Intent(MainActivity.this, LidiaCafetaria.class);
                        startActivity(Dlidia);
                        break;
                }
            }
        });
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listAdapter listaAdapter = (listAdapter) lista.getAdapter();
                ArrayList<Loja> lojaArrayList = listaAdapter.getLojas();
                char opcao = listaAdapter.getOpcao(i);
                switch (lojaArrayList.get(i).getRestaurantName()) {
                    case "Cantina":
                        Intent cantina = new Intent(MainActivity.this, Cantina.class);
                        startActivity(cantina);
                        break;
                    case "Teresa":
                        Intent teresa;
                        if (opcao == 'r')
                            teresa = new Intent(MainActivity.this, Teresa.class);
                        else
                            teresa = new Intent(MainActivity.this, TeresaCafetaria.class);
                        startActivity(teresa);
                        break;
                    case "Girassol":
                        Intent tia;
                        if (opcao == 'r')
                            tia = new Intent(MainActivity.this, Girassol.class);
                        else
                            tia = new Intent(MainActivity.this, GirassolCafetaria.class);
                        startActivity(tia);
                        break;
                    case "Casa do P.":
                        Intent casaPovo;
                        if (opcao == 'r')
                            casaPovo = new Intent(MainActivity.this, CasaPessoal.class);
                        else
                            casaPovo = new Intent(MainActivity.this, CasaPessoalCafetaria.class);
                        startActivity(casaPovo);
                        break;
                    case "My Spot":
                        Intent mySpot;
                        if (opcao == 'r')
                            mySpot = new Intent(MainActivity.this, MySpot.class);
                        else
                            mySpot = new Intent(MainActivity.this, MySpotCafetaria.class);
                        startActivity(mySpot);
                        break;
                    case "C@m. Come":
                        Intent camCome;
                        //if (opcao == 'r')
                        camCome = new Intent(MainActivity.this, CampusCome.class);
/*                        else
                            camCome = new Intent(MainActivity.this, CampusComeCafetaria.class);*/
                        startActivity(camCome);
                        break;
                    case "Sector + Ed.7":
                        Intent sector_mais;
                        if (opcao == 'r')
                            sector_mais = new Intent(MainActivity.this, Sector_mais.class);
                        else
                            sector_mais = new Intent(MainActivity.this, SectorMaisCafetaria.class);
                        startActivity(sector_mais);
                        break;
                    case "Bar Campus":
                        Intent BarCampus;
                        if (opcao == 'r')
                            BarCampus = new Intent(MainActivity.this, BarCampus.class);
                        else
                            BarCampus = new Intent(MainActivity.this, BarCampusCafetaria.class);
                        startActivity(BarCampus);
                        break;
                    case "Sector + Dep":
                        Intent sector_maisDep;
                        if (opcao == 'r')
                            sector_maisDep = new Intent(MainActivity.this, SectorMaisDep.class);
                        else
                            sector_maisDep = new Intent(MainActivity.this, SectorMaisDepCafetaria.class);
                        startActivity(sector_maisDep);
                        break;
                    case "Bar D. Lídia":
                        Intent Dlidia;
                        if (opcao == 'r')
                            Dlidia = new Intent(MainActivity.this, Lidia.class);
                        else
                            Dlidia = new Intent(MainActivity.this, LidiaCafetaria.class);
                        startActivity(Dlidia);
                        break;
                }
            }
        });

        notificationIcon = (RelativeLayout) findViewById(R.id.notificationIcon);
        notificationTxt = (TextView) findViewById(R.id.notificationTxt);
        notificationRL = (RelativeLayout) findViewById(R.id.notificationRL);
        notification = new Notification(notificationTxt, notificationRL, notificationIcon, MainActivity.this);
        notification.setNotificationArrayPrevius(notification.fileToStringArray( new ReadWriteFile(fileNotification,getApplicationContext()).readFile()));
        notification.updateNotification();
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notification_activity=true;
                Intent notificacao = new Intent(MainActivity.this, NotificationActivity.class);
                notificacao.putExtra("notificacao", notification.getNotificationArrayDownload());
                startActivity(notificacao);
            }
        });
    }

    private void lojasToGridOrList(String option, RestaurantAdapter itemsAdapter, listAdapter listaAdapter) {
        setButtonsToOption(option);
        if (listSelected) {
            lista.setAdapter(listaAdapter);
            lista.setScrollingCacheEnabled(false);
        } else {
            gridView.setAdapter(itemsAdapter);
            gridView.setScrollingCacheEnabled(false);
        }
    }

    private void searchComponent(ArrayList<Loja> lojas, String toSearch) {
        //Toast.makeText(MainActivity.this, "Procurando: " + toSearch, Toast.LENGTH_SHORT).show();
        HashMap<Loja, Character> lojaSearch = findRestaurant(toSearch, lojas);
        /*if (lojaSearch.isEmpty())
            Toast.makeText(MainActivity.this, toSearch + " não foi encontrado.", Toast.LENGTH_SHORT).show();*/
        option = optionSearch;
        listaAdapterS = new listAdapter(MainActivity.this, new ArrayList<Loja>(lojaSearch.keySet()), new ArrayList<Character>(lojaSearch.values()));
        itemsAdapterS = new RestaurantAdapter(MainActivity.this, new ArrayList<Loja>(lojaSearch.keySet()), new ArrayList<Character>(lojaSearch.values()));
        if (listSelected) {
            lista.setAdapter(listaAdapterS);
            lista.setScrollingCacheEnabled(false);
        } else {
            gridView.setAdapter(itemsAdapterS);
            gridView.setScrollingCacheEnabled(false);
        }
        setButtonsToOption(option);
    }

    private HashMap<Loja, Character> findRestaurant(String toSearch, ArrayList<Loja> lojas) {
        String filename;
        String[] tagR = {"pratoDoDia", "menu", "sobremesa", "carne", "peixe", "Complementos", "vegetariano", "dieta", "opcao", "Bebidas", "notifications"};
        String[] tagC = {"bebida", "cafetaria", "iogurte", "chocolate", "pastelaria", "bolo", "doce", "salgados", "outro", "padaria", "menuPequenoAlmoçoLanche", "sandes"};
        filename = DataHolder.getInstance().getDataFilename();
        JsonDic myJson = new JsonDic(filename, MainActivity.this);
        HashMap<Loja, Character> searchLojas = new HashMap<>();
        for (int i = 0; i < lojas.size(); i++) {
            Loja loja = lojas.get(i);
            if (loja.getRestaurantName().toLowerCase().contains(toSearch.toLowerCase())) {
                if (loja.getRestaurant() != null)
                    searchLojas.put(loja, 'r');
                else
                    searchLojas.put(loja, 'c');
            }
            if ((loja.getRestaurant() != null) && !searchLojas.containsKey(loja.getRestaurantName())) {

                for (int j = 0; j < tagR.length; j++) {
                    try {
                        String pratoDoDia[] = myJson.getStringArray(loja.getRestaurantName(), tagR[j]);
                        if (pratoDoDia.length != 0) {
                            if (tagR[j].toLowerCase().contains(toSearch.toLowerCase()) && !pratoDoDia[0].isEmpty())
                                searchLojas.put(loja, 'r');
                            ArrayList<String> refeicoes = new ArrayList<>();
                            onlyMeals(pratoDoDia, refeicoes);
                            if (findMealInList(toSearch, refeicoes)) {
                                if (!searchLojas.containsKey(loja))
                                    searchLojas.put(loja, 'r');
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        Log.i("Search", "TAG:" + tagR[j] + " not find in: " + loja.getRestaurantName());
                    }
                }
            }
            if ((loja.getCafetaria() != null) && !searchLojas.containsKey(loja.getRestaurantName())) {
                for (int j = 0; j < tagC.length; j++) {
                    try {
                        String pratoDoDia[] = myJson.getDicArray(loja.getRestaurantName(), "Cafetaria", tagC[j]);
                        if (tagC[j].toLowerCase().contains(toSearch.toLowerCase()) && !pratoDoDia[0].isEmpty() && !searchLojas.containsKey(loja))
                            searchLojas.put(loja, 'c');
                        ArrayList<String> refeicoes = new ArrayList<>();
                        onlyMeals(pratoDoDia, refeicoes);
                        if (findMealInList(toSearch, refeicoes)) {
                            if (!searchLojas.containsKey(loja))
                                searchLojas.put(loja, 'c');
                            break;
                        }
                    } catch (JSONException e) {
                        Log.i("Search", "TAG:" + tagC[j] + " not find in: " + loja.getRestaurantName());
                    }
                }
            }
        }
        return searchLojas;
    }

    private boolean findMealInList(String toSearch, List<String> refeicoes) {
        toSearch = toSearch.toLowerCase();
        toSearch = Normalizer.normalize(toSearch, Normalizer.Form.NFD);
        toSearch = toSearch.replaceAll("[^\\p{ASCII}]", "");
        Iterator<String> iterator = refeicoes.iterator();
        while (iterator.hasNext()) {
            String refeicao = iterator.next().toString();
            refeicao = refeicao.toLowerCase();
            refeicao = Normalizer.normalize(refeicao, Normalizer.Form.NFD);
            refeicao = refeicao.replaceAll("[^\\p{ASCII}]", "");
            /*int compared = refeicao.compareToIgnoreCase(toSearch.toLowerCase());
            if (abs(compared) <= refeicao.length() / 3)*/
            if (refeicao.contains(toSearch))
                return true;
        }
        return false;
    }

    private void onlyMeals(String[] pratoDoDia, List<String> onlyMeals) {
        for (int i = 0; i < pratoDoDia.length; i++) {
            if (!pratoDoDia[i].contains("€")) {
                onlyMeals.add(pratoDoDia[i]);
            }
        }
    }

    private ArrayList<Loja> lojaWithTheOption(ArrayList<Loja> lojas, char option) {
        ArrayList<Loja> lojaArrayList = new ArrayList<>();

        for (int i = 0; i < lojas.size(); i++) {
            if ((option == 'c' || option == 'C') && lojas.get(i).getCafetaria() != null)
                lojaArrayList.add(lojas.get(i));
            if ((option == 'r' || option == 'R') && lojas.get(i).getRestaurant() != null)
                lojaArrayList.add(lojas.get(i));
        }
        return lojaArrayList;
    }

    private void addLojas(final ArrayList<Loja> lojas) {
        int i = 0;
        lojas.add(new Loja("Cantina", "Edifício 7", R.drawable.cantina8));
        addRestaurante(lojas.get(i), "Aberto", R.drawable.bolas_verde, false, 11.30, 14.30, 18.30, 20.30, "Preço Médio", "2.65€");

        //i++;
        //lojas.add(new Loja("Mininova", "Edifício I", R.drawable.dlidiabar));
        //addCafetaria(lojas.get(i), "Aberto", R.drawable.bolas_verde, 12, 14.30, "Preço Café", "4.50€");

        i++;
        lojas.add(new Loja("Teresa", "Hangar I", R.drawable.teresafinal12));
        addRestaurante(lojas.get(i), "Aberto", R.drawable.bolas_verde, true, 11.30, 14.30, 19, 20.45, "Preço Médio", "3.00€");
        addCafetaria(lojas.get(i), "Aberto", R.drawable.bolas_verde, 12, 14.30, "Preço Café", "0.35€");

        i++;
        lojas.add(new Loja("My Spot", "Edifício da Cantina", R.drawable.myspot5));
        addRestaurante(lojas.get(i), "Aberto", R.drawable.bolas_verde, true, 11.45, 15, -1, -1, "Preço Médio", "3.80€");
        addCafetaria(lojas.get(i), "Aberto", R.drawable.bolas_verde, 12, 14.30, "Preço Café", "0.45€");

        i++;
        lojas.add(new Loja("Bar Campus", "Edificio da Biblioteca", R.drawable.campusfinaliss));
        addRestaurante(lojas.get(i), "Aberto", R.drawable.bolas_verde, true, 12, 14.30, -1, -1, "Preço Médio", "3.5€");
        addCafetaria(lojas.get(i), "Aberto", R.drawable.bolas_verde, 12, 14.30, "Preço Café", "0.50€");

        i++;
        lojas.add(new Loja("Casa do P.", "Edifício I", R.drawable.casapfinal));
        addRestaurante(lojas.get(i), "Aberto", R.drawable.bolas_verde, false, 12, 14.30, -1, -1, "Preço Médio", "3.50€");
        addCafetaria(lojas.get(i), "Aberto", R.drawable.bolas_verde, 12, 14.30, "Preço Café", "0.50€");

        i++;
        lojas.add(new Loja("C@m. Come", "Edifício da Cantina", R.drawable.camcome));
        addRestaurante(lojas.get(i), "Aberto", R.drawable.bolas_verde, false, 12, 14.30, -1, -1, "Preço Médio", "8€");

        i++;
        lojas.add(new Loja("Sector + Dep", "Departamental", R.drawable.sector_dep));
        addRestaurante(lojas.get(i), "Aberto", R.drawable.bolas_verde, true, 11.30, 14.30, -1, -1, "Preço Médio", "6.00€");
        addCafetaria(lojas.get(i), "Aberto", R.drawable.bolas_verde, 11.30, 14.30, "Preço Café", "0.50€");

        i++;
        lojas.add(new Loja("Sector + Ed.7", "Edifício 7", R.drawable.sector_mais));
        addRestaurante(lojas.get(i), "Aberto", R.drawable.bolas_verde, false, 12, 14.30, -1, -1, "Preço Médio", "4.05€");
        addCafetaria(lojas.get(i), "Aberto", R.drawable.bolas_verde, 12, 14.30, "Preço Café", "0.50€");

        i++;
        lojas.add(new Loja("Girassol", "Edifício VIII", R.drawable.girassol));
        addRestaurante(lojas.get(i), "Aberto", R.drawable.bolas_verde, false, 11, 14, -1, -1, "Preço Médio", "4.90€");
        addCafetaria(lojas.get(i), "Aberto", R.drawable.bolas_verde, 11, 14, "Preço Café", "0.50€");

        i++;
        lojas.add(new Loja("Bar D. Lídia", "Edifício II", R.drawable.dlidiabar));
        addRestaurante(lojas.get(i), "Aberto", R.drawable.bolas_verde, false, 12, 14.30, -1, -1, "Preço Médio", "4.50€");
        addCafetaria(lojas.get(i), "Aberto", R.drawable.bolas_verde, 12, 14.30, "Preço Café", "0.50€");
    }

    private void addCafetaria(Loja loja, String openTag, int openImage, double open, double close, String mediumPrice, String priceTag) {
        loja.Cafetaria(openTag, openImage, open, close, mediumPrice, priceTag);
    }

    private void addRestaurante(Loja loja, String openTag, int openImage, Boolean Vegetariano, double lunchOpen, double lunchClose, double dinnerOpen, double dinnerClose, String mediumPrice, String priceTag) {
        loja.Restaurant(openTag, openImage, Vegetariano, lunchOpen, lunchClose, dinnerOpen, dinnerClose, mediumPrice, priceTag);
    }


    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.mymenu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.grelha:
                lista.setVisibility(View.GONE);
                listSelected = false;

                gridView.setVisibility(View.VISIBLE);
                if (option.equals(optionRefeicao))
                    gridView.setAdapter(itemsAdapter);
                if (option.equals(optionCafetaria))
                    gridView.setAdapter(itemsAdapterC);
                if (option.equals(optionSearch))
                    gridView.setAdapter(itemsAdapterS);
                gridView.setScrollingCacheEnabled(false);
                break;

            case R.id.lista:
                gridView.setVisibility(View.GONE);

                lista.setVisibility(View.VISIBLE);
                if (option.equals(optionRefeicao))
                    lista.setAdapter(listaAdapter);
                if (option.equals(optionCafetaria))
                    lista.setAdapter(listaAdapterC);
                if (option.equals(optionSearch))
                    lista.setAdapter(listaAdapterS);
                lista.setScrollingCacheEnabled(false);
                listSelected = true;
                break;

        }
        return true;
    }

    //alterar isto
    private void updateTextView(ArrayList<Loja> lojas, RestaurantAdapter itemsAdapter) {

        Date date = new Date();   // given date
        Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
        int minutes = calendar.get(Calendar.MINUTE);  // gets hour in 12h format

        double hora_actual = hour + (minutes * 0.01);

        //Para mudar a toolBar
        updateRestaurantTxt(this.lojaWithTheOption(lojas, 'r'), day, hora_actual);
        updateCafetariaTxt(this.lojaWithTheOption(lojas, 'c'), day, hora_actual);
        //Para mudar as tags de aberto ou fechado +imagem na app

        itemsAdapter.notifyDataSetChanged();
    }

    private void updateCafetariaTxt(ArrayList<Loja> lojas, int day, double hora_actual) {
        if (day == 1 || day == 7) { //Sabado ou Domingo
            for (int i = 0; i < lojas.size(); i++) {
                Cafetaria cafetaria = lojas.get(i).getCafetaria();
                cafetaria.setOpenTag("Fechado");
                cafetaria.setOpenImage(R.drawable.bolas_vermelha);

            }
        } else {
            for (int i = 0; i < lojas.size(); i++) {
                Cafetaria cafetaria = lojas.get(i).getCafetaria();
                if (cafetaria.diffTime(hora_actual)) {
                    cafetaria.setOpenTag("Aberto");
                    cafetaria.setOpenImage(R.drawable.bolas_verde);

                } else {
                    cafetaria.setOpenTag("Fechado");
                    cafetaria.setOpenImage(R.drawable.bolas_vermelha);
                }
            }
        }
    }


    private void updateRestaurantTxt(ArrayList<Loja> lojas, int day, double hora_actual) {
        if (day == 1 || day == 7) { //Sabado ou Domingo
            for (int i = 0; i < lojas.size(); i++) {
                Restaurant restaurants = lojas.get(i).getRestaurant();
                restaurants.changeOpenTag("Fechado");
                restaurants.changeOpenImage(R.drawable.bolas_vermelha);

            }
        } else {
            for (int i = 0; i < lojas.size(); i++) {
                Restaurant restaurant = lojas.get(i).getRestaurant();
                if (restaurant.diffTime(hora_actual)) {
                    restaurant.changeOpenTag("Aberto");
                    restaurant.changeOpenImage(R.drawable.bolas_verde);

                } else {
                    restaurant.changeOpenTag("Fechado");
                    restaurant.changeOpenImage(R.drawable.bolas_vermelha);
                }
            }
        }
    }


    private void setButtonsToOption(String option) {
        if (option.equals(optionRefeicao)) {
            refeicaoBtn.setBackgroundResource(R.drawable.backtextviewtransparent);
            refeicaoBtn.setEnabled(false);
            cafetariaBtn.setEnabled(true);
            cafetariaBtn.setBackgroundResource(R.drawable.backtextview);
        } else if (option.equals(optionCafetaria)) {
            cafetariaBtn.setBackgroundResource(R.drawable.backtextviewtransparent);
            cafetariaBtn.setEnabled(false);
            refeicaoBtn.setEnabled(true);
            refeicaoBtn.setBackgroundResource(R.drawable.backtextview);
        } else if (option.equals(optionSearch)) {
            cafetariaBtn.setBackgroundResource(R.drawable.backtextview);
            cafetariaBtn.setEnabled(true);
            refeicaoBtn.setEnabled(true);
            refeicaoBtn.setBackgroundResource(R.drawable.backtextview);
        }
    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {


    }

    @Override
    public void onDownMotionEvent() {

    }

    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        LinearLayout ab = (LinearLayout) findViewById(R.id.ImaButtonRL);
        if (scrollState == ScrollState.UP) {
            if (ab.getVisibility() == View.VISIBLE) {
                ab.setVisibility(View.GONE);
                //notification.restartNotification(false);
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!(ab.getVisibility() == View.VISIBLE)) {
                ab.setVisibility(View.VISIBLE);
                ab.startAnimation(anifade);
            }
            //notification.restartNotification(false);
        }
    }
    /*
    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.UP) {
            if (toolbarIsShown()) { // TODO Not implemented
                hideToolbar(); // TODO Not implemented
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (toolbarIsHidden()) { // TODO Not implemented
                showToolbar(); // TODO Not implemented
            }
        }
    }*/
    /*
    private boolean toolbarIsShown() {
        // Toolbar is 0 in Y-axis, so we can say it's shown.
        return ViewHelper.getTranslationY(mToolbar) == 0;
    }

    private boolean toolbarIsHidden() {
        // Toolbar is outside of the screen and absolute Y matches the height of it.
        // So we can say it's hidden.
        return ViewHelper.getTranslationY(mToolbar) == -mToolbar.getHeight();
    }

    private void showToolbar() {
        moveToolbar(0);
    }

    private void hideToolbar() {
        moveToolbar(-mToolbar.getHeight());
    }*/
    /*
    private void moveToolbar(float toTranslationY) {
        ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(mToolbar), toTranslationY).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                ViewHelper.setTranslationY(mToolbar, translationY);
                ViewHelper.setTranslationY((View) mScrollable, translationY);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ((View) mScrollable).getLayoutParams();
                lp.height = (int) -translationY + getScreenHeight() - lp.topMargin;
                ((View) mScrollable).requestLayout();
            }
        });
        animator.start();
    }*/



    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug", "sera que deu?");
        if(notification_activity) {
            notification.restartNotification(true);
            notification_activity=false;
        }else
            notification.restartNotification(false);
    }

    @Override
    protected void onStop() {
        String listGrid;
        if (listSelected)
            listGrid = "list";
        else
            listGrid = "grid";
        new ReadWriteFile(fileScore,getApplicationContext()).saveFile(listGrid);
        super.onStop();
    }
}
