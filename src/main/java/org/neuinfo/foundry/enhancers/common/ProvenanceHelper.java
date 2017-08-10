package org.neuinfo.foundry.enhancers.common;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neuinfo.foundry.common.model.DocWrapper;
import org.neuinfo.foundry.common.provenance.ProvenanceRec;
import org.neuinfo.foundry.common.util.Assertion;
import org.neuinfo.foundry.common.util.JSONUtils;

import java.text.ParseException;
import java.util.*;

/**
 * Created by bozyurt on 8/10/17.
 */
public class ProvenanceHelper {

    public static void saveProvRec2DB(DBObject docWrapper, JSONObject provJSON, String currentVersion, Date processedDate) {
        DBObject history = (DBObject) docWrapper.get("History");

        DBObject provDO = (DBObject) history.get("prov");
        if (provDO == null) {
            provDO = new BasicDBObject();
            history.put("prov", provDO);
        }
        provDO.put("curVersion", currentVersion);
        provDO.put("lastProcessedDate", DocWrapper.formatDate(processedDate));
        BasicDBList events = (BasicDBList) provDO.get("events");
        if (events == null) {
            events = new BasicDBList();
            provDO.put("events", events);
        }
        events.add(JSONUtils.encode(provJSON, true));
    }


    public static void saveIngestProvRec2DB(DocWrapper docWrapper, JSONObject provJSON, String currentVersion, Date processedDate) {
        JSONObject history = docWrapper.getHistory();
        if (history == null) {
            history = new JSONObject();
            docWrapper.setHistory(history);
            history.put("batchId", docWrapper.getBatchId());
        }
        JSONObject provJs;
        if (!history.has("prov")) {
            provJs = new JSONObject();
            history.put("prov", provJs);
        } else {
            provJs = history.getJSONObject("prov");
        }
        provJs.put("curVersion", currentVersion);
        provJs.put("lastProcessedDate", DocWrapper.formatDate(processedDate));
        JSONArray events = null;
        if (!provJs.has("events")) {
            events = new JSONArray();
            provJs.put("events", events);
        }
        JSONUtils.escapeJson(provJSON);
        events.put(provJSON);
    }

    public static void clearProv(DBObject docWrapper) {
        DBObject history = (DBObject) docWrapper.get("History");

        DBObject provDO = (DBObject) history.get("prov");
        if (provDO != null) {
            history.removeField("prov");
        }
    }

    public static ProvState getCurrentProvState(DBObject docWrapper) throws ParseException {
        DBObject history = (DBObject) docWrapper.get("History");

        BasicDBObject provDO = (BasicDBObject) history.get("prov");
        if (provDO == null) {
            return null;
        }
        String currentVersion = (String) provDO.get("curVersion");
        Date lpDate = DocWrapper.getDate((String) provDO.get("lastProcessedDate"));
        return new ProvState(currentVersion, lpDate);
    }


    public static class ProvState {
        final String curVersion;
        final Date lastProcessedDate;

        public ProvState(String curVersion, Date lastProcessedDate) {
            this.curVersion = curVersion;
            this.lastProcessedDate = lastProcessedDate;
        }

        public String getCurVersion() {
            return curVersion;
        }

        public Date getLastProcessedDate() {
            return lastProcessedDate;
        }
    }

    public static String saveEnhancerProvenance(String activityName, ProvData provData, DBObject docWrapper) {
        try {

            ProvState provState = getCurrentProvState(docWrapper);
            Assertion.assertNotNull(provState);

            ProvenanceRec.Builder builder = new ProvenanceRec.Builder("http://example.org", "foundry");
            String startTime = EnhancerUtils.getTimeInProvenanceFormat(provState.getLastProcessedDate());
            Date now = new Date();
            String docCreationTime = EnhancerUtils.getTimeInProvenanceFormat(now);
            String label = provData.prepLabel();
            String howLabel = provData.prepLabelHow();
            String version = provState.getCurVersion();
            String inDocId = builder.entityWithAttr("UUID=" + provData.docIdentifier, "creationTime=" + startTime,
                    "sourceId=" + provData.srcId,
                    "label=" + label, "version=" + version).getLastGeneratedId();
            String nextVersion = org.neuinfo.foundry.common.util.Utils.nextVersion(version);
            String outDocId = builder.entityWithAttr("UUID=" + provData.docIdentifier,
                    "creationTime=" + docCreationTime,
                    "label=" + label,
                    "version=" + nextVersion).getLastGeneratedId();
            String activityId = builder.activityWithAttr(activityName, docCreationTime,
                    EnhancerUtils.getTimeInProvenanceFormat(), "prov:how=" + howLabel).getLastGeneratedId();
            ProvenanceRec provenanceRec = builder.used(activityId, inDocId)
                    .wasDerivedFrom(outDocId, inDocId, activityId)
                    .wasGeneratedBy(outDocId, activityId).build();

            String provJSON = provenanceRec.asJSON();
            //    System.out.println(provJSON);
            saveProvRec2DB(docWrapper, new JSONObject(provJSON), nextVersion, now);

            return null;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }


    public static enum ModificationType {
        Added, Modified, Deleted, Ingested, None
    }

    public static class ProvData {
        String sourceName;
        String srcId;
        String batchId;
        String version = "1.0";
        String docTitle;
        String docIdentifier;
        List<String> modifiedList = new ArrayList<String>(5);
        ModificationType modType;

        public ProvData(String docIdentifier, ModificationType modType) {
            this.modType = modType;
            this.docIdentifier = docIdentifier;
        }


        public String getDocTitle() {
            return docTitle;
        }

        public ProvData setDocTitle(String docTitle) {
            this.docTitle = docTitle;
            return this;
        }

        public String getSourceName() {
            return sourceName;
        }

        public ProvData setSourceName(String sourceName) {
            this.sourceName = sourceName;
            return this;
        }

        public String getSrcId() {
            return srcId;
        }

        public ProvData setSrcId(String srcId) {
            this.srcId = srcId;
            return this;
        }

        public String getVersion() {
            return version;
        }

        public ProvData setVersion(String version) {
            this.version = version;
            return this;
        }

        public void addModifiedFieldProv(String fieldProv) {
            this.modifiedList.add(fieldProv);
        }

        public List<String> getModifiedList() {
            return modifiedList;
        }

        public String getBatchId() {
            return batchId;
        }

        public ModificationType getModType() {
            return modType;
        }

        public String prepLabel() {
            StringBuilder sb = new StringBuilder(80);
            sb.append(sourceName).append(":").append(docIdentifier);
            if (docTitle != null && docTitle.isEmpty()) {
                sb.append("::").append(docTitle);
            }
            return sb.toString();
        }

        public String prepLabelHow() {
            StringBuilder sb = new StringBuilder(128);
            if (!this.modifiedList.isEmpty()) {
                for (Iterator<String> iter = modifiedList.iterator(); iter.hasNext(); ) {
                    sb.append(iter.next());
                    if (iter.hasNext()) {
                        sb.append(". ");
                    } else {
                        sb.append('.');
                    }
                }
            } else {
                if (this.modType == ModificationType.Ingested) {
                    sb.append("Added document with uuid ").append(docIdentifier);
                }
            }

            return sb.toString();
        }
    }
}