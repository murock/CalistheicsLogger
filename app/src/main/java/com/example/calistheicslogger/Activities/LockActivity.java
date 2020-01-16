package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.AppExecutors;
import com.example.calistheicslogger.Tools.dslv.DragSortController;
import com.example.calistheicslogger.Tools.dslv.DragSortListView;

import java.util.ArrayList;

public class LockActivity extends Activity {

    DragSortListView dslv;
    ArrayAdapter<String> dslvAdapter;
    DragSortController dragSortController;

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        swapBands(from + 1,to + 1);
                        String item = dslvAdapter.getItem(from);
                        dslvAdapter.remove(item);
                        dslvAdapter.insert(item, to);
                    }
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locker_activity);
        SetUpDSLV();
    }

    private void swapBands(final int BandNo1, final int BandNo2)
    {
//        AppExecutors.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                appDatabase.trackedExerciseDao().swapBySetNumber(SetNo1, SetNo2);
//                updateTrackingList();
//            }
//        });
    }

    public DragSortController buildController(DragSortListView dslv) {
        DragSortController controller = new DragSortController(dslv);
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_LONG_PRESS);
        controller.setRemoveMode(DragSortController.FLING_REMOVE);
        return controller;
    }


    private void SetUpDSLV()
    {
        dslv = findViewById(R.id.lockerDSLV);
        dslv.setDropListener(onDrop);
        dragSortController = buildController(dslv);
        dslv.setFloatViewManager(dragSortController);
        dslv.setOnTouchListener(dragSortController);
    }

    private void UpdateDSLV(ArrayList<String> items){
        dslvAdapter = new ArrayAdapter<>(this,R.layout.center_spinner_text,items);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //dslv = findViewById(R.id.trackedExerciseDSLV);
                dslv.setAdapter(dslvAdapter);
            }
        });
    }
}
