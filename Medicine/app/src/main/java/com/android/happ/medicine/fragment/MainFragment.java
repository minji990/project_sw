package com.android.happ.medicine.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.happ.medicine.EventDecorator;
import com.android.happ.medicine.ScheduleAdapter;
import com.android.happ.medicine.data.ScheduleModel;
import com.android.happ.medicine.databinding.FragmentMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainFragment extends Fragment {
    private FragmentMainBinding binding;
    private LocalDate selectedDate;

    private List<ScheduleModel> scheduleList = new ArrayList<>();
    private ScheduleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setEvent();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectedDate = LocalDate.now(); // 초기값 반드시 설정
        }

        // 리사이클러뷰 설정 (selectedDate 초기화 이후에 실행해야 함)
        binding.scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ScheduleAdapter(scheduleList,
                FirebaseAuth.getInstance().getUid(),
                selectedDate != null ? selectedDate.toString() : "unknown-date");
        binding.scheduleRecyclerView.setAdapter(adapter);

        // 캘린더 날짜 선택
        binding.calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                selectedDate = LocalDate.of(date.getYear(), date.getMonth() + 1, date.getDay());
                fetchSchedulesForDate(selectedDate.toString());
            }
        });

        // 초기 오늘 날짜 로딩
        if (selectedDate != null) {
            binding.calendarView.setSelectedDate(CalendarDay.today());
            fetchSchedulesForDate(selectedDate.toString());
        }

        markScheduleDates();
    }

    private void setEvent() {
        binding.layoutLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            requireActivity().finish();
        });

        binding.btnAddSchedule.setOnClickListener(v -> {
            if (selectedDate == null) {
                Toast.makeText(getContext(), "날짜를 먼저 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            showAddScheduleDialog();
        });
    }

    private void showAddScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(selectedDate + " 일정 등록");

        final EditText input = new EditText(requireContext());
        input.setHint("일정 내용을 입력하세요");
        builder.setView(input);

        builder.setPositiveButton("저장", (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String dateString = selectedDate.toString();
                String docId = dateString + "_" + text.replace(" ", "_"); // 공백 제거

                ScheduleModel schedule = new ScheduleModel(dateString, text, false);

                FirebaseFirestore.getInstance()
                        .collection("Schedules")
                        .document(uid)
                        .collection("schedules")
                        .document(docId) // 직접 문서 ID 지정
                        .set(schedule)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getContext(), "일정 저장 완료", Toast.LENGTH_SHORT).show();
                            fetchSchedulesForDate(dateString);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("Firebase", "저장 실패", e);
                        });
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void fetchSchedulesForDate(String dateString) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("Schedules")
                .document(uid)
                .collection("schedules")
                .whereEqualTo("date", dateString)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    scheduleList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        ScheduleModel schedule = document.toObject(ScheduleModel.class);
                        scheduleList.add(schedule);
                    }

                    // Adapter를 새로 생성해서 최신 selectedDate를 반영
                    adapter = new ScheduleAdapter(scheduleList, uid, dateString);
                    binding.scheduleRecyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Log.e("Firebase", "불러오기 실패", e));
        markScheduleDates();
    }

    private void markScheduleDates() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("Schedules")
                .document(uid)
                .collection("schedules")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    HashSet<CalendarDay> markedDates = new HashSet<>();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        ScheduleModel schedule = doc.toObject(ScheduleModel.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LocalDate date = LocalDate.parse(schedule.getDate());
                            CalendarDay calendarDay = CalendarDay.from(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
                            markedDates.add(calendarDay);
                        }
                    }

                    binding.calendarView.addDecorator(new EventDecorator(
                            Color.BLUE, markedDates
                    ));
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
