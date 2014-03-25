package com.stxnext.management.android.ui.dependencies;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stxnext.management.android.R;
import com.stxnext.management.server.planningpoker.server.dto.combined.Deck;
import com.stxnext.management.server.planningpoker.server.dto.combined.Session;

public class SessionListAdapter  extends BaseAdapter {

    private final Activity context;
    private List<Session> sessionList;
    private HashMap<Long, Deck> decks;

    public SessionListAdapter(Activity context, List<Session> sessionList, List<Deck> decks) {
        this.context = context;
        this.sessionList = sessionList;
        this.decks = new LinkedHashMap<Long, Deck>();
        for(Deck deck : decks){
            this.decks.put(deck.getId(), deck);
        }
    }

    public void setList(List<Session> sessionList) {
        this.sessionList = sessionList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return sessionList.size();
    }

    @Override
    public Object getItem(int position) {
        return sessionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        final Session item = sessionList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.adapter_session, parent, false);

            holder.parent = convertView;
            holder.nameView = (TextView) convertView
                    .findViewById(R.id.sessionNameView);
            holder.ownerView = (TextView) convertView
                    .findViewById(R.id.sessionOwnerName);
            holder.deckView = (TextView) convertView.findViewById(R.id.sessionDeck);
            holder.userCountView = (TextView) convertView.findViewById(R.id.sessionPlayersCount);
            
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        return prepareView(position, item, holder);
    }

    private View prepareView(final int position, final Session item,
            final ViewHolder holder) {
        holder.deckView.setText(decks.get(item.getDeckId()).getName());
        holder.nameView.setText(item.getName());
        holder.ownerView.setText(item.getOwner().getName());
        holder.userCountView.setText(String.valueOf(item.getPlayers().size()));
        
        return holder.parent;
    }

    public class ViewHolder implements Cloneable {
        TextView nameView;
        TextView ownerView;
        TextView userCountView;
        TextView deckView;
        
        View parent;
    }
}