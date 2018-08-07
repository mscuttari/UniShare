package it.unishare.client.database;

import it.unishare.client.connection.ConnectionManager;
import it.unishare.client.utils.Settings;
import it.unishare.common.connection.kademlia.KademliaFile;
import it.unishare.common.connection.kademlia.KademliaFileData;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
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
     * Get connection to the SQLite database
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
        String sql = "CREATE TABLE IF NOT EXISTS my_files(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "key BLOB NOT NULL," +
                    "title TEXT NOT NULL," +
                    "university TEXT NOT NULL," +
                    "department TEXT NOT NULL," +
                    "course TEXT NOT NULL," +
                    "teacher TEXT NOT NULL" +
                    ");";

        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            statement.execute(sql);
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get all files
     */
    public List<KademliaFile> getAllFiles() {
        List<KademliaFile> result = new ArrayList<>();
        String sql = "SELECT key, title, university, department, course, teacher FROM my_files";

        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                KademliaFileData data = new KademliaFileData(
                        resultSet.getString("title"),
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

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * Add file
     *
     * @param   file    file
     */
    public void addFile(KademliaFile file) {
        String sql = "INSERT INTO my_files(key, title, university, department, course, teacher) VALUES(?,?,?,?,?,?)";

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setBytes(1, file.getKey().getBytes());
            statement.setString(2, file.getData().getTitle());
            statement.setString(3, file.getData().getUniversity());
            statement.setString(4, file.getData().getDepartment());
            statement.setString(5, file.getData().getCourse());
            statement.setString(6, file.getData().getTeacher());

            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Delete file
     *
     * @param   key     key
     */
    public void deleteFile(byte[] key) {
        String sql = "DELETE FROM my_files WHERE key = ?";

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setBytes(1, key);

            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
