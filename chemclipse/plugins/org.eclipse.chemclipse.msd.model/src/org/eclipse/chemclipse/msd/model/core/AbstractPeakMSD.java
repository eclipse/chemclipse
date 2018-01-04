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
package org.eclipse.chemclipse.msd.model.core;

import java.util.List;

import org.eclipse.chemclipse.model.core.AbstractPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.model.targets.PeakTarget;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IScanTargetMSD;

public abstract class AbstractPeakMSD extends AbstractPeak implements IPeakMSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 4889978678251526852L;
	//
	final private IPeakModelMSD peakModel;

	public AbstractPeakMSD(IPeakModelMSD peakModel) throws IllegalArgumentException {
		validatePeakModel(peakModel);
		this.peakModel = peakModel;
	}

	public AbstractPeakMSD(IPeakModelMSD peakModel, String modelDescription) throws IllegalArgumentException {
		this(peakModel);
		setModelDescription(modelDescription);
	}

	@Override
	public IPeakModelMSD getPeakModel() {

		return peakModel;
	}

	@Override
	public IPeakMassSpectrum getExtractedMassSpectrum() {

		return peakModel.getPeakMassSpectrum();
	}

	@Override
	public List<IPeakTarget> getTargets() {

		IScan scan = getPeakModel().getPeakMaximum();
		if(scan instanceof IScanMSD) {
			IScanMSD scanMSD = (IScanMSD)scan;
			List<IScanTargetMSD> targets = scanMSD.getTargets();
			if(targets.size() > 0) {
				for(IScanTargetMSD target : targets) {
					addTarget(new PeakTarget(target.getLibraryInformation(), target.getComparisonResult()));
				}
			}
		}
		//
		return super.getTargets();
	}

	@Override
	public boolean equals(Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		AbstractPeakMSD other = (AbstractPeakMSD)otherObject;
		return peakModel.equals(other.getPeakModel());
	}

	@Override
	public int hashCode() {

		return 7 * peakModel.hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("peakModel=" + peakModel);
		builder.append("]");
		return builder.toString();
	}
}
