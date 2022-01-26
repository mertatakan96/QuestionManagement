package com.seproject.questionmanagement.Adapters;

import java.util.Date;

public class Questionnaires {

    public Questionnaires() {
    }

    String title, explain,docID, publisher;
    long  created_time;
    Date deatline;

    public Questionnaires(String title, String explain, String docID, String publisher, long created_time, Date deatline) {
        this.title = title;
        this.explain = explain;
        this.docID = docID;
        this.publisher = publisher;
        this.created_time = created_time;
        this.deatline = deatline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public long getCreated_time() {
        return created_time;
    }

    public void setCreated_time(long created_time) {
        this.created_time = created_time;
    }

    public Date getDeatline() {
        return deatline;
    }

    public void setDeatline(Date deatline) {
        this.deatline = deatline;
    }
}
