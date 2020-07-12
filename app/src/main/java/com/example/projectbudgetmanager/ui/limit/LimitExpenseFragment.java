package com.example.projectbudgetmanager.ui.limit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectbudgetmanager.MainActivity;
import com.example.projectbudgetmanager.R;
import com.example.projectbudgetmanager.ui.home.EditTransactionActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class LimitExpenseFragment extends Fragment {

    private LimitExpenseViewModel mViewModel;
    private Button btn_calculate_limit, btn_set_limit, btn_delete_limit;
    androidx.appcompat.widget.Toolbar limit_expense_fragment_toolbar;
    private EditText et_input_budget_limitation;
    private TextView tv_limit_of_expenses, tv_remind_enter_budget;
    private LinearLayout ll_remind_enter_budget;
    private long budget = 0;


    public static LimitExpenseFragment newInstance() {
        return new LimitExpenseFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_limit_expense, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LimitExpenseViewModel.class);
        // TODO: Use the ViewModel

        btn_calculate_limit= (Button) getView().findViewById(R.id.btn_calculate_limit);
        btn_set_limit = (Button) getView().findViewById(R.id.btn_set_limit);
        btn_delete_limit = (Button)getView().findViewById(R.id.btn_delete_limit);
//        limit_expense_fragment_toolbar = (Toolbar) getView().findViewById(R.id.limit_expense_fragment_toolbar);
        et_input_budget_limitation = (EditText)getView().findViewById(R.id.et_input_budget_limitation);
        tv_limit_of_expenses = (TextView)getView().findViewById(R.id.tv_limit_of_expenses);
        ll_remind_enter_budget = (LinearLayout)getView().findViewById(R.id.ll_remind_enter_budget);
        tv_remind_enter_budget = (TextView)getView().findViewById(R.id.tv_remind_enter_budget);

//        limit_expense_fragment_toolbar.setTitle("Limit your expense");
//        ((AppCompatActivity)getActivity()).setSupportActionBar(limit_expense_fragment_toolbar);
//        //Display back button (Back arrow  <- )
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        //Back arrow to return the before fragment
//        limit_expense_fragment_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().onBackPressed();
//            }
//        });
//        if(((MainActivity)getActivity()).amoutToLimitExpense != 0) {
            tv_limit_of_expenses.setText("" + ((MainActivity)getActivity()).amoutToLimitExpense);
            tv_limit_of_expenses.setVisibility(View.VISIBLE);
//        }
        btn_calculate_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_input_budget_limitation.getText())){
                    tv_remind_enter_budget.setVisibility(View.VISIBLE);
                }
                else {
                    tv_remind_enter_budget.setVisibility(View.GONE);
                    budget = Long.parseLong(et_input_budget_limitation.getText().toString());
                    tv_limit_of_expenses.setText("" + budget/30 + " đ");
                    tv_limit_of_expenses.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_set_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_input_budget_limitation.getText())){
                    tv_remind_enter_budget.setVisibility(View.VISIBLE);
                }
                else {
                    tv_remind_enter_budget.setVisibility(View.GONE);
                    budget = Long.parseLong(et_input_budget_limitation.getText().toString());
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setTitle("Confirm");
                    alertDialog.setMessage("Notification will be send if you pay more than "+ budget/30 + " đ for one day. Set limit expenses?");
                    alertDialog.setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getContext(), "NO selected", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            });
                    alertDialog.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
//                                    SM.sendData(et_input_budget_limitation.getText().toString().trim());
                                    ((MainActivity) getActivity()).amoutToLimitExpense = budget/30;
                                    Toast.makeText(getContext(), "Expenses Limited", Toast.LENGTH_SHORT).show();
                                    tv_limit_of_expenses.setText(((MainActivity)getActivity()).amoutToLimitExpense + " đ");
                                    ((MainActivity)getActivity()).saveLimitAmout();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
        btn_delete_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Do you want to delete the limit " +((MainActivity) getActivity()).amoutToLimitExpense +"đ for a day?");
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
                                ((MainActivity) getActivity()).amoutToLimitExpense = 0;
                                Toast.makeText(getContext(), "Expenses Limit Deleted", Toast.LENGTH_SHORT).show();
                                //((MainActivity)getActivity()).saveLimitAmout();
                                tv_limit_of_expenses.setText(((MainActivity)getActivity()).amoutToLimitExpense + " đ");
                            }
                        });
                alertDialog.show();
            }
        });
        loadLimitAmout();
    }

    //Load limit
    public void loadLimitAmout(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences amout limit", MODE_PRIVATE);
        ((MainActivity)getActivity()).amoutToLimitExpense = sharedPreferences.getLong("Amount To Limit Expense", 0);
    }
}
