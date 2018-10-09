package entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Item {
	
	private int itemId;
	private String name;
	private int type;
	private String summary;
	private int episodes;
	private double rating;
	private String imageUrl;
	private String url;
	
	public JSONObject toJSONObject() {
		
		JSONObject obj = new JSONObject();
		try {
			obj.put( "id", itemId );
			obj.put( "name", name );
			obj.put( "type", type );
			obj.put( "summary", summary );
			obj.put( "eps", episodes );
			obj.put( "rating", rating );
			obj.put( "imageUrl", imageUrl );
			obj.put( "url", url );
		} catch ( JSONException e ) {
			e.printStackTrace();
		}
		
		return obj;
	}

	private Item( ItemBuilder builder ) {
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.type = builder.type;
		this.summary = builder.summary;
		this.episodes = builder.episodes;
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


	public int getType() {
		return type;
	}


	public String getSummary() {
		return summary;
	}


	public int getEpisodes() {
		return episodes;
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
		private int type;
		private String summary;
		private int episodes;
		private double rating;
		private String imageUrl;
		private String url;
		
		public ItemBuilder setItemId( int itemId ) {
			this.itemId = itemId;
			return this;
		}
		
		public ItemBuilder setName( String name ) {
			this.name = name;
			return this;
		}
		
		public ItemBuilder setType( int type ) {
			this.type = type;
			return this;
		}
		
		public ItemBuilder setSummary( String summary ) {
			this.summary = summary;
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
