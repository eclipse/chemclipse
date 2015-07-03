/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
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
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ReaderVersion;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReader extends AbstractChromatogramMSDReader implements IChromatogramMSDReader {

	private static final String MZXML_V_220 = "mzXML_2.2";
	private static final String CONTEXT_PATH_V_220 = "org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v22.model";
	private static final String MZXML_V_300 = "mzXML_3.0";
	private static final String CONTEXT_PATH_V_300 = "org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v30.model";
	private static final String MZXML_V_310 = "mzXML_3.1";
	private static final String CONTEXT_PATH_V_310 = "org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v31.model";
	private static final String MZXML_V_320 = "mzXML_3.2";
	private static final String CONTEXT_PATH_V_320 = "org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v32.model";

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramMSDReader chromatogramReader = getReader(file);
		if(chromatogramReader != null) {
			return chromatogramReader.readOverview(file, monitor);
		} else {
			return null;
		}
	}

	@Override
	public IChromatogramMSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramMSDReader chromatogramReader = getReader(file);
		if(chromatogramReader != null) {
			return chromatogramReader.read(file, monitor);
		} else {
			return null;
		}
	}

	private IChromatogramMSDReader getReader(File file) throws IOException {

		IChromatogramMSDReader chromatogramReader = null;
		//
		FileReader fileReader = new FileReader(file);
		char[] charBuffer = new char[350];
		fileReader.read(charBuffer);
		fileReader.close();
		//
		String header = new String(charBuffer);
		if(header.contains(MZXML_V_220)) {
			/*
			 * 2.2
			 */
			chromatogramReader = new ReaderVersion(CONTEXT_PATH_V_220);
		} else if(header.contains(MZXML_V_300)) {
			/*
			 * 3.0
			 */
			chromatogramReader = new ReaderVersion(CONTEXT_PATH_V_300);
		} else if(header.contains(MZXML_V_310)) {
			/*
			 * 3.1
			 */
			chromatogramReader = new ReaderVersion(CONTEXT_PATH_V_310);
		} else if(header.contains(MZXML_V_320)) {
			/*
			 * 3.2
			 */
			chromatogramReader = new ReaderVersion(CONTEXT_PATH_V_320);
		}
		//
		return chromatogramReader;
	}
}
