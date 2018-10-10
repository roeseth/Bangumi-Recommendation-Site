package external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;


public class JikanAPI {
	private static final String HOST = "https://api.jikan.moe";
	private static final String MIDPOINT = "/v3/user";
	private static final String ENDPOINT = "/animelist/completed";
	private static final String DEFAULT_TERM = "";
	private static final int SEARCH_LIMIT = 20;
	private Map<String, Integer> map = new HashMap<>();

//	private static final String TOKEN_TYPE = "Bearer";
//	private static final String API_KEY = "f30ff8ec0677a6ecd38e0d1a9f00a87041bac1c8";
	
	public JikanAPI() {
		map.put("Action", 1);
		map.put("Adventure", 2);
		map.put("Cars", 3);
		map.put("Comedy", 4);
		map.put("Dementia", 5);
		map.put("Demons", 6);
		map.put("Mystery", 7);
		map.put("Drama", 8);
		map.put("Ecchi", 9);
		map.put("Fantasy", 10);
		map.put("Game", 11);
		map.put("Hentai", 12);
		map.put("Historical", 13);
		map.put("Horror", 14);
		map.put("Kids", 15);
		map.put("Magic", 16);
		map.put("Martial Arts", 17);
		map.put("Mecha", 18);
		map.put("Music", 19);
		map.put("Parody", 20);
		map.put("Samurai", 21);
		map.put("Romance", 22);
		map.put("School", 23);
		map.put("Sci-Fi", 24);
		map.put("Shoujo", 25);
		map.put("Shoujo Ai", 26);
		map.put("Shounen", 27);
		map.put("Shounen Ai", 28);
		map.put("Space", 29);
		map.put("Sports", 30);
		map.put("Super Power", 31);
		map.put("Vampire", 32);
		map.put("Yaoi", 33);
		map.put("Yuri", 34);
		map.put("Harem", 35);
		map.put("Slice of Life", 36);
		map.put("Supernatural", 37);
		map.put("Military", 38);
		map.put("Police", 39);
		map.put("Psychological", 40);
		map.put("Thriller", 41);
		map.put("Seinen", 42);
		map.put("Josei", 43);
	}
	
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
	
	public List<Item> searchByGenre(String genre, int limit) {
		if ( genre == null || genre.isEmpty() ) {
			genre = DEFAULT_TERM;
		}
		try {
			genre = URLEncoder.encode( genre, "UTF-8" );
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
		String url = HOST + "/v3/search/anime?genre=" + getGenreCode(genre) + "&limit=" + limit;
		StringBuilder response = getJSONString(url);
		
		try {
			return getItemList( new JSONObject( response.toString() ), limit );
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private int getGenreCode(String genre) {
		return map.get(genre);
	}

	// Convert the entire JSONArray from API to a list of Item object.
	private List<Item> getItemList( JSONObject jsonObj, int searchLimit ) throws JSONException, IOException {
		List<Item> list = new ArrayList<>();
		JSONArray anime = null;
		if( !jsonObj.isNull("anime") ) {
			anime = jsonObj.getJSONArray("anime");
		}
		else if ( !jsonObj.isNull("results") ) {
			anime = jsonObj.getJSONArray("results");
		}
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
			
			list.add( builder.build() );
			
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
		if( !item.isNull( "genre" ) ) {
			JSONArray array = item.getJSONArray( "genre" );
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
		tmpAPI.queryAPI("Infinite", 5);
	}

}
