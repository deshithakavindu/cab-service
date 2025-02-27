package Controllers;

import Models.Booking;
import Models.Customer;
import Services.BookingService;
import Services.CustomerService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;

@Path("/bookings")
@CrossOrigin(origins = "*")  // Add this for class-level CORS
public class BookingController {

    private final BookingService bookingService = new BookingService();
    private final CustomerService customerService = new CustomerService();
    private final Gson gson = new Gson();

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @CrossOrigin
    public Response createBooking(String jsonBody) {
        try {
            System.out.println("Received booking data: " + jsonBody); // Debug log
            Booking booking = gson.fromJson(jsonBody, Booking.class);

            // First validate the raw JSON data
            Map<String, Object> rawData = gson.fromJson(jsonBody, Map.class);
            if (rawData.containsKey("customerId")) {
                // Convert customerId to the expected format
                int customerId = ((Double) rawData.get("customerId")).intValue();
                Customer customer = new Customer();
                customer.setId(customerId);
                booking.setCustomer(customer);
            }

            // Validate customer
            if (booking.getCustomer() == null || booking.getCustomer().getId() == 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(gson.toJson(Map.of("error", "Customer ID is required")))
                        .build();
            }

            // Fetch customer details
            Customer customer = customerService.getCustomerById(booking.getCustomer().getId());
            if (customer == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(gson.toJson(Map.of("error", "Customer not found")))
                        .build();
            }
            booking.setCustomer(customer);

            // Rest of the validation
            if (booking.getCar() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(gson.toJson(Map.of("error", "Car is required")))
                        .build();
            }

            if (booking.getStartLocation() == null || booking.getStartLocation().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(gson.toJson(Map.of("error", "Start location is required")))
                        .build();
            }

            if (booking.getStopLocation() == null || booking.getStopLocation().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(gson.toJson(Map.of("error", "Stop location is required")))
                        .build();
            }

            if (booking.getKilometers() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(gson.toJson(Map.of("error", "Kilometers must be greater than 0")))
                        .build();
            }

            booking.calculateTotalAmount();
            Booking createdBooking = bookingService.createBooking(booking);

            if (createdBooking != null) {
                return Response.status(Response.Status.CREATED)
                        .entity(gson.toJson(Map.of("message", "Booking created successfully", 
                                                 "bookingId", createdBooking.getBookingNumber())))
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(gson.toJson(Map.of("error", "Failed to create booking")))
                        .build();
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace(); // Add logging
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(gson.toJson(Map.of("error", "Invalid JSON format: " + e.getMessage())))
                    .build();
        } catch (Exception e) {
            e.printStackTrace(); // Add logging
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(Map.of("error", "Unexpected error: " + e.getMessage())))
                    .build();
        }
    }

    
    @GET
    @Path("/customer/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CrossOrigin
    public Response getBookingsByCustomerId(@PathParam("customerId") int customerId) {
        try {
            List<Booking> bookings = bookingService.getBookingsByCustomerId(customerId);
            if (bookings != null && !bookings.isEmpty()) {
                return Response.ok(gson.toJson(bookings)).build();
            } else if (bookings != null && bookings.isEmpty()) {
                return Response.status(Response.Status.OK)
                        .entity(gson.toJson(Map.of("message", "No bookings ")))
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(gson.toJson(Map.of("error", "Unable to retrieve bookings for customer ID: " + customerId)))
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(Map.of("error", "Unexpected error: " + e.getMessage())))
                    .build();
        }
    }
    
    
    
    
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @CrossOrigin
    public Response getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        if (bookings != null) {
            return Response.ok(gson.toJson(bookings)).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(Map.of("error", "Unable to retrieve bookings")))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @CrossOrigin
    public Response getBookingById(@PathParam("id") int id) {
        Booking booking = bookingService.getBookingById(id);
        if (booking != null) {
            return Response.ok(gson.toJson(booking)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(gson.toJson(Map.of("error", "Booking not found")))
                    .build();
        }
    }

    @PUT
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @CrossOrigin
    public Response updateBooking(@PathParam("id") int id, String jsonBody) {
        try {
            Booking updatedBooking = gson.fromJson(jsonBody, Booking.class);
            if (updatedBooking == null || updatedBooking.getKilometers() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(gson.toJson(Map.of("error", "Invalid or incomplete booking details")))
                        .build();
            }

            updatedBooking.calculateTotalAmount();
            boolean isUpdated = bookingService.updateBooking(id, updatedBooking);

            if (isUpdated) {
                return Response.ok(gson.toJson(Map.of("message", "Booking updated successfully")))
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(gson.toJson(Map.of("error", "Booking not found or could not be updated")))
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace(); // Add logging
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(gson.toJson(Map.of("error", "Unexpected error: " + e.getMessage())))
                    .build();
        }
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @CrossOrigin
    public Response deleteBooking(@PathParam("id") int id) {
        boolean deleted = bookingService.deleteBooking(id);
        if (deleted) {
            return Response.ok(gson.toJson(Map.of("message", "Booking deleted successfully")))
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(gson.toJson(Map.of("error", "Booking not found")))
                    .build();
        }
    }
}