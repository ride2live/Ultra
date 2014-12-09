package com.fallen.ultra;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

public abstract class UtilsUltra {
	public static File getTestFileToWrite ()
	{
		File d =  new File( Environment.getExternalStorageDirectory(), "Ultratest");
		d.mkdirs();
		File f = new File(d, "test.m");
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return f;
	}

}
