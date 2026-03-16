import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    DNSEntry(String domain, String ipAddress, long ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class DNSCache {
    private final int capacity;
    private Map<String, DNSEntry> cache = new HashMap<>();
    private LinkedHashMap<String, Long> accessOrder = new LinkedHashMap<>();
    private long hits = 0, misses = 0;

    DNSCache(int capacity) {
        this.capacity = capacity;
    }

    public String resolve(String domain) {
        DNSEntry entry = cache.get(domain);
        if (entry != null && !entry.isExpired()) {
            hits++;
            accessOrder.remove(domain);
            accessOrder.put(domain, System.currentTimeMillis());
            return "Cache HIT → " + entry.ipAddress;
        }

        misses++;
        String ip = queryUpstream(domain);
        DNSEntry newEntry = new DNSEntry(domain, ip, 300);
        if (cache.size() >= capacity) evictLRU();
        cache.put(domain, newEntry);
        accessOrder.put(domain, System.currentTimeMillis());
        return "Cache MISS → Query upstream → " + ip;
    }

    private void evictLRU() {
        String oldestKey = accessOrder.keySet().iterator().next();
        accessOrder.remove(oldestKey);
        cache.remove(oldestKey);
    }

    private String queryUpstream(String domain) {
        return "172.217.14." + new Random().nextInt(255);
    }

    public String getCacheStats() {
        long total = hits + misses;
        double hitRate = total == 0 ? 0 : ((double) hits / total) * 100;
        return "Hit Rate: " + String.format("%.1f", hitRate) + "%, Lookups: " + total;
    }
}

public class DNSCacheTest {
    public static void main(String[] args) throws InterruptedException {
        DNSCache dnsCache = new DNSCache(5);
        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.resolve("google.com"));
        Thread.sleep(310_000);
        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.getCacheStats());
    }
}