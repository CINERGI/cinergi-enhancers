package org.neuinfo.foundry.enhancers.plugins;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neuinfo.foundry.common.util.JSONPathProcessor;
import org.neuinfo.foundry.common.util.JSONUtils;
import org.neuinfo.foundry.consumers.plugin.IPlugin;
import org.neuinfo.foundry.consumers.plugin.Result;
import org.neuinfo.foundry.enhancers.common.ProvenanceHelper;
import org.neuinfo.foundry.enhancers.common.Temporal;

import java.util.*;

/**
 * Created by bozyurt on 4/27/16.
 */
public class TemporalEnhancer implements IPlugin {
    private Temporal temporal;

    private final static Logger logger = Logger.getLogger(TemporalEnhancer.class);

    @Override
    public void initialize(Map<String, String> options) throws Exception {
        this.temporal = new Temporal();
    }

    @Override
    public Result handle(DBObject docWrapper) {
        try {
            Set<String> organizationSet = new HashSet<String>();
            List<String>textStrings = new ArrayList<String>(10);
            String title;
            DBObject originalDoc = (DBObject) docWrapper.get("OriginalDoc");
            DBObject siDBO = (DBObject) docWrapper.get("SourceInfo");
            String srcId = siDBO.get("SourceID").toString();
            String sourceName = siDBO.get("Name").toString();
            String primaryKey = docWrapper.get("primaryKey").toString();
            JSONObject json = JSONUtils.toJSON((BasicDBObject) originalDoc, false);
            JSONPathProcessor jpp = new JSONPathProcessor();
            List<Object> titles = jpp.find("$..'gmd:identificationInfo'.'gmd:MD_DataIdentification'.'gmd:citation'" +
                    ".'gmd:CI_Citation'.'gmd:title'.'gco:CharacterString'.'_$'", json);
            if (titles != null){
                title = titles.get(0).toString();
            }
            List<Object> objects = jpp.find("$..'gmd:abstract'.'gco:CharacterString'.'_$'", json);
            if (objects != null) {
                for (Object o : objects) {
                    textStrings.add(o.toString());
                }
            }
             objects = jpp.find("$..'gmd:purpose'.'gco:CharacterString'.'_$'", json);
            if (objects != null) {
                for (Object o : objects) {
                    textStrings.add(o.toString());
                }
            }
            ArrayList<String> dates = new ArrayList<String>();
            for (String s : textStrings){
                ArrayList<String> dateResult =  Temporal.getdates(s, temporal.getCoreNLP());
                if (dateResult != null ) {
                    dates.addAll(dateResult);
                }
            }


            JSONArray jsArr = new JSONArray();
            for (String org : dates) {
                TemporalDate td = new TemporalDate();
                td.start = org;
                JSONObject js = new JSONObject();
                js.put("date", td.start);
                jsArr.put(td);
            }

            if (jsArr.length() > 0) {
                DBObject data = (DBObject) docWrapper.get("Data");
                data.put("temporal", JSONUtils.encode(jsArr));
                ProvenanceHelper.ProvData provData = new ProvenanceHelper.ProvData(primaryKey, ProvenanceHelper.ModificationType.Added);
                Map<String, List<TemporalDate>> category2KWIListMap = new HashMap<String, List<TemporalDate>>(7);


                provData.setSourceName(sourceName).setSrcId(srcId);
                //EnhancerUtils.prepKeywordsProv(category2KWIListMap, provData);
                ProvenanceHelper.saveEnhancerProvenance("temporalEnhancer", provData, docWrapper);
                return new Result(docWrapper, Result.Status.OK_WITH_CHANGE);
            } else {
                return new Result(docWrapper, Result.Status.OK_WITHOUT_CHANGE);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            Result r = new Result(docWrapper, Result.Status.ERROR);
            r.setErrMessage(t.getMessage());
            return r;
        }
    }

    @Override
    public String getPluginName() {
        return "temporalEnhancer";
    }

    public class TemporalDate {
        String start ;
        String end;
        String type;
    }
}
