import java.util.HashMap;

class TokenBucket {

    int tokens;
    int maxTokens;
    long lastRefillTime;
    int refillRate;

    TokenBucket(int maxTokens, int refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    synchronized boolean allowRequest() {

        refillTokens();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    void refillTokens() {

        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lastRefillTime;

        int tokensToAdd = (int) (timePassed / 3600_000.0 * refillRate);

        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + tokensToAdd);
            lastRefillTime = currentTime;
        }
    }

    int getRemainingTokens() {
        return tokens;
    }
}

class RateLimiter {

    HashMap<String, TokenBucket> clients = new HashMap<>();

    int maxRequests = 1000;
    int refillRate = 1000;

    public void checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId, new TokenBucket(maxRequests, refillRate));

        TokenBucket bucket = clients.get(clientId);

        boolean allowed = bucket.allowRequest();

        if (allowed) {
            System.out.println("Allowed (" + bucket.getRemainingTokens() + " requests remaining)");
        } else {
            System.out.println("Denied (0 requests remaining, retry later)");
        }
    }

    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            System.out.println("Client not found");
            return;
        }

        int used = bucket.maxTokens - bucket.tokens;

        System.out.println("{used: " + used + ", limit: " + bucket.maxTokens + "}");
    }
}

public class DistributedRateLimiter {

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        limiter.checkRateLimit("abc123");
        limiter.checkRateLimit("abc123");
        limiter.checkRateLimit("abc123");

        limiter.getRateLimitStatus("abc123");
    }
}