package com.softclicker.android.student;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.softclicker.android.Constants;
import com.softclicker.android.R;
import com.softclicker.android.apn.ApnData;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by User on 3/19/2016.
 */
public class StudentAdapter extends ArrayAdapter<String> {

    private AlertDialog studentSelectDialog;
    private SharedPreferences sharedPref;

    public StudentAdapter(Context context, AlertDialog studentSelectDialog, ArrayList<String> data) {
        super(context, 0, data);
        this.studentSelectDialog = studentSelectDialog;
        this.sharedPref = context.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final String data = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_student, parent, false);
        }

        RadioButton radioButton = (RadioButton) convertView.findViewById(R.id.radio_button_student);
        radioButton.setText(data);
        final String selectedStudent = sharedPref.getString(Constants.SELECTED_STUDENT, "");
        radioButton.setChecked(selectedStudent.equals(data));

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putString(Constants.SELECTED_STUDENT, data);
                prefEditor.apply();
                studentSelectDialog.dismiss();
            }
        });

        ImageView imageView = (ImageView) convertView.findViewById(R.id.button_remove_student);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONArray updatedArray = new JSONArray();
                    JSONArray studentsJsonArray = new JSONArray(sharedPref.getString(Constants.STUDENT_IDS, "[]"));
                    for (int i =0; i < studentsJsonArray.length(); i++){
                        if (!data.equals(studentsJsonArray.getString(i))){
                            updatedArray.put(studentsJsonArray.get(i));
                        }
                    }
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putString(Constants.UPDATED_STUDENT_IDS, updatedArray.toString());
                    prefEditor.apply();
                    studentSelectDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }
}
