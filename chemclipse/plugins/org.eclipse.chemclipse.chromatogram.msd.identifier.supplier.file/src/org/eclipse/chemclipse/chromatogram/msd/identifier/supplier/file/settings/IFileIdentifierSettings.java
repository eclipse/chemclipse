/*******************************************************************************
 * Copyright (c) 2015, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - extend IIdentifierSettingsMSD
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IIdentifierSettingsMSD;

public interface IFileIdentifierSettings extends IIdentifierSettingsMSD {

	String getMassSpectraFiles();

	void setMassSpectraFiles(String massSpectraFiles);

	@Override
	String getMassSpectrumComparatorId();

	boolean isUsePreOptimization();

	void setUsePreOptimization(boolean usePreOptimization);

	double getThresholdPreOptimization();

	void setThresholdPreOptimization(double thresholdPreOptimization);

	int getNumberOfTargets();

	void setNumberOfTargets(int numberOfTargets);

	float getMinMatchFactor();

	void setMinMatchFactor(float minMatchFactor);

	float getMinReverseMatchFactor();

	void setMinReverseMatchFactor(float minReverseMatchFactor);

	boolean isAddUnknownMzListTarget();

	void setAddUnknownMzListTarget(boolean addUnknownMzListTarget);

	String getAlternateIdentifierId();

	void setAlternateIdentifierId(String alternateIdentifierId);
}
