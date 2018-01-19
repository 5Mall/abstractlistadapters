package com.walkermanx.abstractlistadapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 张伟 on 2017/3/16 : 15:59
 * Email:zhangwei-baba@163.com
 * Description: 为recyclerview系控件编写的通用adapter
 *              使用方法：
 *              实现该抽象类 返回对应的必要参数即可
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter.Holder> {
    public static final int TYPE_HEADER = -101;
    public static final int TYPE_FOOTER = -202;
    public static final int TYPE_FULL = -203;
    private List<T> mDatas = new ArrayList<>();
    private View mHeaderView;
    private View mFooterView;
    private Set<Integer> selectedPosition = new HashSet<>();
    private OnItemClickListener<T> mListener;

    public void addHeaderView(View headerView) {
        if (headerView == null)
            return;
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public void addFooterrView(View footerView) {
        if (footerView == null)
            return;
        if (mFooterView == null || mFooterView != footerView) {
            mFooterView = footerView;
//            notifyItemInserted(getItemCount()-1);
            notifyDataSetChanged();
        }
    }

    public void removeHeaderView() {
        if (mHeaderView != null) {
            mHeaderView = null;
            notifyItemRemoved(0);
        }
    }

    public void removeFooterView() {
        if (mFooterView != null) {
            mFooterView = null;
            notifyItemRemoved(mDatas.size() - 1);
        }
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public View getFooterView() {
        return mFooterView;
    }

    public boolean hasHeader() {
        return mHeaderView != null;
    }

    public boolean hasFooter() {
        return mFooterView != null;
    }

    public BaseRecyclerAdapter<T> setOnItemClickListener(OnItemClickListener<T> li) {
        mListener = li;
        return this;
    }

    public void addDatas(List<T> datas) {
        addDatas(datas, false);
    }

    public void addDatas(List<T> datas, boolean clsPre) {
        if (clsPre)
            mDatas.clear();
        mDatas.addAll(datas);
    }

    public void addData(T data) {
        mDatas.add(data);
    }

    public void addData(int pos, T data) {
        mDatas.add(pos, data);
    }

    public void remove(int pos) {
        if (mDatas.isEmpty())
            return;

        if (pos >= 0 && pos < mDatas.size()) {
            mDatas.remove(pos);
        }
    }

    public void clear() {
        if (!mDatas.isEmpty()) {
            mDatas.clear();
        }
    }

    public boolean isEmpty() {
        return mDatas.isEmpty();
    }

    @Override
    public int getItemViewType(int position) {
        if (position==getItemCount()-1 && hasFooter() )
            return TYPE_FOOTER;

        if (hasHeader()) {
            if (position==0)
                return TYPE_HEADER;
            position--;
        }
        return itemViewType(position);
    }

    @Override
    public BaseRecyclerAdapter.Holder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == TYPE_HEADER) return new Holder(mHeaderView);
        if (viewType == TYPE_FOOTER) return new Holder(mFooterView);
        return onCreate(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerAdapter.Holder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_FOOTER)
            return;

//        final int pos = getRealPosition(viewHolder);
        final int pos = hasHeader() ? position - 1 : position;
        final T data = mDatas.get(pos);
//        onBind(viewHolder, pos, data);
        viewHolder.fillData(data, pos);
        if (mListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(pos, data, false);
                }
            });

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mListener.onItemClick(pos, data, true);
                    return true;
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_FOOTER|| getItemViewType(position) == TYPE_FULL
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(BaseRecyclerAdapter.Holder holder) {
        super.onViewAttachedToWindow(holder);
//        if (!hasHeader() && !hasFooter())
//            return;

        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && holder.getLayoutPosition() == 0) {

            //TODO footer header 处理
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return hasHeader() ? position - 1 : position;
    }

    @Override
    public int getItemCount() {
        int delt=0;
        if (hasHeader())
            delt++;
        if (hasFooter())
            delt++;
        return mDatas.size()+delt;
    }

    /**
     * 不计算header  footer view
     * @return
     */
    public int getRawItemCount() {
        return mDatas.size();
    }

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
        this.selectedPosition.add(selectedPosition);
    }

    public void addSelectedPosition(int selectedPosition) {
        addSelectedPositionSilently(selectedPosition);
        notifyDataSetChanged();
    }

    public void addSelectedPositions(Set<Integer> selectedPositions) {
        if (selectedPositions == null || selectedPositions.isEmpty())
            return;
        this.selectedPosition.addAll(selectedPositions);
        notifyDataSetChanged();
    }

    public void removeSelectedPosition(int selectedPosition) {
        removeSelectedPositionSilently(selectedPosition);
        notifyDataSetChanged();
    }

    public void removeSelectedPositionSilently(int selectedPosition) {
        if (!isPositionSelected(selectedPosition))
            return;
        this.selectedPosition.remove(selectedPosition);
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
        selectedPosition.clear();
    }


    public boolean hasSelectedPosition() {
        return !selectedPosition.isEmpty();
    }

    public boolean isPositionSelected(int positon) {
        return selectedPosition.contains(positon);
    }

    public List<T> getSelectItems() {

        List<T> ts = new ArrayList<>();
        if (selectedPosition.isEmpty())
            return ts;

        for (Integer index : selectedPosition) {
            ts.add(mDatas.get(index));
        }

        return ts;
    }


    @NonNull
    protected abstract BaseItemView GetCurrentModelItemViewInstanceByViewType(Context context, int viewType);

    public BaseRecyclerAdapter.Holder onCreate(ViewGroup parent, int viewType) {
        return new Holder(GetCurrentModelItemViewInstanceByViewType(parent.getContext(),viewType));
    }

    protected  int itemViewType(int pos){
        return 0;
    }

    public class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }

        public void fillData(T data, int position) {
            if (itemView instanceof BaseItemView) {
                itemView.setId(position + 1);
                itemView.setSelected(selectedPosition.contains(position));
                ((BaseItemView) itemView).setPosition(position, mDatas.size());
                ((BaseItemView) itemView).setModel(data);
            }
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClick(int position, T data, boolean isLong);
    }
}