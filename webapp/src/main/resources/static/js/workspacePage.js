window.addEventListener('load', function() {
    var modal = document.getElementById("addChannelModal");
    var btn = document.getElementById("addChannelButton");
    var span = document.getElementsByClassName("addChannelClose")[0];
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