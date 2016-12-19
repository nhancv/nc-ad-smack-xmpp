package com.nhancv.hellosmack.ui.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.helper.NUtil;
import com.nhancv.npreferences.NPreferences;
import com.nhancv.xmpp.listener.XmppListener;
import com.nhancv.xmpp.XmppPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */

@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    @ViewById(R.id.etUser)
    EditText etUser;
    @ViewById(R.id.etPwd)
    EditText etPwd;
    @ViewById(R.id.checkBox)
    CheckBox checkBox;

    @AfterViews
    void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Login");
        }
        String userName = NPreferences.getInstance().getString("username", null);
        String passWord = NPreferences.getInstance().getString("password", null);
        if (userName != null && passWord != null) {
            login(userName, passWord);
        }
    }

    @Click(R.id.btSignin)
    void btSigninOnClick() {
        String userName = etUser.getText().toString();
        String passWord = etPwd.getText().toString();

        login(userName, passWord);
    }

    private void login(String userName, String passWord) {
        NUtil.aSyncTask(subscriber -> {
            try {
                XmppPresenter.getInstance().login(userName, passWord, new XmppListener.IXmppLoginListener() {
                    @Override
                    public void loginSuccess() {
                        if (checkBox.isChecked()) {
                            NPreferences.getInstance()
                                    .edit()
                                    .putString("username", etUser.getText().toString())
                                    .putString("password", etPwd.getText().toString());
                        } else {
                            NPreferences.getInstance().edit().clear();
                        }

                        MainActivity_.intent(LoginActivity.this).start();
                        finish();
                    }

                    @Override
                    public void loginError(Exception ex) {
                        Log.e(TAG, "loginError: " + ex);
                        showToast(ex.getMessage());
                        NPreferences.getInstance().edit().clear();
                    }
                });
            } catch (XMPPException | IOException | SmackException e) {
                e.printStackTrace();
                NUtil.runOnUi(() -> {
                    showToast(e.getMessage());
                });
            }
        });
    }

    @Click(R.id.btSignup)
    void btSignupOnClick() {
        String userName = etUser.getText().toString();
        String passWord = etPwd.getText().toString();

        NUtil.aSyncTask(subscriber -> {
            try {
                XmppPresenter.getInstance().createUser(userName, passWord, new XmppListener.IXmppCreateListener() {
                    @Override
                    public void createSuccess() throws SmackException.NotConnectedException {
                        Log.e(TAG, "createSuccess: ");
                        XmppPresenter.getInstance().getXmppConnector().terminalConnection();
                        NUtil.runOnUi(() -> {
                            btSigninOnClick();
                        });
                    }

                    @Override
                    public void createError(Exception ex) {
                        Log.e(TAG, "createError: " + ex);
                        showToast(ex.getMessage());
                    }
                });
            } catch (XMPPException | IOException | SmackException e) {
                e.printStackTrace();
                NUtil.runOnUi(() -> {
                    showToast(e.getMessage());
                });
            }
        });
    }

    void showToast(String msg) {
        NUtil.runOnUi(() -> {
            NUtil.showToast(this, msg);
        });
    }

}
