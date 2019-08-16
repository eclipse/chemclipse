/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model;

public interface IWncIon {

	int getIon();

	String getName();

	double getPercentageMaxIntensity();

	void setPercentageMaxIntensity(double percentageMaxIntensity);

	double getPercentageSumIntensity();

	void setPercentageSumIntensity(double percentageSumIntensity);
}
