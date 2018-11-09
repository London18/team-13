function s(str) {
  var temp = document.createElement('div');
	temp.textContent = str;
	return temp.innerHTML;
}

function checkLogin() {
  var data = {};
  $.post("/isLoggedIn", data, function(data) {
    var loggedIn = JSON.parse(data);
  });
  if (!loggedIn) window.location.replace("./index.html");
}
