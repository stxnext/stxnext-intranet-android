package com.stxnext.management.android.games.poker.dto;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.parse.ParseObject;

public abstract class AbstractDTO {

    public abstract ParseObject toParse();
    public abstract void inflateFromParse(ParseObject po);
    
    protected <T extends AbstractDTO> List<T> inflateListFromParseObjects(List<ParseObject> pObjects, Class<T> clazz){
        List<T> pojos = new ArrayList<T>();
        if(pObjects != null){
            for(ParseObject po : pObjects){
                try {
                    T target = clazz.newInstance();
                    target.inflateFromParse(po);
                    pojos.add(target);
                } catch (InstantiationException e) {
                    Log.e(this.getClass().getName(),"",e);
                } catch (IllegalAccessException e) {
                    Log.e(this.getClass().getName(),"",e);
                }
            }
        }
        return pojos;
    }
    
    protected <T extends AbstractDTO> List<ParseObject> convertListToParseList(List<T> pojos){
        List<ParseObject> parses = new ArrayList<ParseObject>();
        if(pojos != null){
            for(T pojo : pojos){
                parses.add(pojo.toParse());
            }
        }
        return parses;
    }
    
}
