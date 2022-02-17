/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.io.File;

public abstract class AbstractVendorMassSpectrum extends AbstractRegularMassSpectrum implements IVendorMassSpectrum {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 5013842421250687341L;
	//
	private File file;

	@Override
	public File getFile() {

		return file;
	}

	@Override
	public void setFile(File file) {

		this.file = file;
	}

	@Override
	public String getName() {

		String name = "No file has been set yet.";
		if(file != null) {
			name = file.getName();
		}
		return name;
	}

	@Override
	public boolean checkIntensityCollisions() {

		return true;
	}

	@Override
	public AbstractVendorMassSpectrum addIon(IIon ion) {

		if(getNumberOfIons() < getMaxPossibleIons()) {
			super.addIon(ion);
		}
		return this;
	}

	@Override
	public void setRetentionTime(int retentionTime) {

		if(retentionTime >= getMinPossibleRetentionTime() && retentionTime <= getMaxPossibleRetentionTime()) {
			super.setRetentionTime(retentionTime);
		}
	}
}
