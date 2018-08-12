package it.unishare.common.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class ModelTest {

    @Test
    public void normalReviewTest() {
        Review review = new Review("Author name", 3, "Message");

        assertEquals(review.getAuthor(), "Author name");
        assertEquals(review.getRating(), 3);
        assertEquals(review.getBody(), "Message");
    }


    @Test
    public void emptyReviewTest() {
        Review review = new Review("", 3, "");

        assertNull(review.getAuthor());
        assertNull(review.getBody());
    }


    @Test
    public void reviewWithInvalidRating() {
        Review reviewLower = new Review("", 0, "");
        assertEquals(reviewLower.getRating(), 1);

        Review reviewHigher = new Review("", 6, "");
        assertEquals(reviewHigher.getRating(), 5);
    }


    @Test
    public void normalUserTest() {
        User user = new User("test@email.com", "password", "random_salt", "First name", "Last name");

        assertEquals(user.getEmail(), "test@email.com");
        assertEquals(user.getPassword(), "password");
        assertEquals(user.getSalt(), "random_salt");
        assertEquals(user.getFirstName(), "First name");
        assertEquals(user.getLastName(), "Last name");
    }


    @Test
    public void emptyUserTest() {
        User user = new User("", "", "", "", "");

        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getSalt());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
    }

}
