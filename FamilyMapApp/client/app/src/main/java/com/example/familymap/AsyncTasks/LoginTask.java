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
import com.google.gson.Gson;

public class LoginTask extends AsyncTask<String,Void, Result> {
    private Gson gson = new Gson();
    private static final String TAG = "LoginTask";
    private Context mContext;
    private LoginListener listener;
    public LoginTask(Context context,LoginListener listener) {
        mContext = context;
        this.listener = listener;
    }
    protected Result doInBackground(String... params) {
        ServerProxy server = new ServerProxy(params[0], params[1]);
        return server.login(params[2], params[3]);
    }
    protected void onPostExecute(Result result) {
        if(result.getMessage() == null) {
            LoginRegisterResult logResult = (LoginRegisterResult) result;
            DataSingleton singleton = DataSingleton.getInstance();
            singleton.setAuthToken(logResult.getAuthToken());
            singleton.setUserPersonID(logResult.getPersonID());
            Log.d(TAG,"singleton auth token is now: " + singleton.getAuthToken());
            //make new async task and get data
            Log.d(TAG,"Calling family data task");
            new FamilyDataTask(mContext,this.listener).execute(logResult.getHost(),logResult.getPort());
        }
        else {
            Log.d(TAG,"onpost an error occurred");
            Log.d(TAG,"result message is: " + result.getMessage());
            Toast.makeText(mContext, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
