/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
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

import org.eclipse.chemclipse.converter.exceptions.UnknownVersionException;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ChromatogramReaderVersion20;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ChromatogramReaderVersion21;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ChromatogramReaderVersion22;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ChromatogramReaderVersion30;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ChromatogramReaderVersion31;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io.ChromatogramReaderVersion32;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReader extends AbstractChromatogramMSDReader implements IChromatogramMSDReader {

	public static IChromatogramMSDReader getReader(final File file) throws IOException {

		IChromatogramMSDReader chromatogramReader = null;
		final char[] charBuffer = new char[350];
		try (final FileReader fileReader = new FileReader(file)) {
			fileReader.read(charBuffer);
		}
		final String header = new String(charBuffer);
		if(header.contains(ChromatogramReaderVersion20.VERSION)) {
			chromatogramReader = new ChromatogramReaderVersion20();
		} else if(header.contains(ChromatogramReaderVersion21.VERSION)) {
			chromatogramReader = new ChromatogramReaderVersion21();
		} else if(header.contains(ChromatogramReaderVersion22.VERSION)) {
			chromatogramReader = new ChromatogramReaderVersion22();
		} else if(header.contains(ChromatogramReaderVersion30.VERSION)) {
			chromatogramReader = new ChromatogramReaderVersion30();
		} else if(header.contains(ChromatogramReaderVersion31.VERSION)) {
			chromatogramReader = new ChromatogramReaderVersion31();
		} else if(header.contains(ChromatogramReaderVersion32.VERSION)) {
			chromatogramReader = new ChromatogramReaderVersion32();
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
