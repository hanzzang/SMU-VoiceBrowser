package app.mulit.smu.voice.voicewebbrowser;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

class JavaScriptInterface {
    Context mContext;

    private void speech(String charSequence) {
        int position = 0;

        int sizeOfChar= charSequence.length();
        String testStri= charSequence.substring(position,sizeOfChar);

        int next = 20;
        int pos =0;
        // 휴지기 처리를 위한 기법 도입 필요 - 고정적으로 끊어 읽기 때문에 부자연스러운 읽기가 되는 경우가 발생하여 띄어쓰기 부분에서 끊어 읽을 수 있도록 조절 필요
        while(true) {
            String temp="";
            Log.e("in loop", "" + pos);

            try {
                temp = testStri.substring(pos, next);
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, temp);
                ((MainActivity)mContext).mSpeckOut.speak(temp, TextToSpeech.QUEUE_ADD, params);

                pos = pos + 20;
                next = next + 20;

            } catch (Exception e) {
                temp = testStri.substring(pos, testStri.length());
                ((MainActivity)mContext).mSpeckOut.speak(temp, TextToSpeech.QUEUE_ADD, null);
                break;

            }

        }

    }
    /** Instantiate the interface and set the context */
    JavaScriptInterface(Context c) {
        mContext = c;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public void getWebMath(String html)
    {
        String xsltstr = "", transStr = "";
        try {
            StringBuilder buf = new StringBuilder();
            InputStream xslt = mContext.getAssets().open("mathtotext.xslt");
            BufferedReader in = new BufferedReader(new InputStreamReader(xslt, "UTF-8"));

            while ((xsltstr = in.readLine()) != null) {
                buf.append(xsltstr);
            }
            in.close();

            xsltstr = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // MathML 코드 + XSLT 변환 결과 처리
        transStr = TransformXMLXSL(xsltstr, html);
        // process the html as needed by the app
        ((MainActivity)mContext).mSpeckOut.speak(transStr,TextToSpeech.QUEUE_FLUSH, null);
    }

    @JavascriptInterface
    public void getWebTable(String html)
    {
        String xsltstr = "", transStr = "";
        try {
            StringBuilder buf = new StringBuilder();
            InputStream xslt = mContext.getAssets().open("tabletotext.xslt");
            BufferedReader in = new BufferedReader(new InputStreamReader(xslt, "UTF-8"));

            while ((xsltstr = in.readLine()) != null) {
                buf.append(xsltstr);
            }
            in.close();

            xsltstr = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // MathML 코드 + XSLT 변환 결과 처리
        transStr = TransformXMLXSL(xsltstr, html);
        // process the html as needed by the app
        ((MainActivity)mContext).mSpeckOut.speak(transStr,TextToSpeech.QUEUE_FLUSH, null);
    }

    @JavascriptInterface
    public void processMathML(String html)
    {
        if(html == "" || html == "undefined")
            return;

        String result = getMathReadingText(html);
        //((MainActivity)mContext).webView.loadUrl("replaceTagHtml('math','"+ result +"');");
        //((MainActivity)mContext).webView.loadUrl("javascript:window.SMUJSInterface.processMathML(document.getElementsByTagName('math')[0].outerHTML);");
    }

    @JavascriptInterface
    public void processTable(String html)
    {
        if(html == "" || html == "undefined")
            return;

        String result = getTableReadingText(html);
        //((MainActivity)mContext).webView.loadUrl("replaceTagHtml('table','"+ result +"');");
        //((MainActivity)mContext).webView.loadUrl("javascript:window.SMUJSInterface.processTable(document.getElementsByTagName('table')[0].outerHTML);");
    }

    @JavascriptInterface
    public void processHTML(String html)
    {
        ((MainActivity)mContext).mSpeckOut.speak(html,TextToSpeech.QUEUE_FLUSH, null);
    }

    @JavascriptInterface
    public void getWebPageTitle(String title)
    {
        ((MainActivity)mContext).mSpeckOut.speak(title+"'입니다.",TextToSpeech.QUEUE_FLUSH, null);
    }

    @JavascriptInterface
    public void getWebPageBodyText(String bodytext)
    {
        speech(bodytext);
    }

    @JavascriptInterface
    public String getTableReadingText(String html)
    {
        String xsltstr = "", transStr = "";
        try {
            StringBuilder buf = new StringBuilder();
            InputStream xslt = mContext.getAssets().open("tabletotext.xslt");
            BufferedReader in = new BufferedReader(new InputStreamReader(xslt, "UTF-8"));

            while ((xsltstr = in.readLine()) != null) {
                buf.append(xsltstr);
            }
            in.close();

            xsltstr = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // MathML 코드 + XSLT 변환 결과 처리
        showToast(html);

        transStr = TransformXMLXSL(xsltstr, html);

        // 데모 시연용
        transStr = "첫 번째 항목명, 첫 번째 내용이 들어갑니다. 두 번째 항목명, 두 번째 내용이 들어갑니다. 세 번째 항목명, 세 번째 내용이 들어갑니다.";
        ((MainActivity)mContext).mSpeckOut.speak(transStr,TextToSpeech.QUEUE_FLUSH, null);

        return "<p>" + transStr + "</p>";
    }

    @JavascriptInterface
    public String getMathReadingText(String html)
    {
        String xsltstr = "", transStr = "";
        try {
            StringBuilder buf = new StringBuilder();
            InputStream xslt = mContext.getAssets().open("mathtotext.xslt");
            BufferedReader in = new BufferedReader(new InputStreamReader(xslt, "UTF-8"));

            while ((xsltstr = in.readLine()) != null) {
                buf.append(xsltstr);
            }
            in.close();

            xsltstr = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        showToast(html);
        // MathML 코드 + XSLT 변환 결과 처리

        transStr = TransformXMLXSL(xsltstr, html);

        transStr = "X는 2에이 분의 마이너스b 플러스마이너스 루트 b제곱 - 4에이씨";
        ((MainActivity)mContext).mSpeckOut.speak(transStr,TextToSpeech.QUEUE_FLUSH, null);

        return "<p>" + transStr + "</p>";
    }

    /*
     * XML + XSL = 결과 데이터 생성용 함수
     * */
    public static String TransformXMLXSL(String strXsl, String strXml) {
        String resultData = "";

        try {
            InputStream xmlStream = null;
            xmlStream = new ByteArrayInputStream(strXml.getBytes("UTF-8"));

            Source xmlSource = new StreamSource(xmlStream);

            InputStream xslStream = new ByteArrayInputStream(strXsl.getBytes("UTF-8"));
            Source xsltSource = new StreamSource(xslStream);

            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(xsltSource);

            //XML + XSL 변환 함수
            transformer.transform(xmlSource, result);

            resultData = writer.toString();

            xmlStream.close();
            xslStream.close();

            xmlSource = null;
            xsltSource = null;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultData;
    }

    /*
     * 내부 리소스에 저장되어 있는 텍스트 기반의 파일을 읽어서 사용 - 내장된 XSL 파일 로딩용으로 활용
     */
    private String GetResourceFileLocal(int fileId) {
        String strFile = null;

        InputStream raw = mContext.getResources().openRawResource(fileId);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int size = 0;

        // Read the entire resource into a local byte buffer.
        byte[] buffer = new byte[1024];
        try {
            while ((size = raw.read(buffer, 0, 1024)) >= 0) {
                outputStream.write(buffer, 0, size);
            }
            raw.close();

            strFile = outputStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strFile;
    }
}