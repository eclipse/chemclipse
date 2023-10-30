/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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

import java.io.IOException;
import java.util.zip.ZipOutputStream;

import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IChromatogramMSDZipWriter extends IChromatogramMSDWriter {

	/**
	 * The directoryPrefix could be "" to write the data to the root of the zip file.
	 * 
	 * @param zipOutputStream
	 * @param directoryPrefix
	 * @param chromatogram
	 * @param monitor
	 * @throws IOException
	 */
	void writeChromatogram(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException;
}
