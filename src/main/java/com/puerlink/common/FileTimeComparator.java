package com.puerlink.common;

import java.io.File;
import java.util.Comparator;

public class FileTimeComparator implements Comparator<File> {

	@Override
	public int compare(File file, File file2) {
		if (file == null && file2 == null)
		{
			return 0;
		}

		if (file == null)
		{
			return -1;
		}

		if (file2 == null)
		{
			return 1;
		}

		if (file.lastModified() < file2.lastModified())
		{
			return -1;
		}
		else if (file.lastModified() > file2.lastModified())
		{
			return 1;
		}

		return 0;
	}

}
