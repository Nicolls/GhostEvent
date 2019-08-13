var currentPageHtml = function() {
    window.advertParser.onCurrentPageHtml(document.getElementsByTagName('html')[0].outerHTML);
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

var printMessage = function(msg) {
    window.advertParser.onMessage(msg);
};

var printContext = function(width,height) {
    var clientWidth = (document.documentElement.clientWidth || document.body.clientWidth);
    var clientHeight = (document.documentElement.clientHeight || document.body.clientHeight);
    var devicePixelRatioW=width/clientWidth;
    var devicePixelRatioH=height/clientHeight;
    var context='{"width":'+width+',"height":'+height+',"clientWidth":'+clientWidth+',"clientHeight":'+clientHeight+',"devicePixelRatioW":'+devicePixelRatioW+',"devicePixelRatioH":'+devicePixelRatioH+',"correctClientWidth":'+toDecimal(clientWidth*devicePixelRatioH)+',"correctClientHeight":'+toDecimal(clientHeight*devicePixelRatioH)+'}';
    window.advertParser.onPrintContext(context);
};

var findItemById = function(itemId,width,height) {
    var clientWidth = (document.documentElement.clientWidth || document.body.clientWidth);
    var clientHeight = (document.documentElement.clientHeight || document.body.clientHeight);
    var devicePixelRatio=height/clientHeight;
    var item=document.getElementById(itemId);
    var idName=item.id;
    var left=toDecimal(item.getBoundingClientRect().left*devicePixelRatio);
    var top=toDecimal(item.getBoundingClientRect().top*devicePixelRatio);
    var right=toDecimal(item.getBoundingClientRect().right*devicePixelRatio);
    var bottom=toDecimal(item.getBoundingClientRect().bottom*devicePixelRatio);
    var node='{"index":0,"childIndex":0,"left":'+left+',"top":'+top+',"right":'+right+',"bottom":'+bottom+',"className":"'+className+'","idName":"'+idName+'","title":"'+title+'"}';
    window.advertParser.onFoundIdItem(node);
};

var findItemByClassName = function(width,height) {
    var className='icon-up';
    var clientWidth = (document.documentElement.clientWidth || document.body.clientWidth);
    var clientHeight = (document.documentElement.clientHeight || document.body.clientHeight);
    var devicePixelRatio=height/clientHeight;
    var content=document.getElementsByClassName(className);
    var item=content[0];
    var idName='';
    var left=toDecimal(item.getBoundingClientRect().left*devicePixelRatio);
    var top=toDecimal(item.getBoundingClientRect().top*devicePixelRatio);
    var right=toDecimal(item.getBoundingClientRect().right*devicePixelRatio);
    var bottom=toDecimal(item.getBoundingClientRect().bottom*devicePixelRatio);
    var node='{"index":0,"childIndex":0,"left":'+left+',"top":'+top+',"right":'+right+',"bottom":'+bottom+',"className":"'+className+'","idName":"'+idName+'","title":"arrow_top"}';
    window.advertParser.onFoundClassItem(node);
};

var findItemLocation = function(width,height) {
    window.advertParser.onParseStart();
    var clientWidth = (document.documentElement.clientWidth || document.body.clientWidth);
    var clientHeight = (document.documentElement.clientHeight || document.body.clientHeight);
    var devicePixelRatio=height/clientHeight;
    var items=document.getElementsByClassName("n-item");
    if(items.length<=0){
        window.advertParser.onParseFail();
        return;
    }
    for(var i=0;i<items.length;i++){
        var item=items[i];
        var className=item.className;
        var idName='';
        var contents=item.getElementsByClassName("content");
        var titleDiv=item.getElementsByClassName("n-title element");
        var title=titleDiv[0].getElementsByTagName("span")[0].innerHTML;
        for (var j=0;j<contents.length;j++){
            var content=contents[j];
            var left=toDecimal(content.getBoundingClientRect().left*devicePixelRatio);
            var top=toDecimal(content.getBoundingClientRect().top*devicePixelRatio);
            var right=toDecimal(content.getBoundingClientRect().right*devicePixelRatio);
            var bottom=toDecimal(content.getBoundingClientRect().bottom*devicePixelRatio);
            var node='{"index":'+i+',"childIndex":'+j+',"left":'+left+',"top":'+top+',"right":'+right+',"bottom":'+bottom+',"className":"'+className+'","idName":"'+idName+'","title":"'+title+'"}';
            window.advertParser.onFoundItem(node);
            /*window.advertParser.onFoundItemHtml(content.outerHTML);*/
        }
    }
    window.advertParser.onParseSuccess();
};
