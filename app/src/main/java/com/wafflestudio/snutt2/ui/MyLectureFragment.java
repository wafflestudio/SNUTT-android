package com.wafflestudio.snutt2.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wafflestudio.snutt2.R;
import com.wafflestudio.snutt2.SNUTTBaseFragment;
import com.wafflestudio.snutt2.adapter.MyLectureListAdapter;
import com.wafflestudio.snutt2.manager.LectureManager;
import com.wafflestudio.snutt2.model.Lecture;
import com.wafflestudio.snutt2.view.DividerItemDecoration;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class MyLectureFragment extends SNUTTBaseFragment implements LectureManager.OnLectureChangedListener{
    /**
    * The fragment argument representing the section number for this
    * fragment.
    */
    private static final String TAG = "MY_LECTURE_FRAGMENT" ;
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String DIALOG_DETAIL = "상세보기";
    private static final String DIALOG_SYLLABUS = "강의계획서";
    private static final String DIALOG_DELETE = "삭제";

    private LinearLayout placeholder;
    private RecyclerView recyclerView;
    private MyLectureListAdapter mAdapter;
    private List<Lecture> lectures;

    public MyLectureFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MyLectureFragment newInstance(int sectionNumber) {
        MyLectureFragment fragment = new MyLectureFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_lecture, container, false);
        setHasOptionsMenu(true);

        placeholder = (LinearLayout) rootView.findViewById(R.id.placeholder);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_lecture_recyclerView);
        lectures = LectureManager.getInstance().getLectures();
        mAdapter = new MyLectureListAdapter(lectures);
        mAdapter.setOnItemClickListener(new MyLectureListAdapter.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                Log.d(TAG, String.valueOf(position) + "-th item clicked!");
                getMainActivity().startLectureMain(position);
            }
        });
        mAdapter.setOnItemLongClickListener(new MyLectureListAdapter.LongClickListener() {
            @Override
            public void onLongClick(View v, final int position) {
                Log.d(TAG, String.valueOf(position) + "-th item long clicked!");
                final Lecture lecture = lectures.get(position);
                final CharSequence[] items = {DIALOG_DETAIL, DIALOG_SYLLABUS, DIALOG_DELETE};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(lecture.getCourse_title())
                       .setItems(items, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int index){
                                if (items[index].equals(DIALOG_DETAIL)) {
                                    getMainActivity().startLectureMain(position);
                                } else if (items[index].equals(DIALOG_SYLLABUS)) {
                                    startSyllabus(lecture.getCourse_number(), lecture.getLecture_number());
                                } else {
                                    LectureManager.getInstance().removeLecture(lecture.getId(), null);
                                }
                            }
                       });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.lecture_divider));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        placeholder.setVisibility(lectures.size() == 0 ? View.VISIBLE : View.GONE);
        return rootView;
    }

    @Override
    public void notifyLecturesChanged() {
        Log.d (TAG, "notify lecture changed called");
        mAdapter.notifyDataSetChanged();
        placeholder.setVisibility(lectures.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void notifySearchedLecturesChanged() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_my_lecture, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            //getMainActivity().startTableList();
            //Toast.makeText(getContext(), "custom lecture add clicked!!", Toast.LENGTH_SHORT).show();
            getMainActivity().startLectureMain();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        LectureManager.getInstance().removeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        LectureManager.getInstance().addListener(this);
        if (mAdapter != null) {
            // 강의 색상 변경시 fragment 이동 발생!
            mAdapter.notifyDataSetChanged();
        }
        placeholder.setVisibility(lectures.size() == 0 ? View.VISIBLE : View.GONE);
    }

    private void startSyllabus(String courseNumber, String lectureNumber) {
        LectureManager.getInstance().getCoursebookUrl(courseNumber, lectureNumber, new Callback<Map>() {
            public void success(Map map, Response response) {
                String url = (String) map.get("url");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
}
