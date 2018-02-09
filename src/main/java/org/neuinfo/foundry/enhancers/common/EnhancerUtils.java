package org.neuinfo.foundry.enhancers.common;

import org.neuinfo.foundry.common.util.KeywordInfo;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by bozyurt on 8/10/17.
 */
public class EnhancerUtils {
    public static String getTimeInProvenanceFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date());
    }

    public static String getTimeInProvenanceFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    public static void prepKeywordsProv(Map<String, List<KeywordInfo>> category2KWIListMap, ProvenanceHelper.ProvData provData) {
        if (category2KWIListMap == null || category2KWIListMap.isEmpty()) {
            provData.addModifiedFieldProv("No keywords are added");
            return;
        }
        for (String category : category2KWIListMap.keySet()) {
            List<KeywordInfo> kwiList = category2KWIListMap.get(category);
            StringBuilder sb = new StringBuilder(128);
            sb.append("Added keywords ");
            for (Iterator<KeywordInfo> iter = kwiList.iterator(); iter.hasNext(); ) {
                KeywordInfo kwi = iter.next();
                sb.append(kwi.getTerm());
                if (iter.hasNext()) {
                    sb.append(',');
                }
            }
            sb.append(" for category ").append(category);
            provData.addModifiedFieldProv(sb.toString().trim());
        }
    }

    public static void prepTemporalProv( List<Temporal> temporalList, ProvenanceHelper.ProvData provData) {
        if (temporalList == null || temporalList.isEmpty()) {
            provData.addModifiedFieldProv("No dates are added");
            return;
        }

            StringBuilder sb = new StringBuilder(128);
            sb.append("Added dates ");
            for (Iterator<Temporal> iter = temporalList.iterator(); iter.hasNext(); ) {
                Temporal kwi = iter.next();
                sb.append(kwi.getIsoString());
                if (iter.hasNext()) {
                    sb.append(',');
                }
            }
            //sb.append(" for category ").append(aDate);
            provData.addModifiedFieldProv(sb.toString().trim());

    }
}
