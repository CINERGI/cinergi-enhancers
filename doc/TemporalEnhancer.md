Build Temporal Enhancer

For this example, we will search titles and abstracts for temporal information.
This requires use of the StanfordNLP software. This is downloaded via maven
 

Configure your development environment as outlined in [Creating Enhancer](CreatingEnhancer.md)

Add dependencies to the pom.xml
 ```xml
 <!-- temporal -->
        <!-- https://mvnrepository.com/artifact/edu.stanford.nlp/stanford-corenlp -->
        <dependency>
            <groupId>edu.stanford.nlp</groupId>
            <artifactId>stanford-corenlp</artifactId>
            <version>3.8.0</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.7.25</version>
        </dependency>
        <dependency>
        <groupId>edu.stanford.nlp</groupId>
        <artifactId>stanford-corenlp</artifactId>
        <version>3.8.0</version>
        <classifier>models</classifier> <!--  will get the dependent model jars -->
    </dependency>
        <dependency>
            <groupId>edu.stanford.nlp</groupId>
            <artifactId>stanford-corenlp</artifactId>
            <version>3.8.0</version>
            <classifier>models-english</classifier> <!--  will get the dependent model jars -->
        </dependency>
        <dependency>
            <groupId>edu.stanford.nlp</groupId>
            <artifactId>stanford-corenlp</artifactId>
            <version>3.8.0</version>
            <classifier> models-english-kbp</classifier> <!--  will get the dependent model jars -->
        </dependency>
 ```

Temporal Enhancer
--------------

For the Temporal enhancer, we want to setup tests
* Have examples of titles that include Time Information
* Have examples of abstracts that include Time Information

Step Zero,
Determine what field(s) in the JSON doc
* use the test_data/test_doc_temporal.json
* determine fields
(add some example here)
* modify document to create two use cases:

For the Temporal Enhancer case, we need the title, and the abstract:

Title:

 $..'gmd:identificationInfo'.'gmd:MD_DataIdentification'.'gmd:citation'.'gmd:CI_Citation'.'gmd:title'.'gco:CharacterString'.'_$'"
 ```json
"gmd:identificationInfo": {"gmd:MD_DataIdentification": {
        "gmd:citation": {"gmd:CI_Citation": {
          "gmd:title": {"gco:CharacterString": {"_$": "Seismic Shot Point Navigation Data from the Long Island Shelf acquired during the R/V Endeavor expedition EN370 (2002) "}},
          "gmd:date": {"gmd:CI_Date": {
            "gmd:date": {"gco:Date": {"_$": "2009-05-12"}},
            "gmd:dateType": {"gmd:CI_DateTypeCode": {
              "@codeList": "http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_DateTypeCode",
              "@codeListValue": "publication",
              "_$": "publication"
            }}
          }},     
```   

Abstract:
There should be only one abstract:
$..'gmd:abstract'.'gco:CharacterString'.'_$'

  ```json
     "gmd:identificationInfo": {"gmd:MD_DataIdentification": {

        "gmd:abstract": {"gco:CharacterString": {"_$": "This data set was acquired with the LDEO Portable HiRes Multi-Channel Seismic system during Endeavor expedition R/V EN370 conducted in 2002 (Chief Scientist: Dr. Gregory Mountain; Investigators: Dr. Gregory Mountain, Dr. Cecilia McHugh, and Dr. Nicholas Christie-Blick). These data files are of ASCII format and include Seismic Shot Point Navigation data that provide the primary seismic navigation source for this cruise. Data were acquired as part of the project: Hi-Res MCS, CHIRP and side-scan sonar study of NY/NJ margins, and funding was provided by NSF grants: OCE01-19019 and OCE02-24767. Test: Data are from 2000 to 2001. and January 1999 to December 1999"}},
    
```   

# setup tests




#Create a Temporal Class:
For the temporal class that will be serialized in the provenance, we the following fields are generated out of the 
temporalNLP call

 ```java
public class Temporal {
    private String name;

    private String startDate;
    private String endDate;
    private String duration;
    private String isoString;
    private dateType dateType;
    private isoType isoType;

    private String field;
    private int offsetStart=-1;
    private int offsetEnd=-1;
 ```        
