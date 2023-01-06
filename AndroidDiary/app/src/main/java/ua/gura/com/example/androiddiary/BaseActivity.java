package ua.gura.com.example.androiddiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import ua.gura.com.example.androiddiary.adapter.DiaryAdapter;
import ua.gura.com.example.androiddiary.model.DiaryModel;

public class BaseActivity extends AppCompatActivity implements OnDialogCloseListener {

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private FirebaseFirestore firestore;
    private DiaryAdapter adapter;
    private List<DiaryModel> list;
    private Query query;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        firestore = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(BaseActivity.this));

        floatingActionButton.setOnClickListener(v -> { // call BottomSheetDialogFragment for create a new task
            AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
        });

        list = new ArrayList<>();
        adapter = new DiaryAdapter(list, this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper((adapter))); // control swipe (left-right)
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);
        showData();
    }

    private void showData() {
        query = firestore.collection("task")
                .orderBy("time", Query.Direction.DESCENDING);

        listenerRegistration = query.addSnapshotListener((value, error) -> {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String id = documentChange.getDocument().getId();
                    DiaryModel diaryModel = documentChange
                            .getDocument()
                            .toObject(DiaryModel.class)
                            .withId(id);
                    list.add(diaryModel);
                    adapter.notifyDataSetChanged();
                }
            }
            listenerRegistration.remove();
        });
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        list.clear();
        showData();
        adapter.notifyDataSetChanged();
    }
}