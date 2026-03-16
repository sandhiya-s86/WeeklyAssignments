import java.util.*;

class ParkingSpot {
    String licensePlate;
    long entryTime;
    String status;

    ParkingSpot() {
        status = "EMPTY";
    }
}

class ParkingLot {

    int SIZE = 500;
    ParkingSpot[] table = new ParkingSpot[SIZE];
    int occupied = 0;
    int totalProbes = 0;
    int operations = 0;

    ParkingLot() {
        for (int i = 0; i < SIZE; i++)
            table[i] = new ParkingSpot();
    }

    int hash(String license) {
        return Math.abs(license.hashCode()) % SIZE;
    }

    void parkVehicle(String license) {

        int index = hash(license);
        int probes = 0;

        while (table[index].status.equals("OCCUPIED")) {
            index = (index + 1) % SIZE;
            probes++;
        }

        table[index].licensePlate = license;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = "OCCUPIED";

        occupied++;
        totalProbes += probes;
        operations++;

        System.out.println("parkVehicle(\"" + license + "\") → Assigned spot #" + index + " (" + probes + " probes)");
    }

    void exitVehicle(String license) {

        int index = hash(license);

        while (!table[index].status.equals("EMPTY")) {

            if (table[index].status.equals("OCCUPIED") && table[index].licensePlate.equals(license)) {

                long exitTime = System.currentTimeMillis();
                long duration = (exitTime - table[index].entryTime) / 1000;

                table[index].status = "DELETED";
                occupied--;

                double fee = duration * 0.05;

                System.out.println("exitVehicle(\"" + license + "\") → Spot #" + index +
                        " freed, Duration: " + duration + " seconds, Fee: $" +
                        String.format("%.2f", fee));
                return;
            }

            index = (index + 1) % SIZE;
        }

        System.out.println("Vehicle not found");
    }

    void getStatistics() {

        double occupancyRate = (occupied * 100.0) / SIZE;
        double avgProbes = operations == 0 ? 0 : (double) totalProbes / operations;

        System.out.println("Occupancy: " + String.format("%.2f", occupancyRate) + "%");
        System.out.println("Avg Probes: " + String.format("%.2f", avgProbes));
    }
}

public class ParkingLotOpenAddressing {

    public static void main(String[] args) {

        ParkingLot lot = new ParkingLot();

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}