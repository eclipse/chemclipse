/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
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
import java.io.IOException;

import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.runtime.AbstractWindowsSupport;

public class WindowsSupport extends AbstractWindowsSupport implements IExtendedRuntimeSupport {

	private final INistSupport nistSupport;

	public WindowsSupport(File applicationFolder, String parameter) throws FileNotFoundException {

		super(PreferenceSupplier.getNistExecutable(applicationFolder).getAbsolutePath(), parameter);
		nistSupport = new NistSupport(this);
	}

	@Override
	public boolean isValidApplicationExecutable() {

		return nistSupport.validateExecutable();
	}

	@Override
	public INistSupport getNistSupport() {

		return nistSupport;
	}

	@Override
	public Process executeOpenCommand() throws IOException {

		return new ProcessBuilder(getApplication()).start();
	}

	@Override
	public Process executeKillCommand() throws IOException {

		return getKillCommand().start();
	}

	private ProcessBuilder getKillCommand() {

		if(isValidApplicationExecutable()) {
			/*
			 * taskkill only kills the NIST-DB application.
			 */
			return new ProcessBuilder("taskkill", "/IM", "nistms.exe");
		}
		return new ProcessBuilder();
	}
}
