import java.util.ArrayList;

/**
 * Created by Ayush Bandil on 22/11/2019.
 */
public class Analysis {

    public static void doAnalysis(ArrayList<Coordinates> bingCoordinates, ArrayList<Coordinates> osmCoordinates) {
        ArrayList<Double> deflections = new ArrayList<>();
        for (int i = 0; i < osmCoordinates.size(); i++) {
            double deflection = getDeflection(bingCoordinates, osmCoordinates.get(i));
            deflections.add(deflection);
            System.out.println(deflection);
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
