/*******************************************************************************
 * Copyright (c) 2012, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.processing;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.report.IPeakIdentificationBatchProcessReport;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public interface IPeakIdentificationProcessingInfo extends IProcessingInfo {

	/**
	 * Returns the stored object as the given type.
	 * 
	 * @return IPeakIdentificationBatchProcessReport
	 * @throws TypeCastException
	 */
	IPeakIdentificationBatchProcessReport getPeakIdentificationBatchProcessReport() throws TypeCastException;

	/**
	 * Sets the peak identification report.
	 * 
	 * @param peakIdentificationBatchProcessReport
	 */
	void setPeakIdentificationBatchProcessReport(IPeakIdentificationBatchProcessReport peakIdentificationBatchProcessReport);
}
