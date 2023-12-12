/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msp;

import java.io.File;

import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.peak.AbstractPeakImportConverter;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class MSPPeakImportConverter extends AbstractPeakImportConverter {

	@Override
	public IProcessingInfo<IPeaks<IPeakMSD>> convert(File file, IProgressMonitor monitor) {

		IProcessingInfo<IPeaks<IPeakMSD>> processingInfo = new ProcessingInfo<IPeaks<IPeakMSD>>();
		processingInfo.addErrorMessage("AMDIS MSP Peak Import", "The converter supports no *.msp file import.");
		return processingInfo;
	}
}
