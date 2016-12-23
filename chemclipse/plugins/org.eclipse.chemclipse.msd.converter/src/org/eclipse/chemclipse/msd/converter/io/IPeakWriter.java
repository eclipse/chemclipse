/*******************************************************************************
 * Copyright (c) 2012, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.processing.peak.IPeakExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;

public interface IPeakWriter {

	IPeakExportConverterProcessingInfo write(File file, IPeakMSD peak, boolean append) throws FileNotFoundException, FileIsNotWriteableException, IOException;

	IPeakExportConverterProcessingInfo write(File file, IPeaks peaks, boolean append) throws FileNotFoundException, FileIsNotWriteableException, IOException;
}
