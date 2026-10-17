/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Elham
 */
public class bookingdb {
    private int bookingID;
    private int userID;
    private int showtimeId;
    private int seats;
    private String bookingDate;

    // Constructor
    public bookingdb(int bookingID, int userID, int showtimeId, int seats, String bookingDate) {
        this.bookingID = bookingID;
        this.userID = userID;
        this.showtimeId = showtimeId;
        this.seats = seats;
        this.bookingDate = bookingDate;
    }

    // Getters and Setters
    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(int showtimeId) {
        this.showtimeId = showtimeId;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    // Optional: Override toString() method for easier display of booking info
    @Override
    public String toString() {
        return "BookingDb{" +
                "bookingId=" + bookingID +
                ", userId=" + userID +
                ", showtimeId=" + showtimeId +
                ", seats=" + seats +
                ", bookingDate='" + bookingDate + '\'' +
                '}';
    }
}
