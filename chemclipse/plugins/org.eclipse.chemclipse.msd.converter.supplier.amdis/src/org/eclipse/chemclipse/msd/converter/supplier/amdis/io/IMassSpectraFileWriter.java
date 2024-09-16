/*******************************************************************************
 * Copyright (c) 2012, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io;

import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IMassSpectraFileWriter extends IMassSpectraWriter {

	/**
	 * Writes the mass spectrum with the given file writer.
	 * 
	 * @param fileWriter
	 * @param massSpectrum
	 * @throws IOException
	 */
	void writeMassSpectrum(FileWriter fileWriter, IScanMSD massSpectrum, IProgressMonitor monitor) throws IOException;
}
