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

import java.io.IOException;

public interface IRuntimeSupport {

	/**
	 * Executes the run command and returns the process instance.
	 * 
	 * @return {@link Process}
	 * @throws IOException
	 */
	Process executeRunCommand() throws IOException;

	/**
	 * Executes the open command and returns the process instance.
	 * 
	 * @return {@link Process}
	 * @throws IOException
	 */
	Process executeOpenCommand() throws IOException;

	/**
	 * Executes the kill command and returns the process instance.
	 * 
	 * @return {@link Process}
	 * @throws IOException
	 */
	Process executeKillCommand() throws IOException;

	/**
	 * Returns the path/file of the application exe.<br/>
	 * e.g.:<br/>
	 * "C:\Programs\NIST\MSSEARCH\NISTMS$.EXE"
	 * 
	 * @return String
	 */
	String getApplication();

	/**
	 * Returns the parameter.
	 * E.g. /INSTRUMENT /PAR=2
	 * 
	 * @return String
	 */
	String getParameter();

	/**
	 * Return the path of the application exe.<br/>
	 * e.g.:<br/>
	 * "C:\Programs\NIST\MSSEARCH\"
	 * 
	 * @return
	 */
	String getApplicationPath();

	/**
	 * "nistms$.exe"
	 * 
	 * @return String
	 */
	String getApplicationExecutable();

	/**
	 * Tests whether the NistExecutable is valid.
	 * It should prevent killing other files than the nistms$.exe.
	 * 
	 * @return boolean
	 */
	boolean isValidApplicationExecutable();

	/**
	 * Default: 1000 ms
	 * 
	 * The execution works smoothly under Windows.
	 * But if it will be executed under Linux, the program execution shall be
	 * delayed depending on the numbers of unknown entries to process.
	 * 
	 * @return int
	 */
	int getSleepMillisecondsBeforeExecuteRunCommand();
}
