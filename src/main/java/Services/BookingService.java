package Services;

import Models.Booking;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.mycompany.cab.DatabaseConnection;
import Models.Customer;
import Models.Car;
import java.util.List;

public class BookingService {

    // Create a new booking with Hibernate persistence
	public Booking createBooking(Booking booking) {
	    if (booking == null || booking.getCustomer() == null || booking.getCar() == null) {
	        return null;
	    }
	    
	    Transaction transaction = null;
	    try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
	        transaction = session.beginTransaction();
	        
	        // Load the actual Customer entity from the database
	        Customer customer = session.get(Customer.class, booking.getCustomer().getId());
	        if (customer == null) {
	            throw new Exception("Customer not found");
	        }
	        booking.setCustomer(customer);
	        
	        // Load the actual Car entity from the database
	        Car car = session.get(Car.class, booking.getCar().getId());
	        if (car == null) {
	            throw new Exception("Car not found");
	        }
	        booking.setCar(car);
	        
	        // Now save the booking with the loaded entities
	        session.save(booking);
	        transaction.commit();
	        return booking;
	    } catch (Exception e) {
	        if (transaction != null) {
	            transaction.rollback();
	        }
	        e.printStackTrace();
	        return null;
	    }
	}

    // Get all bookings
    public List<Booking> getAllBookings() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM Booking", Booking.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if there's an error fetching the bookings
        }
    }

    // Get a booking by ID
    public Booking getBookingById(int id) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(Booking.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if the booking is not found or there's an error
        }
    }
    
    public List<Booking> getBookingsByCustomerId(int customerId) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM Booking WHERE customer.id = :customerId", Booking.class)
                          .setParameter("customerId", customerId)
                          .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if there's an error fetching the bookings
        }
    }

    // Update a booking
    public boolean updateBooking(int id, Booking updatedBooking) {
        if (updatedBooking == null) {
            return false; // Early return if the updatedBooking is null
        }

        Transaction transaction = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Booking existingBooking = session.get(Booking.class, id);

            if (existingBooking == null) {
                return false; // Return false if the booking to update is not found
            }

            // Update fields selectively
            existingBooking.setCustomer(updatedBooking.getCustomer());
            existingBooking.setCar(updatedBooking.getCar());
            existingBooking.setStartLocation(updatedBooking.getStartLocation());
            existingBooking.setStopLocation(updatedBooking.getStopLocation());

            session.update(existingBooking);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false; // Return false if there's an error during update
        }
    }

    // Delete a booking
    public boolean deleteBooking(int id) {
        Transaction transaction = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Booking booking = session.get(Booking.class, id);

            if (booking != null) {
                session.delete(booking);
                transaction.commit();
                return true; // Return true if the deletion is successful
            }
            return false; // Return false if the booking is not found
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false; // Return false if there's an error during deletion
        }
    }
}
