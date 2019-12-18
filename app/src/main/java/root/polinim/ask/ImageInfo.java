package root.polinim.ask;

import java.util.ArrayList;

public class ImageInfo{
    private String firstImageLink;
    private String secondImageLink;
    private int firstImageLikeNumber;
    private int secondImageLikeNumber;
    private int totalCommentNumber;
    private ArrayList<String> firstImageLikeID;
    private ArrayList<String> secondImageLikeID;

    public ImageInfo() {
    }

    public ImageInfo(String firstImageLink, String secondImageLink, int firstImageLikeNumber, int secondImageLikeNumber, int totalCommentNumber, ArrayList<String> firstImageLikeID, ArrayList<String> secondImageLikeID) {
        this.firstImageLink = firstImageLink;
        this.secondImageLink = secondImageLink;
        this.firstImageLikeNumber = firstImageLikeNumber;
        this.secondImageLikeNumber = secondImageLikeNumber;
        this.totalCommentNumber = totalCommentNumber;
        this.firstImageLikeID = firstImageLikeID;
        this.secondImageLikeID = secondImageLikeID;
    }

    public String getFirstImageLink() {
        return firstImageLink;
    }

    public void setFirstImageLink(String firstImageLink) {
        this.firstImageLink = firstImageLink;
    }

    public String getSecondImageLink() {
        return secondImageLink;
    }

    public void setSecondImageLink(String secondImageLink) {
        this.secondImageLink = secondImageLink;
    }

    public int getFirstImageLikeNumber() {
        return firstImageLikeNumber;
    }

    public void setFirstImageLikeNumber(int firstImageLikeNumber) {
        this.firstImageLikeNumber = firstImageLikeNumber;
    }

    public int getSecondImageLikeNumber() {
        return secondImageLikeNumber;
    }

    public void setSecondImageLikeNumber(int secondImageLikeNumber) {
        this.secondImageLikeNumber = secondImageLikeNumber;
    }

    public int getTotalCommentNumber() {
        return totalCommentNumber;
    }

    public void setTotalCommentNumber(int totalCommentNumber) {
        this.totalCommentNumber = totalCommentNumber;
    }

    public ArrayList<String> getFirstImageLikeID() {
        return firstImageLikeID;
    }

    public void setFirstImageLikeID(ArrayList<String> firstImageLikeID) {
        this.firstImageLikeID = firstImageLikeID;
    }

    public ArrayList<String> getSecondImageLikeID() {
        return secondImageLikeID;
    }

    public void setSecondImageLikeID(ArrayList<String> secondImageLikeID) {
        this.secondImageLikeID = secondImageLikeID;
    }
}