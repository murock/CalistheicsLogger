package com.example.calistheicslogger.Tools;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.calistheicslogger.R;

import java.util.ArrayList;
import java.util.List;

public class TextViewPRArrayAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private int selectedPosition = -1;
    private ArrayList<Integer> prPositions = new ArrayList<>();


    public void setSelectedPostion(int selectedPosition){
        this.selectedPosition = selectedPosition;
    }

    public void setPrPositions(ArrayList<Integer> prPositions)
    {
        this.prPositions = prPositions;
    }

    public void remove(String item){
        list.remove(item);
    }

    public void insert(String item, int index)
    {
        list.add(index, item);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        //Handle TextView and display string from your list
        TextView listItemTextView = new TextView(this.context);


        if (this.prPositions.contains(position))
        {
            listItemTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pr_medal_icon, 0, 0, 0);
            listItemTextView.setText(list.get(position));
        } else {
            listItemTextView.setText("       " + list.get(position));
        }

        if (position == selectedPosition)
        {
            int backgroundColor = Color.parseColor("#c2d4f0");
            listItemTextView.setBackgroundColor(backgroundColor);
        }else {
            listItemTextView.setBackgroundColor(Color.WHITE);
        }

        return listItemTextView;
    }

    public TextViewPRArrayAdapter(ArrayList<String> list, Context context)
    {
        this.list = list;
        this.context = context;
    }
}
