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
  //if (typeof received == 'undefined' || received.username == "") $(location).attr('href', "./index.html");
}

function logout() {
  var received;
  var data = {};
  $.post("/logout", data, function(data) {
    var received = JSON.parse(data);
  });
  //window.location.replace("./index.html");
}
