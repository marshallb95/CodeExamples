package com.example.familymap.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.familymap.Model.DataSingleton;
import com.example.familymap.R;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "Settings";
    private Switch mLifeStorySwitch;
    private Switch mFamilyTreeSwitch;
    private Switch mSpouseSwitch;
    private Switch mFatherSwitch;
    private Switch mMotherSwitch;
    private Switch mMaleSwitch;
    private Switch mFemaleSwitch;
    private LinearLayout mLogout;
    private DataSingleton singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        singleton = DataSingleton.getInstance();
        mLifeStorySwitch = (Switch) findViewById(R.id.life_stories_switch);

        mLifeStorySwitch.setChecked(singleton.getLifeStoryLines());
        mLifeStorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                singleton.setLifeStoryLines(isChecked);
                //may need to add functionality to refilter later
            }
        });

        mFamilyTreeSwitch = (Switch) findViewById(R.id.family_tree_switch);
        mFamilyTreeSwitch.setChecked(singleton.getFamilyTreeLines());
        mFamilyTreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                singleton.setFamilyTreeLines(isChecked);
                //may need to add functionality to refilter later
            }
        });

        mSpouseSwitch = (Switch) findViewById(R.id.spouse_switch);
        mSpouseSwitch.setChecked(singleton.getSpouseLines());
        mSpouseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                singleton.setSpouseLines(isChecked);
            }
        });

        mFatherSwitch = (Switch) findViewById(R.id.father_switch);
        mFatherSwitch.setChecked(singleton.getFatherSide());
        mFatherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                singleton.setFatherSide(isChecked);
            }
        });

        mMotherSwitch = (Switch) findViewById(R.id.mother_switch);
        mMotherSwitch.setChecked(singleton.getMotherSide());
        mMotherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                singleton.setMotherSide(isChecked);
            }
        });

        mMaleSwitch = (Switch) findViewById(R.id.male_switch);
        mMaleSwitch.setChecked(singleton.getMaleEvents());
        mMaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                singleton.setMaleEvents(isChecked);
            }
        });

        mFemaleSwitch = (Switch) findViewById(R.id.female_switch);
        mFemaleSwitch.setChecked(singleton.getFemaleEvents());
        mFemaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                singleton.setFemaleEvents(isChecked);
            }
        });

        mLogout = (LinearLayout) findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"logout touched");
                //MainActivity parent = (MainActivity) getParent();
                //Log.d(TAG,"got parent");
                //parent.onLogout();
                //Log.d(TAG,"called parent logout");
                singleton.reset();
                Log.d(TAG,"reset singleton");
                Intent intent = new Intent();
                intent.putExtra("logout",true);
                Log.d(TAG,"set logout variable");
                setResult(RESULT_OK,intent);
                Log.d(TAG,"Result ok");
                finish();
                Toast.makeText(SettingsActivity.this,"Logout!",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
