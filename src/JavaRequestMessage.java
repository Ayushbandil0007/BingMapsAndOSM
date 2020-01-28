import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

enum OutputFormats { 
	json,
	xml;
}

enum TransModesGM {
	driving,
	bicycling,
	transit
}

enum ToAvoidGM {
	tolls,
	highways,
	ferries,
	indoor
}

public class JavaRequestMessage {
	
	/*
	 *  Implement alternative routes?
	 */
	
	private static final String API_KEY = "AIzaSyAioRxicWzq2ZrYzjjhejXp0QMW9FjdItA";
	private static final String HEADER = "https://maps.googleapis.com/maps/api/directions/";
	
	private TransModesGM myMode;
	private OutputFormats myFormat;
//	private String myOrigin;
	private Coordinates myOriginCoordinates;
//	private String myDest;
	private Coordinates myDestCoordinates;
	private Collection<ToAvoidGM> myAvoid;

	/**
	 * Testing. Used coords for Seattle as start,
	 * @param args
	 */
	public static void main(String[] args) {
		JavaRequestMessage jrm = new JavaRequestMessage();
//		List<String> route = new ArrayList<>();
//		route.add("Seattle");
//		route.add("Tacoma");
//		jrm.setRoute(route);
		List<Coordinates> route = new ArrayList<>();
		route.add(new Coordinates(47.4642007,-122.2664857));
		route.add(new Coordinates(47.2527802,-122.4442681));
		jrm.setRouteCoords(route);
		jrm.setOutputFormat(OutputFormats.json);
		String request = jrm.generateGRequest();
		System.out.print(request);
		ArrayList<Coordinates> coords = jrm.parseJson(jrm.getJsonResponse(request));
		for (Coordinates c : coords) {
			System.out.println(c.convertToString());
		}
	}

	public ArrayList<Coordinates> parseJson(String myJson) {
		JsonElement jsonElement = new JsonParser().parse(myJson);
		JsonObject jObj = jsonElement.getAsJsonObject();
		JsonArray routesArr = jObj.getAsJsonArray("routes");
		JsonObject route1 = routesArr.get(0).getAsJsonObject();
		JsonArray legs = route1.getAsJsonArray("legs");
		JsonArray steps = legs.get(0).getAsJsonObject().get("steps")
				.getAsJsonArray();

		Iterator<JsonElement> iterator = steps.iterator();
		ArrayList<Coordinates> coords = new ArrayList<>();
		while (iterator.hasNext()) {
			JsonObject obj = iterator.next().getAsJsonObject();
			JsonObject startLoc = obj.getAsJsonObject("start_location");
			JsonObject endLoc = obj.getAsJsonObject("end_location");

			Coordinates start = new Coordinates(
					startLoc.get("lat").getAsDouble(),
					startLoc.get("lng").getAsDouble());
			Coordinates end = new Coordinates(
					endLoc.get("lat").getAsDouble(),
					endLoc.get("lng").getAsDouble());
			if (!coords.contains(start)) {
				coords.add(start);
			}
			if (!coords.contains(end)) {
				coords.add(end);
			}
		}

		return coords;
	}
	
	public String generateGRequest() { 
		StringBuilder sb = new StringBuilder();
		sb.append(HEADER);
		sb.append(myFormat.toString() + "?");
//		sb.append("origin=" + myOrigin + "&");
//		sb.append("destination=" + myDest);
		sb.append("origin=" + myOriginCoordinates.convertToString() + "&");
		sb.append("destination=" + myDestCoordinates.convertToString());
		if (myAvoid != null && !myAvoid.isEmpty()) { 
			sb.append("?");
			Iterator<ToAvoidGM> obs = myAvoid.iterator();
			while (obs.hasNext()) { 
				sb.append(obs.next().toString() + "|");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		if (myMode != null) { 
			sb.append("?");
			sb.append(myMode.toString());
		}		
		sb.append("&key=" + API_KEY);
		return sb.toString();
	}
	
	public void setRoute(Collection<String> theRoute) {
		Iterator<String> iterator = theRoute.iterator();
//		myOrigin = iterator.next();
//		myOrigin = myOrigin.replaceAll(" ", "+");
//		myDest = iterator.next();
//		myDest = myDest.replaceAll(" ", "+");
	}

	public void setRouteCoords(Collection<Coordinates> theRoute) {
		Iterator<Coordinates> iterator = theRoute.iterator();
		myOriginCoordinates = iterator.next();
		myDestCoordinates = iterator.next();
	}
	
	public void setAvoid(Collection<ToAvoidGM> toAvoid) { 
		this.myAvoid = toAvoid;
	}
	
	public void setOutputFormat(OutputFormats theFormat) { 
		this.myFormat = theFormat;
	}
	
	private void setMode(TransModesGM theMode) { 
		this.myMode = theMode;
	}		
	
	public String getJsonResponse(String urlStr) {
        URL url = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(15 * 1000);
            connection.connect();

            // read the output from the server
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
