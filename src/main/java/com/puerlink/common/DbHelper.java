package com.puerlink.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

public class DbHelper {
	
	public static class Record
	{
		private List<String> mColumns = new ArrayList<String>();
		private Hashtable<String, String> mValues = new Hashtable<String, String>();

		public boolean addColumn(String colName, String value)
		{
			if (!TextUtils.isEmpty(colName))
			{
				String lowerColName = colName.toLowerCase(Locale.getDefault());
				mColumns.add(lowerColName);
				mValues.put(lowerColName, value);
				return true;
			}
			return false;
		}
		
		public String getString(String colName)
		{
			if (!TextUtils.isEmpty(colName))
			{
				String lowerColName = colName.toLowerCase(Locale.getDefault());
				if (mValues.containsKey(lowerColName))
				{
					return mValues.get(lowerColName);
				}
			}
			return null;
		}
		
		public String getString(int colIndex)
		{
			if (colIndex >= 0 && colIndex < mColumns.size())
			{
				return getString(mColumns.get(colIndex));
			}
			return null;
		}
		
		public int getInt(String colName)
		{
			String value = getString(colName);
			try
			{
				return Integer.parseInt(value);
			}
			catch (Exception exp)
			{
				return Integer.MAX_VALUE;
			}
		}
		
		public int getInt(int colIndex)
		{
			if (colIndex >= 0 && colIndex < mColumns.size())
			{
				return getInt(mColumns.get(colIndex));
			}
			return Integer.MAX_VALUE;
		}
	}

	public static SQLiteDatabase getDB(Context context, String dbName)
	{
		if (context != null)
		{
			context = context.getApplicationContext();
		}
		
		try
		{
			return context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
		}
		catch (Exception exp)
		{
			return null;
		}
	}
	
	public static boolean dropDatabase(Context context, String dbName)
	{
		if (context != null)
		{
			context = context.getApplicationContext();
		}
		
		try
		{
			return context.deleteDatabase(dbName);
		}
		catch (Exception exp)
		{
			return false;
		}
	}
	
	public static boolean hasTable(SQLiteDatabase db, String tblName)
	{
		if (db != null && db.isOpen())
		{
			String sql = "SELECT COUNT(*) AS C From SQLite_Master Where Type ='table' AND Name ='" + tblName + "';";
	        Cursor cursor = db.rawQuery(sql, null);
	        try
	        {
		        if(cursor != null && cursor.getCount() > 0 && cursor.moveToNext())
		        {
		        	return cursor.getInt(0) > 0;
		        }
	        }
	        catch (Exception exp)
	        {
	        	return false;
	        }
	        finally
	        {
	        	if (cursor != null && !cursor.isClosed())
	        	{
	        		cursor.close();
	        	}
	        }
		}
		return false;
	}
	
	public static boolean dropTable(SQLiteDatabase db, String tblName)
	{
		if (db != null && db.isOpen() && !db.isReadOnly())
		{
			if (hasTable(db, tblName))
			{
				String sqlDropCmd = "DROP TABLE " + tblName;
				try
				{
					db.execSQL(sqlDropCmd);
					return true;
				}
				catch (Exception exp)
				{
					;
				}
			}
		}
		return false;
	}
	
	public static void clearTable(SQLiteDatabase db, String tblName, boolean resetSequence)
	{
		if (db != null && db.isOpen())
		{
			try
			{
				String delSQL = "DELETE FROM " + tblName;
				db.execSQL(delSQL);
				
				if (resetSequence)
				{
					String resetSQL = "UPDATE Sqlite_Sequence SET Seq=0 WHERE Name='" + tblName + "'";
					db.execSQL(resetSQL);
				}
			}
			catch (Exception exp)
			{
				;
			}
		}
	}
	
	public static boolean createTable(SQLiteDatabase db, String tblName, String[] uniqueItems, String...columns)
	{
		if (db != null && db.isOpen() && !db.isReadOnly() && columns.length > 0)
		{
			if (!hasTable(db, tblName))
			{
				StringBuilder sqlCreateCmd = new StringBuilder();
				sqlCreateCmd.append("CREATE TABLE ");
				sqlCreateCmd.append(tblName);
				sqlCreateCmd.append("(");
				
				for (int i = 0; i < columns.length; i++)
				{
					if (i > 0)
					{
						sqlCreateCmd.append(",");
					}
					sqlCreateCmd.append(columns[i]);
				}
				
				sqlCreateCmd.append(");");
				
				if (uniqueItems != null && uniqueItems.length > 0)
				{
					for (int i = 1; i <= uniqueItems.length; i++)
					{
						String uniqueName = tblName + "_ui" + i;
						String uniqueColumns = uniqueItems[i - 1];
						sqlCreateCmd.append("\r\nCREATE UNIQUE INDEX " + uniqueName + " ON " + tblName + "(" + uniqueColumns + ");");
					}
				}
				
				try
				{
					db.execSQL(sqlCreateCmd.toString());
					return true;
				}
				catch (Exception exp)
				{
					;
				}
			}
		}
		return false;
	}
	
	public static boolean createTable(SQLiteDatabase db, String tblName, String...columns)
	{
		return createTable(db, tblName, null, columns);
	}
	
	private static Record selectOne(SQLiteDatabase db, String tblName, String[] columns, String whereText, String orderBy)
	{
		if (db != null && db.isOpen())
		{
			try
			{
				Cursor c = db.query(tblName, columns, whereText, null, null, null, orderBy, "0, 1");
				if (c != null)
				{
					try
					{
						if (c.moveToNext())
						{
							Record result = new Record();
							
							int colCount = c.getColumnCount();
							for (int i = 0; i < colCount; i++)
							{
								String colName = c.getColumnName(i);
								String value = c.getString(i);
								result.addColumn(colName, value == null ? "" : value);
							}
							
							return result;
						}
					}
					catch (Exception exp)
					{
						;
					}
					finally
					{
						c.close();
					}
				}
			}
			catch (Exception exp)
			{
				;
			}
		}
		return null;
	}
	
	public static Record selectOne(SQLiteDatabase db, String tblName, String[] columns, String whereText)
	{
		return selectOne(db, tblName, columns, whereText, null);
	}
	
	public static Record selectOneByOrder(SQLiteDatabase db, String tblName, String[] columns, String whereText, String orderBy)
	{
		return selectOne(db, tblName, columns, whereText, orderBy);
	}
	
	private static List<Record> selectAll(SQLiteDatabase db, String tblName, String[] columns, String whereText, String orderBy)
	{
		if (db != null && db.isOpen())
		{
			try
			{
				Cursor c = db.query(tblName, columns, whereText, null, null, null, orderBy);
				if (c != null)
				{
					try
					{
						int colCount = c.getColumnCount();
						
						List<Record> result = new ArrayList<Record>();
						while (c.moveToNext())
						{
							Record r = new Record();
							for (int i = 0; i < colCount; i++)
							{
								String colName = c.getColumnName(i);
								String value = c.getString(i);
								r.addColumn(colName, value == null ? "" : value);
							}
							result.add(r);
						}
						return result;
					}
					catch (Exception exp)
					{
						;
					}
					finally
					{
						c.close();
					}
				}
			}
			catch (Exception exp)
			{
				;
			}
		}
		return null;
	}
	
	public static List<Record> selectAll(SQLiteDatabase db, String tblName, String[] columns, String whereText)
	{
		return selectAll(db, tblName, columns, whereText, null);
	}
	
	public static List<Record> selectAllByOrder(SQLiteDatabase db, String tblName, String[] columns, String whereText, String orderBy)
	{
		return selectAll(db, tblName, columns, whereText, orderBy);
	}
	
	private static List<Record> selectPage(SQLiteDatabase db, String tblName, String[] columns, String whereText, String orderBy, int offset, int pageSize)
	{
		if (db != null && db.isOpen())
		{
			try
			{
				Cursor c = db.query(tblName, columns, whereText, null, null, null, orderBy, offset + "," + pageSize);
				if (c != null)
				{
					try
					{
						int colCount = c.getColumnCount();
						
						List<Record> result = new ArrayList<Record>();
						while (c.moveToNext())
						{
							Record r = new Record();
							for (int i = 0; i < colCount; i++)
							{
								String colName = c.getColumnName(i);
								String value = c.getString(i);
								r.addColumn(colName, value == null ? "" : value);
							}
							result.add(r);
						}
						return result;
					}
					catch (Exception exp)
					{
						;
					}
					finally
					{
						c.close();
					}
				}
			}
			catch (Exception exp)
			{
				;
			}
		}
		return null;
	}
	
	public static List<Record> selectPage(SQLiteDatabase db, String tblName, String[] columns, String whereText, int offset, int pageSize)
	{
		return selectPage(db, tblName, columns, whereText, null, offset, pageSize);
	}
	
	public static List<Record> selectPageByOrder(SQLiteDatabase db, String tblName, String[] columns, String whereText, String orderBy, int offset, int pageSize)
	{
		return selectPage(db, tblName, columns, whereText, orderBy, offset, pageSize);
	}
	
	public static boolean insert(SQLiteDatabase db, String tblName, ContentValues values, boolean replace)
	{
		if (db != null && db.isOpen() && !db.isReadOnly())
		{
			if (values != null && values.size() > 0)
			{
				try
				{
					if (!replace)
					{
						return db.insert(tblName, null, values) != -1;
					}
					else
					{
						return db.insertWithOnConflict(tblName, null, values, SQLiteDatabase.CONFLICT_REPLACE) != -1;
					}
				}
				catch (Exception exp)
				{
					;
				}
			}
		}
		return false;
	}
	
	public static boolean update(SQLiteDatabase db, String tblName, ContentValues values, String whereText)
	{
		if (db != null && db.isOpen() && !db.isReadOnly())
		{
			if (values != null && values.size() > 0)
			{
				try
				{
					return db.update(tblName, values, whereText, null) > 0;
				}
				catch (Exception exp)
				{
					;
				}
			}
		}
		return false;
	}
	
	public static boolean delete(SQLiteDatabase db, String tblName, String whereClause, String[] whereArgs)
	{
		if (db != null && db.isOpen() && !db.isReadOnly())
		{
			if (whereArgs != null && whereArgs.length > 0)
			{
				try
				{
					return db.delete(tblName, whereClause, whereArgs) > 0;
				}
				catch (Exception exp)
				{
				}
			}
		}
		return false;
	}
	
}
