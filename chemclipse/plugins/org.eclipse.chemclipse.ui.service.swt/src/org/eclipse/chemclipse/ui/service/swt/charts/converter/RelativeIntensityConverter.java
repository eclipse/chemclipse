/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ui.service.swt.charts.converter;

import org.eclipse.chemclipse.ui.service.swt.charts.AbstractAxisScaleConverter;
import org.eclipse.chemclipse.ui.service.swt.charts.IAxisScaleConverter;
import org.eclipse.chemclipse.ui.service.swt.charts.IChartDataCoordinates;

public class RelativeIntensityConverter extends AbstractAxisScaleConverter implements IAxisScaleConverter {

	@Override
	public double getConvertedUnit(double unit) {

		IChartDataCoordinates chartDataCoordinates = getChartDataCoordinates();
		double convertedUnit = unit;
		if(chartDataCoordinates != null) {
			double delta = chartDataCoordinates.getMaxY() - chartDataCoordinates.getMinY();
			if(delta != 0) {
				convertedUnit = (100.0d / delta) * unit;
			}
		}
		return convertedUnit;
	}
}
