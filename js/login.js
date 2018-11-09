$(document).ready( function() {

  $("#login").submit(function(e) {
    var username = s($(this.elements['username']).val());
    var password = s($(this.elements['password']).val());

    var data = {'username': username, 'password': password};

    $.post("/login", data, function(data) {
      var received = JSON.parse(data);
    });

    e.preventDefault();

    if (received) window.location.replace("./home.html");
    window.location.replace("./index.html");

  }
}
