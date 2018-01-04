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

import java.util.List;

public interface IPeakIdentificationBatchJob {

	String getName();

	/**
	 * Returns the list of peak input entries.
	 * 
	 * @return List<IPeakInputEntry>
	 */
	List<IPeakInputEntry> getPeakInputEntries();

	/**
	 * Returns the list of peak output entries.
	 * 
	 * @return List<IPeakOutputEntry>
	 */
	List<IPeakOutputEntry> getPeakOutputEntries();

	/**
	 * Returns the peak integrator.
	 * 
	 * @return {@link IPeakIntegrationEntry}
	 */
	IPeakIntegrationEntry getPeakIntegrationEntry();

	/**
	 * Sets the peak integrator.
	 * 
	 * @return {@link IPeakIntegrationEntry}
	 */
	void setPeakIntegrationEntry(IPeakIntegrationEntry peakIntegrationEntry);

	/**
	 * Returns the peak identifier.
	 * 
	 * @return {@link IPeakIdentificationEntry}
	 */
	IPeakIdentificationEntry getPeakIdentificationEntry();

	/**
	 * Sets the peak identifier.
	 * 
	 * @return {@link IPeakIdentificationEntry}
	 */
	void setPeakIdentificationEntry(IPeakIdentificationEntry peakIdentificationEntry);

	/**
	 * Returns the report folder.
	 * 
	 * @return String
	 */
	String getReportFolder();

	/**
	 * Set the report folder.
	 * 
	 * @param reportFolder
	 */
	void setReportFolder(String reportFolder);

	/**
	 * Override the reports.
	 * 
	 * @return boolean
	 */
	boolean isOverrideReport();

	/**
	 * Sets whether to override reports or not.
	 * 
	 * @param overrideReport
	 */
	void setOverrideReport(boolean overrideReport);
}
