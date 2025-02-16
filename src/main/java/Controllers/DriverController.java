package Controllers;

import Models.Driver;
import Services.DriverService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.gson.Gson;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Path("/drivers")
public class DriverController {

    private final DriverService driverService = new DriverService();
    private final Gson gson = new Gson();

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerDriver(String jsonBody) {
        try {
            Driver driver = gson.fromJson(jsonBody, Driver.class);

            // Basic validation
            if (driver.getName() == null || driver.getName().trim().isEmpty() ||
                driver.getLicenseNumber() == null || driver.getLicenseNumber().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"All fields are required\"}")
                    .build();
            }

            boolean isRegistered = driverService.registerDriver(driver);

            if (isRegistered) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Driver registered successfully!");
                return Response.status(Response.Status.CREATED)
                    .entity(gson.toJson(response))
                    .build();
            } else {
                return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"License number already exists\"}")
                    .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"An error occurred during registration\"}")
                .build();
        }
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        return Response.ok(gson.toJson(drivers)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDriverById(@PathParam("id") int id) {
        Driver driver = driverService.getDriverById(id);
        if (driver != null) {
            return Response.ok(gson.toJson(driver)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Driver not found\"}")
                .build();
        }
    }

    @PUT
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDriver(@PathParam("id") int id, String jsonBody) {
        try {
            Driver driver = gson.fromJson(jsonBody, Driver.class);
            driver.setId(id);

            boolean updated = driverService.updateDriver(driver);
            if (updated) {
                return Response.ok("{\"message\": \"Driver updated successfully\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Driver not found or license number already exists\"}")
                    .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"An error occurred during update\"}")
                .build();
        }
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDriver(@PathParam("id") int id) {
        boolean deleted = driverService.deleteDriver(id);
        if (deleted) {
            return Response.ok("{\"message\": \"Driver deleted successfully\"}").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Driver not found\"}")
                .build();
        }
    }
}
