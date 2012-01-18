package de.davboecki.multimodworld.plugin;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileLoader {
	public static void save(Object obj,String path) throws Exception
	{
		ObjectOutputStream WriteObject = new ObjectOutputStream(new FileOutputStream(path));
		WriteObject.writeObject(obj);
		WriteObject.flush();
		WriteObject.close();
	}
	public static Object load(String path) throws Exception
	{
		ObjectInputStream ReadObject = new ObjectInputStream(new FileInputStream(path));
		Object result = ReadObject.readObject();
		ReadObject.close();
		return result;
	}
}
