/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.statistics;

public class SampleData implements ISampleData {

	private double data;
	private double normalizedData;

	public SampleData() {

		this.data = Double.NaN;
		this.normalizedData = Double.NaN;
	}

	public SampleData(double data) {

		this();
		this.data = data;
		this.normalizedData = data;
	}

	@Override
	public double getData() {

		return data;
	}

	@Override
	public double getModifiedData() {

		return normalizedData;
	}

	@Override
	public void setModifiedData(double normalizedData) {

		this.normalizedData = normalizedData;
	}
}
