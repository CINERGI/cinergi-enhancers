package org.neuinfo.foundry.enhancers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
import org.neuinfo.foundry.common.util.JSONUtils;
import org.neuinfo.foundry.common.util.Utils;
import org.neuinfo.foundry.consumers.plugin.IPlugin;
import org.neuinfo.foundry.consumers.plugin.Result;
import org.neuinfo.foundry.enhancers.plugins.TemporalEnhancer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;

// YOUR ENHANCER



/**
 * Created by valentine 12-2017.
 */
//@DisplayName("Temporal Enhancer. Dates from Text")
public class TemporalEnhancerTest {

    @Test

    public void testTemporalEnhancer() throws Exception {
        // load the test document wrapper from classpath (resources/test_data)
        String jsonStr = loadAsStringFromClassPath("test_data/test_doc_temporal.json");
        JSONObject json = new JSONObject(jsonStr);
        DBObject docWrapper = JSONUtils.encode(json);
        // create the plugin
        IPlugin plugin = new TemporalEnhancer();
        Map<String, String> options = new HashMap<String, String>(7);
        plugin.initialize(options);

        Result result = plugin.handle(docWrapper);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.getStatus() == Result.Status.OK_WITH_CHANGE);

        // show the updated doc wrapper
        JSONObject updatedJson = JSONUtils.toJSON((BasicDBObject) result.getDocWrapper(), false);
        System.out.println(updatedJson.toString(2));

    }

    public static String loadAsStringFromClassPath(String classpath) throws Exception {
        URL url = TemporalEnhancerTest.class.getClassLoader().getResource(classpath);
        String path = url.toURI().getPath();
        return Utils.loadAsString(path);
    }
}
