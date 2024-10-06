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

public class WorkItemAdapter extends RecyclerView.Adapter<WorkItemAdapter.WorkItemViewHolder> {

    private Context context;
    private List<WorkItem> workItemList;
    private OnWorkItemClickListener listener;

    public WorkItemAdapter(Context context, List<WorkItem> workItemList, OnWorkItemClickListener listener) {
        this.context = context;
        this.workItemList = workItemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_item, parent, false);
        return new WorkItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkItemViewHolder holder, int position) {
        WorkItem workItem = workItemList.get(position);
        holder.textViewCategory.setText(workItem.getCategory());
        holder.textViewWorkItem.setText(workItem.getText());
        holder.textViewDateTime.setText(getFormattedDateTime(workItem.getDueDate()));


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

    public class WorkItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewWorkItem;
        TextView textViewCategory;
        TextView textViewDateTime;
        Button buttonEdit;
        Button buttonDelete;

        public WorkItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWorkItem = itemView.findViewById(R.id.textViewWorkItem);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }

    public interface OnWorkItemClickListener {
        void onEditClick(WorkItem workItem);

        void onDeleteClick(WorkItem workItem);
    }

    private String getFormattedDateTime(long dateTimeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        return sdf.format(dateTimeMillis);
    }
}
