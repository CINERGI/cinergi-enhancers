package org.neuinfo.foundry.enhancers.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

public enum dateType implements Serializable {
    TimeInstant("Single Date"),
    TimeRange("Begin and End Date"),
    DURATION("ISO Period"),

    UNKNOWN("");

    private String label;

    @JsonCreator
    dateType(String label) {
        this.label = label;
    }

    @JsonValue
    public String label() {
        return label;
    }


}
