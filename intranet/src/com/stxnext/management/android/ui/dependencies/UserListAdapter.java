
package com.stxnext.management.android.ui.dependencies;

import java.util.HashMap;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.storage.sqlite.EntityMapper;
import com.stxnext.management.android.storage.sqlite.dao.IntranetUserMapper;
import com.stxnext.management.android.ui.controls.RoundedImageView;
import com.stxnext.management.android.web.api.HTTPResponse;
import com.stxnext.management.android.web.api.IntranetApi;

public class UserListAdapter extends CursorAdapter {

    private final Activity context;
    private LruCache<Long, Bitmap> memoryCache;
    private LruCache<Integer, IntranetUser> cursorObjectMemoryCache;
    private HashMap<Long, LoadImageTask> taskIdentifiers = new HashMap<Long, LoadImageTask>();
    private ListView listView;
    private IntranetApi api;
    private EntityMapper<IntranetUser> userMapper;

    private boolean dontUseCursorCache;

    public void setDontUseCursorCache(boolean dontUseCursorCache) {
        this.dontUseCursorCache = dontUseCursorCache;
    }

    public void addBitmapToMemoryCache(Long key, Bitmap bitmap) {
        if (key != null && bitmap != null) {
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(Long key) {
        if (key != null) {
            return (Bitmap) memoryCache.get(key);
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
        memoryCache.evictAll();
        cursorObjectMemoryCache.evictAll();
    }

    public UserListAdapter(Activity context, Cursor c, ListView listView) {
        super(context, c);
        this.context = context;
        this.listView = listView;
        this.api = IntranetApi.getInstance(context.getApplication());
        this.userMapper = new IntranetUserMapper();

        final int memClass = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        final int cacheSize = 1024 * 1024 * memClass;
        memoryCache = new LruCache<Long, Bitmap>((int) (cacheSize * 0.7)) {
            @Override
            protected int sizeOf(Long key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
        cursorObjectMemoryCache = new LruCache<Integer, IntranetUser>(
                (int) (cacheSize * 0.3));
    }

    @Override
    public Object getItem(int position) {
        return userMapper.mapEntity(getCursor(), position);
    }

    public class ViewHolder implements Cloneable {
        private TextView userNameView;
        private RoundedImageView userImageView;
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

    private class LoadImageTask extends AsyncTaskExAggressive<Void, Void, Bitmap> {

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
        protected Bitmap doInBackground(Void... params) {
            return getImageSync(user);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null && !context.isFinishing()) {
                addBitmapToMemoryCache(user.getId().longValue(), result);
                if (!isCancelled()) {
                    if (position >= listView.getFirstVisiblePosition()
                            && position <= listView.getLastVisiblePosition()) {
                        viewHolder.userImageView.setImageBitmap(result);
                        viewHolder.userImageView.setVisibility(View.VISIBLE);
                        applyAnimation(viewHolder.userImageView);
                    }
                }
            }
            taskIdentifiers.remove(user.getId().longValue());
        }
    }
    
    private synchronized Bitmap getImageSync(IntranetUser user){
        Bitmap result = null;
        result = BitmapUtils
                .getTempBitmap(context, user.getId().toString());
        if (result == null) {
            HTTPResponse<Bitmap> reponse = api.downloadBitmap("https://intranet.stxnext.pl"
                    + user.getImageUrl());
            if (reponse != null && reponse.getExpectedResponse() != null) {
                BitmapUtils.saveTempBitmap(context, reponse.getExpectedResponse(), user.getId()
                        .toString());
                result = reponse.getExpectedResponse();
            }
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
        RoundedImageView imageView = (RoundedImageView) parentView
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
            Bitmap bmp = getBitmapFromMemCache(user.getId().longValue());
            if (bmp != null) {
                holder.userImageView.setImageBitmap(bmp);
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
