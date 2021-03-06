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

		logger = ExecutionUtil.getBaseLogger();


		// read DPPs into local variables
		String LINE_SEPARATOR = System.getProperty("line.separator");
		String DPP_XMLRootTag= ExecutionUtil.getDynamicProcessProperty("DPP_XMLRootTag"); 
		String DPP_EndOfRecord= ExecutionUtil.getDynamicProcessProperty("DPP_EndOfRecord"); 
		int    DPP_DataStartPosition = Integer.parseInt(ExecutionUtil.getDynamicProcessProperty("DPP_DataStartPosition"));
		
		for( int i = 0; i < dataContext.getDataCount(); i++ ) 
		{
		  
			//Initialize fields
			InputStream is = dataContext.getStream(i);
			Properties props = dataContext.getProperties(i);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuffer outData = new StringBuffer();
			String line;
			int lineNum = 0;
                        int recordCounter=0;
			ArrayList<String> columnNameList = new ArrayList<String>();
			ArrayList<String> columnValueList = new ArrayList<String>();
			outData.append("<"+DPP_XMLRootTag+">"); //create root tag for the xml
			outData.append(LINE_SEPARATOR );
			int previousDataLineNum=0; 
			String previousColumnName="";
			while ((line = reader.readLine()) != null) //Read Each line 
			{
		
			    if(line.length()>7)// avoid empty/small lines
			        {
			              
					       if(line.substring(0,6).equals("UDF CR")) // check if the line contains "UDF CR" as field qualifier 
			               {
			             	   	String columnName = "";
			             	   	String columnValue = "";
			             	   	columnName =  line.substring(7,DPP_DataStartPosition-2); // read column header
			             	   	columnName = columnName.replaceAll("\\.",""); // replace all . with blank
		                        columnName = columnName.replaceAll("-","");  // replace all - with blank
		                        columnName = columnName.replaceAll("Ã½","");  // replace all ý with blank
		                        columnName = columnName.replaceAll("#","");  // replace all # with blank
		                        columnName = columnName.replaceAll("@","");  // replace all @ with blank
		                        columnName = columnName.replaceAll("\\?","");  // replace all ? with blank
		                        columnName= columnName.trim();                // remove all leading/trailing spaces 
		                        columnName = columnName.replaceAll("\\s","_"); // replace all whitespace with _
			                        
			                     if(line.length()<=DPP_DataStartPosition-1 ) {} // do nothing as it does not contain any data                   	 
			                     else 
			                     {
			                       	columnValue = line.substring(DPP_DataStartPosition-1,line.length()); // read column value
			                        columnValue=columnValue.trim();
		                    	        columnValue=columnValue.replaceAll("&","&amp;");
		                    	        columnValue=columnValue.replaceAll("<","&lt;");
		                    	        columnValue=columnValue.replaceAll(">","&gt;");
		                    	        columnValue=columnValue.replaceAll("\"","&quot;");
		                    	        columnValue=columnValue.replaceAll("\'","&apos;");
			                     }
	    						if(columnName.equals(DPP_EndOfRecord)) // check if this is end of record Identifier
								{
	    							String columnHeaderRecord = "";
	    							String columnValueRecord = ""; 
									outData.append("<"+DPP_XMLRootTag.substring(0, DPP_XMLRootTag.length()-1)+">"); // create root tag for a record
									outData.append(LINE_SEPARATOR );
									for (int j=0 ; j< columnNameList.size();j++)
									{
										String columnNameStr = "<"+columnNameList.get(j)+">"+columnValueList .get(j)+"</"+columnNameList.get(j)+">"; //generate XML tag
										outData.append(columnNameStr); // append a XML tag to outData Buffer 
										outData.append(LINE_SEPARATOR );
									}
									outData.append("</"+DPP_XMLRootTag.substring(0, DPP_XMLRootTag.length()-1)+">"); //create end tag for a record
									outData.append(LINE_SEPARATOR );
									columnNameList.clear();
									columnValueList.clear();
                                                                        recordCounter++;
									
								}  
	    						else 
	    						{
	    								columnNameList.add(columnName); // add field name to a list
	    								columnValueList.add(columnValue); // add field value to a list
	    						}
									
			               }
			               else 
			               {
				            	if(line.length()>DPP_DataStartPosition-1)
				            	{
				            		   if(line.substring(0, DPP_DataStartPosition-2).trim().equals("") ) 
				            		   {
				                		   String tempStr=line.trim();
										   tempStr=tempStr.replaceAll("&","&amp;");
		                    	           tempStr=tempStr.replaceAll("<","&lt;");
		                    	           tempStr=tempStr.replaceAll(">","&gt;");
		                    	           tempStr=tempStr.replaceAll("\"","&quot;");
		                    	           tempStr=tempStr.replaceAll("\'","&apos;");
										   columnValueList.set(columnValueList.size()-1, columnValueList.get(columnValueList.size()-1)+tempStr);
				            		   }
				            		   else 
				            		   {
				            			   if(line.substring(0, DPP_DataStartPosition-2).trim().equals(".")) 
				            			   {
						        			   String tempStr=line.substring(DPP_DataStartPosition-1,line.length()).trim();
										       tempStr=tempStr.replaceAll("&","&amp;");
		                    	               tempStr=tempStr.replaceAll("<","&lt;");
		                    	               tempStr=tempStr.replaceAll(">","&gt;");
		                    	               tempStr=tempStr.replaceAll("\"","&quot;");
		                    	               tempStr=tempStr.replaceAll("\'","&apos;");
											   columnValueList.set(columnValueList.size()-1, columnValueList.get(columnValueList.size()-1)+","+tempStr);
				            			   }
				            		   }
				            	}
			            	}
			        }
			        lineNum++;
			}
			outData.append("</"+DPP_XMLRootTag+">");
                         ExecutionUtil.setDynamicProcessProperty("DPP_TotalRecords", Integer.toString(recordCounter), false); 
			is = new ByteArrayInputStream(outData.toString().getBytes("UTF-8"));
			dataContext.storeStream(is, props);
		}
