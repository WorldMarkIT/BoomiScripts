import java.util.Properties;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import com.boomi.execution.ExecutionUtil;


		//Read DPPs into local variables
		String DPP_ReportHeader=ExecutionUtil.getDynamicProcessProperty("DPP_ReportHeader"); 
		int DPP_NoOfHeaderLines =Integer.parseInt(ExecutionUtil.getDynamicProcessProperty("DPP_NoOfHeaderLines"));

		
		//Initialize local variables
		String LINE_SEPARATOR = System.getProperty("line.separator");
		logger = ExecutionUtil.getBaseLogger();
		
		for( int i = 0; i < dataContext.getDataCount(); i++ ) 
		{
		 
			InputStream is = dataContext.getStream(i);
			Properties props = dataContext.getProperties(i);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuffer outData = new StringBuffer();
			String line;
			int lineNum = 0;
			int headerStartPosition=-1;
			int headerLinesCounter=DPP_NoOfHeaderLines;
			while ((line = reader.readLine()) != null) 
			{
				
				if(line.indexOf(DPP_ReportHeader)>0) {
					headerLinesCounter=1;
					headerStartPosition=lineNum;
					
				}
				else {
					if(headerLinesCounter<DPP_NoOfHeaderLines) {headerLinesCounter++;}
					else {
						if(headerStartPosition>0)
						{
					outData.append(line);
					outData.append(LINE_SEPARATOR );
						}
					}
				}
				lineNum++;
			}
		
		is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
		dataContext.storeStream(is, props);
		}
