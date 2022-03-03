const THEME_KEY = "theme";
const THEME_AUTO = "auto";
const THEME_DARK = "dark";
const THEME_LIGHT = "light";
const THEME_DEFAULT = THEME_LIGHT;
const COLOR_SCHEME_DARK = "(prefers-color-scheme: dark)";

hljs.highlightAll(); // Highlight the code snippets
updateCodeSnippetsThemeIfNecessary();

/* Check the theme and if it is changed, enable the proper style */
function updateCodeSnippetsThemeIfNecessary() {
  let darkStyleLink = document.querySelector(`link[title="dark-code"]`);
  let lightStyleLink = document.querySelector(`link[title="light-code"]`);
  let theme = getUserThemeSelection();
  if (theme === THEME_AUTO) theme = getSystemTheme();
  if (theme === THEME_DARK) {
    darkStyleLink.removeAttribute("disabled");
    lightStyleLink.setAttribute("disabled", "disabled");
  } else {
    darkStyleLink.setAttribute("disabled", "disabled");
    lightStyleLink.removeAttribute("disabled");
  }
  setTimeout(updateCodeSnippetsThemeIfNecessary, 100);
}

function getUserThemeSelection() {
  const userSelection = localStorage.getItem(THEME_KEY);
  return userSelection === null ? THEME_DEFAULT : userSelection;
}

function getSystemTheme() {
  const isDark = window.matchMedia(COLOR_SCHEME_DARK).matches;
  return isDark ? THEME_DARK : THEME_LIGHT;
}
