/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.processing.IPeakDetectorMSDProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.processing.PeakDetectorMSDProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.settings.IPeakDetectorMSDSettings;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.IPeakDetectorSupport;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.PeakDetectorSupplier;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.PeakDetectorSupport;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

/**
 * This class offers several static methods to detect peaks in a chromatogram or
 * chromatogram selection.<br/>
 * There exists several ways to detect peaks in a chromatogram.<br/>
 * You could think of several methods such like:<br/>
 * FirstDerivative<br/>
 * AMDIS<br/>
 * CODA<br/>
 * RTE<br/>
 * Manual Detection<br/>
 * <br/>
 * Everyone can offer a peak detection method by using the extension point given
 * by this plugin.
 * 
 * @author eselmeister
 */
public class PeakDetectorMSD {

	private static final Logger logger = Logger.getLogger(PeakDetectorMSD.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.msd.peak.detector.peakDetectorSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String PEAK_DETECTOR_NAME = "peakDetectorName";
	private static final String PEAK_DETECTOR = "peakDetector";
	/*
	 * Processing Info
	 */
	private static final String NO_PEAK_DETECTOR_AVAILABLE = "There is no peak detector available.";

	/**
	 * This class offers only static methods.
	 */
	private PeakDetectorMSD() {

	}

	/**
	 * Tries to detect the peaks in the chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @param peakDetectorSettings
	 * @param peakDetectorId
	 * @param monitor
	 * @return IPeakDetectorProcessingInfo
	 */
	public static IPeakDetectorMSDProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, IPeakDetectorMSDSettings peakDetectorSettings, String peakDetectorId, IProgressMonitor monitor) {

		IPeakDetectorMSDProcessingInfo processingInfo;
		IPeakDetectorMSD peakDetector = getPeakDetector(peakDetectorId);
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
	 * @return IPeakDetectorProcessingInfo
	 */
	public static IPeakDetectorMSDProcessingInfo detect(IChromatogramSelectionMSD chromatogramSelection, String peakDetectorId, IProgressMonitor monitor) {

		IPeakDetectorMSDProcessingInfo processingInfo;
		IPeakDetectorMSD peakDetector = getPeakDetector(peakDetectorId);
		if(peakDetector != null) {
			processingInfo = peakDetector.detect(chromatogramSelection, monitor);
		} else {
			processingInfo = getNoPeakDetectorAvailableProcessingInfo();
		}
		return processingInfo;
	}

	// ---------------------------------------------------
	public static IPeakDetectorSupport getPeakDetectorSupport() {

		PeakDetectorSupplier supplier;
		PeakDetectorSupport peakDetectorSupport = new PeakDetectorSupport();
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
			supplier = new PeakDetectorSupplier(id, description, peakDetectorName);
			peakDetectorSupport.add(supplier);
		}
		return peakDetectorSupport;
	}

	private static IPeakDetectorMSD getPeakDetector(final String peakDetectorId) {

		IConfigurationElement element;
		element = getConfigurationElement(peakDetectorId);
		IPeakDetectorMSD instance = null;
		if(element != null) {
			try {
				instance = (IPeakDetectorMSD)element.createExecutableExtension(PEAK_DETECTOR);
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

	private static IPeakDetectorMSDProcessingInfo getNoPeakDetectorAvailableProcessingInfo() {

		IPeakDetectorMSDProcessingInfo processingInfo = new PeakDetectorMSDProcessingInfo();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Peak Detector MSD", NO_PEAK_DETECTOR_AVAILABLE);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}
}
