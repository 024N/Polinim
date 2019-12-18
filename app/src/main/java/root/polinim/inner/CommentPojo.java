package root.polinim.inner;

public class CommentPojo {
    private String questionID;
    private String commentID;
    private String userUID;
    private String profileLink;
    private String nickname;
    private String comment;
    private String time;

    public CommentPojo() {
    }

    public CommentPojo(String questionID, String commentID, String userUID, String profileLink, String nickname, String comment, String time) {
        this.questionID = questionID;
        this.commentID = commentID;
        this.userUID = userUID;
        this.profileLink = profileLink;
        this.nickname = nickname;
        this.comment = comment;
        this.time = time;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}