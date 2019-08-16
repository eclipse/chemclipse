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
 * This class represents a linear equation.<br/>
 * f(x) = ax + b
 * 
 * @author eselmeister
 */
public class LinearEquation implements IEquation {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -7941955423018899201L;
	//
	private double a;
	private double b;

	/**
	 * Creates a new quadratic equation.<br/>
	 * f(x) = ax + b
	 * 
	 * @param a
	 * @param b
	 */
	public LinearEquation(double a, double b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public double calculateX(double y) {

		if(a == 0) {
			return Double.NaN;
		} else {
			return (y - b) / a;
		}
	}

	@Override
	public double calculateY(double x) {

		return a * x + b;
	}

	/**
	 * f(x) = ax + b<br/>
	 * This method returns the factor of a.
	 * 
	 * @return double
	 */
	public double getA() {

		return a;
	}

	/**
	 * f(x) = ax + b<br/>
	 * This method returns the factor of b.
	 * 
	 * @return double
	 */
	public double getB() {

		return b;
	}

	// --------------------equals, hashCode, toString
	@Override
	public boolean equals(Object other) {

		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(this.getClass() != other.getClass()) {
			return false;
		}
		LinearEquation otherEquation = (LinearEquation)other;
		return this.getA() == otherEquation.getA() && this.getB() == otherEquation.getB();
	}

	@Override
	public int hashCode() {

		return 7 * Double.valueOf(a).hashCode() + 11 * Double.valueOf(b).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("f(x)=" + a + "x" + " + " + b);
		builder.append("]");
		return builder.toString();
	}
	// --------------------equals, hashCode, toString
}
