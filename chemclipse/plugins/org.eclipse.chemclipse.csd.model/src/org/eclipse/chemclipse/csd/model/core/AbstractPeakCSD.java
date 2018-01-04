/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core;

import java.util.List;

import org.eclipse.chemclipse.csd.model.core.identifier.scan.IScanTargetCSD;
import org.eclipse.chemclipse.model.core.AbstractPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.model.targets.PeakTarget;

public abstract class AbstractPeakCSD extends AbstractPeak implements IPeakCSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -1459911842215911857L;
	//
	private IPeakModelCSD peakModel;

	public AbstractPeakCSD(IPeakModelCSD peakModel) throws IllegalArgumentException {
		validatePeakModel(peakModel);
		this.peakModel = peakModel;
	}

	public AbstractPeakCSD(IPeakModelCSD peakModel, String modelDescription) throws IllegalArgumentException {
		this(peakModel);
		setModelDescription(modelDescription);
	}

	@Override
	public IPeakModelCSD getPeakModel() {

		return peakModel;
	}

	@Override
	public List<IPeakTarget> getTargets() {

		IScan scan = getPeakModel().getPeakMaximum();
		if(scan instanceof IScanCSD) {
			IScanCSD scanCSD = (IScanCSD)scan;
			List<IScanTargetCSD> targets = scanCSD.getTargets();
			if(targets.size() > 0) {
				for(IScanTargetCSD target : targets) {
					addTarget(new PeakTarget(target.getLibraryInformation(), target.getComparisonResult()));
				}
			}
		}
		//
		return super.getTargets();
	}
}
