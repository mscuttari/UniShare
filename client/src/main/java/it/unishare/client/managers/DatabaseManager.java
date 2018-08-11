package it.unishare.client.managers;

import it.unishare.common.connection.dht.NoteFile;
import it.unishare.common.connection.dht.NoteMetadata;
import it.unishare.client.layout.Download;
import it.unishare.client.utils.Settings;
import it.unishare.common.kademlia.KademliaFile;
import it.unishare.common.models.Review;
import it.unishare.common.models.User;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    // Singleton instance
    private static DatabaseManager instance;


    /**
     * Private constructor (for Singleton)
     */
    private DatabaseManager() {

    }


    /**
     * Get singleton instance
     *
     * @return  singleton instance
     */
    public static DatabaseManager getInstance() {
        if (instance == null)
            instance = new DatabaseManager();

        return instance;
    }


    /**
     * Get managers to the SQLite database
     *
     * @return  database {@link Connection}
     */
    private Connection getConnection() throws SQLException {
        String databasePath = getDatabasePath();

        // Check if the database already exists
        boolean dbExists = Files.exists(Paths.get(databasePath));

        // Connect to the database
        String url = "jdbc:sqlite:" + databasePath;
        Connection connection = DriverManager.getConnection(url);

        // If the database didn't exist, initialize it
        if (!dbExists) {
            initializeDatabase();
        }

        return connection;
    }


    /**
     * Get database path
     *
     * @return  database path
     */
    private static String getDatabasePath() {
        return Settings.getDataPath() + File.separator + "unishare.sqlite";
    }


    /**
     * Initialize database
     */
    private void initializeDatabase() {
        String[] SQLs = new String[] {
                "CREATE TABLE IF NOT EXISTS shared_files(" +
                        "user_id INTEGER NOT NULL," +
                        "key BLOB NOT NULL," +
                        "title TEXT NOT NULL," +
                        "university TEXT NOT NULL," +
                        "department TEXT NOT NULL," +
                        "course TEXT NOT NULL," +
                        "teacher TEXT NOT NULL," +
                        "PRIMARY KEY(user_id, key)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS downloaded_files(" +
                        "user_id INTEGER NOT NULL," +
                        "key BLOB NOT NULL," +
                        "title TEXT NOT NULL," +
                        "university TEXT NOT NULL," +
                        "department TEXT NOT NULL," +
                        "course TEXT NOT NULL," +
                        "teacher TEXT NOT NULL, " +
                        "author TEXT NOT NULL, " +
                        "path TEXT NOT NULL," +
                        "show INTEGER NOT NULL DEFAULT 1," +
                        "PRIMARY KEY(user_id, key)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS reviews(" +
                        "user_id INTEGER NOT NULL," +
                        "file_key INTEGER NOT NULL," +
                        "author TEXT NOT NULL, " +
                        "rating INTEGER NOT NULL," +
                        "body TEXT NOT NULL," +
                        "PRIMARY KEY(user_id, file_key, author)," +
                        "FOREIGN KEY(user_id, file_key) REFERENCES shared_files(user_id, key) ON UPDATE CASCADE ON DELETE CASCADE" +
                        ");",
        };

        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            for (String SQL : SQLs)
                statement.execute(SQL);

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get all shared files
     *
     * @return  shared files list
     */
    public List<NoteFile> getSharedFiles(User user) {
        List<NoteFile> result = new ArrayList<>();
        String sql = "SELECT key, title, university, department, course, teacher FROM shared_files WHERE user_id = ?";

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, user.getId());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                NoteMetadata data = new NoteMetadata(
                        resultSet.getString("title"),
                        user.getFullName(),
                        resultSet.getString("university"),
                        resultSet.getString("department"),
                        resultSet.getString("course"),
                        resultSet.getString("teacher")
                );

                NoteFile file = new NoteFile(
                        resultSet.getBytes("key"),
                        ConnectionManager.getInstance().getNode().getInfo(),
                        data
                );

                result.add(file);
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * Add shared file
     *
     * @param   file    file
     */
    public void addSharedFiles(User user, NoteFile file) {
        String sql = "INSERT INTO shared_files(user_id, key, title, university, department, course, teacher) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, user.getId());
            statement.setBytes(2, file.getKey().getBytes());
            statement.setString(3, file.getData().getTitle());
            statement.setString(4, file.getData().getUniversity());
            statement.setString(5, file.getData().getDepartment());
            statement.setString(6, file.getData().getCourse());
            statement.setString(7, file.getData().getTeacher());

            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Delete shared file
     *
     * @param   key     key
     */
    public void deleteSharedFile(long userId, byte[] key) {
        String sql = "DELETE FROM shared_files WHERE user_id = ? AND key = ?";

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, userId);
            statement.setBytes(2, key);

            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get all the downloads that should be shown in the downloads list
     *
     * @return  list of the downloaded files
     */
    public List<Download> getDownloadedFiles(User user) {
        List<Download> result = new ArrayList<>();
        String sql = "SELECT key, title, university, department, course, teacher, author, path FROM downloaded_files WHERE user_id = ?";

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, user.getId());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                NoteMetadata data = new NoteMetadata(
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("university"),
                        resultSet.getString("department"),
                        resultSet.getString("course"),
                        resultSet.getString("teacher")
                );

                NoteFile file = new NoteFile(
                        resultSet.getBytes("key"),
                        ConnectionManager.getInstance().getNode().getInfo(),
                        data
                );

                File path = new File(resultSet.getString("path"));
                result.add(new Download(file, path));
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * Get all the downloads that should be shown in the downloads list
     *
     * @return  list of the downloaded files
     */
    public List<Download> getDownloadedAndShowableFiles(User user) {
        List<Download> result = new ArrayList<>();
        String sql = "SELECT key, title, university, department, course, teacher, author, path FROM downloaded_files WHERE user_id = ? AND show = 1";

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, user.getId());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                NoteMetadata data = new NoteMetadata(
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("university"),
                        resultSet.getString("department"),
                        resultSet.getString("course"),
                        resultSet.getString("teacher")
                );

                NoteFile file = new NoteFile(
                        resultSet.getBytes("key"),
                        ConnectionManager.getInstance().getNode().getInfo(),
                        data
                );

                File path = new File(resultSet.getString("path"));
                result.add(new Download(file, path));
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * Add download
     *
     * @param   user        user
     * @param   download    download
     */
    public void addDownloadedFile(User user, Download download) {
        String sql = "INSERT INTO downloaded_files(user_id, key, title, university, department, course, teacher, author, path) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        NoteFile file = download.getFile();
        File path = download.getPath();

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, user.getId());
            statement.setBytes(2, file.getKey().getBytes());
            statement.setString(3, file.getData().getTitle());
            statement.setString(4, file.getData().getUniversity());
            statement.setString(5, file.getData().getDepartment());
            statement.setString(6, file.getData().getCourse());
            statement.setString(7, file.getData().getTeacher());
            statement.setString(8, file.getData().getAuthor());
            statement.setString(9, path.getAbsolutePath());

            statement.executeUpdate();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Delete download
     *
     * @param   user        user
     * @param   download    download
     */
    public void deleteDownloadedFile(User user, Download download) {
        String sql = "DELETE FROM downloaded_files WHERE user_id = ? AND key = ? AND title = ? AND university = ? AND department = ? AND course = ? AND teacher = ? AND author = ? AND path = ?";
        NoteFile file = download.getFile();
        File path = download.getPath();

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, user.getId());
            statement.setBytes(2, file.getKey().getBytes());
            statement.setString(3, file.getData().getTitle());
            statement.setString(4, file.getData().getUniversity());
            statement.setString(5, file.getData().getDepartment());
            statement.setString(6, file.getData().getCourse());
            statement.setString(7, file.getData().getTeacher());
            statement.setString(8, file.getData().getAuthor());
            statement.setString(9, path.getAbsolutePath());

            statement.executeUpdate();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Hide download
     *
     * @param   user        user
     * @param   download    download
     */
    public void hideDownloadedFile(User user, Download download) {
        String sql = "UPDATE downloaded_files SET show = 0 WHERE user_id = ? AND key = ? AND title = ? AND university = ? AND department = ? AND course = ? AND teacher = ? AND author = ? AND path = ?";
        NoteFile file = download.getFile();
        File path = download.getPath();

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, user.getId());
            statement.setBytes(2, file.getKey().getBytes());
            statement.setString(3, file.getData().getTitle());
            statement.setString(4, file.getData().getUniversity());
            statement.setString(5, file.getData().getDepartment());
            statement.setString(6, file.getData().getCourse());
            statement.setString(7, file.getData().getTeacher());
            statement.setString(8, file.getData().getAuthor());
            statement.setString(9, path.getAbsolutePath());

            statement.executeUpdate();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get file reviews
     *
     * @param   user    user
     * @param   file    file
     *
     * @return  reviews list
     */
    public List<Review> getFileReviews(User user, KademliaFile file) {
        List<Review> result = new ArrayList<>();

        String sql = "SELECT author, rating, body FROM reviews WHERE user_id = ? AND file_key = ?";

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, user.getId());
            statement.setBytes(2, file.getKey().getBytes());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Review review = new Review(
                        resultSet.getString("author"),
                        resultSet.getInt("rating"),
                        resultSet.getString("body")
                );

                result.add(review);
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * Save review (add, update or delete according to the data)
     *
     * @param   user        user
     * @param   file        shared file
     * @param   review      review
     */
    public void saveReview(User user, KademliaFile file, Review review) {
        List<Review> allReviews = getFileReviews(user, file);

        if (allReviews.contains(review)) {
            if (review.getBody() == null || review.getBody().isEmpty()) {
                deleteReview(user, file, review);
            } else {
                updateReview(user, file, review);
            }

        } else {
            addReview(user, file, review);
        }
    }


    /**
     * Add review
     *
     * @param   user        user
     * @param   file        shared file
     * @param   review      review
     */
    private void addReview(User user, KademliaFile file, Review review) {
        String sql = "INSERT INTO reviews(user_id, file_key, author, rating, body) VALUES(?, ?, ?, ?, ?)";

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, user.getId());
            statement.setBytes(2, file.getKey().getBytes());
            statement.setString(3, review.getAuthor());
            statement.setInt(4, review.getRating());
            statement.setString(5, review.getBody());

            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Update review
     *
     * @param   user        user
     * @param   file        file
     * @param   review      review
     */
    private void updateReview(User user, KademliaFile file, Review review) {
        String sql = "UPDATE revews SET rating = ? AND body = ? WHERE user_id = ? AND file_key = ? AND author = ?";

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, review.getRating());
            statement.setString(2, review.getBody());
            statement.setLong(3, user.getId());
            statement.setBytes(4, file.getKey().getBytes());
            statement.setString(5, review.getAuthor());

            statement.executeUpdate();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Delete review
     *
     * @param   user        user
     * @param   file        file
     * @param   review      review
     */
    private void deleteReview(User user, KademliaFile file, Review review) {
        String sql = "DELETE FROM reviews WHERE user_id = ? AND file_key = ? AND author = ?";

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, user.getId());
            statement.setBytes(2, file.getKey().getBytes());
            statement.setString(3, review.getAuthor());

            statement.executeUpdate();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
