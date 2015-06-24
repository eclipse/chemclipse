/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings;

public interface IFileSettings {

	String getMassSpectraFile();

	void setMassSpectraFile(String massSpectraFile);

	String getMassSpectrumComparatorId();

	int getNumberOfTargets();

	void setNumberOfTargets(int numberOfTargets);

	float getMinMatchFactor();

	void setMinMatchFactor(float minMatchFactor);

	float getMinReverseMatchFactor();

	void setMinReverseMatchFactor(float minReverseMatchFactor);
}
