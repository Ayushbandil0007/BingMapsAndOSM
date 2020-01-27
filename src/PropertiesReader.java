import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Ayush Bandil on 23/1/2020.
 */
public class PropertiesReader {
    static Properties properties = new Properties();

    static String getString(String key){
        try {
            properties.load(new FileInputStream("D:\\Projects\\BingMapsRestApi\\resources\\project.properties"));
            return properties.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Integer getInt(String key){
        try {
            properties.load(new FileInputStream("D:\\Projects\\BingMapsRestApi\\resources\\project.properties"));
            return Integer.valueOf(properties.getProperty(key));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Double getDouble(String key){
        try {
            properties.load(new FileInputStream("D:\\Projects\\BingMapsRestApi\\resources\\project.properties"));
            return Double.valueOf(properties.getProperty(key));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
