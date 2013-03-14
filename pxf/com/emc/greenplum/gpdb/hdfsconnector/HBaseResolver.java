package com.emc.greenplum.gpdb.hdfsconnector;

import com.emc.greenplum.gpdb.hadoop.io.GPDBWritable;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.lang.reflect.Method;
import java.util.HashMap;

/*
 * The Bridge API resolver for gphbase protocol.
 * The class is responsible to convert rows from HBase scans (returned as Result objects)
 * into a List of OneField objects which later translates into GPDBWritable objects.
 * That also includes the conversion process of each HBase column's value into its GP assigned type.
 *
 * Currently, the class assumes all HBase values are stored as String object Bytes encoded
 */
class HBaseResolver implements IFieldsResolver
{
	HBaseMetaData conf;

	HBaseResolver(HBaseMetaData configuration) throws Exception
	{
		conf = configuration;
	}

	public List<OneField> GetFields(OneRow onerow) throws Exception
	{
		Result result = (Result)onerow.getData();
		LinkedList<OneField> row = new LinkedList<OneField>();

		for (int i = 0; i < conf.columns(); ++i)
		{
			HBaseColumnDescriptor column = (HBaseColumnDescriptor)conf.getColumn(i);
			byte[] value;
			
			if (column.isRowColumn()) // if a row column is requested
				value = result.getRow(); // just return the row key
			else // else, return column value
				value = getColumnValue(result, column);

			OneField oneField = new OneField();
			oneField.type = column.gpdbColumnType;
			oneField.val = convertToJavaObject(oneField.type, value);
			row.add(oneField);
		}
		return row;
	}

	/*
	 * Call the conversion function for type
	 */
	Object convertToJavaObject(int type, byte[] val) throws Exception
	{
		if (val == null)
			return null;
        try
        {
            switch(type)
            {
                case GPDBWritable.TEXT:
                case GPDBWritable.VARCHAR:
                case GPDBWritable.BPCHAR:
                    return Bytes.toString(val);

                case GPDBWritable.INTEGER:
                    return Integer.parseInt(Bytes.toString(val));

                case GPDBWritable.BIGINT:
                    return Long.parseLong(Bytes.toString(val));

                case GPDBWritable.SMALLINT:
                    return Short.parseShort(Bytes.toString(val));

                case GPDBWritable.REAL:
                    return Float.parseFloat(Bytes.toString(val));

                case GPDBWritable.FLOAT8:
                    return Double.parseDouble(Bytes.toString(val));

                case GPDBWritable.BYTEA:
                    return val;

                default:
                    throw new Exception("GPHBase doesn't support data type " + type + " yet");
            }
        } catch (NumberFormatException e)
        {
            // Replace exception with BadRecordException
            throw new BadRecordException();
        }
	}

	byte[] getColumnValue(Result result, HBaseColumnDescriptor column)
	{
		// if column does not contain a value, return null
		if (!result.containsColumn(column.columnFamilyBytes(),
								   column.qualifierBytes()))
			return null;

		// else, get the latest version of the requested column
		KeyValue keyvalue = result.getColumnLatest(column.columnFamilyBytes(),
												   column.qualifierBytes());
		return keyvalue.getValue();
	}
}