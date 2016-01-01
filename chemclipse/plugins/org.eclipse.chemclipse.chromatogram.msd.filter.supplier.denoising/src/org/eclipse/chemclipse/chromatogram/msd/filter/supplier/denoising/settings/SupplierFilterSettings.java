/*******************************************************************************
 * Copyright (c) 2010, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.settings;

import org.eclipse.chemclipse.model.support.SegmentWidth;
import org.eclipse.chemclipse.chromatogram.filter.settings.AbstractChromatogramFilterSettings;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;

public class SupplierFilterSettings extends AbstractChromatogramFilterSettings implements ISupplierFilterSettings {

	private IMarkedIons ionsToRemove;
	private IMarkedIons ionsToPreserve;
	private boolean adjustThresholdTransitions;
	private int numberOfUsedIonsForCoefficient;
	private SegmentWidth segmentWidth;

	public SupplierFilterSettings() {
		ionsToRemove = new MarkedIons();
		ionsToPreserve = new MarkedIons();
		adjustThresholdTransitions = true;
		numberOfUsedIonsForCoefficient = 1;
		segmentWidth = SegmentWidth.WIDTH_13;
	}

	@Override
	public IMarkedIons getIonsToRemove() {

		return ionsToRemove;
	}

	@Override
	public IMarkedIons getIonsToPreserve() {

		return ionsToPreserve;
	}

	@Override
	public boolean getAdjustThresholdTransitions() {

		return adjustThresholdTransitions;
	}

	@Override
	public void setAdjustThresholdTransitions(boolean adjustThresholdTransitions) {

		this.adjustThresholdTransitions = adjustThresholdTransitions;
	}

	@Override
	public int getNumberOfUsedIonsForCoefficient() {

		return numberOfUsedIonsForCoefficient;
	}

	@Override
	public void setNumberOfUsedIonsForCoefficient(int numberOfUsedIonsForCoefficient) {

		if(numberOfUsedIonsForCoefficient <= 0) {
			this.numberOfUsedIonsForCoefficient = 1;
		} else {
			this.numberOfUsedIonsForCoefficient = numberOfUsedIonsForCoefficient;
		}
	}

	@Override
	public SegmentWidth getSegmentWidth() {

		return segmentWidth;
	}

	@Override
	public void setSegmentWidth(SegmentWidth segmentWidth) {

		if(segmentWidth != null) {
			this.segmentWidth = segmentWidth;
		}
	}
}
