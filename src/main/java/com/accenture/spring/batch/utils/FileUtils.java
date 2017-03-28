package com.accenture.spring.batch.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.apache.commons.io.filefilter.WildcardFileFilter;

public class FileUtils {
	
	public static void moveFiles(String sourcePath, String destinationPath,
			String regex) {
		try {
			/*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(Calendar.getInstance().getTime());*/
			System.out.println("Start");
			File dir = new File(sourcePath.trim());
			FileFilter fileFilter = new WildcardFileFilter(regex);
			System.out.println("fileFilter: "+fileFilter);
			File[] files = dir.listFiles(fileFilter);
			System.out.println("Files: "+files);
			for (File file : files) {
				System.out.println("Moving file to :: " + destinationPath
						+ file.getName());
				Files.move(
						Paths.get(sourcePath + file.getName()),
						Paths.get(destinationPath + file.getName()),
						StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			System.out.println("Error while moving files");
		}

	}
	
	public static void copyFiles(String sourcePath, String destinationPath,
			String regex) {
		try {
			File dir = new File(sourcePath.trim());
			FileFilter fileFilter = new WildcardFileFilter(regex);
			File[] files = dir.listFiles(fileFilter);
				for (File file : files) {
					System.out.println("Copying file :: " + file.getName());
					Files.copy(Paths.get(sourcePath + file.getName()),
							Paths.get(destinationPath + file.getName()),
							StandardCopyOption.REPLACE_EXISTING);
				}
			}
		 catch (IOException e) {
			System.out.println("Exception in  DDCUtils.copyFiles ");
		}

	}

	public static void deleteFiles(String outputDir, String regex) {

		File dir = new File(outputDir.trim());

		FileFilter fileFilter = new WildcardFileFilter(regex);
		File[] files = dir.listFiles(fileFilter);
		for (File file : files) {

			if (file.delete()) {
				System.out.println("Deleting file : " + file);
			}
		}

	}

	public static void renameFiles(String outputDir, String name,String regex) {

		File dir = new File(outputDir.trim());
		System.out.println("dir: "+dir);
		FileFilter fileFilter = new WildcardFileFilter(regex);
		File[] files = dir.listFiles(fileFilter);
		for (File file : files) {
			
			System.out.println("Renaming file : " + file);
			file.renameTo(new File(outputDir + name));

		}

	}

	public static void purgeNdayOldFiles(String archiveDir, String regex,
			int purgeDuration) {

		File dir = new File(archiveDir.trim());
		FileFilter fileFilter = new WildcardFileFilter(regex);
		File[] files = dir.listFiles(fileFilter);
		if (files != null) {
			for (File file : files) {
				long diff = new Date().getTime() - file.lastModified();

				if (diff > purgeDuration * 24 * 60 * 60 * 1000) {
					if (file.delete()) {
						System.out.println("Purging file : " + file);
					}
				}
			}
		}

	}



}
