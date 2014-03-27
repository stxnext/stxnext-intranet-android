
package com.stxnext.management.android.ui.dependencies;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.stxnext.management.android.R;
import com.stxnext.management.server.planningpoker.server.dto.combined.Vote;

public class PlayerVotesGridAdapter extends BaseAdapter {

    private final Activity context;
    private List<Vote> votes = new ArrayList<Vote>();
    ImageListAdapterHandler adapterHandler;

    public PlayerVotesGridAdapter(Activity context, List<Vote> votes, final GridView gridView) {
        this.context = context;
        if(votes!= null)
            this.votes = votes;
        this.adapterHandler = new ImageListAdapterHandler(gridView, context);
    }

    @Override
    public void notifyDataSetChanged() {
        adapterHandler.clearTasks();
        super.notifyDataSetChanged();
    }

    public void setList(List<Vote> votes) {
        if(votes!= null)
            this.votes = votes;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return votes.size();
    }

    @Override
    public Object getItem(int position) {
        return votes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        final Vote item = votes.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.game_adapter_votes, parent, false);

            holder.parent = convertView;
            holder.imageview = (ImageView) convertView
                    .findViewById(R.id.userImage);
            holder.voteValueView = (TextView) convertView.findViewById(R.id.voteValueView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        return prepareView(position, item, holder);
    }

    private View prepareView(final int position, final Vote item,
            final ViewHolder holder) {
        holder.position = position;
        holder.voteValueView.setText(item.getCard().getName());
        adapterHandler.onGetView(holder, item.getPlayer().getExternalId(), item.getPlayer()
                .getImageUrl(), position);
        return holder.parent;
    }

    public class ViewHolder extends HandlerViewHolder implements Cloneable {
        View parent;
        TextView voteValueView;
    }
}
