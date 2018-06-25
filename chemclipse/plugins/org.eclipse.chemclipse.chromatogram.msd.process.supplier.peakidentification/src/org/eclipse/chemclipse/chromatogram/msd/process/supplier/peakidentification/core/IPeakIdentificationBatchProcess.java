/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.core;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IPeakIdentificationBatchProcess {

	/**
	 * Executes the batch job and returns a report.
	 * 
	 * @param peakIdentificationBatchJob
	 * @param monitor
	 * @return {@link IProcessingMessage}
	 */
	IProcessingInfo execute(IPeakIdentificationBatchJob peakIdentificationBatchJob, IProgressMonitor monitor);
}
