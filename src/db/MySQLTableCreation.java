package db;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;

public class MySQLTableCreation {
	// Run as Java application to reset db schema.
		public static void main(String[] args) {
			clear();
		}
		
		public static void clear() {
			try {
				// Connect to MySQL
				System.out.println("Connecting to " + MySQLDBUtil.URL);
				Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
				Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);
				
				if (conn == null) {
					return;
				}
				
				// Drop old tables in case they exist.
				Statement stmt = conn.createStatement();
				String sql = "DROP TABLE IF EXISTS favorite";
				stmt.executeUpdate(sql);

				sql = "DROP TABLE IF EXISTS tags";
				stmt.executeUpdate(sql);

				sql = "DROP TABLE IF EXISTS users";
				stmt.executeUpdate(sql);

				sql = "DROP TABLE IF EXISTS items";
				stmt.executeUpdate(sql);
				
				// Create new tables
				sql = "CREATE TABLE items ("
						+ "item_id INT NOT NULL,"
						+ "name VARCHAR(255),"
						+ "rating FLOAT,"
						+ "user_score FLOAT,"
						+ "summary VARCHAR(255),"
						+ "url VARCHAR(255),"
						+ "image_url VARCHAR(255),"
						+ "episodes INT,"
						+ "air_date VARCHAR(255),"
						+ "PRIMARY KEY (item_id)"
						+ ")";
				stmt.executeUpdate(sql);

				sql = "CREATE TABLE users ("
						+ "user_id VARCHAR(255) NOT NULL,"
						+ "password VARCHAR(255) NOT NULL,"
						+ "first_name VARCHAR(255),"
						+ "last_name VARCHAR(255),"
						+ "PRIMARY KEY (user_id)"
						+ ")";
				stmt.executeUpdate(sql);

				sql = "CREATE TABLE tags ("
						+ "item_id INT NOT NULL,"
						+ "tag VARCHAR(255) NOT NULL,"
						+ "PRIMARY KEY (item_id, tag),"
						+ "FOREIGN KEY (item_id) REFERENCES items(item_id)"
						+ ")";
				stmt.executeUpdate(sql);

				sql = "CREATE TABLE favorite ("
						+ "user_id VARCHAR(255) NOT NULL,"
						+ "item_id INT NOT NULL,"
						+ "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
						+ "PRIMARY KEY (user_id, item_id),"
						+ "FOREIGN KEY (item_id) REFERENCES items(item_id),"
						+ "FOREIGN KEY (user_id) REFERENCES users(user_id)"
						+ ")";
				stmt.executeUpdate(sql);
				
				// Create a fake user 101/fakeuser 9ae9a051134d57f067642f8ec438f3ad
				sql = "INSERT INTO users VALUES ('101', '9ae9a051134d57f067642f8ec438f3ad', 'JB', 'HiFi')";
				stmt.executeUpdate(sql);

				
				System.out.println("Import done successfully");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}
