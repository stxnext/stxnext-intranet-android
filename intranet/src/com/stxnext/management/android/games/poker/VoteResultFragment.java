
package com.stxnext.management.android.games.poker;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.common.collect.Lists;
import com.stxnext.management.android.R;
import com.stxnext.management.android.ui.dependencies.PlayerVotesGridAdapter;
import com.stxnext.management.server.planningpoker.server.dto.combined.Vote;

public class VoteResultFragment extends Fragment {

    private View view;
    GridView votesGridView;
    PlayerVotesGridAdapter adapter;
    private List<Vote> votes = new ArrayList<Vote>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    boolean viewCreated;

    public void setVotes(List<Vote> votes){
        this.votes = votes;
        if(adapter != null){
            adapter.setList(votes);
            adapter.notifyDataSetChanged();
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.game_fragment_votes, container,
                false);

        votesGridView = (GridView) view.findViewById(R.id.votesGrid);
        adapter = new PlayerVotesGridAdapter(getActivity(), votes, votesGridView);
        votesGridView.setAdapter(adapter);
        viewCreated = true;
        return view;
    }
}
