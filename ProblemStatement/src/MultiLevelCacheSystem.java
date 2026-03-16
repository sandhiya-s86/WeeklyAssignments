import java.util.*;

class VideoData {
    String videoId;
    String content;

    VideoData(String id, String content) {
        this.videoId = id;
        this.content = content;
    }
}

class LRUCache<K,V> extends LinkedHashMap<K,V> {
    private int capacity;

    LRUCache(int capacity) {
        super(capacity,0.75f,true);
        this.capacity = capacity;
    }

    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > capacity;
    }
}

class MultiLevelCache {

    LRUCache<String,VideoData> L1;
    LRUCache<String,VideoData> L2;
    HashMap<String,VideoData> L3;

    int L1Hits=0,L2Hits=0,L3Hits=0;

    MultiLevelCache() {
        L1 = new LRUCache<>(10000);
        L2 = new LRUCache<>(100000);
        L3 = new HashMap<>();
    }

    void addVideoToDatabase(String id,String content) {
        L3.put(id,new VideoData(id,content));
    }

    VideoData getVideo(String id) {

        if(L1.containsKey(id)) {
            L1Hits++;
            System.out.println("L1 Cache HIT");
            return L1.get(id);
        }

        if(L2.containsKey(id)) {
            L2Hits++;
            System.out.println("L2 Cache HIT → Promoted to L1");
            VideoData v = L2.get(id);
            L1.put(id,v);
            return v;
        }

        if(L3.containsKey(id)) {
            L3Hits++;
            System.out.println("L3 Database HIT → Added to L2");
            VideoData v = L3.get(id);
            L2.put(id,v);
            return v;
        }

        System.out.println("Video not found");
        return null;
    }

    void updateVideo(String id,String newContent) {
        VideoData v = new VideoData(id,newContent);
        L3.put(id,v);
        L1.remove(id);
        L2.remove(id);
        System.out.println("Cache invalidated for "+id);
    }

    void getStatistics() {

        int total = L1Hits + L2Hits + L3Hits;

        if(total==0) total=1;

        System.out.println("\nCache Statistics");

        System.out.println("L1 Hits: "+L1Hits+" ("+(L1Hits*100.0/total)+"%)");
        System.out.println("L2 Hits: "+L2Hits+" ("+(L2Hits*100.0/total)+"%)");
        System.out.println("L3 Hits: "+L3Hits+" ("+(L3Hits*100.0/total)+"%)");

        double overall = ((L1Hits+L2Hits)*100.0)/total;

        System.out.println("Overall Cache Hit Rate: "+overall+"%");
    }
}

public class MultiLevelCacheSystem {

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        cache.addVideoToDatabase("video_123","Movie A");
        cache.addVideoToDatabase("video_456","Movie B");
        cache.addVideoToDatabase("video_999","Movie C");

        cache.getVideo("video_123");
        cache.getVideo("video_123");
        cache.getVideo("video_999");
        cache.getVideo("video_456");

        cache.updateVideo("video_123","Updated Movie A");

        cache.getStatistics();
    }
}