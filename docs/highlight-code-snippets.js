hljs.highlightAll();

if (window.matchMedia) {
  // If media queries are supported...
  updateCodeSnippetsTheme();
  window
      .matchMedia("(prefers-color-scheme: dark)")
      .addEventListener("change", updateCodeSnippetsTheme);
}

function updateCodeSnippetsTheme() {
  let darkStyleLink = document.querySelector(`link[title="dark-code"]`);
  let lightStyleLink = document.querySelector(`link[title="light-code"]`);
  if (window.matchMedia("(prefers-color-scheme: dark)").matches) {
    darkStyleLink.removeAttribute("disabled");
    lightStyleLink.setAttribute("disabled", "disabled");
  } else {
    darkStyleLink.setAttribute("disabled", "disabled");
    lightStyleLink.removeAttribute("disabled");
  }
}
