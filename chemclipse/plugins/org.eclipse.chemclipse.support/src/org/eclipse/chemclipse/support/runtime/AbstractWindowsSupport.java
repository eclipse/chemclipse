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

public abstract class AbstractWindowsSupport extends AbstractRuntimeSupport implements IRuntimeSupport {

	/**
	 * Set e.g.: C:\Programs\NIST\MSSEARCH\NISTMS$.EXE
	 * 
	 * @param application
	 * @param parameter
	 */
	protected AbstractWindowsSupport(String application, String parameter) throws FileNotFoundException {

		super(application, parameter);
	}

	@Override
	public Process executeRunCommand() throws IOException {

		return getRunCommand().start();
	}

	private ProcessBuilder getRunCommand() {

		/*
		 * Returns e.g.: "C:\Programs\NIST\MSSEARCH\nistms$.exe /INSTRUMENT /PAR=2
		 */
		return new ProcessBuilder(getApplication(), getParameter());
	}
}
