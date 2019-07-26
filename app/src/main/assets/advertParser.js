var currentPageHtml = function() {
    window.advertParser.onCurrentPageHtml(document.getElementsByTagName('html')[0].outerHTML);
};

var findItemText = function() {
    var items=document.getElementsByClassName("cell");
    for(var i=0;i<items.length;i++){
        var item=items[i];
        window.advertParser.onFoundItemHtml(item.innerHTML);
    }
};

var findItemLocation = function() {
    window.advertParser.onParseStart();
    var items=document.getElementsByClassName("cell");
    if(items.length<=0){
        window.advertParser.onParseFail();
        return;
    }
    for(var i=0;i<items.length;i++){
        var item=items[i];
        var left=item.getBoundingClientRect().left;
        var top=item.getBoundingClientRect().top;
        var right=item.getBoundingClientRect().right;
        var bottom=item.getBoundingClientRect().bottom;
        var rect='{"position":'+i+',"left":'+left+',"top":'+top+',"right":'+right+',"bottom":'+bottom+'}';
        window.advertParser.onFoundItem(rect);
        window.advertParser.onFoundItemHtml(item.innerHTML);
    }
    window.advertParser.onParseSuccess();
};
