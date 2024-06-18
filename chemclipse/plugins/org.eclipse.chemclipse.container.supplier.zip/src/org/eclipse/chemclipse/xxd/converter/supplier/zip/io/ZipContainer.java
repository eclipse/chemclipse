/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.zip.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.chemclipse.container.definition.IFileContentProvider;
import org.eclipse.chemclipse.container.supplier.zip.internal.PathHelper;
import org.eclipse.chemclipse.logging.core.Logger;

public class ZipContainer implements IFileContentProvider {

	private final int BUFFER = 2048;
	private static final Logger logger = Logger.getLogger(ZipContainer.class);

	@Override
	public long getContentSize(File file) {

		try (ZipFile zipFile = new ZipFile(file)) {
			return zipFile.size();
		} catch(ZipException e) {
			logger.warn(e);
		} catch(IOException e) {
			logger.warn(e);
		}
		return 0;
	}

	@Override
	public File[] getContents(File file) {

		File[] contents = new File[0];
		try (ZipFile zipFile = new ZipFile(file)) {
			contents = new File[zipFile.size()];
			Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
			File destinationDirectory = new File(PathHelper.getStoragePathImport(), file.getName());
			destinationDirectory.mkdir();
			int i = 0;
			while(zipEntries.hasMoreElements()) {
				ZipEntry zipEntry = zipEntries.nextElement();
				if(zipEntry.isDirectory()) {
					contents[i] = createDir(destinationDirectory, zipEntry);
				} else {
					InputStream zipInputStream = zipFile.getInputStream(zipEntry);
					contents[i] = extractFile(destinationDirectory, zipEntry, zipInputStream);
				}
				i++;
			}
		} catch(IOException e) {
			logger.warn(e);
		}
		return contents;
	}

	private File createDir(File destinationDirectory, ZipEntry zipEntry) {

		File file = new File(getFileName(destinationDirectory, zipEntry));
		file.mkdir();
		return file;
	}

	private String getFileName(File destinationDirectory, ZipEntry zipEntry) {

		return destinationDirectory.getAbsolutePath() + File.separator + zipEntry.getName();
	}

	private File extractFile(File destinationDirectory, ZipEntry zipEntry, InputStream zipInputStream) {

		int count;
		byte[] data = new byte[BUFFER];
		File file = new File(getFileName(destinationDirectory, zipEntry));
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, BUFFER)) {
				while((count = zipInputStream.read(data, 0, BUFFER)) != -1) {
					bufferedOutputStream.write(data, 0, count);
				}
				bufferedOutputStream.flush();
			}
		} catch(FileNotFoundException e) {
			logger.warn(e);
		} catch(IOException e) {
			logger.warn(e);
		}
		return file;
	}
}
