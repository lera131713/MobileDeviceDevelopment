package ua.gura.com.example.androiddiary.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import ua.gura.com.example.androiddiary.AddNewTask;
import ua.gura.com.example.androiddiary.BaseActivity;
import ua.gura.com.example.androiddiary.model.DiaryModel;
import ua.gura.com.example.androiddiary.R;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.MyViewHolder> {

    private final List<DiaryModel> diaryModelList;
    private final BaseActivity activity;
    private FirebaseFirestore fireStore;

    public Context getContext() {
        return activity;
    }

    public void deleteTask(int position){
        DiaryModel diaryModel = diaryModelList.get(position);
        fireStore.collection("task").document(diaryModel.TaskId).delete();
        diaryModelList.remove(position);
        notifyItemRemoved(position);
    }

    public void editTask(int position){
        DiaryModel diaryModel = diaryModelList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("task", diaryModel.getTask());
        bundle.putString("date", diaryModel.getDate());
        bundle.putString("id", diaryModel.TaskId);

        AddNewTask addNewTask = new AddNewTask();
        addNewTask.setArguments(bundle);
        addNewTask.show(activity.getSupportFragmentManager(),addNewTask.getTag());
    }

    public DiaryAdapter(List<DiaryModel> diaryModelList, BaseActivity activity) {
        this.diaryModelList = diaryModelList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.each_task, parent, false);
        fireStore = FirebaseFirestore.getInstance();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DiaryModel diaryModel = diaryModelList.get(position);
        holder.checkBox.setText(diaryModel.getTask());
        holder.textViewDate.setText("Date: " + diaryModel.getDate());
        holder.checkBox.setChecked(convertIntToBoolean(diaryModel.getStatus()));
        holder.checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked){
                fireStore.collection("task")
                        .document(diaryModel.TaskId)
                        .update("status",1);
            }else {
                fireStore.collection("task")
                        .document(diaryModel.TaskId)
                        .update("status",0);
            }
        });
    }

    private boolean convertIntToBoolean(int status) {
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return diaryModelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.idDueDate);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
