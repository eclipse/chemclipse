/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - using a path instead of a string
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.runtime.AbstractMacWineSupport;

public class MacWineSupport extends AbstractMacWineSupport implements IExtendedRuntimeSupport {

	private final INistSupport nistSupport;

	public MacWineSupport(File applicationFolder, String parameter) throws FileNotFoundException {
		super(PreferenceSupplier.getNistExecutable(applicationFolder).getAbsolutePath(), parameter, PreferenceSupplier.getMacWineBinary());
		nistSupport = new NistSupport(this);
	}

	@Override
	public int getSleepMillisecondsBeforeExecuteRunCommand() {

		int numberOfUnknownEntriesToProcess = nistSupport.getNumberOfUnknownEntriesToProcess();
		if(numberOfUnknownEntriesToProcess > 3) {
			/*
			 * Wait 2 second before starting e.g. the NIST-DB
			 */
			return 2000;
		} else {
			/*
			 * I've recognized that the e.g. NIST-DB sometimes didn't start
			 * when running an identification with only one peak or
			 * mass spectrum.
			 */
			return 3000;
		}
	}

	@Override
	public boolean isValidApplicationExecutable() {

		return nistSupport.validateExecutable();
	}

	@Override
	public INistSupport getNistSupport() {

		return nistSupport;
	}
}
