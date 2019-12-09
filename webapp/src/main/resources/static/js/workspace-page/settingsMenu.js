const btn = document.getElementById('settingsMenuButton');
const modal = document.getElementById('settingsList');

window.addEventListener('load', function () {
    // populate select
    let isActive = false;

    btn.onclick = function () {
        if (isActive === false) {
            isActive = true;
            modal.style.display = "block";
        } else {
            isActive = false;
            modal.style.display = "none";
        }
        return false;
    };
});