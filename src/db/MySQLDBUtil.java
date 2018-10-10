package db;

public class MySQLDBUtil {
	private static final String HOSTNAME = "localhost";
	private static final String PORT_NUM = "3306";// local mysql port number
	public static final String DB_NAME = "mal_project";
	public static final String USERNAME = "root";
	public static final String PASSWORD = "???????";
	public static final String URL = "jdbc:mysql://" + HOSTNAME + ":" + PORT_NUM + "/" + DB_NAME + "?user=" + USERNAME
			+ "&password=" + PASSWORD + "&autoreconnect=true&serverTimezone=UTC";
}
