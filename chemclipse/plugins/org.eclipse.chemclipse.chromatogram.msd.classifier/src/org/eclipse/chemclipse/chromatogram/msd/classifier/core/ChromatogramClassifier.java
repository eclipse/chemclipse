/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.core;

import org.eclipse.chemclipse.chromatogram.msd.classifier.result.IChromatogramClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.logging.core.Logger;
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

public class ChromatogramClassifier {

	private static final Logger logger = Logger.getLogger(ChromatogramClassifier.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.msd.classifier.chromatogramClassifierSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String CLASSIFIER_NAME = "classifierName";
	private static final String CLASSIFIER = "classifier";
	private static final String CLASSIFIER_SETTINGS = "classifierSettings";
	/*
	 * Processing Info
	 */
	private static final String NO_CHROMATOGRAM_CLASSIFIER_AVAILABLE = "There is no chromatogram classifier available.";

	/**
	 * This class is a singleton. Use only static methods.
	 */
	private ChromatogramClassifier() {
	}

	public static IProcessingInfo<IChromatogramClassifierResult> applyClassifier(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramClassifierSettings chromatogramClassifierSettings, String classifierId, IProgressMonitor monitor) {

		IChromatogramClassifier chromatogramClassifier = getChromatogramClassifier(classifierId);
		if(chromatogramClassifier != null) {
			return chromatogramClassifier.applyClassifier(chromatogramSelection, chromatogramClassifierSettings, monitor);
		} else {
			return getNoClassifierAvailableProcessingInfo();
		}
	}

	public static IProcessingInfo<IChromatogramClassifierResult> applyClassifier(IChromatogramSelection<?, ?> chromatogramSelection, String classifierId, IProgressMonitor monitor) {

		return applyClassifier(chromatogramSelection, null, classifierId, monitor);
	}

	public static IChromatogramClassifierSupport getChromatogramClassifierSupport() {

		ChromatogramClassifierSupplier supplier;
		ChromatogramClassifierSupport classifierSupport = new ChromatogramClassifierSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new ChromatogramClassifierSupplier();
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setClassifierName(element.getAttribute(CLASSIFIER_NAME));
			if(element.getAttribute(CLASSIFIER_SETTINGS) != null) {
				try {
					IChromatogramClassifierSettings instance = (IChromatogramClassifierSettings)element.createExecutableExtension(CLASSIFIER_SETTINGS);
					supplier.setSettingsClass(instance.getClass());
				} catch(CoreException e) {
					logger.warn(e);
					// settings class is optional, set null instead
					supplier.setSettingsClass(null);
				}
			}
			classifierSupport.add(supplier);
		}
		return classifierSupport;
	}

	// --------------------------------------------private methods
	/**
	 * Returns a {@link IChromatogramClassifier} instance given by the classifierId or
	 * null, if none is available.
	 */
	private static IChromatogramClassifier getChromatogramClassifier(final String classifierId) {

		IConfigurationElement element;
		element = getConfigurationElement(classifierId);
		IChromatogramClassifier instance = null;
		if(element != null) {
			try {
				instance = (IChromatogramClassifier)element.createExecutableExtension(CLASSIFIER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an {@link IChromatogramClassifier} instance or null if none is
	 * available.
	 * 
	 * @param classifierId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String classifierId) {

		if("".equals(classifierId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(ID).equals(classifierId)) {
				return element;
			}
		}
		return null;
	}

	// --------------------------------------------private methods
	private static IProcessingInfo<IChromatogramClassifierResult> getNoClassifierAvailableProcessingInfo() {

		IProcessingInfo<IChromatogramClassifierResult> processingInfo = new ProcessingInfo();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Chromatogram Classifier", NO_CHROMATOGRAM_CLASSIFIER_AVAILABLE);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}
}
