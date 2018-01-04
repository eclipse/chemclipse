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

import org.eclipse.chemclipse.support.runtime.AbstractLinuxWineSupport;

public class LinuxWineSupport extends AbstractLinuxWineSupport implements IExtendedRuntimeSupport {

	private IAmdisSupport amdisSupport;

	public LinuxWineSupport(String application, String parameter) throws FileNotFoundException {
		super(application, parameter);
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

	@Override
	public Process executeKillCommand() throws IOException {

		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(getKillCommand());
		return process;
	}

	@Override
	public Process executeOpenCommand() throws IOException {

		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(getOpenCommand());
		return process;
	}

	private String getKillCommand() {

		/*
		 * "pkill -f AMDIS"
		 */
		String command = "";
		if(isValidApplicationExecutable()) {
			StringBuilder builder = new StringBuilder();
			builder.append("pkill -f");
			builder.append(" ");
			builder.append("AMDIS");
			command = builder.toString();
		}
		return command;
	}

	private String getOpenCommand() {

		/*
		 * "env WINEPREFIX=/home/eselmeister/.wine wine start C:\\programme\\nist\\AMDIS32-271\\AMDIS32$.exe"
		 */
		StringBuilder builder = new StringBuilder();
		/*
		 * LINUX, UNIX
		 */
		builder.append("env WINEPREFIX=");
		builder.append(getWineEnvironment());
		builder.append(" ");
		builder.append("wine start");
		builder.append(" ");
		builder.append(getWineApplication().replace("AMDIS32$.exe", "AMDIS_32.exe")); // run the GUI version
		return builder.toString();
	}
}
