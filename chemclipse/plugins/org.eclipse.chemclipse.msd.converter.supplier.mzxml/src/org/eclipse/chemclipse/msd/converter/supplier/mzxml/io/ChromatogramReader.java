/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
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
import java.io.FileReader;
import java.io.IOException;

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
		final char[] charBuffer = new char[350];
		try (final FileReader fileReader = new FileReader(file)) {
			fileReader.read(charBuffer);
		}
		final String header = new String(charBuffer);
		if(header.contains(IFormat.MZXML_V_200)) {
			chromatogramReader = new ReaderVersion20();
		} else if(header.contains(IFormat.MZXML_V_210)) {
			chromatogramReader = new ReaderVersion21();
		} else if(header.contains(IFormat.MZXML_V_220)) {
			chromatogramReader = new ReaderVersion21();
		} else if(header.contains(IFormat.MZXML_V_300)) {
			chromatogramReader = new ReaderVersion30();
		} else if(header.contains(IFormat.MZXML_V_310)) {
			chromatogramReader = new ReaderVersion31();
		} else if(header.contains(IFormat.MZXML_V_320)) {
			chromatogramReader = new ReaderVersion32();
		} else {
			throw new UnknownVersionException();
		}
		//
		return chromatogramReader;
	}

	@Override
	public IChromatogramMSD read(final File file, final IProgressMonitor monitor) throws IOException, InterruptedException {

		final IChromatogramMSDReader chromatogramReader = getReader(file);
		return chromatogramReader.read(file, monitor);
	}

	@Override
	public IChromatogramOverview readOverview(final File file, final IProgressMonitor monitor) throws IOException {

		final IChromatogramMSDReader chromatogramReader = getReader(file);
		return chromatogramReader.readOverview(file, monitor);
	}
}
