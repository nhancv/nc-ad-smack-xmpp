package com.nhancv.hellosmack.ui.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nhancv.hellosmack.R;
import com.nhancv.hellosmack.helper.NUtil;
import com.nhancv.hellosmack.ui.fragment.GroupFragment;
import com.nhancv.hellosmack.ui.fragment.UsersFragment;
import com.nhancv.hellosmack.xmpp.XmppPresenter;
import com.nhancv.npreferences.NPreferences;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
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
    @ViewById(R.id.btFab)
    FloatingActionButton btFab;
    ViewPagerAdapter adapter;
    int pageSelected = 0;

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
                            XmppPresenter.getInstance().getXmppConnector().terminalConnection();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                        //Transmit to login screen
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                    break;
            }
            return true;
        });
        View header = vNavigation.getHeaderView(0);
        TextView tvEmail = (TextView) header.findViewById(R.id.tvEmail);
        tvEmail.setText(XmppPresenter.getInstance().getXmppConnector().getConnection().getUser());

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
        adapter.addFragment(new UsersFragment(), "Users");
        adapter.addFragment(new GroupFragment(), "Group");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageSelected = position;
                switch (position) {
                    case 0:
                        Log.e(TAG, "onPageSelected: " + pageSelected);
                        btFab.show();
                        break;

                    default:
                        btFab.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Click(R.id.btFab)
    void btFagOnClick() {
        Fragment fragment = adapter.getItem(pageSelected);
        if (fragment instanceof UsersFragment) {
            ((UsersFragment) fragment).btAddContactOnClick();
        }
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
