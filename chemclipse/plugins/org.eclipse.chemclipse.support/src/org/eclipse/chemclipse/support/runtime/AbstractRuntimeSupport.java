/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.runtime;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public abstract class AbstractRuntimeSupport implements IRuntimeSupport {

	private String application = "";
	private String path = "";
	private String executable = "";
	private List<String> parameters = null;

	protected AbstractRuntimeSupport(String application, List<String> parameters) throws FileNotFoundException {

		/*
		 * Application
		 */
		try {
			File file = new File(application);
			if(!file.exists()) {
				throw new FileNotFoundException();
			} else {
				this.application = application;
				path = file.getParent();
				executable = application.replace(path + File.separator, "");
			}
		} catch(Exception e) {
			throw new FileNotFoundException();
		}
		/*
		 * Parameter
		 */
		if(parameters != null) {
			this.parameters = parameters;
		}
	}

	public List<String> getCommand() {

		List<String> command = parameters;
		command.add(0, getApplication());
		return command;
	}

	@Override
	public String getApplication() {

		return application;
	}

	@Override
	public List<String> getParameters() {

		return parameters;
	}

	@Override
	public String getApplicationPath() {

		return path;
	}

	@Override
	public String getApplicationExecutable() {

		return executable;
	}

	@Override
	public int getSleepMillisecondsBeforeExecuteRunCommand() {

		/*
		 * Wait 3 second before starting the application.
		 */
		return 3000;
	}
}
