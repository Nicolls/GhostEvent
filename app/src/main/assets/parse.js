var fetchHtmlContent = function() {
    window.jsParser.onFetchHtml(document.getElementsByTagName('html')[0].outerHTML);
};

var findItemText = function() {
    var items=document.getElementsByClassName("figure flex-block");
    for(var i=0;i<items.length;i++){
        var item=items[i];
        window.jsParser.onFoundItemHtml(item.innerHTML);
    }
};

var findItemLocation = function() {
    window.jsParser.onParseStart();
    var items=document.getElementsByClassName("figure flex-block");
    if(items.length<=0){
        window.jsParser.onParseFail();
        return;
    }
    for(var i=0;i<items.length;i++){
        var item=items[i];
        var left=item.getBoundingClientRect().left;
        var top=item.getBoundingClientRect().top;
        var right=item.getBoundingClientRect().right;
        var bottom=item.getBoundingClientRect().bottom;
        var rect='{"left":'+left+',"top":'+top+',"right":'+right+',"bottom":'+bottom+'}';
        window.jsParser.onFoundItem(rect);
    }
    window.jsParser.onParseSuccess();
};
