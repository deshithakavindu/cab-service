package cab;

import Models.Booking;
import Models.Customer;
import Models.Car;
import Services.BookingService;
import Services.CustomerService;
import org.junit.Before;
import org.junit.Test;

import Controllers.BookingController;

import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BookingControllerTest {

    // Test-specific implementation of BookingService
    private static class TestBookingService extends BookingService {
        // Default return values
        private Booking createdBooking = null;
        private List<Booking> allBookings = new ArrayList<>();
        private List<Booking> customerBookings = new ArrayList<>();
        private boolean updateBookingResult = true;
        private boolean deleteBookingResult = true;

        @Override
        public Booking createBooking(Booking booking) {
            if (booking != null && booking.getCustomer() != null && booking.getCar() != null) {
                // Set a booking number for successful creation
                booking.setBookingNumber(1);
                this.createdBooking = booking;
                return booking;
            }
            return null;
        }

        @Override
        public List<Booking> getAllBookings() {
            return allBookings;
        }

        @Override
        public List<Booking> getBookingsByCustomerId(int customerId) {
            if (customerId == 1) {
                return customerBookings;
            }
            return new ArrayList<>();
        }

        @Override
        public Booking getBookingById(int id) {
            if (id == 1) {
                Booking booking = new Booking();
                booking.setBookingNumber(1);
                
                Customer customer = new Customer();
                customer.setId(1);
                booking.setCustomer(customer);
                
                Car car = new Car();
                car.setId(1);
                booking.setCar(car);
                
                booking.setStartDateTime(LocalDateTime.now());
                booking.setStartLocation("Start Location");
                booking.setStopLocation("Stop Location");
                booking.setKilometers(50);
                booking.setTotalAmount(1000.0);
                
                return booking;
            }
            return null;
        }

        @Override
        public boolean updateBooking(int id, Booking booking) {
            if (id == 1 && booking != null) {
                return updateBookingResult;
            }
            return false;
        }

        @Override
        public boolean deleteBooking(int id) {
            if (id == 1) {
                return deleteBookingResult;
            }
            return false;
        }

        // Helper methods for test setup
        public void setCreatedBooking(Booking booking) {
            this.createdBooking = booking;
        }

        public void setAllBookings(List<Booking> bookings) {
            this.allBookings = bookings;
        }

        public void setCustomerBookings(List<Booking> bookings) {
            this.customerBookings = bookings;
        }

        public void setUpdateBookingResult(boolean result) {
            this.updateBookingResult = result;
        }

        public void setDeleteBookingResult(boolean result) {
            this.deleteBookingResult = result;
        }
    }

    // Test-specific implementation of CustomerService
    private static class TestCustomerService extends CustomerService {
        @Override
        public Customer getCustomerById(int id) {
            if (id == 1) {
                Customer customer = new Customer();
                customer.setId(1);
                customer.setName("John Doe");
                customer.setUsername("johndoe");
                return customer;
            }
            return null;
        }
    }

    private BookingController bookingController;
    private TestBookingService testBookingService;
    private TestCustomerService testCustomerService;

    @Before
    public void setUp() {
        // Create test services and controller
        testBookingService = new TestBookingService();
        testCustomerService = new TestCustomerService();
        bookingController = new BookingController();
        
        // Use reflection to inject our test services
        try {
            // Inject the booking service
            Field bookingServiceField = BookingController.class.getDeclaredField("bookingService");
            bookingServiceField.setAccessible(true);
            bookingServiceField.set(bookingController, testBookingService);
            
            // Inject the customer service
            Field customerServiceField = BookingController.class.getDeclaredField("customerService");
            customerServiceField.setAccessible(true);
            customerServiceField.set(bookingController, testCustomerService);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not set up test environment: " + e.getMessage());
        }
    }

    @Test
    public void testCreateBooking_Success() {
        // Arrange
        String jsonBody = "{\"customerId\":1,\"car\":{\"id\":1,\"name\":\"Toyota Camry\"},\"startDateTime\":\"2023-01-01T10:00:00\",\"startLocation\":\"City Center\",\"stopLocation\":\"Airport\",\"kilometers\":50}";
        
        // Act
        Response response = bookingController.createBooking(jsonBody);
        
        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Booking created successfully"));
    }

    @Test
    public void testCreateBooking_MissingCustomerId() {
        // Arrange
        String jsonBody = "{\"car\":{\"id\":1,\"name\":\"Toyota Camry\"},\"startDateTime\":\"2023-01-01T10:00:00\",\"startLocation\":\"City Center\",\"stopLocation\":\"Airport\",\"kilometers\":50}";
        
        // Act
        Response response = bookingController.createBooking(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Customer ID is required"));
    }

    @Test
    public void testCreateBooking_InvalidCustomerId() {
        // Arrange
        String jsonBody = "{\"customerId\":999,\"car\":{\"id\":1,\"name\":\"Toyota Camry\"},\"startDateTime\":\"2023-01-01T10:00:00\",\"startLocation\":\"City Center\",\"stopLocation\":\"Airport\",\"kilometers\":50}";
        
        // Act
        Response response = bookingController.createBooking(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Customer not found"));
    }

    @Test
    public void testCreateBooking_MissingCar() {
        // Arrange
        String jsonBody = "{\"customerId\":1,\"startDateTime\":\"2023-01-01T10:00:00\",\"startLocation\":\"City Center\",\"stopLocation\":\"Airport\",\"kilometers\":50}";
        
        // Act
        Response response = bookingController.createBooking(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Car is required"));
    }

    @Test
    public void testCreateBooking_MissingStartDateTime() {
        // Arrange
        String jsonBody = "{\"customerId\":1,\"car\":{\"id\":1,\"name\":\"Toyota Camry\"},\"startLocation\":\"City Center\",\"stopLocation\":\"Airport\",\"kilometers\":50}";
        
        // Act
        Response response = bookingController.createBooking(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Start date and time are required"));
    }

    @Test
    public void testCreateBooking_MissingStartLocation() {
        // Arrange
        String jsonBody = "{\"customerId\":1,\"car\":{\"id\":1,\"name\":\"Toyota Camry\"},\"startDateTime\":\"2023-01-01T10:00:00\",\"stopLocation\":\"Airport\",\"kilometers\":50}";
        
        // Act
        Response response = bookingController.createBooking(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Start location is required"));
    }

    @Test
    public void testCreateBooking_MissingStopLocation() {
        // Arrange
        String jsonBody = "{\"customerId\":1,\"car\":{\"id\":1,\"name\":\"Toyota Camry\"},\"startDateTime\":\"2023-01-01T10:00:00\",\"startLocation\":\"City Center\",\"kilometers\":50}";
        
        // Act
        Response response = bookingController.createBooking(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Stop location is required"));
    }

    @Test
    public void testCreateBooking_InvalidKilometers() {
        // Arrange
        String jsonBody = "{\"customerId\":1,\"car\":{\"id\":1,\"name\":\"Toyota Camry\"},\"startDateTime\":\"2023-01-01T10:00:00\",\"startLocation\":\"City Center\",\"stopLocation\":\"Airport\",\"kilometers\":0}";
        
        // Act
        Response response = bookingController.createBooking(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Kilometers must be greater than 0"));
    }

   

    @Test
    public void testGetBookingsByCustomerId_Success() {
        // Arrange
        List<Booking> bookings = new ArrayList<>();
        Booking booking1 = new Booking();
        booking1.setBookingNumber(1);
        booking1.setStartLocation("Location 1");
        bookings.add(booking1);
        
        Booking booking2 = new Booking();
        booking2.setBookingNumber(2);
        booking2.setStartLocation("Location 2");
        bookings.add(booking2);
        
        testBookingService.setCustomerBookings(bookings);
        
        // Act
        Response response = bookingController.getBookingsByCustomerId(1);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Location 1"));
        assertTrue(response.getEntity().toString().contains("Location 2"));
    }

    @Test
    public void testGetBookingsByCustomerId_NoBookings() {
        // Arrange
        testBookingService.setCustomerBookings(new ArrayList<>());
        
        // Act
        Response response = bookingController.getBookingsByCustomerId(1);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("No bookings"));
    }

    @Test
    public void testGetAllBookings_Success() {
        // Arrange
        List<Booking> bookings = new ArrayList<>();
        Booking booking1 = new Booking();
        booking1.setBookingNumber(1);
        booking1.setStartLocation("Location 1");
        bookings.add(booking1);
        
        Booking booking2 = new Booking();
        booking2.setBookingNumber(2);
        booking2.setStartLocation("Location 2");
        bookings.add(booking2);
        
        testBookingService.setAllBookings(bookings);
        
        // Act
        Response response = bookingController.getAllBookings();
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetAllBookings_Failure() {
        // Arrange
        testBookingService.setAllBookings(null);
        
        // Act
        Response response = bookingController.getAllBookings();
        
        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Unable to retrieve bookings"));
    }

    @Test
    public void testGetBookingById_Success() {
        // Arrange - testBookingService is already set up to return a booking for ID 1
        
        // Act
        Response response = bookingController.getBookingById(1);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetBookingById_NotFound() {
        // Arrange - testBookingService is already set up to return null for any ID other than 1
        
        // Act
        Response response = bookingController.getBookingById(999);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Booking not found"));
    }

    @Test
    public void testUpdateBooking_Success() {
        // Arrange
        String jsonBody = "{\"customerId\":1,\"car\":{\"id\":1,\"name\":\"Toyota Camry\"},\"startDateTime\":\"2023-01-01T10:00:00\",\"startLocation\":\"Updated Start\",\"stopLocation\":\"Updated Stop\",\"kilometers\":75}";
        testBookingService.setUpdateBookingResult(true);
        
        // Act
        Response response = bookingController.updateBooking(1, jsonBody);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Booking updated successfully"));
    }

    @Test
    public void testUpdateBooking_InvalidData() {
        // Arrange
        String jsonBody = "{\"customerId\":1,\"car\":{\"id\":1,\"name\":\"Toyota Camry\"},\"startDateTime\":\"2023-01-01T10:00:00\",\"startLocation\":\"Updated Start\",\"stopLocation\":\"Updated Stop\",\"kilometers\":0}";
        
        // Act
        Response response = bookingController.updateBooking(1, jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Invalid or incomplete booking details"));
    }

    @Test
    public void testUpdateBooking_MissingStartDateTime() {
        // Arrange
        String jsonBody = "{\"customerId\":1,\"car\":{\"id\":1,\"name\":\"Toyota Camry\"},\"startLocation\":\"Updated Start\",\"stopLocation\":\"Updated Stop\",\"kilometers\":75}";
        
        // Act
        Response response = bookingController.updateBooking(1, jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Invalid or incomplete booking details"));
    }

    @Test
    public void testUpdateBooking_NotFound() {
        // Arrange
        String jsonBody = "{\"customerId\":1,\"car\":{\"id\":1,\"name\":\"Toyota Camry\"},\"startDateTime\":\"2023-01-01T10:00:00\",\"startLocation\":\"Updated Start\",\"stopLocation\":\"Updated Stop\",\"kilometers\":75}";
        
        // Act
        Response response = bookingController.updateBooking(999, jsonBody);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Booking not found or could not be updated"));
    }

    @Test
    public void testDeleteBooking_Success() {
        // Arrange
        testBookingService.setDeleteBookingResult(true);
        
        // Act
        Response response = bookingController.deleteBooking(1);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Booking deleted successfully"));
    }

    @Test
    public void testDeleteBooking_NotFound() {
        // Arrange - testBookingService is already set up to return false for any ID other than 1
        
        // Act
        Response response = bookingController.deleteBooking(999);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Booking not found"));
    }
}