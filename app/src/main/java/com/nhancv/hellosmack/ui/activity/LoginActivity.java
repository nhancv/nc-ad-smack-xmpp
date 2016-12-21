package com.nhancv.hellosmack.ui.activity;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.helper.NUtil;
import com.nhancv.npreferences.NPreferences;
import com.nhancv.xmpp.XmppPresenter;
import com.nhancv.xmpp.listener.XmppListener;

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

    @ViewById(R.id.vToolbar)
    Toolbar vToolbar;
    @ViewById(R.id.inputLayoutUser)
    TextInputLayout inputLayoutUser;
    @ViewById(R.id.inputLayoutPassword)
    TextInputLayout inputLayoutPassword;
    @ViewById(R.id.etUser)
    EditText etUser;
    @ViewById(R.id.etPwd)
    EditText etPwd;
    @ViewById(R.id.checkBox)
    CheckBox checkBox;

    @AfterViews
    void initView() {

        setupToolbar(vToolbar, "Login");
        String userName = NPreferences.getInstance().getString("username", null);
        String passWord = NPreferences.getInstance().getString("password", null);
        if (userName != null && passWord != null) {
            login(userName, passWord);
        }

        etUser.addTextChangedListener(new ValidateWatcher(etUser));
        etPwd.addTextChangedListener(new ValidateWatcher(etPwd));

    }

    private void submitForm() {
        if (!validateUser()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        String userName = etUser.getText().toString();
        String passWord = etPwd.getText().toString();

        login(userName, passWord);
    }

    private boolean validateUser() {
        if (etUser.getText().toString().trim().isEmpty()) {
            inputLayoutUser.setError("Email is required!");
            requestFocus(etUser);
            return false;
        } else {
            inputLayoutUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (etPwd.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError("Password is required!");
            requestFocus(etPwd);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void setupToolbar(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Click(R.id.btSignin)
    void btSigninOnClick() {
        submitForm();
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

    private class ValidateWatcher implements TextWatcher {

        private View view;

        private ValidateWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.etUser:
                    validateUser();
                    break;
                case R.id.etPwd:
                    validatePassword();
                    break;
            }
        }
    }

}
