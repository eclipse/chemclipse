/*******************************************************************************
 * Copyright (c) 2010, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to simplified API, add generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.identifier.wavespectrum;

import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.wsd.identifier.settings.IWaveSpectrumIdentifierSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResult;
import org.eclipse.chemclipse.model.identifier.core.Identifier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.WaveSpectra;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

/**
 * Use the methods of this class to identify a wave spectrum.
 */
public class WaveSpectrumIdentifier {

	private static final Logger logger = Logger.getLogger(WaveSpectrumIdentifier.class);
	//
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.wsd.identifier.waveSpectrumIdentifier";
	private static final String NO_IDENTIFIER_AVAILABLE = "There is no suitable wave spectrum identifier available";

	/**
	 * This class should have only static methods.
	 */
	private WaveSpectrumIdentifier() {

	}

	/**
	 * Runs the given identifier and returns an {@link IWaveSpectrumIdentificationResult} instance.
	 * 
	 * @param waveSpectrum
	 * @param identifierSettings
	 * @param identifierId
	 * @param monitor
	 * @return {@link IPeakIdentificationResult}
	 * @throws NoIdentifierAvailableException
	 */
	public static IProcessingInfo<WaveSpectra> identify(IScanWSD waveSpectrum, IWaveSpectrumIdentifierSettings identifierSettings, String identifierId, IProgressMonitor monitor) {

		return identify(Collections.singletonList(waveSpectrum), identifierSettings, identifierId, monitor);
	}

	/**
	 * Runs the given identifier and returns an {@link IWaveSpectrumIdentificationResult} instance.
	 * 
	 * @param waveSpectrum
	 * @param identifierId
	 * @param monitor
	 * @return {@link IPeakIdentificationResult}
	 * @throws NoIdentifierAvailableException
	 */
	public static IProcessingInfo<WaveSpectra> identify(IScanWSD waveSpectrum, String identifierId, IProgressMonitor monitor) {

		return identify(Collections.singletonList(waveSpectrum), null, identifierId, monitor);
	}

	/**
	 * Runs the given identifier and returns an {@link IWaveSpectrumIdentificationResults} instance.
	 * 
	 * @param waveSpectra
	 * @param identifierSettings
	 * @param identifierId
	 * @param monitor
	 * @return {@link IWaveSpectrumIdentificationResults}
	 * @throws NoIdentifierAvailableException
	 */
	public static IProcessingInfo<WaveSpectra> identify(List<IScanWSD> waveSpectra, IWaveSpectrumIdentifierSettings identifierSettings, String identifierId, IProgressMonitor monitor) {

		IProcessingInfo<WaveSpectra> processingInfo;
		IWaveSpectrumIdentifier waveSpectrumIdentifier = getWaveSpectrumIdentifier(identifierId);
		if(waveSpectrumIdentifier != null) {
			processingInfo = waveSpectrumIdentifier.identify(waveSpectra, identifierSettings, monitor);
		} else {
			processingInfo = getNoIdentifierAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Runs the given identifier and returns an {@link IWaveSpectrumIdentificationResults} instance.
	 * 
	 * @param waveSpectra
	 * @param identifierId
	 * @param monitor
	 * @return {@link IWaveSpectrumIdentificationResults}
	 * @throws NoIdentifierAvailableException
	 */
	public static IProcessingInfo<WaveSpectra> identify(List<IScanWSD> waveSpectra, String identifierId, IProgressMonitor monitor) {

		return identify(waveSpectra, null, identifierId, monitor);
	}

	/**
	 * Returns an {@link IWaveSpectrumIdentifierSupport} instance.
	 * 
	 * @return {@link IWaveSpectrumIdentifierSupport}
	 */
	public static IWaveSpectrumIdentifierSupport getWaveSpectrumIdentifierSupport() {

		WaveSpectrumIdentifierSupplier supplier;
		WaveSpectrumIdentifierSupport identifierSupport = new WaveSpectrumIdentifierSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new WaveSpectrumIdentifierSupplier();
			supplier.setId(element.getAttribute(Identifier.ID));
			supplier.setDescription(element.getAttribute(Identifier.DESCRIPTION));
			supplier.setIdentifierName(element.getAttribute(Identifier.IDENTIFIER_NAME));
			if(element.getAttribute(Identifier.IDENTIFIER_SETTINGS) != null) {
				try {
					IWaveSpectrumIdentifierSettings instance = (IWaveSpectrumIdentifierSettings)element.createExecutableExtension(Identifier.IDENTIFIER_SETTINGS);
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
	 * Returns a {@link IWaveSpectrumIdentifier} instance given by the
	 * identifierId or null, if none is available.
	 */
	private static IWaveSpectrumIdentifier getWaveSpectrumIdentifier(final String identifierId) {

		IConfigurationElement element;
		element = getConfigurationElement(identifierId);
		IWaveSpectrumIdentifier instance = null;
		if(element != null) {
			try {
				instance = (IWaveSpectrumIdentifier)element.createExecutableExtension(Identifier.IDENTIFIER);
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
		//
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(Identifier.ID).equals(filterId)) {
				return element;
			}
		}
		//
		return null;
	}

	private static IProcessingInfo<WaveSpectra> getNoIdentifierAvailableProcessingInfo() {

		IProcessingInfo<WaveSpectra> processingInfo = new ProcessingInfo<>();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "WaveSpectrum Identifier", NO_IDENTIFIER_AVAILABLE);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}
}
