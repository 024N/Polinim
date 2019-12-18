package root.polinim.outer;


import java.io.Serializable;

import root.polinim.ask.QuestionPojo;

public class OuterSerializable implements Serializable {

    QuestionPojo questionPojo;

    public OuterSerializable(QuestionPojo questionPojo) {
        this.questionPojo = questionPojo;
    }

    public QuestionPojo getQuestionPojo() {
        return questionPojo;
    }

    public void setQuestionPojo(QuestionPojo questionPojo) {
        this.questionPojo = questionPojo;
    }
}