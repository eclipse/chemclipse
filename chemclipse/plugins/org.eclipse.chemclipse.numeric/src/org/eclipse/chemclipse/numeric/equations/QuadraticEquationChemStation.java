/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.equations;

/**
 * EXPERIMENTAL
 */
public class QuadraticEquationChemStation extends QuadraticEquation {

	private static final long serialVersionUID = 7178267475432141528L;

	public QuadraticEquationChemStation(double a, double b, double c) {

		super(a, b, c);
	}

	@Override
	public double calculateX(double y) {

		double c = getC();
		if(c != 0) {
			return super.calculateX(y);
		} else {
			/*
			 * Only valid without intercept.
			 * x = (b - SQRT(POWER(b,2) - (4 * a * y)))) / (a * 2))
			 */
			double a = getA();
			double b = getB();
			//
			double result = 0.0d;
			double denominator = a * 2;
			if(denominator != 0) {
				result = ((b - Math.sqrt(Math.pow(b, 2) - (4 * a * y))) / denominator);
			}
			//
			return result;
		}
	}
}