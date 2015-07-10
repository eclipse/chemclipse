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
package org.eclipse.chemclipse.numeric.statistics.model;

public class AnovaStatistics implements IAnovaStatistics {

	final double pvalue;
	final double fvalue;

	public AnovaStatistics(double pvalue, double fvalue) {

		this.pvalue = pvalue;
		this.fvalue = fvalue;
	}

	@Override
	public double getPValue() {

		return pvalue;
	}

	@Override
	public double getFValue() {

		return fvalue;
	}
}
