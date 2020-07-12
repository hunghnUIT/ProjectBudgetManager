package com.example.projectbudgetmanager.ui.overviewTransaction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.aigestudio.wheelpicker.widgets.WheelDatePicker;
import com.aigestudio.wheelpicker.widgets.WheelMonthPicker;
import com.example.projectbudgetmanager.R;
import com.example.projectbudgetmanager.ui.objectsTransaction.myTransaction;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OverViewListTransactionActivity extends AppCompatActivity implements View.OnClickListener {

    androidx.appcompat.widget.Toolbar overview_transaction_toolbar;
    TextView tv_overview_total_expense, tv_overview_item_food, tv_overview_item_beverage, tv_overview_item_fees, tv_overview_total_income;
    TextView tv_overview_item_shopping, tv_overview_item_gift, tv_overview_item_healthcare, tv_overview_item_relationship, tv_overview_item_debt;
    TextView tv_overview_item_investment, tv_overview_item_education, tv_overview_item_entertainment, tv_overview_item_other_expense;
    TextView tv_overview_item_salary, tv_overview_item_other_income;

    LinearLayout ll_overview_item_food, ll_overview_item_beverage, ll_overview_item_fees, ll_overview_item_debt;
    LinearLayout ll_overview_item_shopping, ll_overview_item_gift, ll_overview_item_healthcare, ll_overview_item_relationship;
    LinearLayout ll_overview_item_investment, ll_overview_item_education, ll_overview_item_entertainment, ll_overview_item_other_expense;
    LinearLayout ll_overview_item_salary, ll_overview_item_other_income;

    List<myTransaction> tempList;

    int total_expense = 0, other_expense = 0, total_income = 0, other_income = 0;
    int food = 0, beverage = 0, fees = 0, shopping = 0, gift = 0, healthcare = 0, relationship = 0, investment = 0;
    int education = 0, debt = 0, entertainment = 0, salary = 0;
    PieChart overviewExpenseChart, overviewIncomeChart;
    ArrayList<PieEntry> valueExpenseOfChart = null, valueIncomeOfChart = null;

    int monthSelected = 0, yearSelected = 0;
    List<String> monthsList;
    List<String> yearsList;
    WheelPicker wheelPickerMonth, wheelPickerYear;
    Dialog dialogPickMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over_view_list_transaction);

        overview_transaction_toolbar = (Toolbar) findViewById(R.id.overview_transaction_toolbar);
        setSupportActionBar(overview_transaction_toolbar);
        //Display back button (Back arrow  <- )
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Over View List Transaction");

        overviewExpenseChart = (PieChart) findViewById(R.id.chart_overview_expense);
        overviewIncomeChart = (PieChart) findViewById(R.id.chart_overview_income);

        tv_overview_total_expense = (TextView) findViewById(R.id.tv_overview_total_expense);
        tv_overview_total_income = (TextView) findViewById(R.id.tv_overview_total_income);
        tv_overview_item_food = (TextView) findViewById(R.id.tv_overview_item_food);
        tv_overview_item_beverage = (TextView) findViewById(R.id.tv_overview_item_beverage);
        tv_overview_item_fees = (TextView) findViewById(R.id.tv_overview_item_fees);
        tv_overview_item_education = (TextView) findViewById(R.id.tv_overview_item_education);
        tv_overview_item_entertainment = (TextView) findViewById(R.id.tv_overview_item_entertainment);
        tv_overview_item_gift = (TextView) findViewById(R.id.tv_overview_item_gift);
        tv_overview_item_healthcare = (TextView) findViewById(R.id.tv_overview_item_healthcare);
        tv_overview_item_investment = (TextView) findViewById(R.id.tv_overview_item_investment);
        tv_overview_item_relationship = (TextView) findViewById(R.id.tv_overview_item_relationship);
        tv_overview_item_shopping = (TextView) findViewById(R.id.tv_overview_item_shopping);
        tv_overview_item_debt = (TextView) findViewById(R.id.tv_overview_item_debt);
        tv_overview_item_other_expense = (TextView) findViewById(R.id.tv_overview_item_other_expense);
        tv_overview_item_salary = (TextView) findViewById(R.id.tv_overview_item_salary);
        tv_overview_item_other_income = (TextView) findViewById(R.id.tv_overview_item_other_income);

        ll_overview_item_food = findViewById(R.id.ll_overview_food);
        ll_overview_item_beverage = findViewById(R.id.ll_overview_beverage);
        ll_overview_item_fees = findViewById(R.id.ll_overview_fees);
        ll_overview_item_education = findViewById(R.id.ll_overview_education);
        ll_overview_item_entertainment = findViewById(R.id.ll_overview_entertainment);
        ll_overview_item_gift = findViewById(R.id.ll_overview_gift);
        ll_overview_item_healthcare = findViewById(R.id.ll_overview_healthcare);
        ll_overview_item_investment = findViewById(R.id.ll_overview_investment);
        ll_overview_item_relationship = findViewById(R.id.ll_overview_relationship);
        ll_overview_item_shopping = findViewById(R.id.ll_overview_shopping);
        ll_overview_item_debt = findViewById(R.id.ll_overview_debt);
        ll_overview_item_other_expense = findViewById(R.id.ll_overview_other_expense);
        ll_overview_item_other_income = findViewById(R.id.ll_overview_other_income);
        ll_overview_item_salary = findViewById(R.id.ll_overview_salary);

        tempList = new ArrayList<>();
        tempList = (ArrayList<myTransaction>) getIntent().getSerializableExtra("listTransaction");
        overView(tempList);
        createExpensePieChart();
        initDataOfExpenseChart();
        createIncomePieChart();
        initDataOfIncomeChart();
        setTextView();
        hideUselessTextView();
    }

    //Back arrow to discard input, the same to btn_close
    @Override
    public boolean onSupportNavigateUp() {
//        onBackPressed();
        finish();
        return true;
    }

    //Create the shape of expense pie chart:
    private void createExpensePieChart() {
        overviewExpenseChart.setUsePercentValues(true);
        overviewExpenseChart.setCenterText("Over View Expense");
        overviewExpenseChart.setExtraOffsets(5, 10, 5, 5);
        overviewExpenseChart.setDragDecelerationFrictionCoef(0.95f);
        overviewExpenseChart.setDrawHoleEnabled(true); //Hole inside chart
        overviewExpenseChart.setHoleColor(Color.WHITE); //Hole is white
        overviewExpenseChart.setTransparentCircleRadius(61f);
        overviewExpenseChart.animateY(1500, Easing.EaseInBack);
    }

    //Create the shape of expense pie chart:
    private void createIncomePieChart() {
        overviewIncomeChart.setUsePercentValues(true);
        overviewIncomeChart.setCenterText("Over View Income");
        overviewIncomeChart.setExtraOffsets(5, 10, 5, 5);
        overviewIncomeChart.setDragDecelerationFrictionCoef(0.95f);
        overviewIncomeChart.setDrawHoleEnabled(true); //Hole inside chart
        overviewIncomeChart.setHoleColor(Color.WHITE); //Hole is white
        overviewIncomeChart.setTransparentCircleRadius(61f);
        overviewIncomeChart.animateY(1500, Easing.EaseInBack);
    }

    //Init the expense pieChart
    private void initDataOfExpenseChart() {
        valueExpenseOfChart = new ArrayList<PieEntry>();
        if (food != 0)
            valueExpenseOfChart.add(new PieEntry(((float) food / total_expense), "Food"));
        if (beverage != 0)
            valueExpenseOfChart.add(new PieEntry(((float) beverage / total_expense), "Beverage"));
        if (fees != 0)
            valueExpenseOfChart.add(new PieEntry(((float) fees / total_expense), "Fees"));
        if (shopping != 0)
            valueExpenseOfChart.add(new PieEntry(((float) shopping / total_expense), "Shopping"));
        if (gift != 0)
            valueExpenseOfChart.add(new PieEntry(((float) gift / total_expense), "Gift"));
        if (healthcare != 0)
            valueExpenseOfChart.add(new PieEntry(((float) healthcare / total_expense), "Health Care"));
        if (relationship != 0)
            valueExpenseOfChart.add(new PieEntry(((float) relationship / total_expense), "Relationship"));
        if (entertainment != 0)
            valueExpenseOfChart.add(new PieEntry(((float) entertainment / total_expense), "Entertainment"));
        if (education != 0)
            valueExpenseOfChart.add(new PieEntry(((float) education / total_expense), "Education"));
        if (investment != 0)
            valueExpenseOfChart.add(new PieEntry(((float) investment / total_expense), "Investment"));
        if (debt != 0)
            valueExpenseOfChart.add(new PieEntry(((float) debt / total_expense), "Debt"));
        if (other_expense != 0)
            valueExpenseOfChart.add(new PieEntry(((float) other_expense / total_expense), "Other"));

        PieDataSet pieDataSet = new PieDataSet(valueExpenseOfChart, "Category");
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(Color.BLACK);

        //Create color for each piece
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        //Create pie data object
        PieData pieData = new PieData(pieDataSet);
        overviewExpenseChart.setData(pieData);
    }

    //Init the income pieChart
    private void initDataOfIncomeChart() {
        valueIncomeOfChart = new ArrayList<PieEntry>();
        if (salary != 0)
            valueIncomeOfChart.add(new PieEntry(((float) salary / total_income), "Salary"));
        if (other_income != 0)
            valueIncomeOfChart.add(new PieEntry(((float) other_income / total_income), "Other"));

        PieDataSet pieDataSet = new PieDataSet(valueIncomeOfChart, "Category");
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setValueTextSize(12f);

        //Create color for each piece
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        //Create pie data object
        PieData pieData = new PieData(pieDataSet);
        overviewIncomeChart.setData(pieData);
    }

    //Classify to handle
    private void classifyToHandle(int type, myTransaction transaction){
        switch (type) {
            case 1:
                handleOverViewIncome(transaction);
                break;
            case 2:
                handleOverViewExpense(transaction);
                break;
            case 3:
                handleOverViewDebt(transaction);
                break;
        }
    }

    //Handle event overview
    private void overView(List<myTransaction> list) {
        for (int i = 0; i < list.size(); i++) {
            classifyToHandle(list.get(i).getTypeOfTransaction(), list.get(i));
//            switch (list.get(i).getTypeOfTransaction()) {
//                case 1:
//                    handleOverViewIncome(list.get(i));
//                    break;
//                case 2:
//                    handleOverViewExpense(list.get(i));
//                    break;
//                case 3:
//                    handleOverViewDebt(list.get(i));
//                    break;
//            }
        }
    }

    private void handleOverViewIncome(myTransaction overviewingTransaction) {
        if (overviewingTransaction.getCategory().equals("Salary"))
            salary += overviewingTransaction.getAmount();
        else other_income += overviewingTransaction.getAmount();
        total_income += overviewingTransaction.getAmount();
    }

    private void handleOverViewExpense(myTransaction overviewingTransaction) {
        switch (overviewingTransaction.getCategory()) {
            case "Food":
                food += overviewingTransaction.getAmount();
                break;
            case "Beverage":
                beverage += overviewingTransaction.getAmount();
                break;
            case "Fees":
                fees += overviewingTransaction.getAmount();
                break;
            case "Shopping":
                shopping += overviewingTransaction.getAmount();
                break;
            case "Gift":
                gift += overviewingTransaction.getAmount();
                break;
            case "Healthcare":
                healthcare += overviewingTransaction.getAmount();
                break;
            case "Relationship":
                relationship += overviewingTransaction.getAmount();
                break;
            case "Entertainment":
                entertainment += overviewingTransaction.getAmount();
                break;
            case "Education":
                education += overviewingTransaction.getAmount();
                break;
            case "Investment":
                investment += overviewingTransaction.getAmount();
                break;
            default:
                other_expense += overviewingTransaction.getAmount();
                break;
        }
        total_expense += overviewingTransaction.getAmount();
    }

    private void handleOverViewDebt(myTransaction overviewingTransaction) {
        //if(overviewingTransaction.getCategory().equals("Debt"))
        debt += overviewingTransaction.getAmount();
        total_expense += overviewingTransaction.getAmount();
    }

    private void hideUselessTextView() {
        if (food == 0) ll_overview_item_food.setVisibility(View.GONE);
        if (beverage == 0) ll_overview_item_beverage.setVisibility(View.GONE);
        if (fees == 0) ll_overview_item_fees.setVisibility(View.GONE);
        if (shopping == 0) ll_overview_item_shopping.setVisibility(View.GONE);
        if (gift == 0) ll_overview_item_gift.setVisibility(View.GONE);
        if (healthcare == 0) ll_overview_item_healthcare.setVisibility(View.GONE);
        if (relationship == 0) ll_overview_item_relationship.setVisibility(View.GONE);
        if (education == 0) ll_overview_item_education.setVisibility(View.GONE);
        if (investment == 0) ll_overview_item_investment.setVisibility(View.GONE);
        if (entertainment == 0) ll_overview_item_entertainment.setVisibility(View.GONE);
        if (debt == 0) ll_overview_item_debt.setVisibility(View.GONE);
        if (other_expense == 0) ll_overview_item_other_expense.setVisibility(View.GONE);
        if (salary == 0) ll_overview_item_salary.setVisibility(View.GONE);
        if (other_income == 0) ll_overview_item_other_income.setVisibility(View.GONE);
    }

    private void setTextView() {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        tv_overview_item_food.setText(food + " đ - " + df.format(((double) food / total_expense) * 100) + "%");
        tv_overview_item_beverage.setText(beverage + " đ - " + df.format(((double) beverage / total_expense) * 100) + "%");
        tv_overview_item_fees.setText(fees + " đ - " + df.format(((double) fees / total_expense) * 100) + "%");
        tv_overview_item_shopping.setText(shopping + " đ - " + df.format(((double) shopping / total_expense) * 100) + "%");
        tv_overview_item_gift.setText(gift + " đ - " + df.format(((double) gift / total_expense) * 100) + "%");
        tv_overview_item_relationship.setText(relationship + " đ - " + (((double) relationship / total_expense) * 100) + "%");
        tv_overview_item_investment.setText(investment + " đ - " + df.format(((double) investment / total_expense) * 100) + "%");
        tv_overview_item_healthcare.setText(healthcare + " đ - " + df.format(((double) healthcare / total_expense) * 100) + "%");
        tv_overview_item_entertainment.setText(entertainment + " đ - " + df.format(((double) entertainment / total_expense) * 100) + "%");
        tv_overview_item_education.setText(education + " đ - " + df.format(((double) education / total_expense) * 100) + "%");
        tv_overview_item_debt.setText(debt + " đ - " + df.format(((double) debt / total_expense) * 100) + "%");
        tv_overview_item_other_expense.setText(other_expense + " đ - " + df.format(((double) other_expense / total_expense) * 100) + "%");
        tv_overview_total_expense.setText(total_expense + " đ");
        tv_overview_total_income.setText(total_income + " đ");
        tv_overview_item_salary.setText(salary + " đ - " + df.format(((double) salary / total_income) * 100) + "%");
        tv_overview_item_other_income.setText(other_income + " đ - " + df.format(((double) other_income / total_income) * 100) + "%");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.extend_function_overview_activity, menu);
        return true;
    }

    //Handle event when clicking item on menu on toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.btn__choose_date_display:
                showDialogPickMonth();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialogPickMonth() {
        dialogPickMonth = new Dialog(this);
        dialogPickMonth.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View mView = getLayoutInflater().inflate(R.layout.pick_month_dialog, null);
        dialogPickMonth.setContentView(mView);

        wheelPickerMonth = (WheelPicker) mView.findViewById(R.id.wheel_month_picker);
        wheelPickerYear = (WheelPicker) mView.findViewById(R.id.wheel_year_picker);
        createWheelPicker();
        dialogPickMonth.show();

        Button btn_ok_pick_month, btn_cancel_pick_month;
        btn_ok_pick_month = (Button) mView.findViewById(R.id.btn_ok_pick_month);
        btn_cancel_pick_month = (Button) mView.findViewById(R.id.btn_cancel_pick_month);
        btn_ok_pick_month.setOnClickListener(this);
        btn_cancel_pick_month.setOnClickListener(this);
    }

    void createWheelPicker() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                showSelectedTimeOverview(monthSelected, yearSelected, tempList);
//                Toast.makeText(this, "Selected " + monthSelected, Toast.LENGTH_SHORT).show();
                dialogPickMonth.dismiss();
                break;
            case R.id.btn_cancel_pick_month:
                dialogPickMonth.dismiss();
                break;
        }
    }

    private void showSelectedTimeOverview(int monthSelected, int yearSelected, List<myTransaction> list){
        total_expense = 0; other_expense = 0; total_income = 0; other_income = 0; food = 0;
        beverage = 0; fees = 0; shopping = 0; gift = 0; healthcare = 0; relationship = 0; investment = 0;
        education = 0; debt = 0; entertainment = 0; salary = 0;

        //case 1: all - year
        if(monthSelected == 0 && yearSelected != 0){
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).getYear() == yearSelected) {
                    classifyToHandle(list.get(i).getTypeOfTransaction(), list.get(i));
                }
            }
        }
        //case 2: month - all
        else if(monthSelected != 0 && yearSelected == 0){
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).getMonth() == monthSelected) {
                    classifyToHandle(list.get(i).getTypeOfTransaction(), list.get(i));
                }
            }
        }
        //case 3: month - year
        else if(monthSelected != 0 && yearSelected != 0){
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getMonth() == monthSelected && list.get(i).getYear() == yearSelected) {
                    classifyToHandle(list.get(i).getTypeOfTransaction(), list.get(i));
                }
            }
        }
        // case 4: all - all
        else if(monthSelected == 0 && yearSelected == 0){
            overView(list);
        }
        createExpensePieChart();
        initDataOfExpenseChart();
        createIncomePieChart();
        initDataOfIncomeChart();
        setTextView();
        hideUselessTextView();
    }
}
