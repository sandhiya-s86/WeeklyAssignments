import java.util.*;

class PlagiarismDetector {

    private Map<String, Set<String>> nGramIndex = new HashMap<>();
    private int N = 5; // 5-grams

    public List<String> extractNGrams(String text) {
        String[] words = text.split("\\s+");
        List<String> ngrams = new ArrayList<>();
        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < N; j++) sb.append(words[i + j]).append(" ");
            ngrams.add(sb.toString().trim());
        }
        return ngrams;
    }

    public void indexDocument(String docId, String text) {
        List<String> ngrams = extractNGrams(text);
        for (String ngram : ngrams) {
            nGramIndex.computeIfAbsent(ngram, k -> new HashSet<>()).add(docId);
        }
    }

    public Map<String, Integer> analyzeDocument(String docId, String text) {
        List<String> ngrams = extractNGrams(text);
        Map<String, Integer> matches = new HashMap<>();
        for (String ngram : ngrams) {
            Set<String> docs = nGramIndex.get(ngram);
            if (docs != null) {
                for (String d : docs) {
                    if (!d.equals(docId)) matches.put(d, matches.getOrDefault(d, 0) + 1);
                }
            }
        }
        return matches;
    }

    public void reportSimilarity(String docId, String text) {
        Map<String, Integer> matches = analyzeDocument(docId, text);
        int totalNgrams = extractNGrams(text).size();
        for (String otherDoc : matches.keySet()) {
            int matched = matches.get(otherDoc);
            double similarity = 100.0 * matched / totalNgrams;
            System.out.println("Found " + matched + " matching n-grams with \"" + otherDoc + "\"");
            System.out.println("Similarity: " + String.format("%.1f", similarity) + "% " +
                    (similarity > 50 ? "(PLAGIARISM DETECTED)" : "(suspicious)"));
        }
    }

    public static void main(String[] args) {
        PlagiarismDetector detector = new PlagiarismDetector();

        String doc1 = "This is a sample essay written by a student for testing plagiarism detection system.";
        String doc2 = "This is a sample essay created by another student for plagiarism detection testing purposes.";
        String doc3 = "Completely different content with no similarity to previous essays.";

        detector.indexDocument("essay_089.txt", doc1);
        detector.indexDocument("essay_092.txt", doc2);
        detector.indexDocument("essay_100.txt", doc3);

        System.out.println("Analyzing new submission essay_123.txt:\n");
        String newDoc = "This is a sample essay created by a student for plagiarism detection system testing.";
        detector.reportSimilarity("essay_123.txt", newDoc);
    }
}