import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Ayush Bandil on 22/11/2019.
 */
public class Analysis {
    private static double tolerance = 200; //in meters
    private static double avgDeflection = 0d;
    private static int totalPoints = 0;
    private static HashMap<Integer, AreaDeflection> areaDefMap = new HashMap<>();
    private static HashMap<Coordinates, AreaDeflection> areaHeatmap = new HashMap<>();

    public static void doAnalysis(ArrayList<Coordinates> bingCoordinates, ArrayList<Coordinates> osmCoordinates) {

        // a simple approach to find the minimum distance of the osmCoordinates from the set of points in Bing
//        getDeflectionBasedOnPoints(bingCoordinates, osmCoordinates);

        // each pair of adjacent points in Bing are connected through a line and the minimum distance is calculated for each point on OSM
        getDeflectionBasedOnLineSegments(bingCoordinates, osmCoordinates);
    }

    //for each osm point im checking the minimum distance to the bing curve to make sure it is less than a specified tolerance, using SQL
    private static void getDeflectionBasedOnLineSegments(ArrayList<Coordinates> bingCoordinates, ArrayList<Coordinates> osmCoordinates) {
        HashMap<Coordinates, Double> osmDistancesMap = new HashMap<>();
        int count = 0;
        for (Coordinates osmCoordinate : osmCoordinates) {
            count++;
//            double dist = getLeastDistanceFromSetOfLinesUsingCode(osmCoordinate, bingCoordinates);
            double dist1 = getLeastDistanceFromSetOfLinesUsingSql(osmCoordinate, bingCoordinates);
//            System.out.println("Point " + count + " difference is " + (dist - dist1));


            //if its less than the threshold, we add it to osmDistance arraylist
            if (dist1 < PropertiesReader.getInt("tolerance")) {
                osmDistancesMap.put(osmCoordinate, dist1);
            } else {
//                System.out.println("deflection for " + count + "th element " + osmCoordinate.getLat() + ", " + osmCoordinate.getLat() + " with distance " + dist1 + " is skipped");
            }
        }
        updateAvgDeflection(new ArrayList<>(osmDistancesMap.values()));
        updateHeatmap(osmDistancesMap);
    }

    private static void updateHeatmap(HashMap<Coordinates, Double> osmDistances) {
        if (areaHeatmap.size() == 0) {
            areaHeatmap = intializeAreaHeatmap(Main.area);
        }

        for (Map.Entry<Coordinates, Double> dist : osmDistances.entrySet()) {
            Double lat = dist.getKey().getLat();
            Double lon = dist.getKey().getLon();

            lat = Math.ceil(lat / 0.05) * 0.05;
            lon = Math.ceil(lon / 0.05) * 0.05;
            Coordinates cor = new Coordinates(lat, lon, 1);
            if (areaHeatmap.containsKey(cor)) {
                AreaDeflection avgDef = areaHeatmap.get(cor);
                Integer noOfPoints = avgDef.getNoOfPoints();
                Double avgDeflection = avgDef.getAvgDeflection();
                avgDeflection = (avgDeflection * noOfPoints + dist.getValue()) / (noOfPoints + 1);
                avgDef.setAvgDeflection(avgDeflection);
                avgDef.setNoOfPoints(noOfPoints + 1);
                areaHeatmap.put(cor, avgDef);
            } else {
                AreaDeflection avgDef = new AreaDeflection(new Area(Main.area, null, null), null, null, dist.getValue(), 1);
                areaHeatmap.put(cor, avgDef);
            }
        }
        System.out.println("   Updated heatmap using " + osmDistances.size() + " points");
        updateheatmapTable();
    }

    private static void updateheatmapTable() {
        String city = Main.area;
        String clearQuery = PropertiesReader.getString("clearPrevHeatmapEntries");
        clearQuery = clearQuery.replace("<CITY>", city);
        ConnectionUtils.executeJdbcQuery(clearQuery);

        String insertQuery = "";
        ArrayList<String> queries = new ArrayList<>();

        // Display the TreeMap which is naturally sorted
        for (Map.Entry<Coordinates, AreaDeflection> x : areaHeatmap.entrySet()) {
            AreaDeflection areaDeflection = x.getValue();
            insertQuery = PropertiesReader.getString("heatMapInsertionQuery");
            insertQuery = insertQuery.replace("<CITY>", city);
            insertQuery = insertQuery.replace("<STATE>", "");
            insertQuery = insertQuery.replace("<COUNTRY>", "");
            insertQuery = insertQuery.replace("<LAT_MAX>", Double.toString(x.getKey().getLat()));
            insertQuery = insertQuery.replace("<LONG_MAX>", Double.toString(x.getKey().getLon()));
            insertQuery = insertQuery.replace("<DATASET_PTS_COUNT>", areaDeflection.getNoOfPoints().toString());
            queries.add(insertQuery);
        }
        ConnectionUtils.executeJdbcBatchQuery(queries);
    }

    private static HashMap<Coordinates, AreaDeflection> intializeAreaHeatmap(String areaStr) {
        HashMap<Coordinates, AreaDeflection> areaHeatmap = new HashMap<>();
        Area area = new Area(areaStr);
        Connection con = ConnectionUtils.getConnection();

        String query = PropertiesReader.getString("heatmapSelectQuery");
        if (area.getCity() != null) {
            query = query.replace("<CITY>", area.getCity());
        } else {
            query = query.replace("CITY = '<CITY>'", "");
        }
        if (area.getState() != null) {
            query = query.replace("<STATE>", area.getState());
        } else {
            query = query.replace(" and STATE = '<STATE>'", "");
        }
        if (area.getCountry() != null) {
            query = query.replace("<COUNTRY>", area.getCountry());
        } else {
            query = query.replace(" and COUNTRY = '<COUNTRY>'", "");
        }

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Coordinates cor = new Coordinates(rs.getDouble("LAT_MAX"), rs.getDouble("LONG_MAX"), 1);
                AreaDeflection areaDef = new AreaDeflection(area, null, null, rs.getDouble("AVG_DEFLECTION"), rs.getInt("DATASET_PTS_COUNT"));
                areaHeatmap.put(cor, areaDef);
            }
            rs.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return areaHeatmap;


    }

    private static double getLeastDistanceFromSetOfLinesUsingSql(Coordinates osmCoordinate, ArrayList<Coordinates> bingCoordinates) {
        Connection con = ConnectionUtils.getConnection();
        double min = 0d;
        String query = PropertiesReader.getString("minDistanceQuery");
        final String[] bingString = {""};

        bingCoordinates.forEach(cor -> {
            bingString[0] += cor.getLon() + " " + cor.getLat() + ",";
        });
        bingString[0] = bingString[0].substring(0, bingString[0].length() - 1); // removing last ,

        query = query.replace("<CORDINATE_STRING>", bingString[0]);
        query = query.replace("<OSM_POINT>", osmCoordinate.getLon() + " " + osmCoordinate.getLat());

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                min = rs.getDouble("DISTANCE");
            }
            rs.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        System.out.println("Minimum distance is " + min + " from SQL Server");
        return min;
    }

    private static void updateAvgDeflection(ArrayList<Double> osmDistances) {
        if (areaDefMap.size() == 0) {
            areaDefMap = intializeAreaDef(Main.area);
        }

        for (Double dist : osmDistances) {
            Integer rangeUpper = (int) Math.ceil(dist / 5) * 5;
            if (areaDefMap.containsKey(rangeUpper)) {
                AreaDeflection avgDef = areaDefMap.get(rangeUpper);
                Integer noOfPoints = avgDef.getNoOfPoints();
                Double avgDeflection = avgDef.getAvgDeflection();
                avgDeflection = (avgDeflection * noOfPoints + dist) / (noOfPoints + 1);
                avgDef.setAvgDeflection(avgDeflection);
                avgDef.setNoOfPoints(noOfPoints + 1);
                areaDefMap.put(rangeUpper, avgDef);
            } else {
                AreaDeflection avgDef = new AreaDeflection(new Area(Main.area, null, null), rangeUpper, null, dist, 1);
                areaDefMap.put(rangeUpper, avgDef);
            }
        }
        System.out.println("   Updated average deflection using " + osmDistances.size() + " points");
        updateAvgDefTable();
    }

    private static void updateAvgDefTable() {
        String city = Main.area;
        String clearQuery = PropertiesReader.getString("clearPrevAreaDefEntries");
        clearQuery = clearQuery.replace("<CITY>", city);
        ConnectionUtils.executeJdbcQuery(clearQuery);
//        System.out.println("Previous entries for " + city + " have been cleared");

        String insertQuery = "";
        ArrayList<String> queries = new ArrayList<>();


        ArrayList<Integer> sortedKeys =
                new ArrayList<>(areaDefMap.keySet());
        Collections.sort(sortedKeys);

        // Display the TreeMap which is naturally sorted
        for (Integer x : sortedKeys) {
            AreaDeflection areaDeflection = areaDefMap.get(x);
            insertQuery = PropertiesReader.getString("areaDefInsertQuery");
            insertQuery = insertQuery.replace("<CITY>", city);
            insertQuery = insertQuery.replace("<STATE>", "");
            insertQuery = insertQuery.replace("<COUNTRY>", "");
            insertQuery = insertQuery.replace("<MAX_DEF_RANGE>", areaDeflection.getRangeUpper().toString());
            insertQuery = insertQuery.replace("<AVG_DEFLECTION>", areaDeflection.getAvgDeflection().toString());
            insertQuery = insertQuery.replace("<DATASET_PTS_COUNT>", areaDeflection.getNoOfPoints().toString());
            queries.add(insertQuery);
        }
        ConnectionUtils.executeJdbcBatchQuery(queries);
    }

    private static HashMap<Integer, AreaDeflection> intializeAreaDef(String areaStr) {
        HashMap<Integer, AreaDeflection> areaDefMap = new HashMap<>();
        Area area = new Area(areaStr);
        Connection con = ConnectionUtils.getConnection();

        String query = PropertiesReader.getString("areaDefQuery");
        if (area.getCity() != null) {
            query = query.replace("<CITY>", area.getCity());
        } else {
            query = query.replace("CITY = '<CITY>'", "");
        }
        if (area.getState() != null) {
            query = query.replace("<STATE>", area.getState());
        } else {
            query = query.replace(" and STATE = '<STATE>'", "");
        }
        if (area.getCountry() != null) {
            query = query.replace("<COUNTRY>", area.getCountry());
        } else {
            query = query.replace(" and COUNTRY = '<COUNTRY>'", "");
        }

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer maxDefRange = rs.getInt("MAX_DEF_RANGE");
                areaDefMap.put(maxDefRange, new AreaDeflection(new Area(rs.getString("CITY"), rs.getString("STATE"),
                        rs.getString("COUNTRY")), maxDefRange, null, rs.getDouble("AVG_DEFLECTION"), rs.getInt("DATASET_PTS_COUNT")));
            }
            rs.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return areaDefMap;
    }

    private static CharSequence wrapStr(String string) {
        return "'" + string + "'";
    }

    @Deprecated
    private static double getLeastDistanceFromSetOfLinesUsingCode(Coordinates coordinates, ArrayList<Coordinates> bingCoordinates) {
        double min = Double.MAX_VALUE;
        for (int i = 1; i < bingCoordinates.size(); i++) {
            Coordinates firstCor = bingCoordinates.get(i - 1);
            Coordinates secondCor = bingCoordinates.get(i);
            double dist = getLeastDisFromLine(firstCor, secondCor, coordinates);
            if (dist < min) {
                min = dist;
            }
        }
//        System.out.println("Minimum distance is " + min + " from code");
        return min;
    }

    @Deprecated
    private static double getLeastDisFromLine(Coordinates firstCor, Coordinates secondCor, Coordinates coordinates) {
        double y1 = firstCor.getLat() * latFactor();
        double x1 = firstCor.getLon() * lonFactor(firstCor.getLat());
        double y2 = secondCor.getLat() * latFactor();
        double x2 = secondCor.getLon() * lonFactor(secondCor.getLat());
        double y3 = coordinates.getLat() * latFactor();
        double x3 = coordinates.getLon() * lonFactor(coordinates.getLat());

        double m = 0d;
        if (y1 != y2) {
            double slope = (x2 - x1) / (y1 - y2);
            m = slope * (x1 - x3) - (y1 - y3);
            m /= -slope * (x2 - x3) - (y2 - y3);
        } else {
            m = (x3 - x1) / (x2 - x3);
        }

        if (m > 0) {
            double x4 = (x1 + m * x2) / (1 + m);
            double y4 = (y1 + m * y2) / (1 + m);
            double distance = Math.pow(x4 - x3, 2) + Math.pow(y4 - y3, 2);
            return Math.sqrt(distance);
        }

        double dis1 = Math.pow(x1 - x3, 2) + Math.pow(y1 - y3, 2);
        double dis2 = Math.pow(x2 - x3, 2) + Math.pow(y2 - y3, 2);
        ;
        return Math.min(Math.sqrt(dis1), Math.sqrt(dis2));
    }

    @Deprecated
    private static double latFactor() {
        return 111000d;
    }

    @Deprecated
    private static double lonFactor(double y1) {
        return 111000d * Math.cos(y1 * Math.PI / 180);
    }

    @Deprecated
    private static void getDeflectionBasedOnPoints(ArrayList<Coordinates> bingCoordinates, ArrayList<Coordinates> osmCoordinates) {
        ArrayList<Double> deflections = new ArrayList<>();
        for (int i = 0; i < osmCoordinates.size(); i++) {
            double deflection = getDeflection(bingCoordinates, osmCoordinates.get(i));
            deflections.add(deflection);
            System.out.println("Point based deflection " + deflection);
        }
    }

    private static double getDeflection(ArrayList<Coordinates> bingCoordinates, Coordinates coordinates) {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < bingCoordinates.size(); i++) {
            Coordinates bingPt = bingCoordinates.get(i);
            double deflection = Math.pow(coordinates.getLat() - bingPt.getLat(), 2) + Math.pow(coordinates.getLon() - bingPt.getLon(), 2);
            if (deflection < min) {
                min = deflection;
            }
        }
        return Math.sqrt(min);
    }
}
