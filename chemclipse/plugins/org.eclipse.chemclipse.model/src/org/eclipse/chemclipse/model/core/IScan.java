/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import org.eclipse.core.runtime.IAdaptable;

/**
 * A chromatogram consists of several scans. There are several detector methods like:
 * 
 * GC/MS
 * GC/FID
 * GC/GC/MS
 * GC/GC/FID
 * GC/MS/MS
 * 
 * Hence, a scan could represent various types.
 * It could be either a mass spectrum, a scan recorded by FID.
 * Or it could be a chromatogram containing subsequent scans too.
 * 
 */
public interface IScan extends IAdaptable, IScanSerializable {

	/**
	 * Returns the chromatogram, where the mass spectrum is stored.<br/>
	 * If there is no parent chromatogram, it will return null.
	 */
	IChromatogram getParentChromatogram();

	/**
	 * Sets the chromatogram which stores the mass spectrum.
	 * 
	 * @param parentChromatogram
	 */
	void setParentChromatogram(IChromatogram parentChromatogram);

	/**
	 * Returns the scan number of the actual mass spectrum.<br/>
	 * If it is not part if a chromatogram, the scan number is 0.
	 * 
	 * @return int
	 */
	int getScanNumber();

	/**
	 * Sets the scan Number of the actual mass spectrum.<br/>
	 * Only values >= 0 are allowed.
	 * 
	 * @param scanNumber
	 */
	void setScanNumber(int scanNumber);

	/**
	 * Returns the retention time of the mass spectrum.<br/>
	 * Retention time is stored as milliseconds.
	 * 
	 * @return int - retention time
	 */
	int getRetentionTime();

	/**
	 * Sets a new retention time.<br/>
	 * Retention time in milliseconds.<br/>
	 * Only values >= 0 are allowed.
	 * 
	 * @param retentionTime
	 *            - new retention time
	 */
	void setRetentionTime(int retentionTime);

	/**
	 * Returns the retention index.
	 * 
	 * @return float - retention index
	 */
	float getRetentionIndex();

	/**
	 * Sets the retention index. Only values >= 0 are allowed.<br/>
	 * This method is protected, as the retentionIndices should only be
	 * calculated by the class Chromatogram.
	 * 
	 * @param retentionIndex
	 */
	void setRetentionIndex(float retentionIndex);

	/**
	 * This flag marks if a this scan has been edited.
	 */
	boolean isDirty();

	/**
	 * This flag marks if this scan has been edited.<br/>
	 * It will only be saved if it is dirty. It should save a little bit of
	 * process time.
	 */
	void setDirty(boolean isDirty);

	/**
	 * Returns the total signal.<br/>
	 * If no signal is stored, 0 will be returned.
	 * 
	 * @return float - signal
	 */
	float getTotalSignal();

	/**
	 * Returns the identifier of the scan.
	 * It is used, e.g. to select and find a specifically marked scan in the list of scans.
	 * 
	 * @return String
	 */
	String getIdentifier();

	/**
	 * Sets an identifier.
	 * 
	 * @param identifier
	 */
	void setIdentifier(String identifier);

	/**
	 * Adjusts the scan to the given total signal.<br/>
	 * It means that all (ions - if it's mass spectrum) will be shifted so that the total signal
	 * will be the given total signal.<br/>
	 * The value must be > 0. Why? If the total signal is 0 there would be no
	 * ion stored in the scan (mass spectrum).<br/>
	 * Be aware of that some (mass spectrum) supplier support not the whole
	 * available signal range.
	 * 
	 * @param totalSignal
	 */
	void adjustTotalSignal(float totalSignal);

	int getTimeSegmentId();

	void setTimeSegmentId(int timeSegmentId);

	/**
	 * The default cycle number is 1.
	 * Cycle numbers are used to display several scans of
	 * one cycle number as one summed TIC.
	 * 
	 * @return int
	 */
	int getCycleNumber();

	void setCycleNumber(int cycleNumber);
}
