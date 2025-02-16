package Controllers;

import Models.Customer;
import Services.CustomerService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;

@Path("/customers")
public class AuthController {

    private final CustomerService customerService = new CustomerService();
    private final Gson gson = new Gson();

    // Register Customer (CREATE)
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerCustomer(String jsonBody) {
        try {
            Customer customer = gson.fromJson(jsonBody, Customer.class);

            // Validation for missing fields
            if (customer.getName() == null || customer.getName().trim().isEmpty() ||
                customer.getUsername() == null || customer.getUsername().trim().isEmpty() ||
                customer.getPassword() == null || customer.getPassword().trim().isEmpty() ||
                customer.getNic() == null || customer.getNic().trim().isEmpty() ||
                customer.getAddress() == null || customer.getAddress().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"All fields are required\"}")
                        .build();
            }

            // Additional validation for username (e.g., alphanumeric and length)
         

            // Additional validation for password (e.g., minimum length, at least one number and one special character)
            if (customer.getPassword().length() < 4|| !customer.getPassword().matches(".*\\d.*") || !customer.getPassword().matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Password must be at least 8 characters long and contain at least one number and one special character\"}")
                        .build();
            }

            // Validation for NIC (e.g., a basic pattern check for a 9-digit number or specific format)
            if (!customer.getNic().matches("^[0-9]{12}[vV]$")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"NIC must be a 12-digit number followed by a 'V' or 'v'\"}")
                        .build();
            }

            // Check if username or NIC already exists
            boolean isRegistered = customerService.registerCustomer(customer);

            if (isRegistered) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Customer registered successfully!");
                return Response.status(Response.Status.CREATED)
                        .entity(gson.toJson(response))
                        .build();
            } else {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\": \"Username or NIC already exists\"}")
                        .build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An error occurred during registration\"}")
                    .build();
        }
    }

    // Login Customer (READ)
 // Login Customer (READ)
    @CrossOrigin
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginCustomer(String jsonBody) {
        try {
            Map<String, String> credentials = gson.fromJson(jsonBody, Map.class);
            String username = credentials.get("username");
            String password = credentials.get("password");

            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Username and password are required\"}")
                        .build();
            }

            // Admin login check
            if ("admin".equals(username) && "123".equals(password)) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Admin login successful!");
                response.put("redirect", "Admin.html");
                response.put("username", username); // Return username in the response
                return Response.ok(gson.toJson(response)).build();
            }

            // Customer login check
            Customer customer = customerService.loginCustomer(username, password);
            if (customer != null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Customer login successful!");
                response.put("redirect", "Home.html");
                response.put("id", String.valueOf(customer.getId()));
                response.put("username", username); // Return username in the response
// Pass the customer ID

                return Response.ok(gson.toJson(response)).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Invalid credentials\"}")
                        .build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An error occurred during login\"}")
                    .build();
        }
    }

    // Get All Customers (READ)
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            return Response.ok(gson.toJson(customers)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An error occurred while retrieving customers\"}")
                    .build();
        }
    }
    
 // Get Customer by Username (READ)

    // Get Customer by ID (READ)
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerById(@PathParam("id") int id) {
        try {
            Customer customer = customerService.getCustomerById(id);
            if (customer != null) {
                return Response.ok(gson.toJson(customer)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Customer not found\"}")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An error occurred while retrieving customer\"}")
                    .build();
        }
    }

    // Update Customer (UPDATE)
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@PathParam("id") int id, String jsonBody) {
        try {
            Customer customer = gson.fromJson(jsonBody, Customer.class);
            customer.setId(id);

            boolean isUpdated = customerService.updateCustomer(customer);

            if (isUpdated) {
                return Response.ok("{\"message\": \"Customer updated successfully\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Customer not found or update failed\"}")
                        .build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An error occurred while updating customer\"}")
                    .build();
        }
    }

    // Delete Customer (DELETE)
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@PathParam("id") int id) {
        try {
            boolean isDeleted = customerService.deleteCustomer(id);

            if (isDeleted) {
                return Response.ok("{\"message\": \"Customer deleted successfully\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Customer not found or deletion failed\"}")
                        .build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An error occurred while deleting customer\"}")
                    .build();
        }
    }
}
