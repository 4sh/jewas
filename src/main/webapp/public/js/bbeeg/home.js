var labelType, useGradients, nativeTextSupport, animate;

(function () {
    var ua = navigator.userAgent,
        iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
        typeOfCanvas = typeof HTMLCanvasElement,
        nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
        textSupport = nativeCanvasSupport
            && (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
    //I'm setting this based on the fact that ExCanvas provides text support for IE
    //and that as of today iPhone/iPad current text support is lame
    labelType = (!nativeCanvasSupport || (textSupport && !iStuff)) ? 'Native' : 'HTML';
    nativeTextSupport = labelType == 'Native';
    useGradients = nativeCanvasSupport;
    animate = !(iStuff || !nativeCanvasSupport);
})();

function initialize() {
    //init data
    var json = "";
    var result = $.getJSON('/domain/hierarchy', function (domainsJson) {
        json = domainsJson;
        renderSiteKnowledgeMap();
    });

    //end

    function renderSiteKnowledgeMap() {

        var infovis = document.getElementById('knowledgeMap');
        var w = infovis.offsetWidth - 50, h = infovis.offsetHeight - 50;

        //init Hypertree
        var ht = new $jit.Hypertree({
            //id of the visualization container
            injectInto:'knowledgeMap',
            //canvas width and height
            width:w,
            height:h,
            //Change node and edge styles such as
            //color, width and dimensions.
            Node:{
                dim:9,
                color:"#434342"
            },
            Edge:{
                lineWidth:2,
                color:"#1099a3"
            },
            //Attach event handlers and add text to the
            //labels. This method is only triggered on label
            //creation
            onCreateLabel:function (domElement, node) {
                domElement.innerHTML = node.name;
                $jit.util.addEvent(domElement, 'click', function () {
                    ht.onClick(node.id, {
                        onComplete:function () {
                            ht.controller.onComplete();
                        }
                    });
                });
            },
            //Change node styles when labels are placed
            //or moved.
            onPlaceLabel:function (domElement, node) {
                var style = domElement.style;
                style.display = '';
                style.cursor = 'pointer';
                if (node._depth <= 1) {
                    style.fontSize = "0.8em";
                    style.color = "#434342";

                } else if (node._depth == 2) {
                    style.fontSize = "0.7em";
                    style.color = "#555";

                } else {
                    style.display = 'none';
                }

                var left = parseInt(style.left);
                var w = domElement.offsetWidth;
                style.left = (left - w / 2) + 'px';
            }

        });
        //load JSON data.
        ht.loadJSON(json);
        //compute positions and plot.
        ht.refresh();
        //end
        ht.controller.onComplete();
    }
}