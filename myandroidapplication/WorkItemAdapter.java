package com.example.myandroidapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WorkItemAdapter extends RecyclerView.Adapter<WorkItemAdapter.ViewHolder> {

    private Context context;
    private List<WorkItem> workItemList;
    private OnWorkItemClickListener listener;

    public WorkItemAdapter(Context context,
                           List<WorkItem> workItemList,
                           OnWorkItemClickListener listener) {
        this.context = context;
        this.workItemList = workItemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_work_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkItem workItem = workItemList.get(position);
        holder.textViewWorkItem.setText(workItem.getText());
        holder.textViewDateTime.setText(getFormattedDateTime(workItem.getDueDate())); // Set date and time

        // Set click listeners for edit and delete buttons
        holder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEditClick(workItem);
                }
            }
        });

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteClick(workItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return workItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewWorkItem;
        TextView textViewDateTime;
        Button buttonEdit;
        Button buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWorkItem = itemView.findViewById(R.id.textViewWorkItem);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }

    // Interface for item click handling
    public interface OnWorkItemClickListener {
        void onEditClick(WorkItem workItem);
        void onDeleteClick(WorkItem workItem);
    }

    // Utility method to format date and time
    private String getFormattedDateTime(long dateTimeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        return sdf.format(dateTimeMillis);
    }
}
