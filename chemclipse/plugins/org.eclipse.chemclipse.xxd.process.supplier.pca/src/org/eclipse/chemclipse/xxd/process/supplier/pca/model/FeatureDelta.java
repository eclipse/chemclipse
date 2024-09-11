/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.model;

import java.util.Objects;

public class FeatureDelta {

	private Feature feature = null;
	private double deltaX = 0;
	private double deltaY = 0;

	public FeatureDelta(Feature feature, double deltaX, double deltaY) {

		this.feature = feature;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	public Feature getFeature() {

		return feature;
	}

	public double getDeltaX() {

		return deltaX;
	}

	public double getDeltaY() {

		return deltaY;
	}

	@Override
	public int hashCode() {

		return Objects.hash(feature);
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		FeatureDelta other = (FeatureDelta)obj;
		return Objects.equals(feature, other.feature);
	}

	@Override
	public String toString() {

		return "FeatureDelta [featurePCA=" + feature + ", deltaX=" + deltaX + ", deltaY=" + deltaY + "]";
	}
}
