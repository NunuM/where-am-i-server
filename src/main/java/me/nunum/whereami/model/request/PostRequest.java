package me.nunum.whereami.model.request;

import me.nunum.whereami.model.Post;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PostRequest {

    @NotNull
    @Size(min = 3, max = 255)
    private String title;

    @NotNull
    @Size(min = 3, max = 255)
    private String imageURL;

    @NotNull
    @URL
    private String sourceURL;


    public PostRequest() {
        this("","","");
    }

    public PostRequest(String title, String imageURL, String sourceURL) {
        this.title = title;
        this.imageURL = imageURL;
        this.sourceURL = sourceURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    @Override
    public String toString() {
        return "PostRequest{" +
                "title='" + title + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", sourceURL='" + sourceURL + '\'' +
                '}';
    }


    public Post build() {
        return new Post(title, imageURL, sourceURL);
    }

    public Post edit(Post post) {

        if (!(this.title == null || title.isEmpty())) {
            post.setTitle(title);
        }

        if (!(this.imageURL == null || imageURL.isEmpty())) {
            post.setImageURL(imageURL);
        }

        if (!(this.sourceURL == null || sourceURL.isEmpty())) {
            post.setSourceURL(sourceURL);
        }

        return post;
    }

}
