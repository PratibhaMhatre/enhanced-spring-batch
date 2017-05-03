package com.accenture.spring.batch.util;

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
			
			File dir = new File(sourcePath.trim());
			FileFilter fileFilter = new WildcardFileFilter(regex);
			File[] files = dir.listFiles(fileFilter);
			for (File file : files) {
				Files.move(
						Paths.get(sourcePath + file.getName()),
						Paths.get(destinationPath + file.getName()),
						StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
		}

	}
	
	public static void copyFiles(String sourcePath, String destinationPath,
			String regex) {
		try {
			File dir = new File(sourcePath.trim());
			FileFilter fileFilter = new WildcardFileFilter(regex);
			File[] files = dir.listFiles(fileFilter);
				for (File file : files) {
					Files.copy(Paths.get(sourcePath + file.getName()),
							Paths.get(destinationPath + file.getName()),
							StandardCopyOption.REPLACE_EXISTING);
				}
			}
		 catch (IOException e) {
		}

	}

	public static void deleteFiles(String outputDir, String regex) {

		File dir = new File(outputDir.trim());

		FileFilter fileFilter = new WildcardFileFilter(regex);
		File[] files = dir.listFiles(fileFilter);
		for (File file : files) {

			if (file.delete()) {
			}
		}

	}

	public static void renameFiles(String outputDir, String name,String regex) {

		File dir = new File(outputDir.trim());
		FileFilter fileFilter = new WildcardFileFilter(regex);
		File[] files = dir.listFiles(fileFilter);
		for (File file : files) {
			
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
					}
				}
			}
		}

	}



}
