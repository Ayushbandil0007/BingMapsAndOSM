import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Ayush Bandil on 23/1/2020.
 */
public class ConnectionUtils {
    public static Connection getConnection() {
        String url = null;
        String username = null;
        String password = null;
        try {
            url = PropertiesReader.getString("url");
            username = PropertiesReader.getString("username");
            password = PropertiesReader.getString("password");
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

    public static void executeJdbcQuery(String query) {
        Connection con = ConnectionUtils.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            ps.execute();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void executeJdbcBatchQuery(ArrayList<String> queries) {
        Connection con = ConnectionUtils.getConnection();
        PreparedStatement ps = null;
        try {
            Statement statement = con.createStatement();
            for (int i = 0; i < queries.size(); i++) {
                statement.addBatch(queries.get(i));
            }
            statement.executeBatch();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // double output implemented
    public static double executeJdbcSingleOutputQuery(String query){
        Connection con = ConnectionUtils.getConnection();
        Double toReturn = 0d;
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                toReturn = rs.getDouble("VALUE");
            }
            rs.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

}
