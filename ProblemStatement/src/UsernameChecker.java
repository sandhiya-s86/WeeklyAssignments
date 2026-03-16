import java.util.*;

public class UsernameChecker {

    HashMap<String, Integer> usernames = new HashMap<>();
    HashMap<String, Integer> attempts = new HashMap<>();

    public boolean checkAvailability(String username) {
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);
        return !usernames.containsKey(username);
    }

    public void register(String username, int userId) {
        usernames.put(username, userId);
    }

    public static void main(String[] args) {

        UsernameChecker u = new UsernameChecker();

        u.register("john_doe", 1);

        System.out.println(u.checkAvailability("john_doe"));
    }
}