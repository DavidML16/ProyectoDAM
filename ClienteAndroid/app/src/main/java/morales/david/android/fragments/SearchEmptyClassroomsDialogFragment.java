package morales.david.android.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import morales.david.android.R;
import morales.david.android.interfaces.OptionClicked;
import morales.david.android.managers.DataManager;
import morales.david.android.models.Course;
import morales.david.android.models.Day;
import morales.david.android.models.Hour;
import morales.david.android.models.TimeZone;

public class SearchEmptyClassroomsDialogFragment extends DialogFragment {

    private OptionClicked callback;

    private AutoCompleteTextView daysDropDown;
    private ArrayAdapter<String> daysAdapter;

    private AutoCompleteTextView hoursDropDown;
    private ArrayAdapter<String> hoursAdapter;

    private Day selectedDay;
    private Hour selectedHour;

    public SearchEmptyClassroomsDialogFragment() { }

    public static SearchEmptyClassroomsDialogFragment newInstance() {
        SearchEmptyClassroomsDialogFragment fragment = new SearchEmptyClassroomsDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Dialog dialog = createDialog();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    private Dialog createDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_search_classrooms_dialog, null);
        builder.setView(view);

        daysAdapter = new ArrayAdapter(getContext(), R.layout.item_dropdown, getDays());
        daysDropDown = view.findViewById(R.id.act_classrooms_search_day);
        daysDropDown.setAdapter(daysAdapter);

        daysDropDown.setOnItemClickListener((parent, v, position, id) -> {

            String selected = daysAdapter.getItem(position);

            if (DataManager.getInstance().getDays().getValue() == null)
                return;

            for (Day day : DataManager.getInstance().getDays().getValue()) {
                if (day.toString().equalsIgnoreCase(selected)) {
                    selectedDay = day;
                    break;
                }
            };

        });

        hoursAdapter = new ArrayAdapter(getContext(), R.layout.item_dropdown, getHours());
        hoursDropDown = view.findViewById(R.id.act_classrooms_search_hour);
        hoursDropDown.setAdapter(hoursAdapter);

        hoursDropDown.setOnItemClickListener((parent, v, position, id) -> {

            String selected = hoursAdapter.getItem(position);

            if (DataManager.getInstance().getHours().getValue() == null)
                return;

            for (Hour hour : DataManager.getInstance().getHours().getValue()) {
                if (hour.toString().equalsIgnoreCase(selected)) {
                    selectedHour = hour;
                    break;
                }
            }

        });

        CardView searchForm = view.findViewById(R.id.act_classrooms_search_button);

        searchForm.setOnClickListener(v -> {

            if(selectedDay == null || selectedHour == null)
                return;

            TimeZone timeZone = null;

            if (DataManager.getInstance().getTimeZones().getValue() == null)
                return;

            for(TimeZone loopTimeZone : DataManager.getInstance().getTimeZones().getValue()) {
                if(loopTimeZone.getDay().getId() == selectedDay.getId() && loopTimeZone.getHour().getId() == selectedHour.getId()) {
                    timeZone = loopTimeZone;
                    break;
                }
            }

            if(timeZone == null)
                return;

            callback.onClick(v, timeZone);

            dismiss();

        });

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        if(context instanceof OptionClicked)
            this.callback = (OptionClicked) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_classrooms_dialog, container, false);
    }

    private List<String> getDays() {

        List<String> days = new ArrayList<>();

        if (DataManager.getInstance().getDays().getValue() == null)
            return days;

        for (Day day : DataManager.getInstance().getDays().getValue())
            days.add(day.toString());

        return days;

    }

    private List<String> getHours() {

        List<String> hours = new ArrayList<>();

        if (DataManager.getInstance().getHours().getValue() == null)
            return hours;

        for (Hour hour : DataManager.getInstance().getHours().getValue())
            hours.add(hour.toString());

        return hours;

    }

}