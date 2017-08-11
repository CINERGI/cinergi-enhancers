Cinergi Enhancers
=================

Starter project for developing enhancers for CINERGI Foundry pipeline system.

Prerequisites
-------------

* Unix like OS (Linux, Mac OSX)
* Java 8 or higher
* Apache Maven (to build the software)


Getting the code
----------------

    cd $HOME
    git clone https://<username>@github.com/CINERGI/cinergi-enhancers
    cd $HOME/cinergi-enhancers

Installing Foundry 
------------------

Enhancer plugin API and Foundry common library used for enhancer development is required for the enhancement development. To get the latest jar files for these libraries you need to clone the Foundry code and build and install these libraries to your local Maven repository.

    cd $HOME
    git clone https://<username>@github.com/CINERGI/Foundry
    cd $HOME/Foundry/dependencies
    ./install_prov_xml_2mvn.sh
    ./install_prov_model_2mvn.sh
    ./install_prov_json__2mvn.sh
    ./install_bnlp_2mvn.sh
    ./install_bnlp_dependencies_2mvn.sh
    ./install_bnlp_model2mvn.sh
    cd $HOME/Foundry

Then build and install via

    mvn -Pdev clean install


# Implementing a new Enhancer

All enhancers need to implement the `org.neuinfo.foundry.consumers.plugin.IPlugin` interface which is located 
in the `consumer-plugin-api` subproject of the Foundry-ES. To develop a new enhancer, you need to include 
the Foundry-ES `common` and `consumer-plugin-api` libraries to your Maven `pom.xml` after you have built it 
via `mvn -Pdev clean install` in addition to all the dependencies from `common/pom.xml` of the Foundry-ES.

```xml
<dependency>
   <groupId>org.neuinfo</groupId>
   <artifactId>foundry-common</artifactId>
   <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.neuinfo</groupId>
    <artifactId>foundry-consumer-plugin-api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

```

The `org.neuinfo.foundry.consumers.plugin.IPlugin` interface is shown below; 

```java
public interface IPlugin {

    public void initialize(Map<String, String> options) throws Exception;

    public Result handle(DBObject docWrapper);

    public String getPluginName();
}
```

The enhancement is done in the `handle(DBObject docWrapper)` method which takes a Mongo `DBObject` 
object corresponding to the currently processed [document wrapper](doc_ingestion.md) from the Mongo database. 
The original document is stored inline in the document wrapper. 
Below is a code fragment to get the original document converted to JSON from the `docWrapper`.

```java
BasicDBObject data = (BasicDBObject) docWrapper.get("Data");
DBObject originalDoc = (DBObject) docWrapper.get("OriginalDoc");
JSONObject json = JSONUtils.toJSON((BasicDBObject) originalDoc, false);

```

To convert a Mongo Java driver database object to JSON you can use the following snippet

```java
JSONObject json = JSONUtils.toJSON((BasicDBObject) originalDoc, false);
```

Similarly, a manipulated json object can be put to the document wrapper using Java code similar to the following

```java
DBObject data = (DBObject) docWrapper.get("Data");
DBObject spatial = JSONUtils.encode(spatialJson, false);
data.put("spatial", spatial);
```

The `handle` method returns its result using the following Java code snippet

```java
return new Result(docWrapper, Result.Status.OK_WITH_CHANGE);
```

If there is an error occurred during the enhancement you need to return an error result as shown below;

```java
Result r = new Result(docWrapper, Result.Status.ERROR);
r.setErrMessage(errorMessage);
return r;
```

To get more information about writing new enhancers, please check to code of the existing enhancers under the 
package `org.neuinfo.foundry.consumers.jms.consumers.plugins` namely 
`KeywordEnhancer`, `SpatialEnhancer`, `OrganizationEnhancer` and `WAFExporter`.


