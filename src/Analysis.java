import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Ayush Bandil on 22/11/2019.
 */
public class Analysis {
    private static double tolerance = 100; //in meters
    private static double avgDeflection = 0d;
    private static int totalPoints = 0;

    public static void doAnalysis(ArrayList<Coordinates> bingCoordinates, ArrayList<Coordinates> osmCoordinates) {

        // a simple approach to find the minimum distance of the osmCoordinates from the set of points in Bing
//        getDeflectionBasedOnPoints(bingCoordinates, osmCoordinates);

        // each pair of adjacent points in Bing are connected through a line and the minimum distance is calculated for each point on OSM
        getDeflectionBasedOnLineSegments(bingCoordinates, osmCoordinates);


    }

    private static void getDeflectionBasedOnLineSegments(ArrayList<Coordinates> bingCoordinates, ArrayList<Coordinates> osmCoordinates) {
        ArrayList<Double> osmDistances = new ArrayList<>();
        int count = 0;
        for (Coordinates osmCoordinate : osmCoordinates) {
            count++;
            double dist = getLeastDistanceFromSetOfLinesUsingCode(osmCoordinate, bingCoordinates);
            double dist1 = getLeastDistanceFromSetOfLinesUsingSql(osmCoordinate, bingCoordinates);

            if (dist < tolerance) {
                osmDistances.add(dist);
            } else {
                System.out.println("deflection for " + count + "th element " + osmCoordinate.getLat() + ", " + osmCoordinate.getLat() + " with distance " + dist + " is skipped");
            }
        }

        updateAvgDeflection(osmDistances);
    }

    private static double getLeastDistanceFromSetOfLinesUsingSql(Coordinates osmCoordinate, ArrayList<Coordinates> bingCoordinates) {
        Connection con = ConnectionUtils.getConnection();
        double min = 0d;
        String query = PropertiesReader.getValue("minDistanceQuery");
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

        System.out.println("Minimum distance is " + min);
        return min;
    }

    private static void updateAvgDeflection(ArrayList<Double> osmDistances) {
        for (Double dist : osmDistances) {
            avgDeflection = (avgDeflection * totalPoints + dist) / (totalPoints + 1);
            totalPoints++;
        }
        System.out.println("Average deflection = " + avgDeflection + " for " + totalPoints + " points");
    }

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
        System.out.println("Minimum distance is " + min + " from code");
        return min;
    }

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

    private static double latFactor() {
        return 111000d;
    }

    private static double lonFactor(double y1) {
        return 111000d * Math.cos(y1 * Math.PI / 180);
    }


    public static void main(String[] args) {
        Coordinates first = new Coordinates(2f, 0f);
        Coordinates second = new Coordinates(0f, -2f);
        double dis = getLeastDisFromLine(first, second, new Coordinates(0f, 0f));
    }

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
