/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.statistics;

import org.apache.commons.math3.stat.StatUtils;

public class ResidualStandardDeviationCalculator {

	/*
	 * STFEHLERYX / STEYX
	 * (Y_VALS, X_VALS)
	 */
	public double calculate(double[][] data) {

		double result = 0;
		int size = data.length;
		double denominator = size - 2.0d;
		/*
		 * Prevent dividing by zero
		 */
		if(denominator != 0) {
			double sampleFactor = 1.0d / denominator;
			double[] yValues = new double[size];
			double[] xValues = new double[size];
			//
			int i = 0;
			for(double[] pair : data) {
				double y = pair[0];
				double x = pair[1];
				//
				yValues[i] = y;
				xValues[i] = x;
				i++;
			}
			//
			double yMean = StatUtils.mean(yValues);
			double xMean = StatUtils.mean(xValues);
			//
			double sumYMeanSquare = 0;
			double sumXMeanSquare = 0;
			double sumXMeanYMean = 0;
			//
			for(double[] pair : data) {
				double y = pair[0];
				double x = pair[1];
				//
				double ym = y - yMean;
				double xm = x - xMean;
				//
				sumYMeanSquare += Math.pow(ym, 2);
				sumXMeanSquare += Math.pow(xm, 2);
				sumXMeanYMean += xm * ym;
			}
			//
			double sumXMeanYMeanSquare = Math.pow(sumXMeanYMean, 2);
			/*
			 * Prevent dividing by zero
			 */
			if(sumXMeanSquare != 0) {
				result = Math.sqrt(sampleFactor * (sumYMeanSquare - (sumXMeanYMeanSquare / sumXMeanSquare)));
			}
		}
		//
		return result;
	}
}
