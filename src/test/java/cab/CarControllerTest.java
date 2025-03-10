package cab;

import Models.Car;
import Services.CarService;
import org.junit.Before;
import org.junit.Test;

import Controllers.CarController;

import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CarControllerTest {

    // Test-specific implementation of CarService
    private static class TestCarService extends CarService {
        // Default return values
        private Car registeredCar = null;
        private List<Car> allCars = new ArrayList<>();
        private boolean updateCarResult = true;
        private boolean deleteCarResult = true;

        @Override
        public Car registerCar(Car car) {
            if (car != null && car.getModelType() != null && car.getLicensePlateNumber() != null) {
                // Set an ID for successful registration
                car.setId(1);
                this.registeredCar = car;
                return car;
            }
            return null;
        }

        @Override
        public List<Car> getAllCars() {
            return allCars;
        }

        @Override
        public Car getCarById(int id) {
            if (id == 1) {
                Car car = new Car();
                car.setId(1);
                car.setModelType("Toyota Camry");
                car.setLicensePlateNumber("ABC123");
                return car;
            }
            return null;
        }

        @Override
        public boolean updateCar(int id, Car car) {
            if (id == 1 && car != null) {
                return updateCarResult;
            }
            return false;
        }

        @Override
        public boolean deleteCar(int id) {
            if (id == 1) {
                return deleteCarResult;
            }
            return false;
        }

        // Helper methods for test setup
        public void setRegisteredCar(Car car) {
            this.registeredCar = car;
        }

        public void setAllCars(List<Car> cars) {
            this.allCars = cars;
        }

        public void setUpdateCarResult(boolean result) {
            this.updateCarResult = result;
        }

        public void setDeleteCarResult(boolean result) {
            this.deleteCarResult = result;
        }
    }

    private CarController carController;
    private TestCarService testCarService;

    @Before
    public void setUp() {
        // Create test service and controller
        testCarService = new TestCarService();
        carController = new CarController();
        
        // Use reflection to inject our test service
        try {
            Field carServiceField = CarController.class.getDeclaredField("carService");
            carServiceField.setAccessible(true);
            carServiceField.set(carController, testCarService);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not set up test environment: " + e.getMessage());
        }
    }

    @Test
    public void testRegisterCar_Success() {
        // Arrange
        String jsonBody = "{\"modelType\":\"Toyota Camry\",\"licensePlateNumber\":\"ABC123\"}";
        
        // Act
        Response response = carController.registerCar(jsonBody);
        
        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Car registered successfully"));
    }

    @Test
    public void testRegisterCar_MissingModelType() {
        // Arrange
        String jsonBody = "{\"licensePlateNumber\":\"ABC123\"}";
        
        // Act
        Response response = carController.registerCar(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("All fields are required"));
    }

    @Test
    public void testRegisterCar_MissingLicensePlate() {
        // Arrange
        String jsonBody = "{\"modelType\":\"Toyota Camry\"}";
        
        // Act
        Response response = carController.registerCar(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("All fields are required"));
    }

    @Test
    public void testRegisterCar_EmptyFields() {
        // Arrange
        String jsonBody = "{\"modelType\":\"\",\"licensePlateNumber\":\"\"}";
        
        // Act
        Response response = carController.registerCar(jsonBody);
        
        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("All fields are required"));
    }

 

    @Test
    public void testGetAllCars_Success() {
        // Arrange
        List<Car> cars = new ArrayList<>();
        Car car1 = new Car();
        car1.setId(1);
        car1.setModelType("Toyota Camry");
        car1.setLicensePlateNumber("ABC123");
        cars.add(car1);
        
        Car car2 = new Car();
        car2.setId(2);
        car2.setModelType("Honda Civic");
        car2.setLicensePlateNumber("XYZ789");
        cars.add(car2);
        
        testCarService.setAllCars(cars);
        
        // Act
        Response response = carController.getAllCars();
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String responseBody = response.getEntity().toString();
        assertTrue(responseBody.contains("Toyota Camry"));
        assertTrue(responseBody.contains("Honda Civic"));
        assertTrue(responseBody.contains("ABC123"));
        assertTrue(responseBody.contains("XYZ789"));
    }

    @Test
    public void testGetAllCars_EmptyList() {
        // Arrange
        testCarService.setAllCars(new ArrayList<>());
        
        // Act
        Response response = carController.getAllCars();
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("[]", response.getEntity());
    }

    @Test
    public void testGetCarById_Success() {
        // Arrange - testCarService is already set up to return a car for ID 1
        
        // Act
        Response response = carController.getCarById(1);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String responseBody = response.getEntity().toString();
        assertTrue(responseBody.contains("Toyota Camry"));
        assertTrue(responseBody.contains("ABC123"));
    }

    @Test
    public void testGetCarById_NotFound() {
        // Arrange - testCarService is already set up to return null for any ID other than 1
        
        // Act
        Response response = carController.getCarById(999);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Car not found"));
    }

    @Test
    public void testUpdateCar_Success() {
        // Arrange
        String jsonBody = "{\"modelType\":\"Toyota Corolla\",\"licensePlateNumber\":\"DEF456\"}";
        testCarService.setUpdateCarResult(true);
        
        // Act
        Response response = carController.updateCar(1, jsonBody);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Car updated successfully"));
    }

    @Test
    public void testUpdateCar_NotFound() {
        // Arrange
        String jsonBody = "{\"modelType\":\"Toyota Corolla\",\"licensePlateNumber\":\"DEF456\"}";
        
        // Act
        Response response = carController.updateCar(999, jsonBody);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Car not found or license plate already exists"));
    }

    @Test
    public void testUpdateCar_DuplicateLicensePlate() {
        // Arrange
        String jsonBody = "{\"modelType\":\"Toyota Corolla\",\"licensePlateNumber\":\"DEF456\"}";
        testCarService.setUpdateCarResult(false); // Simulate duplicate license plate
        
        // Act
        Response response = carController.updateCar(1, jsonBody);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Car not found or license plate already exists"));
    }

    @Test
    public void testDeleteCar_Success() {
        // Arrange
        testCarService.setDeleteCarResult(true);
        
        // Act
        Response response = carController.deleteCar(1);
        
        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Car deleted successfully"));
    }

    @Test
    public void testDeleteCar_NotFound() {
        // Arrange - testCarService is already set up to return false for any ID other than 1
        
        // Act
        Response response = carController.deleteCar(999);
        
        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Car not found"));
    }
}