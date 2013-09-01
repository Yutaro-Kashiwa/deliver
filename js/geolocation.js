$(function() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            show_location, show_error);
    }
    else {
        alert('not supported');
    };
});

function show_location(position) {
    var lat = position.coords.latitude;
    var lon = position.coords.longitude;
    $('#latlon').val(lat + ' ' + lon);
}

function show_error(error) {
    $('#latlon').val(error);
}