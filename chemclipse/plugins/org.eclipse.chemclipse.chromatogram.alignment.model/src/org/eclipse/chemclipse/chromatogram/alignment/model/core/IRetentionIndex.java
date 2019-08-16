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
package org.eclipse.chemclipse.chromatogram.alignment.model.core;

public interface IRetentionIndex {

	/**
	 * Returns the retention time of the correspondent retention index.<br/>
	 * The retention time is stored in milliseconds.
	 * 
	 * @return retentionTime
	 */
	int getRetentionTime();

	/**
	 * Sets the retention time to the correspondent retention index.<br/>
	 * The retention time is stored in milliseconds.
	 * 
	 * @param retentionTime
	 */
	void setRetentionTime(int retentionTime);

	/**
	 * Returns the index to the correspondent retention time.
	 * 
	 * @return retentionIndex
	 */
	float getIndex();

	/**
	 * Sets the index to the correspondent retention time.
	 * 
	 * @param index
	 */
	void setIndex(float index);

	/**
	 * Returns the name of the calibration substance.
	 * 
	 * @return name
	 */
	String getName();

	/**
	 * Sets the name of the calibration substance.
	 * 
	 * @param name
	 */
	void setName(String name);
}
