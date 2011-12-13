/**
 * Event handler called by video tags (onError event handler).
 */
function failed(e) {
    // video playback failed - show a message saying why
    var parentNode = e.target.parentNode;
    parentNode.removeChild(e.target);
    if (e.target instanceof HTMLVideoElement) {
        var message = "";
        switch (e.target.error.code) {
            case e.target.error.MEDIA_ERR_ABORTED:
                message = 'You aborted the video playback.';
                break;
            case e.target.error.MEDIA_ERR_NETWORK:
                message = 'A network error caused the video download to fail part-way.';
                break;
            case e.target.error.MEDIA_ERR_DECODE:
                message = 'The video playback was aborted due to a corruption problem or because the video used features your browser did not support.';
                break;
            case e.target.error.MEDIA_ERR_SRC_NOT_SUPPORTED:
                message = 'The video could not be loaded, either because the server or network failed or because the format is not supported.';
                break;
            default:
                message = 'An unknown error occurred.';
                break;
        }
        // Open error dialog
        var errorDialog = $("#errorDialog");
        if (!! errorDialog) {
            errorDialog.dialog('open');
        }
    }
}