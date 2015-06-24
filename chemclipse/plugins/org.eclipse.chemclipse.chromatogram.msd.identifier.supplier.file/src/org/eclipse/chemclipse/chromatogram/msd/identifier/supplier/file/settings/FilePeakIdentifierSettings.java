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

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.AbstractPeakIdentifierSettings;

public class FilePeakIdentifierSettings extends AbstractPeakIdentifierSettings implements IFilePeakIdentifierSettings {

	private String massSpectraFile;
	private int numberOfTargets;
	private float minMatchFactor;
	private float minReverseMatchFactor;

	@Override
	public String getMassSpectraFile() {

		return massSpectraFile;
	}

	@Override
	public void setMassSpectraFile(String massSpectraFile) {

		this.massSpectraFile = massSpectraFile;
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
}
