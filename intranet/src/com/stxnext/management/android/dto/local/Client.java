
package com.stxnext.management.android.dto.local;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.android.dto.postmessage.AbstractMessage;

public class Client extends AbstractMessage {

    //we should go that way so we don't need to have different names for json fields and database. Consider ormlite if have too much time
    public interface ClientFields{
        public static final String ENTITY_NAME = "client";
        public static final String NAME = "name";
        public static final String EXTERNAL_ID = "id"; // as distinct from _id android native id should be treated as externalId
    }
    
    @Expose
    @SerializedName(ClientFields.NAME)
    String name;
    @Expose
    @SerializedName(ClientFields.EXTERNAL_ID)
    Number id;

    public String getName() {
        return name;
    }

    public Number getExternalId() {
        return id;
    }

}
