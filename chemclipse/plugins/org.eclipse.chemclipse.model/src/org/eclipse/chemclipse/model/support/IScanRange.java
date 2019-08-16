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
package org.eclipse.chemclipse.model.support;

/**
 * @author eselmeister
 */
public interface IScanRange {

	int MIN_SCAN = 1;
	int MAX_SCAN = Integer.MAX_VALUE;

	/**
	 * Returns the start scan number.
	 * 
	 * @return int
	 */
	int getStartScan();

	/**
	 * Returns the stop scan number.
	 * 
	 * @return int
	 */
	int getStopScan();

	/**
	 * Returns the width of the scan range.
	 * 
	 * @return int
	 */
	int getWidth();
}
