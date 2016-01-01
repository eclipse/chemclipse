/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.core;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.processing.IPeakIdentificationProcessingInfo;

public interface IPeakIdentificationBatchProcess {

	/**
	 * Executes the batch job and returns a report.
	 * 
	 * @param peakIdentificationBatchJob
	 * @param monitor
	 * @return {@link IProcessingMessage}
	 */
	IPeakIdentificationProcessingInfo execute(IPeakIdentificationBatchJob peakIdentificationBatchJob, IProgressMonitor monitor);
}
