package com.calisthenicslogger.Tools;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.calisthenicslogger.RoomDatabase.Entities.TrackedExercise;
import com.calisthenicslogger.activities.TrackActivity;

import java.util.ArrayList;

public class Utilities {

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getTrackedExerciseString(TrackedExercise exercise, boolean includeSetNum){
        // TODO: adapt this for many different units e.g kgs/m etc
        ArrayList<String> trackedComponents = new ArrayList<String>();

        if (includeSetNum)
        {
            trackedComponents.add(Integer.toString(exercise.getSetNumber()));
        }
        if (!(exercise.getReps() == -1)) {
            //  result += "    " + exercise.getReps() + " reps";
            trackedComponents.add(exercise.getReps() + " reps");
        }

        if (exercise.getCluster().length() > 0 && exercise.getCluster() != "0"){
            String[] clusterArray = exercise.getCluster().split(" ");
            String clusterString = "";
            for(String clusterRep : clusterArray){
                clusterString += " +" + clusterRep;
            }
            trackedComponents.add(clusterString);
        }

        if (!(exercise.getWeight() == -1))
        {
            trackedComponents.add(exercise.getWeight() + " kgs");
        }
        if (!exercise.getTime().isEmpty() && !exercise.getTime().equals("000000"))
        {
            String time = exercise.getTime();
            String displayTime = time.substring(0,2) + ":" + time.substring(2,4) + ":" + time.substring(4,6);
            trackedComponents.add(displayTime);
        }
        if (!exercise.getBand().isEmpty() && !exercise.getBand().equals("No"))
        {
            trackedComponents.add(exercise.getBand() + " band");
        }
        if (!exercise.getTool().isEmpty())
        {
            trackedComponents.add(exercise.getTool());
        }
        int distance = exercise.getDistance();
        if (distance != -1)
        {
            trackedComponents.add(distance + " m");
        }
        if (!exercise.getTempo().isEmpty() && !exercise.getTempo().equals(":::"))
        {
            trackedComponents.add(exercise.getTempo());
        }
        return ListToRow(trackedComponents);
    }

    // TODO: Improve this to use some kind of column system
    public static String ListToRow(ArrayList<String> list)
    {
        String result = new String();
        String spacer = "";
        switch(list.size()){
            case 1:
                spacer = "       ";
                break;
            case 2:
                spacer = "      ";
                break;
            case 3:
                spacer = "     ";
                break;
            case 4:
                spacer = "    ";
                break;
            case 5:
                spacer = "   ";
                break;
            case 6:
                spacer = "  ";
                break;
            default:

        }
        for (String item : list){
            result += item + spacer;
        }
        result = result.substring(0,result.length() - spacer.length());
        return result;
    }
}
