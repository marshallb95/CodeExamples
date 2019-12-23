package com.example.familymap.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.familymap.AsyncTasks.LoginTask;
import com.example.familymap.AsyncTasks.RegisterTask;
import com.example.familymap.LoginListener;
import com.example.familymap.Model.Login;
import com.example.familymap.R;
import com.google.gson.Gson;

public class LoginFragment extends Fragment {
    private Gson gson;
    private static final String TAG = "LoginFragment";
    private Login mLogin;
    private EditText mHost;
    private EditText mPort;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private RadioGroup mGenderGroup;
    private Button mSignInButton;
    private Button mRegisterButton;
    LoginListener listener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLogin = new Login();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View v = inflater.inflate(R.layout.fragment_login, container, false);
      mSignInButton = (Button) v.findViewById(R.id.sign_in);
      mSignInButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              new LoginTask(getActivity(),listener).execute(mLogin.getHost(), mLogin.getPort(), mLogin.getUsername(),mLogin.getPassword());
          }
      });
      mRegisterButton = (Button) v.findViewById(R.id.register);
      mRegisterButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              new RegisterTask(getActivity(),listener).execute(mLogin.getHost(), mLogin.getPort(), mLogin.getUsername(), mLogin.getPassword(),
                      mLogin.getEmail(), mLogin.getFirstName(), mLogin.getLastName(),mLogin.getGender());
          }
      });
      mHost = (EditText) v.findViewById(R.id.host_name);
      mHost.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              //This space intentionally left blank
          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLogin.setHost(s.toString());
          }

          @Override
          public void afterTextChanged(Editable s) {
              validate();
          }
      });

      mPort = (EditText) v.findViewById(R.id.port_number);
      mPort.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              //This space intentionally left blank
          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLogin.setPort(s.toString());
          }

          @Override
          public void afterTextChanged(Editable s) {
              validate();
          }
      });

      mUsername = (EditText) v.findViewById(R.id.username);
      mUsername.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              //This space intentionally left blank
          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
              mLogin.setUsername(s.toString());
          }

          @Override
          public void afterTextChanged(Editable s) {
              validate();
          }
      });

      mPassword = (EditText) v.findViewById(R.id.password);
      mPassword.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              //This space intentionally left blank

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            mLogin.setPassword(s.toString());
          }

          @Override
          public void afterTextChanged(Editable s) {
            validate();
          }
      });

      mFirstName = (EditText) v.findViewById(R.id.first_name);
      mFirstName.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              //This space intentionally left blank

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            mLogin.setFirstName(s.toString());
          }

          @Override
          public void afterTextChanged(Editable s) {
            validate();
          }
      });

      mLastName = (EditText) v.findViewById(R.id.last_name);
      mLastName.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              //This space intentionally left blank

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            mLogin.setLastName(s.toString());
          }

          @Override
          public void afterTextChanged(Editable s) {
            validate();
          }
      });

      mEmail = (EditText) v.findViewById(R.id.email);
      mEmail.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              //This space intentionally left blank
          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
              mLogin.setEmail(s.toString());
          }

          @Override
          public void afterTextChanged(Editable s) {
            validate();
          }
      });

      mGenderGroup = (RadioGroup) v.findViewById(R.id.gender_group);
      mGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(RadioGroup group, int checkedId) {
              RadioButton checked = (RadioButton) group.findViewById(checkedId);
              boolean isChecked = checked.isChecked();
              Log.d(TAG,"Radio button was clicked");
              if(isChecked) {
                  String radioGender = checked.getText().toString();
                  Log.d(TAG, radioGender);
                  if (radioGender.equals("Male")) {
                      mLogin.setGender("m");
                  } else if (radioGender.equals("Female")) {
                      mLogin.setGender("f");
                  }
              }
              validate();
          }
      });
      validate();
      return v;
    }
    public void validate() {
        if(mLogin.getHost() == null || mLogin.getPort() == null || mLogin.getUsername() == null || mLogin.getPassword() == null) {
            mSignInButton.setEnabled(false);
        }
        else if(mLogin.getHost().equals("") || mLogin.getPort().equals("") || mLogin.getUsername().equals("") || mLogin.getPassword().equals("")){
            mSignInButton.setEnabled(false);
        }
        else {
            mSignInButton.setEnabled(true);
        }

        if(mLogin.getHost() == null || mLogin.getPort() == null || mLogin.getUsername() == null
         || mLogin.getPassword() == null || mLogin.getFirstName() == null || mLogin.getLastName() == null
        || mLogin.getEmail() == null || mLogin.getGender() == null) {
            Log.d(TAG,"an object is null");
            Log.d(TAG,"host "+mLogin.getHost());
            Log.d(TAG,"port "+mLogin.getPort());
            Log.d(TAG,"username "+mLogin.getUsername());
            Log.d(TAG,"password "+mLogin.getPassword());
            Log.d(TAG,"first name "+mLogin.getFirstName());
            Log.d(TAG,"last name "+mLogin.getLastName());
            Log.d(TAG,"email "+mLogin.getEmail());
            Log.d(TAG,"gender "+mLogin.getGender());

            mRegisterButton.setEnabled(false);
        }
        else if(mLogin.getHost().equals("") || mLogin.getPort().equals("") || mLogin.getUsername().equals("") || mLogin.getPassword().equals("")
        || mLogin.getFirstName().equals("") || mLogin.getLastName().equals("") || mLogin.getEmail().equals("") || mLogin.getGender().equals("")) {
            Log.d(TAG, "A string is empty");
            mRegisterButton.setEnabled(false);
        }
        else {
            mRegisterButton.setEnabled(true);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (LoginListener) context;
    }
}
