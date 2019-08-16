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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model;

public interface IPeakProcessEntry {

	/**
	 * Return the processor id.
	 * 
	 * @return String
	 */
	String getProcessorId();

	/**
	 * Set the processor id.
	 * 
	 * @param processorId
	 */
	void setProcessorId(String processorId);

	/**
	 * Report results.
	 * 
	 * @return boolean
	 */
	boolean isReport();

	/**
	 * Set the report status.
	 * 
	 * @param report
	 * @return boolean
	 */
	void setReport(boolean report);
}
