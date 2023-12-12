/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.chromatogram;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IChromatogramIdentifierSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.IChromatogramIdentificationResult;
import org.eclipse.chemclipse.model.identifier.core.Identifier;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
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

/**
 * Use the methods of this class to identify a chromatogram.
 * 
 * @author eselmeister
 */
public class ChromatogramIdentifier {

	private static final Logger logger = Logger.getLogger(ChromatogramIdentifier.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.msd.identifier.chromatogramIdentifier";
	private static final String NO_IDENTIFIER_AVAILABLE = "There is no suitable chromatogram identifier available";

	/**
	 * This class should only have static methods.
	 */
	private ChromatogramIdentifier() {

	}

	/**
	 * Runs the chromatogram identifier with the given id and the given
	 * settings.
	 * 
	 * @param chromatogramSelection
	 * @param identifierSettings
	 * @param identifierId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IChromatogramIdentificationResult> identify(IChromatogramSelectionMSD chromatogramSelection, IChromatogramIdentifierSettings identifierSettings, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramIdentificationResult> processingInfo;
		IChromatogramIdentifier chromatogramIdentifier = getChromatogramIdentifier(identifierId);
		if(chromatogramIdentifier != null) {
			processingInfo = chromatogramIdentifier.identify(chromatogramSelection, identifierSettings, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Runs the chromatogram identifier with the given id.
	 * 
	 * @param chromatogramSelection
	 * @param identifierId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IChromatogramIdentificationResult> identify(IChromatogramSelectionMSD chromatogramSelection, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramIdentificationResult> processingInfo;
		IChromatogramIdentifier chromatogramIdentifier = getChromatogramIdentifier(identifierId);
		if(chromatogramIdentifier != null) {
			processingInfo = chromatogramIdentifier.identify(chromatogramSelection, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Returns an {@link IChromatogramIdentifierSupport} instance.
	 * 
	 * @return {@link IChromatogramIdentifierSupport}
	 */
	public static IChromatogramIdentifierSupport getChromatogramIdentifierSupport() {

		ChromatogramIdentifierSupplier supplier;
		ChromatogramIdentifierSupport identifierSupport = new ChromatogramIdentifierSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new ChromatogramIdentifierSupplier();
			supplier.setId(element.getAttribute(Identifier.ID));
			supplier.setDescription(element.getAttribute(Identifier.DESCRIPTION));
			supplier.setIdentifierName(element.getAttribute(Identifier.IDENTIFIER_NAME));
			if(element.getAttribute(Identifier.IDENTIFIER_SETTINGS) != null) {
				try {
					IChromatogramIdentifierSettings instance = (IChromatogramIdentifierSettings)element.createExecutableExtension(Identifier.IDENTIFIER_SETTINGS);
					supplier.setIdentifierSettingsClass(instance.getClass());
				} catch(CoreException e) {
					logger.warn(e);
					// settings class is optional, set null instead
					supplier.setIdentifierSettingsClass(null);
				}
			}
			identifierSupport.add(supplier);
		}
		return identifierSupport;
	}

	// --------------------------------------------private methods
	/**
	 * Returns a {@link IChromatogramIdentifier} instance given by the
	 * identifierId or null, if none is available.
	 */
	private static IChromatogramIdentifier getChromatogramIdentifier(final String identifierId) {

		IConfigurationElement element;
		element = getConfigurationElement(identifierId);
		IChromatogramIdentifier instance = null;
		if(element != null) {
			try {
				instance = (IChromatogramIdentifier)element.createExecutableExtension(Identifier.IDENTIFIER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an {@link IChromatogramFilter} instance or null if none is
	 * available.
	 * 
	 * @param filterId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String filterId) {

		if("".equals(filterId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(Identifier.ID).equals(filterId)) {
				return element;
			}
		}
		return null;
	}

	private static IProcessingInfo<IChromatogramIdentificationResult> getNoIdentifierAvailableProcessingInfo() {

		IProcessingInfo<IChromatogramIdentificationResult> processingInfo = new ProcessingInfo<>();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Chromatogram Identifier", NO_IDENTIFIER_AVAILABLE);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}
}
