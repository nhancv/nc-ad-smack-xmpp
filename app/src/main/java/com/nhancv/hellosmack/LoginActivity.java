package com.nhancv.hellosmack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nhancv.hellosmack.bus.LoginBus;
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
    }

    @Override
    protected void onPause() {
        App.bus.unregister(this);
        super.onPause();
    }

    @OnClick(R.id.btSignin)
    public void onClickLoginBtn(View view) {
        try {
            String userName = etUser.getText().toString();
            String passWord = etPwd.getText().toString();
            XmppHandler.getInstance().init(userName, passWord).createConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void loginBusListener(LoginBus loginBus) {
        switch (loginBus.code) {
            case LoginBus.SUCCESS:
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case LoginBus.ERROR:
                Toast.makeText(LoginActivity.this, (CharSequence) loginBus.data, Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
