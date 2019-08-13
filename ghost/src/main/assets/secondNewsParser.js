var currentPageHtml = function() {
    window.secondNewsParser.onCurrentPageHtml(document.getElementsByTagName('html')[0].outerHTML);
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
    window.secondNewsParser.onMessage(msg);
};

var findArrowTopItem = function(width,height) {
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
    window.secondNewsParser.onFoundItem(node);
};

var findMainIconItem = function(width,height) {
    var className='icon-home';
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
    var node='{"index":0,"childIndex":0,"left":'+left+',"top":'+top+',"right":'+right+',"bottom":'+bottom+',"className":"'+className+'","idName":"'+idName+'","title":"main_icon"}';
    window.secondNewsParser.onFoundItem(node);
};

var findReadMoreItem = function(width,height) {
    var className='_1Dz8F';
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
    var node='{"index":0,"childIndex":0,"left":'+left+',"top":'+top+',"right":'+right+',"bottom":'+bottom+',"className":"'+className+'","idName":"'+idName+'","title":"read_more"}';
    window.secondNewsParser.onFoundItem(node);
};

var findAdvertTopItem = function(width,height) {
    var className='a-item-inner';
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
    var node='{"index":0,"childIndex":0,"left":'+left+',"top":'+top+',"right":'+right+',"bottom":'+bottom+',"className":"'+className+'","idName":"'+idName+'","title":"advert_top"}';
    window.secondNewsParser.onFoundItem(node);
};
