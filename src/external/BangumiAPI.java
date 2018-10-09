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


public class BangumiAPI {
	private static final String HOST = "https://api.bgm.tv";
	private static final String ENDPOINT = "/calendar";
	private static final String DEFAULT_TERM = "";
	private static final int SEARCH_LIMIT = 20;

	private static final String TOKEN_TYPE = "Bearer";
	private static final String API_KEY = "f30ff8ec0677a6ecd38e0d1a9f00a87041bac1c8";
	
	public List<Item> search() {
		// Old search query, gonna use calendar instead
//		if ( username == null || username.isEmpty() ) {
//			username = DEFAULT_TERM;
//		}
//		try {
//			username = URLEncoder.encode( username, "UTF-8" );
//		}
//		catch ( Exception e) {
//			e.printStackTrace();
//		}
//		
//		if ( cat == null || cat.isEmpty() ) {
//			cat = "watching";
//		}
//		try {
//			cat = URLEncoder.encode( cat, "UTF-8" );
//		}
//		catch ( Exception e) {
//			e.printStackTrace();
//		}
		
//		String query = String.format( "cat=%s", cat );
//		String url = HOST + ENDPOINT + username + "/collection?" + query;
		String url = HOST + ENDPOINT;
		
		try {
			HttpURLConnection connection = ( HttpURLConnection ) new URL(url).openConnection();
			
			connection.setRequestMethod( "GET" );
			connection.setRequestProperty( "Authorization", TOKEN_TYPE + " " + API_KEY );
			connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/69.0.3497.100 Safari/537.36");
			
			int responseCode = connection.getResponseCode();
			
			// for debug
			System.out.println( "Sending Request to URL:" + url );
			System.out.println( "Response Code:" + responseCode );
			
			if ( responseCode != 200 ) {
				return new ArrayList<>();
//				return new JSONArray();
			}
			
			BufferedReader in = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
			String inputLine = "";
			StringBuilder response = new StringBuilder();
			
			while ( ( inputLine = in.readLine() ) != null ) {
				response.append( inputLine );
			}
			in.close();
			
			return getItemList( new JSONArray( response.toString() ) );
//			return new JSONArray( response.toString() );
		}
			
		catch ( Exception e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>();
//		return new JSONArray();
	}
	
	// Convert the entire JSONArray from API to a list of Item object.
	private List<Item> getItemList( JSONArray jsonArray ) throws JSONException, IOException {
		List<Item> list = new ArrayList<>();
		
		Document doc = Jsoup.connect( "http://bangumi.tv/subject/253" ).get();
		Elements a = doc.getElementsByClass("subject_tag_section");
		Elements b = a.select( "a[href]" );
		for ( Element element : b ) {
			Elements span = element.getElementsByTag("span");
			String name = span.text();
			System.out.println( name );
		}
		
		
		for ( int i = 0; i < jsonArray.length(); i++ ) {
			JSONObject day = jsonArray.getJSONObject(i);
			JSONArray items = day.getJSONArray( "items" );
			
			for( int j = 0; j < items.length(); j++ ) {
				
				ItemBuilder builder = new ItemBuilder();
				JSONObject item = items.getJSONObject(j);
				if ( !item.isNull("id") ) {
					builder.setItemId( item.getInt("id") );
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
				builder.setImageUrl( getImageUrl(item) );
				
				list.add( builder.builder() );
			}
			
		}
		
		return list;
	}
	
	// Helper to parse the score from rating Json
	private double getRating( JSONObject item ) throws JSONException {
		double score = 0;
		if ( !item.isNull( "rating" ) ) {
			JSONObject rating = item.getJSONObject( "rating" );
			score = rating.getDouble( "score" );
//			for ( int i = 0; i < array.length(); i++ ) {
//				JSONObject rating = array.getJSONObject(i);
//				if ( !rating.isNull( "score" ) ) {
//					score = rating.getDouble( "score" );
//				}
//			}
		}
		return score;
	}

	// Help to parse the imageUrl from images Json
	private String getImageUrl( JSONObject item ) throws JSONException {
		String image = null;
		if( !item.isNull( "images" ) ) {
			JSONObject images = item.getJSONObject( "images" );
			if( !images.isNull("large") ) {
				image = images.getString( "large" );
			}
		}
		return image;

	}
	
	private void queryAPI() {
		List<Item> itemList = search();
		for( Item item : itemList ) {
			item.toJSONObject();
			JSONObject jsonObject = item.toJSONObject();
			System.out.println( jsonObject );
		}
		// old JSONArray API
//		JSONArray array = search( username, null );
//		for ( int i = 0; i < array.length(); i++ ) {
//			try {
//				JSONObject obj = array.getJSONObject(i);
//				System.out.println( obj );
//			}
//			catch ( JSONException e ) {
//				e.printStackTrace();
//			}
//			
//		}
	}
	
	// Testing
	public static void main( String[] args ) {
		BangumiAPI tmpAPI = new BangumiAPI();
//		tmpAPI.queryAPI("test");
		tmpAPI.queryAPI();
	}
}
