window.onload = () => {
    videojs.Vhs.xhr.beforeRequest = (options) => {
        options.headers = {
            Authorization: 'TODO'
        }

        return options
    }

    var player = videojs('video')
    player.src({
        src : '/hls/test.m3u8',
        type: 'application/x-mpegurl'

    })
}