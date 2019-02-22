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
import java.io.IOException;

import org.eclipse.chemclipse.support.runtime.AbstractWindowsSupport;

public class WindowsSupport extends AbstractWindowsSupport implements IExtendedRuntimeSupport {

	private IAmdisSupport amdisSupport;

	public WindowsSupport(String application, String parameter) throws FileNotFoundException {
		super(application, parameter);
		amdisSupport = new AmdisSupport(this);
	}

	@Override
	public boolean isValidApplicationExecutable() {

		return amdisSupport.validateExecutable();
	}

	@Override
	public IAmdisSupport getAmdisSupport() {

		return amdisSupport;
	}

	@Override
	public Process executeOpenCommand() throws IOException {

		Runtime runtime = Runtime.getRuntime();
		return runtime.exec(getOpenCommand());
	}

	@Override
	public Process executeKillCommand() throws IOException {

		Runtime runtime = Runtime.getRuntime();
		return runtime.exec(getKillCommand());
	}

	private String getOpenCommand() {

		/*
		 * Returns e.g.: "C:\Programs\NIST\AMDIS32\AMDIS32$.exe
		 */
		StringBuilder builder = new StringBuilder();
		builder.append(getApplication().replace("AMDIS32$.exe", "AMDIS_32.exe")); // run the GUI version
		return builder.toString();
	}

	private String getKillCommand() {

		String command = "";
		if(isValidApplicationExecutable()) {
			/*
			 * taskkill kills the e.g. AMDIS application.
			 */
			command = "taskkill /f /IM AMDIS_32.exe";
		}
		return command;
	}
}
