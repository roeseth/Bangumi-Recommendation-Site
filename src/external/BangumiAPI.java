package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class BangumiAPI {
	private static final String HOST = "https://api.bgm.tv";
	private static final String ENDPOINT = "/user/";
	private static final String DEFAULT_TERM = "";
	private static final int SEARCH_LIMIT = 20;

	private static final String TOKEN_TYPE = "Bearer";
	private static final String API_KEY = "4a75cb0d0be46787a9e7752bfcd8a97cbb951520";
	
	public JSONArray search( String username, String cat ) {
		if ( username == null || username.isEmpty() ) {
			username = DEFAULT_TERM;
		}
		try {
			username = URLEncoder.encode( username, "UTF-8" );
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
		
		if ( cat == null || cat.isEmpty() ) {
			cat = "watching";
		}
		try {
			cat = URLEncoder.encode( cat, "UTF-8" );
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
		
		String query = String.format( "cat=%s", cat );
		String url = HOST + ENDPOINT + username + "/collection?" + query;
		
		try {
			HttpURLConnection connection = ( HttpURLConnection ) new URL(url).openConnection();
			
			connection.setRequestMethod( "GET" );
			connection.setRequestProperty( "Authorization", TOKEN_TYPE + " " + API_KEY );
			connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/69.0.3497.100 Safari/537.36");
			
			int responseCode = connection.getResponseCode();
			
			//for debug
			System.out.println( "Sending Request to URL:" + url );
			System.out.println( "Response Code:" + responseCode );
			
			if ( responseCode != 200 ) {
				return new JSONArray();
			}
			
			BufferedReader in = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
			String inputLine = "";
			StringBuilder response = new StringBuilder();
			
			while ( ( inputLine = in.readLine() ) != null ) {
				response.append( inputLine );
			}
			in.close();
			JSONArray array = new JSONArray( response.toString() );
			return array;
			/*
			if ( obj.isNull( "username" ) ) {
				return obj.getJSONArray( "username" );
			}
			*/
			
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
		
		return new JSONArray();
	}
	
	// Convert JSONArray to a list of Item object.
	private List<Item> getItemList( JSONArray items ) throws JSONException {
		List<Item> list = new ArrayList<>();
		
		for ( int i = 0; i < items.length(); i++ ) {
			JSONObject item = items.getJSONObject(i);
			
			ItemBuilder builder = new ItemBuilder();
			
			if ( !item.isNull("id") ) {
				builder.setItemId( item.getString("id") );
			}
			if ( !item.isNull("name") ) {
				builder.setName( item.getString("name") );
			}
			if ( !item.isNull("type") ) {
				builder.setType( item.getInt("type") );
			}
			if ( !item.isNull("summary") ) {
				builder.setSummary( item.getString("name") );
			}
			if ( !item.isNull("episodes") ) {
				builder.setEpisodes( item.getInt("eps") );
			}
			if ( !item.isNull("url") ) {
				builder.setUrl( item.getString("url") );
			}
			
			builder.setRating( getRating(item) );
			builder.setImages( getImages(item) );
			
			list.add( builder.builder() );
		}
		
		return list;
	}
	
	// helper to
	private Set<String> getRating( JSONObject item ) throws JSONException {
		Set<String> rating = new HashSet<>();

		return rating;
	}

	private Set<String> getImages( JSONObject item ) throws JSONException {
		Set<String> images = new HashSet<>();

		return images;

	}
	
	private void queryAPI( String username ) {
		JSONArray items = search( username, null );
		try {
			for ( int i=0; i < items.length(); i++ ) {
				JSONObject item = items.getJSONObject( i );
				System.out.println( item );
			}
		}
		catch ( JSONException e ) {
			e.printStackTrace();
		}
	}
	
	// testing
	public static void main( String[] args ) {
		BangumiAPI tmpAPI = new BangumiAPI();
		tmpAPI.queryAPI("roeseth");
	}
}
