/*******************************************************************************
 * Copyright (c) 2008, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.peak;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

import org.eclipse.chemclipse.chromatogram.msd.identifier.core.Identifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IPeakIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.PeakIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

/**
 * Use the methods of this class to identify the mass spectrum of a peak.<br/>
 * 
 * @author Dr. Philip Wenig
 */
public class PeakIdentifier {

	private static final Logger logger = Logger.getLogger(PeakIdentifier.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.msd.identifier.peakIdentifier";
	private static final String NO_IDENTIFIER_AVAILABLE = "There is no suitable peak identifier available";

	/**
	 * This class should have only static methods.
	 */
	private PeakIdentifier() {
	}

	/**
	 * Runs the given identifier
	 * 
	 * @param peak
	 * @param identifierSettings
	 * @param identifierId
	 * @param monitor
	 * @return {@link IPeakIdentifierProcessingInfo}
	 * @throws NoIdentifierAvailableException
	 */
	public static IPeakIdentifierProcessingInfo identify(IPeakMSD peak, IPeakIdentifierSettings identifierSettings, String identifierId, IProgressMonitor monitor) {

		IPeakIdentifierProcessingInfo processingInfo;
		IPeakIdentifier peakIdentifier = getPeakIdentifier(identifierId);
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
	 * @return {@link IPeakIdentifierProcessingInfo}
	 * @throws NoIdentifierAvailableException
	 */
	public static IPeakIdentifierProcessingInfo identify(IPeakMSD peak, String identifierId, IProgressMonitor monitor) {

		IPeakIdentifierProcessingInfo processingInfo;
		IPeakIdentifier peakIdentifier = getPeakIdentifier(identifierId);
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
	 * @return {@link IPeakIdentifierProcessingInfo}
	 * @throws NoIdentifierAvailableException
	 */
	public static IPeakIdentifierProcessingInfo identify(List<IPeakMSD> peaks, IPeakIdentifierSettings identifierSettings, String identifierId, IProgressMonitor monitor) {

		IPeakIdentifierProcessingInfo processingInfo;
		IPeakIdentifier peakIdentifier = getPeakIdentifier(identifierId);
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
	 * @return {@link IPeakIdentifierProcessingInfo}
	 * @throws NoIdentifierAvailableException
	 */
	public static IPeakIdentifierProcessingInfo identify(List<IPeakMSD> peaks, String identifierId, IProgressMonitor monitor) {

		IPeakIdentifierProcessingInfo processingInfo;
		IPeakIdentifier peakIdentifier = getPeakIdentifier(identifierId);
		if(peakIdentifier != null) {
			processingInfo = peakIdentifier.identify(peaks, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Returns an {@link IPeakIdentifierSupport} instance.
	 * 
	 * @return {@link IPeakIdentifierSupport}
	 */
	public static IPeakIdentifierSupport getPeakIdentifierSupport() {

		PeakIdentifierSupplier supplier;
		PeakIdentifierSupport identifierSupport = new PeakIdentifierSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new PeakIdentifierSupplier();
			supplier.setId(element.getAttribute(Identifier.ID));
			supplier.setDescription(element.getAttribute(Identifier.DESCRIPTION));
			supplier.setIdentifierName(element.getAttribute(Identifier.IDENTIFIER_NAME));
			identifierSupport.add(supplier);
		}
		return identifierSupport;
	}

	// --------------------------------------------private methods
	/**
	 * Returns a {@link IPeakIdentifier} instance given by the identifierId or
	 * null, if none is available.
	 */
	private static IPeakIdentifier getPeakIdentifier(final String identifierId) {

		IConfigurationElement element;
		element = getConfigurationElement(identifierId);
		IPeakIdentifier instance = null;
		if(element != null) {
			try {
				instance = (IPeakIdentifier)element.createExecutableExtension(Identifier.IDENTIFIER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

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
	private static IPeakIdentifierProcessingInfo getNoIdentifierAvailableProcessingInfo() {

		IPeakIdentifierProcessingInfo processingInfo = new PeakIdentifierProcessingInfo();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Peak Identifier", NO_IDENTIFIER_AVAILABLE);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}
}
