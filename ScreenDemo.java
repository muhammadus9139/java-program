import src.*;
public class ScreenDemo{
    public static void main(String[] args){

        Screen screen1 = new Screen("Hall-1");
        System.out.println("Initial Screen Layout");
        screen1.displayLayout();
        System.out.println();

        System.out.println("Creating Default Screen");
        System.out.println(screen1);
        System.out.println();

        System.out.println("Seats info all");
        System.out.println("Total Seats: " + screen1.getTotalSeatCount());
        System.out.println("Available Seats: " + screen1.getAvailableSeatCount());
        System.out.println("REGULAR Seats: " + screen1.getTotalSeatsByType(Seat.Seattype.REGULAR));
        System.out.println("PREMIUM Seats: " + screen1.getTotalSeatsByType(Seat.Seattype.PREMIUM));
        System.out.println("VIP Seats: " + screen1.getTotalSeatsByType(Seat.Seattype.VIP));
        System.out.println("RECLINER Seats: " + screen1.getTotalSeatsByType(Seat.Seattype.RECLINER));
        System.out.println();

        System.out.println("Book seat by id");
        boolean book1 = screen1.book("1-001");
        System.out.println("Booking seat 1-001: " + (book1 ? "booked" : "FAILED"));

        boolean book2 = screen1.book("3-007");
        System.out.println("Booking seat 3-007: " + (book2 ? "booked" : "FAILED"));

        boolean book3 = screen1.book("4-010");
        System.out.println("Booking seat 4-010: " + (book3 ? "booked" : "FAILED"));



//        System.out.println("double booking error");
//        boolean doubleBook = screen1.book("3-007");
//        System.out.println("Booking seat " + (doubleBook ? "booked" : "FAILED"));
//        System.out.println();

        System.out.println("Print after booking");
        screen1.displayLayout();
        System.out.println();

//        System.out.println("Booking seat by row col");
//        boolean book5 = screen1.book(1, 5); // Row 2, Col 6
//        System.out.println("Booking seat " + (book5 ? "booked" : "FAILED"));
//        System.out.println();

//        System.out.println("Find seat");
//        Seat foundSeat1 = screen1.getSeat("1-001");
//        if (foundSeat1 != null) {
//            System.out.println("Found seat by id" + foundSeat1);
//        }

//        Seat foundSeat2 = screen1.getSeat(2, 6);
//        if (foundSeat2 != null) {
//            System.out.println("Found seat by row col " + foundSeat2);
//        }
//        System.out.println();


        System.out.println("Cancel booking");
        boolean cancel1 = screen1.cancel("3-007");
        System.out.println("Cancelling seat " + (cancel1 ? "Success" : "FAILED"));

        boolean cancel2 = screen1.cancel("1-005"); // Not booked
        System.out.println("Cancelling seat " + (cancel2 ? "Success" : "FAILED"));
        System.out.println();

        System.out.println("Layout After Cancel");
        screen1.displayLayout();
        System.out.println();

    }

}

