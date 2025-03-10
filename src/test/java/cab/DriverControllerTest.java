package cab;

import Models.Driver;
import Services.DriverService;
import org.junit.Before;
import org.junit.Test;

import Controllers.DriverController;

import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DriverControllerTest {

    // Test-specific implementation of DriverService
    private static class TestDriverService extends DriverService {
        // Default return values
        private boolean registerDriverResult = true;
        private List<Driver> allDrivers = new ArrayList<>();
        private boolean updateDriverResult = true;
        private boolean deleteDriverResult = true;

        @Override
        public boolean registerDriver(Driver driver) {
            if (driver != null && driver.getName() != null && driver.getLicenseNumber() != null) {
                return registerDriverResult;
            }
            return false;
        }

        @Override
        public List<Driver> getAllDrivers() {
            return allDrivers;
        }

        @Override
        public Driver getDriverById(int id) {
            if (id == 1) {
                Driver driver = new Driver();
                driver.setId(1);
                driver.setName("John Doe");
                driver.setLicenseNumber("DL12345");
                return driver;
            }
            return null;
        }

        @Override
        public boolean updateDriver(Driver driver) {
            if (driver != null && driver.getId() == 1) {
                return updateDriverResult;
            }
            return false;
        }

        @Override
        public boolean deleteDriver(int id) {
            if (id == 1) {
                return deleteDriverResult;
            }
            return false;
        }

        // Helper methods for test setup
        public void setRegisterDriverResult(boolean result) {
            this.registerDriverResult = result;
        }

        public void setAllDrivers(List<Driver> drivers) {
            this.allDrivers = drivers;
        }

        public void setUpdateDriverResult(boolean result) {
            this.updateDriverResult = result;
        }

        public void setDeleteDriverResult(boolean result) {
            this.deleteDriverResult = result;
        }
    }

    private DriverController driverController;
    private TestDriverService testDriverService;

    @Before
    public void setUp() {
        // Create test service and controller
        testDriverService = new TestDriverService();
        driverController = new DriverController();
        
        // Use reflection to inject our test service
        try {
            Field driverServiceField = DriverController.class.getDeclaredField("driverService");
            driverServiceField.setAccessible(true);
            driverServiceField.set(driverController, testDriverService);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not set up test environment: " + e.getMessage());
        }
    }

    @Test
    public void testRegisterDriver_Success() {
        // Arrange
        String jsonBody = "{\"name\":\"John Doe\",\"licenseNumber\":\"DL12345\"}";
        testDriverService.setRegisterDriverResult(true);
        
        // Act
        Response response = driverController.registerDriver(jsonBody);
        
        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Driver registered successfully"));
    }

    @Test
    public void testRegisterDriver_MissingName() {
        // Arrange
        String jsonBody = "{\"licenseNumber\":\"DL12345\"}";
        
        // Act
        Response response = driverController.registerDriver(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("All fields are required"));
    }

    @Test
    public void testRegisterDriver_MissingLicenseNumber() {
        // Arrange
        String jsonBody = "{\"name\":\"John Doe\"}";
        
        // Act
        Response response = driverController.registerDriver(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("All fields are required"));
    }

    @Test
    public void testRegisterDriver_EmptyFields() {
        // Arrange
        String jsonBody = "{\"name\":\"\",\"licenseNumber\":\"\"}";
        
        // Act
        Response response = driverController.registerDriver(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("All fields are required"));
    }

    @Test
    public void testRegisterDriver_DuplicateLicenseNumber() {
        // Arrange
        String jsonBody = "{\"name\":\"John Doe\",\"licenseNumber\":\"DL12345\"}";
        testDriverService.setRegisterDriverResult(false); // Simulate duplicate license number
        
        // Act
        Response response = driverController.registerDriver(jsonBody);
        
        // Assert
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("License number already exists"));
    }

    @Test
    public void testGetAllDrivers_Success() {
        // Arrange
        List<Driver> drivers = new ArrayList<>();
        Driver driver1 = new Driver();
        driver1.setId(1);
        driver1.setName("John Doe");
        driver1.setLicenseNumber("DL12345");
        drivers.add(driver1);
        
        Driver driver2 = new Driver();
        driver2.setId(2);
        driver2.setName("Jane Smith");
        driver2.setLicenseNumber("DL67890");
        drivers.add(driver2);
        
        testDriverService.setAllDrivers(drivers);
        
        // Act
        Response response = driverController.getAllDrivers();
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String responseBody = response.getEntity().toString();
        assertTrue(responseBody.contains("John Doe"));
        assertTrue(responseBody.contains("Jane Smith"));
        assertTrue(responseBody.contains("DL12345"));
        assertTrue(responseBody.contains("DL67890"));
    }

    @Test
    public void testGetAllDrivers_EmptyList() {
        // Arrange
        testDriverService.setAllDrivers(new ArrayList<>());
        
        // Act
        Response response = driverController.getAllDrivers();
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("[]", response.getEntity());
    }

    @Test
    public void testGetDriverById_Success() {
        // Arrange - testDriverService is already set up to return a driver for ID 1
        
        // Act
        Response response = driverController.getDriverById(1);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String responseBody = response.getEntity().toString();
        assertTrue(responseBody.contains("John Doe"));
        assertTrue(responseBody.contains("DL12345"));
    }

    @Test
    public void testGetDriverById_NotFound() {
        // Arrange - testDriverService is already set up to return null for any ID other than 1
        
        // Act
        Response response = driverController.getDriverById(999);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Driver not found"));
    }

    @Test
    public void testUpdateDriver_Success() {
        // Arrange
        String jsonBody = "{\"name\":\"John Updated\",\"licenseNumber\":\"DL99999\"}";
        testDriverService.setUpdateDriverResult(true);
        
        // Act
        Response response = driverController.updateDriver(1, jsonBody);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Driver updated successfully"));
    }

    @Test
    public void testUpdateDriver_NotFound() {
        // Arrange
        String jsonBody = "{\"name\":\"John Updated\",\"licenseNumber\":\"DL99999\"}";
        
        // Act
        Response response = driverController.updateDriver(999, jsonBody);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Driver not found or license number already exists"));
    }

    @Test
    public void testUpdateDriver_DuplicateLicenseNumber() {
        // Arrange
        String jsonBody = "{\"name\":\"John Updated\",\"licenseNumber\":\"DL99999\"}";
        testDriverService.setUpdateDriverResult(false); // Simulate duplicate license number
        
        // Act
        Response response = driverController.updateDriver(1, jsonBody);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Driver not found or license number already exists"));
    }

    @Test
    public void testUpdateDriver_Exception() {
        // Arrange
        String jsonBody = "{\"name\":\"John Updated\",\"licenseNumber\":\"DL99999\"; // Invalid JSON";
        
        // Act
        Response response = driverController.updateDriver(1, jsonBody);
        
        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("An error occurred during update"));
    }

    @Test
    public void testDeleteDriver_Success() {
        // Arrange
        testDriverService.setDeleteDriverResult(true);
        
        // Act
        Response response = driverController.deleteDriver(1);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Driver deleted successfully"));
    }

    @Test
    public void testDeleteDriver_NotFound() {
        // Arrange - testDriverService is already set up to return false for any ID other than 1
        
        // Act
        Response response = driverController.deleteDriver(999);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Driver not found"));
    }
}