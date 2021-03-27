package com.wafflestudio.snutt2.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.wafflestudio.snutt2.R;
import com.wafflestudio.snutt2.SNUTTBaseActivity;
import com.wafflestudio.snutt2.adapter.IntroAdapter;

/**
 * Created by makesource on 2016. 3. 18..
 */
public class WelcomeActivity extends SNUTTBaseActivity {
    private ViewPager mViewPager;
    private IntroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_welcome);
        int type = getIntent().getIntExtra(INTENT_KEY_FRAGMENT_TYPE, 0);
        switch (type) {
            case 0:
                setFragment(SignInFragment.newInstance());
                break;
            case 1:
                setFragment(SignUpFragment.newInstance());
                break;
            default:
                break;
        }
    }

    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
    }
}
