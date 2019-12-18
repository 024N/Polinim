package root.polinim.ask;

import java.util.ArrayList;

public class QuestionPojo {
    private String questionID;
    private String date;
    private String time;
    private String caregory;
    private String question;
    private User user = new User();
    private ImageInfo imageInfo = new ImageInfo();
    //private ArrayList<String> commentInfo = new ArrayList<>();
    //private CommentInfo commentInfo = new CommentInfo();

    public QuestionPojo() {
    }

    public QuestionPojo(String questionID, String date, String time, String caregory, String question, User user, ImageInfo imageInfo) {
        this.questionID = questionID;
        this.date = date;
        this.time = time;
        this.caregory = caregory;
        this.question = question;
        this.user = user;
        this.imageInfo = imageInfo;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCaregory() {
        return caregory;
    }

    public void setCaregory(String caregory) {
        this.caregory = caregory;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }
}