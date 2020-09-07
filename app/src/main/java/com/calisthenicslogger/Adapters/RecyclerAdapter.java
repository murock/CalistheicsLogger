package com.calisthenicslogger.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.calisthenicslogger.R;
import com.calisthenicslogger.RoomDatabase.Entities.TrackedExercise;

import java.util.ArrayList;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TrackedExercise> trackedExercises = new ArrayList<>();
    private Context mContext;

    public RecyclerAdapter(Context context, List<TrackedExercise> exercises) {
        trackedExercises = exercises;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_with_title, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ((ViewHolder)viewHolder).titleTextView.setText("Test title");
        ((ViewHolder)viewHolder).bodyTextView.setText("This is a test \nNewline test");

        // Set the name of the 'NicePlace'
        //((ViewHolder)viewHolder).mName.setText(trackedExercises.get(i).getTitle());

        // Set the image
//        RequestOptions defaultOptions = new RequestOptions()
//                .error(R.drawable.ic_launcher_background);
//        Glide.with(mContext)
//                .setDefaultRequestOptions(defaultOptions)
//                .load(trackedExercises.get(i).getImageUrl())
//                .into(((ViewHolder)viewHolder).mImage);
    }

    @Override
    public int getItemCount() {
        return trackedExercises.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder{

        private TextView titleTextView;
        private TextView bodyTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            bodyTextView = itemView.findViewById(R.id.bodyTextView);
        }
    }
}
