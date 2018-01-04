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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.editors;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;

/**
 * @author Dr. Philip Wenig
 * 
 */
public interface IMultiEditorPage {

	/**
	 * Returns the page index.
	 * 
	 * @return int
	 */
	int getPageIndex();

	/**
	 * Dispose.
	 */
	void dispose();

	/**
	 * Sets the batch job.
	 * 
	 * @param peakIdentificationBatchJob
	 */
	void setPeakIdentificationBatchJob(IPeakIdentificationBatchJob peakIdentificationBatchJob);

	/**
	 * Set the focus.
	 */
	void setFocus();
}