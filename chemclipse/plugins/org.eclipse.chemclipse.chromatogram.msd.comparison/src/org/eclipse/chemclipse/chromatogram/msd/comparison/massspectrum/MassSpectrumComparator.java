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
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import org.eclipse.chemclipse.chromatogram.msd.comparison.exceptions.ComparisonException;
import org.eclipse.chemclipse.chromatogram.msd.comparison.exceptions.NoMassSpectrumComparatorAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.comparison.exceptions.NoMassSpectrumComparisonResultAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.purity.IMassSpectrumPurityResult;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.purity.MassSpectrumPurityResult;
import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.IMassSpectrumComparatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.IMassSpectrumPurityProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.MassSpectrumComparatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.MassSpectrumPurityProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.logging.core.Logger;

/**
 * This serves methods to compare IMassSpectrum instances.<br/>
 * <br/>
 * There exists several methods to compare mass spectra and to calculate the
 * similarity between them. The methods of this class can be used to do
 * calculations with several different types of comparators. They are known as
 * e.g.:<br/>
 * INCOS<br/>
 * PBM<br/>
 * SISCOM<br/>
 * STIRS<br/>
 * <br/>
 * But this class offers no database search algorithms. It only provides a
 * comparison between two mass spectra.<br/>
 * Database search will still use this feature but should be implemented in
 * another class to separate the two different concerns of comparison and
 * identification.
 * 
 * @author eselmeister
 */
public class MassSpectrumComparator {

	private static final Logger logger = Logger.getLogger(MassSpectrumComparator.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.msd.comparison.massSpectrumComparisonSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String COMPARATOR_NAME = "comparatorName";
	private static final String MASS_SPECTRUM_COMPARATOR = "massSpectrumComparator";

	/**
	 * This class has only static methods.
	 */
	private MassSpectrumComparator() {

	}

	/**
	 * Returns an array of all available comparator names and ids.
	 * The array may be empty.
	 * 
	 * @return String[][]
	 */
	public static String[][] getAvailableComparatorIds() {

		IMassSpectrumComparatorSupport massSpectrumComparatorSupport = getMassSpectrumComparatorSupport();
		String[][] comparatorArray = null;
		/*
		 * Get the entries.
		 */
		try {
			List<String> comparatorIds = massSpectrumComparatorSupport.getAvailableComparatorIds();
			int size = comparatorIds.size();
			comparatorArray = new String[size][2];
			for(int i = 0; i < size; i++) {
				/*
				 * Get the id and name.
				 */
				String converterId;
				String name;
				try {
					converterId = comparatorIds.get(i);
					name = massSpectrumComparatorSupport.getMassSpectrumComparisonSupplier(converterId).getComparatorName();
				} catch(Exception e) {
					logger.warn(e);
					converterId = "";
					name = "n.a.";
				}
				//
				comparatorArray[i][0] = name;
				comparatorArray[i][1] = converterId;
			}
		} catch(NoMassSpectrumComparatorAvailableException e1) {
			logger.warn(e1);
		}
		/*
		 * Create an empty array if no entry is available.
		 */
		if(comparatorArray == null) {
			comparatorArray = new String[0][0];
		}
		//
		return comparatorArray;
	}

	/**
	 * This class returns a mass spectrum comparison result object.<br/>
	 * Via an extension point several methods can register themselves to support
	 * a comparison.<br/>
	 * You can chose the comparator through the comparatorId.<br/>
	 * The unknown and reference mass spectrum will be left as they are.
	 * 
	 * @param unknown
	 * @param reference
	 * @param converterId
	 * @param ionRange
	 * @return {@link IMassSpectrumComparisonResult}
	 * @throws NoMassSpectrumComparisonResultAvailableException
	 */
	public static IMassSpectrumComparatorProcessingInfo compare(IScanMSD unknown, IScanMSD reference, String comparatorId) {

		IMassSpectrumComparatorProcessingInfo processingInfo;
		IMassSpectrumComparator massSpectrumComparator = getMassSpectrumComparator(comparatorId);
		if(massSpectrumComparator != null) {
			processingInfo = massSpectrumComparator.compare(unknown, reference);
		} else {
			processingInfo = getNoComparatorAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Calculates the purity of the extracted mass spectrum in comparison to the genuine.
	 * 
	 * @param extractedMassSpectrum
	 * @param genuineMassSpectrum
	 * @param ionRange
	 * @return {@link IMassSpectrumPurityProcessingInfo}
	 */
	public static IMassSpectrumPurityProcessingInfo getPurityResult(IScanMSD extractedMassSpectrum, IScanMSD genuineMassSpectrum) {

		IMassSpectrumPurityProcessingInfo processingInfo = new MassSpectrumPurityProcessingInfo();
		try {
			IMassSpectrumPurityResult massSpectrumPurityResult = new MassSpectrumPurityResult(extractedMassSpectrum, genuineMassSpectrum);
			processingInfo.setMassSpectrumPurityResult(massSpectrumPurityResult);
		} catch(ComparisonException e) {
			logger.warn(e);
			processingInfo.addErrorMessage("MassSpectrum Purity", "The mass spectrum purity couldn't be calculated.");
		}
		return processingInfo;
	}

	public static IMassSpectrumComparatorSupport getMassSpectrumComparatorSupport() {

		MassSpectrumComparisonSupplier supplier;
		MassSpectrumComparatorSupport massSpectrumComparisonSupport = new MassSpectrumComparatorSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new MassSpectrumComparisonSupplier();
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setComparatorName(element.getAttribute(COMPARATOR_NAME));
			massSpectrumComparisonSupport.add(supplier);
		}
		return massSpectrumComparisonSupport;
	}

	// --------------------------------------------private methods
	private static IMassSpectrumComparator getMassSpectrumComparator(final String comparatorId) {

		IConfigurationElement element;
		element = getConfigurationElement(comparatorId);
		IMassSpectrumComparator instance = null;
		if(element != null) {
			try {
				instance = (IMassSpectrumComparator)element.createExecutableExtension(MASS_SPECTRUM_COMPARATOR);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an IMassSpectrumComparator instance or null if none is available.
	 * 
	 * @param converterId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String converterId) {

		if("".equals(converterId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(ID).equals(converterId)) {
				return element;
			}
		}
		return null;
	}

	// --------------------------------------------private methods
	private static IMassSpectrumComparatorProcessingInfo getNoComparatorAvailableProcessingInfo() {

		IMassSpectrumComparatorProcessingInfo processingInfo = new MassSpectrumComparatorProcessingInfo();
		processingInfo.addErrorMessage("MassSpectrum Comparator", "There is no suitable mass spectrum comparator available.");
		return processingInfo;
	}
}
