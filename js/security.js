function s(str) {
  var temp = document.createElement('div');
	temp.textContent = str;
	return temp.innerHTML;
}

function checkLogin() {
  var data = {};
  $.post("/isLoggedIn", data, function(data) {
    var username = JSON.parse(data);
  });
  if (username = "") window.location.replace("./index.html");
}
