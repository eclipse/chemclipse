/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.processing;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.report.IPeakIdentificationBatchProcessReport;
import org.eclipse.chemclipse.processing.core.AbstractProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class PeakIdentificationProcessingInfo extends AbstractProcessingInfo implements IPeakIdentificationProcessingInfo {

	@Override
	public IPeakIdentificationBatchProcessReport getPeakIdentificationBatchProcessReport() throws TypeCastException {

		Object object = getProcessingResult();
		if(object instanceof IPeakIdentificationBatchProcessReport) {
			return (IPeakIdentificationBatchProcessReport)object;
		} else {
			throw createTypeCastException("Peak Identification Report", object.getClass(), IPeakIdentificationBatchProcessReport.class);
		}
	}

	@Override
	public void setPeakIdentificationBatchProcessReport(IPeakIdentificationBatchProcessReport peakIdentificationBatchProcessReport) {

		setProcessingResult(peakIdentificationBatchProcessReport);
	}
}
