/**
 * Created by Ayush Bandil on 22/11/2019.
 */
public class Coordinates {
    private Float lat;
    private Float lon;

    public Coordinates(Float lat, Float lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Float getLat() {
        return lat;
    }

    public Float getLon() {
        return lon;
    }

    public Coordinates(Float[] coordinates) {
        this.lat = coordinates[0];
        this.lon = coordinates[1];
    }

    public Coordinates(Float[] coordinates, boolean isInverted) {
        if (isInverted) {
            this.lat = coordinates[1];
            this.lon = coordinates[0];
        } else {
            new Coordinates(coordinates);
        }
    }
}
