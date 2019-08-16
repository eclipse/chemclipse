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
package org.eclipse.chemclipse.numeric.equations;

/**
 * TEST
 */
public class PolynomicalEquation4thDegree implements IEquation {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -6786649038436947751L;
	//
	private double a;
	private double b;
	private double c;
	private double d;

	public PolynomicalEquation4thDegree(double a, double b, double c, double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	@Override
	public double calculateY(double x) {

		double ax = (a * Math.pow(x, 4.0));
		double bx = (b * Math.pow(x, 3.0));
		double cx = (c * Math.pow(x, 2.0));
		double dx = d;
		double result = ax + bx + cx + dx;
		return result;
	}

	@Override
	public double calculateX(double y) {

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("f(x)=");
		builder.append(a + "x^9");
		builder.append(" + ");
		builder.append(b + "x^8");
		builder.append(" + ");
		builder.append(c + "x^7");
		builder.append(" + ");
		builder.append(d + "x^6");
		builder.append("]");
		return builder.toString();
	}
}
