/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.runtime;

import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class AbstractMacWineSupport extends AbstractWineRuntimeSupport implements IWineRuntimeSupport {

	private String macWineBinary = "";

	/**
	 * /Users/chemclipse/.wine/drive_c/Programme/NIST/MSSEARCH/nistms$.exe
	 * /Users/chemclipse/.wine/dosdevices/c:/Programme/NIST/MSSEARCH/nistms$.exe
	 * 
	 * @param application
	 * @param parameter
	 * @param macWineBinary
	 *            (e.g. "/Applications/Wine.app")
	 */
	protected AbstractMacWineSupport(String application, String parameter, String macWineBinary) throws FileNotFoundException {

		super(application, parameter);
		this.macWineBinary = macWineBinary;
	}

	@Override
	public Process executeRunCommand() throws IOException {

		Runtime runtime = Runtime.getRuntime();
		return runtime.exec(getOpenCommand()); // the run command still not works, hence use the open command
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

	private String[] getOpenCommand() {

		/*
		 * String[]{"open", "/Applications/Wine.app", "/Users/chemclipse/Wine Files/drive_c/NISTDEMO/MSSEARCH/nistms.exe"}
		 */
		return new String[]{"open", macWineBinary, getApplication().replace("$.exe", ".exe")};
	}

	private String[] getKillCommand() {

		String command = "";
		if(isValidApplicationExecutable()) {
			StringBuilder builder = new StringBuilder();
			builder.append("$(ps ax | grep '");
			builder.append(getApplicationExecutable());
			builder.append("' | grep -v grep | awk '{print $1}')");
			command = builder.toString();
		}
		return new String[]{"kill -9", command};
	}
}
