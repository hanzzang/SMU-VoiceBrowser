// 해당 원본 테그 내용을 TTS가 읽을 수 있는 내용으로 변경
function replaceTableTagHtml() {
    var tagName = 'table';
    try {
        for(var idx = 0; idx < document.getElementsByTagName(tagName).length; idx++) {
            var textContent = window.SMUJSInterface.getTableReadingText(document.getElementsByTagName(tagName)[idx].outerHTML);
            document.getElementsByTagName(tagName)[idx].outerHTML = textContent;
        }
    } catch(err) {
    }
}

function replaceMathMLTagHtml() {
    var tagName = 'math';
    try {
        for(var idx = 0; idx < document.getElementsByTagName(tagName).length; idx++) {
            var textContent = window.SMUJSInterface.getMathReadingText(document.getElementsByTagName(tagName)[idx].outerHTML);
            document.getElementsByTagName(tagName)[idx].outerHTML = textContent;
        }
    } catch(err) {
    }
}

// 필요한 경우 활용 - HTML 내의 해당 테그(math나 table 등)의 수
function getTagCount(tagName)
{
    return document.getElementsByTagName(tagName).length;
}


////////////////////// 선택모드
var targetArray = []; // target 담는 배열
/*var parentTag; // 더블 탭 부모
var parentCount = 0;*/

var bStartEvent = false; //touchstart 이벤트 발생 여부 플래그
var bMoveEvent = false; //touchmove 이벤트 발생 여부 플래그

htClickInfo = { //더블탭을 판단하기 위한 마지막 탭 이벤트의 정보 해시 테이블
    sType : null,
    nX : -1,
    nY : -1,
    nTime : 0
}

var nDoubleTapDuration = 200; //더블탭을 판단하는 기준 시간(ms)
var nTapThreshold = 5; //탭을 판단하는 거리
var oTapEventTimer = null; //탭-더블탭 대기 타이머

// 선택모드 이벤트 리스너
function initChoice(){
    document.addEventListener("touchstart", onStart);
    document.addEventListener("touchmove", onMove);
    document.addEventListener("touchend", onEnd);

    // 링크 이동 비활성화
    $('a').click(function () {return false;});
    $('input').click(function () {return false;});
    $("a").css( "pointer-events", "none" );
    $("input").css( "pointer-events", "none" );

    console.log("*******************initChoice****************");
    console.log(bStartEvent + "initChoice" + bMoveEvent);
}

function initClearInfo() {
    htClickInfo.sType = null;
}

function onStart(e) {
    bStartEvent = true;
    console.log("*******************onStart****************");
}

function onMove(e) {
    if(!bStartEvent) {
        return; //touchstart 이벤트가 발생하지 않으면 처리하지 않는다.
    }
    bMoveEvent = true; //touchmove 이벤트 발생 여부를 설정한다.
    console.log("*******************onMove****************");
}

function onEnd(e) {
    var nX;
    var nY;
    var targetTag;
    var i;
    var nTime;

    //유효한 태그 찾기 - p, strong, span
    for(i = 0; i<e.changedTouches.length;i++){
        if(e.changedTouches[i].target.tagName.toLowerCase() == 'p' || e.changedTouches[i].target.tagName.toLowerCase() == 'h1' ||
            e.changedTouches[i].target.tagName.toLowerCase() == 'h2' || e.changedTouches[i].target.tagName.toLowerCase() == 'h3' ||
            e.changedTouches[i].target.tagName.toLowerCase() == 'h4' || e.changedTouches[i].target.tagName.toLowerCase() == 'h5' ||
            e.changedTouches[i].target.tagName.toLowerCase() == 'h6' || e.changedTouches[i].target.tagName.toLowerCase() == 'ul' ||
            e.changedTouches[i].target.tagName.toLowerCase() == 'li' || e.changedTouches[i].target.tagName.toLowerCase() == 'ol' ||
            e.changedTouches[i].target.tagName.toLowerCase() == 'th' || e.changedTouches[i].target.tagName.toLowerCase() == 'td' ||
            e.changedTouches[i].target.tagName.toLowerCase() == 'dt' || e.changedTouches[i].target.tagName.toLowerCase() == 'dd' ||
            e.changedTouches[i].target.tagName.toLowerCase() == 'div'|| e.changedTouches[i].target.tagName.toLowerCase() == 'span'){
                nX = e.changedTouches[i].pageX;
                nY = e.changedTouches[i].pageY;
                targetTag = e.changedTouches[i].target;
                nTime = e.timeStamp;
                break;
        }else if(i==e.changedTouches.length-1){ //터치 영역에 유효한 태그 없으면 탭 이벤트 정보를 초기화
            initClearInfo();
            bStartEvent = false;
            bMoveEvent = false;
        }else{
            continue;
        }
    }

    if(bStartEvent && !bMoveEvent) {
        //이전 탭 이벤트와 시간 차이가 200ms 이하일 경우
        if(htClickInfo.sType == "tap" && (nTime - htClickInfo.nTime) <= nDoubleTapDuration){
            if( (Math.abs(htClickInfo.nX-nX) <= nTapThreshold)
                 && (Math.abs(htClickInfo.nY-nY) <= nTapThreshold) ){   //더블탭으로 판단한다. (탭이 발생하지 않게 탭 발생 타이머 초기화한다.)
/*                console.log("------------------dddddtap------------------");
                clearTimeout(oTapEventTimer);

                targetTag = targetTag.parentElement.parentElement;

                var isTarget = targetArray.indexOf(targetTag);
                if(isTarget != -1){ // 이미 선택한 태그라면
                    // 스타일 원래대로
                    targetTag.style.removeProperty('background');
                    targetTag.style.removeProperty('color');
                    // 배열에서 삭제
                    var textContent = window.SMUJSInterface.removeWebPageDomObject(isTarget);
                    targetArray.splice(isTarget,1);
                }else{ // 선택하지 않은 태그라면
                    // 배열에 추가
                    if(targetTag.innerHTML == '&nbsp;'){
                    }else{
                        targetArray.push(targetTag);
                        var tn = targetTag.tagName;
                        var index = $( tn ).index( targetTag );
                        var textContent = window.SMUJSInterface.setWebPageDomObject(index, targetTag.tagName, targetTag.innerText);
                        // 하이라이팅
                        targetTag.style.background = 'orange';
                        targetTag.style.color = 'black';
                    }
                }*/
                /*if(Object.equals(parentTag, targetTag.parentElement.parentElement) && parentCount < 3){  // 또 부모로
                    console.log("------------------parentTag.parentElement;------------------");
                    var isTarget = targetArray.indexOf(parentTag);
                    // 스타일 원래대로
                    parentTag.style.removeProperty('background');
                    parentTag.style.removeProperty('color');
                    // 배열에서 삭제
                    var textContent = window.SMUJSInterface.removeWebPageDomObject(isTarget);
                    targetArray.splice(isTarget,1);

                    parentTag = parentTag.parentElement.parentElement;
                    parentCount++;

                    // 선택하지 않은 태그라면
                    // 배열에 추가
                    if(parentTag.innerHTML == '&nbsp;'){
                    }else{
                        targetArray.push(parentTag);
                        var tn = parentTag.tagName;
                        var index = $( tn ).index( parentTag );
                        var textContent = window.SMUJSInterface.setWebPageDomObject(index, parentTag.tagName, parentTag.innerText);
                        // 하이라이팅
                        parentTag.style.background = 'orange';
                        parentTag.style.color = 'black';
                    }
                } else if(Object.equals(parentTag, targetTag.parentElement.parentElement) && parentCount == 1){   // 리셋
                    console.log("------------------parentCount3.parentElement;------------------");
                    var isTarget = targetArray.indexOf(parentTag);
                    // 스타일 원래대로
                    parentTag.style.removeProperty('background');
                    parentTag.style.removeProperty('color');
                    // 배열에서 삭제
                    var textContent = window.SMUJSInterface.removeWebPageDomObject(isTarget);
                    targetArray.splice(isTarget,1);
                    parentTag = null;
                    parentCount = 0;
                } else { // 새로운 부모
                    console.log("------------------target.parentElement;------------------");
                    parentTag = targetTag.parentElement.parentElement

                    // 선택하지 않은 태그라면
                    // 배열에 추가
                    if(parentTag.innerHTML == '&nbsp;'){
                    }else{
                        targetArray.push(parentTag);
                        var tn = parentTag.tagName;
                        var index = $( tn ).index( parentTag );
                        var textContent = window.SMUJSInterface.setWebPageDomObject(index, parentTag.tagName, parentTag.innerText);
                        // 하이라이팅
                        parentTag.style.background = 'pink';
                        parentTag.style.color = 'black';
                    }
                }*/
            }
        } else {
            //탭 이벤트로 판단한다.
            //현재 탭 이벤트들에 대한 정보를 업데이트한다.
            /*console.log("------------------tap------------------");
            oTapEventTimer = setTimeout(function(){*/
                var isTarget = targetArray.indexOf(targetTag);
                if(isTarget != -1){ // 이미 선택한 태그라면
                    // 스타일 원래대로
                    targetTag.style.removeProperty('background');
                    targetTag.style.removeProperty('color');
                    // 배열에서 삭제
                    var textContent = window.SMUJSInterface.removeWebPageDomObject(isTarget);
                    targetArray.splice(isTarget,1);
                }else{ // 선택하지 않은 태그라면
                    // 배열에 추가
                    if(targetTag.innerHTML == '&nbsp;'){
                    }else{
                        targetArray.push(targetTag);
                        var tn = targetTag.tagName;
                        var index = $( tn ).index( targetTag );
                        var textContent = window.SMUJSInterface.setWebPageDomObject(index, targetTag.tagName, targetTag.innerText);
                        // 하이라이팅
                        targetTag.style.background = 'yellow';
                        targetTag.style.color = 'black';
                    }
                }
/*            }.bind(this), 300);*/

            htClickInfo.sType = "tap";
            htClickInfo.nX = nX;
            htClickInfo.nY =nY;
            htClickInfo.nTime = nTime;
            console.log("*******************tapend****************");
        }
    } else {
        //탭 이벤트가 아니므로 탭 이벤트 정보를 초기화한다.
        initClearInfo();
        console.log("*******************initClearInfo****************");
    }

    bStartEvent = false;
    bMoveEvent = false;
}

// TTS 시작
function startTTS(){
    try {
        for (var prop in targetArray) {
            var textContent = window.SMUJSInterface.getWebPageBodyText(targetArray[prop].innerText);
        }
    } catch(err) {
    }
}

// 선택 모드만 막기
function stopChoice(){
    bStartEvent = false;
    bMoveEvent = false;

    document.removeEventListener("touchstart", onStart);
    document.removeEventListener("touchmove", onMove);
    document.removeEventListener("touchend", onEnd);
    console.log("*******************stopChoice****************");
}

// 선택 모드 완전 종료
function finalizeChoice(){
    bStartEvent = false;
    bMoveEvent = false;

    // 링크 이동 활성화
    $('a').unbind('click');
    $('input').unbind('click');
    $("a").css( "pointer-events", "auto" );
    $("input").css( "pointer-events", "auto" );

    // 스타일 원래대로
    for (var prop in targetArray) {
        targetArray[prop].style.removeProperty('background');
        targetArray[prop].style.removeProperty('color');
        targetArray[prop].style.removeProperty('fontSize');
        targetArray[prop].style.removeProperty('fontWeight');
    }

    // 배열 비우기
/*    parentTag = null;
    parentCount = 0;*/
    targetArray.splice(0,targetArray.length);
    var textContent = window.SMUJSInterface.clearWebPageDomObject();

    document.removeEventListener("touchstart", onStart);
    document.removeEventListener("touchmove", onMove);
    document.removeEventListener("touchend", onEnd);
    console.log("*******************finalizeChoice****************");
}



function startZoom(mode){
    document.write("<body style='font-size:40px; font-weight:bold; line-height:150%;'>");
    for (var prop in targetArray) {
        document.writeln("<" + targetArray[prop].tagName + ">" + targetArray[prop].innerText + "</" + targetArray[prop].tagName + ">" );
    }
    document.write("</body>");

    if(mode == "zoomtts"){
        startTTS();
    }
}

function backZoom(){
    loadTag();
    initChoice();
}

function checkTag(){
    for (var prop in targetArray) {
        console.log("----checkTag " + prop + " = " + targetArray[prop] + " / " + targetArray[prop].tagName +" / " + targetArray[prop].innerText);
    }
}

function zoomin(){
    txt = document.body;
    style = window.getComputedStyle(txt, null).getPropertyValue('font-size');
    currentSize = parseFloat(style);
    if(currentSize < 100){
        txt.style.fontSize = (currentSize + 10) + 'px';
    }
}

function zoomout(){
    txt = document.body;
    style = window.getComputedStyle(txt, null).getPropertyValue('font-size');
    currentSize = parseFloat(style);
    if(currentSize > 5){
        txt.style.fontSize = (currentSize - 5) + 'px';
    }
}

function reversal(){
    if(document.body.style.backgroundColor == "black"){
        document.body.style.backgroundColor = "white"
        document.body.style.color = "black";
    } else{
        document.body.style.backgroundColor = "black";
        document.body.style.color = "white";
    }
}

function choiceReset(){
    // 스타일 원래대로
    for (var prop in targetArray) {
        targetArray[prop].style.removeProperty('background');
        targetArray[prop].style.removeProperty('color');
        targetArray[prop].style.removeProperty('fontSize');
        targetArray[prop].style.removeProperty('fontWeight');
     }

    // 배열 비우기
/*    parentTag = null;
    parentCount = 0;*/
    targetArray.splice(0,targetArray.length);
    var textContent = window.SMUJSInterface.clearWebPageDomObject();
}

// 돌아왔을때 세팅
function loadTag(){
    var targetArraySize = window.SMUJSInterface.getWebPageDomObjectSize();

    for(var i = 0; i<targetArraySize; i++){
        var index = window.SMUJSInterface.getWebPageDomObjectIndex(i);
        var tn = window.SMUJSInterface.getWebPageDomObjectTagName(i);
        var temp = document.getElementsByTagName(tn)[index];

        temp.style.background = 'yellow';
        temp.style.color = 'black';
        targetArray.push(temp);
    }
    checkTag();
}

Object.equals = function( x, y ) {
  if ( x === y ) return true;
    // if both x and y are null or undefined and exactly the same

  if ( ! ( x instanceof Object ) || ! ( y instanceof Object ) ) return false;
    // if they are not strictly equal, they both need to be Objects

  if ( x.constructor !== y.constructor ) return false;
    // they must have the exact same prototype chain, the closest we can do is
    // test there constructor.

  for ( var p in x ) {
    if ( ! x.hasOwnProperty( p ) ) continue;
      // other properties were tested using x.constructor === y.constructor

    if ( ! y.hasOwnProperty( p ) ) return false;
      // allows to compare x[ p ] and y[ p ] when set to undefined

    if ( x[ p ] === y[ p ] ) continue;
      // if they have the same strict value or identity then they are equal

    if ( typeof( x[ p ] ) !== "object" ) return false;
      // Numbers, Strings, Functions, Booleans must be strictly equal

    if ( ! Object.equals( x[ p ],  y[ p ] ) ) return false;
      // Objects and Arrays must be tested recursively
  }

  for ( p in y ) {
    if ( y.hasOwnProperty( p ) && ! x.hasOwnProperty( p ) ) return false;
      // allows x[ p ] to be set to undefined
  }
  return true;
}