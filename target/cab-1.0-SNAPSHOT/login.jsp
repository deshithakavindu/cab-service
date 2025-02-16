<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Login</title>
</head>
<body>

<h2>User Login</h2>

<!-- Login Form -->
<form id="loginForm">
    <div>
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" required>
    </div>

    <div>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required>
    </div>

    <div>
        <button type="submit">Login</button>
    </div>
</form>

<!-- Display error or success messages -->
<div id="responseMessage"></div>

<script>
    // Event listener for form submission
    document.getElementById("loginForm").addEventListener("submit", function(event) {
        event.preventDefault(); // Prevent default form submission

        var form = this;

        // Create an object to hold form data
        var loginData = {
            username: form.username.value,
            password: form.password.value
        };

        // Send the data as JSON via AJAX
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "http://localhost:8080/cab/api/customers/login", true);
        xhr.setRequestHeader("Content-Type", "application/json");

        // Handle the response
        xhr.onload = function() {
            if (xhr.status === 200) {
                var customer = JSON.parse(xhr.responseText);
                document.getElementById("responseMessage").innerHTML = "Login successful! Welcome, " + customer.name;
            } else if (xhr.status === 400) {
                var errorResponse = JSON.parse(xhr.responseText);
                document.getElementById("responseMessage").innerHTML = errorResponse.error;
            } else if (xhr.status === 401) {
                var errorResponse = JSON.parse(xhr.responseText);
                document.getElementById("responseMessage").innerHTML = errorResponse.error;
            } else {
                document.getElementById("responseMessage").innerHTML = "An unexpected error occurred.";
            }
        };

        // Handle network errors
        xhr.onerror = function() {
            document.getElementById("responseMessage").innerHTML = "Network error occurred.";
        };

        // Send the data as a JSON string
        xhr.send(JSON.stringify(loginData));
    });
</script>

</body>
</html>
