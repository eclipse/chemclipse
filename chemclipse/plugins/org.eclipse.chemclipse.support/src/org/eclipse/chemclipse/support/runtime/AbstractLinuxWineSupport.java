/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
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

public abstract class AbstractLinuxWineSupport extends AbstractWineRuntimeSupport implements IWineRuntimeSupport {

	/**
	 * /home/chemclipse/.wine/dosdevices/c:/Programme/NIST/MSSEARCH/nistms$.exe<br/>
	 * /home/chemclipse/.wine/drive_c/Programme/NIST/MSSEARCH/nistms$.exe<br/>
	 * 
	 * @param application
	 * @param parameter
	 */
	public AbstractLinuxWineSupport(String application, String parameter) throws FileNotFoundException {
		super(application, parameter);
	}

	@Override
	public Process executeRunCommand() throws IOException {

		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(getRunCommand());
		return process;
	}

	private String getRunCommand() {

		/*
		 * "env WINEPREFIX=/home/eselmeister/.wine wine start C:\\programme\\nist\\MSSEARCH\\nistms$.exe /INSTRUMENT /PAR=2"
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
		builder.append(getWineApplication());
		builder.append(" ");
		builder.append(getParameter());
		return builder.toString();
	}
}
