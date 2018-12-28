/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.baseline;

/**
 * Defines the values for a baseline segment, e.g. start and stop retention
 * time, start and stop background abundance.
 * 
 * @author eselmeister
 */
public interface IBaselineSegment {

	int getStartRetentionTime();

	/**
	 * The start retention time must be >= 0 and must be =< stopRetentionTime.
	 * 
	 * @param startRetentionTime
	 */
	@Deprecated
	void setStartRetentionTime(int startRetentionTime);

	float getStartBackgroundAbundance();

	/**
	 * The start background abundance time must be >= 0.
	 * 
	 * @param startBackgroundAbundance
	 */
	void setStartBackgroundAbundance(float startBackgroundAbundance);

	int getStopRetentionTime();

	/**
	 * The stop retention time must be >= 0 and must be >= startRetentionTime.
	 * 
	 * @param stopRetentionTime
	 */
	@Deprecated
	void setStopRetentionTime(int stopRetentionTime);

	float getStopBackgroundAbundance();

	/**
	 * The stop background abundance time must be >= 0.
	 * 
	 * @param stopBackgroundAbundance
	 */
	void setStopBackgroundAbundance(float stopBackgroundAbundance);

	float getBackgroundAbundance(int retentionTime);
}
