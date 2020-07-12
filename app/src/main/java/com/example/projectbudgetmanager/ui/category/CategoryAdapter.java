package com.example.projectbudgetmanager.ui.category;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.projectbudgetmanager.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends ArrayAdapter<String> {
    private Context context;
    private int resource;
    private ArrayList<String> listCategory;

    public CategoryAdapter(@NonNull Activity context, int resource, ArrayList<String> listCategory) {
        super(context, resource, listCategory);

        this.context = context;
        this.resource = resource;
        this.listCategory = listCategory;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category, null,
                    false);
        }
        //Get item
        String category = getItem(position);
        //Get view
        TextView tv_name_category = (TextView) convertView.findViewById(R.id.tv_name_category);
        CircleImageView img_of_category = (CircleImageView) convertView.findViewById(R.id.img_of_category);
        //Set the value for each item
        tv_name_category.setText(category);
        int id = getImageId(context, category.toLowerCase());
        if(id == 0) id = getImageId(context, "other");
        img_of_category.setImageResource(id);

        return convertView;
    }

    private static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }
}
