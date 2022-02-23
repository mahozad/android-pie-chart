updateTheme();

function detectTheme() {
    const storedTheme = localStorage.getItem("theme");
    // Default to light theme
    const theme = { mode: "custom", name: "light" }
    if (storedTheme === "auto") {
        theme.name = getSystemTheme();
        theme.mode = "auto";
    } else if (storedTheme === "dark") {
        theme.name = "dark";
        theme.mode = "custom";
    }
    return theme;
}

function getSystemTheme() {
    if (window.matchMedia("(prefers-color-scheme: dark)").matches) {
        return "dark";
    } else {
        return "light";
    }
}

function updateTheme() {
    let theme = detectTheme();
    if (theme.name === "dark") {
        document.documentElement.setAttribute("data-theme", "dark");
    } else {
        document.documentElement.setAttribute("data-theme", "light");
    }
}

// See https://stackoverflow.com/q/48316611
function toggleTheme() {
    let currentTheme = detectTheme();
    if (currentTheme.mode === "auto") {
        currentTheme.mode = "custom";
        currentTheme.name = "light";
        document.getElementById("anim6").beginElement()
        document.getElementById("anim7").beginElement()
        document.getElementById("anim8").beginElement()
        document.getElementById("anim9").beginElement()
    } else if (currentTheme.name === "dark") {
        currentTheme.mode = "auto";
        currentTheme.name = "-";
        document.getElementById("anim4").beginElement()
        document.getElementById("anim5").beginElement()
    } else {
        currentTheme.mode = "custom";
        currentTheme.name = "dark";
        document.getElementById("anim1").beginElement()
        document.getElementById("anim2").beginElement()
        document.getElementById("anim3").beginElement()
    }
    localStorage.setItem("theme", currentTheme.mode === "auto" ? "auto" : currentTheme.name);
    updateTheme();
}

window
    .matchMedia("(prefers-color-scheme: dark)")
    .addEventListener("change", updateTheme);

// Wait for the document to be ready: https://stackoverflow.com/a/800010
document.addEventListener("DOMContentLoaded", () => {
    let currentTheme = detectTheme();
    if (currentTheme.mode === "auto") {
        document.getElementById("circle").setAttribute("r", "10")
        document.getElementById("rays").setAttribute("opacity", "0");
        document.getElementById("letter").setAttribute("opacity", "1");
    } else if (currentTheme.name === "dark") {
        document.getElementById("circle").setAttribute("r", "10");
        document.getElementById("rays").setAttribute("opacity", "0");
        document.getElementById("eclipse").setAttribute("cx", "20");
    }
    const toggleButton = document.getElementById("theme-switch")
    toggleButton.style.visibility = "visible";
    toggleButton.addEventListener("click", toggleTheme);
});
