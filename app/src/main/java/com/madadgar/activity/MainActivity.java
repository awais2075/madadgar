package com.madadgar.activity;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.madadgar.R;
import com.madadgar.adapter.ViewPagerAdapter;
import com.madadgar.enums.UserType;
import com.madadgar.fragment.BloodFragment;
import com.madadgar.fragment.EmergencyFragment;
import com.madadgar.util.Util;

import java.util.List;

/*Main Activity
 * Implementing ViewPager inorder to show Fragments in Tabs*/
public class MainActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    /*Tabs Title Array*/
    private String[] tabTitleList = new String[]{"Emergency", "Blood", "Map"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Util.showToast(this, user.getUserEmail() + " : " + user.getUserType());
        initViews();
    }

    /*Getting View associated with this Activity i.e. xml layout*/
    @Override
    protected int getView() {
        return R.layout.activity_main;
    }

    /*Initializing Views defined in xml associated with this Activity*/
    @Override
    protected void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewPager);
        setUpViewPager(viewPager);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);


//        Snackbar.make(findViewById(R.id.layout_main), "You are LoggedIn as " + user.getUserType(), Snackbar.LENGTH_LONG).show();
    }

    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new EmergencyFragment(fireBaseDb, user), tabTitleList[0]);
        adapter.addFragment(new BloodFragment(fireBaseDb, user), tabTitleList[1]);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
    }

    /*Implementing click listeners of Views that are consuming onClick Event*/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /*case R.id.button_signOut:
                auth.signOut();
                finish();
                break;*/
        }
    }

    /*Not Required for this Activity*/
    @Override
    public void onSuccess(List list) {

    }


    /*Not Required for this Activity*/
    @Override
    public void onFailure(String message) {

    }

    /*Not Required for this Project*/
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /*Changing the Title of the toolbar of this Activity whenever user swipe tabs*/
    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                toolbar.setTitle(tabTitleList[position]);
                break;
            case 1:
                toolbar.setTitle(tabTitleList[position]);
                break;
            case 2:
                toolbar.setTitle(tabTitleList[position]);
                break;
        }
    }

    /*Not Required for this Project*/
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /*Option Menu specifically for this Activity containing options
     * Add Critic
     * View Profiles
     * SignOut*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        if (user.getUserType() == UserType.Admin) {
            menu.findItem(R.id.item_addStaff).setVisible(true);
        }
        return true;
    }

    /*Implementing method whenever user selects item form Option Menu*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_addStaff:
                startActivity(new Intent(this, SignUpActivity.class).putExtra("isUser", false));
                break;
            case R.id.item_signOut:
                auth.signOut();
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                break;
        }
        return true;
    }
}
