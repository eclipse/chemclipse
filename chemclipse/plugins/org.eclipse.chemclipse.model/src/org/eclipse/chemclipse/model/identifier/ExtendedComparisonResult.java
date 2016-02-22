/*******************************************************************************
 * Copyright (c) 2015, 2016 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 * Dr. Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import java.util.List;

public class ExtendedComparisonResult extends AbstractComparisonResult implements IExtendedComparisonResult {

	final private float forwardMatchFactor;
	
	final private List<Double> intensitiesFromUnknown;

	final private List<Double> intensitiesFromReference;

	public ExtendedComparisonResult(float matchFactor, float reverseMatchFactor, float forwardMatchFactor, List<Double> intensitiesFromUnknown, List<Double> intensitiesFromReference) {
		super(matchFactor, reverseMatchFactor);
		this.forwardMatchFactor = forwardMatchFactor;
		this.intensitiesFromUnknown = intensitiesFromUnknown;
		this.intensitiesFromReference = intensitiesFromReference;
	}

	public ExtendedComparisonResult(float matchFactor, float reverseMatchFactor, float forwardMatchFactor, List<Double> intensitiesFromUnknown, List<Double> intensitiesFromReference, float probability) {
		super(matchFactor, reverseMatchFactor, probability);
		this.forwardMatchFactor = forwardMatchFactor;
		this.intensitiesFromUnknown = intensitiesFromUnknown;
		this.intensitiesFromReference = intensitiesFromReference;
	}

	@Override
	public float getForwardMatchFactor() {

		return forwardMatchFactor;
	}
	
	public List<Double> getIntensitiesFromUnknown() {
	    return intensitiesFromUnknown;
	}

	public List<Double> getIntensitiesFromReference() {
	    return intensitiesFromReference;
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
