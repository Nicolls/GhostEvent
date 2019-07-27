var currentPageHtml = function() {
    window.advertParser.onCurrentPageHtml(document.getElementsByTagName('html')[0].outerHTML);
};

var printMessage = function() {
    window.advertParser.onMessage('test');
};

var findItemText = function() {
    var items=document.getElementsByClassName("n-item-ad");
    for(var i=0;i<items.length;i++){
        var item=items[i];
        var content=item.getElementsByClassName("content");
        window.advertParser.onFoundItemHtml(item.innerHTML);
    }
};

var findItemLocation = function() {
    window.advertParser.onParseStart();
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
            var left=content.getBoundingClientRect().left*window.devicePixelRatio;
            var top=content.getBoundingClientRect().top*window.devicePixelRatio;
            var right=content.getBoundingClientRect().right*window.devicePixelRatio;
            var bottom=content.getBoundingClientRect().bottom*window.devicePixelRatio;
            var clientWidth = (document.documentElement.clientWidth || document.body.clientWidth)*window.devicePixelRatio;
            var clientHeight = (document.documentElement.clientHeight || document.body.clientHeight)*window.devicePixelRatio;
            var node='{"position":'+i+',"childIndex":'+j+',"left":'+left+',"top":'+top+',"right":'+right+',"bottom":'+bottom+',"clientWidth":'+clientWidth+',"clientHeight":'+clientHeight+',"className":"'+className+'","title":"'+title+'"}';
            window.advertParser.onFoundItem(node);
            window.advertParser.onFoundItemHtml(content.outerHTML);
        }
    }
    window.advertParser.onParseSuccess();
};
