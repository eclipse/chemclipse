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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.helper.PathHelper;
import org.eclipse.chemclipse.support.runtime.IRuntimeSupport;

public class NistSupport implements INistSupport {

	private static final Logger logger = Logger.getLogger(NistSupport.class);
	//
	private IRuntimeSupport runtimeSupport;
	private int numberOfUnknownEntriesToProcess = 1;

	public NistSupport(IRuntimeSupport runtimeSupport) {
		this.runtimeSupport = runtimeSupport;
	}

	@Override
	public void setNumberOfTargets(int numberOfTargets) {

		try {
			/*
			 * Parse the settings file and replace the hits to print number.
			 */
			File nistSettings = new File(getNISTSettingsFile());
			FileReader fileReader = new FileReader(nistSettings);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			// Writer
			File nistSettingsTmp = new File(PathHelper.getStoragePath() + File.separator + INistSupport.NIST_SETTINGS_FILE);
			PrintWriter printWriter = new PrintWriter(nistSettingsTmp);
			String line;
			while((line = bufferedReader.readLine()) != null) {
				if(line.startsWith(INistSupport.HITS_TO_PRINT)) {
					printWriter.println(INistSupport.HITS_TO_PRINT + numberOfTargets);
				} else {
					printWriter.println(line);
				}
			}
			// Close the streams.
			printWriter.flush();
			printWriter.close();
			bufferedReader.close();
			fileReader.close();
			/*
			 * Copy the temporary file.
			 */
			FileReader in = new FileReader(nistSettingsTmp);
			FileWriter out = new FileWriter(nistSettings);
			int b;
			while((b = in.read()) != -1) {
				out.write(b);
			}
			in.close();
			out.flush();
			out.close();
		} catch(IOException e) {
			logger.warn(e);
		}
	}

	// --------------------------------------------general
	@Override
	public boolean validateExecutable() {

		return runtimeSupport.getApplicationExecutable().toLowerCase().startsWith(NIST_EXE_IDENTIFIER_LC);
	}

	// --------------------------------------------nist library specific
	@Override
	public String getNistlogFile() {

		return runtimeSupport.getApplicationPath() + File.separator + INistSupport.NISTLOG_FILE;
	}

	@Override
	public String getSrcreadyFile() {

		return runtimeSupport.getApplicationPath() + File.separator + INistSupport.SRCREADY_FILE;
	}

	@Override
	public String getSrcresltFile() {

		return runtimeSupport.getApplicationPath() + File.separator + INistSupport.SRCRESLT_FILE;
	}

	@Override
	public String getAutoimpFile() {

		return runtimeSupport.getApplicationPath() + File.separator + INistSupport.AUTOIMP_FILE;
	}

	@Override
	public String getNISTSettingsFile() {

		return runtimeSupport.getApplicationPath() + File.separator + INistSupport.NIST_SETTINGS_FILE;
	}

	@Override
	public void setNumberOfUnknownEntriesToProcess(int numberOfUnknownEntriesToProcess) {

		this.numberOfUnknownEntriesToProcess = numberOfUnknownEntriesToProcess;
	}

	@Override
	public int getNumberOfUnknownEntriesToProcess() {

		return numberOfUnknownEntriesToProcess;
	}

	// --------------------------------------------openchrom specific
	@Override
	public String getFilespecFile() {

		return PathHelper.getStoragePath() + File.separator + INistSupport.FILESPEC_FILE;
	}

	@Override
	public String getMassSpectraFile() {

		return PathHelper.getStoragePath() + File.separator + INistSupport.MASSSPECTRA_FILE;
	}
}
