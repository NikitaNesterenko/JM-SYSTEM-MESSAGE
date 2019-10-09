window.addEventListener('load', function() {
    const modal = document.getElementById("addChannelModal");
    const btn = document.getElementById("addChannelButton");
    const span = document.getElementsByClassName("addChannelClose")[0];
    btn.onclick = function() {
        modal.style.display = "block";
    };
    span.onclick = function() {
        modal.style.display = "none";
    };
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    }
});

$(document).ready(function () {
    $.getJSON("http://localhost:8080/channel", function(data) {
        $.each(data, function(i, item) {
            console.log(item.name)
            $('#channel-box').append('<p><a href="" class="channel-link">' + item.name + '</a>');
        });
    });
});