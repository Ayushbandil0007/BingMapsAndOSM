import java.util.Objects;

/**
 * Created by Ayush Bandil on 22/11/2019.
 */
public class Coordinates {
    private Double lat;
    private Double lon;
    private int routeId;

    public Coordinates(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Coordinates(Double lat, Double lon, int routeId) {
        this.lat = lat;
        this.lon = lon;
        this.routeId = routeId;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }


    public Coordinates(Double[] coordinates, int routeId) {
        this.lat = coordinates[0];
        this.lon = coordinates[1];
        this.routeId = routeId;
    }

    public Coordinates(Double[] coordinates, boolean isInverted, int routeId) {
        if (isInverted) {
            this.lat = coordinates[1];
            this.lon = coordinates[0];
            this.routeId = routeId;
        } else {
            new Coordinates(coordinates, routeId);
        }
    }

    public String convertToString(){
        return this.getLat() + "," + this.getLon();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Coordinates)){
            return false;
        }
        Coordinates other = (Coordinates) o;
        return Objects.equals(other.getLat(), this.lat) && Objects.equals(other.getLon(), this.lon);
    }
}
