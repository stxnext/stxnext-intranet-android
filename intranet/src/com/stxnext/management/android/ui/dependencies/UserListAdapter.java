
package com.stxnext.management.android.ui.dependencies;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.storage.sqlite.EntityMapper;
import com.stxnext.management.android.storage.sqlite.dao.IntranetUserColumns;
import com.stxnext.management.android.storage.sqlite.dao.IntranetUserMapper;

public class UserListAdapter extends CursorAdapter {

    private final Activity context;
    private BetterHashMapLruCache<Integer, IntranetUser> cursorObjectMemoryCache;
    private EntityMapper<IntranetUser> userMapper;
    private List<Long> selectedUsers;
    private UserAdapterListener listener;
    ImageListAdapterHandler adapterHandler;
    
    private boolean dontUseCursorCache;

    public void setDontUseCursorCache(boolean dontUseCursorCache) {
        this.dontUseCursorCache = dontUseCursorCache;
    }
    
    public void setListener(UserAdapterListener listener) {
        this.listener = listener;
    }
    boolean usesSelection;

    public void setUsesSelection(boolean usesSelection) {
        this.usesSelection = usesSelection;
        selectedUsers.clear();
        notifyDataSetChanged();
    }

    public List<Long> getSelectedIds() {
        return selectedUsers;
    }
    
    public List<IntranetUser> getSelected(){
        Cursor c = getCursor();
        c.move(-1);
        return userMapper.mapEntity(c);
    }

    private void addSelectedFromCursor(Cursor c){
        selectedUsers.add(c.getLong(c.getColumnIndex(IntranetUserColumns.EXTERNAL_ID)));
    }
    
    public void setAllSelected(boolean all){
        selectedUsers.clear();
        if(all){
            Cursor c = getCursor();
            c.move(-1);
            while(c.moveToNext()){
                addSelectedFromCursor(c);
            }
        }
        notifyDataSetChanged();
    }
    
    private boolean isUserSelected(Long userId) {
        return selectedUsers.contains(userId);
    }

    private void setUserSelected(Long userId, boolean selected) {
        if (selected)
            selectedUsers.add(userId);
        else
            selectedUsers.remove(userId);
        
        if(this.listener != null){
            if(selectedUsers.size()<=0){
                listener.onSelectionEdgeState(false);
            }
            else if(selectedUsers.size() == getCursor().getCount()){
                listener.onSelectionEdgeState(true);
            }
        }
    }

    public void addCursorObjectToMemoryCache(Integer key, IntranetUser bitmap) {
        if (dontUseCursorCache)
            return;
        if (key != null && bitmap != null) {
            cursorObjectMemoryCache.put(key, bitmap);
        }
    }

    public IntranetUser getCursorObjectFromMemCache(Integer key) {
        if (dontUseCursorCache)
            return null;

        if (key != null) {
            return (IntranetUser) cursorObjectMemoryCache.get(key);
        }
        return null;
    }

    public void clearCache() {
        adapterHandler.clearCache();
        cursorObjectMemoryCache.trimToElementsCount(0);
    }

    int firstVisiblePosition;
    int lastVisiblePosition;

    public UserListAdapter(Activity context, Cursor c, final ListView listView) {
        super(context, c);
        this.context = context;
        this.adapterHandler = new ImageListAdapterHandler(listView, context);
        this.userMapper = new IntranetUserMapper();

        final int cacheSize = BetterHashMapLruCache.getPreperredCacheSize();

        selectedUsers = new ArrayList<Long>();
        cursorObjectMemoryCache = new BetterHashMapLruCache<Integer, IntranetUser>(
                (int) (cacheSize * 0.3), 150);
    }

    @Override
    public Object getItem(int position) {
        return userMapper.mapEntity(getCursor(), position);
    }

    public class ViewHolder extends HandlerViewHolder implements Cloneable {
        private TextView userNameView;
        private View phoneRow;
        private View ircRow;
        private TextView roleView;
        private TextView groupView;
        private TextView lateView;
        private TextView phoneView;
        private TextView ircView;
        private View checkboxArea;
        private View roleArea;
        private ImageView checkBox;

        private View parent;

        public ViewHolder(Integer position) {
            super();
            this.position = position;
        }
    }

    @Override
    public void notifyDataSetChanged() {
        adapterHandler.clearTasks();
        super.notifyDataSetChanged();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final ViewHolder holder = new ViewHolder(cursor.getPosition());

        LayoutInflater inflater = LayoutInflater.from(this.context);

        View parentView = inflater.inflate(R.layout.adapter_users, null);

        TextView nameView = (TextView) parentView.findViewById(R.id.nameView);
        ImageView imageView = (ImageView) parentView
                .findViewById(R.id.userImageView);
        holder.phoneRow = parentView.findViewById(R.id.phoneRow);
        holder.ircRow = parentView.findViewById(R.id.ircRow);
        holder.roleView = (TextView) parentView.findViewById(R.id.roleView);
        holder.groupView = (TextView) parentView.findViewById(R.id.groupView);
        holder.lateView = (TextView) parentView.findViewById(R.id.lateView);

        holder.phoneView = (TextView) parentView.findViewById(R.id.phoneView);
        holder.ircView = (TextView) parentView.findViewById(R.id.ircView);
        holder.checkboxArea = parentView.findViewById(R.id.checkboxArea);
        holder.roleArea = parentView.findViewById(R.id.roleArea);
        holder.checkBox = (ImageView) parentView.findViewById(R.id.checkmark);

        holder.parent = parentView;
        holder.userNameView = nameView;
        holder.imageview = imageView;

        parentView.setTag(holder);

        return parentView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final ViewHolder holder = (ViewHolder) view.getTag();
        int position = cursor.getPosition();
        holder.position = position;

        IntranetUser cachedUser = getCursorObjectFromMemCache(position);
        
        final IntranetUser user = cachedUser!=null?cachedUser : userMapper.mapEntity(cursor, position);
        if(cachedUser == null){
            addCursorObjectToMemoryCache(position, user);
        }

        holder.groupView.setVisibility(usesSelection ? View.GONE : View.VISIBLE);
        holder.checkboxArea.setVisibility(!usesSelection ? View.GONE : View.VISIBLE);
        if (usesSelection) {
            holder.checkBox.setImageLevel(isUserSelected(user.getId().longValue()) ? 1 : 0);
        }

        holder.parent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usesSelection) {
                    boolean selectionstate = isUserSelected(user.getId().longValue());
                    setUserSelected(user.getId().longValue(), !selectionstate);
                    holder.checkBox.setImageLevel(!selectionstate ? 1 : 0);
                }
            }
        });

        holder.userNameView.setText(user.getName());

        if (!Strings.isNullOrEmpty(user.getPhone())) {
            holder.phoneView.setText(user.getPhone());
            holder.phoneRow.setVisibility(View.VISIBLE);
        } else {
            holder.phoneRow.setVisibility(View.INVISIBLE);
        }

        if (!Strings.isNullOrEmpty(user.getIrc())) {
            holder.ircView.setText(user.getIrc());
            holder.ircRow.setVisibility(View.VISIBLE);
        } else {
            holder.ircRow.setVisibility(View.INVISIBLE);
        }

        boolean absenceDataPresent = false;
        if (user.getAbsenceDisplayData() != null) {
            holder.lateView.setText(context.getString(R.string.label_absence));
            holder.lateView.setTextColor(Color.parseColor("#22ff0000"));
            absenceDataPresent = true;
        }

        else if (user.getLatenessDisplayData() != null) {
            holder.lateView.setText(context.getString(R.string.label_lateness));
            holder.lateView.setTextColor(Color.parseColor("#220000ff"));
            absenceDataPresent = true;
        }

        holder.lateView.setVisibility(absenceDataPresent ? View.VISIBLE
                : View.INVISIBLE);

        if (user.getRoles() != null && user.getRoles().size() > 0) {
            holder.roleView.setText(user.getRoles().get(0));
            holder.roleView.setVisibility(View.VISIBLE);
        } else {
            holder.roleView.setVisibility(View.INVISIBLE);
        }

        if (user.getGroups() != null && user.getGroups().size() > 0) {
            holder.groupView.setText(user.getGroups().get(0));
            holder.groupView.setVisibility(View.VISIBLE);
        } else {
            holder.groupView.setVisibility(View.INVISIBLE);
        }
        
        
        adapterHandler.onGetView(holder, user.getId().longValue(), user.getImageUrl(), position);
    }
    
    public interface UserAdapterListener{
        public void onSelectionEdgeState(boolean allSelected);
    }

}
