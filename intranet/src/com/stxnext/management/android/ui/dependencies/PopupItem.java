
package com.stxnext.management.android.ui.dependencies;

import android.view.View;

import com.stxnext.management.android.R;

public class PopupItem {
    private String titleText;
    private Object content;
    private View layout;
    private boolean selected;

    public PopupItem(String titleText, Object content) {
        super();
        this.titleText = titleText;
        this.content = content;
    }

    public View getLayout() {
        return layout;
    }

    public void setLayout(View layout) {
        this.layout = layout;
    }

    public String getTitleText() {
        return titleText;
    }

    public Object getContent() {
        return content;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if(layout!=null){
            View v = layout.findViewById(R.id.checkmark);
            v.setVisibility(selected?View.VISIBLE:View.INVISIBLE);
        }
    }

}
