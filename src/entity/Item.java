package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Item {
	
	private int itemId;
	private String name;
	private String summary;
	private int episodes;
	private double rating;
	private double userScore;
	private String imageUrl;
	private String url;
	private String date;
	private Set<String> genres;
	
	public JSONObject toJSONObject() {
		
		JSONObject obj = new JSONObject();
		try {
			obj.put( "id", itemId );
			obj.put( "name", name );
			obj.put( "summary", summary );
			obj.put( "eps", episodes );
			obj.put( "userScore", userScore );
			obj.put( "rating", rating );
			obj.put( "imageUrl", imageUrl );
			obj.put( "url", url );
			obj.put( "date", date );
			obj.put( "genres", new JSONArray(genres) );
		} catch ( JSONException e ) {
			e.printStackTrace();
		}
		
		return obj;
	}

	private Item( ItemBuilder builder ) {
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.date = builder.date;
		this.summary = builder.summary;
		this.episodes = builder.episodes;
		this.genres = builder.genres;
		this.userScore = builder.userScore;
		this.rating = builder.rating;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
	}

	public int getItemId() {
		return itemId;
	}


	public String getName() {
		return name;
	}


	public double getUserScore() {
		return userScore;
	}

	public String getDate() {
		return date;
	}

	public String getSummary() {
		return summary;
	}


	public int getEpisodes() {
		return episodes;
	}

	public Set<String> getGenres() {
		return genres;
	}
	
	public double getRating() {
		return rating;
	}


	public String getImageUrl() {
		return imageUrl;
	}


	public String getUrl() {
		return url;
	}
	
	public static class ItemBuilder {
		private int itemId;
		private String name;
		private String summary;
		private int episodes;
		private double rating;
		private double userScore;
		private String imageUrl;
		private String url;
		private String date;
		private Set<String> genres;
		
		public ItemBuilder setItemId( int itemId ) {
			this.itemId = itemId;
			return this;
		}
		
		public ItemBuilder setName( String name ) {
			this.name = name;
			return this;
		}
		
		public ItemBuilder setUserScore( double userScore ) {
			this.userScore = userScore;
			return this;
		}
		
		public ItemBuilder setSummary( String summary ) {
			this.summary = summary;
			return this;
		}
		
		public ItemBuilder setDate( String date ) {
			this.date = date;
			return this;
		}
		
		public ItemBuilder setGenres( Set<String> genres ) {
			this.genres = genres;
			return this;
		}
		
		public ItemBuilder setEpisodes( int episodes ) {
			this.episodes = episodes;
			return this;
		}
		
		public ItemBuilder setRating( double rating ) {
			this.rating = rating;
			return this;
		}
		
		public ItemBuilder setImageUrl( String imageUrl ) {
			this.imageUrl = imageUrl;
			return this;
		}
		
		public ItemBuilder setUrl( String url ) {
			this.url = url;
			return this;
		}
		
		public Item builder() {
			return new Item( this );
		}
		
	}

}
