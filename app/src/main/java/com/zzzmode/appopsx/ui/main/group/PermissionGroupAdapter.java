package com.zzzmode.appopsx.ui.main.group;

import android.support.annotation.IntRange;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.zzzmode.appopsx.R;
import com.zzzmode.appopsx.ui.core.LocalImageLoader;
import com.zzzmode.appopsx.ui.model.PermissionChildItem;
import com.zzzmode.appopsx.ui.model.PermissionGroup;
import com.zzzmode.appopsx.ui.widget.ExpandableItemIndicator;
import java.util.List;

/**
 * Created by zl on 2017/1/18.
 */

class PermissionGroupAdapter extends
    AbstractExpandableItemAdapter<PermissionGroupAdapter.GroupViewHolder, PermissionGroupAdapter.ChildViewHolder> implements
    View.OnClickListener, AdapterView.OnItemSelectedListener {


  private PermissionGroupAdapter.OnSwitchItemClickListener listener;
  private OnGroupOtherClickListener mOnGroupOtherClickListener;
  private List<PermissionGroup> mData;


  private RecyclerViewExpandableItemManager mExpandableItemManager = null;


  PermissionGroupAdapter(RecyclerViewExpandableItemManager expandableItemManager) {
    mExpandableItemManager = expandableItemManager;
  }

  public void setData(List<PermissionGroup> data) {
    this.mData = data;
  }

  public List<PermissionGroup> getData() {
    return mData;
  }

  void changeTitle(int groupPosition, boolean allowed) {
    PermissionGroup permissionGroup = mData.get(groupPosition);
    if (permissionGroup != null) {
      permissionGroup.grants += (allowed ? 1 : -1);
    }
  }

  void setListener(OnSwitchItemClickListener listener,
      OnGroupOtherClickListener onGroupOtherClickListener) {
    this.listener = listener;
    this.mOnGroupOtherClickListener = onGroupOtherClickListener;
  }


  @Override
  public int getGroupCount() {
    return mData.size();
  }

  @Override
  public int getChildCount(int groupPosition) {
    return mData.get(groupPosition).apps.size();
  }

  @Override
  public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  @Override
  public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
  }

  @Override
  public GroupViewHolder onCreateGroupViewHolder(ViewGroup parent,
      @IntRange(from = -8388608L, to = 8388607L) int viewType) {
    return new GroupViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.layout_permission_group_item, parent, false));
  }

  @Override
  public ChildViewHolder onCreateChildViewHolder(ViewGroup parent,
      @IntRange(from = -8388608L, to = 8388607L) int viewType) {
    return new ChildViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.layout_permission_child_item, parent, false));
  }

  @Override
  public void onBindGroupViewHolder(GroupViewHolder holder, int groupPosition,
      @IntRange(from = -8388608L, to = 8388607L) int viewType) {
    PermissionGroup permissionGroup = mData.get(groupPosition);
    if (TextUtils.isEmpty(permissionGroup.opPermsLab)) {
      holder.tvPermName.setText(permissionGroup.opName);
    } else {
      holder.tvPermName.setText(permissionGroup.opPermsLab);
    }
    holder.groupIcon.setImageResource(permissionGroup.icon);

    holder.itemView.setTag(R.id.groupPosition, groupPosition);

    holder.itemView.setTag(holder);
    holder.itemView.setOnClickListener(this);

    holder.tvCount.setText(holder.itemView.getResources()
        .getString(R.string.permission_count, permissionGroup.grants, permissionGroup.count));

    holder.imgMenu.setTag(holder);
    holder.imgMenu.setOnClickListener(this);

    final int expandState = holder.getExpandStateFlags();

    if ((expandState & ExpandableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
      boolean isExpanded = (expandState & ExpandableItemConstants.STATE_FLAG_IS_EXPANDED) != 0;
      boolean animateIndicator = (
          (expandState & ExpandableItemConstants.STATE_FLAG_HAS_EXPANDED_STATE_CHANGED) != 0);
      holder.indicator.setExpandedState(isExpanded, animateIndicator);

      holder.imgMenu.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

  }

  @Override
  public void onBindChildViewHolder(ChildViewHolder holder, int groupPosition, int childPosition,
      @IntRange(from = -8388608L, to = 8388607L) int viewType) {
    PermissionChildItem appPermissions = mData.get(groupPosition).apps.get(childPosition);

    LocalImageLoader.load(holder.imgIcon, appPermissions.appInfo);

    holder.tvName.setText(appPermissions.appInfo.appName);

    long time = appPermissions.opEntryInfo.opEntry.getTime();
    if (time > 0) {
      holder.tvLastTime.setText(DateUtils
          .getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS,
              DateUtils.FORMAT_ABBREV_TIME));
    } else {
      holder.tvLastTime.setText(R.string.never_used);
    }


    holder.itemView.setOnClickListener(this);
    holder.itemView.setTag(holder);

    holder.switchCompat.setTag(appPermissions);
    holder.switchCompat.setTag(R.id.groupPosition, groupPosition);
    holder.switchCompat.setTag(R.id.childPosition, childPosition);

    holder.switchCompat.setOnItemSelectedListener(null);
    holder.switchCompat.setSelection(appPermissions.opEntryInfo.mode);
    holder.switchCompat.setOnItemSelectedListener(this);


  }

  @Override
  public boolean onCheckCanExpandOrCollapseGroup(GroupViewHolder holder, int groupPosition, int x,
      int y, boolean expand) {
    return false;
  }

  @Override
  public void onClick(View v) {

    Object tag = v.getTag();
    if (tag instanceof RecyclerView.ViewHolder) {
      int flatPosition = ((RecyclerView.ViewHolder) v.getTag()).getAdapterPosition();

      if (flatPosition == RecyclerView.NO_POSITION) {
        return;
      }

      long expandablePosition = mExpandableItemManager.getExpandablePosition(flatPosition);
      int groupPosition = RecyclerViewExpandableItemManager
          .getPackedPositionGroup(expandablePosition);
      int childPosition = RecyclerViewExpandableItemManager
          .getPackedPositionChild(expandablePosition);

      switch (v.getId()) {
        case R.id.layout_group_item:
          // toggle expanded/collapsed
          if (mExpandableItemManager.isGroupExpanded(groupPosition)) {
            mExpandableItemManager.collapseGroup(groupPosition);
          } else {
            mExpandableItemManager.expandGroup(groupPosition);
          }
          break;
        case R.id.img_menu_ups:
          if (mOnGroupOtherClickListener != null) {
            mOnGroupOtherClickListener.onOtherClick(groupPosition, v);
          }
          break;
      }
    }

    if (v.getTag() instanceof ChildViewHolder) {
      ((ChildViewHolder) v.getTag()).switchCompat.performClick();
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    if (adapterView.getTag() instanceof PermissionChildItem && listener != null) {
      int groupPosition = (int) adapterView.getTag(R.id.groupPosition);
      int childPosition = (int) adapterView.getTag(R.id.childPosition);
      PermissionChildItem permissionChildItem = (PermissionChildItem) adapterView.getTag();
      if(l==permissionChildItem.opEntryInfo.mode)
          return;
        listener.onSwitch(groupPosition, childPosition, permissionChildItem, l);

    } else if (view.getId() == R.id.img_menu_ups) {

    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> adapterView) {

  }


  static class GroupViewHolder extends AbstractExpandableItemViewHolder {

    TextView tvPermName;
    TextView tvCount;
    ImageView groupIcon;
    ExpandableItemIndicator indicator;
    ImageView imgMenu;

    public GroupViewHolder(View itemView) {
      super(itemView);
      tvPermName = (TextView) itemView.findViewById(R.id.tv_permission_name);
      tvCount = (TextView) itemView.findViewById(R.id.tv_permission_count);
      indicator = (ExpandableItemIndicator) itemView.findViewById(R.id.indicator);
      groupIcon = (ImageView) itemView.findViewById(R.id.img_group);
      imgMenu = (ImageView) itemView.findViewById(R.id.img_menu_ups);
    }
  }

  static class ChildViewHolder extends AbstractExpandableItemViewHolder {

    ImageView imgIcon;
    TextView tvName;
    TextView tvLastTime;
    AppCompatSpinner switchCompat;

    public ChildViewHolder(View itemView) {
      super(itemView);
      imgIcon = (ImageView) itemView.findViewById(R.id.app_icon);
      tvName = (TextView) itemView.findViewById(R.id.app_name);
      switchCompat = (AppCompatSpinner) itemView.findViewById(R.id.switch_compat);
      tvLastTime = (TextView) itemView.findViewById(R.id.perm_last_time);
    }
  }

  interface OnSwitchItemClickListener {

    void onSwitch(int groupPosition, int childPosition, PermissionChildItem item, long v);
  }

  interface OnGroupOtherClickListener {

    void onOtherClick(int groupPosition, View view);
  }
}
