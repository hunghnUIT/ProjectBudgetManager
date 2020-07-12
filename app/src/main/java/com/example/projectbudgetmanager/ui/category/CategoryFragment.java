package com.example.projectbudgetmanager.ui.category;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectbudgetmanager.MainActivity;
import com.example.projectbudgetmanager.R;
import com.example.projectbudgetmanager.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    
    private FloatingActionButton fab_add_category;
    private ListView lv_category;
    private Button btn_ok_add_category, btn_cancel_add_category;
    private EditText et_add_category;
    private String clickedCategory;
    private int clickedPosition;
    private TextView tv_remind_enter_category;
    public CategoryAdapter categoryAdapter;
    private OnFragmentInteractionListener mListener;
    ArrayList<String> localListOfCategory;
    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        localListOfCategory = ((MainActivity)getActivity()).listCategory;

        categoryAdapter = new CategoryAdapter(getActivity(),1, localListOfCategory);
        lv_category = (ListView)getView().findViewById(R.id.lv_category);
        lv_category.setAdapter(categoryAdapter);

        //Handle event edit when click on item: ??????
        lv_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedPosition = position;
//                clickedCategory = ((MainActivity)getActivity()).listCategory.get(position).toString();
                clickedCategory = localListOfCategory.get(position).toString();
                showDialog(true);
                ((MainActivity)getActivity()).saveListCategory();
                //((MainActivity)getActivity()).categoryAdapter.clear();
                ((MainActivity)getActivity()).loadListCategory();
                //categoryAdapter.notifyDataSetChanged();
            }
        });
        ////Handle event delete when long click on item
        lv_category.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Are you sure want to delete permanently this category?");
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
//                                ((MainActivity)getActivity()).listCategory.remove(pos);
                                localListOfCategory.remove(pos);
                                ((MainActivity)getActivity()).listCategory = localListOfCategory;
                                ((MainActivity)getActivity()).saveListCategory();
                                //((MainActivity)getActivity()).categoryAdapter.clear();
                                ((MainActivity)getActivity()).loadListCategory();
                                categoryAdapter.notifyDataSetChanged();
                            }
                        });
                alertDialog.show();
                return true;
            }
        });
        fab_add_category = (FloatingActionButton) getView().findViewById(R.id.fab_add_category);
        fab_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(false);
            }
        });
        ((MainActivity)getActivity()).loadListCategory();
    }

    private void showDialog(final boolean isEdit) {
        final Dialog dialogAddCategory = new Dialog(getContext());
        dialogAddCategory.setContentView(R.layout.add_category_dialog);
        dialogAddCategory.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddCategory.show();
        btn_ok_add_category = (Button)dialogAddCategory.findViewById(R.id.btn_ok_add_category);
        btn_cancel_add_category = (Button)dialogAddCategory.findViewById(R.id.btn_cancel_add_category);
        et_add_category = (EditText) dialogAddCategory.findViewById(R.id.et_add_category);
        tv_remind_enter_category = (TextView) dialogAddCategory.findViewById(R.id.tv_remind_enter_name_category);
        if(isEdit) { et_add_category.setText(clickedCategory); }

        btn_ok_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_add_category.getText())){
                    tv_remind_enter_category.setVisibility(View.VISIBLE);
                }
                else {
                    if(isEdit){
//                        ((MainActivity)getActivity()).listCategory.set(clickedPosition, et_add_category.getText().toString());
                        localListOfCategory.set(clickedPosition, et_add_category.getText().toString());
                    }
                    else{
//                        ((MainActivity)getActivity()).listCategory.add(et_add_category.getText().toString());
                        localListOfCategory.add(et_add_category.getText().toString());
                    }

                    ((MainActivity)getActivity()).listCategory = localListOfCategory;

                    ((MainActivity)getActivity()).sortCategoryList(localListOfCategory);
                    ((MainActivity)getActivity()).sortCategoryList(((MainActivity)getActivity()).listCategory);
                    ((MainActivity)getActivity()).saveListCategory();

                    Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                    categoryAdapter.notifyDataSetChanged();
                    dialogAddCategory.dismiss();
                }
            }
        });
        btn_cancel_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddCategory.dismiss();
            }
        });
    }


    private void initListCategory() {
        ((MainActivity)getActivity()).listCategory.add("Food");
        ((MainActivity)getActivity()).listCategory.add("Beverage");
        ((MainActivity)getActivity()).listCategory.add("Shopping");
        ((MainActivity)getActivity()).listCategory.add("Gift");
        ((MainActivity)getActivity()).listCategory.add("Book");
        ((MainActivity)getActivity()).listCategory.add("Education");
        ((MainActivity)getActivity()).listCategory.add("Fees");
        ((MainActivity)getActivity()).listCategory.add("Travel");
        ((MainActivity)getActivity()).listCategory.add("Relationship");
        ((MainActivity)getActivity()).listCategory.add("Entertainment");
        ((MainActivity)getActivity()).listCategory.add("Health Care");
        ((MainActivity)getActivity()).listCategory.add("Investment");
        ((MainActivity)getActivity()).listCategory.add("background2018");
        ((MainActivity)getActivity()).listCategory.add("Other");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
         * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
