var currentPageHtml = function() {
    window.advertParser.onCurrentPageHtml(document.getElementsByTagName('html')[0].outerHTML);
};

var printMessage = function(msg) {
    console.log(msg.toString());
    window.advertParser.onMessage(msg);
};

/*covert to 2 float*/
var toDecimal = function (x) {
    var f = parseFloat(x);
    if (isNaN(f)) {
        return;
    }
    f = Math.round(x*100)/100;
    return f;
};

var findItemLocation = function(width,height) {
    printMessage('viewport:'+width+'-'+height);
    window.advertParser.onParseStart();
    var clientWidth = (document.documentElement.clientWidth || document.body.clientWidth);
    var clientHeight = (document.documentElement.clientHeight || document.body.clientHeight);
    printMessage('h5 body:'+width+'-'+height);
    var devicePixelRatio=height/clientHeight;
    printMessage('devicePixelRatio:'+devicePixelRatio+'-'+window.devicePixelRatio);
    var items=document.getElementsByClassName("n-item");
    if(items.length<=0){
        window.advertParser.onParseFail();
        return;
    }
    for(var i=0;i<items.length;i++){
        var item=items[i];
        var className=item.className;
        var contents=item.getElementsByClassName("content");
        var titleDiv=item.getElementsByClassName("n-title element");
        var title=titleDiv[0].getElementsByTagName("span")[0].innerHTML;
        for (var j=0;j<contents.length;j++){
            var content=contents[j];
            var left=toDecimal(content.getBoundingClientRect().left*devicePixelRatio);
            var top=toDecimal(content.getBoundingClientRect().top*devicePixelRatio);
            var right=toDecimal(content.getBoundingClientRect().right*devicePixelRatio);
            var bottom=toDecimal(content.getBoundingClientRect().bottom*devicePixelRatio);
            var node='{"position":'+i+',"childIndex":'+j+',"left":'+left+',"top":'+top+',"right":'+right+',"bottom":'+bottom+',"clientWidth":'+toDecimal(clientWidth*devicePixelRatio)+',"clientHeight":'+toDecimal(clientHeight*devicePixelRatio)+',"className":"'+className+'","title":"'+title+'"}';
            window.advertParser.onFoundItem(node);
            /*window.advertParser.onFoundItemHtml(content.outerHTML);*/
        }
    }
    window.advertParser.onParseSuccess();
};
