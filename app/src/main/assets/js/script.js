var tagArray = new Array;

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

// p태그 뽑아서 저장
function getPTag() {
    var tagName = 'p';
    try {
        for(var idx = 0; idx < document.getElementsByTagName(tagName).length; idx++) {
            var text = document.getElementsByTagName(tagName)[idx];
            var textContent = text.innerText;
            tagArray.push(textContent);
            //var textContent = window.SMUJSInterface.getWebPageBodyText(document.getElementsByTagName(tagName)[idx].innerText);
            //alert(textContent);
            //document.getElementsByTagName(tagName)[idx].outerHTML = textContent;
        }
        //alert(tagArray);
    } catch(err) {
    }
}

// 스타일 적용
function addStyle(tagName) {
	var styles = tagName + '{ background: yellow; color: black }';
	var css = document.createElement('style');
	css.type = 'text/css';

	if (css.styleSheet)
		css.styleSheet.cssText = styles;
	else
	    css.appendChild(document.createTextNode(styles));

    try {
        for(var idx = 0; idx < document.getElementsByTagName(tagName).length; idx++) {
            var text = document.getElementsByTagName(tagName)[idx];
            // 하이라이팅
            text.appendChild(css);
            // 태그 값 가져오기
            var textContent = text.innerText;
            // 태그 값 저장
            tagArray.push(textContent);
        }
        //alert(tagArray);
    } catch(err) {
    }
}

// TTS 시작
function startTTS(){
    var tagName = 'p';
        try {
            for(var idx = 0; idx < document.getElementsByTagName(tagName).length; idx++) {
                var textContent = window.SMUJSInterface.getWebPageBodyText(document.getElementsByTagName(tagName)[idx].innerText);
                //document.getElementsByTagName(tagName)[idx].outerHTML = textContent;
            }
        } catch(err) {
        }
}

//webView.loadUrl("javascript:window.SMUJSInterface.getWebPageBodyText(document.getElementsByTagName('body')[0].innerText);");

function touchEndHandler(e){
    var elem = e.changedTouches.item(0);
    console.log($(elem).text());
}



// body에 터치이벤트 추가: 영역 제한을 여기서 해버리면? 아예 body영역에서만 적용되는 터치 이벤트!
function addEvent(){
    var elm = document.getElementsByTagName("body")[0];
    elm.addEventListener("touchstart", handleStart, true);
}

// 터치된 태그 이름 출력
// ?? 터치 스크롤도 적용되네; 막아야겠다. 막을 수 있나?
// ?? input 태그는 왜 계속 나오지 실제 코드에는 input 태그 없는데
function handleStart(evt) {
    var x = event.targetTouches[0].target.tagName;
    addStyle(x);

    var y = event.targetTouches[0].target;
    alert(x + " / " + y);
}