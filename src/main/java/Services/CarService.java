package Services;

import Models.Car;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.mycompany.cab.DatabaseConnection;

import java.util.List;

public class CarService {

    // Register a new car
	public Car registerCar(Car car) {
	    Transaction transaction = null;
	    try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
	        transaction = session.beginTransaction();

	        // Check if license plate already exists
	        Car existingCar = (Car) session.createQuery("FROM Car WHERE licensePlateNumber = :plate")
	                .setParameter("plate", car.getLicensePlateNumber())
	                .uniqueResult();

	        if (existingCar != null) {
	            return null; // License plate already exists, return null
	        }

	        session.save(car);
	        transaction.commit();
	        return car; // Return the saved car object
	    } catch (Exception e) {
	        if (transaction != null) {
	            transaction.rollback();
	        }
	        e.printStackTrace();
	        return null; // Return null on failure
	    }
	}

    // Get all cars
    public List<Car> getAllCars() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM Car", Car.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get car by ID
    public Car getCarById(int id) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(Car.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Update a car
    public boolean updateCar(int id, Car updatedCar) {
        Transaction transaction = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Car car = session.get(Car.class, id);
            if (car == null) {
                return false; // Car not found
            }

            car.setModelType(updatedCar.getModelType());
            car.setLicensePlateNumber(updatedCar.getLicensePlateNumber());
            car.setDriver(updatedCar.getDriver());

            session.update(car);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    // Delete a car
    public boolean deleteCar(int id) {
        Transaction transaction = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Car car = session.get(Car.class, id);
            if (car == null) {
                return false; // Car not found
            }

            session.delete(car);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}
