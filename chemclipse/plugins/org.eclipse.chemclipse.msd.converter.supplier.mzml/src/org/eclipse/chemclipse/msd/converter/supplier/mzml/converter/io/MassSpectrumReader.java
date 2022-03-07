/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.io.MassSpectrumReaderVersion110;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.xxd.converter.supplier.io.exception.UnknownVersionException;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumReader extends AbstractMassSpectraReader implements IMassSpectraReader {

	@Override
	public IMassSpectra read(File file, IProgressMonitor monitor) throws IOException {

		final IMassSpectraReader massSpectraReader = getReader(file);
		return massSpectraReader.read(file, monitor);
	}

	public static IMassSpectraReader getReader(final File file) throws IOException {

		IMassSpectraReader massSpectraReader = null;
		//
		try (FileReader fileReader = new FileReader(file)) {
			final char[] charBuffer = new char[500];
			fileReader.read(charBuffer);
			final String header = new String(charBuffer);
			if(header.contains(IFormat.MZML_V_110)) {
				massSpectraReader = new MassSpectrumReaderVersion110();
			} else {
				throw new UnknownVersionException();
			}
		}
		//
		return massSpectraReader;
	}
}
