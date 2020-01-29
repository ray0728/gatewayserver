var amap, amarker, swiper, videoid;
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
    initSwiper();
    let url = 'https://webapi.amap.com/maps?v=1.4.15&key=15af621c64962e63c36080f73f44cf8b&callback=onApiLoaded';
    let jsapi = document.createElement('script');
    jsapi.charset = 'utf-8';
    jsapi.src = url;
    document.head.appendChild(jsapi);
    $.base64.utf8encode = true;
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

addPOIMarker = function (name) {
    if (!amarker) {
        amarker = new AMap.Marker({
            title: name
        });
    } else {
        amarker.setTitle(name);
    }
    amap.setZoom(15);
    xhr_upload_clock.remove("autolocation");
    xhr_upload_clock.put("autolocation", setInterval(function () {
        autoRefreshLocation(name);
    }, 1000));
};

autoRefreshLocation = function (name) {
    $.get("/rst/lab/voice?name=" + $.base64.encode(name), function (data, status) {
        if (data != "") {
            let location = JSON.parse(data);
            amarker.setPosition(location);
            amap.add(amarker);
            amap.setCenter(location);
        }
    });
};

autoRefreshDevices = function () {
    $('#voice_devices').load("/lab/voice/devices", function (response, status, xhr) {
        initSwiper();
    });
};

initSwiper = function () {
    swiper = new Swiper('.swiper-container', {
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
};

$('#videomodal').on('show.bs.modal', function (e) {
    initVideo(videoid);
});

$('#videomodal').on('hidden.bs.modal', function (e) {
    $('.modal-div').empty();
});

playVideo = function (url) {
    videoid = generateVideoId(url.length);
    let video = $('<video controls>')
        .attr("id", videoid);
    video.addClass('video-js');
    video.addClass('vjs-big-play-centered');
    video.html('<source src="' + url + '">');
    $('.modal-div').append(video);
    $("#videomodal").modal('show');
};

generateVideoId = function (length) {
    return 'v' + Number(Math.random().toString().substr(3, length) + Date.now()).toString(36);
};

initVideo = function (id) {
    let options = {
        fluid: true,
        controls: true,
        preload: 'auto',
        controlBar: {
            volumePanel: {
                inline: false
            },
            remainingTimeDisplay: false
        }
    };
    videojs(id, options);

};