package com.stxnext.management.android.ui.dependencies;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.IntranetUser;

public class UserListAdapter extends BaseAdapter {

    private final Context context;
    private List<IntranetUser> users;

    public UserListAdapter(Context context, List<IntranetUser> users) {
        this.context = context;
        this.users = users;
    }

    public void setUsers(List<IntranetUser> users) {
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        final IntranetUser item = users.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.adapter_users, parent,false);

            TextView nameView = (TextView) convertView
                    .findViewById(R.id.nameView);
            ImageView imageView = (ImageView) convertView
                    .findViewById(R.id.userImageView);

            holder.parent = convertView;
            holder.userNameView = nameView;
            holder.userImageView = imageView;
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        return prepareView(position, item, holder);
    }


    private View prepareView(final int position, final IntranetUser item,
            final ViewHolder holder) {
        holder.userNameView.setText(item.getName());
        return holder.parent;
    }

    public class ViewHolder implements Cloneable {
        private TextView userNameView;
        private ImageView userImageView;
        private View parent;
        private Integer position;
    }

}
