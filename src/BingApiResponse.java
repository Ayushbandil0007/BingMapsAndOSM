import java.util.ArrayList;

/**
 * Created by Ayush Bandil on 17/11/2019.
 */

public class BingApiResponse {
    private String authenticationResultCode = "";
    private String brandLogoUri = "";
    private String copyright = "";
    private ArrayList<ResourceSets> resourceSets = new ArrayList<>();

    public String getAuthenticationResultCode() {
        return authenticationResultCode;
    }

    public void setAuthenticationResultCode(String authenticationResultCode) {
        this.authenticationResultCode = authenticationResultCode;
    }

    public String getBrandLogoUri() {
        return brandLogoUri;
    }

    public void setBrandLogoUri(String brandLogoUri) {
        this.brandLogoUri = brandLogoUri;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public ArrayList<ResourceSets> getResourceSets() {
        return resourceSets;
    }

    public void setResourceSets(ArrayList<ResourceSets> resourceSets) {
        this.resourceSets = resourceSets;
    }

    public ArrayList<Coordinates> getBingCoordinates() {
        ArrayList<Coordinates> allCoordinates = new ArrayList<>();
        RouteLegs routeLegs = getResourceSets().get(0).getResources().get(0).getRouteLegs().get(0);
        Coordinates start = new Coordinates(routeLegs.getActualStart().getCoordinates());
        allCoordinates.add(start);

        routeLegs.getItineraryItems().forEach(item -> allCoordinates.add(new Coordinates(item.getManeuverPoint().getCoordinates())));

        Coordinates end = new Coordinates(routeLegs.getActualEnd().getCoordinates());
        allCoordinates.add(end);

        return allCoordinates;
    }

}

class ResourceSets {
    private int estimatedTotal = 0;
    private ArrayList<Resources> resources = new ArrayList<>();

    public int getEstimatedTotal() {
        return estimatedTotal;
    }

    public void setEstimatedTotal(int estimatedTotal) {
        this.estimatedTotal = estimatedTotal;
    }

    public ArrayList<Resources> getResources() {
        return resources;
    }

    public void setResources(ArrayList<Resources> resources) {
        this.resources = resources;
    }
}

class Resources {
    private String __type = "";
    private Float[] bbox = new Float[4];
    private String id = "";
    private String distanceUnit = "";
    private String durationUnit = "";
    private ArrayList<RouteLegs> routeLegs = new ArrayList<>();

    public String get__type() {
        return __type;
    }

    public void set__type(String __type) {
        this.__type = __type;
    }

    public Float[] getBbox() {
        return bbox;
    }

    public void setBbox(Float[] bbox) {
        this.bbox = bbox;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public String getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }

    public ArrayList<RouteLegs> getRouteLegs() {
        return routeLegs;
    }

    public void setRouteLegs(ArrayList<RouteLegs> routeLegs) {
        this.routeLegs = routeLegs;
    }
}

class RouteLegs {
    private ActualPoints actualEnd = new ActualPoints();
    private ActualPoints actualStart = new ActualPoints();
    private int cost = 0;
    private String description = "";
    private ArrayList<ItineraryItems> itineraryItems = new ArrayList<>();

    public ActualPoints getActualEnd() {
        return actualEnd;
    }

    public void setActualEnd(ActualPoints actualEnd) {
        this.actualEnd = actualEnd;
    }

    public ActualPoints getActualStart() {
        return actualStart;
    }

    public void setActualStart(ActualPoints actualStart) {
        this.actualStart = actualStart;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<ItineraryItems> getItineraryItems() {
        return itineraryItems;
    }

    public void setItineraryItems(ArrayList<ItineraryItems> itineraryItems) {
        this.itineraryItems = itineraryItems;
    }
}

class ActualPoints {
    private String type = "";
    private Float[] coordinates = new Float[2];

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Float[] coordinates) {
        this.coordinates = coordinates;
    }
}

class ItineraryItems {
    private String compassDirection = "";
    private ManeuverPoint maneuverPoint = new ManeuverPoint();

    public String getCompassDirection() {
        return compassDirection;
    }

    public void setCompassDirection(String compassDirection) {
        this.compassDirection = compassDirection;
    }

    public ManeuverPoint getManeuverPoint() {
        return maneuverPoint;
    }

    public void setManeuverPoint(ManeuverPoint maneuverPoint) {
        this.maneuverPoint = maneuverPoint;
    }
}

class ItineraryItemDetails {

}

class ManeuverPoint{
    private String type = "";
    Float[] coordinates = new Float[2];

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Float[] coordinates) {
        this.coordinates = coordinates;
    }
}
