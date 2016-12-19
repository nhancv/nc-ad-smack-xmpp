package com.nhancv.hellosmack.ui.activity;

import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.helper.NUtil;
import com.nhancv.hellosmack.ui.fragment.GroupFragment_;
import com.nhancv.hellosmack.ui.fragment.UsersFragment_;
import com.nhancv.npreferences.NPreferences;
import com.nhancv.xmpp.XmppPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.List;

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

    ViewPagerAdapter adapter;

    @AfterViews
    void initView() {
        setSupportActionBar(vToolbar);
        setupViewPager(vViewPager);
        vTabs.setupWithViewPager(vViewPager);
        initNavigationDrawer();
    }

    public void initNavigationDrawer() {
        vNavigation.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.logout:
                    vDrawer.closeDrawers();
                    NUtil.aSyncTask(subscriber -> {
                        //Clear preference
                        NPreferences.getInstance().edit().clear();
                        //Terminal current connection
                        try {

                            XmppPresenter.getInstance().logout();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                        //Transmit to login screen
                        LoginActivity_.intent(MainActivity.this).start();
                        finish();
                    });
                    break;
            }
            return true;
        });
        View header = vNavigation.getHeaderView(0);
        TextView tvEmail = (TextView) header.findViewById(R.id.tvEmail);
        tvEmail.setText(XmppPresenter.getInstance().getCurrentUser());

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

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UsersFragment_(), "Users");
        adapter.addFragment(new GroupFragment_(), "Group");
        viewPager.setAdapter(adapter);
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
