/*******************************************************************************
 * Copyright (c) 2012, 2024 Lablicate GmbH.
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.versions.VersionConstants;
import org.eclipse.chemclipse.xxd.converter.supplier.zip.internal.converter.PathHelper;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramWriter extends AbstractChromatogramMSDWriter {

	private static final int LEVEL = 9;
	private static final int METHOD = ZipOutputStream.DEFLATED;

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileIsNotWriteableException, IOException {

		File destinationDirectory = PathHelper.getStoragePathExport();
		File destinationFile = new File(destinationDirectory.getAbsolutePath() + File.separator + chromatogram.getName());
		IProcessingInfo<File> processingInfo = ChromatogramConverterMSD.getInstance().convert(destinationFile, chromatogram, VersionConstants.CONVERTER_ID_CHROMATOGRAM, monitor);
		File export = processingInfo.getProcessingResult();
		monitor.subTask("Compress the chromatogram");
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));
		zipOutputStream.setLevel(LEVEL);
		zipOutputStream.setMethod(METHOD);
		/*
		 * Add files
		 */
		ZipEntry zipEntry = new ZipEntry(export.getName());
		FileInputStream fileInputStream = new FileInputStream(export);
		try {
			zipOutputStream.putNextEntry(zipEntry);
			for(int data = fileInputStream.read(); data != -1; data = fileInputStream.read()) {
				zipOutputStream.write(data);
			}
		} finally {
			fileInputStream.close();
		}
		/*
		 *
		 */
		zipOutputStream.flush();
		zipOutputStream.close();
		/*
		 * Delete the temporarily exported file
		 */
		export.delete();
	}
}
