/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Jan Holy - implementation
 * Alexander Kerner - Generics, Logging
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.identifier.peak;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.csd.identifier.settings.IPeakIdentifierSettingsCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.identifier.core.Identifier;
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

public class PeakIdentifierCSD {

	private static final Logger logger = Logger.getLogger(PeakIdentifierCSD.class);
	//
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.csd.identifier.peakIdentifier";
	private static final String NO_IDENTIFIER_AVAILABLE = "There is no suitable peak identifier available";

	/**
	 * Returns an {@link IConfigurationElement} instance or null if none is
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

	// --------------------------------------------private methods
	private static <T> IProcessingInfo<T> getNoIdentifierAvailableProcessingInfo() {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Peak Identifier", NO_IDENTIFIER_AVAILABLE);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}

	// --------------------------------------------private methods
	/**
	 * Returns a {@link IPeakIdentifierCSD} instance given by the identifierId or
	 * null, if none is available.
	 */
	private static <T> IPeakIdentifierCSD<T> getPeakIdentifier(final String identifierId) {

		IConfigurationElement element;
		element = getConfigurationElement(identifierId);
		IPeakIdentifierCSD<T> instance = null;
		if(element != null) {
			try {
				instance = (IPeakIdentifierCSD<T>)element.createExecutableExtension(Identifier.IDENTIFIER);
			} catch(CoreException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		return instance;
	}

	/**
	 * Returns an {@link IPeakIdentifierSupportCSD} instance.
	 *
	 * @return {@link IPeakIdentifierSupportCSD}
	 */
	public static IPeakIdentifierSupportCSD getPeakIdentifierSupport() {

		PeakIdentifierSupplierCSD supplier;
		PeakIdentifierSupportCSD identifierSupport = new PeakIdentifierSupportCSD();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new PeakIdentifierSupplierCSD();
			supplier.setId(element.getAttribute(Identifier.ID));
			supplier.setDescription(element.getAttribute(Identifier.DESCRIPTION));
			supplier.setIdentifierName(element.getAttribute(Identifier.IDENTIFIER_NAME));
			if(element.getAttribute(Identifier.IDENTIFIER_SETTINGS) != null) {
				try {
					IPeakIdentifierSettingsCSD instance = (IPeakIdentifierSettingsCSD)element.createExecutableExtension(Identifier.IDENTIFIER_SETTINGS);
					supplier.setIdentifierSettingsClass(instance.getClass());
				} catch(CoreException e) {
					logger.error(e.getLocalizedMessage(), e);
					// settings class is optional, set null instead
					supplier.setIdentifierSettingsClass(null);
				}
			}
			identifierSupport.add(supplier);
		}
		return identifierSupport;
	}

	/**
	 * Runs the given identifier
	 *
	 * @param peak
	 * @param identifierSettings
	 * @param identifierId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 * @throws NoIdentifierAvailableException
	 */
	public static <T> IProcessingInfo<T> identify(IChromatogramPeakCSD peak, IPeakIdentifierSettingsCSD identifierSettings, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo<T> processingInfo;
		IPeakIdentifierCSD<T> peakIdentifier = getPeakIdentifier(identifierId);
		if(peakIdentifier != null) {
			processingInfo = peakIdentifier.identify(peak, identifierSettings, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Runs the given identifier
	 *
	 * @param peak
	 * @param identifierId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 * @throws NoIdentifierAvailableException
	 */
	public static <T> IProcessingInfo<T> identify(IPeakCSD peak, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo<T> processingInfo;
		IPeakIdentifierCSD<T> peakIdentifier = getPeakIdentifier(identifierId);
		if(peakIdentifier != null) {
			processingInfo = peakIdentifier.identify(peak, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Runs the given identifier
	 *
	 * @param peaks
	 * @param identifierSettings
	 * @param identifierId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 * @throws NoIdentifierAvailableException
	 */
	public static <T> IProcessingInfo<T> identify(List<? extends IPeakCSD> peaks, IPeakIdentifierSettingsCSD identifierSettings, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo<T> processingInfo;
		IPeakIdentifierCSD<T> peakIdentifier = getPeakIdentifier(identifierId);
		if(peakIdentifier != null) {
			processingInfo = peakIdentifier.identify(peaks, identifierSettings, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Runs the given identifier
	 *
	 * @param peaks
	 * @param identifierId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 * @throws NoIdentifierAvailableException
	 */
	public static <T> IProcessingInfo<T> identify(List<? extends IPeakCSD> peaks, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo<T> processingInfo;
		IPeakIdentifierCSD<T> peakIdentifier = getPeakIdentifier(identifierId);
		if(peakIdentifier != null) {
			processingInfo = peakIdentifier.identify(peaks, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	public static <T> IProcessingInfo<T> identify(IChromatogramSelectionCSD chromatogramSelectionCSD, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo<T> processingInfo;
		IPeakIdentifierCSD<T> peakIdentifier = getPeakIdentifier(identifierId);
		if(peakIdentifier != null) {
			processingInfo = peakIdentifier.identify(chromatogramSelectionCSD, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	public static <T> IProcessingInfo<T> identify(IChromatogramSelectionCSD chromatogramSelectionCSD, IPeakIdentifierSettingsCSD peakIdentifierSettingsCSD, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo<T> processingInfo;
		IPeakIdentifierCSD<T> peakIdentifier = getPeakIdentifier(identifierId);
		if(peakIdentifier != null) {
			processingInfo = peakIdentifier.identify(chromatogramSelectionCSD, peakIdentifierSettingsCSD, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * This class should have only static methods.
	 */
	private PeakIdentifierCSD() {

	}
}
