/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.IBaselineDetectorSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
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

/**
 * Several plugins can extend the baseline detector extension point.<br/>
 * There could be plugins, which detects automatically a baseline or those who
 * have an manual approach.<br/>
 * The baseline model is owned by {@link IChromatogram} so the model can be
 * manipulated by several baseline detector plugins.<br/>
 * What does it mean? An approach could be to detect the baseline automatically
 * and correct is manually afterwards.
 */
public class BaselineDetector {

	private static final Logger logger = Logger.getLogger(BaselineDetector.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.baselineDetectorSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String DETECTOR_NAME = "detectorName";
	private static final String BASELINE_DETECTOR = "baselineDetector";
	private static final String DETECTOR_SETTINGS = "detectorSettings";
	/*
	 * Processing Info
	 */
	private static final String NO_DETECTOR_AVAILABLE = "There is no baseline detector available.";

	/**
	 * This class has only static methods.
	 */
	private BaselineDetector() {

	}

	/**
	 * Use this method to call an extension point which sets a baseline to the
	 * given chromatogram.
	 * 
	 * @param chromatogram
	 * @param baselineDetectorSettings
	 * @param detectorId
	 * @param monitor
	 * @return IProcessingInfo
	 */
	@SuppressWarnings("rawtypes")
	public static IProcessingInfo setBaseline(IChromatogramSelection chromatogramSelection, IBaselineDetectorSettings baselineDetectorSettings, final String detectorId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IBaselineDetector detector = getBaselineDetector(detectorId);
		if(detector != null) {
			processingInfo = detector.setBaseline(chromatogramSelection, baselineDetectorSettings, monitor);
			chromatogramSelection.getChromatogram().setDirty(true);
		} else {
			processingInfo = getNoDetectorAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * See also other set baseline method. This method needs no settings.
	 * 
	 * @param chromatogramSelection
	 * @param detectorId
	 * @param monitor
	 * @return IProcessingInfo
	 */
	@SuppressWarnings("rawtypes")
	public static IProcessingInfo setBaseline(IChromatogramSelection chromatogramSelection, final String detectorId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IBaselineDetector detector = getBaselineDetector(detectorId);
		if(detector != null) {
			processingInfo = detector.setBaseline(chromatogramSelection, monitor);
		} else {
			processingInfo = getNoDetectorAvailableProcessingInfo();
		}
		return processingInfo;
	}

	public static IBaselineDetectorSupport getBaselineDetectorSupport() {

		BaselineDetectorSupplier supplier;
		BaselineDetectorSupport baselineDetectorSupport = new BaselineDetectorSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new BaselineDetectorSupplier();
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setDetectorName(element.getAttribute(DETECTOR_NAME));
			if(element.getAttribute(DETECTOR_SETTINGS) != null) {
				try {
					IBaselineDetectorSettings instance = (IBaselineDetectorSettings)element.createExecutableExtension(DETECTOR_SETTINGS);
					supplier.setSettingsClass(instance.getClass());
				} catch(CoreException e) {
					logger.warn(e);
					// settings class is optional, set null instead
					supplier.setSettingsClass(null);
				}
			}
			baselineDetectorSupport.add(supplier);
		}
		return baselineDetectorSupport;
	}

	private static IBaselineDetector getBaselineDetector(final String detectorId) {

		IConfigurationElement element;
		element = getConfigurationElement(detectorId);
		IBaselineDetector instance = null;
		if(element != null) {
			try {
				instance = (IBaselineDetector)element.createExecutableExtension(BASELINE_DETECTOR);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an IBaselineDetector instance or null if none is available.
	 * 
	 * @param detectorId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String detectorId) {

		if("".equals(detectorId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(ID).equals(detectorId)) {
				return element;
			}
		}
		return null;
	}

	private static IProcessingInfo<?> getNoDetectorAvailableProcessingInfo() {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<Object>();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Baseline Detector", NO_DETECTOR_AVAILABLE);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}
}
