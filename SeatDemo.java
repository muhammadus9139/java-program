import src.*;
public class SeatDemo{
    public static void main(String[] args) {


        Seat regular = new Seat("1-001", 500, Seat.Seattype.REGULAR, true);
        Seat premium = new Seat("2-005", 750, Seat.Seattype.PREMIUM, true);
        Seat vip = new Seat("3-007", 1000, Seat.Seattype.VIP, true);
        Seat recliner= new Seat("5-010", 1200, Seat.Seattype.RECLINER, true);

        System.out.println();
        System.out.println("Initial seat display");
        System.out.println("Regular seat: " + regular);
        System.out.println("Premium seat: " + premium);
        System.out.println("VIP seat: " + vip);
        System.out.println("Recliner Seat: " + recliner);

        System.out.println();
//        System.out.println("check availability of seats");
//        System.out.println("Regular Seat Available: " + regular.isAvailable());
//        System.out.println("Premium Seat Available: " + premium.isAvailable());
//        System.out.println("VIP Seat Available: " + vip.isAvailable());
//        System.out.println("Recliner Seat Available: " + recliner.isAvailable());

        System.out.println("\nBooking seats");
        regular.setAvailable(false);
        System.out.println("Booked Regular Seat");
        vip.setAvailable(false);
        System.out.println("Booked Vip Seat");

        System.out.println("\nBooked already booked seats show error");
        if (!vip.isAvailable()) {
            System.out.println("VIP seat is already booked");
        }

        System.out.println("\nCancel seat");
        regular.setAvailable(true);
        System.out.println("Cancelled Regular seat");

//        System.out.println("\nCancel available seat");
//        if (premium.isAvailable()) {
//            System.out.println("Premium Seat is not booked yet");
//        }

        System.out.println("\nStatus After Cancellation");
        System.out.println("Regular Seat: " + regular);
        System.out.println("Premium Seat: " + premium);
        System.out.println("VIP Seat: " + vip);
        System.out.println("Recliner Seat: " + recliner);

//        prices
//        System.out.println("\n=== Price Adjustment ===");
//        System.out.println("Original Premium Seat Price: " + premium.getprice() + " PKR");
//        premium.setprice(800.0);
//        System.out.println("Updated Premium Seat Price: " + premium.getprice() + " PKR");
//        System.out.println("Updated Premium Seat: " + premium);


        System.out.println("\n=== Final Seat Summary (using toString) ===");
        System.out.println(regular);
        System.out.println(premium);
        System.out.println(vip);
        System.out.println(recliner);

//        Enum verification
//        System.out.println("Regular Seat Type: " + regular.getseattype());
//        System.out.println("Premium Seat Type: " + premium.getseattype());
//        System.out.println("VIP Seat Type: " + vip.getseattype());
//        System.out.println("Recliner Seat Type: " + recliner.getseattype());


//        System.out.println("Regular Seat Price: " + regular.getprice());
//        System.out.println("Premium Seat Price: " + premium.getprice());
//        System.out.println("VIP Seat Price: " + vip.getprice());
//        System.out.println("Recliner Seat Price: " + recliner.getprice());

//        System.out.println("Regular Seat available: " + regular.isAvailable());
//        System.out.println("Premium Seat available: " + premium.isAvailable());
//        System.out.println("VIP Seat available: " + vip.isAvailable());
//        System.out.println("Recliner Seat available: " + recliner.isAvailable());
    }
}