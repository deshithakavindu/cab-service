// theme.js
document.addEventListener("DOMContentLoaded", () => {
    const themeToggle = document.getElementById("themeToggle");
    const body = document.body;

    // Load saved theme preference
    if (localStorage.getItem("theme") === "light") {
        body.classList.add("light-theme");
        themeToggle.textContent = "☀️";
    } else {
        themeToggle.textContent = "🌙";
    }

    // Toggle theme on click
    themeToggle.addEventListener("click", () => {
        body.classList.toggle("light-theme");
        const isLight = body.classList.contains("light-theme");
        localStorage.setItem("theme", isLight ? "light" : "dark");
        themeToggle.textContent = isLight ? "☀️" : "🌙";
    });
});