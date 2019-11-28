/**
 * Created by Ayush Bandil on 22/11/2019.
 */
public class OsmRequestMessage {
    private String header = "https://router.project-osrm.org/route/v1/driving/";
    private String tailer = "?overview=false&alternatives=true&steps=true&hints=;";
    private Coordinates from;
    private Coordinates to;

    public void setFrom(Coordinates from) {
        this.from = from;
    }

    public void setTo(Coordinates to) {
        this.to = to;
    }

    public String generateRequest() {
        String toReturn = header;
        toReturn = appendCoodinates(toReturn, from);
        toReturn += ";";
        toReturn = appendCoodinates(toReturn, to);
        toReturn += tailer;
        return toReturn;
    }

    private String appendCoodinates(String parent, Coordinates coordinates) {
        return parent + coordinates.getLon().toString() + "," + coordinates.getLat().toString();
    }
}
