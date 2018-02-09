package org.neuinfo.foundry.enhancers.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

// add islocaltime
// UTC, LOCAL,Unknown

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

    public String getName (){
        return this.name;
    }
    public void setName (String name){
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getIsoString() {
        return isoString;


    }

    public void setIsoString(String isoisoString) {
        this.isoString = isoisoString;
    }

    public dateType getDateType() {
        return dateType;
    }

    public void setDateType(dateType dateType) {
        this.dateType = dateType;
    }

    public isoType getIsoType() {
        return isoType;
    }

    public void setIsoType(isoType isoType) {
        this.isoType = isoType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getOffsetStart() {
        return offsetStart;
    }

    public void setOffsetStart(int offsetStart) {
        this.offsetStart = offsetStart;
    }

    public int getOffsetEnd() {
        return offsetEnd;
    }

    public void setOffsetEnd(int offsetEnd) {
        this.offsetEnd = offsetEnd;
    }

   // @JsonFormat(shape = JsonFormat.Shape.OBJECT)


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Temporal{");
        sb.append("name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

}

