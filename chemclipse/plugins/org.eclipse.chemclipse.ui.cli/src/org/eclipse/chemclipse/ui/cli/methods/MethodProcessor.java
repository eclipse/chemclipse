/*******************************************************************************
 * Copyright (c) 2020, 2022 Christoph Läubrich.
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
package org.eclipse.chemclipse.ui.cli.methods;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.rcp.app.cli.AbstractCommandLineProcessor;
import org.eclipse.chemclipse.rcp.app.cli.ICommandLineProcessor;
import org.eclipse.chemclipse.ui.cli.ContextCLI;
import org.eclipse.chemclipse.ui.cli.converter.ChromatogramImportProcessor;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.NullProgressMonitor;

public class MethodProcessor extends AbstractCommandLineProcessor implements ICommandLineProcessor {

	private static final NullProgressMonitor MONITOR = new NullProgressMonitor();
	private static final Logger LOG = Logger.getLogger(ChromatogramImportProcessor.class);

	@Override
	public void process(String[] args) {

		IProcessSupplierContext processSupplierContext = ContextCLI.getProcessSupplierContext();
		if(processSupplierContext == null) {
			LOG.error("Can't get a supplier context!");
			throw new RuntimeException();
			// return;
		}
		AtomicBoolean isProcessing = new AtomicBoolean(true);
		for(String arg : args) {
			File file = new File(arg).getAbsoluteFile();
			IProcessMethod method = Adapters.adapt(file, IProcessMethod.class);
			if(method == null) {
				LOG.error("Can't read method file " + arg);
				continue;
			}
			//
			LOG.info("Applying " + method.getName() + "...");
			for(IChromatogram<?> chromatogram : ContextCLI.getChromatograms()) {
				IChromatogramSelection<?, ?> selection;
				if(chromatogram instanceof IChromatogramMSD) {
					selection = new ChromatogramSelectionMSD((IChromatogramMSD)chromatogram) {

						@Override
						public void fireUpdateChange(boolean forceReload) {

							if(isProcessing.get()) {
								return;
							}
							super.fireUpdateChange(forceReload);
						}
					};
				} else if(chromatogram instanceof IChromatogramCSD) {
					selection = new ChromatogramSelectionCSD((IChromatogramCSD)chromatogram) {

						@Override
						public void fireUpdateChange(boolean forceReload) {

							if(isProcessing.get()) {
								return;
							}
							super.fireUpdateChange(forceReload);
						}
					};
				} else if(chromatogram instanceof IChromatogramWSD) {
					selection = new ChromatogramSelectionWSD((IChromatogramWSD)chromatogram) {

						@Override
						public void fireUpdateChange(boolean forceReload) {

							if(isProcessing.get()) {
								return;
							}
							super.fireUpdateChange(forceReload);
						}
					};
				} else {
					LOG.error("Unknwon Chromatogram Type " + chromatogram);
					continue;
				}
				//
				LOG.info("... processing " + chromatogram.getName());
				ProcessingInfo<?> processorResult = new ProcessingInfo<>();
				ProcessEntryContainer.applyProcessEntries(method, new ProcessExecutionContext(MONITOR, processorResult, processSupplierContext), IChromatogramSelectionProcessSupplier.createConsumer(selection));
				for(IProcessingMessage msg : processorResult.getMessages()) {
					String message = "[" + msg.getDescription() + "] " + msg.getMessage();
					switch(msg.getMessageType()) {
						case ERROR:
							LOG.error(message, msg.getException());
							break;
						case WARN:
							LOG.warn(message);
							break;
						case INFO:
							LOG.info(message);
							break;
						default:
							LOG.debug(message);
							break;
					}
				}
			}
		}
		isProcessing.set(false);
	}
}
