var map = new BMap.Map("container");
$(document).ready(function () {
    let point = new BMap.Point(116.404, 39.915);
    map.centerAndZoom(point, 15);
    map.enableScrollWheelZoom(true);
});