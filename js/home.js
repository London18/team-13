$(document).ready( function() {

  checkLogin();

  var date = new Date();

  var data = {'date': date.toJSON()};

  $.post("/getSchedule", data, function(data) {
    var schedule = JSON.parse(data);
  });

}
