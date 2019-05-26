package me.nunum.whereami.model.request;

import me.nunum.whereami.model.Algorithm;
import org.hibernate.validator.constraints.URL;


public class UpdateAlgorithmRequest {

    private String name;

    private String authorName;

    @URL
    private String paperURL;


    public UpdateAlgorithmRequest() {
    }

    public UpdateAlgorithmRequest(String name, String authorName, String paperURL) {
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

    public Algorithm upateAlgorithm(Algorithm algorithm) {
        if (!(this.name == null
                || this.name.isEmpty()
                || this.name.length() < 4
                || this.name.length() > 255)) {
            algorithm.setName(this.name);
        }

        if (!(this.authorName == null
                || this.authorName.isEmpty()
                || this.authorName.length() < 4
                || this.authorName.length() > 255)) {
            algorithm.setAuthorName(this.authorName);
        }

        if (!(this.paperURL == null)) {
            algorithm.setPaperURL(this.paperURL);
        }

        return algorithm;
    }

    @Override
    public String toString() {
        return "UpdateAlgorithmRequest{" +
                "name='" + name + '\'' +
                ", authorName='" + authorName + '\'' +
                ", paperURL='" + paperURL + '\'' +
                '}';
    }
}
