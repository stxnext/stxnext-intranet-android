package com.stxnext.management.android.games.poker.dto;

import java.util.List;

import com.parse.ParseObject;

public class Ticket extends AbstractDTO{

    private static final String ENTITY_NAME = "ticket";

    private static final String FIELD_NAME = "name";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_FINAL_ESTIMATE = "finalEstimate";
    private static final String FIELD_ROUNDS = "rounds";

    private Object externalId;
    private String name;
    private String description;
    private Number finalEstimate;
    private List<Round> rounds;

    @Override
    public ParseObject toParse() {
        ParseObject po = new ParseObject(ENTITY_NAME);
        if(this.externalId!=null){
            po.setObjectId(String.valueOf(this.externalId));
        }
        po.put(FIELD_NAME, this.name);
        po.put(FIELD_DESCRIPTION, this.description);
        po.put(FIELD_FINAL_ESTIMATE, this.finalEstimate);
        po.put(FIELD_ROUNDS, convertListToParseList(this.rounds));
        return po;
    }

    @Override
    public void inflateFromParse(ParseObject po) {
        this.externalId = po.getObjectId();
        this.description = po.getString(FIELD_DESCRIPTION);
        this.name = po.getString(ENTITY_NAME);
        this.finalEstimate = po.getNumber(FIELD_FINAL_ESTIMATE);
        
        List<ParseObject> parses = po.getList(FIELD_ROUNDS);
        this.rounds = inflateListFromParseObjects(parses, Round.class);
    }

    public Object getExternalId() {
        return externalId;
    }

    public void setExternalId(Object externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Number getFinalEstimate() {
        return finalEstimate;
    }

    public void setFinalEstimate(Number finalEstimate) {
        this.finalEstimate = finalEstimate;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }
    
    

}
