var htmlContent = function() {
    return document.getElementsByTagName('html')[0].outerHTML;
};

var itemText = function() {
    var items=document.getElementsByClassName("figure flex-block");
    for(var i=0;i<items.length;i++){
        var item=items[i];
        window.jsParser.foundItem(item.innerHTML);
    }
};

var parseCompleted = function() {
    window.jsParser.parseCompleted(true);
};

var itemLocation = function() {
    window.jsParser.parseStart();
    var items=document.getElementsByClassName("figure flex-block");
    if(items.length<=0){
        window.jsParser.parseFail();
        return;
    }
    for(var i=0;i<items.length;i++){
        var item=items[i];
        var left=item.getBoundingClientRect().left;
        var top=item.getBoundingClientRect().top;
        var right=item.getBoundingClientRect().right;
        var bottom=item.getBoundingClientRect().bottom;
        var rect='{"left":'+left+',"top":'+top+',"right":'+right+',"bottom":'+bottom+'}';
        window.jsParser.foundItem(rect);
    }
    window.jsParser.parseSuccess();
};
itemLocation();
