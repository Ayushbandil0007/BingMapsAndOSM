import com.google.gson.Gson;
import org.jfree.ui.RefineryUtilities;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.*;

/**
 * Created by Ayush Bandil on 15/11/2019.
 */

public class Main {
    private static String from = "";
    private static String to = "";
    private static String area = "Seattle";
    private static ArrayList<Coordinates> bingCoordinates;
    private static ArrayList<Coordinates> osmCoordinates;

    public static void main(String[] args) {

        ArrayList<Coordinates> areaCoordinates = generateAreaCoordinates();

        Coordinates fromCor = null;
        Coordinates toCor = null;

        while (fromCor == null || fromCor.getRouteId() != 0) {
            fromCor = areaCoordinates.get((int) (Math.random() * areaCoordinates.size()));
            from = fromCor.convertToString();
        }

        while (toCor == null || toCor.getRouteId() != 0 || to.equals(from)) {
            toCor = areaCoordinates.get((int) (Math.random() * areaCoordinates.size()));
            to = toCor.convertToString();
        }

        // Processing Bing
        {
            String bingURL = getBingURL();
            String json = getJsonResponse(bingURL);
            Gson gson = new Gson();
            BingApiResponse response = gson.fromJson(json, BingApiResponse.class);
            bingCoordinates = response.getBingCoordinates();
        }

//        for (int i = 0; i < bingCoordinates.size(); i++) {
//            System.out.println(bingCoordinates.get(i).getLat() + ", " + bingCoordinates.get(i).getLon());
//        }

        // Processing OSM
        {
            String osmURL = getOsmURL();
            String json = getJsonResponse(osmURL);
            Gson gson = new Gson();
            OsmApiResponse response = gson.fromJson(json, OsmApiResponse.class);
            osmCoordinates = response.getOsmCoordinates();
        }

        generateGraph();

        insertNewCordinates(areaCoordinates);

        Analysis.doAnalysis(bingCoordinates, osmCoordinates);

    }

    private static void insertNewCordinates(ArrayList<Coordinates> areaCoordinates) {
        Connection con = ConnectionUtils.getConnection();
        Float lat = 0f, lon = 0f;
        final int[] count = {0};

        bingCoordinates.forEach(cor -> {
            if (!areaCoordinates.contains(cor)) {
                insertCoordinate(cor.getLat(), cor.getLon(), con);
                areaCoordinates.add(cor);
                count[0]++;
            }
        });

        osmCoordinates.forEach(cor -> {
            if (!areaCoordinates.contains(cor)) {
                insertCoordinate(cor.getLat(), cor.getLon(), con);
                areaCoordinates.add(cor);
                count[0]++;
            }
        });

        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Sucessfully inserted " + count[0] + " entries");
    }

    private static void insertCoordinate(double lat, double lon, Connection con) {
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO COORDINATES" +
                    "([CITY]\n" +
                    ",[Lat]\n" +
                    ",[Long]\n" +
                    ",[Priority])\n" +
                    "VALUES\n" +
                    "('" + area +
                    "'," + lat +
                    "," + lon +
                    "," + 1 + ")");
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        message.setTo(bingCoordinates.get(bingCoordinates.size() - 1));
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

    public static ArrayList<Coordinates> generateAreaCoordinates() {
        Connection con = ConnectionUtils.getConnection();
        ArrayList<Coordinates> areaCoordinates = new ArrayList<>();
        PreparedStatement ps = null;
        double lat = 0f;
        double lon = 0f;
        int routeId = 0;

        try {
            ps = con.prepareStatement("select * from COORDINATES where CITY = '" + area + "';");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lat = rs.getDouble("Lat");
                lon = rs.getDouble("Long");
                routeId = rs.getInt("Priority");
                areaCoordinates.add(new Coordinates(lat, lon, routeId));
            }
            rs.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return areaCoordinates;
    }
}
