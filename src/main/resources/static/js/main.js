function transactionGroup (data) {
    this.transactions = data;
}

function transaction (date, amount, category) {
    this.date = date;
    this.amount = amount;
    this.category = category;

    this.getButtonHtml = function (transactionObject) {
            return "<div class=\"transactionButton\">"
                    + "<span class=\"transactionButtonDate\">" + transactionObject.date + "</span>"
                    + "<span class=\"transactionButtonAmount\">" + transactionObject.amount + "</span>"
                    + "<span class=\"transactionButtonCategory\">" + transactionObject.category + "</span>"
                    + "</div>";};
}

function getHtmlTransactionButton(transactionObject) {
    return "<div class=\"transactionButton\">"
            + "<span class=\"transactionButtonDate\">" + transactionObject.date + "</span>"
            + "<span class=\"transactionButtonAmount\">" + transactionObject.amount + "</span>"
            + "<span class=\"transactionButtonCategory\">" + transactionObject.category + "</span>"
            + "</div>";
}

function getHtmlTransactionButtons(transactionGroupObject) {
    buttonHtml = "<div class=\"transactionButtons>";
    for (let t of transactionGroupObject.transactions) {
        buttonHtml = buttonHtml + getHtmlTransactionButton(t) + "<br />";
    }
    buttonHtml = buttonHtml + "</div>";
    return buttonHtml
}

function setTransactionGroupWithHandler(date, transactionGroup, handler) {
    var newJson = JSON.stringify(transactionGroup);
    $.post(
        "set-transaction-group",
        {
            unparsedDate: date,
            password: sessionStorage.password,
            json: newJson
        }
    ).done(handler);
}

function getTransactionGroupWithHandler(date, handler) {
    $.post(
        "get-transaction-group",
        {
            unparsedDate: date,
            password: sessionStorage.password
        }
    ).done(handler);
}

function createNewTransactionGroup(date, handler) {
    return setTransactionGroupWithHandler(date,
                                          new transactionGroup(date, []),
                                          handler);
}

function getHtmlTransactionGroupButton(date) {
    return "<div class=\"headerbutton\">" + date + "</div>";
}

function getHtmlNewTransactionGroupButton() {
    return "<input type=\"text\" id=\"newTransactionGroupDate\"><div id=\"newTransactionGroupButton\" class=\"newTransactionGroupButton\">New transaction group</div>";
}

function createTransactionGroupHeaders(transactionGroupHeadersContainer) {
    $(".headerbutton").remove();
    $.post("get-transaction-group-headers", function(data){
        for (let tg of JSON.parse(data).dates) {
            transactionGroupHeadersContainer.append(getHtmlTransactionGroupButton(tg));
        }
    });
}

function createNewTransactionGroupButton(newTransactionGroupButtonContainer,
                                         transactionGroupHeadersContainer) {
    newTransactionGroupButtonContainer.append(getHtmlNewTransactionGroupButton());
    newTransactionGroupButtonContainer.find("#newTransactionGroupDate").change(function(){
        var field = $("#newTransactionGroupDate");
        var oldDate = field.val();
        $.post("round-date", {unparsedDate:oldDate})
                .done(function(data){
                    field.val(data)
                });
    });
    newTransactionGroupButtonContainer.find("#newTransactionGroupButton").click(function(){
        createNewTransactionGroup(
                $("#newTransactionGroupDate").val(),
                function(data){
                    alert(data);
                    createTransactionGroupHeaders(transactionGroupHeadersContainer)
                });
    });
}

function activate() {
    $("#loginform").attr("hidden", true);
    createTransactionGroupHeaders($("#transactiongroupheaders"));
    createNewTransactionGroupButton($("#transactiongroupheaders"), $("#transactiongroupheaders"));
}

$(document).ready (function(){
    $("#needjavascript").hide();
    $.post("test-session", function(username) {
        if (username === "") {
            $("#loginform")
                .attr("hidden", false)
                .submit(function() {
                    var u = $("#usernameinp").val()
                    var p = $("#passwordinp").val()
                    $.post(
                        "login", {
                            username:u,
                            passwordAttempt:p
                        }).done(function(data){
                            if (data == "success") {
                                sessionStorage.setItem("password", p);
                                activate();
                            }
                        })
                    return false;
                });
        } else {
            activate("Already logged in");
        }


    });
});