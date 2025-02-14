import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;

public class App {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";

    private static final String username = "root";

    private static final String password = "SQL@0718";

    public static void reserveRoom(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter guest name: ");
            String guestName = sc.next();
            sc.nextLine();
            System.out.println("Enter room number: ");
            int roomNumber = sc.nextInt();
            System.out.println("Enter contact number:");
            String contactNumber = sc.next();

            String sql = "INSERT INTO reservations(guest_name, room_number, contact_number)" + "VALUES('" + guestName
                    + "', " + roomNumber + ", '" + contactNumber + "')";

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation Successful");
                } else {
                    System.out.println("Reservation Failed");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewReservation(Connection connection) throws SQLException {
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

        try (Statement statement = connection.createStatement();

                ResultSet resultset = statement.executeQuery(sql)) {

            System.out.println("Current Reservations: ");
            System.out.println(
                    "+------------------+-------------------+-----------------+---------------------+----------------------+");
            System.out.println(
                    "| Reservation Id   |    Guest          |     Room Number |    Contact Number   |   Reservation Date   |");
            System.out.println(
                    "+------------------+-------------------+-----------------+---------------------+----------------------+");

            while (resultset.next()) {
                int reservationId = resultset.getInt("reservation_id");
                String guestName = resultset.getString("guest_name");
                int roomNumber = resultset.getInt("room_number");
                String contactNumber = resultset.getString("contact_number");
                String reservationDate = resultset.getTimestamp("reservation_date").toString();

                // Format and Display the reservation data in a table-like format

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s  | \n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println(
                    "+------------------+-------------------+-----------------+---------------------+----------------------+");

        }
    }

    public static void getRoomNumber(Connection connection, Scanner sc) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = sc.nextInt();
            sc.nextLine(); // Consume the newline left by nextInt()

            System.out.print("Enter guest name: ");
            String guestName = sc.nextLine(); // Use nextLine() to capture full name

            String sql = "SELECT room_number FROM reservations WHERE reservation_id = ? AND guest_name = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, reservationId);
                pstmt.setString(2, guestName);

                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        int roomNumber = resultSet.getInt("room_number");
                        System.out.println("Room number for Reservation ID " + reservationId +
                                " and Guest " + guestName + " is: " + roomNumber);
                    } else {
                        System.out.println("Reservation not found for the given ID and guest name.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateReservation(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter reservation Id to update: ");
            int reservationId = sc.nextInt();
            sc.nextLine();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.println("Enter new guest name: ");
            String newGuestName = sc.next();
            System.out.println("Enter the room number: ");
            int newRoomNumber = sc.nextInt();
            System.out.println("Enter new Contact number");
            String newContactNumber = sc.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully");
                } else {
                    System.out.println("Reservation update failed");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteReservation(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter reservation ID to delete: ");
            int reservationId = sc.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted Successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                    ResultSet resultset = statement.executeQuery(sql)) {
                return resultset.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while (i != 0) {
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }

        System.out.println();
        System.out.println("Thank you, For using Hotel Reservation System!!");
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);

            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM....");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.println("Choose an Option: ");

                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        reserveRoom(connection, sc);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, sc);
                        break;
                    case 4:
                        updateReservation(connection, sc);
                        break;
                    case 5:
                        deleteReservation(connection, sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
