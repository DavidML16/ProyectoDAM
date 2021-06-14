package morales.david.android.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.util.List;

import morales.david.android.R;
import morales.david.android.adapters.ClassroomsAdapter;
import morales.david.android.models.Classroom;
import morales.david.android.models.TimeZone;

public class EmptyClassroomsDialogFragment extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ClassroomsAdapter adapter;

    public static EmptyClassroomsDialogFragment newInstance(TimeZone timeZone, List<Classroom> classrooms) {
        final EmptyClassroomsDialogFragment fragment = new EmptyClassroomsDialogFragment();
        final Bundle args = new Bundle();
        args.putSerializable("timeZone", timeZone);
        args.putSerializable("classrooms", (Serializable) classrooms);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empty_classrooms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.frg_classrooms_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        List<Classroom> classrooms = (List<Classroom>) getArguments().getSerializable("classrooms");

        adapter = new ClassroomsAdapter(getActivity(), classrooms);

        recyclerView.setAdapter(adapter);

    }

}