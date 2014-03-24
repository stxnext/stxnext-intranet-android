
package com.stxnext.management.android.ui.dependencies;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.stxnext.management.android.R;

public class Popup {

    private PopupWindow popupWindow;
    private OnPopupItemClickListener onPopupItemClickListener;
    private ScrollView rootView;
    private LinearLayout holderView;
    private LayoutInflater lInf;
    private TextView anchorView;
    private List<PopupItem> items;

    public Popup(Context context, TextView anchor) {
        super();
        this.anchorView = anchor;
        this.popupWindow = new PopupWindow(context);
        popupWindow.setBackgroundDrawable(null);
        // popupWindow.setAnimationStyle(R.style.PopupAnimation);
        lInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = (ScrollView) lInf.inflate(R.layout.popup_view, null);
        holderView =  (LinearLayout) rootView.findViewById(R.id.popup_slots);
    }

    public void addItems(List<PopupItem> items) {
        this.items = items;
        int count = 0;
        for (PopupItem item : items) {
            addItem(item, count);
            count++;
        }
    }

    public void setSelected(Object content) {
        String langFound = null;
        for (PopupItem item : items) {
            boolean matchFound = item.getContent().equals(content);
            item.setSelected(matchFound);
            if (matchFound)
                langFound = item.getTitleText();
        }
        anchorView.setHint(null);
        anchorView.setText(langFound);
    }
    
    public Object getSelected(){
        for (PopupItem item : items) {
            if(item.isSelected())
                return item.getContent();
        }
        return null;
    }

    private void addItem(final PopupItem item, int position) {
        View view = lInf.inflate(R.layout.popup_item, null);
        item.setLayout(view);
        TextView textView = (TextView) view.findViewById(R.id.tv_popup_item);
        textView.setText(item.getTitleText());
        holderView.addView(view);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onPopupItemClickListener.onItemClick(item.getContent());
                popupWindow.dismiss();
            }
        });

    }
    
    public void dismiss(){
        popupWindow.dismiss();
    }

    public void toggle() {

        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        else {
            popupWindow.setWidth(anchorView.getWidth());
            popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(rootView);

            popupWindow.showAsDropDown(anchorView, 0, 0);
        }
    }

    public void setOnItemClickListener(OnPopupItemClickListener onPopupItemClickListener) {
        this.onPopupItemClickListener = onPopupItemClickListener;
    }

    public interface OnPopupItemClickListener {
        public abstract void onItemClick(Object content);
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

}
