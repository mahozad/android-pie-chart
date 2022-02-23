hljs.highlightAll(); // Highlight the code snippets
updateCodeSnippetsThemeIfNecessary();

/* Check the theme and if it is changed, enable the proper style */
function updateCodeSnippetsThemeIfNecessary() {
  let currentTheme = detectTheme();
  let darkStyleLink = document.querySelector(`link[title="dark-code"]`);
  let lightStyleLink = document.querySelector(`link[title="light-code"]`);
  if (currentTheme.name === "dark") {
    darkStyleLink.removeAttribute("disabled");
    lightStyleLink.setAttribute("disabled", "disabled");
  } else {
    darkStyleLink.setAttribute("disabled", "disabled");
    lightStyleLink.removeAttribute("disabled");
  }
  setTimeout(updateCodeSnippetsThemeIfNecessary, 100);
}
