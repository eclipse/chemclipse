/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
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

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.AbstractPeakIdentifierSettings;

public class FilePeakIdentifierSettings extends AbstractPeakIdentifierSettings implements IFilePeakIdentifierSettings {

	private List<String> massSpectraFiles;
	private int numberOfTargets;
	private float minMatchFactor;
	private float minReverseMatchFactor;
	private boolean addUnknownMzListTarget;

	@Override
	public List<String> getMassSpectraFiles() {

		return massSpectraFiles;
	}

	@Override
	public void setMassSpectraFiles(List<String> massSpectraFiles) {

		this.massSpectraFiles = massSpectraFiles;
	}

	@Override
	public int getNumberOfTargets() {

		return numberOfTargets;
	}

	@Override
	public void setNumberOfTargets(int numberOfTargets) {

		this.numberOfTargets = numberOfTargets;
	}

	@Override
	public float getMinMatchFactor() {

		return minMatchFactor;
	}

	@Override
	public void setMinMatchFactor(float minMatchFactor) {

		this.minMatchFactor = minMatchFactor;
	}

	@Override
	public float getMinReverseMatchFactor() {

		return minReverseMatchFactor;
	}

	@Override
	public void setMinReverseMatchFactor(float minReverseMatchFactor) {

		this.minReverseMatchFactor = minReverseMatchFactor;
	}

	@Override
	public boolean isAddUnknownMzListTarget() {

		return addUnknownMzListTarget;
	}

	@Override
	public void setAddUnknownMzListTarget(boolean addUnknownMzListTarget) {

		this.addUnknownMzListTarget = addUnknownMzListTarget;
	}
}
