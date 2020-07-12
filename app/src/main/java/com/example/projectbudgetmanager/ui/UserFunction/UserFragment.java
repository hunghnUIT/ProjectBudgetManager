package com.example.projectbudgetmanager.ui.UserFunction;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projectbudgetmanager.R;
import com.example.projectbudgetmanager.back_end.BaseUser;
import com.example.projectbudgetmanager.back_end.controller.DangNhap;
import com.example.projectbudgetmanager.back_end.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
//import com.example.projectbudgetmanager.User_cache;
//import com.example.projectbudgetmanager.backend.userInterface;

import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
//    userInterface userInterface;
    Button btn_login, btn_to_register_activity;
    EditText et_username, et_password;

    OnHeadlineSelectedListener mCallback;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

//        et_username = (EditText) getActivity().findViewById(R.id.et_username_login);
//        et_password = (EditText) getActivity().findViewById(R.id.et_password_login);
//        btn_login = (Button) getActivity().findViewById(R.id.btn_login);
//        btn_to_register_activity = (Button) getActivity().findViewById(R.id.btn_to_register_activity);
//
//        btn_to_register_activity.setOnClickListener(this);
//        btn_login.setOnClickListener(this);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btn_login:
//                login();
//                break;
//            case R.id.btn_to_register_activity:
//                Intent intent = new Intent(getContext(), RegisterActivity.class);
//                startActivity(intent);
//                break;
//        }

    }

    void login(){
        final Gson gson =new Gson();
        DangNhap dangNhap=new DangNhap();
        Response response=null;
        JsonObject jsonObject=new JsonObject();
        String redata="";
        BaseUser baseUser=new BaseUser();
        String data=gson.toJson(new User(et_username.getText().toString(),et_password.getText().toString()));

        try {
            response=dangNhap.execute("https://api-backend-nhom15.herokuapp.com/api/login",data).get();
            redata=response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(response.code()==200)
        {
            jsonObject=gson.fromJson(redata,JsonObject.class);
            Toast.makeText(getContext(),jsonObject.get("Token").getAsString(),Toast.LENGTH_LONG).show();
            baseUser.base=jsonObject.get("Token").getAsString();
        }

    }
}


    // Container Activity must implement this interface
    interface OnHeadlineSelectedListener {
        public void onArticleSelected(int position);
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
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


