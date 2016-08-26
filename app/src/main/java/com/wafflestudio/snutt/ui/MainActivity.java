package com.wafflestudio.snutt.ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseActivity;
import com.wafflestudio.snutt.manager.TagManager;
import com.wafflestudio.snutt.ui.adapter.SectionsPagerAdapter;
import com.wafflestudio.snutt.manager.LectureManager;
import com.wafflestudio.snutt.manager.PrefManager;
import com.wafflestudio.snutt.manager.TableManager;
import com.wafflestudio.snutt.model.Table;

import dalvik.annotation.TestTarget;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends SNUTTBaseActivity {

    private static final String TAG = "MainActivity" ;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // 1. token 의 유무 검사
        if (PrefManager.getInstance().getPrefKeyXAccessToken() == null) {
            // 로그인 창으로 이동
            startWelcome();
            finish();
        }

        // 2. 앱 내부에 저장된 시간표 뛰어주기
        // TODO : 저장된 정보를 불러와 보여주기, 없으면 empty상태로 띄어준다.
        String json = PrefManager.getInstance().getCurrentTable();
        if (json != null) {
            Table table = new Gson().fromJson(json, Table.class);
            getSupportActionBar().setTitle(table.getTitle());
            LectureManager.getInstance().setLectures(table.getLecture_list());
        }

        // 3. 서버에서 시간표 정보 얻어오기
        // TODO : 서버에서 마지막에 본 시간표 정보 받아오기
        String id = PrefManager.getInstance().getLastViewTableId();
        if (id != null) {
            TableManager.getInstance().getTableById(id, new Callback<Table>() {
                @Override
                public void success(Table table, Response response) {
                    getSupportActionBar().setTitle(table.getTitle());
                }
                @Override
                public void failure(RetrofitError error) {
                }
            });
        } else {
            // 처음 로그인한 경우 -> 서버에서 default값을 요청
            TableManager.getInstance().getDefaultTable(new Callback<Table>() {
                @Override
                public void success(Table table, Response response) {
                    // default 가 존재하는 경우
                    getSupportActionBar().setTitle(table.getTitle());
                }

                @Override
                public void failure(RetrofitError error) {
                    // default가 존재하지 않거나, network error 인 경우
                    getSupportActionBar().setTitle("empty table");
                }
            });
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // TableListActivity에서 들어 오는 경우 -> 선택한 table 을 보여줘야 함
        String id = null;
        if (intent.getExtras() != null) { // intent의 강의 id 받아오기
            id = intent.getExtras().getString(INTENT_KEY_TABLE_ID);
        }
        if (id == null) {
            Log.e(TAG, "intent has no table id!!");
            return ;
        }
        // 서버에서 받아와서 다시 그리기
        TableManager.getInstance().getTableById(id, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                getSupportActionBar().setTitle(table.getTitle());
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       // int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       // if (id == R.id.action_settings) {
       //     return true;
        //}

        return super.onOptionsItemSelected(item);
    }

}
