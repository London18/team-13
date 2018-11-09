$(document).ready( function() {

  checkLogin();

  var date = new Date();

  var data = {'date': date.toJSON()};

  $.post("/getSchedule", data, function(data) {
    var schedule = JSON.parse(data);
  });

  var submitted = false;

  $(".action").submit(function(e) {

    if (!submitted) {

      submitted = true;

      var action = s($(this.elements['action']).val());
      var comment = s($(this.elements['comment']).val());
      var sid = s($(this.elements['sid']).val());

      var data = {'action': username, 'comment': password};

      $.post("/postAction", data, function(data) {
        var received = JSON.parse(data);
      });

      $(this).parent.hide().html("<p>Action submitted</p>").fadeIn(500);

      e.preventDefault();

    }
    
}
