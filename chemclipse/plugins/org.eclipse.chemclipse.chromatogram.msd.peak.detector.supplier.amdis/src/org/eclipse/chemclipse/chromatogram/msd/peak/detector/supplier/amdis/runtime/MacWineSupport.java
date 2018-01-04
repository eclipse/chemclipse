/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.runtime;

import java.io.FileNotFoundException;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.runtime.AbstractMacWineSupport;

public class MacWineSupport extends AbstractMacWineSupport implements IExtendedRuntimeSupport {

	private IAmdisSupport amdisSupport;

	public MacWineSupport(String application, String parameter) throws FileNotFoundException {
		super(application, parameter, PreferenceSupplier.getMacWineBinary());
		amdisSupport = new AmdisSupport(this);
	}

	@Override
	public int getSleepMillisecondsBeforeExecuteRunCommand() {

		/*
		 * I've recognized that the e.g. NIST-DB sometimes don't start.
		 * Does a sleep time preventing this?
		 */
		return 4000;
	}

	@Override
	public boolean isValidApplicationExecutable() {

		return amdisSupport.validateExecutable();
	}

	@Override
	public IAmdisSupport getAmdisSupport() {

		return amdisSupport;
	}
}
