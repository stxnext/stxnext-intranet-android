
package com.stxnext.management.android.games.poker.dto;

import com.parse.ParseObject;

public class Vote extends AbstractDTO {

    private static final String ENTITY_NAME = "vote";

    private static final String FIELD_VALUE = "value";
    private static final String FIELD_AUTHOR = "author";

    private Object externalId;
    private Number value;
    private Person author;

    @Override
    public ParseObject toParse() {
        ParseObject po = new ParseObject(ENTITY_NAME);
        if (this.externalId != null) {
            po.setObjectId(String.valueOf(this.externalId));
        }
        po.put(FIELD_VALUE, this.value);
        po.put(FIELD_AUTHOR, this.author.toParse());
        return null;
    }

    @Override
    public void inflateFromParse(ParseObject po) {
        this.externalId = po.getObjectId();
        this.author = new Person(po.getParseObject(FIELD_AUTHOR));
        this.value = po.getNumber(FIELD_VALUE);
    }

    public Object getExternalId() {
        return externalId;
    }

    public void setExternalId(Object externalId) {
        this.externalId = externalId;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

}
