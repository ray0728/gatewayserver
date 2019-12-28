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

function onApiLoaded() {
    let map = new AMap.Map('voice_map', {
        center: [117.000923, 36.675807],
        zoom: 6,
        mapStyle: "amap://styles/whitesmoke"
    });
    map.plugin(["AMap.ToolBar"], function () {
        map.addControl(new AMap.ToolBar());
    });
}
