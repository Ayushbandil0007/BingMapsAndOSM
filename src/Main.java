import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import org.jfree.ui.RefineryUtilities;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Ayush Bandil on 15/11/2019.
 */

public class Main {
    private static String from = "Seattle%2Cwa";
    private static String to = "Tacoma%2Cwa";
    private static ArrayList<Coordinates> bingCoordinates;
    private static ArrayList<Coordinates> osmCoordinates;

    public static void main(String[] args) {

            // Processing Bing
        {
            String bingURL = getBingURL();
            String json = getJsonResponse(bingURL);
            Gson gson = new Gson();
            BingApiResponse response = gson.fromJson(json, BingApiResponse.class);
            bingCoordinates = response.getBingCoordinates();
        }

           // Processing OSM
        {
            String osmURL = getOsmURL();
            String json = getJsonResponse(osmURL);
            Gson gson = new Gson();
            OsmApiResponse response = gson.fromJson(json, OsmApiResponse.class);
            osmCoordinates = response.getOsmCoordinates();
        }

        generateGraph();

        Analysis.doAnalysis(bingCoordinates, osmCoordinates);

    }

    private static void generateGraph() {
        final XYPlotter demo = new XYPlotter("Ayush", bingCoordinates, osmCoordinates);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

    private static String getBingURL() {
        BingRequestMessage message = new BingRequestMessage();
        Collection<String> wps = new ArrayList<>();
        wps.add(from);
        wps.add(to);
        message.setWayPoints(wps);

        Collection<ToAvoid> avoid = new ArrayList<>();
        avoid.add(ToAvoid.minimizeTolls);
        message.setAvoid(avoid);

        return message.generateRequest();
    }

    private static String getOsmURL() {
        OsmRequestMessage message = new OsmRequestMessage();
        message.setFrom(bingCoordinates.get(0));
        message.setTo(bingCoordinates.get(bingCoordinates.size()-1));
        return message.generateRequest();
    }

    private static String getJsonResponse(String urlStr) {
        URL url = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(15 * 1000);
            connection.connect();

            // read the output from the server
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }



    private static String appendParem(String target, String param) {
        return target + "&" + param;
    }
}
