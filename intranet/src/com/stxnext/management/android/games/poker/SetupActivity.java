
package com.stxnext.management.android.games.poker;

import java.util.ArrayList;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.storage.prefs.StoragePrefs;
import com.stxnext.management.android.storage.sqlite.dao.DAO;
import com.stxnext.management.android.ui.dependencies.ExtendedViewPager;

public class SetupActivity extends SherlockFragmentActivity implements GameSetupListener {

    private StoragePrefs prefs;

    SetupFragmentAdapter mAdapter;
    ExtendedViewPager mPager;
    SetupRoleFragment roleFragment;
    IntranetUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_game_setup);

        prefs = StoragePrefs.getInstance(this);
        Long userId = prefs.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, "Couldn't identify user", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        currentUser = DAO.getInstance().getIntranetUser().getById(userId);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new SetupRoleFragment(this));

        mAdapter = new SetupFragmentAdapter(this, getSupportFragmentManager(), fragments);
        mPager = (ExtendedViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(3);
        mPager.setAdapter(mAdapter);

        mPager.setPagingEnabled(false);
    }

    boolean exitRequested = false;

    @Override
    public void onBackPressed() {
        Builder builder = new android.app.AlertDialog.Builder(this)
                .setTitle("Discard session setup?")
                .setMessage("Do you wish to discard session setup?")
                .setNegativeButton(getString(R.string.common_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton(getString(R.string.common_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
        builder.show();
    }

    @Override
    public void onRoleChosen(GameRole role) {

    }

    @Override
    public IntranetUser getCurrentUser() {
        return this.currentUser;
    }

}
