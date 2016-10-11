package io.github.TcFoxy.ArenaTOW.updater;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

public class FileUpdater {

	File oldFile;
	File backupDir;

	Version updateVersion;
	Version oldVersion;

	public static enum SearchType{
		CONTAINS, MATCHES
	}
	public static enum UpdateType{
		ADDAFTER, REPLACE, DELETE, DELETEALLFROM, ADDBEFORE
	}

	public static class Update{
		public final UpdateType type;
		public final String search;
		public final String[] updates;
		public final SearchType searchType;
		public Update(String search, UpdateType type, SearchType searchType, String... strings){
			this.type = type;
			this.search = search;
			this.updates = strings;
			this.searchType = searchType;
		}
	}

	HashMap<String, Update> updates = new HashMap<String, Update>();
	HashMap<String, String> replaces = new HashMap<String, String>();

	public FileUpdater(File oldFile, File backupDir, Version newVersion, Version oldVersion){
		this.oldFile = oldFile.getAbsoluteFile();
		this.backupDir = backupDir.getAbsoluteFile();
		this.updateVersion = newVersion;
		this.oldVersion = oldVersion;
	}

	public void delete(String str){
		updates.put(str, new Update(str,UpdateType.DELETE, SearchType.MATCHES, ""));
	}

	public void addAfter(String str, String...strings){
		updates.put(str, new Update(str,UpdateType.ADDAFTER, SearchType.MATCHES, strings));
	}

	public void addBefore(String str, String...strings){
		updates.put(str, new Update(str,UpdateType.ADDBEFORE, SearchType.MATCHES, strings));
	}

	public void replace(String str, String...strings){
		updates.put(str, new Update(str,UpdateType.REPLACE, SearchType.MATCHES, strings));
	}

	public void deleteAllFrom(String str){
		updates.put(str, new Update(str,UpdateType.DELETEALLFROM, SearchType.MATCHES,""));
	}

	public void replaceAll(String string, String string2) {
		replaces.put(string, string2);
	}

	public Version update() throws IOException {
		System.out.println("[Plugin Updater] updating " + oldFile.getName() +" from "+ oldVersion+" to " + updateVersion);
		System.out.println("[Plugin Updater] old version backup inside of " + backupDir.getAbsolutePath());

		BufferedReader br;
		BufferedWriter fw;
		File tempFile;
		try {
			br = new BufferedReader(new FileReader(oldFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		try {
			tempFile = new File(backupDir+"/temp-"+new Random().nextInt()+".yml");
			fw = new BufferedWriter(new FileWriter(tempFile));
		} catch (IOException e) {
			e.printStackTrace();
			try{br.close();}catch (Exception e2){}
			return null;
		}

		boolean quit = false;
		String line =null;
		try {
			while ((line = br.readLine()) != null && !quit){
				boolean foundMatch = false;
				for (Entry<String,Update> entry : updates.entrySet()){
					Update up = entry.getValue();
					switch(up.searchType){
					case MATCHES:
						if (!line.matches(up.search)) {
							continue;}
						break;
					case CONTAINS:
						if (!line.contains(up.search)){
							continue;}
						break;
					}
					foundMatch = true;
					if (up.type == UpdateType.DELETEALLFROM){
						quit = true;
						break;
					}

					if (up.type == UpdateType.ADDAFTER){ /// add the original line before
						fw.write(line+"\n");}

					/// Add all the lines
					if (up.type != UpdateType.DELETE)
						for (String update: up.updates){
							fw.write(update+"\n");}

					if (up.type == UpdateType.ADDBEFORE){ /// add original line after
						fw.write(line+"\n");}

					break;
				}
				if (!foundMatch){
					for (Entry<String,String> rs : replaces.entrySet()){
						line = line.replaceAll(rs.getKey(), rs.getValue());
					}
					fw.write(line+"\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {br.close();} catch (Exception e) {}
			try {fw.close();} catch (Exception e) {}
		}
		String nameWithoutExt = oldFile.getName().replaceFirst("[.][^.]+$", "");
		String ext = oldFile.getName().substring(nameWithoutExt.length()+1);
		if (ext.isEmpty())
			ext =".bk";
		copy(oldFile, new File(backupDir+"/"+nameWithoutExt+"."+oldVersion+"."+ext));
		return renameTo(tempFile, oldFile) ? updateVersion : null;
	}

	public static boolean renameTo(File file1, File file2) throws IOException {
		if (!file1.exists()){
			throw new IOException(file1.getAbsolutePath()+" does not exist");}
		/// That's right, I can't just rename the file, i need to move and delete
		if (isWindows()){
			File temp = new File(file2.getAbsoluteFile() +"."+new Random().nextInt()+".backup");
			if (temp.exists()){
				temp.delete();}

			if (file2.exists()){
				file2.renameTo(temp);
				file2.delete();
			}
			if (!file1.renameTo(file2)){
				throw new IOException(temp.getAbsolutePath() +" could not be renamed to " + file2.getAbsolutePath());
			} else {
				temp.delete();
			}
		} else {
			if (!file1.renameTo(file2)){
				throw new IOException(file1.getAbsolutePath() +" could not be renamed to " + file2.getAbsolutePath());
			}
		}
		return true;
	}


	public void copy(File file1, File file2) {
		InputStream inputStream = null;
		OutputStream out= null;
		try{
			if (file2.exists()){
				file2.delete();}
			inputStream = new FileInputStream(file1);
			out=new FileOutputStream(file2);
			byte buf[]=new byte[1024];
			int len;
			while((len=inputStream.read(buf))>0){
				out.write(buf,0,len);}
		} catch (Exception e){
			e.printStackTrace();
		} finally{
			try{ out.close();} catch (Exception e){}
			try{ inputStream.close();} catch (Exception e){}
		}
	}

	public static void deleteIfExists(File file) {
		if (file.exists()){
			file.delete();}
	}

	public static void makeIfNotExists(File file) {
		if (!file.exists()){
			file.mkdir();}
	}

	public static void moveIfExists(File file, File file2) {
		if (file.exists()){
			try {
				FileUpdater.renameTo(file,file2);
			} catch (IOException e) {
				e.printStackTrace();
			}
			deleteIfExists(file);
		}
	}

    public static boolean isWindows() {
        return System.getProperty("os.name").toUpperCase().contains("WINDOWS");
    }
}
