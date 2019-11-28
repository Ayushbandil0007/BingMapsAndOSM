import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Ayush Bandil on 21/11/2019.
 */


enum TravelMode {
    Driving, Walking, Transit;
}

enum ToAvoid {
    highways,
    tolls,
    ferry,
    minimizeHighways,
    minimizeTolls,
    borderCrossing
}

enum Optimize {
    distance,
    time,
    timeWithTraffic,
    timeAvoidClosure
}

public class BingRequestMessage {
    private String header = "http://dev.virtualearth.net/REST/V1/Routes/";
    TravelMode travelMode = TravelMode.Driving;
    Collection<String> wayPoints = new ArrayList<>();
    Collection<String> viaWayPoints = new ArrayList<>();
    Integer heading;
    Collection<ToAvoid> avoid = new ArrayList<>();
    Integer distanceBeforeFirstTurn;
    Optimize optimize = Optimize.time;
    String bingMapsKey = "AqGyWa8wWbEbXaFePdL2ASXBXyWdLLgvoIHn4JDJWKSpS7xick_HlF3p0LBglxPl";

    public TravelMode getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(TravelMode travelMode) {
        this.travelMode = travelMode;
    }

    public Collection<String> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(Collection<String> wayPoints) {
        this.wayPoints = wayPoints;
    }

    public Collection<String> getViaWayPoints() {
        return viaWayPoints;
    }

    public void setViaWayPoints(Collection<String> viaWayPoints) {
        this.viaWayPoints = viaWayPoints;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public Collection<ToAvoid> getAvoid() {
        return avoid;
    }

    public void setAvoid(Collection<ToAvoid> avoid) {
        this.avoid = avoid;
    }

    public int getDistanceBeforeFirstTurn() {
        return distanceBeforeFirstTurn;
    }

    public void setDistanceBeforeFirstTurn(int distanceBeforeFirstTurn) {
        this.distanceBeforeFirstTurn = distanceBeforeFirstTurn;
    }

    public Optimize getOptimize() {
        return optimize;
    }

    public void setOptimize(Optimize optimize) {
        this.optimize = optimize;
    }

    public String generateRequest() {
        String toReturn = header;
        toReturn = toReturn + travelMode.toString() + "?";
        if (wayPoints != null && wayPoints.size() != 0) {
            int wpCount = 0;

            for (String wp : wayPoints) {
                if (wpCount==0){
                    toReturn = toReturn + "wp." + wpCount + "=" + wp;
                } else {
                    toReturn = append(toReturn, "wp." + wpCount + "=" + wp);
                }
                wpCount++;
            }
        }

        if (avoid.size() != 0) {
            Iterator value = avoid.iterator();
            value.hasNext();
            toReturn = append(toReturn, "avoid=" + (String) value.next().toString());
            while (value.hasNext()) {
                toReturn = append(toReturn, "," + (String) value.next());
            }
        }

        toReturn = (distanceBeforeFirstTurn != null) ? append(toReturn, "dbft=" + distanceBeforeFirstTurn) : toReturn;
        toReturn = (heading != null) ? append(toReturn, "hd=" + heading) : toReturn;
        toReturn = append(toReturn, "optimize=" + optimize.toString());
        toReturn = append(toReturn, "key=" + bingMapsKey);

        return toReturn;
    }

    public String append(String parent, String child) {
        return parent + "&" + child;
    }
}
