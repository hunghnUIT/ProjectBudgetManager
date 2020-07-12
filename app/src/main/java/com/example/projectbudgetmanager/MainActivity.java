package com.example.projectbudgetmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

//import com.example.projectbudgetmanager.ui.UserFunction.UserFragment;
import com.example.projectbudgetmanager.back_end.BaseUser;
import com.example.projectbudgetmanager.back_end.LoginActivity;
import com.example.projectbudgetmanager.back_end.User;
import com.example.projectbudgetmanager.back_end.controller.DangNhap;
import com.example.projectbudgetmanager.back_end.controller.GetTrans;
import com.example.projectbudgetmanager.back_end.controller.PostTrans;
import com.example.projectbudgetmanager.ui.UserFunction.UserFragment;
import com.example.projectbudgetmanager.ui.calculateIncome.CalculateIncomeFragment;
import com.example.projectbudgetmanager.ui.category.CategoryAdapter;
import com.example.projectbudgetmanager.ui.category.CategoryFragment;
import com.example.projectbudgetmanager.ui.home.HomeFragment;
import com.example.projectbudgetmanager.ui.infor.InformationFragment;
import com.example.projectbudgetmanager.ui.limit.LimitExpenseFragment;
import com.example.projectbudgetmanager.ui.objectsTransaction.ItemTransactionAdapterRV;
import com.example.projectbudgetmanager.ui.objectsTransaction.myTransaction;
import com.example.projectbudgetmanager.ui.settings.SettingsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.Response;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    public FragmentTransaction fragmentTransaction;
    private ItemTransactionAdapterRV myItemTransactionAdapterRV;
//    public CategoryAdapter categoryAdapter;


    //public CategoryAdapter categoryAdapter;
    static String URL_GETTRANS = "https://api-backend-nhom15.herokuapp.com/api/user/trans";
    //These are what received from cloud
    public ArrayList<myTransaction> listTransactionGetFromCloud;
    //These are what received from from loginActivity
    public ArrayList<String> listCategory; //Initialized already, need to add more later
    public long amoutToLimitExpense; //Maybe already initialized, need to edit later
    public boolean tryingToRefreshCategoryFragment;
    public boolean firstOpen = true;

    final static String TAG = "check";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //Compile activity_main.xml first

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar); //Create a ToolBar object
        setSupportActionBar(toolbar); //set supported actions on that ToolBar

        //Create toggle for nav drawer
        drawer = findViewById(R.id.nav_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle); //The same to set adapter on list view
        toggle.syncState();

        //Create a navigation view object.
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); //Handle event onClick

        if(tryingToRefreshCategoryFragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CategoryFragment()).commit();
        }
        //HomeFragment will be show up first when open the app if there is no fragment are opening.
         else if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_wallet); //Announce this fragment has just been checked.
            getSupportFragmentManager().beginTransaction().addToBackStack("Home");
        }
        loadListCategory();

//        categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listCategory);
//        categoryAdapter = new CategoryAdapter(this, 1, listCategory);
        initListCategory(); //Just need to call once.
        loadLimitAmout();

        //Sync data here:
        getDataFromCloud();
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    private void initListCategory() {
        if(!listCategory.contains("Food")) listCategory.add("Food");
        if(!listCategory.contains("Beverage")) listCategory.add("Beverage");
        if(!listCategory.contains("Shopping")) listCategory.add("Shopping");
        if(!listCategory.contains("Gift")) listCategory.add("Gift");
        if(!listCategory.contains("Education")) listCategory.add("Education");
        if(!listCategory.contains("Fees")) listCategory.add("Fees");
        if(!listCategory.contains("Travel")) listCategory.add("Travel");
        if(!listCategory.contains("Relationship")) listCategory.add("Relationship");
        if(!listCategory.contains("Entertainment")) listCategory.add("Entertainment");
        if(!listCategory.contains("Healthcare")) listCategory.add("Healthcare");
        if(!listCategory.contains("Investment")) listCategory.add("Investment");
        if(!listCategory.contains("Salary")) listCategory.add("Salary");
        if(!listCategory.contains("Other")) listCategory.add("Other");
        sortCategoryList(listCategory);
        saveListCategory();
    }

    public void sortCategoryList(ArrayList<String> listToSort){
        Collections.sort(listToSort, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        //categoryAdapter.notifyDataSetChanged();
    }

    //Save arraylist listCategory
    public void saveListCategory(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences list category", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();  //Gson and Json is the same to Serialize and Deserialize
        String json = gson.toJson(listCategory); //Serialize the array list
        editor.putString("Category List", json);
        editor.apply();
    }

    //Load arraylist listCategory
    public void loadListCategory(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences list category", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Category List", null);
        Type type = new TypeToken<ArrayList<String >>() {}.getType();
        listCategory = gson.fromJson(json, type);
        if (listCategory == null) {
            listCategory = new ArrayList<>();
        }
    }

    //Save limit
    public void saveLimitAmout(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences amout limit", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("Amount To Limit Expense", amoutToLimitExpense);
        editor.apply();
    }

    //Load limit
    public void loadLimitAmout(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences amout limit", MODE_PRIVATE);
        //First load data from internal storage
        amoutToLimitExpense = sharedPreferences.getLong("Amount To Limit Expense", 0);
        //Then load from loginActivity
        amoutToLimitExpense = getIntent().getLongExtra("amoutToLimitExpense",0);
    }

    //Handle events when click item navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //Just because this is dynamic fragment not static, then you should use
        //getSupportFragmentManager to do every thing such as replace, add or remove.

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //In this case, replace the fragment container in nav_drawer.xml with the chosen fragment.
        switch (menuItem.getItemId()){         //Check id to do the event.
            case R.id.nav_wallet:
                //getSupportFragmentManager begin a transaction (of android, not class myTransaction) to replace
                // the fragment_contain with HomeFragment(.java)
                fragmentTransaction.replace(R.id.fragment_container, new HomeFragment()).commit();
                fragmentTransaction.addToBackStack("Home"); //stack this state for back to the previous fragment
                break;
            case R.id.nav_info:
                fragmentTransaction.replace(R.id.fragment_container, new InformationFragment()).commit();
                fragmentTransaction.addToBackStack("Info");
                break;
            case R.id.nav_setting:
                fragmentTransaction.replace(R.id.fragment_container, new SettingsFragment()).commit();
                fragmentTransaction.addToBackStack("Setting");
                break;
            case R.id.nav_limit_expense:
                fragmentTransaction.replace(R.id.fragment_container, new LimitExpenseFragment()).commit();
                fragmentTransaction.addToBackStack("Limit");
                break;
            case R.id.nav_category:
                fragmentTransaction.replace(R.id.fragment_container, new CategoryFragment()).commit();
                fragmentTransaction.addToBackStack("Category");
                break;
            case R.id.nav_help:
                fragmentTransaction.replace(R.id.fragment_container, new HelpFragment()).commit();
                fragmentTransaction.addToBackStack("Help");
                break;
            case R.id.nav_user:
                logOut();
                break;

        }
        drawer.closeDrawer(GravityCompat.START); //Close Navigation Drawer after clicking on item.
        return true; //If false, no item will be accepted when you click on item.
    }

    //Handle event when click back button of the phone
    @Override
    public void onBackPressed() {
        //Drawer will be closed when you open it and click "Back" button.
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else{
                super.onBackPressed();
            }
    }

//    public void updateLV(){
//        categoryAdapter.notifyDataSetChanged();
//    }

    public void getDataFromCloud(){
        GetTrans getTrans=new GetTrans();
        String redata = "";

        try {
            //Execute to call the AsyncTask, get to get the values returned;
            redata = getTrans.execute(URL_GETTRANS).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Type listtype=new TypeToken<List<JSONObject>>(){}.getType();
        JSONArray list= null;
        try {
            list = new JSONArray(redata);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Get data from cloud successfully, put it into array list type myTransaction
        listTransactionGetFromCloud = new ArrayList<>();
        Log.e(TAG, "getDataFromCloud: list size: "+list.length() );
        for(int i=0;i<list.length();i++){
            try {
                JSONObject a=list.getJSONObject(i);
                //Temp function, NEED TO EDIT.
                int typeOfTrans = findType(a.getString("kind"));
                String dateReceived = a.getString("date");
                String yearReceived = dateReceived.substring(0,4);
                String monthReceived = dateReceived.substring(5,7);
                String dayReceived = dateReceived.substring(8,10);

//                myTransaction newTransaction = new myTransaction(typeOfTrans,a.getInt("amount"),a.getString("category"),Integer.parseInt(dayReceived),Integer.parseInt(monthReceived),Integer.parseInt(yearReceived));
                myTransaction newTransaction = new myTransaction(typeOfTrans,a.getInt("amount"),a.getString("category"), a.getString("_id"));
                newTransaction.setDay(Integer.parseInt(dayReceived));
                Log.e(TAG, "getDataFromCloud: day: " + dayReceived);
                newTransaction.setMonth(Integer.parseInt(monthReceived));
                Log.e(TAG, "getDataFromCloud: month: " + monthReceived);
                newTransaction.setYear(Integer.parseInt(yearReceived));
                Log.e(TAG, "getDataFromCloud: year: " + yearReceived);

                Log.e(TAG, "getDataFromCloud: done datetime");

                String note = null;
                try {
                    note = a.getString("note");
                    if(!note.isEmpty())
                        newTransaction.setNote(note);
                    Log.e(TAG, "getDataFromCloud: note: "+ note + "at " + i);
                } catch (JSONException e) {
                    note = "";
                    e.printStackTrace();
                }

                if (newTransaction.getTypeOfTransaction() == 3){
                    String phone = "";
                    try {
                        phone = a.getString("phone");
                        newTransaction.setPhoneNumber(phone);
                        Log.e(TAG, "getDataFromCloud: phone: "+ phone + "at " + i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                listTransactionGetFromCloud.add(newTransaction);
            } catch (JSONException e) {
                //Toast.makeText(this, "Can not sync data, please use SYNC button", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private int findType(String kind){
        int typeOfTrans = 0;
        switch (kind){
            case "income": typeOfTrans = 1;
                break;
            case "expense": typeOfTrans = 2;
                break;
            case "debt": typeOfTrans = 3;
                break;
        }
        return typeOfTrans;
    }

    private void logOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign out");
        builder.setMessage("Are you sure want to sign out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = getSharedPreferences("userData", MODE_PRIVATE).edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }).setNegativeButton("Cancel", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}



