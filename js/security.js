function s(str) {
  var temp = document.createElement('div');
	temp.textContent = str;
	return temp.innerHTML;
}

function checkLogin() {
  var data = {};
  $.post("/isLoggedIn", data, function(data) {
    var received = JSON.parse(data);
  });
  if (received.username = "") window.location.replace("./index.html");
}
