package com.zzzmode.appopsx.ui.permission;

import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.zzzmode.appopsx.R;
import com.zzzmode.appopsx.ui.model.OpEntryInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zl on 2016/11/18.
 */
class AppPermissionAdapter extends RecyclerView.Adapter<AppPermissionAdapter.ViewHolder> implements
    AdapterView.OnItemSelectedListener, View.OnClickListener{

  private List<OpEntryInfo> datas = new ArrayList<>();

  private OnSwitchItemClickListener listener;

  private boolean showPermDesc;
  private boolean showOpName;
  private boolean showPermTime;

  void setShowConfig(boolean showPermDesc, boolean showOpName, boolean showPermTime) {
    this.showPermDesc = showPermDesc;
    this.showOpName = showOpName;
    this.showPermTime = showPermTime;
  }

  void setDatas(List<OpEntryInfo> datas) {
    this.datas = datas;
  }

  List<OpEntryInfo> getDatas() {
    return datas;
  }

  void updateItem(OpEntryInfo info) {
    if (datas != null && info != null) {
      int i = datas.indexOf(info);
      if (i != -1 && i < datas.size()) {
        notifyItemChanged(i);
      }
    }
  }

  public void setListener(OnSwitchItemClickListener listener) {
    this.listener = listener;
  }


  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_permission_item, parent, false));
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    OpEntryInfo opEntryInfo = datas.get(position);
//    holder.itemView.setOnClickListener(this);
    holder.itemView.setTag(holder);

    holder.switchCompat.setTag(opEntryInfo);
    if (opEntryInfo != null) {
      holder.icon.setImageResource(opEntryInfo.icon);
      if (opEntryInfo.opPermsLab != null) {
        holder.title.setText(opEntryInfo.opPermsLab);
      } else {
        holder.title.setText(opEntryInfo.opName);
      }

      if (showOpName && opEntryInfo.opName != null) {
        holder.summary.setVisibility(View.VISIBLE);
        holder.summary.setText(opEntryInfo.opName);
      } else {
        holder.summary.setVisibility(View.GONE);
      }

      if (showPermDesc && opEntryInfo.opPermsDesc != null) {
        holder.summary.setVisibility(View.VISIBLE);
        holder.summary.setText(opEntryInfo.opPermsDesc);
      } else {
        if (!showOpName) {
          holder.summary.setVisibility(View.GONE);
        }
      }

      if (showPermTime ) {
        long time = opEntryInfo.opEntry.getTime();
        if(time > 0){
          holder.lastTime.setText(DateUtils
              .getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS,
                  DateUtils.FORMAT_ABBREV_TIME));
        }else {
          holder.lastTime.setText(R.string.never_used);
        }
        holder.lastTime.setVisibility(View.VISIBLE);

      } else {
        holder.lastTime.setVisibility(View.GONE);
      }

      holder.switchCompat.setOnItemSelectedListener(null);
      holder.switchCompat.setSelection(opEntryInfo.mode);
      holder.switchCompat.setOnItemSelectedListener(this);
    }
  }

  @Override
  public int getItemCount() {
    return datas.size();
  }

  @Override
  public void onClick(View v) {
    if (v.getTag() instanceof ViewHolder) {
      ((ViewHolder) v.getTag()).switchCompat.performClick();
    }
  }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getTag() instanceof OpEntryInfo && listener != null) {
            OpEntryInfo opEntryInfo = (OpEntryInfo) adapterView.getTag();
            if(l==opEntryInfo.mode)
                return;
            listener.onSwitch(opEntryInfo, l);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

  static class ViewHolder extends RecyclerView.ViewHolder {

    ImageView icon;
    TextView title;
    TextView summary;
    TextView lastTime;
    AppCompatSpinner switchCompat;

    ViewHolder(View itemView) {
      super(itemView);
      icon = (ImageView) itemView.findViewById(R.id.img_group);
      title = (TextView) itemView.findViewById(android.R.id.title);
      summary = (TextView) itemView.findViewById(android.R.id.summary);
      lastTime = (TextView) itemView.findViewById(R.id.last_time);
      switchCompat = (AppCompatSpinner) itemView.findViewById(R.id.switch_compat);

    }
  }

  public interface OnSwitchItemClickListener {

    void onSwitch(OpEntryInfo info, long v);
  }

}
