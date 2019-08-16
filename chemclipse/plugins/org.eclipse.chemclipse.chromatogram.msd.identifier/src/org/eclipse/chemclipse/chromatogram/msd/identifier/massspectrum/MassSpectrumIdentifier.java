/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResult;
import org.eclipse.chemclipse.model.identifier.core.Identifier;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
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
 * Use the methods of this class to identify a mass spectrum.<br/>
 * 
 * @author Dr. Philip Wenig
 */
public class MassSpectrumIdentifier {

	private static final Logger logger = Logger.getLogger(MassSpectrumIdentifier.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.msd.identifier.massSpectrumIdentifier";
	private static final String NO_IDENTIFIER_AVAILABLE = "There is no suitable mass spectrum identifier available";

	/**
	 * This class should have only static methods.
	 */
	private MassSpectrumIdentifier() {
	}

	/**
	 * Runs the given identifier and returns an {@link IMassSpectrumIdentificationResult} instance.
	 * 
	 * @param massSpectrum
	 * @param identifierSettings
	 * @param identifierId
	 * @param monitor
	 * @return {@link IPeakIdentificationResult}
	 * @throws NoIdentifierAvailableException
	 */
	public static IProcessingInfo identify(IScanMSD massSpectrum, IMassSpectrumIdentifierSettings identifierSettings, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IMassSpectrumIdentifier massSpectrumIdentifier = getMassSpectrumIdentifier(identifierId);
		if(massSpectrumIdentifier != null) {
			processingInfo = massSpectrumIdentifier.identify(massSpectrum, identifierSettings, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Runs the given identifier and returns an {@link IMassSpectrumIdentificationResult} instance.
	 * 
	 * @param massSpectrum
	 * @param identifierId
	 * @param monitor
	 * @return {@link IPeakIdentificationResult}
	 * @throws NoIdentifierAvailableException
	 */
	public static IProcessingInfo identify(IScanMSD massSpectrum, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IMassSpectrumIdentifier massSpectrumIdentifier = getMassSpectrumIdentifier(identifierId);
		if(massSpectrumIdentifier != null) {
			processingInfo = massSpectrumIdentifier.identify(massSpectrum, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Runs the given identifier and returns an {@link IMassSpectrumIdentificationResults} instance.
	 * 
	 * @param massSpectra
	 * @param identifierSettings
	 * @param identifierId
	 * @param monitor
	 * @return {@link IMassSpectrumIdentificationResults}
	 * @throws NoIdentifierAvailableException
	 */
	public static IProcessingInfo identify(List<IScanMSD> massSpectra, IMassSpectrumIdentifierSettings identifierSettings, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IMassSpectrumIdentifier massSpectrumIdentifier = getMassSpectrumIdentifier(identifierId);
		if(massSpectrumIdentifier != null) {
			processingInfo = massSpectrumIdentifier.identify(massSpectra, identifierSettings, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Runs the given identifier and returns an {@link IMassSpectrumIdentificationResults} instance.
	 * 
	 * @param massSpectra
	 * @param identifierId
	 * @param monitor
	 * @return {@link IMassSpectrumIdentificationResults}
	 * @throws NoIdentifierAvailableException
	 */
	public static IProcessingInfo identify(List<IScanMSD> massSpectra, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IMassSpectrumIdentifier massSpectrumIdentifier = getMassSpectrumIdentifier(identifierId);
		if(massSpectrumIdentifier != null) {
			processingInfo = massSpectrumIdentifier.identify(massSpectra, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Returns an {@link IMassSpectrumIdentifierSupport} instance.
	 * 
	 * @return {@link IMassSpectrumIdentifierSupport}
	 */
	public static IMassSpectrumIdentifierSupport getMassSpectrumIdentifierSupport() {

		MassSpectrumIdentifierSupplier supplier;
		MassSpectrumIdentifierSupport identifierSupport = new MassSpectrumIdentifierSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new MassSpectrumIdentifierSupplier();
			supplier.setId(element.getAttribute(Identifier.ID));
			supplier.setDescription(element.getAttribute(Identifier.DESCRIPTION));
			supplier.setIdentifierName(element.getAttribute(Identifier.IDENTIFIER_NAME));
			identifierSupport.add(supplier);
		}
		return identifierSupport;
	}

	// --------------------------------------------private methods
	/**
	 * Returns a {@link IMassSpectrumIdentifier} instance given by the
	 * identifierId or null, if none is available.
	 */
	private static IMassSpectrumIdentifier getMassSpectrumIdentifier(final String identifierId) {

		IConfigurationElement element;
		element = getConfigurationElement(identifierId);
		IMassSpectrumIdentifier instance = null;
		if(element != null) {
			try {
				instance = (IMassSpectrumIdentifier)element.createExecutableExtension(Identifier.IDENTIFIER);
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
	private static IProcessingInfo getNoIdentifierAvailableProcessingInfo() {

		IProcessingInfo processingInfo = new ProcessingInfo();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "MassSpectrum Identifier", NO_IDENTIFIER_AVAILABLE);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}
}
