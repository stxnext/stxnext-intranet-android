package com.stxnext.management.android.ui.fragment;

import com.stxnext.management.android.dto.postmessage.AbstractMessage;

public interface FormActionReceiver{
    public void onSubmitFormWithMessage(AbstractMessage message);
}