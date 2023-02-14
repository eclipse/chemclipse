/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.services;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.numeric.services.IMaximaDetectorService;
import org.eclipse.chemclipse.numeric.services.IMaximaDetectorSettings;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(service = {IMaximaDetectorService.class}, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class FirstDerivativeMaximaService implements IMaximaDetectorService {

	private static final Logger logger = Logger.getLogger(FirstDerivativeMaximaService.class);

	@Activate
	public void start() {

		logger.info("Service started: " + getName());
	}

	@Override
	public String getId() {

		return "org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative";
	}

	@Override
	public String getName() {

		return "Maxima Detector (First Derivative)";
	}

	@Override
	public String getDescription() {

		return "This detector tries to find maxima using the first derivative.";
	}

	@Override
	public String getVersion() {

		return "0.9.0";
	}

	@Override
	public IMaximaDetectorSettings getSettings() {

		return new FirstDerivativeMaximaSettings();
	}

	@Override
	public double[] calculate(double[] xValues, double[] yValues, IMaximaDetectorSettings maximaSettings) {

		if(xValues.length != yValues.length) {
			return new double[0];
		} else {
			/*
			 * Extract the derivative.
			 */
			double[] derivatives = new double[yValues.length];
			derivatives[0] = 0.0d;
			for(int i = 0; i < (yValues.length - 1); i++) {
				int next = i + 1;
				derivatives[next] = yValues[next] - yValues[i];
			}
			/*
			 * Find the maxima.
			 */
			List<Double> maxima = new ArrayList<>();
			for(int i = 0; i < (derivatives.length - 1); i++) {
				int next = i + 1;
				double a = derivatives[i];
				double b = derivatives[next];
				if(a > 0 && b < 0) {
					maxima.add(xValues[i]);
				}
			}
			/*
			 * Convert maxima to array.
			 */
			return maxima.stream().mapToDouble(d -> d).toArray();
		}
	}
}
