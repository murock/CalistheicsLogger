package com.example.calistheicslogger.Tools;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;

import com.example.calistheicslogger.Tools.MultiSelectSpinner.Item;

import java.util.ArrayList;
import java.util.Arrays;

public class MultiSelectionButton extends androidx.appcompat.widget.AppCompatImageButton implements DialogInterface.OnMultiChoiceClickListener {

    ArrayList<Item> items = null;
    boolean[] selection = null;


    public MultiSelectionButton(Context context) {
        super(context);
    }

    public MultiSelectionButton(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

        final AlertDialog alertDialog = (AlertDialog)dialog;
        final ListView listView = alertDialog.getListView();
        // if  something other than 'All' selected and checked then uncheck 'All'
        if (which != 0 && isChecked && selection.length > 0){
            Log.i("Alfie", "Got here");
            selection[0] = false;
            items.set(0, new Item("All",false));

            listView.setItemChecked(0, false);
        }
        // Check 'All' if rest of items are unchecked
        if (allUnchecked(listView)){
            selection[0] = true;
            items.set(0, new Item("All",true));
            listView.setItemChecked(0, true);
        }
        if (selection != null && which < selection.length) {
            selection[which] = isChecked;

//            adapter.clear();
//            adapter.add(buildSelectedItemString());
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String[] itemNames = new String[items.size()];

        for (int i = 0; i < items.size(); i++) {
            itemNames[i] = items.get(i).getName();
        }

        builder.setMultiChoiceItems(itemNames, selection, this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                // Do nothing
            }
        });

        builder.show();
        return true;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
        this.setUpSelection();
//        adapter.clear();
//        adapter.add("");
    }

    private void setUpSelection()
    {
        selection = new boolean[this.items.size()];
        for(int i = 0; i < this.items.size(); i++)
        {
            this.selection[i] = items.get(i).getValue();
        }
    }

    private boolean allUnchecked(ListView listView)
    {
        for(int i = 1; i < listView.getCount(); i++)
        {
            if(listView.isItemChecked(i))
            {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Item> getSelectedItems() {
        ArrayList<Item> selectedItems = new ArrayList<>();

        for (int i = 0; i < items.size(); ++i) {
            if (selection[i]) {
                selectedItems.add(items.get(i));
            }
        }

        return selectedItems;
    }

    public void Reset(){
        for (int i = 0; i < this.selection.length; i++) {
            this.selection[i] = false;
        }
        for (int i = 0; i < items.size(); ++i) {
            items.get(i).setValue(false);
        }
        // Set 'All' to true
        if (items.size() > 0 && selection.length > 0){
            items.get(0).setValue(true);
            selection[0] = true;
        }
    }
}
