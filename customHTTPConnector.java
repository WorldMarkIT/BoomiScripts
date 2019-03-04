/*

Author: Saagar Kamrani
Date: 2019-03-04
Context: Due to license limitation on Boomi - Default HTTP connectors can not be freely utilized for small integration scenarios.
        This is where this custom HTTP connector comes to place. Deploy this in Data Process Shape. Script assumes that following would be provided:
        1) Body in Input Stream
        2) Dynamic Process Property: DPP_EndPoint
        3) Headers in DPP_Headers (Multiple Headers can be injected as follows; Content-Type: application/json|ClientID: 12345
        This will pass ClientID and Content-Type in Headers for API POST call.
        
If Response Code is 200, this sub-process returns response message, otherwise raises "warning" in logger and returns exception message.

*/

import java.util.Properties;
import java.io.InputStream;
import com.boomi.execution.ExecutionUtil;


//Collecting EndPoint and Headers from properties

String DPP_EndPoint= ExecutionUtil.getDynamicProcessProperty("DPP_EndPoint"); 
String DPP_Headers= ExecutionUtil.getDynamicProcessProperty("DPP_Headers"); 

// Splitting Headers to Array
String[] HeaderInfo = DPP_Headers.split("\\|");

//Iterating Through Documents Passed
for( int i = 0; i < dataContext.getDataCount(); i++ ) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);
    
    //Reading incoming data through BufferedReader
    reader = new BufferedReader(new InputStreamReader(is));
    inData = new StringBuffer();
    lineNum = 0;

    while ((line = reader.readLine()) != null) {
        inData.append(line);
    }
    
    // Posting Data to API
    def post = new URL(DPP_EndPoint).openConnection();
    def message = inData.toString()
    post.setRequestMethod("POST")
    post.setDoOutput(true);
    
    //Iterating through each header and splitting key value
    for (String header : HeaderInfo) {
        int iend = header.indexOf(":"); 
        int len = header.length();
        
        String key;
        String value;
        
        if (iend != -1) 
        {
            key = header.substring(0 , iend);
            value = header.substring(iend+2,len);
            post.setRequestProperty(key, value);
        }
    }
    
    post.getOutputStream().write(message.getBytes("UTF-8"));
    def postRC = post.getResponseCode();
    
    if(postRC.equals(200)) {
        outData = post.getInputStream().getText();
    }
    
    else{
        logger = ExecutionUtil.getBaseLogger();
        logger.warning(outData = "Error Code: " + post.getResponseCode() + " Response: " + post.getErrorStream().getText());
        outData = "Error Code: " + post.getResponseCode() + " Response: " + post.getErrorStream().getText();
    }
    
    
    is = new ByteArrayInputStream(outData.toString().getBytes());
    dataContext.storeStream(is, props);

}
