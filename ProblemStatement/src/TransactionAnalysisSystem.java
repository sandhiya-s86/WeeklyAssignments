import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    int time;

    Transaction(int id, int amount, String merchant, String account, int time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

class TransactionAnalyzer {

    List<Transaction> transactions = new ArrayList<>();

    void addTransaction(Transaction t) {
        transactions.add(t);
    }

    void findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction prev = map.get(complement);

                System.out.println("TwoSum Pair → (" + prev.id + ", " + t.id + ")");
            }

            map.put(t.amount, t);
        }
    }

    void findTwoSumTimeWindow(int target, int window) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction prev = map.get(complement);

                if (Math.abs(t.time - prev.time) <= window) {
                    System.out.println("TwoSum within time window → (" + prev.id + ", " + t.id + ")");
                }
            }

            map.put(t.amount, t);
        }
    }

    void detectDuplicates() {

        HashMap<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        for (String key : map.keySet()) {

            List<Transaction> list = map.get(key);

            if (list.size() > 1) {

                System.out.print("Duplicate Transactions → ");

                for (Transaction t : list)
                    System.out.print("ID:" + t.id + " ");

                System.out.println();
            }
        }
    }

    void findKSum(int k, int target) {
        kSumHelper(0, k, target, new ArrayList<>());
    }

    void kSumHelper(int start, int k, int target, List<Transaction> path) {

        if (k == 0 && target == 0) {

            System.out.print("K-Sum → ");

            for (Transaction t : path)
                System.out.print("ID:" + t.id + " ");

            System.out.println();
            return;
        }

        if (k == 0 || start >= transactions.size())
            return;

        for (int i = start; i < transactions.size(); i++) {

            Transaction t = transactions.get(i);

            path.add(t);

            kSumHelper(i + 1, k - 1, target - t.amount, path);

            path.remove(path.size() - 1);
        }
    }
}

public class TransactionAnalysisSystem {

    public static void main(String[] args) {

        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        analyzer.addTransaction(new Transaction(1, 500, "StoreA", "acc1", 1000));
        analyzer.addTransaction(new Transaction(2, 300, "StoreB", "acc2", 1015));
        analyzer.addTransaction(new Transaction(3, 200, "StoreC", "acc3", 1030));
        analyzer.addTransaction(new Transaction(4, 500, "StoreA", "acc4", 1040));

        analyzer.findTwoSum(500);

        analyzer.findTwoSumTimeWindow(500, 60);

        analyzer.detectDuplicates();

        analyzer.findKSum(3, 1000);
    }
}