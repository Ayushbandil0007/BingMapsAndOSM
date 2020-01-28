import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	private String myOrigin;
	private String myDest; 
	private Collection<ToAvoidGM> myAvoid;
	
	public static void main(String[] args) {
		JavaRequestMessage jrm = new JavaRequestMessage();
		List<String> route = new ArrayList<>();
		route.add("Seattle");
		route.add("Tacoma");
		jrm.setRoute(route);
		jrm.setOutputFormat(OutputFormats.json);
		String str2 = jrm.generateGRequest();
		System.out.println(str2);
		System.out.println(jrm.getJsonResponse(str2));
	}
	
	public String generateGRequest() { 
		StringBuilder sb = new StringBuilder();
		sb.append(HEADER);
		sb.append(myFormat.toString() + "?");
		sb.append("origin=" + myOrigin + "&");
		sb.append("destination=" + myDest);
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
		myOrigin = iterator.next();
		myOrigin = myOrigin.replaceAll(" ", "+");
		myDest = iterator.next();
		myDest = myDest.replaceAll(" ", "+");
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
