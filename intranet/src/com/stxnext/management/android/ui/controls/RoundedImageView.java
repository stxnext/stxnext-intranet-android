package com.stxnext.management.android.ui.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class RoundedImageView  extends ImageView {

    private Float cornersRadius;
    private Path clipPath = new Path();
    private RectF pathRect = new RectF(0, 0, 0, 0);
    private float pathOffset = 0F;

    public void setPathOffset(float pathOffset) {
        this.pathOffset = pathOffset;
    }

    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        if (cornersRadius != null && !this.isHWAcceleratedSafe(canvas)) {
//            pathRect.right = this.getWidth();
//            pathRect.bottom = this.getHeight()+pathOffset;
//            clipPath.addRoundRect(pathRect, this.cornersRadius,
//                    this.cornersRadius, Path.Direction.CW);
//            canvas.clipPath(this.clipPath);
//        }

        super.onDraw(canvas);
    }

    private boolean isHWAcceleratedSafe(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return canvas.isHardwareAccelerated();
        }
        return false;
    }

    public void setCornersRadius(Float cornersRadius) {
//        if (cornersRadius != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            }
//        }
        this.cornersRadius = cornersRadius;
    }

    public interface ExtendedImageViewListener {
        public void onObservedSizeChanged(int w, int h);
    }

}
