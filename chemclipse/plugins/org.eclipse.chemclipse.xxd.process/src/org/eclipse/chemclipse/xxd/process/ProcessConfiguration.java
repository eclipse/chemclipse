/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.SystemSettings;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@SystemSettings(SystemSettingsStrategy.NONE)
public class ProcessConfiguration {

	public static final String VARIABLE_CHROMATOGRAM_NAME = "{chromatogram_name}";
	@JsonProperty(value = "Working Directory")
	@FileSettingProperty(onlyDirectory = true)
	private File workingDirectory;
	@JsonProperty(value = "Command", defaultValue = "")
	@JsonPropertyDescription("the system command to execute, the follwoing variables might be used: " + VARIABLE_CHROMATOGRAM_NAME)
	private String command = "";
	@JsonProperty(value = "Sucess Exit Code", defaultValue = "0")
	@JsonPropertyDescription("The exit code that is returned when the program was sucessfull")
	private int successCode = 0;
	@JsonProperty(value = "Timeout", defaultValue = "30")
	private long timeout = 30;
	@JsonProperty(value = "Unit", defaultValue = "SECONDS")
	private TimeUnit timeoutUnit = TimeUnit.SECONDS;

	public ProcessConfiguration() {
		workingDirectory = new File(System.getProperty("java.io.tmpdir"));
	}

	public File getWorkingDirectory() {

		return workingDirectory;
	}

	public void setWorkingDirectory(File workingDirectory) {

		this.workingDirectory = workingDirectory;
	}

	public String getCommand() {

		return command;
	}

	public void setCommand(String command) {

		this.command = command;
	}

	public int getSuccessCode() {

		return successCode;
	}

	public void setSuccessCode(int successCode) {

		this.successCode = successCode;
	}

	public TimeUnit getTimeoutUnit() {

		return timeoutUnit;
	}

	public void setTimeoutUnit(TimeUnit timeoutUnit) {

		this.timeoutUnit = timeoutUnit;
	}

	public long getTimeout() {

		return timeout;
	}

	public void setTimeout(long timeout) {

		this.timeout = timeout;
	}
}
