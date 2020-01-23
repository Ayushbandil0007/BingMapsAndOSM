import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Ayush Bandil on 23/1/2020.
 */
public class ConnectionUtils {
    public static Connection getConnection() {
        String url = null;
        String username = null;
        String password = null;
        try {
            url = PropertiesReader.getValue("url");
            username = PropertiesReader.getValue("username");
            password = PropertiesReader.getValue("password");
            Connection connection = DriverManager.getConnection(url, username, password);
            if (connection != null) {
                return connection;
            } else {
                throw new SQLException("Could not establish connection");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
