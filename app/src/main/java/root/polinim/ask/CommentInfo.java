package root.polinim.ask;

public class CommentInfo{
    private String commentID;
    private User user = new User();
    private String comment;
    private String time;

    public CommentInfo() {
    }

    public CommentInfo(User user, String commentID, String comment, String time) {
        this.user = user;
        this.commentID = commentID;
        this.comment = comment;
        this.time = time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
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