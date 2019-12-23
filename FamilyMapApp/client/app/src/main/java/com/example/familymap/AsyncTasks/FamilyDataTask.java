package com.example.familymap.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.familymap.LoginListener;
import com.example.familymap.Model.DataSingleton;
import com.example.familymap.Model.ServerProxy;
import com.example.familymap.Result.EventResult;
import com.example.familymap.Result.PersonResult;
import com.example.familymap.Result.Result;

public class FamilyDataTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "FamilyDataTask";
    private Context mContext;
    LoginListener listener;

    public FamilyDataTask(Context context, LoginListener listener) {
        mContext = context;
        this.listener = listener;
    }
    protected String doInBackground(String... params) {
        ServerProxy server = new ServerProxy(params[0], params[1]);
        DataSingleton singleton = DataSingleton.getInstance();
        Result result = server.getPeople(singleton.getAuthToken(), singleton.getUserPersonID());

        if(result.getMessage() == null) {
            PersonResult pResult = (PersonResult) result;
            singleton.setPersons(pResult.getPersons());
            result = server.getEvents(singleton.getAuthToken());

            if (result.getMessage() == null) {
                EventResult eResult = (EventResult) result;
                singleton.setEvents(eResult.getEvents());
                //sort and accomodate data appropriately
                return null;
            }
            else {
                return result.getMessage();
            }


        }
        else {
            return result.getMessage();
        }
    }
    protected void onPostExecute(String resultString) {
        if(resultString == null) {
            DataSingleton singleton = DataSingleton.getInstance();
            singleton.prepareData();
            listener.onLogin();
        }
        Log.d(TAG,"result is " + resultString);
        Toast.makeText(mContext,resultString,Toast.LENGTH_SHORT).show();
    }
}

