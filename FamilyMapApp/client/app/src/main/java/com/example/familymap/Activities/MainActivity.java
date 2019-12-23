package com.example.familymap.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import com.example.familymap.Fragments.LoginFragment;
import com.example.familymap.Fragments.MapFragment;
import com.example.familymap.LoginListener;
import com.example.familymap.Model.DataSingleton;
import com.example.familymap.R;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

public class MainActivity extends AppCompatActivity implements LoginListener {
    private FragmentManager fm;
    private static final String TAG = "MainActivity";
    private DataSingleton singleton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        singleton = DataSingleton.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Iconify.with(new FontAwesomeModule());

        fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.login_fragment_container);

        if(fragment == null) {
            fragment = new LoginFragment();
            //if the singleton information either
            if(singleton.getAuthToken() != null && singleton.getPersons() != null && singleton.getEvents() != null && singleton.getUserPersonID() != null) {
                fragment = new MapFragment();
            }
            else {
                fragment = new LoginFragment();
            }
            fm.beginTransaction().add(R.id.login_fragment_container,fragment).commit();

        }
    }

   public void onLogin() {
        fm = getSupportFragmentManager();
        Fragment fragment = new MapFragment();
        fm.beginTransaction().replace(R.id.login_fragment_container,fragment).commit();
   }
    public void onLogout() {
        Log.d(TAG,"In logout");
        fm = getSupportFragmentManager();
        Log.d(TAG,"Got fragment support");
        Fragment fragment = new LoginFragment();
        Log.d(TAG,"Created login fragment");
        fm.beginTransaction().replace(R.id.login_fragment_container,fragment).commit();
        Log.d(TAG,"Replaced map with login fragment");
    }
}
