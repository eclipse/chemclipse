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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ProcessProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "External Programs";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessProcessSupplier(this));
	}

	private static final class ProcessProcessSupplier extends AbstractProcessSupplier<ProcessConfiguration> implements IChromatogramSelectionProcessSupplier<ProcessConfiguration> {

		public ProcessProcessSupplier(IProcessTypeSupplier parent) {
			super("org.eclipse.chemclipse.xxd.process.ProcessProcessTypeSupplier.ProcessProcessSupplier", "Execute Operating System Command", "Executes an arbitrary native operation system command", ProcessConfiguration.class, parent, DataCategory.MSD, DataCategory.WSD, DataCategory.CSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, ProcessConfiguration processSettings, ProcessExecutionContext context) throws InterruptedException {

			if(processSettings == null) {
				processSettings = new ProcessConfiguration();
			}
			ProcessBuilder processBuilder = getProcessBuilder(chromatogramSelection, processSettings);
			try {
				Process process = processBuilder.start();
				ExecutorService executor = Executors.newSingleThreadExecutor();
				try {
					Future<String> processOutput = executor.submit(() -> IOUtils.toString(process.getInputStream(), Charset.defaultCharset()));
					if(process.waitFor(processSettings.getTimeout(), processSettings.getTimeoutUnit())) {
						try {
							String output = processOutput.get();
							int exitValue = process.exitValue();
							if(exitValue == processSettings.getSuccessCode()) {
								context.addMessage(getName(), "Executed sucessfull", output, null, MessageType.INFO);
							} else {
								context.addMessage(getName(), "Execution failed with code " + exitValue, output, null, MessageType.ERROR);
							}
						} catch(ExecutionException e) {
							context.addErrorMessage(getDescription(), "Fetching process output failed", e);
						}
					} else {
						process.destroyForcibly();
						context.addErrorMessage(getName(), "Timeout elapsed");
					}
				} finally {
					executor.shutdownNow();
				}
			} catch(IOException e) {
				context.addErrorMessage(getDescription(), "Ecxecution of " + processSettings.getCommand() + " failed", e);
			}
			return chromatogramSelection;
		}

		private ProcessBuilder getProcessBuilder(IChromatogramSelection<?, ?> chromatogramSelection, ProcessConfiguration processSettings) {

			String chromatogramName = chromatogramSelection.getChromatogram().getName();
			String command = processSettings.getCommand();
			StringTokenizer st = new StringTokenizer(command);
			List<String> commands = new ArrayList<>();
			while(st.hasMoreTokens()) {
				commands.add(st.nextToken().replace(ProcessConfiguration.VARIABLE_CHROMATOGRAM_NAME, chromatogramName));
			}
			return new ProcessBuilder(commands).directory(processSettings.getWorkingDirectory()).redirectErrorStream(true);
		}
	}
}
