package com.wafflestudio.snutt.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;
import com.wafflestudio.snutt.manager.UserManager;
import com.wafflestudio.snutt.model.SettingsItem;
import com.wafflestudio.snutt.model.User;
import com.wafflestudio.snutt.ui.adapter.SettingsAdapter;
import com.wafflestudio.snutt.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;

/**
 * Created by makesource on 2017. 1. 24..
 */

public class AccountFragment extends SNUTTBaseFragment {
    private static final String TAG = "ACCOUNT_FRAGMENT";
    private List<SettingsItem> lists;
    private SettingsAdapter adapter;
    private LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lists = new ArrayList<SettingsItem>();
        lists.add(new SettingsItem(SettingsItem.Type.Header));
        lists.add(new SettingsItem("아이디", "test", SettingsItem.Type.ItemTitle));
        lists.add(new SettingsItem("비밀번호 변경", SettingsItem.Type.ItemTitle));
        lists.add(new SettingsItem(SettingsItem.Type.Header));
        lists.add(new SettingsItem("페이스북 연동", SettingsItem.Type.ItemTitle));
        lists.add(new SettingsItem(SettingsItem.Type.Header));
        lists.add(new SettingsItem("이메일", "test@gmail.com", SettingsItem.Type.ItemTitle));
        lists.add(new SettingsItem("이메일 변경", SettingsItem.Type.ItemTitle));
        lists.add(new SettingsItem(SettingsItem.Type.Header));
        lists.add(new SettingsItem("회원탈퇴", SettingsItem.Type.ItemTitle));
        lists.add(new SettingsItem(SettingsItem.Type.Header));

        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adapter = new SettingsAdapter(getActivity(), lists);
        adapter.setOnItemClickListener(new SettingsAdapter.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                Log.d(TAG, String.valueOf(position) + "-th item clicked!");
                switch (position) {
                    case 2: // change password
                        final View layout = inflater.inflate(R.layout.dialog_change_password, null);

                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("비밀번호 변경");
                        alert.setView(layout);
                        alert.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String oldPassword = ((EditText)layout.findViewById(R.id.now_password)).getText().toString();
                                String newPassword = ((EditText)layout.findViewById(R.id.new_password)).getText().toString();
                                String newPasswordConfirm = ((EditText)layout.findViewById(R.id.new_password_confirm)).getText().toString();

                                if (!newPassword.equals(newPasswordConfirm)) {
                                    Toast.makeText(getContext(), "새 비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                                }
                                // Do something with value!

                            }
                        }).setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                });

                        alert.show();
                        break;
                    case 7:
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View layout2 = inflater.inflate(R.layout.dialog_change_email, null);
                        AlertDialog.Builder alert2 = new AlertDialog.Builder(getContext());
                        alert2.setTitle("이메일 변경");
                        alert2.setView(layout2);
                        alert2.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String email = ((EditText)layout2.findViewById(R.id.email)).getText().toString();


                                // Do something with value!

                            }
                        }).setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                });

                        alert2.show();
                    default:
                        break;
                }
            }
        });

        UserManager.getInstance().getUserInfo(new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                lists.get(1).setDetail(user.getLocal_id());
                lists.get(6).setDetail(user.getEmail());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.account_recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return rootView;
    }
}
