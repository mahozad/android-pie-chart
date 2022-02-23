const THEME_KEY = "theme";
const THEME_ATTR = "data-theme";
const THEME_DARK = "dark";
const THEME_AUTO = "auto";
const THEME_LIGHT = "light";

updateTheme();
// Wait for the document to be ready: https://stackoverflow.com/a/800010
document.addEventListener("DOMContentLoaded", onDocumentReady);
window
    .matchMedia("(prefers-color-scheme: dark)")
    .addEventListener("change", updateTheme);

function updateTheme() {
    let theme = getUserThemeSelection();
    if (theme === THEME_AUTO) theme = getSystemTheme();
    document.documentElement.setAttribute(THEME_ATTR, theme);
}

function getUserThemeSelection() {
    const defaultTheme = THEME_LIGHT;
    const userSelection = localStorage.getItem(THEME_KEY);
    return userSelection === null ? defaultTheme : userSelection;
}

function getSystemTheme() {
    if (window.matchMedia("(prefers-color-scheme: dark)").matches) {
        return THEME_DARK;
    } else {
        return THEME_LIGHT;
    }
}

// See https://stackoverflow.com/q/48316611
function toggleTheme() {
    let oldTheme = getUserThemeSelection();
    let newTheme;
    if (oldTheme === THEME_AUTO) {
        newTheme = THEME_LIGHT;
        animateButtonIconToLight();
    } else if (oldTheme === THEME_DARK) {
        newTheme = THEME_AUTO;
        animateButtonIconToAuto();
    } else /* if (theme === THEME_LIGHT) */ {
        newTheme = THEME_DARK;
        animateButtonIconToDark();
    }
    localStorage.setItem(THEME_KEY, newTheme);
    updateTheme();
}

function animateButtonIconToLight() {
    document.getElementById("letter-anim-hide").beginElement();
    document.getElementById("core-anim-shrink").beginElement();
    document.getElementById("rays-anim-show").beginElement();
    document.getElementById("rays-anim-rotate").beginElement();
}

function animateButtonIconToAuto() {
    document.getElementById("eclipse-anim-go").beginElement();
    document.getElementById("letter-anim-show").beginElement();
}

function animateButtonIconToDark() {
    document.getElementById("core-anim-enlarge").beginElement();
    document.getElementById("rays-anim-hide").beginElement();
    document.getElementById("eclipse-anim-come").beginElement();
}

function onDocumentReady() {
    setThemeButtonInitialIcon();
    const toggleButton = document.getElementById("theme-switch");
    toggleButton.style.visibility = "visible";
    toggleButton.addEventListener("click", toggleTheme);
}

function setThemeButtonInitialIcon() {
    let theme = getUserThemeSelection();
    if (theme === THEME_AUTO) {
        document.getElementById("circle").setAttribute("r", "10");
        document.getElementById("rays").setAttribute("opacity", "0");
        document.getElementById("letter").setAttribute("opacity", "1");
    } else if (theme === THEME_DARK) {
        document.getElementById("circle").setAttribute("r", "10");
        document.getElementById("rays").setAttribute("opacity", "0");
        document.getElementById("eclipse").setAttribute("cx", "20");
    } else /* if (theme === THEME_LIGHT) */ {
        // Do nothing and show the icon as is
    }
}
