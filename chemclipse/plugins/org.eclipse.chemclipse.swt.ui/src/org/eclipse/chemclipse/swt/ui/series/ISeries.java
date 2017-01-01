/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.series;

// TODO JUnit
/**
 * @author eselmeister
 */
public interface ISeries extends IDataRange {

	/**
	 * Returns the x series.
	 * 
	 * @return double[]
	 */
	double[] getXSeries();

	/**
	 * Returns the y series.
	 * 
	 * @return double[]
	 */
	double[] getYSeries();

	/**
	 * Returns the id of the series.
	 * 
	 * @return String
	 */
	String getId();
}
