// https://stackoverflow.com/q/43262121/
// https://stackoverflow.com/q/36975619

const URL = "https://api.github.com/repos/mahozad/android-pie-chart/releases/latest"

fetch(URL)
  .then(response => response.json())
  .then(jsonResponse => {
    const version = jsonResponse.tag_name.substring(1);
    document.body.innerHTML = document.body.innerHTML.replace("&lt;version&gt;", version);
  });
