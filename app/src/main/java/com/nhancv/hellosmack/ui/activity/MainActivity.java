package com.nhancv.hellosmack.ui.activity;

import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.bus.InvitationBus;
import com.nhancv.hellosmack.bus.MessageBus;
import com.nhancv.hellosmack.bus.RosterBus;
import com.nhancv.hellosmack.bus.XmppConnBus;
import com.nhancv.hellosmack.helper.RxHelper;
import com.nhancv.hellosmack.helper.XmppService;
import com.nhancv.hellosmack.helper.XmppService_;
import com.nhancv.hellosmack.ui.fragment.GroupFragment;
import com.nhancv.hellosmack.ui.fragment.GroupFragment_;
import com.nhancv.hellosmack.ui.fragment.UsersFragment;
import com.nhancv.hellosmack.ui.fragment.UsersFragment_;
import com.nhancv.npreferences.NPreferences;
import com.nhancv.xmpp.XmppPresenter;
import com.nhancv.xmpp.model.BaseError;
import com.nhancv.xmpp.model.BaseInvitation;
import com.nhancv.xmpp.model.BaseMessage;
import com.nhancv.xmpp.model.BaseRoster;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;
import org.jxmpp.util.XmppStringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.nhancv.hellosmack.R.id.logout;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @ViewById(R.id.vToolbar)
    Toolbar vToolbar;
    @ViewById(R.id.vTabs)
    TabLayout vTabs;
    @ViewById(R.id.vViewPager)
    ViewPager vViewPager;
    @ViewById(R.id.vDrawer)
    DrawerLayout vDrawer;
    @ViewById(R.id.vNavigation)
    NavigationView vNavigation;
    @ViewById(R.id.tvName)
    TextView tvName;

    ViewPagerAdapter adapter;
    UsersFragment usersFragment = new UsersFragment_();
    GroupFragment groupFragment = new GroupFragment_();

    @AfterViews
    void initView() {
        setupToolbar(vToolbar, "Main activity");
        setupViewPager(vViewPager);
        vTabs.setupWithViewPager(vViewPager);
        initNavigationDrawer();
    }

    @Subscribe
    public void invitationSubscribe(InvitationBus invitationBus) {
        BaseInvitation invitation = invitationBus.getData();
        Log.d(TAG, "invitationSubscribe: Entered invitation handler... " + invitation.getMessage());
        BaseError error = XmppPresenter.getInstance().joinRoom(invitation.getRoom(), participantPresence -> {
            groupFragment.updateAdapter();
            showToast("processPresence: " + participantPresence.getJid() + " " + participantPresence.getRole());
        }, XmppPresenter.getInstance().getCurrentUser());
        if (error.isError()) {
            showToast("error: " + error.getMessage());
        } else {
            showToast("invitationSubscribe: auto accepted");
            groupFragment.updateAdapter();
        }
    }

    @Subscribe
    public void xmppConnSubscribe(XmppConnBus xmppConnBus) {
        switch (xmppConnBus.getType()) {
            case CLOSE_ERROR:
                showToast(((Exception) xmppConnBus.getData()).getMessage());
                logout();
                break;
            default:
                showToast(xmppConnBus.getType().name());
                break;

        }
    }

    @Subscribe
    public void messageSubscribe(MessageBus messageBus) {
        BaseMessage baseMessage = (BaseMessage) messageBus.getData();
        if (baseMessage != null) {
            Log.d(TAG, "messageSubscribe: " + baseMessage.getMessage());
        }
        usersFragment.updateAdapter();
        groupFragment.updateAdapter();
    }

    @Subscribe
    public void rosterSubscribe(RosterBus rosterBus) {
        BaseRoster baseRoster = ((BaseRoster) rosterBus.getData());
        String status = (baseRoster != null ? baseRoster.getName() + " -> " + baseRoster.getPresence().getType() : null);
        if (status != null) {
            Log.d(TAG, "rosterSubscribe: " + status);
        }
        usersFragment.updateAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        XmppService.getBus().register(this);
        if (XmppPresenter.getInstance().isConnected()) {
            groupFragment.updateAdapter();
            usersFragment.updateAdapter();
        } else {
            logout();
        }
    }

    @Override
    protected void onPause() {
        XmppService.getBus().unregister(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        XmppService_.intent(getApplication()).stop();
        super.onDestroy();
    }

    private void setupToolbar(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    public void initNavigationDrawer() {
        vNavigation.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {
                case logout:
                    vDrawer.closeDrawers();
                    logout();
                    break;
            }
            return true;
        });
        View header = vNavigation.getHeaderView(0);
        TextView tvName = (TextView) header.findViewById(R.id.tvName);
        tvName.setText(XmppStringUtils.parseBareJid(XmppPresenter.getInstance().getCurrentUser()));

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, vDrawer, vToolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        vDrawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void logout() {
        RxHelper.aSyncTask(subscriber -> {
            //Clear preference
            NPreferences.getInstance().edit().clear();
            //Terminal current connection
            XmppPresenter.getInstance().logout();
            //Stop service
            XmppService_.intent(getApplication()).stop();
            //Transmit to login screen
            LoginActivity_.intent(MainActivity.this).start();
            finish();
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(usersFragment, "Users");
        adapter.addFragment(groupFragment, "Group");
        viewPager.setAdapter(adapter);
    }


    public void showToast(String msg) {
        RxHelper.runOnUi(() -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
