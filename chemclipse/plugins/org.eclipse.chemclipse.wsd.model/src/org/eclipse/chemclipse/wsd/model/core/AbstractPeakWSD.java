/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core;

import java.util.List;

import org.eclipse.chemclipse.model.core.AbstractPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.model.targets.PeakTarget;
import org.eclipse.chemclipse.wsd.model.core.identifier.scan.IScanTargetWSD;

public abstract class AbstractPeakWSD extends AbstractPeak implements IPeakWSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 641004513534018519L;
	//
	private IPeakModelWSD peakModel;

	public AbstractPeakWSD(IPeakModelWSD peakModel) throws IllegalArgumentException {
		validatePeakModel(peakModel);
		this.peakModel = peakModel;
	}

	public AbstractPeakWSD(IPeakModelWSD peakModel, String modelDescription) throws IllegalArgumentException {
		this(peakModel);
		setModelDescription(modelDescription);
	}

	@Override
	public IPeakModelWSD getPeakModel() {

		return peakModel;
	}

	@Override
	public List<IPeakTarget> getTargets() {

		IScan scan = getPeakModel().getPeakMaximum();
		if(scan instanceof IScanWSD) {
			IScanWSD scanWSD = (IScanWSD)scan;
			List<IScanTargetWSD> targets = scanWSD.getTargets();
			if(targets.size() > 0) {
				for(IScanTargetWSD target : targets) {
					addTarget(new PeakTarget(target.getLibraryInformation(), target.getComparisonResult()));
				}
			}
		}
		//
		return super.getTargets();
	}
}
