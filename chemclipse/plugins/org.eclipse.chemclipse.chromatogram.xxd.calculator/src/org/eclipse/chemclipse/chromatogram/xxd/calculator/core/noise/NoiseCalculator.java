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
 * Christoph Läubrich - adjust to changed API, add null check
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.exceptions.NoNoiseCalculatorAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class NoiseCalculator {

	private static final Logger logger = Logger.getLogger(NoiseCalculator.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.xxd.calculator.noiseCalculationSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String CALCULATOR_NAME = "calculatorName";
	private static final String NOISE_CALCULATOR = "noiseCalculator";

	/**
	 * This class has only static methods.
	 */
	private NoiseCalculator() {

	}

	/**
	 * Returns an array of all available noise calculator names and ids.
	 * The array may be empty.
	 * 
	 * @return String[][]
	 */
	public static String[][] getAvailableCalculatorIds() {

		INoiseCalculatorSupport noiseCalculatorSupport = getNoiseCalculatorSupport();
		String[][] calculatorArray = null;
		/*
		 * Get the entries.
		 */
		try {
			List<String> calculatorIds = noiseCalculatorSupport.getAvailableCalculatorIds();
			int size = calculatorIds.size();
			calculatorArray = new String[size][2];
			for(int i = 0; i < size; i++) {
				/*
				 * Get the id and name.
				 */
				String calculatorId;
				String name;
				try {
					calculatorId = calculatorIds.get(i);
					name = noiseCalculatorSupport.getCalculatorSupplier(calculatorId).getCalculatorName();
				} catch(Exception e) {
					logger.warn(e);
					calculatorId = "";
					name = "n.a.";
				}
				//
				calculatorArray[i][0] = name;
				calculatorArray[i][1] = calculatorId;
			}
		} catch(NoNoiseCalculatorAvailableException e1) {
			logger.warn(e1);
		}
		/*
		 * Create an empty array if no entry is available.
		 */
		if(calculatorArray == null) {
			calculatorArray = new String[0][0];
		}
		//
		return calculatorArray;
	}

	public static INoiseCalculatorSupport getNoiseCalculatorSupport() {

		NoiseCalculatorSupplier supplier;
		NoiseCalculatorSupport noiseCalculatorSupport = new NoiseCalculatorSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new NoiseCalculatorSupplier();
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setDetectorName(element.getAttribute(CALCULATOR_NAME));
			noiseCalculatorSupport.add(supplier);
		}
		return noiseCalculatorSupport;
	}

	/**
	 * Return the noise calculator given by the calculator id.
	 * This method may return null.
	 * 
	 * @param calculatorId
	 * @return {@link INoiseCalculator}
	 */
	public static INoiseCalculator getNoiseCalculator(final String calculatorId) {

		IConfigurationElement element;
		element = getConfigurationElement(calculatorId);
		INoiseCalculator instance = null;
		if(element != null) {
			try {
				instance = (INoiseCalculator)element.createExecutableExtension(NOISE_CALCULATOR);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an INoiseCalculator instance or null if none is available.
	 * 
	 * @param detectorId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String detectorId) {

		if("".equals(detectorId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		if(registry != null) {
			IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
			for(IConfigurationElement element : elements) {
				if(element.getAttribute(ID).equals(detectorId)) {
					return element;
				}
			}
		}
		return null;
	}
}
