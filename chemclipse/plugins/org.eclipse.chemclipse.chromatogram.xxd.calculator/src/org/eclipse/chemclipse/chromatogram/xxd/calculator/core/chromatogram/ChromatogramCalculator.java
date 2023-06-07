/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.settings.IChromatogramCalculatorSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class ChromatogramCalculator {

	private static final Logger logger = Logger.getLogger(ChromatogramCalculator.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.xxd.calculator.chromatogramCalculatorSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String CALCULATOR_NAME = "calculatorName";
	private static final String CALCULATOR = "calculator";
	private static final String CALCULATOR_SETTINGS = "calculatorSettings";
	//
	private static final String PROCESSING_DESCRIPTION = "Chromatogram Calculator";
	private static final String NO_CHROMATOGRAM_CALCULATOR_AVAILABLE = "There is no chromatogram calculator available.";

	/**
	 * This class is a singleton. Use only static methods.
	 */
	private ChromatogramCalculator() {

	}

	/**
	 * Applies the specified filter (filterID) with the given {@link IChromatogramCalculatorSettings} on the {@link IChromatogramSelectionMSD} .<br/>
	 * The filter can be supported as a plugin through the extension point
	 * mechanism.<br/>
	 * You could think of filters that for example remove background
	 * automatically or mean normalize the chromatogram.
	 * 
	 * @param chromatogramSelection
	 * @param chromatogramCalculatorSettings
	 * @param filterId
	 * @return {@link IChromatogramCalculatorProcessingInfo}
	 */
	public static IProcessingInfo<?> applyCalculator(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramCalculatorSettings chromatogramCalculatorSettings, String filterId, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo;
		IChromatogramCalculator chromatogramCalculator = getChromatogramCalculator(filterId);
		if(chromatogramCalculator != null) {
			processingInfo = chromatogramCalculator.applyCalculator(chromatogramSelection, chromatogramCalculatorSettings, monitor);
			chromatogramSelection.getChromatogram().setDirty(true);
		} else {
			processingInfo = new ProcessingInfo<>();
			processingInfo.addErrorMessage(PROCESSING_DESCRIPTION, NO_CHROMATOGRAM_CALCULATOR_AVAILABLE);
		}
		return processingInfo;
	}

	// TODO JUnit
	/**
	 * Applies the specified filter, but retrieves the IChromatogramFilterSettings dynamically.<br/>
	 * See also method: applyFilter(IChromatogramSelection<?, ?>chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, String filterId, IProgressMonitor monitor)
	 * 
	 * @param chromatogramSelection
	 * @param calculatorId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<?> applyCalculator(IChromatogramSelection<?, ?> chromatogramSelection, String calculatorId, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo;
		IChromatogramCalculator chromatogramCalculator = getChromatogramCalculator(calculatorId);
		if(chromatogramCalculator != null) {
			processingInfo = chromatogramCalculator.applyCalculator(chromatogramSelection, monitor);
		} else {
			processingInfo = new ProcessingInfo<>();
			processingInfo.addErrorMessage(PROCESSING_DESCRIPTION, NO_CHROMATOGRAM_CALCULATOR_AVAILABLE);
		}
		return processingInfo;
	}

	public static IChromatogramCalculatorSupport getChromatogramCalculatorSupport() {

		ChromatogramCalculatorSupplier supplier;
		ChromatogramCalculatorSupport calculatorSupport = new ChromatogramCalculatorSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new ChromatogramCalculatorSupplier();
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setCalculatorName(element.getAttribute(CALCULATOR_NAME));
			if(element.getAttribute(CALCULATOR_SETTINGS) != null) {
				try {
					IChromatogramCalculatorSettings instance = (IChromatogramCalculatorSettings)element.createExecutableExtension(CALCULATOR_SETTINGS);
					supplier.setSettingsClass(instance.getClass());
				} catch(CoreException e) {
					logger.warn(e);
					// settings class is optional, set null instead
					supplier.setSettingsClass(null);
				}
			}
			calculatorSupport.add(supplier);
		}
		return calculatorSupport;
	}

	/**
	 * Returns a {@link IChromatogramCalculator} instance given by the filterId or
	 * null, if none is available.
	 */
	private static IChromatogramCalculator getChromatogramCalculator(final String calculatorId) {

		IConfigurationElement element;
		element = getConfigurationElement(calculatorId);
		IChromatogramCalculator instance = null;
		if(element != null) {
			try {
				instance = (IChromatogramCalculator)element.createExecutableExtension(CALCULATOR);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an {@link IChromatogramCalculator} instance or null if none is
	 * available.
	 * 
	 * @param calculatorId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String calculatorId) {

		if("".equals(calculatorId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(ID).equals(calculatorId)) {
				return element;
			}
		}
		return null;
	}
}
