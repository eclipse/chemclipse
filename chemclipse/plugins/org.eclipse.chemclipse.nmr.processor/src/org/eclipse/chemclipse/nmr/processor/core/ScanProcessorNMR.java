/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.processor.core;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.nmr.model.core.IScanNMR;
import org.eclipse.chemclipse.nmr.processor.settings.IProcessorSettings;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class ScanProcessorNMR {

	private static final Logger logger = Logger.getLogger(ScanProcessorNMR.class);
	//
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.nmr.processor.scanProcessor"; //$NON-NLS-1$
	private static final String ID = "id"; //$NON-NLS-1$
	private static final String DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String PROCESSOR_NAME = "processorName"; //$NON-NLS-1$
	private static final String PROCESSOR = "processor"; //$NON-NLS-1$

	private ScanProcessorNMR() {
	}

	public static IProcessingInfo process(IScanNMR scanNMR, IProcessorSettings processorSettings, String processorId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IScanProcessor scanProcessor = getScanProcessor(processorId);
		if(scanProcessor != null) {
			processingInfo = scanProcessor.process(scanNMR, processorSettings, monitor);
		} else {
			processingInfo = getProcessingError(processorId);
		}
		return processingInfo;
	}

	private static IScanProcessor getScanProcessor(final String processorId) {

		IConfigurationElement element;
		element = getConfigurationElement(processorId);
		IScanProcessor instance = null;
		if(element != null) {
			try {
				instance = (IScanProcessor)element.createExecutableExtension(PROCESSOR);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	private static IConfigurationElement getConfigurationElement(final String processorId) {

		if("".equals(processorId)) {
			return null;
		}
		//
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(ID).equals(processorId)) {
				return element;
			}
		}
		return null;
	}

	public static IScanProcessorSupport getScanProcessorSupport() {

		IScanProcessorSupport support = new ScanProcessorSupport();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			IScanProcessorSupplier supplier = new ScanProcessorSupplier();
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setProcessorName(element.getAttribute(PROCESSOR_NAME));
			support.add(supplier);
		}
		return support;
	}

	private static IProcessingInfo getProcessingError(String processorId) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addErrorMessage("Scan Processor", "No suitable processor was found for: " + processorId);
		return processingInfo;
	}
}
