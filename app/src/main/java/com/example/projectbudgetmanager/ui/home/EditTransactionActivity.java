package com.example.projectbudgetmanager.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import com.example.projectbudgetmanager.R;
import com.example.projectbudgetmanager.ui.objectsTransaction.myTransaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditTransactionActivity extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar edit_transaction_toolbar;
    boolean flagBtnBack = false;
    Button btn_edit_date, btn_edit_phonenumber;
    int year = 0, month = 0, dayOfMonth = 0;
    Spinner spn_edit_category;
    TextView tv_edit_payer_payee, tv_edit_remind_enter_amount, tv_edit_remind_choose_day;
    EditText et_edit_money, et_edit_notes, et_edit_payer;
    LinearLayout edit_phonenumber_layer , edit_payer_payee_layer, edit_category_layer;
    boolean flagDeleteConfirmed = false;
    List<String> listCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        //Create a toolbar to put btn close, btn delete and btn done to it
        edit_transaction_toolbar = findViewById(R.id.edit_transaction_toolbar);
        setSupportActionBar(edit_transaction_toolbar);
        //Display back button (Back arrow  <- )
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Transaction");

        et_edit_money= (EditText) findViewById(R.id.et_edit_money);
        et_edit_notes = (EditText) findViewById(R.id.et_edit_notes);
        et_edit_payer = (EditText) findViewById(R.id.et_edit_payer_payee);

        btn_edit_date = (Button) findViewById(R.id.btn_edit_date);
        btn_edit_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditTransactionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int yearPicked, int monthPicker, int dayPicked) {
                                year = yearPicked;
                                month = monthPicker + 1;
                                dayOfMonth = dayPicked;
                                btn_edit_date.setText(dayOfMonth + "/" + month + "/" + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker();
                datePickerDialog.show();
            }
        });

        spn_edit_category = (Spinner) findViewById(R.id.spn_edit_category);
        loadListCategory();

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,listCategory);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spn_edit_category.setAdapter(adapter);
        spn_edit_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_edit_phonenumber = (Button) findViewById(R.id.btn_edit_phonenumber);
        btn_edit_phonenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calContctPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                calContctPickerIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(calContctPickerIntent, 1);}
        });

        Bundle extras = getIntent().getExtras();
        String data = extras.getString("key");

        if (data.equals("Payer") || data.equals("Payee")){ // code tam
//            tv_edit_payer_payee = (TextView) findViewById(R.id.tv_edit_payer_payee);
//            tv_edit_payer_payee.setText(data);
            edit_phonenumber_layer = (LinearLayout) findViewById(R.id.edit_phonenumber_layer);
            edit_phonenumber_layer.setVisibility(View.GONE);
        }else{
            edit_payer_payee_layer = (LinearLayout) findViewById(R.id.edit_payer_payee_layer);
            edit_payer_payee_layer.setVisibility(View.GONE);
            edit_category_layer = (LinearLayout) findViewById(R.id.edit_category_layer);
            edit_category_layer.setVisibility(View.GONE);
        }

        myTransaction clickedTransaction = new myTransaction();
        clickedTransaction = (myTransaction) getIntent().getSerializableExtra("clickedTransaction");

        et_edit_money.setText(""+ clickedTransaction.getAmount()); //NO "" WILL LEAD TO CRASHING APP.
        year = clickedTransaction.getYear();
        month = clickedTransaction.getMonth();
        dayOfMonth = clickedTransaction.getDay();
        btn_edit_date.setText(dayOfMonth + "/" + month + "/" + year);
        //Get the position of selection of spinner.
        int spinnerSelection = adapter.getPosition(clickedTransaction.getCategory());
        spn_edit_category.setSelection(spinnerSelection);
        et_edit_payer.setText(clickedTransaction.getPayee());
        et_edit_notes.setText(clickedTransaction.getNote());
        btn_edit_phonenumber.setText(clickedTransaction.getPhoneNumber());
    }

    //Catch the number returned from activity choose number contact
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

                    Log.i("Names", ContctNamVar);

                    if (Integer.parseInt(contctCursorVar.getString(contctCursorVar.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        // Query phone here. Covered next
                        String ContctMobVar = contctCursorVar.getString(contctCursorVar.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        btn_edit_phonenumber.setText(ContctMobVar);
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
                setBtn_edit_done();
                return true;
            case R.id.btn_delete:
                confirmDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Back arrow to discard input, the same to btn_close
    @Override
    public boolean onSupportNavigateUp() {
        flagBtnBack = true;
        onBackPressed();
//        finish();
        return true;
    }

    private void confirmDelete(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you sure want to delete permanently this transaction?");
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(EditTransactionActivity.this, "NO selected", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        flagDeleteConfirmed = true;
                        setBtn_edit_done();
                    }
                });
        alertDialog.show();
    }

    private void setBtn_edit_done(){
        et_edit_money = (EditText)  findViewById(R.id.et_edit_money);
        et_edit_payer = (EditText)  findViewById(R.id.et_edit_payer_payee);
        et_edit_notes = (EditText)  findViewById(R.id.et_edit_notes);
        Button btn_edit_phonenumber = (Button)  findViewById(R.id.btn_edit_phonenumber);
        tv_edit_remind_enter_amount = (TextView) findViewById(R.id.tv_edit_remind_enter_amount);
        tv_edit_remind_choose_day = (TextView) findViewById(R.id.tv_edit_remind_choose_day);

        if(TextUtils.isEmpty(et_edit_money.getText())) {
            tv_edit_remind_enter_amount.setVisibility(View.VISIBLE);
        }
        else if (btn_edit_date.getText().equals("CHOOSE A DAY")){
            tv_edit_remind_choose_day.setVisibility(View.VISIBLE);
        }
        else{
            Intent data = new Intent();
            data.putExtra("isEdit?", 1);
            if(flagDeleteConfirmed){
                data.putExtra("deleteConfirmed", true);
            }
            else{
                data.putExtra("year", year);
                data.putExtra("month", month);
                data.putExtra("dayOfMonth", dayOfMonth);

                String money = et_edit_money.getText().toString() ;
                data.putExtra("money", money);

                String payer_payee = et_edit_payer.getText().toString() ;
                data.putExtra("payer_payee", payer_payee);

                data.putExtra("date", btn_edit_date.getText().toString());

                String notes = et_edit_notes.getText().toString() ;
                data.putExtra("notes", notes);

                String phonenumber = btn_edit_phonenumber.getText().toString() ;
                data.putExtra("phonenumber", phonenumber);

                //prevent error crash app when edit debt because of lacking spinner category of debt
                try {
                    data.putExtra("category", spn_edit_category.getSelectedItem().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            setResult(RESULT_OK, data);
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
}
