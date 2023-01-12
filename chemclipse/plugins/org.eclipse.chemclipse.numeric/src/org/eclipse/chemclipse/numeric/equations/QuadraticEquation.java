/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
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
 * This class represents a quadratic equation.<br/>
 * f(x) = ax^2 + bx + c
 */
public class QuadraticEquation implements IEquation {

	private static final long serialVersionUID = -5348071897593888033L;
	//
	private double a;
	private double b;
	private double c;

	/**
	 * Creates a new quadratic equation.
	 * f(x) = ax^2 + bx + c
	 * 
	 * @param a
	 * @param b
	 * @param c
	 */
	public QuadraticEquation(double a, double b, double c) {

		this.a = a;
		this.b = b;
		this.c = c;
	}

	public double getA() {

		return a;
	}

	public double getB() {

		return b;
	}

	public double getC() {

		return c;
	}

	@Override
	public double calculateX(double y) {

		/*
		 * x = (c - y) / (-0.5 * (b + SQRT(POWER(b,2) - (4 * a * (c - y)))))
		 */
		double result = 0.0d;
		double denominator = -0.5d * (b + Math.sqrt(Math.pow(b, 2) - (4 * a * (c - y))));
		if(denominator != 0) {
			result = (c - y) / denominator;
		}
		//
		return result;
	}

	@Override
	public double calculateY(double x) {

		return (a * Math.pow(x, 2.0)) + (b * x) + c;
	}

	public double getApexValueForX(Apex result) {

		/*
		 * PQ Formula Constraint
		 * a != 0
		 * p = b/a
		 * q = c/a
		 * x1,2 = - p/2 +- ((p/2)^2 - q)^0.5
		 */
		if(a == 0) {
			return 0;
		}
		//
		double p = b / a;
		double q = c / a;
		double x = 0;
		double parameter1 = -(p / 2);
		double parameter2 = Math.sqrt(Math.pow((p / 2), 2) - q);
		//
		switch(result) {
			case NEGATIVE:
				x = parameter1 - parameter2;
				break;
			case POSITIVE:
				x = parameter1 + parameter2;
				break;
		}
		//
		return x;
	}

	@Override
	public boolean equals(Object obj) {

		return super.equals(obj);
	}

	@Override
	public int hashCode() {

		return super.hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("f(x)=" + a + "x^2" + " + " + b + "x" + " + " + c);
		builder.append("]");
		return builder.toString();
	}
}