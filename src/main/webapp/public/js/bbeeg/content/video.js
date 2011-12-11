/**
 * Event handler called by video tags (onError event handler).
 * The current video tag source video cannot be played due to codec error
 * and so, this error handler just remove the source and display an error message.
 */
function fallback() {
    var video = $('#videoTagId')[0];
    while (video.firstChild) {
        if (video.firstChild instanceof HTMLSourceElement) {
            video.removeChild(video.firstChild);
        } else {
            video.parentNode.insertBefore(video.firstChild, video);
        }
    }
    video.parentNode.removeChild(video);
}