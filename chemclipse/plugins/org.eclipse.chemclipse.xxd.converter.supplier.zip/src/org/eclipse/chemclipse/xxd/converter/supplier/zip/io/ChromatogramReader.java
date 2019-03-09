/*******************************************************************************
 * Copyright (c) 2012, 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.zip.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDReader;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.zip.internal.converter.IConstants;
import org.eclipse.chemclipse.xxd.converter.supplier.zip.internal.converter.PathHelper;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReader extends AbstractChromatogramMSDReader {

	private final int BUFFER = 2048;
	private static final Logger logger = Logger.getLogger(ChromatogramReader.class);
	private List<String> chromatogramFileExtensions;
	private String defaultFileExtension = "";

	public ChromatogramReader() {
		/*
		 * All chromatogram file extensions.
		 */
		chromatogramFileExtensions = new ArrayList<>();
		IChromatogramConverterSupport support = ChromatogramConverterMSD.getInstance().getChromatogramConverterSupport();
		for(ISupplier supplier : support.getSupplier()) {
			chromatogramFileExtensions.add(supplier.getFileExtension());
		}
		/*
		 * Default extension.
		 */
		try {
			ISupplier supplier = support.getSupplier(IConstants.CONVERTER_ID);
			defaultFileExtension = supplier.getFileExtension();
		} catch(NoConverterAvailableException e) {
			logger.warn(e);
		}
	}

	@Override
	public IChromatogramMSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		File fileChromatogram = extractChromatogramFile(file, monitor);
		IProcessingInfo<IChromatogramMSD> processingInfo = ChromatogramConverterMSD.getInstance().convert(fileChromatogram, monitor);
		return processingInfo.getProcessingResult();
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		File fileChromatogram = extractChromatogramFile(file, monitor);
		IProcessingInfo<IChromatogramOverview> processingInfo = ChromatogramConverterMSD.getInstance().convertOverview(fileChromatogram, monitor);
		return processingInfo.getProcessingResult();
	}

	private File extractChromatogramFile(File file, IProgressMonitor monitor) throws IOException {

		/*
		 * Extract the ZIP file.
		 */
		File destinationDirectory = PathHelper.getStoragePathImport();
		File extractedFile = extractFileToLocalDirectory(file, destinationDirectory, monitor);
		File fileChromatogram;
		/*
		 * If no regular chromatogram file could be found.
		 */
		if(extractedFile == null) {
			String fileName = getFileName(file);
			fileChromatogram = new File(destinationDirectory + File.separator + fileName);
		} else {
			fileChromatogram = extractedFile;
		}
		return fileChromatogram;
	}

	private String getFileName(File file) {

		/*
		 * Removes the ".zip" extension.
		 */
		String fileName;
		if(file.getName().endsWith(".zip")) {
			fileName = file.getName().substring(0, file.getName().length() - 4);
		} else {
			fileName = file.getName() + defaultFileExtension;
		}
		return fileName;
	}

	/**
	 * This method returns null if no directory have been found.
	 *
	 * @param file
	 * @param destinationDirectory
	 * @param monitor
	 * @return File
	 * @throws IOException
	 */
	private File extractFileToLocalDirectory(File file, File destinationDirectory, IProgressMonitor monitor) throws IOException {

		File chromatogram = null;
		FileInputStream fileInputStream = new FileInputStream(file);
		ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
		ZipEntry zipEntry;
		while((zipEntry = zipInputStream.getNextEntry()) != null) {
			if(zipEntry.isDirectory()) {
				/*
				 * The first directory will be taken as the chromatogram directory. Agilent chromatograms are stored for example in a directory called "*.D".
				 */
				if(chromatogram == null) {
					chromatogram = createDir(destinationDirectory, zipEntry);
				} else {
					createDir(destinationDirectory, zipEntry);
				}
			} else {
				/*
				 * If the chromatogram is still null, there seems to be a file which contains the chromatogram.
				 * But each file will be checked against the file extensions that are importable.
				 * Cause chromatograms will be stored with additional information normally, like method files ...
				 */
				if(chromatogram == null) {
					File chromatogramFile = extractFile(destinationDirectory, zipEntry, zipInputStream);
					for(String chromatogramFileExtension : chromatogramFileExtensions) {
						if(chromatogramFile.getAbsolutePath().endsWith(chromatogramFileExtension)) {
							chromatogram = chromatogramFile;
						}
					}
				} else {
					extractFile(destinationDirectory, zipEntry, zipInputStream);
				}
			}
		}
		return chromatogram;
	}

	private File createDir(File destinationDirectory, ZipEntry zipEntry) {

		File file = new File(getFileName(destinationDirectory, zipEntry));
		file.mkdir();
		return file;
	}

	private String getFileName(File destinationDirectory, ZipEntry zipEntry) {

		return destinationDirectory.getAbsolutePath() + File.separator + zipEntry.getName();
	}

	private File extractFile(File destinationDirectory, ZipEntry zipEntry, ZipInputStream zipInputStream) throws IOException {

		int count;
		byte[] data = new byte[BUFFER];
		File file = new File(getFileName(destinationDirectory, zipEntry));
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, BUFFER);
		while((count = zipInputStream.read(data, 0, BUFFER)) != -1) {
			bufferedOutputStream.write(data, 0, count);
		}
		bufferedOutputStream.flush();
		bufferedOutputStream.close();
		//
		return file;
	}
}
