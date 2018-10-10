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
			String sql = "INSERT IGNORE INTO history(user_id, item_id) VALUES (?, ?)";
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
	   		 String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
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

	public Set<String> getFavoriteItemIds(String userId) {
		return null;
	}

	public Set<Item> getFavoriteItems(String userId) {
		return null;
	}

	public Set<String> getCategories(String itemId) {
		return null;
	}

	public List<Item> searchItems( String username, int limit ) {
		JikanAPI api = new JikanAPI();
		List<Item> items = api.search(username, limit);
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

			sql = "INSERT IGNORE INTO categories VALUES(?, ?)";
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

