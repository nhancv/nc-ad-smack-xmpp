package com.nhancv.hellosmack.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.nhancv.hellosmack.App;
import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.XmppHandler;
import com.nhancv.hellosmack.bus.LoginBus;
import com.nhancv.npreferences.NPreferences;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.etUser)
    EditText etUser;
    @BindView(R.id.etPwd)
    EditText etPwd;
    @BindView(R.id.checkBox)
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.bus.register(this);
        String userName = NPreferences.getInstance().getString("username", null);
        String passWord = NPreferences.getInstance().getString("password", null);
        if (userName != null && passWord != null) {
            XmppHandler.getInstance().init(userName, passWord).createConnection();
        }
    }

    @Override
    protected void onPause() {
        App.bus.unregister(this);
        super.onPause();
    }

    @OnClick(R.id.btSignin)
    public void onClickLoginBtn(View view) {
        String userName = etUser.getText().toString();
        String passWord = etPwd.getText().toString();
        XmppHandler.getInstance().init(userName, passWord).createConnection();
    }

    @Subscribe
    public void loginBusListener(LoginBus loginBus) {
        switch (loginBus.code) {
            case LoginBus.SUCCESS:
                if (checkBox.isChecked()) {
                    NPreferences.getInstance()
                            .edit()
                            .putString("username", etUser.getText().toString())
                            .putString("password", etPwd.getText().toString())
                            .apply();
                } else {
                    NPreferences.getInstance().edit().clear().apply();
                }

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case LoginBus.ERROR:
                NPreferences.getInstance().edit().clear().apply();
                Toast.makeText(LoginActivity.this, (CharSequence) loginBus.data, Toast.LENGTH_SHORT).show();
                break;
        }
    }

}