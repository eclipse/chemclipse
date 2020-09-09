/*******************************************************************************
 * Copyright (c) 2020 Christoph Läubrich.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - adjust bundle/class naming conventions
 *******************************************************************************/
package org.eclipse.chemclipse.ui.cli.converter;

import java.io.File;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.rcp.app.cli.AbstractCommandLineProcessor;
import org.eclipse.chemclipse.rcp.app.cli.ICommandLineProcessor;
import org.eclipse.chemclipse.ui.cli.ContextCLI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

public abstract class ChromatogramImportProcessor extends AbstractCommandLineProcessor implements ICommandLineProcessor {

	private static final NullProgressMonitor MONITOR = new NullProgressMonitor();
	private static final Logger LOG = Logger.getLogger(ChromatogramImportProcessor.class);

	@Override
	public void process(String[] args) {

		for(String arg : args) {
			File file = new File(arg).getAbsoluteFile();
			LOG.info("Reading file " + file.getPath() + "...");
			IProcessingInfo<? extends IChromatogram<?>> info = load(file, MONITOR);
			if(info == null || info.hasErrorMessages() || info.getProcessingResult() == null) {
				LOG.info("... failed!");
				continue;
			}
			ContextCLI.addChromatogram(info.getProcessingResult());
		}
	}

	protected abstract IProcessingInfo<? extends IChromatogram<?>> load(File file, IProgressMonitor monitor);
}
