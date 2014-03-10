
package com.stxnext.management.android.games.poker.dto;

import java.util.Date;
import java.util.List;

import com.parse.ParseObject;

public class Round extends AbstractDTO {

    private static final String ENTITY_NAME = "round";

    private static final String FIELD_INDEX = "index";
    private static final String FIELD_START_DATE = "startDate";
    private static final String FIELD_END_DATE = "endDate";
    private static final String FIELD_TIMEOUT = "timeout";
    private static final String FIELD_VOTES = "votes";

    private Object externalId;
    private Number index;
    private Date startDate;
    private Date endDate;
    private Number timeout;
    private List<Vote> votes;

    public Round(ParseObject po) {
        this.inflateFromParse(po);
    }

    @Override
    public ParseObject toParse() {
        ParseObject po = ParseObject.create(ENTITY_NAME);
        if (this.externalId != null) {
            po.setObjectId(String.valueOf(this.externalId));
        }
        po.put(FIELD_INDEX, this.index);
        po.put(FIELD_START_DATE, this.startDate);
        po.put(FIELD_END_DATE, this.endDate);
        po.put(FIELD_TIMEOUT, this.timeout);
        po.put(FIELD_VOTES, convertListToParseList(this.votes));

        return po;
    }

    @Override
    public void inflateFromParse(ParseObject po) {
        this.externalId = po.getObjectId();
        this.index = po.getNumber(FIELD_INDEX);
        this.startDate = po.getDate(FIELD_START_DATE);
        this.endDate = po.getDate(FIELD_END_DATE);
        this.timeout = po.getNumber(FIELD_TIMEOUT);

        List<ParseObject> parses = po.getList(FIELD_VOTES);
        this.votes = inflateListFromParseObjects(parses, Vote.class);
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public Object getExternalId() {
        return externalId;
    }

    public void setExternalId(Object externalId) {
        this.externalId = externalId;
    }

    public Number getIndex() {
        return index;
    }

    public void setIndex(Number index) {
        this.index = index;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Number getTimeout() {
        return timeout;
    }

    public void setTimeout(Number timeout) {
        this.timeout = timeout;
    }

}
