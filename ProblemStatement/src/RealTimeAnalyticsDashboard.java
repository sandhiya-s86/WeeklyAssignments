import java.util.*;

class Event {
    String url;
    String userId;
    String source;

    Event(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

class AnalyticsDashboard {

    HashMap<String, Integer> pageViews = new HashMap<>();
    HashMap<String, HashSet<String>> uniqueVisitors = new HashMap<>();
    HashMap<String, Integer> trafficSourceCount = new HashMap<>();

    public void processEvent(Event e) {
        pageViews.put(e.url, pageViews.getOrDefault(e.url, 0) + 1);

        uniqueVisitors.putIfAbsent(e.url, new HashSet<>());
        uniqueVisitors.get(e.url).add(e.userId);

        trafficSourceCount.put(e.source, trafficSourceCount.getOrDefault(e.source, 0) + 1);
    }

    public List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        List<Map.Entry<String, Integer>> result = new ArrayList<>();

        int count = 0;

        while (!pq.isEmpty() && count < 10) {
            result.add(pq.poll());
            count++;
        }

        return result;
    }

    public void getDashboard() {

        System.out.println("\nTop Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;

        for (Map.Entry<String, Integer> page : topPages) {
            System.out.println(rank + ". " + page.getKey() + " - "
                    + page.getValue() + " views ("
                    + uniqueVisitors.get(page.getKey()).size() + " unique)");
            rank++;
        }

        System.out.println("\nTraffic Sources:");

        int total = 0;

        for (int count : trafficSourceCount.values())
            total += count;

        for (Map.Entry<String, Integer> source : trafficSourceCount.entrySet()) {

            double percent = (source.getValue() * 100.0) / total;

            System.out.println(source.getKey() + " : " + String.format("%.2f", percent) + "%");
        }
    }
}

public class RealTimeAnalyticsDashboard {

    public static void main(String[] args) {

        AnalyticsDashboard dashboard = new AnalyticsDashboard();

        dashboard.processEvent(new Event("/article/breaking-news", "user_123", "Google"));
        dashboard.processEvent(new Event("/article/breaking-news", "user_456", "Facebook"));
        dashboard.processEvent(new Event("/sports/championship", "user_789", "Direct"));
        dashboard.processEvent(new Event("/article/breaking-news", "user_123", "Google"));
        dashboard.processEvent(new Event("/sports/championship", "user_555", "Google"));
        dashboard.processEvent(new Event("/tech/ai-news", "user_777", "Direct"));
        dashboard.processEvent(new Event("/tech/ai-news", "user_888", "Google"));

        dashboard.getDashboard();
    }
}