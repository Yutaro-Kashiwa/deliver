$(function() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            show_location, show_error);
    }
    else {
        alert("not supported");
    };
});

function show_location(position) {
    var latitude = position.coords.latitude;
    var longitude = position.coords.longitude;
    $('#msg').val(latitude + " " + longitude);
}

function show_error(error) {
    $('#msg').val(error);
}