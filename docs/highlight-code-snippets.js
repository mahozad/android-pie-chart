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
