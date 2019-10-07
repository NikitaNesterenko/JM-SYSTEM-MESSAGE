function addChannelFunction() {
    var modal = document.getElementById("addChannelModal");
    var btn = document.getElementById("addChannelBtn");
    var span = document.getElementsByClassName("addChannelClose")[0];
    btn.onclick = function() {
        modal.style.display = "block";
    };
    span.onclick = function() {
        modal.style.display = "none";
    };
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }
}

/*
$(document).ready(function(){
    $("#addChannelBtn").click(function(){
        $("#addChannelModal").modal('show');

    });
});
*/
