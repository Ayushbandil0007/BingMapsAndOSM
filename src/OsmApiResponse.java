import java.util.ArrayList;

/**
 * Created by Ayush Bandil on 22/11/2019.
 */
public class OsmApiResponse {
    private ArrayList<Routes> routes = new ArrayList<>();

    public ArrayList<Routes> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Routes> routes) {
        this.routes = routes;
    }

    public ArrayList<Coordinates> getOsmCoordinates() {
        ArrayList<Coordinates> allCoordinates = new ArrayList<>();
        ArrayList<Steps> steps = routes.get(0).getLegs().get(0).getSteps();
//        allCoordinates.add();

        steps.forEach(step -> allCoordinates.add(new Coordinates(step.getManeuver().getLocation(), true)));
        return allCoordinates;
    }
}

class Routes {
    private ArrayList<Legs> legs = new ArrayList<>();
    private String weight_name = "";
    private Float weight;
    private Float duration;
    private Float distance;

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public ArrayList<Legs> getLegs() {
        return legs;
    }

    public void setLegs(ArrayList<Legs> legs) {
        this.legs = legs;
    }

    public String getWeight_name() {
        return weight_name;
    }

    public void setWeight_name(String weight_name) {
        this.weight_name = weight_name;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }
}

class Legs {
    private String summary = "";
    private Double weight;
    private Double duration;
    private ArrayList<Steps> steps = new ArrayList<>();
    private Float distance;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public ArrayList<Steps> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Steps> steps) {
        this.steps = steps;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }
}

class Steps {
    private ArrayList<Intersections> intersections = new ArrayList<>();
    private String driving_side;
    private String mode;
    private Maneuver maneuver;

    public ArrayList<Intersections> getIntersections() {
        return intersections;
    }

    public void setIntersections(ArrayList<Intersections> intersections) {
        this.intersections = intersections;
    }

    public String getDriving_side() {
        return driving_side;
    }

    public void setDriving_side(String driving_side) {
        this.driving_side = driving_side;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Maneuver getManeuver() {
        return maneuver;
    }

    public void setManeuver(Maneuver maneuver) {
        this.maneuver = maneuver;
    }
}

class Intersections {
    private Float[] location = new Float[2];
}

class Maneuver {
    private Float[] location = new Float[2];

    public Float[] getLocation() {
        return location;
    }
}
