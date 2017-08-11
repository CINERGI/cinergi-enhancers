Document Ingestion Management
=============================

An application with a REST interface for document metadata and document ingestion can be used to manage document ingestion in a systematic way. 
As used by Elasticsearch and Solr a  REST web service based interface would allow flexible data management at ingestion level. 
Documents in XML, CSV or JSON formats  can be ingested through the REST API. 
The Document Ingestion Management System (DIMS) is responsible to transform the original document to JSON (if not already in JSON) , validate it, 
wrap it with standard document components such as processing, provenance etc 
and persist to MongoDB. 
For each source, first source meta-data needs to be ingested via the REST interface. 

## Source Metadata

The object model of the Source metadata is defined in `common` subproject 
`org.neuinfo.foundry.common.model.Source` class. The 
class also allows conversion to or from JSON format and has a Builder 
to build `Source` objects. 
Below is the JSON representation of the source metadata in MongoDB [Burak's original JSON object]

```JSON
{
    nifId:""
    name:"<source-name>"
    description:"",
    schema:"<JSON-Schema-for-documents-for-this source>",
    primaryKey:"<primary key pointer to the element(s) in the document in JSON Path format>",
    provenance:"<provenance record in PROV-DM format for the source metadata ingestion>",
    batchInfos:"<A list of internal records to manage multiple ingestion batches>"
}
```

A draft schema and example JSON object for a richer source representation is in the json-schemas/HarvestSourceObjectSchema.js file in this repository. This JSON object design used namespace qualifiers to allow JSON elements to be qualified (globally unique) to enable the objects to be encoded using JSON-LD for interoperability.  Element names have been expanded to make them less ambiguous when taken out of context. 

### Primary Key Representation

While each MongoDB ingested document is provided with a unique synthetic id, to identify the document a more natural primary key is also needed. 
The value of a field inside the JSON document can be used as the natural primary key. A pointer to that field is represented in JSONPath (similar to XPath) 
and stored in the source metadata JSON document in MongoDB (See above). 
A JSONPath processor is implemented by `org.neuinfo.foundry.common.util.JSONPathProcessor` class in `common` subproject.

An alternate approach is to finger print the incoming document using a hash based on the text in the document. This hash would uniquely fingerprint the harvested bitstream.

## Document Wrapper JSON

The object model of the document wrapper JSON representation to wrap the original document and manage processing of the document until Elasticsearch indexing is defined in `common` subproject 
`org.neuinfo.foundry.common.model.DocWrapper` class. 

Below is the JSON representation of the document wrapper that is stored in MongoDB.

```JSON
{
   "primaryKey":"<>",
   "Version":"<version-number>",
   "CrawlDate":"",
   "indexDate":"",
   "SourceInfo": { 
      "SourceID":"<nif-id of the source>",
      "ViewID":"",
      "Name":"<source-name>",
   },   
   "OriginalDoc":"<original doc as JSON>",
   "Processing": {
     "Status":"<process-status used to guide message oriented document processors>",
   },
   "History": {
    "provenance":"<provenance records in PROV-DM JSON format for the document ingestion and processing>",
    "batchId":"<>",
   },
   "Data":{}
}
```

This basic JSON object has been elaborated in json-schemas/MetadataObjectJSONSchema.js in the Foundry GitHub repository. Element names are expanded to make them unambiguous (mostly) outside the context of the JSON object, allowing subsets of the elements to be assembled into flat (not nested) simple JSON views of a subset of the content. Element names are also namespace qualified with a URI to make them globally unique when used in a qualified format. This enables the JSON objects to be used in JSON-LD applications.


## Provenance

PROV-DM specification (http://www.w3.org/TR/2013/REC-prov-dm-20130430/) based ProvToolBox (https://github.com/lucmoreau/ProvToolbox) libraries are used in foundry-common module to build a representation independent Java model of provenance and save is in PROV-JSON format (http://www.w3.org/Submission/2013/SUBM-prov-json-20130424/).

In `foundry-common module` there is a builder for provenance record hiding the details of JSON-DM. Below is an example, using Java fluent API style, to state that a document `doc1` entity is processed by  software agent `docIdAssigner` that assigns a document id to the entity. The activity took place at May 16 2014, 16:05 lasting 1 second.

```java
final ProvenanceRec.Builder builder = new ProvenanceRec.Builder("http://example.org", "foundry");

ProvenanceRec provenanceRec = builder.docEntity("doc1", "document")
                .softwareAgent("docIdAssigner")
                .activity("assignId", "doc-id-assigment",  "2014-05-16T16:05:00",
                        "2014-05-16T16:05:01")
                .wasAssociatedWith("assignId","docIdAssigner")
                .used("assignId","doc1")
                .build();

provenanceRec.save("/tmp/doc_id_assigment_prov.json");
```

Below is the PROV-JSON document generated and also validated against PROV-JSON JSON schema ()

```JSON
{
  "wasAssociatedWith": {
    "_:wAW2": {
      "prov:activity": "foundry:assignId",
      "prov:agent": "foundry:docIdAssigner"
    }
  },
  "entity": {
    "foundry:doc1": {
      "prov:label": "document"
    }
  },
  "prefix": {
    "xsd": "http://www.w3.org/2001/XMLSchema",
    "prov": "http://www.w3.org/ns/prov#",
    "foundry": "http://example.org"
  },
  "used": {
    "_:u2": {
      "prov:activity": "foundry:assignId",
      "prov:entity": "foundry:doc1"
    }
  },
  "agent": {
    "foundry:docIdAssigner": {
      "prov:type": {
        "$": "prov:SoftwareAgent",
        "type": "xsd:string"
      }
    }
  },
  "activity": {
    "foundry:assignId": {
      "prov:type": {
        "$": "doc-id-assigment",
        "type": "xsd:string"
      },
      "prov:startTime": "2014-05-16T16:05:00-07:00",
      "prov:endTime": "2014-05-16T16:05:01-07:00"
    }
  }
}
```


