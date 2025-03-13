package cab;

import org.junit.jupiter.api.Test;

import Models.Booking;
import Models.Car;
import Models.Customer;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class BookingTest {

    @Test
    public void testCalculateTotalAmount_WithoutDiscount() {
        
        Customer customer = new Customer(); 
        Car car = new Car(); 
        LocalDateTime startDateTime = LocalDateTime.now();
        Booking booking = new Booking(customer, car, startDateTime, "Delhi", "Mumbai", 80);
        
        
        booking.calculateTotalAmount();
        
        // Assert
        double expectedBaseAmount = 80 * 150; 
        double expectedTax = expectedBaseAmount * 0.10;
        double expectedTotalAmount = expectedBaseAmount + expectedTax;
        double expectedDiscount = 0; 
        double expectedFinalAmount = expectedTotalAmount - expectedDiscount;
        
        assertEquals(expectedBaseAmount + expectedTax, booking.getTotalAmount(), 0.01);
        assertEquals(expectedTax, booking.getTax(), 0.01);
        assertEquals(expectedDiscount, booking.getDiscount(), 0.01);
        assertEquals(expectedFinalAmount, booking.getFinalAmount(), 0.01);
    }
    
    @Test
    public void testCalculateTotalAmount_WithDiscount() {
        
        Customer customer = new Customer(); 
        Car car = new Car(); 
        LocalDateTime startDateTime = LocalDateTime.now();
        Booking booking = new Booking(customer, car, startDateTime, "Delhi", "Bangalore", 150);
        
        
        booking.calculateTotalAmount();
        
       
        double expectedBaseAmount = 150 * 150; 
        double expectedTax = expectedBaseAmount * 0.10;
        double expectedTotalAmount = expectedBaseAmount + expectedTax;
        double expectedDiscount = expectedTotalAmount * 0.10; 
        double expectedFinalAmount = expectedTotalAmount - expectedDiscount;
        
        assertEquals(expectedTotalAmount, booking.getTotalAmount(), 0.01);
        assertEquals(expectedTax, booking.getTax(), 0.01);
        assertEquals(expectedDiscount, booking.getDiscount(), 0.01);
        assertEquals(expectedFinalAmount, booking.getFinalAmount(), 0.01);
    }
    
    @Test
    public void testCalculateTotalAmount_ExactlyOneHundredKilometers() {
        // Arrange
        Customer customer = new Customer(); // Create a mock customer
        Car car = new Car(); // Create a mock car
        LocalDateTime startDateTime = LocalDateTime.now();
        Booking booking = new Booking(customer, car, startDateTime, "Chennai", "Hyderabad", 100);
        
        // Act
        booking.calculateTotalAmount();
        
        // Assert
        double expectedBaseAmount = 100 * 150; // 100 km * 150 rupees per km
        double expectedTax = expectedBaseAmount * 0.10;
        double expectedTotalAmount = expectedBaseAmount + expectedTax;
        double expectedDiscount = 0; // No discount for exactly 100 km
        double expectedFinalAmount = expectedTotalAmount - expectedDiscount;
        
        assertEquals(expectedTotalAmount, booking.getTotalAmount(), 0.01);
        assertEquals(expectedTax, booking.getTax(), 0.01);
        assertEquals(expectedDiscount, booking.getDiscount(), 0.01);
        assertEquals(expectedFinalAmount, booking.getFinalAmount(), 0.01);
    }
    
    @Test
    public void testCalculateTotalAmount_CustomRate() {
        // Arrange
        Customer customer = new Customer(); // Create a mock customer
        Car car = new Car(); // Create a mock car
        LocalDateTime startDateTime = LocalDateTime.now();
        Booking booking = new Booking(customer, car, startDateTime, "Pune", "Goa", 200);
        booking.setRatePerKilometer(175); // Set custom rate
        
        // Act
        booking.calculateTotalAmount();
        
        // Assert
        double expectedBaseAmount = 200 * 175; // 200 km * 175 rupees per km
        double expectedTax = expectedBaseAmount * 0.10;
        double expectedTotalAmount = expectedBaseAmount + expectedTax;
        double expectedDiscount = expectedTotalAmount * 0.10; // 10% discount for > 100 km
        double expectedFinalAmount = expectedTotalAmount - expectedDiscount;
        
        assertEquals(expectedTotalAmount, booking.getTotalAmount(), 0.01);
        assertEquals(expectedTax, booking.getTax(), 0.01);
        assertEquals(expectedDiscount, booking.getDiscount(), 0.01);
        assertEquals(expectedFinalAmount, booking.getFinalAmount(), 0.01);
    }
}