/*******************************************************************************
 * Copyright (c) 2008, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.support;

import org.eclipse.chemclipse.model.core.IChromatogram;

public class RetentionTimeConverter {

	/**
	 * Use only static methods.
	 */
	private RetentionTimeConverter() {
	}

	/**
	 * Returns the given retention time (milliseconds) in minutes.
	 * 
	 * @param milliseconds
	 * @return double
	 */
	public static double getRetentionTimeInMinutes(int milliseconds) {

		return milliseconds / IChromatogram.MINUTE_CORRELATION_FACTOR;
	}

	/**
	 * Returns the given retention time (milliseconds) in minutes.
	 * 
	 * @param milliseconds
	 * @return double
	 */
	public static double getRetentionTimeInMinutes(double milliseconds) {

		return milliseconds / IChromatogram.MINUTE_CORRELATION_FACTOR;
	}
}
