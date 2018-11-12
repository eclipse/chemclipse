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
 * Jan Holy - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.peak;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.identifier.core.Identifier;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
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
 * Use the methods of this class to identify the mass spectrum of a peak.<br/>
 *
 * @author Dr. Philip Wenig
 */
public class PeakIdentifierMSD {

	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.msd.identifier.peakIdentifier";
	private static final Logger logger = Logger.getLogger(PeakIdentifierMSD.class);
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
	private static IProcessingInfo getNoIdentifierAvailableProcessingInfo() {

		IProcessingInfo processingInfo = new ProcessingInfo();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Peak Identifier", NO_IDENTIFIER_AVAILABLE);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}

	// --------------------------------------------private methods
	/**
	 * Returns a {@link IPeakIdentifierMSD} instance given by the identifierId or
	 * null, if none is available.
	 */
	private static IPeakIdentifierMSD getPeakIdentifier(final String identifierId) {

		IConfigurationElement element;
		element = getConfigurationElement(identifierId);
		IPeakIdentifierMSD instance = null;
		if(element != null) {
			try {
				instance = (IPeakIdentifierMSD)element.createExecutableExtension(Identifier.IDENTIFIER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an {@link IPeakIdentifierSupportMSD} instance.
	 *
	 * @return {@link IPeakIdentifierSupportMSD}
	 */
	public static IPeakIdentifierSupportMSD getPeakIdentifierSupport() {

		PeakIdentifierSupplierMSD supplier;
		PeakIdentifierSupportMSD identifierSupport = new PeakIdentifierSupportMSD();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new PeakIdentifierSupplierMSD();
			supplier.setId(element.getAttribute(Identifier.ID));
			supplier.setDescription(element.getAttribute(Identifier.DESCRIPTION));
			supplier.setIdentifierName(element.getAttribute(Identifier.IDENTIFIER_NAME));
			if(element.getAttribute(Identifier.IDENTIFIER_SETTINGS) != null) {
				try {
					IPeakIdentifierSettingsMSD instance = (IPeakIdentifierSettingsMSD)element.createExecutableExtension(Identifier.IDENTIFIER_SETTINGS);
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
	public static IProcessingInfo identify(IPeakMSD peak, IPeakIdentifierSettingsMSD identifierSettings, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IPeakIdentifierMSD peakIdentifier = getPeakIdentifier(identifierId);
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
	public static IProcessingInfo identify(IPeakMSD peak, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IPeakIdentifierMSD peakIdentifier = getPeakIdentifier(identifierId);
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
	public static IProcessingInfo identify(List<IPeakMSD> peaks, IPeakIdentifierSettingsMSD identifierSettings, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IPeakIdentifierMSD peakIdentifier = getPeakIdentifier(identifierId);
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
	public static IProcessingInfo identify(List<IPeakMSD> peaks, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IPeakIdentifierMSD peakIdentifier = getPeakIdentifier(identifierId);
		if(peakIdentifier != null) {
			processingInfo = peakIdentifier.identify(peaks, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	public static IProcessingInfo identify(IChromatogramSelectionMSD chromatogramSelectionMSD, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IPeakIdentifierMSD peakIdentifier = getPeakIdentifier(identifierId);
		if(peakIdentifier != null) {
			processingInfo = peakIdentifier.identify(chromatogramSelectionMSD, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	public static IProcessingInfo identify(IChromatogramSelectionMSD chromatogramSelectionMSD, IPeakIdentifierSettingsMSD peakIdentifierSettingsMSD, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IPeakIdentifierMSD peakIdentifier = getPeakIdentifier(identifierId);
		if(peakIdentifier != null) {
			processingInfo = peakIdentifier.identify(chromatogramSelectionMSD, peakIdentifierSettingsMSD, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * This class should have only static methods.
	 */
	private PeakIdentifierMSD() {
	}
}
