<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Registration</title>
</head>
<h2>User Registration</h2>

<body>

<form id="registrationForm">
    <div>
        <label for="registrationNumber">Registration Number:</label>
        <input type="text" id="registrationNumber" name="registrationNumber" required>
    </div>

    <div>
        <label for="name">Full Name:</label>
        <input type="text" id="name" name="name" required>
    </div>

    <div>
        <label for="address">Address:</label>
        <input type="text" id="address" name="address" required>
    </div>

    <div>
        <label for="nic">NIC:</label>
        <input type="text" id="nic" name="nic" required>
    </div>

    <div>
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" required>
    </div>

    <div>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required>
    </div>

    <div>
        <button type="submit">Register</button>
    </div>
</form>

<!-- Optional: Display error or success messages -->
<c:if test="${not empty param.message}">
    <div>${param.message}</div>
</c:if>

<script>
    // Event listener for form submission
    document.getElementById("registrationForm").addEventListener("submit", function(event) {
        event.preventDefault(); // Prevent default form submission

        var form = this;

        // Create an object to hold form data
        var formData = {
            registrationNumber: form.registrationNumber.value,
            name: form.name.value,
            address: form.address.value,
            nic: form.nic.value,
            username: form.username.value,
            password: form.password.value
        };

        // Send the data as JSON via AJAX
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "http://localhost:8080/cab/api/customers/register", true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.onload = function() {
            if (xhr.status === 201) {
                // Handle successful registration
                alert("Registration successful!");
                // You can redirect or show a success message
            } else {
                // Handle error
                alert("Error during registration: " + xhr.statusText);
            }
        };

        // Send the data as a JSON string
        xhr.send(JSON.stringify(formData));
    });
</script>

</body>
</html>
