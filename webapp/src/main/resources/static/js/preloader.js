window.onload = function() {
    let preloader = document.getElementById('preloader');
    preloader.style.opacity = '0';
    setTimeout(function () {
        preloader.style.display = 'none';
    }, 300)

};