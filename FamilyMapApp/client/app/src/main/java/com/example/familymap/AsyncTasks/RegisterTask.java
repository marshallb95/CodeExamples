package com.example.familymap.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.familymap.LoginListener;
import com.example.familymap.Model.DataSingleton;
import com.example.familymap.Model.ServerProxy;
import com.example.familymap.Result.LoginRegisterResult;
import com.example.familymap.Result.Result;


public class RegisterTask extends AsyncTask<String,Void, Result> {
    private static final String TAG = "RegisterTask";
    private Context mContext;
    LoginListener listener;
    public RegisterTask(Context context, LoginListener listener) {
        mContext = context;
        this.listener = listener;
    }
    protected Result doInBackground(String... params) {
        ServerProxy server = new ServerProxy(params[0], params[1]);
        return server.register(params[2], params[3], params[4], params[5], params[6], params[7]);
    }
    protected void onPostExecute(Result result) {
        if(result.getMessage() == null) {
            LoginRegisterResult logResult = (LoginRegisterResult) result;
            DataSingleton singleton = DataSingleton.getInstance();
            singleton.setAuthToken(logResult.getAuthToken());
            singleton.setUserPersonID(logResult.getPersonID());
            //make new async task and get data
            Log.d(TAG,"Calling family data task");
            Log.d(TAG,"auth token " + logResult.getAuthToken());
            Log.d(TAG,"personID" + logResult.getPersonID());
            new FamilyDataTask(mContext,listener).execute(logResult.getHost(),logResult.getPort(), logResult.getAuthToken(), logResult.getPersonID());
        }
        else {
            Log.d(TAG,"onpost an error occurred");
            Log.d(TAG,"result message is: " + result.getMessage());
            Toast.makeText(mContext, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}


