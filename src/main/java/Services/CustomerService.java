package Services;

import Models.Customer;
import com.mycompany.cab.DatabaseConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class CustomerService {

    public boolean registerCustomer(Customer customer) {
        Transaction transaction = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Check if username already exists
            Query<Customer> query = session.createQuery("FROM Customer WHERE username = :username", Customer.class);
            query.setParameter("username", customer.getUsername());
            if (!query.getResultList().isEmpty()) {
                return false;
            }

            // Check if NIC already exists
            query = session.createQuery("FROM Customer WHERE nic = :nic", Customer.class);
            query.setParameter("nic", customer.getNic());
            if (!query.getResultList().isEmpty()) {
                return false;
            }

            // Hash the password before saving
            String hashedPassword = BCrypt.hashpw(customer.getPassword(), BCrypt.gensalt());
            customer.setPassword(hashedPassword);

            session.save(customer);
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

    public Customer loginCustomer(String username, String password) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            Query<Customer> query = session.createQuery(
                "FROM Customer WHERE username = :username", Customer.class
            );
            query.setParameter("username", username);

            Customer customer = query.uniqueResult();
            if (customer != null) {
                String storedPassword = customer.getPassword();
                if (storedPassword != null && storedPassword.startsWith("$2a$")) { // Ensure it's a hashed password
                    if (BCrypt.checkpw(password, storedPassword)) {
                        return customer; // Login success
                    }
                }
            }
            return null; // Login failed
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Customer> getAllCustomers() {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.createQuery("FROM Customer", Customer.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Customer getCustomerById(int id) {
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            return session.get(Customer.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateCustomer(Customer customer) {
        Transaction transaction = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Ensure password is not rehashed if already hashed
            if (!customer.getPassword().startsWith("$2a$")) {
                String hashedPassword = BCrypt.hashpw(customer.getPassword(), BCrypt.gensalt());
                customer.setPassword(hashedPassword);
            }

            session.update(customer);
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

    public boolean deleteCustomer(int id) {
        Transaction transaction = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Customer customer = session.get(Customer.class, id);
            if (customer != null) {
                session.delete(customer);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}
