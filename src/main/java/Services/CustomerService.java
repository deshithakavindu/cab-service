package Services;

import Models.Customer;
import com.mycompany.cab.DatabaseConnection;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class CustomerService {
    
    public boolean registerCustomer(Customer customer) {
        Transaction transaction = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            // Check if username already exists
            Query query = session.createQuery("FROM Customer WHERE username = :username");
            query.setParameter("username", customer.getUsername());
            if (!query.getResultList().isEmpty()) {
                return false;
            }
            
            // Check if NIC already exists
            query = session.createQuery("FROM Customer WHERE nic = :nic");
            query.setParameter("nic", customer.getNic());
            if (!query.getResultList().isEmpty()) {
                return false;
            }
            
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
            "FROM Customer WHERE username = :username AND password = :password", 
            Customer.class
        );
        query.setParameter("username", username);
        query.setParameter("password", password);
        
        Customer customer = query.uniqueResult();

        if (customer != null) {
            return customer; // Return Customer object instead of a string
        } else {
            return null; // Or handle as needed
        }
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
