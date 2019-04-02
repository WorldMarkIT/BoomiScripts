/*
Author: Saagar Kamrani
Year: 2018
Description: This script reverses CSV documents. Used before removing duplicates so that newer document remains in the input buffer.
*/

import java.util.Properties;
import java.io.InputStream;
import java.io.BufferedReader;

String LINE_SEPARATOR = System.getProperty("line.separator");

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
   InputStream is = dataContext.getStream(i);
   Properties props = dataContext.getProperties(i);
   BufferedReader reader = new BufferedReader(new InputStreamReader(is));
   String outString = "";
   while ((line = reader.readLine()) != null) {
      outString = line + LINE_SEPARATOR  + outString;
   }
   is = new ByteArrayInputStream(outString.getBytes("UTF-8"));
   dataContext.storeStream(is, props);
}
