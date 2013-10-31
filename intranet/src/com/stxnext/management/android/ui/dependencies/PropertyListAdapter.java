
package com.stxnext.management.android.ui.dependencies;

import java.util.List;

import android.app.Activity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.UserProperty;

public class PropertyListAdapter extends BaseAdapter {

    private final Activity context;
    private List<UserProperty> properties;
    private ListView listView;

    public PropertyListAdapter(Activity context, ListView listView, List<UserProperty> properties) {
        this.context = context;
        this.properties = properties;
        this.listView = listView;
    }

    public void setProperties(List<UserProperty> properties) {
        this.properties = properties;
    }

    @Override
    public int getCount() {
        return properties.size();
    }

    @Override
    public Object getItem(int position) {
        return properties.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        final UserProperty item = properties.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.adapter_user_property, parent, false);

            TextView nameView = (TextView) convertView
                    .findViewById(R.id.nameView);
            TextView valueView = (TextView) convertView
                    .findViewById(R.id.valueView);

            holder.parent = convertView;
            holder.nameView = nameView;
            holder.valueView = valueView;
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        return prepareView(position, item, holder);
    }

    private View prepareView(final int position, final UserProperty item,
            final ViewHolder holder) {
        holder.nameView.setText(item.getName());
        holder.valueView.setText(Html.fromHtml(item.getValue()));
        holder.valueView.setMovementMethod(LinkMovementMethod.getInstance());

        return holder.parent;
    }

    public class ViewHolder implements Cloneable {
        private TextView nameView;
        private TextView valueView;
        private View parent;
    }

}
