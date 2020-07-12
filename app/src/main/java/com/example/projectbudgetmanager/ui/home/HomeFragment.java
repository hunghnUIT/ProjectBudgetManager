package com.example.projectbudgetmanager.ui.home;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.aigestudio.wheelpicker.widgets.WheelMonthPicker;
import com.example.projectbudgetmanager.GroupTransaction;
import com.example.projectbudgetmanager.MainActivity;
import com.example.projectbudgetmanager.back_end.RetrofitClient;
import com.example.projectbudgetmanager.back_end.controller.DeleteTrans;
import com.example.projectbudgetmanager.back_end.controller.EditTrans;
import com.example.projectbudgetmanager.back_end.controller.PostTrans;
import com.example.projectbudgetmanager.ui.overviewTransaction.OverViewListTransactionActivity;
import com.example.projectbudgetmanager.R;
import com.example.projectbudgetmanager.ui.objectsTransaction.myTransaction;
import com.example.projectbudgetmanager.ui.objectsTransaction.ItemTransactionAdapterRV;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
//import static com.example.projectbudgetmanager.MainActivity.listMyTransaction;

public class HomeFragment extends Fragment implements ItemTransactionAdapterRV.OnItemTransactionListener, View.OnClickListener {
    private static final String CHANNEL_ID = "my_channel_1";
    private HomeViewModel mViewModel;
    private RecyclerView rv_transaction;
    private ItemTransactionAdapterRV myItemTransactionAdapterRV;
    private ArrayList<myTransaction> listMyTransaction;
    private List<GroupTransaction> listGroupTransaction;
    private TextView tvIncome, tvExpense, tvBalance;
    private myTransaction clickedTransaction;
    private int tempAmountOfClickedTransaction = 0;
    private int clickedTransactionPosition = 0;
    private int[][] sumExpenseTable; //Sum expense by day to table int[31][12]
    private RelativeLayout rl_balance_layout;
    private FloatingActionsMenu floatingActionsMenu;
    //Temp
    private String money, payer_payee, date, notes = "", category, phonenumber = "";
    int income = 0, expense = 0, balance = 0;
    private int year = 0, month = 0, dayOfMonth = 0;
    int monthSelected = 0, yearSelected = 0;
    List<String> monthsList;
    List<String> yearsList;
    WheelPicker wheelPickerMonth, wheelPickerYear;
    Dialog dialogPickMonth;
    Button btn_ok_pick_month, btn_cancel_pick_month;
    final static String TAG = "check";
    View view;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//Make sure you have this line of code.
        listMyTransaction = ((MainActivity) getActivity()).listTransactionGetFromCloud;
        Log.e(TAG, "onCreate: ");
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.e(TAG, "onCreateView: ");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        rv_transaction = (RecyclerView) getView().findViewById(R.id.rv_transaction);
        tvIncome = (TextView) getView().findViewById(R.id.tv_income);
        tvExpense = (TextView) getView().findViewById(R.id.tv_expense);
        tvBalance = (TextView) getView().findViewById(R.id.tv_balance);

        loadListMyTransaction();
        loadExpenseTable();

        myItemTransactionAdapterRV = new ItemTransactionAdapterRV(getContext(), listMyTransaction, this);
        rv_transaction.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_transaction.setAdapter(myItemTransactionAdapterRV);

        //We call findByViewID() in this func because of compiler has just finished read fragment_home.xml
        //in onCreateView() func above.
        //This func mean activity and fragment created successfully

        //Show dialog choose the type of transaction
        floatingActionsMenu = (FloatingActionsMenu) getView().findViewById(R.id.float_menu);

        com.getbase.floatingactionbutton.FloatingActionButton fab_add_income = (com.getbase.floatingactionbutton.FloatingActionButton) getView().findViewById(R.id.fab_add_income); //No difference between getView, getActivity?
        com.getbase.floatingactionbutton.FloatingActionButton fab_add_expense = (com.getbase.floatingactionbutton.FloatingActionButton) getActivity().findViewById(R.id.fab_add_expense);
        com.getbase.floatingactionbutton.FloatingActionButton fab_add_debt = (com.getbase.floatingactionbutton.FloatingActionButton) getActivity().findViewById(R.id.fab_add_debt);
        fab_add_income.setOnClickListener(this);
        fab_add_expense.setOnClickListener(this);
        fab_add_debt.setOnClickListener(this);

        syncDataFromCloud();

        Log.e(TAG, "onActivityCreated: size listMyTrans" +listMyTransaction.size());
        // TODO: Use the ViewModel
    }


    @Override
    public void onResume() {
        super.onResume();
        if (((MainActivity) getActivity()).firstOpen) {
            ((MainActivity) getActivity()).firstOpen = false;
            reload();
        }
        Log.e("check", "onResume: ");
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add_income:
                floatingActionsMenu.collapse();
                Intent income = new Intent(getContext(), InputTransactionActivity.class);
                income.putExtra("key", "Payer"); // value cho income
                startActivityForResult(income, 1);
                break;
            case R.id.fab_add_expense:
                floatingActionsMenu.collapse();
                Intent expense = new Intent(getContext(), InputTransactionActivity.class);
                expense.putExtra("key", "Payee"); // value cho expense
                startActivityForResult(expense, 2);
                break;
            case R.id.fab_add_debt:
                floatingActionsMenu.collapse();
                Intent debt = new Intent(getContext(), InputTransactionActivity.class);
                debt.putExtra("key", "Debt");
                startActivityForResult(debt, 3);
                break;
            case R.id.btn_ok_pick_month:
                monthSelected = wheelPickerMonth.getCurrentItemPosition();
                switch (wheelPickerYear.getCurrentItemPosition()){
                    case 0: yearSelected = 0;
                        break;
                    case 1: yearSelected = 2018;
                        break;
                    case 2: yearSelected = 2019;
                        break;
                    case 3: yearSelected = 2020;
                        break;
                }
                filterListTransactionByDate(monthSelected, yearSelected);
                dialogPickMonth.dismiss();
                break;
            case R.id.btn_cancel_pick_month:
                dialogPickMonth.dismiss();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (data.getExtras().getInt("isEdit?")) {
                case 0:
                    createNewTransaction(requestCode, data);
                    break;
                case 1:
                    editTransaction(requestCode, data);
                    break;
            }
            sortTransactionListByDateTime(listMyTransaction);
            saveListMyTransaction();
        }
    }

    //Handle event create new item transaction
    private void createNewTransaction(int requestCode, Intent data) {
        myTransaction newTransaction = new myTransaction();
        newTransaction.setTypeOfTransaction(requestCode);
        switch (requestCode) {
            case 1:
            case 2:
                payer_payee = data.getExtras().getString("payer_payee");
                newTransaction.setPayee(payer_payee);
                category = data.getExtras().getString("category");
                newTransaction.setCategory(category);
                break;
            case 3:
                //date = data.getExtras().getString("date");
                newTransaction.setCategory("Debt");
                phonenumber = data.getExtras().getString("phonenumber");
                newTransaction.setPhoneNumber(phonenumber);
                break;
        }
        money = data.getExtras().getString("money");
        newTransaction.setAmount(Integer.parseInt(money));
        notes = data.getExtras().getString("notes");
        newTransaction.setNote(notes);
        year = data.getExtras().getInt("year");
        newTransaction.setYear(year);
        month = data.getExtras().getInt("month");
        newTransaction.setMonth(month);
        dayOfMonth = data.getExtras().getInt("dayOfMonth");
        newTransaction.setDay(dayOfMonth);
        date = data.getExtras().getString("date");


        if (newTransaction.getAmount() != 0) {
            calculateBalance(newTransaction, false, false);
            listMyTransaction.add(newTransaction);
            myItemTransactionAdapterRV.notifyDataSetChanged();
            switch (requestCode){
                case 1: postTransToCloud("income",Integer.parseInt(money),category,date,phonenumber,notes);
                    break;
                case 2: postTransToCloud("expense",Integer.parseInt(money),category,date,phonenumber,notes);
                    break;
                case 3: postTransToCloud("debt",Integer.parseInt(money),category,date,phonenumber,notes);
                    break;
            }

            Toast.makeText(getContext(), "Successful", Toast.LENGTH_LONG).show();
            if (requestCode == 2 || requestCode == 3) {
                //Check only with expense and debt.
                checkMoreThanLimit(((MainActivity) getActivity()).amoutToLimitExpense, newTransaction);
            }
        }
    }

    //Handle event edit item transaction
    private void editTransaction(int requestCode, Intent data) {
        boolean deleteConfirmed = data.getBooleanExtra("deleteConfirmed", false);
        if (deleteConfirmed) {
            deleteTransactionAt(clickedTransactionPosition);
            deleteTransOnCloud(clickedTransaction.getId());
            calculateBalance(clickedTransaction, false, true);
            myItemTransactionAdapterRV.notifyDataSetChanged();
            Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
        } else { //Edit item
            switch (requestCode) {
                case 1:
                case 2: //Income or expenses
                    payer_payee = data.getExtras().getString("payer_payee");
                    clickedTransaction.setPayee(payer_payee);
                    category = data.getExtras().getString("category");
                    clickedTransaction.setCategory(category);
                    break;
                case 3: //Debt
                    clickedTransaction.setCategory("Debt");
                    phonenumber = data.getExtras().getString("phonenumber");
                    clickedTransaction.setPhoneNumber(phonenumber);
                    break;
            }
            money = data.getExtras().getString("money");
            clickedTransaction.setAmount(Integer.parseInt(money));
            notes = data.getExtras().getString("notes");
            clickedTransaction.setNote(notes);
            year = data.getExtras().getInt("year");
            clickedTransaction.setYear(year);
            month = data.getExtras().getInt("month");
            clickedTransaction.setMonth(month);
            dayOfMonth = data.getExtras().getInt("dayOfMonth");
            clickedTransaction.setDay(dayOfMonth);

            if (clickedTransaction.getAmount() != 0) {
                calculateBalance(clickedTransaction, true, false);
                myItemTransactionAdapterRV.notifyDataSetChanged();
                Toast.makeText(getContext(), "Edited", Toast.LENGTH_SHORT).show();
                if (requestCode == 2 || requestCode == 3) {
                    //Check only with expense and debt.
                    checkMoreThanLimit(((MainActivity) getActivity()).amoutToLimitExpense, clickedTransaction);
                }
                //editTransOnCloud(clickedTransaction.getId(), clickedTransaction);
            }
        }

    }

    //Handle event for click item of RecyclerView: edit.
    @Override
    public void onItemClick(int position) {
        //Get the clicked transaction
        clickedTransactionPosition = position; //Get position in case of deleting
        clickedTransaction = listMyTransaction.get(position);
        tempAmountOfClickedTransaction = clickedTransaction.getAmount();
        //Toast.makeText(getContext(), "Month: " + clickedTransaction.getMonth(), Toast.LENGTH_LONG).show();

        Intent edit = new Intent(getContext(), EditTransactionActivity.class);
        edit.putExtra("clickedTransaction", clickedTransaction);

        switch (clickedTransaction.getTypeOfTransaction()) {
            case 1:
                edit.putExtra("key", "Payer"); // value cho income
                startActivityForResult(edit, 1);
                break;
            case 2:
                edit.putExtra("key", "Payee"); // value cho expense
                startActivityForResult(edit, 2);
                break;
            case 3:
                edit.putExtra("key", "Debt");
                startActivityForResult(edit, 3);
                break;
        }
    }

    //Handle on item long click of RecyclerView.: delete.
    @Override
    public void onItemLongClick(int position) {
        final int pos = position;
        clickedTransaction = listMyTransaction.get(position);
        tempAmountOfClickedTransaction = clickedTransaction.getAmount();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you sure want to delete permanently this transaction?");
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        calculateBalance(clickedTransaction, false, true);
                        deleteTransOnCloud(clickedTransaction.getId());
                        deleteTransactionAt(pos);
                    }
                });
        alertDialog.show();
    }

    //Calculate the balance
    private void calculateBalance(myTransaction newTransaction, boolean isEdit, boolean deleteConfirmed) {
        income = Integer.parseInt(tvIncome.getText().toString());
        expense = Integer.parseInt(tvExpense.getText().toString());
        switch (newTransaction.getTypeOfTransaction()) {
            case 1:
                if (deleteConfirmed) {
                    income = income - tempAmountOfClickedTransaction; //Minus the old amount
                    tvIncome.setText("" + income);
                    break;
                } else if (isEdit) {
                    income = income - tempAmountOfClickedTransaction;
                }//Minus the old amount
                income = income + newTransaction.getAmount();
                tvIncome.setText("" + income); //This is the way to set int to a textview
                break;
            case 2:
            case 3:
                if (deleteConfirmed) {
                    expense = expense - tempAmountOfClickedTransaction; //Minus the old amount
                    sumExpenseTable[newTransaction.getMonth() - 1][newTransaction.getDay() - 1] = sumExpenseTable[newTransaction.getMonth() - 1][newTransaction.getDay() - 1] - tempAmountOfClickedTransaction;
                    tvExpense.setText("" + expense);
                    break;
                } else if (isEdit) { //Minus the old amount
                    expense = expense - tempAmountOfClickedTransaction;
                    sumExpenseTable[newTransaction.getMonth() - 1][newTransaction.getDay() - 1] = sumExpenseTable[newTransaction.getMonth() - 1][newTransaction.getDay() - 1] - tempAmountOfClickedTransaction;
                }
                expense = expense + newTransaction.getAmount();
                sumExpenseTable[newTransaction.getMonth() - 1][newTransaction.getDay() - 1] = sumExpenseTable[newTransaction.getMonth() - 1][newTransaction.getDay() - 1] + newTransaction.getAmount();
                tvExpense.setText("" + expense);
                break;
        }
        balance = income - expense;
        tvBalance.setText("" + balance);
        saveSumExpenseTable();
    }

    //Delete the item a at position
    private void deleteTransactionAt(int position) {
        listMyTransaction.remove(position);
        myItemTransactionAdapterRV.notifyDataSetChanged();
        //Update listMytransaction
        saveListMyTransaction();
        Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
    }

    //Sort the item list by date time order
    private void sortTransactionListByDateTime(ArrayList listToSort) {
        Collections.sort(listToSort, new Comparator<myTransaction>() {
            @Override
            public int compare(myTransaction o1, myTransaction o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
                String stringDate1 = o1.getDay() + "/" + o1.getMonth() + "/" + o1.getYear();
                String stringDate2 = o2.getDay() + "/" + o2.getMonth() + "/" + o2.getYear();
                try {
                    Date date1 = sdf.parse(stringDate1);
                    Date date2 = sdf.parse(stringDate2);
                    return date2.compareTo(date1);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Catch Exception!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                myItemTransactionAdapterRV.notifyDataSetChanged();
                return -1; //Return -1 to meet the require return int of compare function
                //In fact, no chance to return -1, if there is, it's system error
            }
        });

    }

    private void checkMoreThanLimit(long amountLimit, myTransaction transaction) {
        int day = 0, month = 0;
        day = transaction.getDay();
        month = transaction.getMonth();
        if (sumExpenseTable[month - 1][day - 1] > amountLimit && amountLimit > 0) {
            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                CharSequence name = "my_channel";
                String Description = "This is my channel";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setDescription(Description);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mChannel.setShowBadge(false);
                notificationManager.createNotificationChannel(mChannel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_warning_notification)
                    .setContentTitle("Exceeding Limit")
                    .setContentText("You have spended more than " + ((MainActivity) getActivity()).amoutToLimitExpense + " đ in today.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setWhen(System.currentTimeMillis());

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(1, builder.build());
        }
    }

    private void saveListMyTransaction() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();  //Gson and Json is the same to Serialize and Deserialize
        String json = gson.toJson(listMyTransaction); //Serialize the array list
        editor.putString("Transaction List", json);
        editor.putInt("income", income);
        editor.putInt("expense", expense);
        editor.putInt("balance", balance);
        editor.apply();
    }

    private void loadListMyTransaction() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Transaction List", null);
        Type type = new TypeToken<ArrayList<myTransaction>>() {
        }.getType();
        listMyTransaction = gson.fromJson(json, type);
        if (listMyTransaction == null) {
            listMyTransaction = new ArrayList<>();
        }
        income = sharedPreferences.getInt("income", 0);
        expense = sharedPreferences.getInt("expense", 0);
        balance = sharedPreferences.getInt("balance", 0);
        tvIncome.setText("" + income);
        tvExpense.setText("" + expense);
        tvBalance.setText("" + balance);

    }

    //Save table sumExpenseTable
    public void saveSumExpenseTable() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences sum expense table", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();  //Gson and Json is the same to Serialize and Deserialize
        String json = gson.toJson(sumExpenseTable); //Serialize the table
        editor.putString("Expense Table", json);
        editor.apply();
    }

    //Load table sumExpenseTable
    public void loadExpenseTable() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences sum expense table", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Expense Table", null);
        Type type = new TypeToken<int[][]>() {
        }.getType();
        sumExpenseTable = gson.fromJson(json, type);
        if (sumExpenseTable == null) {
            sumExpenseTable = new int[12][31];
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.extend_function_home_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.btn_search_note);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                myItemTransactionAdapterRV.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myItemTransactionAdapterRV.getFilter().filter(newText);
                return true;
            }
        });
    }

    //Handle event when clicking item on menu on toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.btn_sync_menu:
                Toast.makeText(getContext(), "Sync ", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).getDataFromCloud();
                syncDataFromCloud();
                myItemTransactionAdapterRV.notifyDataSetChanged();
                reload();
                return true;
            case R.id.btn_reload_home:
                reload();
                return true;
            case R.id.btn_view_by_date:
                showDialogPickMonth();
                return true;
            case R.id.btn_overview_transaction:
                Intent intent = new Intent(getContext(), OverViewListTransactionActivity.class);
                intent.putExtra("listTransaction", (Serializable) listMyTransaction);
                startActivity(intent);
                return true;
//            case R.id.btn_test:
//                //postTransToCloud();
//                postTransUseRetrofit();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialogPickMonth() {
        dialogPickMonth = new Dialog(getContext());
        dialogPickMonth.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View mView = getLayoutInflater().inflate(R.layout.pick_month_dialog, null);
        dialogPickMonth.setContentView(mView);
        wheelPickerMonth = (WheelPicker) mView.findViewById(R.id.wheel_month_picker);
        wheelPickerYear = (WheelPicker) mView.findViewById(R.id.wheel_year_picker);
        btn_ok_pick_month = (Button) mView.findViewById(R.id.btn_ok_pick_month);
        btn_cancel_pick_month = (Button) mView.findViewById(R.id.btn_cancel_pick_month);
        btn_ok_pick_month.setOnClickListener(this);
        btn_cancel_pick_month.setOnClickListener(this);
        createWheelPicker();
        dialogPickMonth.show();
    }

    private void createWheelPicker() {
        monthsList = new ArrayList<>();
        monthsList.add("Show all");
        monthsList.add("January");
        monthsList.add("February");
        monthsList.add("March");
        monthsList.add("April");
        monthsList.add("May");
        monthsList.add("June");
        monthsList.add("July");
        monthsList.add("August");
        monthsList.add("September");
        monthsList.add("October");
        monthsList.add("November");
        monthsList.add("December");
        wheelPickerMonth.setData(monthsList);

        yearsList = new ArrayList<>();
        yearsList.add("Show all");
        yearsList.add("2018");
        yearsList.add("2019");
        yearsList.add("2020");
        wheelPickerYear.setData(yearsList);
    }

    private void filterListTransactionByDate(int monthSelected, int yearSelected) {
        ArrayList<myTransaction> filteredListByDate = new ArrayList<>();

        //case 1: all - year
        if (monthSelected == 0 && yearSelected != 0) {
            for (myTransaction item : listMyTransaction) {
                if (item.getYear() == yearSelected)
                    filteredListByDate.add(item);
            }
        }
        //case 2: month - all
        else if (monthSelected != 0 && yearSelected == 0) {
            for (myTransaction item : listMyTransaction) {
                if (item.getMonth() == monthSelected)
                    filteredListByDate.add(item);
            }
        }
        //case 3: month - year
        else if (monthSelected != 0 && yearSelected != 0) {
            for (myTransaction item : listMyTransaction) {
                if (item.getYear() == yearSelected && item.getYear() == yearSelected)
                    filteredListByDate.add(item);
            }
        }
        // case 4: all - all
        else if (monthSelected == 0 && yearSelected == 0) {
            for (myTransaction item : listMyTransaction) {
                filteredListByDate = listMyTransaction;
            }
        }
        Log.e(TAG, "filterListTransactionByDate: month: " + monthSelected + " year: " +yearSelected);
        myItemTransactionAdapterRV.filterListByDate(filteredListByDate);
    }

    private void syncDataFromCloud() {
        income = 0;
        expense = 0;
        balance = 0;
        //get listTransaction
        sortTransactionListByDateTime(((MainActivity) getActivity()).listTransactionGetFromCloud);
        listMyTransaction = ((MainActivity) getActivity()).listTransactionGetFromCloud;
        myItemTransactionAdapterRV.notifyDataSetChanged();
        for (myTransaction item : listMyTransaction) {
            if (item.getTypeOfTransaction() == 1) {
                income += item.getAmount();
            } else {
                expense += item.getAmount();
            }
        }
        balance = income - expense;
        tvIncome.setText("" + income);
        tvExpense.setText("" + expense);
        tvBalance.setText("" + balance);
        saveListMyTransaction();
    }

    private void deleteTransOnCloud(String id) {
        final Gson gson = new Gson();
        JSONObject jsonObject = new JSONObject(); //Create json object to put data into it to send it to cloud.
        try {
            jsonObject.put("_id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String datatest = ""; //Container to contain json object above
        datatest = gson.toJson(jsonObject);
        DeleteTrans deleteTrans = new DeleteTrans();
        try {
            String redatafrompost = deleteTrans.execute("https://api-backend-nhom15.herokuapp.com/api/user/trans/" + id, datatest).get(); //Response data check is ok or not
            Log.e("check", "Delete TransToCloud: " + redatafrompost);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ((MainActivity) getActivity()).getDataFromCloud();
        syncDataFromCloud();
    }

    private void editTransOnCloud(String id, myTransaction transaction){
        final Gson gson = new Gson();
        JSONObject jsonObject = new JSONObject(); //Create json object to put data into it to send it to cloud.
        try {
            switch (transaction.getTypeOfTransaction()){
                case 1:
                    jsonObject.put("kind", "income");
                    break;
                case 2: jsonObject.put("kind", "expense");
                    break;
                case 3: jsonObject.put("kind", "debt");
                        jsonObject.put("phone", transaction.getPhoneNumber());
                    break;
            }
            jsonObject.put("amount", transaction.getAmount());
            jsonObject.put("note", transaction.getNote());
            jsonObject.put("category", transaction.getCategory());
            //jsonObject.put("date", "12/18/2012");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String datatest = ""; //Container to contain json object above
        datatest = gson.toJson(jsonObject);
        EditTrans editTrans = new EditTrans();
        try {
            String redatafrompost = editTrans.execute("https://api-backend-nhom15.herokuapp.com/api/user/trans/" + id, datatest).get(); //Response data check is ok or not
            Log.e("check", "Edit TransToCloud: " + redatafrompost);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void postTransToCloud(String kind, int amount, String category, String date, String phone, String note) {
        final Gson gson = new Gson();
        JSONObject jsoObject = new JSONObject(); //Create json object to put data into it to send it to cloud.
        try {
            jsoObject.put("kind", "income");
            jsoObject.put("amount", 69);
            jsoObject.put("note", "test post trans");
            jsoObject.put("category", "Fees");
            jsoObject.put("date", "12/18/2012");
            jsoObject.put("phone", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String datatest = ""; //Container to contain json object above
        datatest = gson.toJson(jsoObject);
        PostTrans postTrans = new PostTrans(); //Create new method
        try {
            String redatafrompost = postTrans.execute("https://api-backend-nhom15.herokuapp.com/api/user/trans/", datatest).get(); //Response data check is ok or not
            Log.e("check", "postTransToCloud: redata" + redatafrompost);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("check: postTrans", datatest);
    }

    private void postTransUseRetrofit(){
        retrofit2.Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .postTrans("income",68,"Fees", "Note");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void reload() {
        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof HomeFragment) {
            FragmentTransaction fragTransaction = (getActivity()).getSupportFragmentManager().beginTransaction();
            fragTransaction.detach(currentFragment);
            fragTransaction.attach(currentFragment);
            fragTransaction.commit();
        }
    }
}
