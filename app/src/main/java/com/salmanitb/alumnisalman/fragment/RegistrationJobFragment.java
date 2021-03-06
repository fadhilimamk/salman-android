package com.salmanitb.alumnisalman.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.salmanitb.alumnisalman.R;
import com.salmanitb.alumnisalman.SalmanApplication;
import com.salmanitb.alumnisalman.activity.RegistrationActivity;
import com.salmanitb.alumnisalman.helper.RegistrationCheckerCallback;
import com.salmanitb.alumnisalman.helper.RegistrationStepFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.salmanitb.alumnisalman.activity.EditProfileActivity.EDIT_ARGUMENT;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationJobFragment extends RegistrationStepFragment {

    @BindView(R.id.leftColumn)
    LinearLayout leftColumn;
    @BindView(R.id.rightColumn)
    LinearLayout rightColumn;
    @BindView(R.id.input_company)
    EditText inputCompany;
    @BindView(R.id.checkbox_others)
    CheckBox checkboxOthers;
    @BindView(R.id.input_others)
    EditText inputOthers;
    @BindView(R.id.txt_error)
    TextView txtError;

    ArrayList<CheckBox> jobCheckbox;

    private boolean isEdit;

    public RegistrationJobFragment() {
        // Required empty public constructor
        isEdit = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registration_job, container, false);
        ButterKnife.bind(this, rootView);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            isEdit = bundle.getBoolean(EDIT_ARGUMENT);
//            Log.d("FRAGMENT_DEBUG","isEdit true");
        }

        jobCheckbox = new ArrayList<>();
        String jobs[] = getResources().getStringArray(R.array.default_registered_profession);
        prepareJobOption(leftColumn, rightColumn, jobs);

        checkboxOthers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    inputOthers.setEnabled(true);
                    inputOthers.setBackground(getResources().getDrawable(R.drawable.edit_text_background));
                } else {
                    inputOthers.setEnabled(false);
                    inputOthers.setText("");
                    inputOthers.setBackground(getResources().getDrawable(R.drawable.edit_text_background_disabled));
                }
            }
        });

        if (isEdit){
            setData();
        }

        return rootView;
    }

    private void setData() {
        inputCompany.setText(SalmanApplication.getCurrentUser().getCompany());
    }

    @Override
    public void checkInput(RegistrationCheckerCallback callback) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Harap perhatikan data yang anda input, terjadi kesalahan:\n");
        final StringBuilder job = new StringBuilder();

        int counter = 0;
        for (CheckBox checkBox: jobCheckbox) {
            if (checkBox.isChecked()) {
                job.append(checkBox.getText().toString()).append(", ");
                counter++;
            }
        }

        if (job.length() > 2) {
            job.delete(job.length()-2, job.length());
        }

        if (counter == 0 && !checkboxOthers.isChecked()) {
            stringBuilder.append("  - Minimal ada pekerjaan lainnya yang dimasukan\n");
            txtError.setText(stringBuilder.toString());
            txtError.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Terjadi kesalahan input", Toast.LENGTH_SHORT).show();
            callback.onFinishChecking(false);
            return;
        }

        if (checkboxOthers.isChecked()) {
            if (counter > 0)
                job.append(", ");
            job.append(inputOthers.getText().toString());
        }

        SalmanApplication.getCurrentUser().setJob(job.toString());
        SalmanApplication.getCurrentUser().setCompany(inputCompany.getText().toString());

        callback.onFinishChecking(true);
    }

    private void prepareJobOption(LinearLayout leftColumn, LinearLayout rightColumn, String data[]) {
        for (int i = 0; i < data.length; i++) {
            CheckBox checkBox = new CheckBox(getActivity());
            checkBox.setText(data[i]);
            if (isEdit) {
                if (SalmanApplication.getCurrentUser().getJob().equals(data[i])) {
                    checkBox.setChecked(true);
                }
            }

            if (i%2==0)
                leftColumn.addView(checkBox);
            else
                rightColumn.addView(checkBox);

            jobCheckbox.add(checkBox);
        }
        if (isEdit) {
            boolean other = true;
            for (String s : data) {
                if (SalmanApplication.currentUser.getJob().equals(s)){
                    other = false;
                }
            }
            if (other) {
                checkboxOthers.setChecked(true);
                inputOthers.setText(SalmanApplication.getCurrentUser().getJob());
                inputOthers.setEnabled(true);
                inputOthers.setFocusable(true);
            }
        }
    }

    @OnClick(R.id.txt_error)
    protected void hide() {
        txtError.setVisibility(View.GONE);
    }
}
