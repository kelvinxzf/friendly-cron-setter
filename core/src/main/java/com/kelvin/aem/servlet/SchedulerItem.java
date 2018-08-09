package com.kelvin.aem.servlet;

public class SchedulerItem {

    private String value;
    private SchedulerContent content;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SchedulerContent getContent() {
        return content;
    }

    public void setContent(SchedulerContent content) {
        this.content = content;
    }

}
