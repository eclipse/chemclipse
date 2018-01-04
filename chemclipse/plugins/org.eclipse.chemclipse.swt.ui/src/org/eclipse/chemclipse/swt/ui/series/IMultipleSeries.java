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
package org.eclipse.chemclipse.swt.ui.series;

import java.util.List;

public interface IMultipleSeries extends IDataRange {

	/**
	 * Returns the stored series.
	 * 
	 * @return List<ISeries>
	 */
	List<ISeries> getMultipleSeries();

	/**
	 * Adds the given series.
	 * 
	 * @param series
	 */
	void add(ISeries series);

	/**
	 * Removes the given series.
	 * 
	 * @param series
	 */
	void remove(ISeries series);

	/**
	 * Removes the series at the given index.
	 * 
	 * @param index
	 */
	void remove(int index);

	/**
	 * Removes all series.
	 */
	void removeAll();
}
