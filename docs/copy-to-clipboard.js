$(document).on("click", ".copy", function () {
  let button = $(this);
  let code = button.next().text();
  navigator
      .clipboard
      .writeText(code)
      .then(() => {
          button.text("✓"); // OR Emoji which cannot be colored: ✔️
          button.addClass("copy-success");
          window.setTimeout(() => {
              button.removeClass("copy-success");
              button.text("copy");
          }, 750);
      },
            () => alert("Failed to copy! Check for clipboard permissions.")
      );
});
