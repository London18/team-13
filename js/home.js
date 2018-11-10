$(document).ready( function() {

  checkLogin();

  var date = new Date();

  var data = {};

  $.post("/getSchedule", data, function(data) {
    var received = JSON.parse(data);
  });

  if (typeof received !== 'undefined') {

    var html;

    for (i in received.schedule) {
      html += "<tr id='" + s(received.schedule[i].sid) + "'><td>" + s(received.schedule[i].fname) + "</td>";
      html += "<td>" + s(received.schedule[i].start) + "</td>";
      html += "<td>" + s(received.schedule[i].end) + "</td>";
      html += "<td>" + s(received.schedule[i].faddr) + "</td>";
      html += "<td class='status'>" + s(received.schedule[i].status) + "</td></tr>";
      html += "<td onclick='openComment()''>Update Status</td>";
    }

    document.getElementById('schedule_entries').append(html);

  }

  received = null;

  $.post("/getActivity", data, function(data) {
    received = JSON.parse(data);
  });

  if (typeof received !== 'undefined' && received !== null) {

    html = "";

    for (i in received.activity) {
      html += "<tr id='" + s(received.activity[i].vid) + "'><td>" + s(received.activity[i].time) + "</td>";
      html += "<td>" + s(received.activity[i].action) + "</td>";
      html += "<td>" + s(received.activity[i].comments) + "</td></tr>";
    }

    document.getElementById('activity_entries').append(html);

  }

  $(".action").submit(function(e) {

    e.preventDefault();

    var action = s($(this.elements['action']).val());
    var comment = s($(this.elements['comment']).val());
    var sid = s($(this.elements['sid']).val());
    received = null;

    var data = {'action': username, 'comment': password, 'sid': sid};

    $.post("/postAction", data, function(data) {
      received = JSON.parse(data);
    });

    if (typeof received !== 'undefined' && received !== null) $(this).parent.hide().html("<p>Action submitted</p>").fadeIn(500);
    else $(this).parent.hide().html("<p>Action failed to submit</p>").fadeIn(500);

  });

  $("#logout").submit(function(e) {

    received = null;
    var data = {};
    $.post("/logout", data, function(data) {
      var received = JSON.parse(data);
    });

    window.location.replace("./index.html");

  });

});
