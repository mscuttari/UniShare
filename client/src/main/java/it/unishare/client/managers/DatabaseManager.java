package it.unishare.client.managers;

import it.unishare.client.layout.Download;
import it.unishare.client.utils.Settings;
import it.unishare.common.connection.kademlia.KademliaFile;
import it.unishare.common.connection.kademlia.KademliaFileData;
import it.unishare.common.models.User;
import it.unishare.common.utils.Pair;

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
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "key BLOB NOT NULL," +
                        "title TEXT NOT NULL," +
                        "university TEXT NOT NULL," +
                        "department TEXT NOT NULL," +
                        "course TEXT NOT NULL," +
                        "teacher TEXT NOT NULL" +
                        ");",

                "CREATE TABLE IF NOT EXISTS downloaded_files(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "key BLOB NOT NULL," +
                        "title TEXT NOT NULL," +
                        "university TEXT NOT NULL," +
                        "department TEXT NOT NULL," +
                        "course TEXT NOT NULL," +
                        "teacher TEXT NOT NULL, " +
                        "author TEXT NOT NULL, " +
                        "path TEXT NOT NULL" +
                        ");"
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
    public List<KademliaFile> getSharedFiles(User user) {
        List<KademliaFile> result = new ArrayList<>();
        String sql = "SELECT key, title, university, department, course, teacher FROM shared_files WHERE user_id = ?";

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, user.getId());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                KademliaFileData data = new KademliaFileData(
                        resultSet.getString("title"),
                        user.getFullName(),
                        resultSet.getString("university"),
                        resultSet.getString("department"),
                        resultSet.getString("course"),
                        resultSet.getString("teacher")
                );

                KademliaFile file = new KademliaFile(
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
    public void addSharedFiles(User user, KademliaFile file) {
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
     * Get all the downloads
     *
     * @return  list of the completed downloads
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
                KademliaFileData data = new KademliaFileData(
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("university"),
                        resultSet.getString("department"),
                        resultSet.getString("course"),
                        resultSet.getString("teacher")
                );

                KademliaFile file = new KademliaFile(
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
        KademliaFile file = download.getFile();
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
        KademliaFile file = download.getFile();
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

}
