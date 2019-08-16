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
public class PolynomicalEquation9thDegree implements IEquation {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 6883637910982170361L;
	//
	private double a;
	private double b;
	private double c;
	private double d;
	private double e;
	private double f;
	private double g;
	private double h;
	private double i;

	public PolynomicalEquation9thDegree(double a, double b, double c, double d, double e, double f, double g, double h, double i) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
		this.g = g;
		this.h = h;
		this.i = i;
	}

	@Override
	public double calculateY(double x) {

		double ax = (a * Math.pow(x, 9.0));
		double bx = (b * Math.pow(x, 8.0));
		double cx = (c * Math.pow(x, 7.0));
		double dx = (d * Math.pow(x, 6.0));
		double ex = (e * Math.pow(x, 5.0));
		double fx = (f * Math.pow(x, 4.0));
		double gx = (g * Math.pow(x, 3.0));
		double hx = (h * Math.pow(x, 2.0));
		double ix = i;
		double result = ax + bx + cx + dx + ex + fx + gx + hx + ix;
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
		builder.append(" + ");
		builder.append(e + "x^5");
		builder.append(" + ");
		builder.append(f + "x^4");
		builder.append(" + ");
		builder.append(g + "x^3");
		builder.append(" + ");
		builder.append(h + "x^2");
		builder.append(" + ");
		builder.append(i);
		builder.append("]");
		return builder.toString();
	}
}
