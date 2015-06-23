/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.elu;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.peak.AbstractPeakExportConverter;
import org.eclipse.chemclipse.msd.converter.processing.peak.IPeakExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.processing.peak.PeakExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;

public class ELUPeakExportConverter extends AbstractPeakExportConverter {

	@Override
	public IPeakExportConverterProcessingInfo convert(File file, IPeakMSD peak, boolean append, IProgressMonitor monitor) {

		return getNoSupportMessage();
	}

	@Override
	public IPeakExportConverterProcessingInfo convert(File file, IPeaks peaks, boolean append, IProgressMonitor monitor) {

		return getNoSupportMessage();
	}

	private IPeakExportConverterProcessingInfo getNoSupportMessage() {

		IPeakExportConverterProcessingInfo processingInfo = new PeakExportConverterProcessingInfo();
		processingInfo.addErrorMessage("ELU Peak Export", "There are no capabilities to export peaks in ELU format.");
		return processingInfo;
	}
}
