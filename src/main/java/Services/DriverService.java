package Services;

import Models.Driver;
import com.mycompany.cab.DatabaseConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class DriverService {

	public boolean registerDriver(Driver driver) {
	    Transaction transaction = null;
	    Session session = null;
	    try {
	        session = DatabaseConnection.getSessionFactory().openSession();
	        transaction = session.beginTransaction();

	        // Check if License Number already exists
	        Query query = session.createQuery("FROM Driver WHERE licenseNumber = :licenseNumber");
	        query.setParameter("licenseNumber", driver.getLicenseNumber());
	        if (!query.getResultList().isEmpty()) {
	            transaction.rollback(); // Rollback if duplicate
	            return false;
	        }

	        session.save(driver);
	        transaction.commit();
	        return true;
	    } catch (Exception e) {
	        if (transaction != null && transaction.isActive()) {
	            transaction.rollback(); // Rollback only if active
	        }
	        e.printStackTrace();
	        return false;
	    } finally {
	        if (session != null && session.isOpen()) {
	            session.close(); // Close session in `finally` block
	        }
	    }
	}


    public List<Driver> getAllDrivers() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM Driver", Driver.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Driver getDriverById(int id) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(Driver.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateDriver(Driver driver) {
        Transaction transaction = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Driver existingDriver = session.get(Driver.class, driver.getId());
            if (existingDriver == null) {
                return false;
            }

            // Check if new license number already exists (and it's not the current driver)
            Query query = session.createQuery("FROM Driver WHERE licenseNumber = :licenseNumber AND id != :id");
            query.setParameter("licenseNumber", driver.getLicenseNumber());
            query.setParameter("id", driver.getId());
            if (!query.getResultList().isEmpty()) {
                return false;
            }

            existingDriver.setName(driver.getName());
            existingDriver.setLicenseNumber(driver.getLicenseNumber());

            session.update(existingDriver);
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

    public boolean deleteDriver(int id) {
        Transaction transaction = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Driver driver = session.get(Driver.class, id);
            if (driver != null) {
                session.delete(driver);
                transaction.commit();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}
