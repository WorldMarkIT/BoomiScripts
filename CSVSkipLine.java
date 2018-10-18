/*
Author: Saagar Kamrani
Date: 2018-10-18
Description: This script takes input as CSV file and removes first line/multiple header lines.
*/

//This script strips the first line out of each document and outputs the rest of the document contents unaltered.
newline = System.getProperty("line.separator");

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
  InputStream is = dataContext.getStream(i);
  Properties props = dataContext.getProperties(i);

  reader = new BufferedReader(new InputStreamReader(is));
  outData = new StringBuffer();
  lineNum = 0;

  while ((line = reader.readLine()) != null) {
    // Skip first line
    if (lineNum==0) {
      lineNum++;
      continue;
    }

    outData.append(line);
    outData.append(newline);
  }

  is = new ByteArrayInputStream(outData.toString().getBytes());
  dataContext.storeStream(is, props);
}
