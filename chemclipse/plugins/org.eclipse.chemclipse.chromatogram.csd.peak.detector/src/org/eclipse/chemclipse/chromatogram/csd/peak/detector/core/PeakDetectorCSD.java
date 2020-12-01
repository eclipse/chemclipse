/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.peak.detector.core;

import org.eclipse.chemclipse.chromatogram.csd.peak.detector.settings.IPeakDetectorSettingsCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
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

public class PeakDetectorCSD {

	private static final Logger logger = Logger.getLogger(PeakDetectorCSD.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.csd.peak.detector.peakDetectorSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String PEAK_DETECTOR_NAME = "peakDetectorName";
	private static final String PEAK_DETECTOR = "peakDetector";
	private static final String PEAK_DETECTOR_SETTINGS = "peakDetectorSettings";
	/*
	 * Processing Info
	 */
	private static final String NO_PEAK_DETECTOR_AVAILABLE = "There is no peak detector available.";

	/**
	 * This class offers only static methods.
	 */
	private PeakDetectorCSD() {

	}

	/**
	 * Tries to detect the peaks in the chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @param peakDetectorSettings
	 * @param peakDetectorId
	 * @param monitor
	 * @return IProcessingInfo
	 */
	public static IProcessingInfo<?> detect(IChromatogramSelectionCSD chromatogramSelection, IPeakDetectorSettingsCSD peakDetectorSettings, String peakDetectorId, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo;
		IPeakDetectorCSD peakDetector = getPeakDetector(peakDetectorId);
		if(peakDetector != null) {
			processingInfo = peakDetector.detect(chromatogramSelection, peakDetectorSettings, monitor);
		} else {
			processingInfo = getNoPeakDetectorAvailableProcessingInfo();
		}
		return processingInfo;
	}

	// TODO JUnit
	/**
	 * Tries to detect the peaks in the chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @param peakDetectorId
	 * @param monitor
	 * @return IProcessingInfo
	 */
	public static IProcessingInfo<?> detect(IChromatogramSelectionCSD chromatogramSelection, String peakDetectorId, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo;
		IPeakDetectorCSD peakDetector = getPeakDetector(peakDetectorId);
		if(peakDetector != null) {
			processingInfo = peakDetector.detect(chromatogramSelection, monitor);
		} else {
			processingInfo = getNoPeakDetectorAvailableProcessingInfo();
		}
		return processingInfo;
	}

	public static IPeakDetectorCSDSupport getPeakDetectorSupport() {

		PeakDetectorCSDSupplier supplier;
		PeakDetectorCSDSupport peakDetectorSupport = new PeakDetectorCSDSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			String id = element.getAttribute(ID);
			String description = element.getAttribute(DESCRIPTION);
			String peakDetectorName = element.getAttribute(PEAK_DETECTOR_NAME);
			supplier = new PeakDetectorCSDSupplier(id, description, peakDetectorName);
			if(element.getAttribute(PEAK_DETECTOR_SETTINGS) != null) {
				try {
					IPeakDetectorSettingsCSD instance = (IPeakDetectorSettingsCSD)element.createExecutableExtension(PEAK_DETECTOR_SETTINGS);
					supplier.setSettingsClass(instance.getClass());
				} catch(CoreException e) {
					logger.error(e.getLocalizedMessage(), e);
					// settings class is optional, set null instead
					supplier.setSettingsClass(null);
				}
			}
			peakDetectorSupport.add(supplier);
		}
		return peakDetectorSupport;
	}

	private static IPeakDetectorCSD getPeakDetector(final String peakDetectorId) {

		IConfigurationElement element;
		element = getConfigurationElement(peakDetectorId);
		IPeakDetectorCSD instance = null;
		if(element != null) {
			try {
				instance = (IPeakDetectorCSD)element.createExecutableExtension(PEAK_DETECTOR);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an IPeakDetector instance or null if none is available.
	 * 
	 * @param peakDetectorId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String peakDetectorId) {

		if("".equals(peakDetectorId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(ID).equals(peakDetectorId)) {
				return element;
			}
		}
		return null;
	}

	private static IProcessingInfo<?> getNoPeakDetectorAvailableProcessingInfo() {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Peak Detector FID", NO_PEAK_DETECTOR_AVAILABLE);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}
}
