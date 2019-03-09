/*******************************************************************************
 * Copyright (c) 2008, 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.comparison.comparator.DefaultMassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.exceptions.ComparisonException;
import org.eclipse.chemclipse.chromatogram.msd.comparison.exceptions.NoMassSpectrumComparatorAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.comparison.internal.massspectrum.ComparatorCache;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.purity.IMassSpectrumPurityResult;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.purity.MassSpectrumPurityResult;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

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
	private static final String SUPPORTS_NOMINAL_MS = "nominalMS";
	private static final String SUPPORTS_TANDEM_MS = "tandemMS";
	private static final String SUPPORTS_HIGH_RESOLUTION_MS = "highResolutionMS";
	//
	private static ComparatorCache comparatorCache;
	private static IProcessingInfo<IComparisonResult> processingInfoComparisonSkip;
	private static final float NO_MATCH = 0.0f;
	/*
	 * Initialize all values.
	 */
	static {
		comparatorCache = new ComparatorCache();
		processingInfoComparisonSkip = new ProcessingInfo<>();
		processingInfoComparisonSkip.setProcessingResult(new ComparisonResult(NO_MATCH, NO_MATCH, NO_MATCH, NO_MATCH));
	}

	/**
	 * This class has only static methods.
	 */
	private MassSpectrumComparator() {

	}

	/**
	 * Returns a mass spectrum comparison result object.<br/>
	 * Via an extension point several methods can register themselves to support
	 * a comparison.<br/>
	 * You can chose the comparator through the comparatorId.<br/>
	 * The unknown and reference mass spectrum will be left as they are.
	 *
	 */
	public static IProcessingInfo<IComparisonResult> compare(IScanMSD unknown, IScanMSD reference, String comparatorId, boolean usePreOptimization, double thresholdPreOptimization) {

		return compare(unknown, reference, getMassSpectrumComparator(comparatorId), usePreOptimization, thresholdPreOptimization);
	}

	public static IProcessingInfo<IComparisonResult> compare(IScanMSD unknown, IScanMSD reference, IMassSpectrumComparator massSpectrumComparator, boolean usePreOptimization, double thresholdPreOptimization) {

		/*
		 * Check if the mass spectrum needs to be compared.
		 */
		boolean compare = true;
		if(usePreOptimization) {
			compare = comparatorCache.useReferenceForComparison(unknown, reference, thresholdPreOptimization);
		}
		/*
		 * Do the comparison
		 */
		IProcessingInfo<IComparisonResult> processingInfo;
		if(compare) {
			if(massSpectrumComparator != null) {
				processingInfo = massSpectrumComparator.compare(unknown, reference);
			} else {
				massSpectrumComparator = new DefaultMassSpectrumComparator();
				processingInfo = massSpectrumComparator.compare(unknown, reference);
				processingInfo.addInfoMessage("MassSpectrum Comparator", "The requested comparator was not available. Instead the default comparator has been used.");
			}
		} else {
			processingInfo = processingInfoComparisonSkip;
		}
		//
		return processingInfo;
	}

	/**
	 * Calculates the purity of the extracted mass spectrum in comparison to the genuine.
	 *
	 * @param extractedMassSpectrum
	 * @param genuineMassSpectrum
	 * @param ionRange
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IMassSpectrumPurityResult> getPurityResult(IScanMSD extractedMassSpectrum, IScanMSD genuineMassSpectrum) {

		IProcessingInfo<IMassSpectrumPurityResult> processingInfo = new ProcessingInfo<>();
		try {
			IMassSpectrumPurityResult massSpectrumPurityResult = new MassSpectrumPurityResult(extractedMassSpectrum, genuineMassSpectrum);
			processingInfo.setProcessingResult(massSpectrumPurityResult);
		} catch(ComparisonException e) {
			logger.error(e.getLocalizedMessage(), e);
			processingInfo.addErrorMessage("MassSpectrum Purity", "The mass spectrum purity couldn't be calculated.");
		}
		return processingInfo;
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

	public static IMassSpectrumComparatorSupport getMassSpectrumComparatorSupport() {

		MassSpectrumComparatorSupport massSpectrumComparisonSupport = new MassSpectrumComparatorSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			IMassSpectrumComparisonSupplier massSpectrumComparisonSupplier = getMassSpectrumComparisonSupplier(element);
			massSpectrumComparisonSupport.add(massSpectrumComparisonSupplier);
		}
		return massSpectrumComparisonSupport;
	}

	public static IMassSpectrumComparator getMassSpectrumComparator(final String comparatorId) {

		IConfigurationElement element = getConfigurationElement(comparatorId);
		IMassSpectrumComparator instance = null;
		if(element != null) {
			try {
				instance = (IMassSpectrumComparator)element.createExecutableExtension(MASS_SPECTRUM_COMPARATOR);
				IMassSpectrumComparisonSupplier massSpectrumComparisonSupplier = getMassSpectrumComparisonSupplier(element);
				((AbstractMassSpectrumComparator)instance).setMassSpectrumComparisonSupplier(massSpectrumComparisonSupplier);
			} catch(CoreException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		return instance;
	}

	private static IMassSpectrumComparisonSupplier getMassSpectrumComparisonSupplier(IConfigurationElement element) {

		MassSpectrumComparisonSupplier massSpectrumComparisonSupplier = new MassSpectrumComparisonSupplier();
		massSpectrumComparisonSupplier.setId(element.getAttribute(ID));
		massSpectrumComparisonSupplier.setDescription(element.getAttribute(DESCRIPTION));
		massSpectrumComparisonSupplier.setComparatorName(element.getAttribute(COMPARATOR_NAME));
		massSpectrumComparisonSupplier.setSupportsNominalMS(Boolean.valueOf(element.getAttribute(SUPPORTS_NOMINAL_MS)));
		massSpectrumComparisonSupplier.setSupportsTandemMS(Boolean.valueOf(element.getAttribute(SUPPORTS_TANDEM_MS)));
		massSpectrumComparisonSupplier.setSupportsHighResolutionMS(Boolean.valueOf(element.getAttribute(SUPPORTS_HIGH_RESOLUTION_MS)));
		//
		return massSpectrumComparisonSupplier;
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
}
