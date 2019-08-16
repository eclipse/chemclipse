/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.support.runtime.AbstractLinuxWineSupport;

public class LinuxWineSupport extends AbstractLinuxWineSupport implements IExtendedRuntimeSupport {

	private INistSupport nistSupport;

	public LinuxWineSupport(String application, String parameter) throws FileNotFoundException {
		super(application, parameter);
		nistSupport = new NistSupport(this);
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

		return nistSupport.validateExecutable();
	}

	@Override
	public INistSupport getNistSupport() {

		return nistSupport;
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
		 * "pkill -f nist"
		 */
		String command = "";
		if(isValidApplicationExecutable()) {
			StringBuilder builder = new StringBuilder();
			builder.append("pkill -f");
			builder.append(" ");
			builder.append("nist");
			command = builder.toString();
		}
		return command;
	}

	private String getOpenCommand() {

		/*
		 * "env WINEPREFIX=/home/eselmeister/.wine wine start C:\\programme\\nist\\MSSEARCH\\nistms.exe"
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
		builder.append(getWineApplication().replace("$.exe", ".exe")); // run the GUI version
		return builder.toString();
	}
}
