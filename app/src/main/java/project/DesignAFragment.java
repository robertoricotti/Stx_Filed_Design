package project;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.stx_field_design.R;

public class DesignAFragment extends Fragment {

    public DataProjectSingleton dataProject;
    public EditText z, length, slope;
    private OnViewCreated mListener;



    @SuppressLint({"MissingInflatedId", "SetTextI18n", "DefaultLocale"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.design_a_fragment, container, false);

        dataProject = DataProjectSingleton.getInstance();

        z = view.findViewById(R.id.editZ);
        length = view.findViewById(R.id.editLength);
        slope = view.findViewById(R.id.editSlope);

        mListener.onCreated(this);
        return view;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnViewCreated) context;
        }
        catch (ClassCastException ignored) {}
    }


}