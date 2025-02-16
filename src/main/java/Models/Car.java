package Models;

import javax.persistence.*;

@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private int id; // Car ID (Primary Key)

    @Column(name = "model_type", nullable = false)
    private String modelType; // Car Model Type (e.g., Sedan, SUV)

    @Column(name = "license_plate_number", nullable = false, unique = true)
    private String licensePlateNumber; // Unique License Plate Number for the car

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false) // Foreign key to Driver table
    private Driver driver; // The driver associated with this car

  

    // Default Constructor
    public Car() {}

    // Parameterized Constructor
    public Car(String modelType, String licensePlateNumber, Driver driver, byte[] carImage) {
        this.modelType = modelType;
        this.licensePlateNumber = licensePlateNumber;
        this.driver = driver;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

   
}
