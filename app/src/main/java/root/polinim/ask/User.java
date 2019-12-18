package root.polinim.ask;

public class User{
    private String uid;
    private String nickname;
    private String profileLink;

    public User() {
    }

    public User(String uid, String nickname, String profileLink) {
        this.uid = uid;
        this.nickname = nickname;
        this.profileLink = profileLink;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }
}