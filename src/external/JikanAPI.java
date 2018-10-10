package external;

import java.io.BufferedReader;
import java.io.IOException;
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

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class JikanAPI {
	private static final String HOST = "https://api.jikan.moe";
	private static final String MIDPOINT = "/v3/user";
	private static final String ENDPOINT = "/animelist/completed/1";
	private static final String DEFAULT_TERM = "";
	private static final int SEARCH_LIMIT = 20;

//	private static final String TOKEN_TYPE = "Bearer";
//	private static final String API_KEY = "f30ff8ec0677a6ecd38e0d1a9f00a87041bac1c8";
	
	public List<Item> search( String username, int searchLimit) {
		if ( username == null || username.isEmpty() ) {
			username = DEFAULT_TERM;
		}
		try {
			username = URLEncoder.encode( username, "UTF-8" );
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
		
		String url = HOST + MIDPOINT + "/" + username + ENDPOINT;
		StringBuilder response = getJSONString(url);
		
		try {
			return getItemList( new JSONObject( response.toString() ), searchLimit );
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Convert the entire JSONArray from API to a list of Item object.
	private List<Item> getItemList( JSONObject jsonObj, int searchLimit ) throws JSONException, IOException {
		List<Item> list = new ArrayList<>();
		
		JSONArray anime = jsonObj.getJSONArray("anime");
		if (searchLimit == 0) {
			searchLimit = SEARCH_LIMIT;
		}
		for ( int i = 0; i < anime.length() && i < searchLimit; i++ ) {
			JSONObject item = anime.getJSONObject(i);
			
			ItemBuilder builder = new ItemBuilder();
			if ( !item.isNull("mal_id") ) {
				builder.setItemId( item.getInt("mal_id") );
			}
			if ( !item.isNull("title") ) {
				builder.setName( item.getString("title") );
			}
			if ( !item.isNull("start_date") ) {
				builder.setDate( item.getString("start_date").substring(0, 10) );
			}
			if ( !item.isNull("score") ) {
				builder.setUserScore( item.getDouble("score") );
			}
			
			if ( !item.isNull("total_episodes") ) {
				builder.setEpisodes( item.getInt("total_episodes") );
			}
			if ( !item.isNull("url") ) {
				builder.setUrl( item.getString("url") );
			}
			if ( !item.isNull("image_url") ) {
				builder.setImageUrl( item.getString("image_url") );
			}
			
			// fetch new single entry JSON
			StringBuilder singleGET = getJSONString( HOST + "/anime/" + item.getInt("mal_id")  );
			JSONObject singleEntry = new JSONObject( singleGET.toString() );
			
			builder.setRating( getRating(singleEntry) );
			builder.setSummary( getSummary(singleEntry) );
			builder.setGenres(getGenres(singleEntry));
			builder.setName(getName(singleEntry));
			
			list.add( builder.builder() );
			
		}
		
		return list;
	}
	
	private String getName( JSONObject item ) throws JSONException {
		String name = "";
		if ( !item.isNull( "title_japanese" ) ) {
			name = item.getString( "title_japanese" );
		}
		return name;
	}

	// Helper to parse the score from the new Json
	private double getRating( JSONObject item ) throws JSONException {
		double score = 0;
		if ( !item.isNull( "score" ) ) {
			score = item.getDouble( "score" );
		}
		return score;
	}

	// Help to parse the summary from the new Json
	private String getSummary( JSONObject item ) throws JSONException {
		String summary = "";
		if ( !item.isNull( "synopsis" ) ) {
			summary = item.getString( "synopsis" );
		}
		return summary;
	}
	
	// Help to parse the genres from the new Json
	private Set<String> getGenres( JSONObject item ) throws JSONException {
		Set<String> genres = new HashSet<>();
		if( !item.isNull( "genres" ) ) {
			JSONArray array = item.getJSONArray( "genres" );
			for ( int i = 0; i < array.length(); i++ ) {
				JSONObject genre = array.getJSONObject(i);
				if( !genre.isNull( "name" ) ) {
					genres.add( genre.getString( "name" ) );
				}
			}
		}
		return genres;

	}
	
	// Testing queryAPI
	private void queryAPI( String username, int limit ) {
		List<Item> itemList = search( username, limit );
		for( Item item : itemList ) {
			item.toJSONObject();
			JSONObject jsonObject = item.toJSONObject();
			System.out.println( jsonObject );
		}
	}
	
	// fetch new JSON String from URL
	private StringBuilder getJSONString( String url ) {
		StringBuilder response = new StringBuilder();
		try {
			HttpURLConnection connection = ( HttpURLConnection ) new URL(url).openConnection();
			
			connection.setRequestMethod( "GET" );
			// no auth required for Jikan API
//			connection.setRequestProperty( "Authorization", TOKEN_TYPE + " " + API_KEY );
			connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/69.0.3497.100 Safari/537.36");
			
			int responseCode = connection.getResponseCode();
			
			// for debug
			System.out.println( "Sending Request to URL:" + url );
			System.out.println( "Response Code:" + responseCode );
			
			if ( responseCode != 200 ) {
				return response;
//				return new JSONArray();
			}
			
			BufferedReader in = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
			String inputLine = "";
			
			while ( ( inputLine = in.readLine() ) != null ) {
				response.append( inputLine );
			}
			in.close();
		}
		catch ( Exception e) {
				e.printStackTrace();
		}
		return response;
	}
	
	public static void main( String[] args ) {
		JikanAPI tmpAPI = new JikanAPI();
		tmpAPI.queryAPI("Infinite", 20);
	}
}
