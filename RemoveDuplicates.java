import java.util.Properties;
import java.io.InputStream;

/*
This script outputs a unique group of documents based on the value of a
"FILTER_BY_VALUE" user defined document property. When a new FILTER_BY_VALUE
value is encountered, the document is output and the FILTER_BY_VALUE recorded.
If the same FILTER_BY_VALUE is encountered in the same group of documents,
subsequent documents are silently skipped and not output.

This can be used to filter out "duplicate" documents within the same
document group.

Context: Done to clear out any duplicate documents in the same file, to increase performance of stored procedure which finds existing deals/customers in db

*/

// Name of User Defined Document Property that contains the filter by values.
filterByValuePropName = "DDP_UniqueVal";

Set docList = new HashSet();

// Loop through documents and inspect the filter-by-values. If not encountered before,
// store the value in the HashSet and output the document, otherwise silently skip it.

for ( int i = 0; i < dataContext.getDataCount(); i++ ) {
  InputStream is = dataContext.getStream(i);
  Properties props = dataContext.getProperties(i);

  recordId = props.getProperty("document.dynamic.userdefined." + filterByValuePropName);

  if ( docList.contains(recordId) ) {

    // Skip the document by simply not adding it to the storeStream.
    continue;

  } else {

    docList.add(recordId);

    dataContext.storeStream(is, props);

  }

}
