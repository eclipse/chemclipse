/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.io.File;

/**
 * IChromatogramOverview is used, when the scans of a chromatogram should not be
 * parsed.<br/>
 * It is possible to use it for a short file overview or a chromatogram overlay
 * method.
 */
public interface IChromatogramOverview extends IMeasurementInfo {

	double SECOND_CORRELATION_FACTOR = 1000.0d; // 1ms * 1000 = 1s;
	double MINUTE_CORRELATION_FACTOR = 60000.0d; // 1ms * 1000 = 1s; 1s * 60 = 1min
	double HOUR_CORRELATION_FACTOR = 3600000.0d; // 1ms * 1000 = 1s; 1s * 60 = 1min * 60 = 1hour

	/**
	 * Returns the minimal available scan signal.<br/>
	 * If no scans are stored, 0 will be returned.
	 * 
	 * @return float
	 */
	float getMinSignal();

	/**
	 * Returns the maximal available scan signal.<br/>
	 * If no scans are stored, 0 will be returned.
	 * 
	 * @return float
	 */
	float getMaxSignal();

	/**
	 * The max signal is higher if the cycle number scans shall be
	 * condensed.
	 * 
	 * @param condenseCycleNumberScans
	 * @return float
	 */
	float getMaxSignal(boolean condenseCycleNumberScans);

	/**
	 * Returns the start retention time in milliseconds.<br/>
	 * If no scans are stored, 0 will be returned.
	 * 
	 * @return start Retention Time [ms]
	 */
	int getStartRetentionTime(); // milliseconds

	/**
	 * Returns the stop retention time in milliseconds
	 * 
	 * @return stop Retention Time [ms]
	 */
	int getStopRetentionTime(); // milliseconds

	/**
	 * Returns the scan interval of the current chromatogram.<br/>
	 * If no scans are stored, 0 will be returned.
	 * 
	 * @return scanInterval in milliseconds
	 */
	int getScanInterval(); // in milliseconds

	/**
	 * Sets the scan interval for the current chromatogram.<br/>
	 * If the retention times should be recalculated, call
	 * chromatogram.recalculateRetentionTimes().
	 * 
	 * @param milliseconds
	 */
	void setScanInterval(int milliseconds);

	/**
	 * Set the scan interval for the current chromatogram.<br/>
	 * Here a value can be defined as scans per second.<br/>
	 * <br/>
	 * i.e.: 1.3 - means 1.3 scans per second is approximately 769 milliseconds<br/>
	 * <br/>
	 * If the retention times should be recalculated, call
	 * chromatogram.recalculateRetentionTimes().
	 * 
	 * @param scansPerSecond
	 */
	void setScanInterval(float scansPerSecond);

	/**
	 * Returns the initial scan delay.
	 * 
	 * @return scanDelay in milliseconds
	 */
	int getScanDelay();

	/**
	 * Sets the initial scan delay. The value must be given in milliseconds.<br/>
	 * If the retention times should be recalculated, call
	 * chromatogram.recalculateRetentionTimes().
	 * 
	 * @param milliseconds
	 */
	void setScanDelay(int milliseconds);

	/**
	 * Returns the corresponding scan number to the given retention time.<br/>
	 * A floor value will be returned.<br/>
	 * E.g.:<br/>
	 * Scan 34 - 45003ms<br/>
	 * Scan 35 - 47800ms<br/>
	 * <br/>
	 * When getScanNumber(47790) is invoked:<br/>
	 * 34 will be returned.<br/>
	 * <br/>
	 * The retention time is given in milliseconds.<br/>
	 * If no scan fits to the given retention time, 0 will be returned.
	 * 
	 * @param retentionTime
	 * @return int
	 */
	int getScanNumber(int retentionTime);

	/**
	 * Returns the corresponding scan number to the given retention time.<br/>
	 * A floor value will be returned.<br/>
	 * E.g.:<br/>
	 * Scan 34 - 45003ms (0.75005 min)<br/>
	 * Scan 35 - 47800ms (0.796666667 min)<br/>
	 * <br/>
	 * When getScanNumber(0.7965) is invoked:<br/>
	 * 34 will be returned.<br/>
	 * <br/>
	 * The retention time is given in minutes.<br/>
	 * If no scan fits to the given retention time, 0 will be returned.
	 * 
	 * @param retentionTime
	 * @return int
	 */
	int getScanNumber(float retentionTime);

	/**
	 * Returns the number of scans.
	 * 
	 * @return int
	 */
	int getNumberOfScans();

	/**
	 * Return the name of the chromatogram.<br/>
	 * If no file has been stored, null will be returned.
	 * 
	 * @return String
	 */
	String getName();

	/**
	 * Sets the chromatogram file.<br/>
	 * This method should only be used in org.eclipse.chemclipse.msd.model.
	 */
	void setFile(File file);

	/**
	 * Returns the file of the chromatogram.
	 * 
	 * @return File
	 */
	File getFile();

	/**
	 * Returns the total signal of the parent chromatogram.<br/>
	 * The total signal is the accumulated total signal of all scans.<br/>
	 * If no scans are stored, 0 will be returned.
	 * 
	 * @return float
	 */
	float getTotalSignal();

	/**
	 * Recalculates the retention times with the given scanInterval value.<br/>
	 * If you delete or add a mass spectrum, the retention times need to be
	 * corrected.<br/>
	 * One exception is adding a scan to the end of the ArrayList.<br/>
	 * In this case, the previous retention times do not need to be corrected.<br/>
	 * This would use too much time.
	 */
	void recalculateRetentionTimes();

	/**
	 * Recalculates the scan numbers, e.g. if scans have been removed etc.
	 */
	void recalculateScanNumbers();
}
