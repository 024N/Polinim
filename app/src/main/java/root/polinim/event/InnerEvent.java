package root.polinim.event;


import android.util.Log;

import root.polinim.ask.QuestionPojo;

public class InnerEvent {

    QuestionPojo questionPojo;

    public InnerEvent(QuestionPojo questionPojo) {
        this.questionPojo = questionPojo;
    }

    public QuestionPojo getQuestionPojo() {
        return questionPojo;
    }

    public void setQuestionPojo(QuestionPojo questionPojo) {
        this.questionPojo = questionPojo;
    }
}