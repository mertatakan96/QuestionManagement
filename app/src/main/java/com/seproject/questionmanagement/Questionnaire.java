package com.seproject.questionmanagement;

import java.io.Serializable;
import java.util.ArrayList;

public class Questionnaire implements Serializable {

    public String question;
    public ArrayList<String> answers;

    public Questionnaire() {
    }

    public Questionnaire(String question, ArrayList<String> answers) {
        this.question = question;
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }
}
