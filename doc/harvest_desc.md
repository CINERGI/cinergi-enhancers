Harvest Descriptors
------------------

## A sample harvest descriptor JSON document

```JSON
{
    "sourceInformation": {
        "resourceID": "cinergi-0030",
        "name": "IDEA MGDS",
        "dataSource": "IDEA MGDS"
    },
    "ingestConfiguration": {
        "ingestMethod": "WAF",
        "ingestURL": "http://get.iedadata.org/metadata/iso/series/",
        "allowDuplicates": "false",
        "crawlFrequency": {
            "crawlType": "Frequency",
            "hours": "48",
            "minutes": "0",
            "startDays": [
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday"
            ],
            "startTime": "0:00",
            "operationEndTime": "24:00"
        }
    },
    "contentSpecification": {
        "keepMissing": "false",
        "locale": "en_US"
    },
    "originalRecordIdentifierSpec": {
        "fields": [
            "$.'gmi:MI_Metadata'.'gmd:fileIdentifier'.'gco:CharacterString'.'_$'"
        ],
        "delimiter": [
            ":"
        ],
        "method": "Value"
    },
    "documentProcessing": [
        "UUID Generation",
        "XML2Cinergi",
        "Index"
    ]
}

```

