package com.nhancv.hellosmack;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private XmppService mService;
    private View view;
    private boolean mBounded;
    private final ServiceConnection mConnection = new ServiceConnection() {

        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name,
                                       final IBinder service) {
            mService = ((LocalBinder<XmppService>) service).getService();
            mBounded = true;
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mService = null;
            mBounded = false;
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Click Handler for Login Button
    public void onClickLoginBtn(View view) {
        try {
            EditText userId = (EditText) findViewById(R.id.tvUser);
            EditText userPwd = (EditText) findViewById(R.id.tvPwd);
            String userName = userId.getText().toString();
            String passWord = userPwd.getText().toString();
            Intent intent = new Intent(getBaseContext(), XmppService.class);
            intent.putExtra("user", userName);
            intent.putExtra("pwd", passWord);
            startService(intent);


            //mService.connectConnection(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
