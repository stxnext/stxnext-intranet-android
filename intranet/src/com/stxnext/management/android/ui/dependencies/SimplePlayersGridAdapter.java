package com.stxnext.management.android.ui.dependencies;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.stxnext.management.android.R;
import com.stxnext.management.server.planningpoker.server.dto.combined.Player;

public class SimplePlayersGridAdapter extends BaseAdapter {

    private final Activity context;
    private List<Player> playerList;
    ImageListAdapterHandler adapterHandler;
    
    public SimplePlayersGridAdapter(Activity context, List<Player> playerList,final GridView gridView) {
        this.context = context;
        this.playerList = playerList;
        this.adapterHandler = new ImageListAdapterHandler(gridView, context);
    }

    @Override
    public void notifyDataSetChanged() {
        adapterHandler.clearTasks();
        super.notifyDataSetChanged();
    }
    
    public void setList(List<Player> playerList) {
        this.playerList = playerList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return playerList.size();
    }

    @Override
    public Object getItem(int position) {
        return playerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        final Player item = playerList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.game_adapter_player, parent, false);

            holder.parent = convertView;
            holder.imageview = (ImageView) convertView
                    .findViewById(R.id.userImage);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        return prepareView(position, item, holder);
    }

    private View prepareView(final int position, final Player item,
            final ViewHolder holder) {
        holder.position = position;
        adapterHandler.onGetView(holder, item.getExternalId(), item.getImageUrl(), position);
        return holder.parent;
    }

    public class ViewHolder extends HandlerViewHolder implements Cloneable {
        View parent;
    }
}