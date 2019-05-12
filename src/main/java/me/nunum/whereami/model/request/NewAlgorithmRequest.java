package me.nunum.whereami.model.request;

import me.nunum.whereami.model.Algorithm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NewAlgorithmRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    private String authorName;

    @NotNull
    @Size(min = 1, max = 255)
    private String paperURL;


    public NewAlgorithmRequest() {
        this("","","");
    }

    public NewAlgorithmRequest(String name, String authorName, String paperURL) {
        this.name = name;
        this.authorName = authorName;
        this.paperURL = paperURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPaperURL() {
        return paperURL;
    }

    public void setPaperURL(String paperURL) {
        this.paperURL = paperURL;
    }


    @Override
    public String toString() {
        return "NewAlgorithmRequest{" +
                "name='" + name + '\'' +
                ", authorName='" + authorName + '\'' +
                ", paperURL='" + paperURL + '\'' +
                '}';
    }

    public Algorithm build() {
        return new Algorithm(name, authorName, paperURL);
    }
}
