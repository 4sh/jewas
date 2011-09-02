/**
 * Ajax upload
 * Project page - http://valums.com/ajax-upload/
 * Copyright (c) 2008 Andris Valums, http://valums.com
 * Licensed under the MIT license (http://valums.com/mit-license/)
 * Version 3.6 (26.06.2009)
 * PATCHED BY XAVIER HANIN 2009-12-28
 * -- add setAction(action) method to allow dynamic configuration of form action
 *  
   // PATCHED BY XAVIER HANIN 2009-12-28 - START
    setAction : function(action){
        this._settings.action = action;
    },
    // PATCHED BY XAVIER HANIN 2009-12-28 - END
 * 
 * -- removed getOffset function, use one from jquery: less code + bug free
 *
 * PATCHED BY Damien RICCIO 2011-09-02
 * -- add getCurrentFileExtension() method to get the extension of the current file
 * -- add possibility to give a callback in submit method. This callback will be executed
 *    a the end of the upload, but after the onComplete call.
 */

/**
 * Changes from the previous version:
 * 1. Fixed minor bug where click outside the button
 * would open the file browse window
 * 
 * For the full changelog please visit: 
 * http://valums.com/ajax-upload-changelog/
 */

(function(){
    
var d = document, w = window;

/**
 * Get element by id
 */ 
function get(element){
    if (typeof element == "string")
        element = d.getElementById(element);
    return element;
}

/**
 * Attaches event to a dom element
 */
function addEvent(el, type, fn){
    if (w.addEventListener){
        el.addEventListener(type, fn, false);
    } else if (w.attachEvent){
        var f = function(){
          fn.call(el, w.event);
        };          
        el.attachEvent('on' + type, f)
    }
}


/**
 * Creates and returns element from html chunk
 */
var toElement = function(){
    var div = d.createElement('div');
    return function(html){
        div.innerHTML = html;
        var el = div.childNodes[0];
        div.removeChild(el);
        return el;
    }
}();

function hasClass(ele,cls){
    return ele.className.match(new RegExp('(\\s|^)'+cls+'(\\s|$)'));
}
function addClass(ele,cls) {
    if (!hasClass(ele,cls)) ele.className += " "+cls;
}
function removeClass(ele,cls) {
    var reg = new RegExp('(\\s|^)'+cls+'(\\s|$)');
    ele.className=ele.className.replace(reg,' ');
}

// PATCHED BY XAVIER HANIN 2009-12-28 - START
// removed getOffset function, use one from jquery: less code + bug free
//PATCHED BY XAVIER HANIN 2009-12-28 - END

function getBox(el){
    var left, right, top, bottom;   
    // PATCHED BY XAVIER HANIN 2009-12-28 - START
    // removed getOffset function, use one from jquery: less code + bug free
    // var offset = getOffset(el);
    var offset = jQuery(el).offset();
    // PATCHED BY XAVIER HANIN 2009-12-28 - END
    left = offset.left;
    top = offset.top;
                        
    right = left + el.offsetWidth;
    bottom = top + el.offsetHeight;     
        
    return {
        left: left,
        right: right,
        top: top,
        bottom: bottom
    };
}

/**
 * Crossbrowser mouse coordinates
 */
function getMouseCoords(e){     
    // pageX/Y is not supported in IE
    // http://www.quirksmode.org/dom/w3c_cssom.html         
    if (!e.pageX && e.clientX){
        // In Internet Explorer 7 some properties (mouse coordinates) are treated as physical,
        // while others are logical (offset).
        var zoom = 1;   
        var body = document.body;
        
        if (body.getBoundingClientRect) {
            var bound = body.getBoundingClientRect();
            zoom = (bound.right - bound.left)/body.clientWidth;
        }

        return {
            x: e.clientX / zoom + d.body.scrollLeft + d.documentElement.scrollLeft,
            y: e.clientY / zoom + d.body.scrollTop + d.documentElement.scrollTop
        };
    }
    
    return {
        x: e.pageX,
        y: e.pageY
    };      

}
/**
 * Function generates unique id
 */     
var getUID = function(){
    var id = 0;
    return function(){
        return 'ValumsAjaxUpload' + id++;
    }
}();

function fileFromPath(file){
    return file.replace(/.*(\/|\\)/, "");           
}

function getExt(file){
    return (/[.]/.exec(file)) ? /[^.]+$/.exec(file.toLowerCase()) : '';
}           

// Please use AjaxUpload , Ajax_upload will be removed in the next version
Ajax_upload = AjaxUpload = function(button, options){
    if (button.jquery){
        // jquery object was passed
        button = button[0];
    } else if (typeof button == "string" && /^#.*/.test(button)){                   
        button = button.slice(1);               
    }
    button = get(button);   
    
    this._input = null;
    this._button = button;
    this._disabled = false;
    this._submitting = false;
    // Variable changes to true if the button was clicked
    // 3 seconds ago (requred to fix Safari on Mac error)
    this._justClicked = false;
    this._parentDialog = d.body;
    
    if (window.jQuery && jQuery.ui && jQuery.ui.dialog){
        var parentDialog = jQuery(this._button).parents('.ui-dialog');
        if (parentDialog.length){
            this._parentDialog = parentDialog[0];
        }
    }           
                    
    this._settings = {
        // Location of the server-side upload script
        action: 'upload.php',           
        // File upload name
        name: 'userfile',
        // Additional data to send
        data: {},
        // Submit file as soon as it's selected
        autoSubmit: true,
        // The type of data that you're expecting back from the server.
        // Html and xml are detected automatically.
        // Only useful when you are using json data as a response.
        // Set to "json" in that case. 
        responseType: false,
        // When user selects a file, useful with autoSubmit disabled            
        onChange: function(file, extension){},                  
        // Callback to fire before file is uploaded
        // You can return false to cancel upload
        onSubmit: function(file, extension){},
        // Fired when file upload is completed
        // WARNING! DO NOT USE "FALSE" STRING AS A RESPONSE!
        onComplete: function(file, response) {}
    };

    // Merge the users options with our defaults
    for (var i in options) {
        this._settings[i] = options[i];
    }
    
    this._createInput();
    this._rerouteClicks();
}
            
// assigning methods to our class
AjaxUpload.prototype = {
    setData : function(data){
        this._settings.data = data;
    },
    disable : function(){
        this._disabled = true;
    },
    enable : function(){
        this._disabled = false;
    },
    // PATCHED BY XAVIER HANIN 2009-12-28 - START
    setAction : function(action){
        this._settings.action = action;
    },
    // PATCHED BY XAVIER HANIN 2009-12-28 - END
    // removes ajaxupload
    destroy : function(){
        if(this._input){
            if(this._input.parentNode){
                this._input.parentNode.removeChild(this._input);
            }
            this._input = null;
        }
    },              
    /**
     * Creates invisible file input above the button 
     */
    _createInput : function(){
        var self = this;
        var input = d.createElement("input");
        input.setAttribute('type', 'file');
        input.setAttribute('name', this._settings.name);
        var styles = {
            'position' : 'absolute'
            ,'margin': '-5px 0 0 -175px'
            ,'padding': 0
            ,'width': '220px'
            ,'height': '30px'
            ,'fontSize': '14px'                             
            ,'opacity': 0
            ,'cursor': 'pointer'
            ,'display' : 'none'
            ,'zIndex' :  2147483583 //Max zIndex supported by Opera 9.0-9.2x 
            // Strange, I expected 2147483647                   
        };
        for (var i in styles){
            input.style[i] = styles[i];
        }
        
        // Make sure that element opacity exists
        // (IE uses filter instead)
        if ( ! (input.style.opacity === "0")){
            input.style.filter = "alpha(opacity=0)";
        }
                            
        this._parentDialog.appendChild(input);

        addEvent(input, 'change', function(){
            // get filename from input
            var file = fileFromPath(this.value);    
            if(self._settings.onChange.call(self, file, getExt(file)) == false ){
                return;             
            }                                                       
            // Submit form when value is changed
            if (self._settings.autoSubmit){
                self.submit();                      
            }                       
        });
        
        // Fixing problem with Safari
        // The problem is that if you leave input before the file select dialog opens
        // it does not upload the file.
        // As dialog opens slowly (it is a sheet dialog which takes some time to open)
        // there is some time while you can leave the button.
        // So we should not change display to none immediately
        addEvent(input, 'click', function(){
            self.justClicked = true;
            setTimeout(function(){
                // we will wait 3 seconds for dialog to open
                self.justClicked = false;
            }, 2500);           
        });     
        
        this._input = input;
    },
    _rerouteClicks : function (){
        var self = this;
    
        // IE displays 'access denied' error when using this method
        // other browsers just ignore click()
        // addEvent(this._button, 'click', function(e){
        //   self._input.click();
        // });
                
        var box, dialogOffset = {top:0, left:0}, over = false;
                                    
        addEvent(self._button, 'mouseover', function(e){
            if (!self._input || over) return;
            
            over = true;
            box = getBox(self._button);
                    
            if (self._parentDialog != d.body){
                dialogOffset = getOffset(self._parentDialog);
            }   
        });
        
    
        // We can't use mouseout on the button,
        // because invisible input is over it
        addEvent(document, 'mousemove', function(e){
            var input = self._input;            
            if (!input || !over) return;
            
            if (self._disabled){
                removeClass(self._button, 'hover');
                input.style.display = 'none';
                return;
            }   
                                        
            var c = getMouseCoords(e);

            if ((c.x >= box.left) && (c.x <= box.right) && 
            (c.y >= box.top) && (c.y <= box.bottom)){
                            
                input.style.top = c.y - dialogOffset.top + 'px';
                input.style.left = c.x - dialogOffset.left + 'px';
                input.style.display = 'block';
                addClass(self._button, 'hover');
                                
            } else {        
                // mouse left the button
                over = false;
            
                var check = setInterval(function(){
                    // if input was just clicked do not hide it
                    // to prevent safari bug
                     
                    if (self.justClicked){
                        return;
                    }
                    
                    if ( !over ){
                        input.style.display = 'none';   
                    }                       
                
                    clearInterval(check);
                
                }, 25);
                    

                removeClass(self._button, 'hover');
            }           
        });         
            
    },
    /**
     * Creates iframe with unique name
     */
    _createIframe : function(){
        // unique name
        // We cannot use getTime, because it sometimes return
        // same value in safari :(
        var id = getUID();
        
        // Remove ie6 "This page contains both secure and nonsecure items" prompt 
        // http://tinyurl.com/77w9wh
        var iframe = toElement('<iframe src="javascript:false;" name="' + id + '" />');
        iframe.id = id;
        iframe.style.display = 'none';
        d.body.appendChild(iframe);         
        return iframe;                      
    },
    getCurrentFileExtension : function(){
        if (this._input.value === ''){
            // there is no file
            return "";
        }

        // get filename from input
        var file = fileFromPath(this._input.value);

        return getExt(file);
    },
    /**
     * Upload file without refreshing the page
     */
    submit : function(callback){
        var self = this, settings = this._settings; 
                    
        if (this._input.value === ''){
            // there is no file
            return;
        }
                                        
        // get filename from input
        var file = fileFromPath(this._input.value);         

        // execute user event
        if (! (settings.onSubmit.call(this, file, getExt(file)) == false)) {
            // Create new iframe for this submission
            var iframe = this._createIframe();
            
            // Do not submit if user function returns false                                     
            var form = this._createForm(iframe);
            form.appendChild(this._input);
            
            form.submit();
            
            d.body.removeChild(form);               
            form = null;
            this._input = null;
            
            // create new input
            this._createInput();
            
            var toDeleteFlag = false;
            
            addEvent(iframe, 'load', function(e){
                    
                if (// For Safari
                    iframe.src == "javascript:'%3Chtml%3E%3C/html%3E';" ||
                    // For FF, IE
                    iframe.src == "javascript:'<html></html>';"){                       
                    
                    // First time around, do not delete.
                    if( toDeleteFlag ){
                        // Fix busy state in FF3
                        setTimeout( function() {
                            d.body.removeChild(iframe);
                        }, 0);
                    }
                    return;
                }               
                
                var doc = iframe.contentDocument ? iframe.contentDocument : frames[iframe.id].document;

                // fixing Opera 9.26
                if (doc.readyState && doc.readyState != 'complete'){
                    // Opera fires load event multiple times
                    // Even when the DOM is not ready yet
                    // this fix should not affect other browsers
                    return;
                }
                
                // fixing Opera 9.64
                if (doc.body && doc.body.innerHTML == "false"){
                    // In Opera 9.64 event was fired second time
                    // when body.innerHTML changed from false 
                    // to server response approx. after 1 sec
                    return;             
                }
                
                var response;
                                    
                if (doc.XMLDocument){
                    // response is a xml document IE property
                    response = doc.XMLDocument;
                } else if (doc.body){
                    // response is html document or plain text
                    response = doc.body.innerHTML;
                    if (settings.responseType && settings.responseType.toLowerCase() == 'json'){
                        // If the document was sent as 'application/javascript' or
                        // 'text/javascript', then the browser wraps the text in a <pre>
                        // tag and performs html encoding on the contents.  In this case,
                        // we need to pull the original text content from the text node's
                        // nodeValue property to retrieve the unmangled content.
                        // Note that IE6 only understands text/html
                        if (doc.body.firstChild && doc.body.firstChild.nodeName.toUpperCase() == 'PRE'){
                            response = doc.body.firstChild.firstChild.nodeValue;
                        }
                        if (response) {
                            response = window["eval"]("(" + response + ")");
                        } else {
                            response = {};
                        }
                    }
                } else {
                    // response is a xml document
                    var response = doc;
                }
                                                                            
                settings.onComplete.call(self, file, response);

                if (callback != null) {
                    callback();
                }

                // Reload blank page, so that reloading main page
                // does not re-submit the post. Also, remember to
                // delete the frame
                toDeleteFlag = true;
                
                // Fix IE mixed content issue
                iframe.src = "javascript:'<html></html>';";                                     
            });
    
        } else {
            // clear input to allow user to select same file
            // Doesn't work in IE6
            // this._input.value = '';
            d.body.removeChild(this._input);                
            this._input = null;
            
            // create new input
            this._createInput();                        
        }
    },      
    /**
     * Creates form, that will be submitted to iframe
     */
    _createForm : function(iframe){
        var settings = this._settings;
        
        // method, enctype must be specified here
        // because changing this attr on the fly is not allowed in IE 6/7       
        var form = toElement('<form method="post" enctype="multipart/form-data"></form>');
        form.style.display = 'none';
        form.action = settings.action;
        form.target = iframe.name;
        d.body.appendChild(form);
        
        // Create hidden input element for each data key
        for (var prop in settings.data){
            var el = d.createElement("input");
            el.type = 'hidden';
            el.name = prop;
            el.value = settings.data[prop];
            form.appendChild(el);
        }           
        return form;
    }   
};
})();