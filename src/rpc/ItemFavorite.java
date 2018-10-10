package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import db.MySQLConnection;
import entity.Item;

/**
 * Servlet implementation class ItemFavorite
 */
@WebServlet( "/favorite" )
public class ItemFavorite extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemFavorite() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet( HttpServletRequest request, HttpServletResponse response )
	 */
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {		
		MySQLConnection connection = new MySQLConnection();
		try {
			String userId = request.getParameter("user_id");
			JSONArray array = new JSONArray();
			
			Set<Item> favoriteItems = connection.getFavoriteItems(userId);
			
			for (Item item : favoriteItems) {
				JSONObject obj = item.toJSONObject();
				obj.put("favorite", true);
				array.put(obj);
			}
			RpcHelper.writeJSONArray(response, array);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}	


	}

	/**
	 * @see HttpServlet#doPost( HttpServletRequest request, HttpServletResponse response )
	 */
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		MySQLConnection connection = new MySQLConnection();
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			String userId = input.getString("user_id");
			JSONArray array = input.getJSONArray("favorite");
			List<Integer> itemIds = new ArrayList<>();
			for (int i = 0; i < array.length(); ++i) {
				itemIds.add(array.getInt(i));
			}
			connection.setFavoriteItems(userId, itemIds);
			RpcHelper.writeJSONObject(response, new JSONObject().put("result", "SUCCESS"));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}

	/**
	 * @see HttpServlet#doDelete( HttpServletRequest, HttpServletResponse )
	 */
	protected void doDelete( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		MySQLConnection connection = new MySQLConnection();
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			String userId = input.getString("user_id");
			JSONArray array = input.getJSONArray("favorite");
			List<Integer> itemIds = new ArrayList<>();
			for (int i = 0; i < array.length(); ++i) {
				itemIds.add(array.getInt(i));
			}
			connection.unsetFavoriteItems(userId, itemIds);
			RpcHelper.writeJSONObject(response, new JSONObject().put("result", "SUCCESS"));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}

}
