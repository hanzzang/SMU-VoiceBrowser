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