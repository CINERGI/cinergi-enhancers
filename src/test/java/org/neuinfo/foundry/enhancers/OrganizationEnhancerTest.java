package org.neuinfo.foundry.enhancers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.neuinfo.foundry.common.util.JSONUtils;
import org.neuinfo.foundry.common.util.Utils;
import org.neuinfo.foundry.consumers.plugin.IPlugin;
import org.neuinfo.foundry.consumers.plugin.Result;
import org.neuinfo.foundry.enhancers.plugins.OrganizationEnhancer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bozyurt on 8/10/17.
 */
public class OrganizationEnhancerTest {

    @Test
    public void testOrganizationEnhancer() throws Exception {
        // load the test document wrapper from classpath (resources/test_data)
        String jsonStr = loadAsStringFromClassPath("test_data/test_doc.json");
        JSONObject json = new JSONObject(jsonStr);
        DBObject docWrapper = JSONUtils.encode(json);
        // create the plugin
        IPlugin plugin = new OrganizationEnhancer();
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
        URL url = OrganizationEnhancerTest.class.getClassLoader().getResource(classpath);
        String path = url.toURI().getPath();
        return Utils.loadAsString(path);
    }
}
