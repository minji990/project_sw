package com.android.happ.medicine;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.happ.medicine.data.ScheduleModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private List<ScheduleModel> scheduleList;
    private String uid;
    private String selectedDate;

    public ScheduleAdapter(List<ScheduleModel> scheduleList, String uid, String selectedDate) {
        this.scheduleList = scheduleList;
        this.uid = uid;
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleModel item = scheduleList.get(position);
        holder.textContent.setText(item.getContent());

        holder.checkBox.setOnCheckedChangeListener(null); // 리스너 중복 방지
        holder.checkBox.setChecked(item.isCompleted());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setCompleted(isChecked);

            // 문서 ID 기반 접근
            String docId = selectedDate + "_" + item.getContent().replace(" ", "_");

            FirebaseFirestore.getInstance()
                    .collection("Schedules")
                    .document(uid)
                    .collection("schedules")
                    .document(docId)
                    .update("completed", isChecked)
                    .addOnSuccessListener(aVoid -> Log.d("ScheduleAdapter", "업데이트 성공: " + docId))
                    .addOnFailureListener(e -> Log.e("ScheduleAdapter", "업데이트 실패", e));
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textContent;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            textContent = itemView.findViewById(R.id.textContent);
        }
    }
}
