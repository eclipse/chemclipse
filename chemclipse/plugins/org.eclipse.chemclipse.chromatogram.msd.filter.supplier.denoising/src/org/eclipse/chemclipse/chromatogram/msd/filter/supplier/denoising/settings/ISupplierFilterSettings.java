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

import org.eclipse.chemclipse.model.support.SegmentWidth;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;

public interface ISupplierFilterSettings extends IChromatogramFilterSettings {

	/**
	 * Returns the ions that should be removed in all cases.
	 * 
	 * @return {@link IMarkedIons}
	 */
	IMarkedIons getIonsToRemove();

	/**
	 * Returns the ions which shall be preserved while calculating the
	 * noise mass spectrum.
	 * 
	 * @return {@link IMarkedIons}
	 */
	IMarkedIons getIonsToPreserve();

	/**
	 * Returns whether threshold transition adjustment shall be used or not.
	 * 
	 * @return
	 */
	boolean getAdjustThresholdTransitions();

	/**
	 * Sets whether threshold transition adjustment shall be used or not.
	 * 
	 * @param adjustThresholdTransitions
	 */
	void setAdjustThresholdTransitions(boolean adjustThresholdTransitions);

	/**
	 * Returns the number of used ions for the calculation of the
	 * coefficient.
	 * 
	 * @return
	 */
	int getNumberOfUsedIonsForCoefficient();

	/**
	 * Sets the number of used ions for the calculation of the
	 * coefficient.
	 */
	void setNumberOfUsedIonsForCoefficient(int numberOfUsedIonsForCoefficient);

	/**
	 * Returns the segment width enum.
	 * 
	 * @return {@link SegmentWidth}
	 */
	SegmentWidth getSegmentWidth();

	/**
	 * Sets the segment width enum.
	 * 
	 * @param segmentWidth
	 */
	void setSegmentWidth(SegmentWidth segmentWidth);
}
