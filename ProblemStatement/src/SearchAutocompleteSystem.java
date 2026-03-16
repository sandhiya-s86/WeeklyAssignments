import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEnd = false;
    int frequency = 0;
}

class AutocompleteSystem {

    TrieNode root = new TrieNode();
    HashMap<String, Integer> queryFrequency = new HashMap<>();

    public void insert(String query, int freq) {

        queryFrequency.put(query, queryFrequency.getOrDefault(query, 0) + freq);

        TrieNode node = root;

        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isEnd = true;
        node.frequency = queryFrequency.get(query);
    }

    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c))
                return new ArrayList<>();

            node = node.children.get(c);
        }

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> a.getValue() - b.getValue());

        dfs(node, prefix, pq);

        List<String> result = new ArrayList<>();

        while (!pq.isEmpty())
            result.add(0, pq.poll().getKey());

        return result;
    }

    void dfs(TrieNode node, String prefix,
             PriorityQueue<Map.Entry<String, Integer>> pq) {

        if (node.isEnd) {

            pq.offer(new AbstractMap.SimpleEntry<>(prefix, node.frequency));

            if (pq.size() > 10)
                pq.poll();
        }

        for (char c : node.children.keySet()) {
            dfs(node.children.get(c), prefix + c, pq);
        }
    }

    public void updateFrequency(String query) {
        insert(query, 1);
    }
}

public class SearchAutocompleteSystem {

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.insert("java tutorial", 1234567);
        system.insert("javascript", 987654);
        system.insert("java download", 456789);
        system.insert("java 21 features", 10);
        system.insert("java stream api", 50000);

        List<String> results = system.search("jav");

        System.out.println("Suggestions:");

        int rank = 1;

        for (String r : results) {
            System.out.println(rank + ". " + r);
            rank++;
        }

        system.updateFrequency("java 21 features");
    }
}