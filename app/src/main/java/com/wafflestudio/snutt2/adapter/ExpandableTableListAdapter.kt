package com.wafflestudio.snutt2.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wafflestudio.snutt2.R;
import com.wafflestudio.snutt2.manager.PrefManager;
import com.wafflestudio.snutt2.model.Table;
import com.wafflestudio.snutt2.ui.TableListActivity;

import java.util.ArrayList;

/**
 * Created by makesource on 2016. 1. 24..
 */
public class ExpandableTableListAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> groupList = null;
    private ArrayList<ArrayList<Table>> childList = null;
    private ViewHolder viewHolder = null;

    public ExpandableTableListAdapter(TableListActivity c, ArrayList<String> groupList,
                                 ArrayList<ArrayList<Table>> childList){
        super();
        this.groupList = groupList;
        this.childList = childList;
    }

    // 그룹 포지션을 반환한다.
    @Override
    public String getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    // 그룹 사이즈를 반환한다.
    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    // 그룹 ID를 반환한다.
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // 그룹뷰 각각의 ROW
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = convertView;

        if(v == null){
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.cell_table_section, parent, false);
            viewHolder.tableSectionName = (TextView) v.findViewById(R.id.cell_table_section);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)v.getTag();
        }

        // 그룹을 펼칠때와 닫을때 아이콘을 변경해 준다.
        /*if(isExpanded){
            viewHolder.iv_image.setBackgroundColor(Color.GREEN);
        }else{
            viewHolder.iv_image.setBackgroundColor(Color.WHITE);
        }*/

        viewHolder.tableSectionName.setText(getGroup(groupPosition));
        return v;
    }

    // 차일드뷰를 반환한다.
    @Override
    public String getChild(int groupPosition, int childPosition) {
        if (childList.get(groupPosition).isEmpty()) return "+ 새로운 시간표 추가";
        return childList.get(groupPosition).get(childPosition).getTitle();
    }

    // 차일드뷰 사이즈를 반환한다.
    @Override
    public int getChildrenCount(int groupPosition) {
        if (childList.get(groupPosition).isEmpty()) return 1;
        return childList.get(groupPosition).size();
    }

    // 차일드뷰 ID를 반환한다.
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // 차일드뷰 각각의 ROW
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = convertView;

        if(v == null){
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.cell_table, null);
            viewHolder.tableName = (TextView) v.findViewById(R.id.cell_table);
            viewHolder.checked = (ImageView) v.findViewById(R.id.checked);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)v.getTag();
        }

        viewHolder.tableName.setText(getChild(groupPosition, childPosition));
        if (childList.get(groupPosition).isEmpty()) {
            viewHolder.tableName.setTextColor(Color.GRAY);
            viewHolder.checked.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.tableName.setTextColor(Color.BLACK);
            String id = childList.get(groupPosition).get(childPosition).getId();
            if (PrefManager.getInstance().getLastViewTableId().equals(id)) {
                viewHolder.checked.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checked.setVisibility(View.INVISIBLE);
            }
        }
        return v;
    }

    @Override
    public boolean hasStableIds() { return true; }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }

    class ViewHolder {
        public TextView tableName;
        public TextView tableSectionName;
        public ImageView checked;
    }

}