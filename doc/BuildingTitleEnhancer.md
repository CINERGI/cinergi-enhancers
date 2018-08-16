Building a Simple Enhancer
-------------------------

For this example, we will enhance titles that are shorter than a certain length.

Configure your development environment as outlined in [Creating Enhancer](CreatingEnhancer.md)



Title Enhancer
--------------

For the title enhancer, we want to setup tests
* is the title short
* is is just one of a set of common titles, like Roads

Step Zero,
Determine what field(s) in the JSON doc
* copy a document for resources/test_data
* determine fields
(add some example here)
* modify document to create two use cases:

For the Title Enhancer case, we need the title, and the organization:

Title:
 $..'gmd:identificationInfo'.'gmd:MD_DataIdentification'.'gmd:citation'.'gmd:CI_Citation'.'gmd:title'.'gco:CharacterString'.'_$'"
 ```json
         "gmd:identificationInfo": {"gmd:MD_DataIdentification": {
           "gmd:citation": {"gmd:CI_Citation": {
             "gmd:title": {"gco:CharacterString": {"_$": "Road"}},
             "gmd:date": {"gmd:CI_Date": {
               "gmd:date": {"gco:Date": {"_$": "2009-05-12"}},
               "gmd:dateType": {"gmd:CI_DateTypeCode": {
                 "@codeList": "http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_DateTypeCode",
                 "@codeListValue": "publication",
                 "_$": "publication"
               }}
             }},     
```   
         
Organization:                    
$..'gmd:identificationInfo'.'gmd:MD_DataIdentification'.'gmd:pointOfContact'[*].'gmd:CI_ResponsibleParty'.'gmd:organisationName'.'gco:CharacterString'.'_$'"
```json
      "gmd:pointOfContact": [
          {"gmd:CI_ResponsibleParty": {
            "gmd:individualName": {"gco:CharacterString": {"_$": "MGDS Data Manager"}},
            "gmd:organisationName": {"gco:CharacterString": {"_$": "Interdisciplinary Earth Data Alliance (IEDA):Marine Geoscience Data System (MGDS)"}},
            "gmd:contactInfo": {"gmd:CI_Contact": {"gmd:address": {"gmd:CI_Address": {"gmd:electronicMailAddress": {"gco:CharacterString": {"_$": "info@marine-geo.org"}}}}}},
            "gmd:role": {"gmd:CI_RoleCode": {
              "@codeList": "http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_RoleCode",
              "@codeListValue": "pointOfContact",
              "_$": "pointOfContact"
            }}
          }},
          {"gmd:CI_ResponsibleParty": {
            "gmd:individualName": {"gco:CharacterString": {"_$": "Mountain, Gregory"}},
            "gmd:organisationName": {"gco:CharacterString": {"_$": "Lamont-Doherty Earth Observatory (LDEO)"}},
            "gmd:role": {"gmd:CI_RoleCode": {
              "@codeList": "http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_RoleCode",
              "@codeListValue": "pointofContact",
              "_$": "principleInvestigator"
            }}
          }}
        ],     
```  

# Step 1: setup tests
* Copy and modify, OrganizationEnhancerTest

* rename to TitleEnhancerTest

```java
public class TitleEnhancer implements IPlugin {
    private int minTitleLength = 20;

    private final static Logger logger = Logger.getLogger(TitleEnhancer.class);
    public static String loadAsStringFromClassPath(String classpath) throws Exception {
            URL url = TitleEnhancerTest.class.getClassLoader().getResource(classpath);
            String path = url.toURI().getPath();
            return Utils.loadAsString(path);
        }
}
```   

* Add modified test:
 this test will intially only test to see that the document is modified.
 
```java
  @Test
     public void testTitleEnhancer_1() throws Exception {
         // load the test document wrapper from classpath (resources/test_data)
         String jsonStr = loadAsStringFromClassPath("test_data/test_doc_title_1.json");
         JSONObject json = new JSONObject(jsonStr);
         DBObject docWrapper = JSONUtils.encode(json);
         // create the plugin
         IPlugin plugin = new TitleEnhancer();
         Map<String, String> options = new HashMap<String, String>(7);
         plugin.initialize(options);
 
         Result result = plugin.handle(docWrapper);
 
         Assert.assertNotNull(result);
         Assert.assertTrue(result.getStatus() == Result.Status.OK_WITH_CHANGE);
 
         // show the updated doc wrapper
         JSONObject updatedJson = JSONUtils.toJSON((BasicDBObject) result.getDocWrapper(), false);
         System.out.println(updatedJson.toString(2));
 
     }
```

# Step 2:
Create a class to hold the information about the title change. This will allow it to be converted to JSON.

```java
Package org.neuinfo.foundry.enhancers.common;

 public class TitleEnhanced {
    private String title ;
     private String org;
     private String originalTitle;

     @Override
     public String toString() {
         final StringBuilder sb = new StringBuilder("Title{");
         sb.append("title='").append(getTitle()).append('\'');
         sb.append('}');
         return sb.toString();
     }

     public String getTitle() {
         return title;
     }

     public void setTitle(String title) {
         this.title = title;
     }

     public String getOrg() {
         return org;
     }

     public void setOrg(String org) {
         this.org = org;
     }

     public String getOriginalTitle() {
         return originalTitle;
     }

     public void setOriginalTitle(String originalTitle) {
         this.originalTitle = originalTitle;
     }
 }
```
 
# Step 3:

Create class
```java
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
    
 
     private final static Logger logger = Logger.getLogger(TitleEnhancer.class);
     
    
     @Override
     public void initialize(Map<String, String> options) throws Exception {
 
     }
 
     @Override
     public Result handle(DBObject docWrapper) {
        {
              Result r = new Result(docWrapper, Result.Status.ERROR);
             r.setErrMessage(t.getMessage());
             return r;
     }
 
     @Override
     public String getPluginName() {
         return "titleEnhancer";
     }
 
 
 }
```
Run test.
Should Fail


Add Code to handle title length:
* get title.
* if too short, then get organizations.
* add Provenance

```java


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
                       title= title + "from Organization " + s;
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


```

# Determine how XML output will be modified
In our case, title is changed, this is the distinct path:

```XML
/gmi:MI_Metadata/gmd:identificationInfo[1]/gmd:MD_DataIdentification[1]/gmd:citation[1]/gmd:CI_Citation[1]/gmd:title[1]/gco:CharacterString[1]
```

How to modify:
org.neuinfo.foundry.common.util.ISOXMLGenerator2



