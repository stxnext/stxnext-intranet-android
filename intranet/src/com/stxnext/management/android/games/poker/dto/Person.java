
package com.stxnext.management.android.games.poker.dto;

import com.parse.ParseObject;

public class Person extends AbstractDTO {
    private static final String ENTITY_NAME = "person";

    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_REVISION = "revision";

    private Object externalId;
    private String email;
    private Number revision;

    public Person(){};
    
    public Person(ParseObject po) {
        this.inflateFromParse(po);
    }

    @Override
    public ParseObject toParse() {
        ParseObject po = new ParseObject(ENTITY_NAME);
        po.put(FIELD_EMAIL, this.email);
        po.put(FIELD_REVISION, this.revision);
        if (this.externalId != null) {
            po.setObjectId(String.valueOf(this.externalId));
        }
        return po;
    }

    @Override
    public void inflateFromParse(ParseObject po) {
        this.externalId = po.getObjectId();
        this.email = po.getString(FIELD_EMAIL);
        this.revision = po.getNumber(FIELD_REVISION);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Number getRevision() {
        return revision;
    }

    public void setRevision(Number revision) {
        this.revision = revision;
    }

    public Object getExternalId() {
        return externalId;
    }

    public void setExternalId(Object externalId) {
        this.externalId = externalId;
    }

}
