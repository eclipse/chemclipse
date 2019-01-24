/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

/**
 * Interface an UI can implement to state that it has a chart with configurable axis
 * 
 * @author Christoph Läubrich
 *
 */
public interface AxisConfig {

	enum ChartAxis {
		PRIMARY_X, PRIMARY_Y, SECONDARY_X, SECONDARY_Y
	}

	/**
	 * Set the visibility of the label the axis to the given value
	 * 
	 * @param axis
	 * @param visible
	 */
	void setAxisLabelVisible(ChartAxis axis, boolean visible);

	/**
	 * Set the visibility of the given axis to the given value
	 * 
	 * @param axis
	 * @param visible
	 */
	void setAxisVisible(ChartAxis axis, boolean visible);

	/**
	 * 
	 * @param axis
	 * @return true if the included chart contains the given axis
	 */
	boolean hasAxis(ChartAxis axis);
}
