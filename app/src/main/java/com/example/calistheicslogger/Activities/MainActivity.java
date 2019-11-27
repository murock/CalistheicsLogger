package com.example.calistheicslogger.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.calistheicslogger.R;

public class MainActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Handle clicks on elipsis menu items
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.settings:
                Log.i("Item selected","Settings");
                return true;
            case R.id.locker:
                Log.i("Item selected","locker");
                return true;
            default:
                return  false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addExerciseClick(View view){
        Log.i("Button Pressed:", "Add Exercise");
        Intent addExercise = new Intent(this,ExerciseListActivity.class);
        startActivity(addExercise);
    }

    public void calendarClick(View view)
    {
        LinearLayout linearLayout = findViewById(R.id.listviewBox);
//        Log.i("Button pressed:","Calendar");
//        ListView test = new ListView(this);
//        final String[] DynamicListElements = new String[] {
//                "Android Test",
//                "PHP",
//                "Android Studio",
//                "PhpMyAdmin"
//        };
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>
//                (MainActivity.this, android.R.layout.simple_list_item_1, DynamicListElements);
//        test.setAdapter(adapter);
//        LinearLayout linearLayout = findViewById(R.id.listviewBox);
//        Log.i("Alfie", test.getHeight() + "");
//        test.setMinimumHeight(1000);
//        linearLayout.addView(test);

        final String[] DynamicListElements = new String[] {
                "Android Test",
                "PHP",
                "Android Studio",
                "PhpMyAdmin"
        };
        for(String item : DynamicListElements)
        {
            TextView test = new TextView(this);
            test.setText(item);
            linearLayout.addView(test);
        }
        TextView spacer = new TextView(this);
        spacer.setText("-------------------------");
        linearLayout.addView(spacer);

    }

//    public static void setListViewHeightBasedOnChildren(ListView listView)
//    {
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null)
//        {
//            return;
//        }
//        int totalHeight = 0;
//        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
//        for (int i = 0; i < listAdapter.getCount(); i++)
//        {
//            View listItem = listAdapter.getView(i, null, listView);
//            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
//            totalHeight += listItem.getMeasuredHeight();
//        }
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))+20;
//        listView.setLayoutParams(params);
//        listView.requestLayout();
//    }
}
