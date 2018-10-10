package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import entity.Item;
import entity.Item.ItemBuilder;
import external.JikanAPI;


public class MySQLConnection {
	private Connection conn;

	// constructor
	public MySQLConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setFavoriteItems(String userId, List<Integer> itemIds) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}

		try {
			String sql = "INSERT IGNORE INTO favorite(user_id, item_id) VALUES (?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			for (int itemId : itemIds) {
				ps.setInt(2, itemId);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void unsetFavoriteItems(String userId, List<Integer> itemIds) {
		 if (conn == null) {
	   		 System.err.println("DB connection failed");
	   		 return;
	   	       }
	   	 
	   	      try {
	   		 String sql = "DELETE FROM favorite WHERE user_id = ? AND item_id = ?";
	   		 PreparedStatement ps = conn.prepareStatement(sql);
	   		 ps.setString(1, userId);
	   		 for (int itemId : itemIds) {
	   			 ps.setInt(2, itemId);
	   			 ps.execute();
	   		 }
	   		 
	   	       } catch (Exception e) {
	   		 e.printStackTrace();
	   	       }

	}

	public Set<Integer> getFavoriteItemIds(String userId) {
		if (conn == null) {
			return new HashSet<>();
		}
		Set<Integer> favoriteItemIds = new HashSet<>();
		String sql = "SELECT item_id FROM favorite WHERE user_id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				favoriteItemIds.add(rs.getInt("item_id"));
			}		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return favoriteItemIds;

	}

	public Set<Item> getFavoriteItems(String userId) {
		if (conn == null) {
			return new HashSet<>();
		}
		Set<Item> favoriteItems = new HashSet<>();
		Set<Integer> itemIds = getFavoriteItemIds(userId);
		
		String sql = "SELECT * FROM items WHERE item_id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			for (int itemId : itemIds) {
				ps.setInt(1, itemId);
				ResultSet rs = ps.executeQuery();
				
				ItemBuilder builder = new ItemBuilder();
				while (rs.next()) {
					builder.setItemId(rs.getInt("item_id"));
					builder.setName(rs.getString("name"));
					builder.setSummary(rs.getString("summary"));
					builder.setEpisodes(rs.getInt("episodes"));
					builder.setUserScore(rs.getDouble("user_score"));
					builder.setImageUrl(rs.getString("image_url"));
					builder.setUrl(rs.getString("url"));
					builder.setRating(rs.getDouble("rating"));
					builder.setDate(rs.getString("air_date"));
					builder.setGenres(getGenres(itemId));
					
					favoriteItems.add(builder.build());
				}
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return favoriteItems;
	}

	public Set<String> getGenres(int itemId) {
		if (conn == null) {
			return null;
		}
		Set<String> tags = new HashSet<>();
		String sql = "SELECT tag FROM tags WHERE item_id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, itemId);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				tags.add(rs.getString("tag"));
			}		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return tags;
	}

	public List<Item> searchItems( String username, int limit ) {
		JikanAPI api = new JikanAPI();
		List<Item> items = api.search(username, limit);
		for (Item item : items) {
			saveItem(item);
		}
		return items;
	}
	
	public List<Item> searchItemsbyGenre( String genre, int limit ) {
		JikanAPI api = new JikanAPI();
		List<Item> items = api.searchByGenre(genre, limit);
		for (Item item : items) {
			saveItem(item);
		}
		return items;
	}

	public void saveItem(Item item) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}

		try {
			String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, item.getItemId());
			ps.setNString(2, item.getName());
			ps.setDouble(3, item.getRating());
			ps.setDouble(4, item.getUserScore());
			ps.setString(5, item.getSummary());
			ps.setString(6, item.getUrl());
			ps.setString(7, item.getImageUrl());
			ps.setInt(8, item.getEpisodes());
			ps.setString(9, item.getDate());
			ps.execute();

			sql = "INSERT IGNORE INTO tags VALUES(?, ?)";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, item.getItemId());
			for (String genre : item.getGenres()) {
				ps.setString(2, genre);
				ps.execute();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

