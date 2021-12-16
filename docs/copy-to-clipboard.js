$(document).on("click", ".copy", function () {
  let button = $(this);
  let code = button.next().text();
  navigator
      .clipboard
      .writeText(code)
      .then(() => {
          button.text("✔️");
          window.setTimeout(() => button.text("copy"), 750);
      },
            () => alert("Failure to copy. Check for clipboard permissions")
      );
});
