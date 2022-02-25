const THEME_KEY = "theme";
const THEME_AUTO = "auto";
const THEME_DARK = "dark";
const THEME_LIGHT = "light";
const THEME_ATTRIBUTE = "data-theme";
const COLOR_SCHEME_QUERY = "(prefers-color-scheme: dark)";

updateTheme();
document.addEventListener("DOMContentLoaded", onDocumentReady);
window.matchMedia(COLOR_SCHEME_QUERY)
    .addEventListener("change", updateTheme);

function updateTheme() {
    let theme = getUserThemeSelection();
    if (theme === THEME_AUTO) theme = getSystemTheme();
    document.documentElement.setAttribute(THEME_ATTRIBUTE, theme);
}

function getUserThemeSelection() {
    const defaultTheme = THEME_LIGHT;
    const userSelection = localStorage.getItem(THEME_KEY);
    return userSelection === null ? defaultTheme : userSelection;
}

function getSystemTheme() {
    const isDark = window.matchMedia(COLOR_SCHEME_QUERY).matches;
    return isDark ? THEME_DARK : THEME_LIGHT;
}

function onDocumentReady() {
    setThemeButtonInitialIcon();
    let toggleButton = document.getElementById("theme-switch");
    toggleButton.style.visibility = "visible";
    // NOTE: Setting click listener here sometimes did not work in browsers.
    //  So, the onclick attribute is set on the element in the HTML.
}

function setThemeButtonInitialIcon() {
    let theme = getUserThemeSelection();
    if (theme === THEME_AUTO) {
        setThemeButtonIconToAuto();
    } else if (theme === THEME_DARK) {
        setThemeButtonIconToDark();
    } else /* if (theme === THEME_LIGHT) */ {
        // Do nothing and show the icon as is
    }
}

// See https://stackoverflow.com/q/48316611
function toggleTheme() {
    let theme = getUserThemeSelection();
    if (theme === THEME_AUTO) {
        localStorage.setItem(THEME_KEY, THEME_LIGHT);
        animateThemeButtonIconToLight();
    } else if (theme === THEME_DARK) {
        localStorage.setItem(THEME_KEY, THEME_AUTO);
        animateThemeButtonIconToAuto();
    } else /* if (theme === THEME_LIGHT) */ {
        localStorage.setItem(THEME_KEY, THEME_DARK);
        animateThemeButtonIconToDark();
    }
    updateTheme();
}

function animateThemeButtonIconToLight() {
    document.getElementById("letter-anim-hide").beginElement();
    document.getElementById("core-anim-shrink").beginElement();
    document.getElementById("rays-anim-show").beginElement();
    document.getElementById("rays-anim-rotate").beginElement();
}

function animateThemeButtonIconToAuto() {
    document.getElementById("eclipse-anim-go").beginElement();
    document.getElementById("letter-anim-show").beginElement();
}

function animateThemeButtonIconToDark() {
    document.getElementById("core-anim-enlarge").beginElement();
    document.getElementById("rays-anim-hide").beginElement();
    document.getElementById("eclipse-anim-come").beginElement();
}

function setThemeButtonIconToAuto() {
    document.getElementById("circle").setAttribute("r", "10");
    document.getElementById("rays").setAttribute("opacity", "0");
    document.getElementById("letter").setAttribute("stroke-dashoffset", "0");
}

function setThemeButtonIconToDark() {
    document.getElementById("circle").setAttribute("r", "10");
    document.getElementById("rays").setAttribute("opacity", "0");
    document.getElementById("eclipse").setAttribute("cx", "20");
}
