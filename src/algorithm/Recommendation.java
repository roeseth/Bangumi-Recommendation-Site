package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.MySQLConnection;
import entity.Item;

public class Recommendation {
	public List<Item> recommendItems(String userId, int limit) {
		List<Item> recommendedItems = new ArrayList<>();
		
		// get all favorited itemids
		MySQLConnection connection = new MySQLConnection();
		Set<Integer> favoritedItemIds = connection.getFavoriteItemIds(userId);
		
		// get all genres, sort by count
		Map<String, Integer> allGenres = new HashMap<>();
		for (int itemId : favoritedItemIds) {
			Set<String> genres = connection.getGenres(itemId);
			for (String genre : genres) {
				allGenres.put(genre, allGenres.getOrDefault(genre, 0) + 1);
			}
		}		
		
		List<Entry<String, Integer>> genreList = new ArrayList<>(allGenres.entrySet());
		Collections.sort(genreList, (Entry<String, Integer> o1, Entry<String, Integer> o2) -> {
			return Integer.compare(o2.getValue(), o1.getValue());
		});
		
		// Step 3, search based on category, filter out favorite items
		Set<Item> visitedItems = new HashSet<>();
		for (Entry<String, Integer> genre : genreList) {
			List<Item> items = connection.searchItemsbyGenre(genre.getKey(), limit);
			List<Item> filteredItems = new ArrayList<>();
			
			for (Item item : items) {
				if (!favoritedItemIds.contains(item.getItemId()) && !visitedItems.contains(item)) {
					filteredItems.add(item);
				}
			}
			
			visitedItems.addAll(filteredItems);
			recommendedItems.addAll(filteredItems);
		}
		
		return recommendedItems;
  }

}
