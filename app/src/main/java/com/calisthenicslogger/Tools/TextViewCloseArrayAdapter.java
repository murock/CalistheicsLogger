package com.calisthenicslogger.Tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.calisthenicslogger.R;

import java.util.ArrayList;

public class TextViewCloseArrayAdapter extends BaseAdapter implements ListAdapter, Filterable {
    private ArrayList<String> list = new ArrayList<String>();
    private  ArrayList<String> fullList = new ArrayList<>();
    private Context context;
    public String removedItem;



    public TextViewCloseArrayAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.fullList = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.textview_close_button, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                removedItem = list.get(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<String> FilteredArrayNames = new ArrayList<String>();

                // perform your search here using the searchConstraint String.

                String constraintString = constraint.toString().toLowerCase();
                for (int i = 0; i < fullList.size(); i++) {
                    String dataNames = fullList.get(i);
                    dataNames = dataNames.toLowerCase();
                    String[] words = dataNames.split("\\s+");
                    boolean filterItem = false;
                    if(constraintString.contains(" ") && dataNames.contains(constraintString)){
                        // Check if user have type multiple words e.g "Pike P"
                        filterItem = true;
                    }else{
                        // Check against each word e.g "Up" will filter both "Pull Up" and "Push Up"
                        for(String word : words)
                        {
                            if (word.startsWith(constraintString))  {
                                filterItem = true;
                            }
                        }
                    }

                    if(filterItem){
                        FilteredArrayNames.add(fullList.get(i));
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (ArrayList<String>)results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
