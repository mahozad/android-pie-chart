
$(document).on("click", ".copy", function () {
  var button = $(this);
  var code = button.next().text();
  navigator
      .clipboard
      .writeText(code)
      .then(function () {
          button.text("✔️");
          window.setTimeout(function () { button.text("copy") }, 750);
      },
            function () { alert("Failure to copy. Check for clipboard permissions"); }
      );
});
