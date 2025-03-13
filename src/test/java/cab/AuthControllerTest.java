package cab;

import Models.Customer;
import Services.CustomerService;
import org.junit.Before;
import org.junit.Test;

import Controllers.AuthController;

import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AuthControllerTest {

    // Create a test-specific implementation of CustomerService
    private static class TestCustomerService extends CustomerService {
        // Default return values
        private boolean registerCustomerResult = true;
        private List<Customer> allCustomers = new ArrayList<>();
        private boolean updateCustomerResult = true;
        private boolean deleteCustomerResult = true;

        @Override
        public boolean registerCustomer(Customer customer) {
            // Check for existing username or NIC logic
            if ("existingUser".equals(customer.getUsername())) {
                return false;
            }
            return registerCustomerResult;
        }

        @Override
        public Customer loginCustomer(String username, String password) {
            // Simple login logic for testing
            if ("johndoe".equals(username) && "Pass@123".equals(password)) {
                Customer customer = new Customer();
                customer.setId(1);
                customer.setUsername(username);
                return customer;
            }
            return null;
        }

        @Override
        public List<Customer> getAllCustomers() {
            return allCustomers;
        }

        @Override
        public Customer getCustomerById(int id) {
            if (id == 1) {
                Customer customer = new Customer();
                customer.setId(1);
                customer.setUsername("johndoe");
                return customer;
            }
            return null;
        }

        @Override
        public boolean updateCustomer(Customer customer) {
            return customer.getId() == 1 && updateCustomerResult;
        }

        @Override
        public boolean deleteCustomer(int id) {
            return id == 1 && deleteCustomerResult;
        }

        // Helper methods for test setup
        public void setRegisterCustomerResult(boolean result) {
            this.registerCustomerResult = result;
        }

        public void setUpdateCustomerResult(boolean result) {
            this.updateCustomerResult = result;
        }

        public void setDeleteCustomerResult(boolean result) {
            this.deleteCustomerResult = result;
        }

        public void setupAllCustomers(List<Customer> customers) {
            this.allCustomers = customers;
        }
    }

    private AuthController authController;
    private TestCustomerService testService;

    @Before
    public void setUp() {
        // Create test service and controller
        testService = new TestCustomerService();
        authController = new AuthController();
        
        // Use reflection to inject our test service
        try {
            Field field = AuthController.class.getDeclaredField("customerService");
            field.setAccessible(true);
            field.set(authController, testService);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not set up test environment: " + e.getMessage());
        }
    }

    @Test
    public void testRegisterCustomer_Success() {
        
        String jsonBody = "{\"name\":\"John Doe\",\"username\":\"johndoe\","
        		+ "\"password\":\"Pass@123\",\"nic\":\"123456789012v\","
        		+ "\"address\":\"123 Main St\"}";
        
        Response response = authController.registerCustomer(jsonBody);
        
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Customer registered successfully"));
    }

    @Test
    public void testRegisterCustomer_MissingFields() {
        // Arrange
        String jsonBody = "{\"name\":\"\",\"username\":\"johndoe\",\"password\":\"Pass@123\",\"nic\":\"123456789012v\",\"address\":\"123 Main St\"}";
        
        // Act
        Response response = authController.registerCustomer(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("All fields are required"));
    }

    @Test
    public void testRegisterCustomer_InvalidPassword() {
        // Arrange
        String jsonBody = "{\"name\":\"John Doe\",\"username\":\"johndoe\",\"password\":\"123\",\"nic\":\"123456789012v\",\"address\":\"123 Main St\"}";
        
        // Act
        Response response = authController.registerCustomer(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Password must be"));
    }

    @Test
    public void testRegisterCustomer_InvalidNIC() {
        // Arrange
        String jsonBody = "{\"name\":\"John Doe\",\"username\":\"johndoe\",\"password\":\"Pass@123\",\"nic\":\"12345\",\"address\":\"123 Main St\"}";
        
        // Act
        Response response = authController.registerCustomer(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("NIC must be"));
    }

    @Test
    public void testRegisterCustomer_UserAlreadyExists() {
        // Arrange
        String jsonBody = "{\"name\":\"John Doe\",\"username\":\"existingUser\",\"password\":\"Pass@123\",\"nic\":\"123456789012v\",\"address\":\"123 Main St\"}";
        
        // Act
        Response response = authController.registerCustomer(jsonBody);
        
        // Assert
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Username or NIC already exists"));
    }

    @Test
    public void testLoginCustomer_Success() {
        // Arrange
        String jsonBody = "{\"username\":\"johndoe\",\"password\":\"Pass@123\"}";
        
        // Act
        Response response = authController.loginCustomer(jsonBody);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Customer login successful"));
        assertTrue(response.getEntity().toString().contains("Home.html"));
    }

    @Test
    public void testLoginCustomer_AdminSuccess() {
        // Arrange
        String jsonBody = "{\"username\":\"admin\",\"password\":\"123\"}";
        
        // Act
        Response response = authController.loginCustomer(jsonBody);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Admin login successful"));
        assertTrue(response.getEntity().toString().contains("Admin.html"));
    }

    @Test
    public void testLoginCustomer_MissingFields() {
        // Arrange
        String jsonBody = "{\"username\":\"\",\"password\":\"Pass@123\"}";
        
        // Act
        Response response = authController.loginCustomer(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Username and password are required"));
    }

    @Test
    public void testLoginCustomer_InvalidCredentials() {
        // Arrange
        String jsonBody = "{\"username\":\"johndoe\",\"password\":\"wrongpass\"}";
        
        // Act
        Response response = authController.loginCustomer(jsonBody);
        
        // Assert
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Invalid credentials"));
    }

    @Test
    public void testGetAllCustomers_Success() {
        // Arrange
        List<Customer> customers = new ArrayList<>();
        Customer customer1 = new Customer();
        customer1.setId(1);
        customer1.setName("John Doe");
        customers.add(customer1);
        
        Customer customer2 = new Customer();
        customer2.setId(2);
        customer2.setName("Jane Doe");
        customers.add(customer2);
        
        testService.setupAllCustomers(customers);
        
        // Act
        Response response = authController.getAllCustomers();
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetCustomerById_Success() {
        // Arrange - testService is already set up to return a customer for ID 1
        
        // Act
        Response response = authController.getCustomerById(1);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetCustomerById_NotFound() {
        // Arrange - testService is already set up to return null for any ID other than 1
        
        // Act
        Response response = authController.getCustomerById(999);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Customer not found"));
    }

    @Test
    public void testUpdateCustomer_Success() {
        // Arrange
        String jsonBody = "{\"name\":\"John Doe Updated\",\"username\":\"johndoe\",\"password\":\"Pass@123\",\"nic\":\"123456789012v\",\"address\":\"123 Main St Updated\"}";
        testService.setUpdateCustomerResult(true);
        
        // Act
        Response response = authController.updateCustomer(1, jsonBody);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Customer updated successfully"));
    }

    @Test
    public void testUpdateCustomer_NotFound() {
        // Arrange
        String jsonBody = "{\"name\":\"John Doe Updated\",\"username\":\"johndoe\",\"password\":\"Pass@123\",\"nic\":\"123456789012v\",\"address\":\"123 Main St Updated\"}";
        
        // Act
        Response response = authController.updateCustomer(999, jsonBody);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Customer not found or update failed"));
    }

    @Test
    public void testDeleteCustomer_Success() {
        // Arrange
        testService.setDeleteCustomerResult(true);
        
        // Act
        Response response = authController.deleteCustomer(1);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Customer deleted successfully"));
    }

    @Test
    public void testDeleteCustomer_NotFound() {
        // Arrange - testService is already set up to return false for any ID other than 1
        
        // Act
        Response response = authController.deleteCustomer(999);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Customer not found or deletion failed"));
    }
}