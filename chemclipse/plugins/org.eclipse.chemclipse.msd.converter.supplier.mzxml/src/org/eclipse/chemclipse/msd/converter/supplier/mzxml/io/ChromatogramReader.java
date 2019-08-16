/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ReaderVersion20;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ReaderVersion21;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ReaderVersion30;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ReaderVersion31;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ReaderVersion32;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.xxd.converter.supplier.io.exception.UnknownVersionException;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReader extends AbstractChromatogramMSDReader implements IChromatogramMSDReader {

	private static final String CONTEXT_PATH_V_200 = "org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v20.model";
	private static final String CONTEXT_PATH_V_210 = "org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v21.model";
	private static final String CONTEXT_PATH_V_220 = "org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v22.model";
	private static final String CONTEXT_PATH_V_300 = "org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v30.model";
	private static final String CONTEXT_PATH_V_310 = "org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v31.model";
	private static final String CONTEXT_PATH_V_320 = "org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v32.model";
	private static final String MZXML_V_200 = "mzXML_2.0";
	private static final String MZXML_V_210 = "mzXML_2.1";
	private static final String MZXML_V_220 = "mzXML_2.2";
	private static final String MZXML_V_300 = "mzXML_3.0";
	private static final String MZXML_V_310 = "mzXML_3.1";
	private static final String MZXML_V_320 = "mzXML_3.2";

	public static IChromatogramMSDReader getReader(final File file) throws IOException {

		IChromatogramMSDReader chromatogramReader = null;
		//
		final FileReader fileReader = new FileReader(file);
		final char[] charBuffer = new char[350];
		fileReader.read(charBuffer);
		fileReader.close();
		//
		final String header = new String(charBuffer);
		if(header.contains(MZXML_V_200)) {
			chromatogramReader = new ReaderVersion20(CONTEXT_PATH_V_200);
		} else if(header.contains(MZXML_V_210)) {
			chromatogramReader = new ReaderVersion21(CONTEXT_PATH_V_210);
		} else if(header.contains(MZXML_V_220)) {
			chromatogramReader = new ReaderVersion21(CONTEXT_PATH_V_220);
		} else if(header.contains(MZXML_V_300)) {
			chromatogramReader = new ReaderVersion30(CONTEXT_PATH_V_300);
		} else if(header.contains(MZXML_V_310)) {
			chromatogramReader = new ReaderVersion31(CONTEXT_PATH_V_310);
		} else if(header.contains(MZXML_V_320)) {
			chromatogramReader = new ReaderVersion32(CONTEXT_PATH_V_320);
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
