package com.example.projectbudgetmanager.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectbudgetmanager.MainActivity;
import com.example.projectbudgetmanager.R;
import com.example.projectbudgetmanager.back_end.controller.PostTrans;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.PendingIntent.getActivity;

public class InputTransactionActivity extends AppCompatActivity {

    LinearLayout phonenumber_layer , payer_payee_layer, category_layer;
    Button btn_date, btn_phonenumber;
    TextView tv_payer_payee, tv_remind_enter_amount, tv_remind_choose_day, tv_remind_choose_phone_number;
    Spinner spn_Category;
    androidx.appcompat.widget.Toolbar input_transaction_toolbar;
    int year = 0, month = 0, dayOfMonth = 0;
    boolean isDebtTransaction = false; //For remind choose a phone number purpose.
    List<String> listCategory;
    final static String URL_POSTTRANS = "https://api-backend-nhom15.herokuapp.com/api/user/trans";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_transaction);

        //Create a toolbar to put btn close, btn delete and btn done to it
        input_transaction_toolbar = findViewById(R.id.input_transaction_toolbar);
        setSupportActionBar(input_transaction_toolbar);
        //Display back button (Back arrow  <- )
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create New Transaction");

        btn_date = (Button) findViewById(R.id.btn_date);
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(InputTransactionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int yearPicked, int monthPicker, int dayPicked) {
                                year = yearPicked;
                                month = monthPicker + 1;
                                dayOfMonth = dayPicked;
                                btn_date.setText(dayOfMonth + "/" + month + "/" + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker();
                datePickerDialog.show();
            }
        });

        spn_Category = (Spinner) findViewById(R.id.spn_Category);
        loadListCategory();
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,listCategory);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spn_Category.setAdapter(adapter);
        spn_Category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_phonenumber = (Button) findViewById(R.id.btn_phonenumber);
        btn_phonenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calContctPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                calContctPickerIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(calContctPickerIntent, 1);}
        });

        Bundle extras = getIntent().getExtras();
        String data = extras.getString("key");

        if (data.equals("Payer") || data.equals("Payee")){ // code tam
//            tv_payer_payee = (TextView) findViewById(R.id.tv_payer_payee);
//            tv_payer_payee.setText(data);
            phonenumber_layer = (LinearLayout) findViewById(R.id.phonenumber_layer);
            phonenumber_layer.setVisibility(View.GONE);
        }else{
            payer_payee_layer = (LinearLayout) findViewById(R.id.payer_payee_layer);
            payer_payee_layer.setVisibility(View.GONE);
            category_layer = (LinearLayout) findViewById(R.id.category_layer);
            category_layer.setVisibility(View.GONE);
            isDebtTransaction = true;
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK && reqCode == 1) {
            Uri contctDataVar = data.getData();

            Cursor contctCursorVar = getContentResolver().query(contctDataVar, null,
                    null, null, null);
            if (contctCursorVar.getCount() > 0) {
                while (contctCursorVar.moveToNext()) {
                    String ContctUidVar = contctCursorVar.getString(contctCursorVar.getColumnIndex(ContactsContract.Contacts._ID));

                    String ContctNamVar = contctCursorVar.getString(contctCursorVar.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    Log.i("Names", ContctNamVar
                    );

                    if (Integer.parseInt(contctCursorVar.getString(contctCursorVar.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        // Query phone here. Covered next
                        String ContctMobVar = contctCursorVar.getString(contctCursorVar.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        btn_phonenumber.setText(ContctMobVar);
                    }
                }
            }
        }
    }

    //This func create the icon done and delete on the toolbar as you can see
    //when open inputTransactionActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.input_menu, menu);
        return true;
    }

    //Handle event when clicking item on menu on toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.btn_done:
                setBtn_done();
                return true;
            case R.id.btn_delete:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Back arrow to discard input, the same to btn_close
    @Override
    public boolean onSupportNavigateUp() {
//        onBackPressed();
        finish();
        return true;
    }

    private void setBtn_done(){
        EditText edt_inputmoney = (EditText)  findViewById(R.id.edt_inputmoney);
        EditText edt_payer_payee = (EditText)  findViewById(R.id.edt_payer_payee);
        EditText edt_notes = (EditText)  findViewById(R.id.edt_notes);
        Button btn_phonenumber = (Button)  findViewById(R.id.btn_phonenumber);
        tv_remind_enter_amount = (TextView) findViewById(R.id.tv_remind_enter_amount);
        tv_remind_choose_day = (TextView) findViewById(R.id.tv_remind_choose_day);
        tv_remind_choose_phone_number = (TextView) findViewById(R.id.tv_remind_choose_phone_number);

        if(TextUtils.isEmpty(edt_inputmoney.getText())) {
            tv_remind_enter_amount.setVisibility(View.VISIBLE);
        }
        else if (btn_date.getText().equals("CHOOSE A DAY")){
            tv_remind_choose_day.setVisibility(View.VISIBLE);
        }
        else if(btn_phonenumber.getText().equals("CHOOSE CONTACT") && isDebtTransaction){
            tv_remind_choose_phone_number.setVisibility(View.VISIBLE);
        }
        else{
            Intent data = new Intent();

            data.putExtra("isEdit?", 0);

            data.putExtra("year", year);
            data.putExtra("month", month);
            data.putExtra("dayOfMonth", dayOfMonth);

            String money = edt_inputmoney.getText().toString() ;
            data.putExtra("money", money);

            String payer_payee = edt_payer_payee.getText().toString() ;
            data.putExtra("payer_payee", payer_payee);

            data.putExtra("date", btn_date.getText().toString());

            String notes = edt_notes.getText().toString() ;
            data.putExtra("notes", notes);

            String phonenumber = btn_phonenumber.getText().toString() ;
            data.putExtra("phonenumber", phonenumber);

            data.putExtra("category", spn_Category.getSelectedItem().toString());

            setResult(RESULT_OK, data);
            postTransToCloud();
            finish();
        }
    }

    //Load arraylist listCategor
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
    private void postTransToCloud(){
        final Gson gson = new Gson();
        JSONObject jsoObject=new JSONObject(); //Create json object to put data into it to send it to cloud.
        try {
            jsoObject.put("kind", "income");
            jsoObject.put("amount",69);
            jsoObject.put("note","test post trans");
            jsoObject.put("category","Fees");
            jsoObject.put("date", "12/18/2012");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String datatest=""; //Container to contain json object above
        datatest=gson.toJson(jsoObject);
//        datatest = jsoObject.toString();
        PostTrans postTrans=new PostTrans(); //Create new method
        try {
            String redatafrompost=postTrans.execute(URL_POSTTRANS,datatest).get(); //Response data check is ok or not
            Log.e("check", "postTransToCloud: "+redatafrompost);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("check: postTrans",datatest);
    }

}

