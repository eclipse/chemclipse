/*******************************************************************************
 * Copyright (c) 2010, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.settings;

import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;

public interface ISupplierFilterSettings extends IChromatogramFilterSettings {

	String getIonsToRemove();

	void setIonsToRemove(String ionsToRemove);

	String getIonsToPreserve();

	void setIonsToPreserve(String ionsToPreserve);

	boolean isAdjustThresholdTransitions();

	void setAdjustThresholdTransitions(boolean adjustThresholdTransitions);

	int getNumberOfUsedIonsForCoefficient();

	void setNumberOfUsedIonsForCoefficient(int numberOfUsedIonsForCoefficient);

	String getSegmentWidth();

	void setSegmentWidth(String segmentWidth);
}
