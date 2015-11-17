/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;


public class ExtendedComparisonResult extends AbstractComparisonResult implements IExtendedComparisonResult {

	final private float forwardMatchFactor;

	public ExtendedComparisonResult(float matchFactor, float reverseMatchFactor, float forwardMatchFactor) {

		super(matchFactor, reverseMatchFactor);
		this.forwardMatchFactor = forwardMatchFactor;
	}

	public ExtendedComparisonResult(float matchFactor, float reverseMatchFactor, float forwardMatchFactor, float probability) {

		super(matchFactor, reverseMatchFactor, probability);
		this.forwardMatchFactor = forwardMatchFactor;
	}

	@Override
	public float getForwardMatchFactor() {

		return forwardMatchFactor;
	}

	@Override
	public int hashCode() {

		return super.hashCode() + 17 * Float.valueOf(forwardMatchFactor).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("matchQuality=" + getMatchFactor());
		builder.append(",");
		builder.append("forwardMatchQuality=" + getForwardMatchFactor());
		builder.append(",");
		builder.append("reverseMatchQuality=" + getReverseMatchFactor());
		builder.append(",");
		builder.append("probability=" + getProbability());
		builder.append("]");
		return builder.toString();
	}
}
