package org.neuinfo.foundry.enhancers.common;

public enum isoType {
    GYEAR("xsd:gYear"),
    GYEARMONTH("xsd:gYearMonth"),
    DATE("xsd:date"),
    DATETIME("xsd:dateTime"),
    UNKNOWN("");

    private String schemaType;

    isoType(String schemaType) {
        this.schemaType = schemaType;
    }

    public String schemaType() {
        return schemaType;
    }
}
