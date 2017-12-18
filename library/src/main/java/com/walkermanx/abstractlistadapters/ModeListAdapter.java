package com.walkermanx.abstractlistadapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 张伟 on 2017/6/26 : 16:35
 * Email:zhangwei-baba@163.com
 * Description: 为listview gridview等控件编写的通用adapter
 *              使用方法：
 *              实现该抽象类 返回对应的必要参数即可
 */

public abstract class ModeListAdapter<T> extends BaseAdapter {

    private Context context;
    private List<T> mData = new ArrayList<T>();
    private Set<Integer> selectedPositions = new HashSet<>();

    public ModeListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T location = getItem(position);
        BaseItemView itemView = getModelItemView(parent.getContext(),convertView, getItemViewType(position));
        itemView.setPosition(position,mData.size());
        itemView.setSelected(selectedPositions.contains(position));
        itemView.setId(position + 1);
        itemView.setModel(location);
        return itemView;
    }

    private BaseItemView getModelItemView(Context context,View convertView, int viewType) {
        return null != convertView ? (BaseItemView) convertView : GetCurrentModelItemViewInstanceByViewType(context,viewType);
    }

    // getItemViewType返回的数值其取值必须在getViewTypeCount返回的数值之内。
    // 也就是如果getViewTypeCount返回的数值是3，
    // 那么getItemViewType返回的数值就必须是0，1，2三个中的一个 否则无效
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @NonNull
    protected abstract BaseItemView GetCurrentModelItemViewInstanceByViewType(Context context,int viewType);


    public void setSelectedPosition(int selectedPosition) {
        setSelectedPositionSilently(selectedPosition);
        notifyDataSetChanged();
    }
    public void setSelectedPositionSilently(int selectedPosition) {
        clearAllSelectedPositions();
        addSelectedPositionSilently(selectedPosition);
    }

    public void addSelectedPositionSilently(int selectedPosition) {
        if (isPositionSelected(selectedPosition))
            return;
        this.selectedPositions.add(selectedPosition);
    }

    public void addSelectedPosition(int selectedPosition) {
        addSelectedPositionSilently(selectedPosition);
        notifyDataSetChanged();
    }

    public void addSelectedPositions(Set<Integer> selectedPositions) {
        if (selectedPositions == null || selectedPositions.isEmpty())
            return;
        this.selectedPositions.addAll(selectedPositions);
        notifyDataSetChanged();
    }

    public void removeSelectedPosition(int selectedPosition) {
        removeSelectedPositionSilently(selectedPosition);
        notifyDataSetChanged();
    }

    public void removeSelectedPositionSilently(int selectedPosition) {
        if (!isPositionSelected(selectedPosition))
            return;
        this.selectedPositions.remove(selectedPosition);
    }

    public void removeOrAddSelectedPosition(int selectedPosition) {
        removeOrAddSelectedPositionSilently(selectedPosition);
        notifyDataSetChanged();
    }

    public void removeOrAddSelectedPositionSilently(int selectedPosition) {
        if (isPositionSelected(selectedPosition))
            removeSelectedPositionSilently(selectedPosition);
        else
            addSelectedPositionSilently(selectedPosition);
    }

    public void clearAllSelectedPositions() {
        selectedPositions.clear();
    }


    public boolean hasSelectedPosition() {
        return !selectedPositions.isEmpty();
    }

    public boolean isPositionSelected(int positon) {
        return selectedPositions.contains(positon);
    }

    public List<T> getSelectItems() {

        List<T> ts = new ArrayList<>();
        if (selectedPositions.isEmpty())
            return ts;

        for (Integer index : selectedPositions) {
            ts.add(mData.get(index));
        }

        return ts;
    }

    public List<T> getData() {
        return mData;
    }

    public void clear(){
        mData.clear();
        notifyDataSetChanged();
    }

    public void setData(List<T> data) {
        if (data == null || data.isEmpty())
            return;
        this.mData = data;
    }

    public void addDatas(List<T> data) {
        if (data == null || data.isEmpty())
            return;
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(T data) {
        addData(0,data);
    }

    public void addData(int position, T data) {
        if (data == null)
            return;
        this.mData.add(position,data);
        notifyDataSetChanged();
    }

}
