/*
This script reads excel file and converts to a useable flat file profile
Originally found on:
https://community.boomi.com/docs/DOC-2280

Requires apache 3.13 to run and installed on Boomi platform, instructions can be found on above link.

Some modifications were made to handle blank fields and long numbers

*/


import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

for( int i = 0; i < dataContext.getDataCount(); i++ ) {
InputStream is = dataContext.getStream(i);
Properties props = dataContext.getProperties(i);
DataFormatter formatter = new DataFormatter( Locale.default );
Workbook wb = WorkbookFactory.create( is );
List sheetList = wb.sheets;
StringBuffer sb = new StringBuffer();
for ( int j = 0; j < sheetList.size(); j++) {


Sheet sheet = wb.getSheetAt( j );

// Decide which rows to process
int rowStart = Math.min(15, sheet.getFirstRowNum());
int rowEnd = Math.max(1400, sheet.getLastRowNum());

for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
Row r = sheet.getRow(rowNum);
if (r == null) {
continue;
}

int lastColumn = Math.max(r.getLastCellNum()-r.getFirstCellNum(),2);

for (int cn = 0; cn < lastColumn; cn++) {
Cell c = r.getCell(cn, Row.RETURN_BLANK_AS_NULL);
if (c == null) {
sb.append("");
sb.append(",");
} else {
switch( c.getCellType() ) {
case Cell.CELL_TYPE_NUMERIC : 
if (DateUtil.isCellDateFormatted(c)) {
sb.append("\"" + formatter.formatCellValue( c ) +"\"" );
sb.append(",");
} else {
int test1 = (int)c.getNumericCellValue(); 
strCellValue = String.valueOf(test1);

sb.append("\"" + strCellValue + "\"");
sb.append(",");
}
break;
case Cell.CELL_TYPE_STRING: 
sb.append( "\"" + c.getStringCellValue() +"\"" );
sb.append(",");
break;
case Cell.CELL_TYPE_FORMULA: 
sb.append( "\"" + c.getCellFormula() +"\"" );
sb.append(",");
break;
case Cell.CELL_TYPE_BOOLEAN: 
sb.append( "\"" + c.getBooleanCellValue() +"\"" );
sb.append(",");
break; 
case Cell.CELL_TYPE_BLANK: 
sb.append("");
sb.append(",");
break;
default: 
sb.append("");
sb.append(",");
break;
}
}
}
if ( sb.length() > 0 ) {
sb.setLength(sb.length() - 1);
}
sb.append("\r\n");
}
String output = sb.toString();
sb.setLength(0);
InputStream is2= new ByteArrayInputStream(output.getBytes());
dataContext.storeStream(is2, props);}
}
