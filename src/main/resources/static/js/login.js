$(document).ready( function() {

  $("#login").submit(function(e) {

    e.preventDefault();

    var username = s($(this.elements['username']).val());
    var password = s($(this.elements['password']).val());

    var data = {'username': username, 'password': password};

    $.post("/login", data, function(data) {
      var received = JSON.parse(data);
    });

    console.log(received.result);
    if (typeof received !== 'undefined' && received.result) {
      alert('Success');
      $(location).attr('href', "../../templates/home.html");
    } $(location).attr('href', "../../templates/index.html");

  });
});
