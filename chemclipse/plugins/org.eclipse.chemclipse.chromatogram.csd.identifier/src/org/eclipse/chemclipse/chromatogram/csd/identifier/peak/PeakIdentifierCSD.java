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
 * Jan Holy - implementation
 * Alexander Kerner - Generics, Logging
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.identifier.peak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.csd.identifier.l10n.Messages;
import org.eclipse.chemclipse.chromatogram.csd.identifier.settings.IPeakIdentifierSettingsCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.identifier.IIdentificationResults;
import org.eclipse.chemclipse.model.identifier.core.Identifier;
import org.eclipse.chemclipse.processing.core.ICategories;
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
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.csd.identifier.peakIdentifier"; //$NON-NLS-1$

	/**
	 * This class should have only static methods.
	 */
	private PeakIdentifierCSD() {

	}

	/**
	 * Returns an {@link IConfigurationElement} instance or null if none is
	 * available.
	 *
	 * @param filterId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String filterId) {

		if(filterId.isEmpty()) {
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

	private static <T> IProcessingInfo<T> getNoIdentifierAvailableProcessingInfo() {

		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, ICategories.PEAK_IDENTIFIER, Messages.noIdentifierAvailable);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}

	/**
	 * Returns a {@link IPeakIdentifierCSD} instance given by the identifierId or
	 * null, if none is available.
	 */
	@SuppressWarnings("unchecked")
	private static <T> IPeakIdentifierCSD<T> getPeakIdentifier(final String identifierId) {

		IConfigurationElement element;
		element = getConfigurationElement(identifierId);
		IPeakIdentifierCSD<T> instance = null;
		if(element != null) {
			try {
				instance = (IPeakIdentifierCSD<T>)element.createExecutableExtension(Identifier.IDENTIFIER);
			} catch(CoreException e) {
				logger.error(e);
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
					logger.error(e);
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
	public static <T extends IIdentificationResults> IProcessingInfo<T> identify(IChromatogramPeakCSD peak, IPeakIdentifierSettingsCSD identifierSettings, String identifierId, IProgressMonitor monitor) {

		List<IChromatogramPeakCSD> peaks = new ArrayList<>();
		peaks.add(peak);
		return identify(peaks, identifierSettings, identifierId, monitor);
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
	public static <T extends IIdentificationResults> IProcessingInfo<T> identify(IPeakCSD peak, String identifierId, IProgressMonitor monitor) {

		return identify(Collections.singletonList(peak), identifierId, monitor);
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
	public static <T extends IIdentificationResults> IProcessingInfo<T> identify(List<? extends IPeakCSD> peaks, IPeakIdentifierSettingsCSD identifierSettings, String identifierId, IProgressMonitor monitor) {

		IPeakIdentifierCSD<T> peakIdentifier = getPeakIdentifier(identifierId);
		if(peakIdentifier != null) {
			return peakIdentifier.identify(peaks, identifierSettings, monitor);
		} else {
			return getNoIdentifierAvailableProcessingInfo();
		}
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
	public static <T extends IIdentificationResults> IProcessingInfo<T> identify(List<? extends IPeakCSD> peaks, String identifierId, IProgressMonitor monitor) {

		return identify(peaks, null, identifierId, monitor);
	}

	public static <T extends IIdentificationResults> IProcessingInfo<T> identify(IChromatogramSelectionCSD chromatogramSelectionCSD, String identifierId, IProgressMonitor monitor) {

		return identify(chromatogramSelectionCSD, null, identifierId, monitor);
	}

	public static <T extends IIdentificationResults> IProcessingInfo<T> identify(IChromatogramSelectionCSD chromatogramSelectionCSD, IPeakIdentifierSettingsCSD peakIdentifierSettingsCSD, String identifierId, IProgressMonitor monitor) {

		return identify(chromatogramSelectionCSD.getChromatogram().getPeaks(chromatogramSelectionCSD), peakIdentifierSettingsCSD, identifierId, monitor);
	}
}
