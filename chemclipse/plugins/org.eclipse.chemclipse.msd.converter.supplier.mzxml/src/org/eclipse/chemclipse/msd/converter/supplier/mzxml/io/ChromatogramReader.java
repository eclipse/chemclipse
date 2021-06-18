/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.IFormat;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ReaderVersion20;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ReaderVersion21;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ReaderVersion30;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ReaderVersion31;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ReaderVersion32;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.xxd.converter.supplier.io.exception.UnknownVersionException;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReader extends AbstractChromatogramMSDReader implements IChromatogramMSDReader {

	public static IChromatogramMSDReader getReader(final File file) throws IOException {

		IChromatogramMSDReader chromatogramReader = null;
		//
		final FileReader fileReader = new FileReader(file);
		final char[] charBuffer = new char[350];
		fileReader.read(charBuffer);
		fileReader.close();
		//
		final String header = new String(charBuffer);
		if(header.contains(IFormat.MZXML_V_200)) {
			chromatogramReader = new ReaderVersion20(IFormat.CONTEXT_PATH_V_200);
		} else if(header.contains(IFormat.MZXML_V_210)) {
			chromatogramReader = new ReaderVersion21(IFormat.CONTEXT_PATH_V_210);
		} else if(header.contains(IFormat.MZXML_V_220)) {
			chromatogramReader = new ReaderVersion21(IFormat.CONTEXT_PATH_V_220);
		} else if(header.contains(IFormat.MZXML_V_300)) {
			chromatogramReader = new ReaderVersion30(IFormat.CONTEXT_PATH_V_300);
		} else if(header.contains(IFormat.MZXML_V_310)) {
			chromatogramReader = new ReaderVersion31(IFormat.CONTEXT_PATH_V_310);
		} else if(header.contains(IFormat.MZXML_V_320)) {
			chromatogramReader = new ReaderVersion32(IFormat.CONTEXT_PATH_V_320);
		} else {
			throw new UnknownVersionException();
		}
		//
		return chromatogramReader;
	}

	@Override
	public IChromatogramMSD read(final File file, final IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException, InterruptedException {

		final IChromatogramMSDReader chromatogramReader = getReader(file);
		if(chromatogramReader != null) {
			return chromatogramReader.read(file, monitor);
		} else {
			return null;
		}
	}

	@Override
	public IChromatogramOverview readOverview(final File file, final IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		final IChromatogramMSDReader chromatogramReader = getReader(file);
		if(chromatogramReader != null) {
			return chromatogramReader.readOverview(file, monitor);
		} else {
			return null;
		}
	}
}
