package rpc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import algorithm.Recommendation;
import entity.Item;

/**
 * Servlet implementation class RecommendationItem
 */
@WebServlet("/recommendation")
public class RecommendationItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendationItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("user_id");
		String limitTerm = request.getParameter("limit");
		int limit = (limitTerm == null) ? 0 : Integer.parseInt(limitTerm);
		Recommendation recommendation = new Recommendation();
		List<Item> items = recommendation.recommendItems(userId, limit);

		JSONArray result = new JSONArray();
		try {
			for (Item item : items) {
				result.put(item.toJSONObject());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		RpcHelper.writeJSONArray(response, result);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
