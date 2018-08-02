package org.neuinfo.foundry.enhancers.plugins;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.neuinfo.foundry.common.util.JSONPathProcessor;
import org.neuinfo.foundry.common.util.JSONUtils;
import org.neuinfo.foundry.consumers.plugin.IPlugin;
import org.neuinfo.foundry.consumers.plugin.Result;
import org.neuinfo.foundry.enhancers.common.ProvenanceHelper;
import org.neuinfo.foundry.enhancers.common.TitleEnhanced;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by valentine 2018-06-30
 * Idea:
 * If title is a common word, then enhance
 * if title is a common pattern, then enhnace
 * if title is short, enhance
 *
 * need common word list
 * common pattern list for an ingest session
 *
 */
public class TitleEnhancer implements IPlugin {
    private int minTitleLength = 20;

    private final static Logger logger = Logger.getLogger(TitleEnhancer.class);
    private List<String> commonWords = new ArrayList<>();
    private List<String> commonPatterns = new ArrayList<>();
    private List<String> ingestPatterns = new ArrayList<>();

    @Override
    public void initialize(Map<String, String> options) throws Exception {

    }

    @Override
    public Result handle(DBObject docWrapper) {
        try {


            List<String>textStrings = new ArrayList<String>(10);
            String title = null;
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
            if (title == null  ||title.length() < minTitleLength ) {

               // List<Object> orgs = jpp.find("$..'gmd:pointOfContact'", json);
                TitleEnhanced te = new TitleEnhanced();
                te.setOriginalTitle(title);

                List<Object> objects = jpp.find("$..'gmd:identificationInfo'.'gmd:MD_DataIdentification'.'gmd:pointOfContact'[*].'gmd:CI_ResponsibleParty'.'gmd:organisationName'.'gco:CharacterString'.'_$'", json);
                if (objects != null) {
                    for (Object o : objects) {
                        textStrings.add(o.toString());
                    }
                }

                for (String s : textStrings) {

                    if (title != null  ) {
                       title= title + " (from Organization " + s + ")";
                        te.setOrg(s);
                    }
                }



                te.setTitle(title);
                JSONObject js = new JSONObject(te);


                if (js.length() > 0) {
                    DBObject data = (DBObject) docWrapper.get("Data");
                    data.put("title", JSONUtils.encode(js));
                    ProvenanceHelper.ProvData provData = new ProvenanceHelper.ProvData(primaryKey,
                            ProvenanceHelper.ModificationType.Added);


                    provData.setSourceName(sourceName).setSrcId(srcId);
                    //EnhancerUtils.prepTemporalProv(te, provData);
                    provData.addModifiedFieldProv("Title Enhanced: ");
                    StringBuilder sb = new StringBuilder(128);
                    sb.append( title);

                    provData.addModifiedFieldProv(sb.toString().trim());
                    ProvenanceHelper.saveEnhancerProvenance("titleEnhancer", provData, docWrapper);
                    return new Result(docWrapper, Result.Status.OK_WITH_CHANGE);
                } else {
                    return new Result(docWrapper, Result.Status.OK_WITHOUT_CHANGE);
                }
            } return new Result(docWrapper, Result.Status.OK_WITHOUT_CHANGE);
        } catch (Throwable t) {
            t.printStackTrace();
            Result r = new Result(docWrapper, Result.Status.ERROR);
            r.setErrMessage(t.getMessage());
            return r;
        }
    }

    @Override
    public String getPluginName() {
        return "titleEnhancer";
    }


}
