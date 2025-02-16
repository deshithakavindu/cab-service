package Models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_number")
    private int bookingNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false) // Foreign Key to Customer table
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false) // Foreign Key to Car table
    private Car car;

    @Column(name = "start_location")
    private String startLocation;

    @Column(name = "stop_location")
    private String stopLocation;

    @Column(name = "kilometers")
    private double kilometers;

    @Column(name = "rate_per_kilometer")
    private double ratePerKilometer = 150; // Set default rate to 150 rupees

    @Column(name = "tax")
    private double tax; // Tax amount (10% will be set in the calculation method)

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "discount")
    private double discount;

    @Column(name = "final_amount")
    private double finalAmount;

    // Default Constructor
    public Booking() {}

    // Parameterized Constructor
    public Booking(Customer customer, Car car, String startLocation, String stopLocation, double kilometers) {
        this.customer = customer;
        this.car = car;
        this.startLocation = startLocation;
        this.stopLocation = stopLocation;
        this.kilometers = kilometers;
    }

    // Getters and Setters
    public int getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(int bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getStopLocation() {
        return stopLocation;
    }

    public void setStopLocation(String stopLocation) {
        this.stopLocation = stopLocation;
    }

    public double getKilometers() {
        return kilometers;
    }

    public void setKilometers(double kilometers) {
        this.kilometers = kilometers;
    }

    public double getRatePerKilometer() {
        return ratePerKilometer;
    }

    public void setRatePerKilometer(double ratePerKilometer) {
        this.ratePerKilometer = ratePerKilometer;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    // Method to calculate the total amount
    public void calculateTotalAmount() {
        // Calculate base total amount
        totalAmount = (kilometers * ratePerKilometer);
        
        // Apply tax (10% of the total amount)
        tax = totalAmount * 0.10;

        // Add tax to the total amount
        totalAmount += tax;

        // Check for discount if kilometers exceed 100
        if (kilometers > 100) {
            // Apply discount if condition is met (e.g., 10% discount for distances greater than 100 km)
            discount = totalAmount * 0.10; // Example: 10% discount
        }

        // Apply the discount to the total amount
        finalAmount = totalAmount - discount;
    }
}
