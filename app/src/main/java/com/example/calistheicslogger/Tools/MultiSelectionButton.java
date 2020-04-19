package com.example.calistheicslogger.Tools;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.Button;

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
        this.items.add(0, new Item("All", true));
        selection = new boolean[this.items.size()];
//        adapter.clear();
//        adapter.add("");
        Arrays.fill(selection, false);
    }
}
