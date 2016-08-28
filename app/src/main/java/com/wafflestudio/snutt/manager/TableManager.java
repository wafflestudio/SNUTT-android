package com.wafflestudio.snutt.manager;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wafflestudio.snutt.SNUTTApplication;
import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.model.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class TableManager {

    private static final String TAG = "TABLE_MANAGER" ;

    private Table current;
    private List<Table> tables;
    private Map<String, Table> tableMap;
    private SNUTTApplication app;

    private static TableManager singleton;

    /**
     * TableManager 싱글톤
     */

    private TableManager(SNUTTApplication app) {
        this.app = app;
        this.tables = new ArrayList<>();
        this.tableMap = new HashMap<>();
    }

    public static TableManager getInstance(SNUTTApplication app) {
        if(singleton == null) {
            singleton = new TableManager(app);
        }
        return singleton;
    }

    public static TableManager getInstance() {
        if (singleton == null) Log.e(TAG, "This method should not be called at this time!!");
        return singleton;
    }

    public void getTableList(final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        app.getRestService().getTableList(token, new Callback<List<Table>>() {
            @Override
            public void success(List<Table> table_list, Response response) {
                Log.d(TAG, "get table list request success!");
                tables.clear();
                tableMap.clear();
                for (Table table : table_list) addTable(table);
                if (callback != null) callback.success(tables, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "get table list request failed..!");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void postTable(int year, int semester, String title, final Callback<List<Table>> callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        Map query = new HashMap();
        query.put("year", year);
        query.put("semester", semester);
        query.put("title", title);
        app.getRestService().postTable(token, query, new Callback<List<Table>>() {
            @Override
            public void success(List<Table> table_list, Response response) {
                Log.d(TAG, "post new table request success!!");
                tables.clear();
                tableMap.clear();
                for (Table table : table_list) addTable(table);
                if (callback != null) callback.success(tables, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "post new table request failed..!");
                if (callback != null) callback.failure(error);
            }
        });
    }

    /*public void setTableList(List<Table> tables) {
        this.tables = tables;
    }*/

    public void getTableById(String id, final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        app.getRestService().getTableById(token, id, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                Log.d(TAG, "get table by id success");
                LectureManager.getInstance().setLectures(table.getLecture_list());
                PrefManager.getInstance().updateNewTable(table);
                TagManager.getInstance().updateNewTag(table.getYear(), table.getSemester());
                if (callback != null) callback.success(table, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "get table by id is failed!");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void getDefaultTable(final Callback<Table> callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        app.getRestService().getRecentTable(token, new Callback<Table>() {
            @Override
            public void success(Table table, Response response) {
                Log.d(TAG, "get recent table request success");
                LectureManager.getInstance().setLectures(table.getLecture_list());
                PrefManager.getInstance().updateNewTable(table);
                TagManager.getInstance().updateNewTag(table.getYear(), table.getSemester());
                if (callback != null) callback.success(table, response);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "get recent table request failed!");
                if (callback != null) callback.failure(error);
            }
        });

    }

    public void addTable(Table table) {
        tables.add(table);
        tableMap.put(table.getId(), table);
        Collections.sort(tables);
    }

    public void updateTables(Table table) {
        // TODO : (SeongWon) server에 update 요청 날리기

    }

    /*private void getDefaultTable() {

        Lecture sample = new Lecture();
        sample.setClassification("교양");
        sample.setDepartment("건설환경공학부");
        sample.setAcademic_year("2학년");
        sample.setCourse_number("035.001");
        sample.setCourse_title("컴퓨터의 개념 및 실습");
        sample.setLecture_number("001");
        sample.setLocation("301-1/301-2");
        sample.setCredit(3);
        sample.setClass_time("월(6-2)/수(6-2)");
        sample.setInstructor("몰라아직 ㅜㅜ");
        sample.setQuota(60);
        sample.setEnrollment(0);
        sample.setRemark("건설환경공학부만 수강가능");
        sample.setCategory("foundation_computer");
        sample.setColorIndex(1);

        List<Lecture> sampleList = new ArrayList<>();
        sampleList.add(sample);

        tables = new ArrayList<>();
        tables.add(new Table("0",2016,1,"이번학기 시간표",new ArrayList<Lecture>()));
        tables.add(new Table("1",2015,3,"후보 1",new ArrayList<Lecture>()));
        tables.add(new Table("2",2015,3,"후보 2",new ArrayList<Lecture>()));
        tables.add(new Table("3",2015,1,"최종안",new ArrayList<Lecture>()));
        tables.add(new Table("4",2015,1,"제 1안",new ArrayList<Lecture>()));
        tables.add(new Table("5",2014,2,"후.. 개발하기힘들다",new ArrayList<Lecture>()));

        tableMap = new HashMap<>();
        for(int i=0;i<tables.size();i++) {
            tableMap.put( String.valueOf(i) , tables.get(i));
        }
    }*/
}
