import java.util.*;
import java.util.concurrent.*;

public class FlashSaleManager {

    private Map<String, Integer> stockMap = new ConcurrentHashMap<>();
    private Map<String, LinkedList<Integer>> waitingListMap = new ConcurrentHashMap<>();

    public void addProduct(String productId, int stock) {
        stockMap.put(productId, stock);
        waitingListMap.put(productId, new LinkedList<>());
    }

    public int checkStock(String productId) {
        return stockMap.getOrDefault(productId, 0);
    }

    public String purchaseItem(String productId, int userId) {
        synchronized (getLock(productId)) {
            int stock = stockMap.getOrDefault(productId, 0);
            if (stock > 0) {
                stockMap.put(productId, stock - 1);
                return "Success, " + (stock - 1) + " units remaining";
            } else {
                waitingListMap.get(productId).add(userId);
                int position = waitingListMap.get(productId).size();
                return "Added to waiting list, position #" + position;
            }
        }
    }

    private Object getLock(String productId) {
        return productId.intern();
    }

    public List<Integer> viewWaitingList(String productId) {
        return new ArrayList<>(waitingListMap.getOrDefault(productId, new LinkedList<>()));
    }

    public static void main(String[] args) {
        FlashSaleManager manager = new FlashSaleManager();
        manager.addProduct("IPHONE15_256GB", 100);

        System.out.println(manager.checkStock("IPHONE15_256GB"));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));

        manager.stockMap.put("IPHONE15_256GB", 0);
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));
    }
}