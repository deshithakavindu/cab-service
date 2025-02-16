package Controllers;

import Models.Car;
import Services.CarService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.CrossOrigin;

import com.google.gson.Gson;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Path("/cars")
public class CarController {

    private final CarService carService = new CarService();
    private final Gson gson = new Gson();

   
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerCar(String jsonBody) {
        try {
            Car car = gson.fromJson(jsonBody, Car.class);

            // Basic validation
            if (car.getModelType() == null || car.getModelType().trim().isEmpty() ||
                car.getLicensePlateNumber() == null || car.getLicensePlateNumber().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"All fields are required\"}")
                    .build();
            }

            Car registeredCar = carService.registerCar(car);

            if (registeredCar != null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Car registered successfully!");
                return Response.status(Response.Status.CREATED)
                    .entity(gson.toJson(response))
                    .build();
            } else {
                return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"License plate already exists\"}")
                    .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"An error occurred during registration\"}")
                .build();
        }
    }

    // Get all cars
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCars() {
        List<Car> cars = carService.getAllCars();
        return Response.ok(gson.toJson(cars)).build();
    }

    // Get car by ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCarById(@PathParam("id") int id) {
        Car car = carService.getCarById(id);
        if (car != null) {
            return Response.ok(gson.toJson(car)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Car not found\"}")
                .build();
        }
    }

    // Update a car
    @PUT
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCar(@PathParam("id") int id, String jsonBody) {
        try {
            Car updatedCar = gson.fromJson(jsonBody, Car.class);
            boolean isUpdated = carService.updateCar(id, updatedCar); // ✅ Expecting boolean return

            if (isUpdated) {
                return Response.ok("{\"message\": \"Car updated successfully\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Car not found or license plate already exists\"}")
                    .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"An error occurred during update\"}")
                .build();
        }
    }


    // Delete a car
    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCar(@PathParam("id") int id) {
        boolean deleted = carService.deleteCar(id);
        if (deleted) {
            return Response.ok("{\"message\": \"Car deleted successfully\"}").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Car not found\"}")
                .build();
        }
    }
}
