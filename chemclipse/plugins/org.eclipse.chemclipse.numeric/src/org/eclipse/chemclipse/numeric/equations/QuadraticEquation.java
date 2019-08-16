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
 * This class represents a quadratic equation.<br/>
 * f(x) = ax^2 + bx + c
 * 
 * @author eselmeister
 */
public class QuadraticEquation implements IEquation {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -2371137795806083758L;
	//
	private double a;
	private double b;
	private double c;

	/**
	 * Creates a new quadratic equation.<br/>
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

		return calculateX(y, true);
	}

	/**
	 * Resolving x of a quadratic equation results in two values.
	 * x = -b/2a [+/-] SQRT(y/a - c/a + (b/2a)^2)
	 * 
	 * Use positive to decide whether to use [+] or [-] for the term [+/-].
	 * 
	 * @param y
	 * @param positive
	 * @return double
	 */
	public double calculateX(double y, boolean positive) {

		/*
		 * x = -b/2a [+/-] SQRT(y/a - c/a + (b/2a)^2)
		 */
		double result = 0;
		if(a == 0) {
			return result;
		}
		//
		double termA = -b / (2 * a);
		double termB = Math.sqrt((y / a) - (c / a) + Math.pow(b / (2 * a), 2));
		if(positive) {
			result = termA + termB;
		} else {
			result = termA - termB;
		}
		return result;
	}

	@Override
	public double calculateY(double x) {

		double result = (a * Math.pow(x, 2.0)) + (b * x) + c;
		return result;
	}

	public double getApexValueForX(Apex result) {

		// double apexY = ((4 * a * c) - Math.pow(b, 2))/(4 * a);
		// return calculateX(apexY, result);
		/*
		 * pq-formula constraint a != 0 p = b/a q = c/a x1,2 = - p/2 +- ((p/2)^2
		 * - q)^0.5
		 */
		// TODO Exception werfen NoSuchValue?
		if(a == 0) {
			return 0;
		}
		double p = b / a;
		double q = c / a;
		double x = 0;
		// TODO ist Math.abs() in allen Fällen korrekt?
		switch(result) {
			case NEGATIVE:
				x = -p / 2 - 1 / 2 * Math.sqrt(Math.abs(Math.pow(p, 2) - (4 * q)));
				break;
			case POSITIVE:
				x = -p / 2 + 1 / 2 * Math.sqrt(Math.abs(Math.pow(p, 2) - (4 * q)));
				break;
		}
		return x;
	}

	// --------------------equals, hashCode, toString
	@Override
	public boolean equals(Object obj) {

		// TODO implementieren
		return super.equals(obj);
	}

	@Override
	public int hashCode() {

		// TODO implementieren
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
	// --------------------equals, hashCode, toString
}
