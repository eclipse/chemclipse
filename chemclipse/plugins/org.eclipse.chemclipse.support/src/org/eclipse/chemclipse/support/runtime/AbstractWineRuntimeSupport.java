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

import java.io.File;
import java.io.FileNotFoundException;

public abstract class AbstractWineRuntimeSupport extends AbstractRuntimeSupport implements IWineRuntimeSupport {

	private String wineEnvironment = "";
	private String wineApplication = "";

	public AbstractWineRuntimeSupport(String application, String parameter) throws FileNotFoundException {
		super(application, parameter);
		extractValues();
	}

	@Override
	public String getWineEnvironment() {

		return wineEnvironment;
	}

	@Override
	public String getWineApplication() {

		return wineApplication;
	}

	/**
	 * Extracts the values from the application path.
	 */
	private void extractValues() {

		String DOSDEVICES = File.separator + "dosdevices" + File.separator;
		String DRIVE = File.separator + "drive_";
		String[] parts = null;
		/*
		 * Get the parts.
		 */
		String nistApplication = getApplication();
		if(nistApplication.contains(DOSDEVICES)) {
			/*
			 * /home/chemclipse/.wine/dosdevices/c:/Programme/NIST/MSSEARCH/nistms$.exe
			 * =>
			 * parts[0] = /home/chemclipse/.wine
			 * parts[1] = c:/Programme/NIST/MSSEARCH/nistms$.exe
			 */
			parts = nistApplication.split(DOSDEVICES);
		} else if(nistApplication.contains(DRIVE)) {
			/*
			 * /home/chemclipse/.wine/drive_c/Programme/NIST/MSSEARCH/nistms$.exe
			 * =>
			 * parts[0] = /home/chemclipse/.wine
			 * parts[1] = c/Programme/NIST/MSSEARCH/nistms$.exe
			 */
			parts = nistApplication.split(DRIVE);
			/*
			 * parts[1] = c/Programme/NIST/MSSEARCH/nistms$.exe
			 * =>
			 * parts[1] = c:/Programme/NIST/MSSEARCH/nistms$.exe
			 */
			parts[1] = parts[1].replaceFirst("/", ":/");
		}
		/*
		 * Set the variables.
		 */
		if(parts != null) {
			wineEnvironment = parts[0]; // "/home/chemclipse/.wine"
			wineApplication = parts[1].replace("/", "\\"); // "c:\Programme\NIST\MSSEARCH\nistms$.exe"
		}
	}
}
