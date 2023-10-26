/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.ocx.io;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.msd.converter.supplier.ocx.model.chromatogram.IVendorScan;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.model.chromatogram.IVendorScanProxy;
import org.eclipse.chemclipse.msd.model.core.IIonTransitionSettings;

public interface IReaderProxy {

	/**
	 * Reads a single mass spectrum from the given offset by using
	 * a random access file approach.
	 * 
	 * @param file
	 * @param offset
	 * @param massSpectrum
	 * @param ionTransitionSettings
	 * @param monitor
	 * @throws IOException
	 */
	void readMassSpectrum(File file, int offset, IVendorScanProxy massSpectrum, IIonTransitionSettings ionTransitionSettings) throws IOException;

	/**
	 * Reads the mass spectrum by using the given data input stream.
	 * 
	 * @param massSpectrum
	 * @param dataInputStream
	 * @param ionTransitionSettings
	 * @param monitor
	 * @throws IOException
	 */
	void readMassSpectrum(IVendorScan massSpectrum, DataInputStream dataInputStream, IIonTransitionSettings ionTransitionSettings) throws IOException;
}
