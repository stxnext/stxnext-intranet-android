package com.stxnext.management.android.ui.dependencies;

import java.util.List;

import android.app.Activity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.Lateness;
import com.stxnext.management.android.dto.local.UserProperty;
import com.stxnext.management.android.ui.dependencies.PropertyListAdapter.ViewHolder;

public class LatenessListAdapter  extends BaseAdapter {

    private final Activity context;
    private List<Lateness> lateList;

    public LatenessListAdapter(Activity context, List<Lateness> lateList) {
        this.context = context;
        this.lateList = lateList;
    }

    public void setList(List<Lateness> lateList) {
        this.lateList = lateList;
    }

    @Override
    public int getCount() {
        return lateList.size();
    }

    @Override
    public Object getItem(int position) {
        return lateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        final Lateness item = lateList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.adapter_presence, parent, false);

            TextView nameView = (TextView) convertView
                    .findViewById(R.id.nameView);
            TextView fromToView = (TextView) convertView
                    .findViewById(R.id.fromToView);

            holder.parent = convertView;
            holder.nameView = nameView;
            holder.fromToView = fromToView;
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        return prepareView(position, item, holder);
    }

    private View prepareView(final int position, final Lateness item,
            final ViewHolder holder) {
        holder.nameView.setText(item.getName());
        holder.fromToView.setText(item.getStart()!=null?(item.getStart()+" - "+item.getEnd()):"");
        return holder.parent;
    }

    public class ViewHolder implements Cloneable {
        private TextView nameView;
        private TextView fromToView;
        private View parent;
    }

}
