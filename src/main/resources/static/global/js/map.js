var amap, amarker;
var ClockHashMap = function () {
    let size = 0;
    let entry = new Object();
    this.put = function (key, value) {
        if (!this.containsKey(key)) {
            size++;
            entry[key] = value;
        }
    };
    this.get = function (key) {
        return this.containsKey(key) ? entry[key] : null;
    };
    this.remove = function (key) {
        if (this.containsKey(key) && clearInterval(entry[key])) {
            delete entry[key];
            size--;
        }
    };
    this.containsKey = function (key) {
        return (key in entry);
    }
};
var xhr_upload_clock = new ClockHashMap();
$(document).ready(function () {
    var swiper = new Swiper('.swiper-container', {
        slidesPerView: 3,
        spaceBetween: 30,
        slidesPerGroup: 3,
        loop: true,
        loopFillGroupWithBlank: true,
        pagination: {
            el: '.swiper-pagination',
            clickable: true,
        },
        navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev',
        },
    });
    let url = 'https://webapi.amap.com/maps?v=1.4.15&key=15af621c64962e63c36080f73f44cf8b&callback=onApiLoaded';
    let jsapi = document.createElement('script');
    jsapi.charset = 'utf-8';
    jsapi.src = url;
    document.head.appendChild(jsapi);
});
onApiLoaded = function () {
    amap = new AMap.Map('voice_map', {
        center: [117.000923, 36.675807],
        zoom: 6,
        mapStyle: "amap://styles/whitesmoke"
    });
    xhr_upload_clock.put("autodevices", setInterval(function () {
        autoRefreshDevices();
    }, 3000));
};

addPOIMarker = function (name, location) {
    if (amarker) {
        amarker.setPosition(location);
    } else {
        amarker = new AMap.Marker({
            position: new AMap.LngLat(location)
        });
        amap.add(amarker);
    }
    xhr_upload_clock.remove("autolocation");
    xhr_upload_clock.put("autolocation", setInterval(function () {
        autoRefreshLocation(name);
    }, 1000));
};

autoRefreshLocation = function(name) {
    $.get("/rst/lab/voice?name=" + $.base64.encode(name), function (data, status) {
        if (data != "") {
            let location = JSON.parse(data);
            alert(location[0]);
            amarker.setPosition(location[0]);
        }
    });
};

autoRefreshDevices = function(){
    $('#voice_devices').load("/lab/voice/devices");
}
