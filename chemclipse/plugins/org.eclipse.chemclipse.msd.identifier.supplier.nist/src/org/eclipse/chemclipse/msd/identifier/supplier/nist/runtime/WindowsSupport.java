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

import org.eclipse.chemclipse.support.runtime.AbstractWindowsSupport;

public class WindowsSupport extends AbstractWindowsSupport implements IExtendedRuntimeSupport {

	private INistSupport nistSupport;

	public WindowsSupport(String application, String parameter) throws FileNotFoundException {
		super(application, parameter);
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
		 * Returns e.g.: "C:\Programs\NIST\MSSEARCH\nistms.exe
		 */
		StringBuilder builder = new StringBuilder();
		builder.append(getApplication().replace("nistms$.exe", "nistms.exe")); // run the GUI version
		return builder.toString();
	}

	private String getKillCommand() {

		String command = "";
		if(isValidApplicationExecutable()) {
			/*
			 * taskkill only kills the NIST-DB application.
			 */
			command = "taskkill /IM nistms.exe";
		}
		return command;
	}
}
