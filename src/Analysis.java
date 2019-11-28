import java.util.ArrayList;

/**
 * Created by Ayush Bandil on 22/11/2019.
 */
public class Analysis {

    public static void doAnalysis(ArrayList<Coordinates> bingCoordinates, ArrayList<Coordinates> osmCoordinates) {

        // a simple approach to find the minimum distance of the osmCoordinates from the set of points in Bing
        getDeflectionBasedOnPoints(bingCoordinates, osmCoordinates);

        // each pair of adjacent points in Bing are connected through a line and the minimum distance is calculated for each point on OSM
        getDeflectionBasedOnLineSegments(bingCoordinates, osmCoordinates);


    }

    private static void getDeflectionBasedOnLineSegments(ArrayList<Coordinates> bingCoordinates, ArrayList<Coordinates> osmCoordinates) {

        for (int i = 0; i < osmCoordinates.size(); i++) {
            double dist = getLeastDistanceFromSetOfLines(osmCoordinates.get(i), bingCoordinates);
            System.out.println("Line based deflection " + dist);
        }
    }

    private static double getLeastDistanceFromSetOfLines(Coordinates coordinates, ArrayList<Coordinates> bingCoordinates) {
        double min = Double.MAX_VALUE;
        for (int i = 1; i < bingCoordinates.size(); i++) {
            Coordinates firstCor = bingCoordinates.get(i - 1);
            Coordinates secondCor = bingCoordinates.get(i);
            double dist = getLeastDisFromLine(firstCor, secondCor, coordinates);
            if (dist < min) {
                min = dist;
            }
        }
        return min;
    }

    private static double getLeastDisFromLine(Coordinates firstCor, Coordinates secondCor, Coordinates coordinates) {
        double x1 = firstCor.getLon();
        double y1 = firstCor.getLat();
        double x2 = secondCor.getLon();
        double y2 = secondCor.getLat();
        double x3 = coordinates.getLon();
        double y3 = coordinates.getLat();
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
