package it.unishare.common.models;

import java.io.Serializable;

public class Review implements Serializable {

    private static final long serialVersionUID = -1239175990964059541L;

    private Integer rating;
    private String author;
    private String body;


    /**
     * Default constructor
     */
    public Review() {
        this(null, null, null);
    }


    /**
     * Constructor
     *
     * @param   author          author
     * @param   rating          rating
     * @param   body            body
     */
    public Review(String author, Integer rating, String body) {
        this.rating = rating;
        this.author = author;
        this.body = body;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Review))
            return false;

        Review review = (Review) obj;
        return getAuthor().equals(review.getAuthor());
    }


    @Override
    public int hashCode() {
        return getAuthor().hashCode();
    }


    @Override
    public String toString() {
        return "{" + rating + ";" + author + ";" + body + "}";
    }


    /**
     * Get author
     *
     * @return  author
     */
    public String getAuthor() {
        return author;
    }


    /**
     * Set author
     *
     * @param   author      author
     */
    public void setAuthor(String author) {
        this.author = author == null || author.isEmpty() ? null : author;
    }


    /**
     * Get rating
     *
     * @return  rating
     */
    public Integer getRating() {
        return rating;
    }


    /**
     * Set rating
     *
     * @param   rating      rating
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }


    /**
     * Get body
     *
     * @return  body
     */
    public String getBody() {
        return body;
    }


    /**
     * Set body
     *
     * @param   body    body
     */
    public void setBody(String body) {
        this.body = body == null || body.isEmpty() ? null : body;
    }

}
