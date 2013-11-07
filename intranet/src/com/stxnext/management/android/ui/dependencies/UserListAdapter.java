
package com.stxnext.management.android.ui.dependencies;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.ui.controls.RoundedImageView;
import com.stxnext.management.android.web.api.IntranetApi;

public class UserListAdapter extends BaseAdapter {

    private final Activity context;
    private List<IntranetUser> users;
    private LruCache<Long, Bitmap> memoryCache;
    private HashMap<Long, LoadImageTask> taskIdentifiers = new HashMap<Long, LoadImageTask>();
    private ListView listView;
    private IntranetApi api;

    public void addBitmapToMemoryCache(Long key, Bitmap bitmap) {
        if (key != null && bitmap != null) {
            memoryCache.remove(key);
            memoryCache.put(key, bitmap);
        }
    }

    public void clearCache(){
        memoryCache.evictAll();
    }
    
    public Bitmap getBitmapFromMemCache(Long key) {
        if (key != null) {
            return (Bitmap) memoryCache.get(key);
        }
        return null;
    }

    public UserListAdapter(Activity context, ListView listView, List<IntranetUser> users) {
        this.context = context;
        this.users = users;
        this.listView = listView;
        this.api = IntranetApi.getInstance(context.getApplication());
        
        final int memClass = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
            final int cacheSize = 1024 * 1024 * memClass;
            memoryCache = new LruCache<Long, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(Long key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
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
            convertView = inflater.inflate(R.layout.adapter_users, parent, false);

            TextView nameView = (TextView) convertView
                    .findViewById(R.id.nameView);
            RoundedImageView imageView = (RoundedImageView) convertView
                    .findViewById(R.id.userImageView);
            imageView.setCornersRadius(12F);

            holder.parent = convertView;
            holder.userNameView = nameView;
            holder.userImageView = imageView;
            holder.position = position;
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
        boolean taskInProgress = taskIdentifiers.get(item
                .getId().longValue()) != null;

        holder.userImageView.setImageBitmap(null);
        holder.userImageView.setVisibility(View.INVISIBLE);
        if (!taskInProgress) {
            holder.userImageView.setVisibility(View.INVISIBLE);
            Bitmap bmp = getBitmapFromMemCache(item.getId()
                    .longValue());
            if (bmp != null) {
                holder.userImageView.setImageBitmap(bmp);
                holder.userImageView.setVisibility(View.VISIBLE);
            }
            else {
                LoadImageTask imageTask = new LoadImageTask(
                        holder, item, position);
                taskIdentifiers.put(item.getId().longValue(),
                        imageTask);
                imageTask.execute();
            }
        }

        return holder.parent;
    }

    public class ViewHolder implements Cloneable {
        private TextView userNameView;
        private RoundedImageView userImageView;
        private View parent;
        private Integer position;
    }

    private class LoadImageTask extends AsyncTaskEx<Void, Void, Bitmap> {

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
            Bitmap result = null;
            result = BitmapUtils.getTempBitmap(context, user.getId().toString());
            if(result == null){
                //Log.e("got saved bitmap",user.getImageUrl());
                result = api.downloadBitmap("https://intranet.stxnext.pl"+user.getImageUrl());
                if(result!=null){
                    BitmapUtils.saveTempBitmap(context, result, user.getId().toString());
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null && !context.isFinishing()) {
                addBitmapToMemoryCache(user.getId().longValue(),
                        result);
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
}
