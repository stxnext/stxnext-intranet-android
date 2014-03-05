
package com.stxnext.management.android.ui.dependencies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.storage.sqlite.EntityMapper;
import com.stxnext.management.android.storage.sqlite.dao.IntranetUserMapper;
import com.stxnext.management.android.web.api.HTTPResponse;
import com.stxnext.management.android.web.api.IntranetApi;

public class UserListAdapter extends CursorAdapter {

    private final Activity context;
    private BetterHashMapLruCache<Long, RoundedDrawable> memoryCache;
    private BetterHashMapLruCache<Integer, IntranetUser> cursorObjectMemoryCache;
    private HashMap<Long, LoadImageTask> taskIdentifiers = new HashMap<Long, LoadImageTask>();
    private ListView listView;
    private IntranetApi api;
    private EntityMapper<IntranetUser> userMapper;

    private boolean dontUseCursorCache;

    public void setDontUseCursorCache(boolean dontUseCursorCache) {
        this.dontUseCursorCache = dontUseCursorCache;
    }

    public void addBitmapToMemoryCache(Long key, RoundedDrawable bitmap) {
        if (key != null && bitmap != null) {
            memoryCache.put(key, bitmap);
        }
    }

    public RoundedDrawable getBitmapFromMemCache(Long key) {
        if (key != null) {
            return memoryCache.get(key);
        }
        return null;
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
        memoryCache.trimToElementsCount(0);
        cursorObjectMemoryCache.trimToElementsCount(0);
    }

    int firstVisiblePosition;
    int lastVisiblePosition;
    
    public UserListAdapter(Activity context, Cursor c, ListView listView) {
        super(context, c);
        this.context = context;
        this.listView = listView;
        this.api = IntranetApi.getInstance(context.getApplication());
        this.userMapper = new IntranetUserMapper();

        final int cacheSize = (int) (Runtime.getRuntime().maxMemory() * 0.80F);
        
        memoryCache = new BetterHashMapLruCache<Long, RoundedDrawable>((int) (cacheSize * 0.7),150) {
            @Override
            public int sizeOf(Long key, RoundedDrawable bitmap) {
                return (int) bitmap.getSize();
            }

            @Override
            public boolean trimToSize(int arg0) {
                long runtimeAvail = Runtime.getRuntime().freeMemory();
                boolean runtimeNearlyDepleted = runtimeAvail < BetterHashMapLruCache.RUNTIME_AVAIL_MEM_THRESHOLD;
                long maxMemToUse = runtimeNearlyDepleted ? 0 : cacheSize;
                return super.trimToSize((int) (maxMemToUse));
            }
        };
        cursorObjectMemoryCache = new BetterHashMapLruCache<Integer, IntranetUser>(
                (int) (cacheSize * 0.3),150);
        
        listView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                firstVisiblePosition = firstVisibleItem;
                lastVisiblePosition = firstVisibleItem+visibleItemCount;
                
                runTaskCleaning();
            }
        });
        firstVisiblePosition = listView.getFirstVisiblePosition();
        lastVisiblePosition = listView.getLastVisiblePosition();
    }
    Thread cleaningThread;
    private void runTaskCleaning(){
        if(taskIdentifiers.size()==0 || (cleaningThread!= null && cleaningThread.isAlive()))
            return;
        
        cleaningThread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                List<LoadImageTask> tasks = new ArrayList<UserListAdapter.LoadImageTask>(taskIdentifiers.values());
                final List<Long> toRemove = new ArrayList<Long>();
                for(LoadImageTask task : tasks){
                    if(!(isVisible(task.position))){
                        toRemove.add(task.user.getId().longValue());
                        task.cancel(true);
                    }
                }

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(Long id : toRemove){
                            taskIdentifiers.remove(id);
                        }
                    }
                });
            }
        });
        cleaningThread.start();
    }
    
    
    
    @Override
    public Object getItem(int position) {
        return userMapper.mapEntity(getCursor(), position);
    }

    public class ViewHolder implements Cloneable {
        private TextView userNameView;
        private ImageView userImageView;
        private View phoneRow;
        private View ircRow;
        private TextView roleView;
        private TextView groupView;
        private TextView lateView;
        private TextView phoneView;
        private TextView ircView;

        private View parent;
        private Integer position;
    }

    private class LoadImageTask extends AsyncTaskExAggressive<Void, Void, RoundedDrawable> {

        private ViewHolder viewHolder;
        private IntranetUser user;
        private int position;

        public LoadImageTask(ViewHolder holder, IntranetUser user, int position) {
            super(Thread.MIN_PRIORITY);
            this.viewHolder = holder;
            this.user = user;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            if (!this.isCancelled()) {
                viewHolder.userImageView.setVisibility(View.INVISIBLE);
            }
            super.onPreExecute();
        }

        @Override
        protected RoundedDrawable doInBackground(Void... params) {
            return getImageSync(user);
        }

        @Override
        protected void onPostExecute(RoundedDrawable result) {
            super.onPostExecute(result);
            if (result != null && !context.isFinishing()) {
                addBitmapToMemoryCache(user.getId().longValue(), result);
                if (!isCancelled()) {
                    if (isVisible(position) && viewHolder.position == position) {
                        viewHolder.userImageView.setImageDrawable(result);
                        viewHolder.userImageView.setVisibility(View.VISIBLE);
                        applyAnimation(viewHolder.userImageView);
                    }
                }
            }
            taskIdentifiers.remove(user.getId().longValue());
        }
    }
    
    private boolean isVisible(int position){
        return position >= firstVisiblePosition-4
                && position <= lastVisiblePosition+4;
    }
    
    private synchronized RoundedDrawable getImageSync(IntranetUser user){
        Bitmap resultBitmap = null;
        RoundedDrawable result = null;
        resultBitmap = BitmapUtils
                .getTempBitmap(context, user.getId().toString());
        if (resultBitmap == null) {
            HTTPResponse<Bitmap> reponse = api.downloadBitmap("https://intranet.stxnext.pl"
                    + user.getImageUrl());
            if (reponse != null && reponse.getExpectedResponse() != null) {
                BitmapUtils.saveTempBitmap(context, reponse.getExpectedResponse(), user.getId()
                        .toString());
                resultBitmap = reponse.getExpectedResponse();
            }
        }
        if(resultBitmap!=null){
            result = new RoundedDrawable(resultBitmap);
            result.setCornerRadius(15F);
        }
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Log.e(this.getClass().getName(),"loading bitmap",e);
        }
        return result;
    }

    private void applyAnimation(final ImageView view) {
        view.clearAnimation();
        if (view.getAnimation() != null) {
            view.getAnimation().reset();
        } else {
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(850);

            AnimationSet animation = new AnimationSet(false);
            animation.addAnimation(fadeIn);
            view.setAnimation(animation);
        }
        view.getAnimation().startNow();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final ViewHolder holder = new ViewHolder();

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

        // imageView.setCornersRadius(12F);

        holder.parent = parentView;
        holder.userNameView = nameView;
        holder.userImageView = imageView;
        holder.position = cursor.getPosition();

        parentView.setTag(holder);

        return parentView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final ViewHolder holder = (ViewHolder) view.getTag();
        int position = cursor.getPosition();
        holder.position = position;

        IntranetUser user = getCursorObjectFromMemCache(position);
        if (user == null) {
            user = userMapper.mapEntity(cursor, position);
            addCursorObjectToMemoryCache(position, user);
        }

        holder.userNameView.setText(user.getName());
        boolean taskInProgress = taskIdentifiers.get(user.getId().longValue()) != null;

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
            holder.lateView.setText("Nieobecność");
            holder.lateView.setTextColor(Color.parseColor("#22ff0000"));
            absenceDataPresent = true;
        }

        else if (user.getLatenessDisplayData() != null) {
            holder.lateView.setText("Spóźnienie");
            holder.lateView.setTextColor(Color.parseColor("#220000ff"));
            absenceDataPresent = true;
        }

        holder.lateView.setVisibility(absenceDataPresent ? View.VISIBLE
                : View.INVISIBLE);

        if (user.getRoles().size() > 0) {
            holder.roleView.setText(user.getRoles().get(0));
            holder.roleView.setVisibility(View.VISIBLE);
        } else {
            holder.roleView.setVisibility(View.INVISIBLE);
        }

        if (user.getGroups().size() > 0) {
            holder.groupView.setText(user.getGroups().get(0));
            holder.groupView.setVisibility(View.VISIBLE);
        } else {
            holder.groupView.setVisibility(View.INVISIBLE);
        }

        holder.userImageView.setImageBitmap(null);
        holder.userImageView.setVisibility(View.INVISIBLE);
        if (!taskInProgress) {
            holder.userImageView.setVisibility(View.INVISIBLE);
            RoundedDrawable bmp = getBitmapFromMemCache(user.getId().longValue());
            if (bmp != null) {
                holder.userImageView.setImageDrawable(bmp);
                holder.userImageView.setVisibility(View.VISIBLE);
            } else {
                LoadImageTask imageTask = new LoadImageTask(holder, user,
                        position);
                taskIdentifiers.put(user.getId().longValue(), imageTask);
                imageTask.execute();
            }
        }
    }
    
}
