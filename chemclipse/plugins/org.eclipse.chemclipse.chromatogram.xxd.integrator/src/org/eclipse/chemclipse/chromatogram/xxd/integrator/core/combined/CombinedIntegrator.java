/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.combined.ICombinedIntegrationSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class CombinedIntegrator {

	private static final Logger logger = Logger.getLogger(CombinedIntegrator.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.xxd.integrator.combinedIntegratorSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String INTEGRATOR_NAME = "integratorName";
	private static final String INTEGRATOR = "integrator";
	private static final String INTEGRATOR_SETTINGS = "integratorSettings";
	//
	private static final String NO_INTEGRATOR_AVAILABLE = "There is no combined integrator available.";

	/**
	 * This class has only static methods.
	 */
	private CombinedIntegrator() {

	}

	@SuppressWarnings("rawtypes")
	public static IProcessingInfo integrate(IChromatogramSelection chromatogramSelection, ICombinedIntegrationSettings combinedIntegrationSettings, String integratorId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		ICombinedIntegrator integrator = getCombinedIntegrator(integratorId);
		if(integrator != null) {
			processingInfo = integrator.integrate(chromatogramSelection, combinedIntegrationSettings, monitor);
		} else {
			processingInfo = getNoIntegratorAvailableProcessingInfo();
		}
		return processingInfo;
	}

	@SuppressWarnings("rawtypes")
	public static IProcessingInfo integrate(IChromatogramSelection chromatogramSelection, String integratorId, IProgressMonitor monitor) {

		return integrate(chromatogramSelection, getCombinedIntegrator(integratorId), monitor);
	}

	@SuppressWarnings("rawtypes")
	public static IProcessingInfo integrate(IChromatogramSelection chromatogramSelection, ICombinedIntegrator integrator, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		if(integrator != null) {
			processingInfo = integrator.integrate(chromatogramSelection, monitor);
		} else {
			processingInfo = getNoIntegratorAvailableProcessingInfo();
		}
		return processingInfo;
	}

	public static ICombinedIntegratorSupport getCombinedIntegratorSupport() {

		CombinedIntegratorSupplier supplier;
		CombinedIntegratorSupport integratorSupport = new CombinedIntegratorSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new CombinedIntegratorSupplier();
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setIntegratorName(element.getAttribute(INTEGRATOR_NAME));
			if(element.getAttribute(INTEGRATOR_SETTINGS) != null) {
				try {
					ICombinedIntegrationSettings instance = (ICombinedIntegrationSettings)element.createExecutableExtension(INTEGRATOR_SETTINGS);
					supplier.setSettingsClass(instance.getClass());
				} catch(CoreException e) {
					logger.warn(e);
					// settings class is optional, set null instead
					supplier.setSettingsClass(null);
				}
			}
			integratorSupport.add(supplier);
		}
		return integratorSupport;
	}

	private static ICombinedIntegrator getCombinedIntegrator(final String integratorId) {

		IConfigurationElement element;
		element = getConfigurationElement(integratorId);
		ICombinedIntegrator instance = null;
		if(element != null) {
			try {
				instance = (ICombinedIntegrator)element.createExecutableExtension(INTEGRATOR);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an IIntegrator instance or null if none is available.
	 * 
	 * @param integratorId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String integratorId) {

		if("".equals(integratorId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(ID).equals(integratorId)) {
				return element;
			}
		}
		return null;
	}

	private static IProcessingInfo<?> getNoIntegratorAvailableProcessingInfo() {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Combined Integrator", NO_INTEGRATOR_AVAILABLE);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}
}
