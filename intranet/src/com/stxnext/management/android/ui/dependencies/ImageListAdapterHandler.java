package com.stxnext.management.android.ui.dependencies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.stxnext.management.android.web.api.HTTPResponse;
import com.stxnext.management.android.web.api.IntranetApi;

public class ImageListAdapterHandler {

    private BetterHashMapLruCache<Long, RoundedDrawable> memoryCache;
    private HashMap<Long, LoadImageTask> taskIdentifiers = new HashMap<Long, LoadImageTask>();
    private AbsListView listView;
    private IntranetApi api;
    int firstVisiblePosition;
    int lastVisiblePosition;
    private String baseImageUrl="https://intranet.stxnext.pl";
    private Activity activity;
    
    public ImageListAdapterHandler(AbsListView listView, Activity activity){
        this.api = IntranetApi.getInstance(activity.getApplication());
        this.activity = activity;
        memoryCache = BetterHashMapLruCache.getConfiguredInstance(Long.class, RoundedDrawable.class);
        listView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                firstVisiblePosition = firstVisibleItem;
                lastVisiblePosition = firstVisibleItem + visibleItemCount;
                runTaskCleaning();
            }
        });
        firstVisiblePosition = listView.getFirstVisiblePosition();
        lastVisiblePosition = listView.getLastVisiblePosition();
    }
    
    private void addBitmapToMemoryCache(Long key, RoundedDrawable bitmap) {
        if (key != null && bitmap != null) {
            memoryCache.put(key, bitmap);
        }
    }

    private RoundedDrawable getBitmapFromMemCache(Long key) {
        if (key != null) {
            return memoryCache.get(key);
        }
        return null;
    }
    
    public void clearCache() {
        memoryCache.trimToElementsCount(0);
        taskIdentifiers.clear();
    }
    
    public void clearTasks(){
        taskIdentifiers.clear();
    }
    
    public void onGetView(HandlerViewHolder holder, Long entityId, String imageUrl, int position){
        holder.imageview.setImageBitmap(null);
        holder.imageview.setVisibility(View.INVISIBLE);
        boolean taskInProgress = taskIdentifiers.get(entityId) != null;
        if (!taskInProgress) {
            holder.imageview.setVisibility(View.INVISIBLE);
            RoundedDrawable bmp = getBitmapFromMemCache(entityId);
            if (bmp != null) {
                holder.imageview.setImageDrawable(bmp);
                holder.imageview.setVisibility(View.VISIBLE);
            } else {
                LoadImageTask imageTask = new LoadImageTask(holder, entityId, imageUrl, position);
                taskIdentifiers.put(entityId, imageTask);
                imageTask.execute();
            }
        }
    }
    
    Thread cleaningThread;
    private void runTaskCleaning() {
        if (taskIdentifiers.size() == 0 || (cleaningThread != null && cleaningThread.isAlive()))
            return;

        final List<LoadImageTask> tasks = new ArrayList<LoadImageTask>(
                taskIdentifiers.values());
        cleaningThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Long> toRemove = new ArrayList<Long>();
                for (LoadImageTask task : tasks) {
                    if (!(isVisible(task.position))) {
                        toRemove.add(task.entityId);
                        task.cancel(true);
                    }
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (Long id : toRemove) {
                            taskIdentifiers.remove(id);
                        }
                    }
                });
            }
        });
        cleaningThread.start();
    }
    
    private class LoadImageTask extends AsyncTaskExAggressive<Void, Void, RoundedDrawable> {

        private HandlerViewHolder viewHolder;
        private Long entityId;
        private String imageUrl;
        private Integer position;

        public LoadImageTask(HandlerViewHolder viewHolder, Long entityId, String imageUrl,Integer position) {
            super(Thread.MIN_PRIORITY);
            this.viewHolder = viewHolder;
            this.entityId = entityId;
            this.position = position;
            this.imageUrl = imageUrl;
        }

        @Override
        protected void onPreExecute() {
            if (!this.isCancelled()) {
                viewHolder.imageview.setVisibility(View.INVISIBLE);
            }
            super.onPreExecute();
        }

        @Override
        protected RoundedDrawable doInBackground(Void... params) {
            return getImageSync(entityId,imageUrl);
        }

        @Override
        protected void onPostExecute(RoundedDrawable result) {
            super.onPostExecute(result);
            if (result != null && !activity.isFinishing()) {
                addBitmapToMemoryCache(entityId, result);
                if (!isCancelled()) {
                    if (isVisible(position) && position == viewHolder.position) {
                        viewHolder.imageview.setImageDrawable(result);
                        viewHolder.imageview.setVisibility(View.VISIBLE);
                        applyAnimation(viewHolder.imageview);
                    }
                    else {
                        Log.e("", "not visible setting image for position " + position + ",vhpos:"
                                + viewHolder.position + ",pos:" + position);
                    }
                }
                else {
                    Log.e("", "cancelled setting image for position " + position);
                }
            }
            taskIdentifiers.remove(entityId);
        }
    }

    private boolean isVisible(int position) {
        return position >= firstVisiblePosition - 4
                && position <= lastVisiblePosition + 4;
    }

    private synchronized RoundedDrawable getImageSync(Long entityId, String imageUrl) {
        Bitmap resultBitmap = null;
        RoundedDrawable result = null;
        resultBitmap = BitmapUtils
                .getTempBitmap(activity, String.valueOf(entityId));
        if (resultBitmap == null) {
            HTTPResponse<Bitmap> reponse = api.downloadBitmap(baseImageUrl
                    + imageUrl);
            if (reponse != null && reponse.getExpectedResponse() != null) {
                BitmapUtils.saveTempBitmap(activity, reponse.getExpectedResponse(), String.valueOf(entityId));
                resultBitmap = reponse.getExpectedResponse();
            }
        }
        if (resultBitmap != null) {
            result = new RoundedDrawable(resultBitmap);
            result.setCornerRadius(15F);
        }
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Log.e(this.getClass().getName(), "loading bitmap", e);
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
}
