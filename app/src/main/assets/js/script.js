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

// 선택모드 이벤트 리스너
function initChoice(){
/*    document.addEventListener("touchstart", this.onStart.bind(this));
    document.addEventListener("touchmove", this.onMove.bind(this));
    document.addEventListener("touchend", this.onEnd.bind(this));*/
    document.addEventListener("touchstart", onStart);
    document.addEventListener("touchmove", onMove);
    document.addEventListener("touchend", onEnd);

    // a.href 링크 이동 방지인데 해제하는 법을 모름 흠.. 찾아보자
    //$(document).on('click', 'a', function(event) {return false;});
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
        if(e.changedTouches[i].target.tagName.toLowerCase() == 'p' || e.changedTouches[i].target.tagName.toLowerCase() == 'strong' ||
            e.changedTouches[i].target.tagName.toLowerCase() == 'span'){
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
                alert('double tap!');
                clearTimeout(oTapEventTimer);
            }
        } else {
            //탭 이벤트로 판단한다.
            //현재 탭 이벤트들에 대한 정보를 업데이트한다.
            var isTarget = targetArray.indexOf(targetTag);
            if(isTarget != -1){ // 이미 선택한 태그라면
                // 스타일 원래대로
                targetTag.style.removeProperty('background');
                targetTag.style.removeProperty('color');
                // 배열에서 삭제
                targetArray.splice(isTarget,1);
            }else{ // 선택하지 않은 태그라면
                // 배열에 추가
                /*targetClone = deepCopy(targetTag);
                targetArray.push(targetClone);*/
                targetArray.push(targetTag);
                // 하이라이팅
                targetTag.style.background = 'yellow';
                targetTag.style.color = 'black';

                htClickInfo.sType = "tap";
                htClickInfo.nX = nX;
                htClickInfo.nY =nY;
                htClickInfo.nTime = nTime;
            }

            //배열 확인
            for (var prop in targetArray) {
                console.log("---- " + prop + " = " + targetArray[prop] + " / " + targetArray[prop].tagName +" / " + targetArray[prop].innerText + " / " + targetArray[prop].style.background + " / " + targetArray[prop].style.color);
            }
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
/*    document.removeEventListener("touchstart", this.onStart.bind(this));
    document.removeEventListener("touchmove", this.onMove.bind(this));
    document.removeEventListener("touchend", this.onEnd.bind(this));*/

    //$(document).on('click', 'a', function(event) {return true;}); // a.href 링크 이동 방지 해제...?

    document.removeEventListener("touchstart", onStart);
    document.removeEventListener("touchmove", onMove);
    document.removeEventListener("touchend", onEnd);
    console.log("*******************stopChoice****************");
}

// 선택 모드 완전 종료
function finalizeChoice(){
    bStartEvent = false;
    bMoveEvent = false;
/*    document.removeEventListener("touchstart", this.onStart.bind(this));
    document.removeEventListener("touchmove", this.onMove.bind(this));
    document.removeEventListener("touchend", this.onEnd.bind(this));*/

    //$(document).on('click', 'a', function(event) {return true;}); // a.href 링크 이동 방지 해제...?

    // 스타일 원래대로
    for (var prop in targetArray) {
        targetArray[prop].style.removeProperty('background');
        targetArray[prop].style.removeProperty('color');
        targetArray[prop].style.removeProperty('fontSize');
        targetArray[prop].style.removeProperty('fontWeight');
    }

    // 배열 비우기
    targetArray.splice(0,targetArray.length);

    document.removeEventListener("touchstart", onStart);
    document.removeEventListener("touchmove", onMove);
    document.removeEventListener("touchend", onEnd);
    console.log("*******************finalizeChoice****************");
}



function startZoom(mode){
    //location.href="/zoom.html";

    for (var prop in targetArray) {
/*        targetArray[prop].style.fontSize = "30px";
        targetArray[prop].style.fontWeight = "bold";*/
        document.write("<h1>" + targetArray[prop].innerText + "</h1>");
        //document.write("<" + targetArray[prop].tagName + " id = '" + prop + "'>" + targetArray[prop].innerText + "<" + targetArray[prop].tagName + "/>");
                    console.log("----startZoom " + prop + " = " + targetArray[prop] + " / " + targetArray[prop].tagName +" / " + targetArray[prop].innerText);
    }

    if(mode == "zoomtts"){
        startTTS();
    }
}

function backZoom(){
    location.reload();
/*    loadTag();
                //배열 확인
                for (var prop in targetArray) {
                    console.log("----backZoom1 " + prop + " = " + targetArray[prop] + " / " + targetArray[prop].tagName +" / " + targetArray[prop].innerText);
                }
    initChoice();
                //배열 확인
                for (var prop in targetArray) {
                    console.log("----backZoom2 " + prop + " = " + targetArray[prop] + " / " + targetArray[prop].tagName +" / " + targetArray[prop].innerText);
                }*/
}

function loadTag(){
        targetTag = document.getElementsByTagName('*');
    for (var prop = 0; prop < targetTag.length; prop++) {
        if(targetTag[prop].tagName.toLowerCase() == 'h1'){
                    console.log("----loadTag " + prop + " = " + targetTag[prop] + " / " + targetTag[prop].tagName +" / " + targetTag[prop].innerText);

            targetArray.push(targetTag[prop]);//배열 확인
        }
    }
}