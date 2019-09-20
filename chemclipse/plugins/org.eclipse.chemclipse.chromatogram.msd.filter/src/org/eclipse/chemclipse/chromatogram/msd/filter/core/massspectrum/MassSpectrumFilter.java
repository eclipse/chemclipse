/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum;

import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.filter.result.IMassSpectrumFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class MassSpectrumFilter {

	private static final Logger logger = Logger.getLogger(MassSpectrumFilter.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.msd.filter.massSpectrumFilterSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String FILTER_NAME = "filterName";
	private static final String FILTER = "filter";
	private static final String FILTER_CONFIG = "config";
	//
	private static final String PROCESSING_DESCRIPTION = "Mass Spectrum Filter";
	private static final String NO_MASS_SPECTRUM_FILTER_AVAILABLE = "There is no mass spectrum filter available.";

	/**
	 * This class is a singleton. Use only static methods.
	 */
	private MassSpectrumFilter() {
	}

	/**
	 * Applies the specified filter (filterID) with the given {@link IMassSpectrumFilterSettings} on the {@link IScanMSD} .<br/>
	 * The filter can be supported as a plugin through the extension point
	 * mechanism.
	 * 
	 * @param massSpectrum
	 * @param massSpectrumFilterSettings
	 * @param filterId
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IMassSpectrumFilterResult> applyFilter(IScanMSD massSpectrum, IMassSpectrumFilterSettings massSpectrumFilterSettings, String filterId, IProgressMonitor monitor) {

		return applyFilter(Collections.singletonList(massSpectrum), massSpectrumFilterSettings, filterId, monitor);
	}

	/**
	 * Applies the specified filter, but retrieves the IMassSpectrumFilterSettings dynamically.<br/>
	 * See also method: applyFilter(IMassSpectrum massSpectrum, IMassSpectrumFilterSettings massSpectrumFilterSettings, String filterId, IProgressMonitor monitor)
	 * 
	 * @param massSpectrum
	 * @param filterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IMassSpectrumFilterResult> applyFilter(IScanMSD massSpectrum, String filterId, IProgressMonitor monitor) {

		return applyFilter(Collections.singletonList(massSpectrum), null, filterId, monitor);
	}

	/**
	 * Applies the specified filter (filterID) with the given {@link IMassSpectrumFilterSettings} on the mass spectra list .<br/>
	 * The filter can be supported as a plugin through the extension point
	 * mechanism.
	 * 
	 * @param List
	 *            <IMassSpectrum> massSpectra
	 * @param massSpectraFilterSettings
	 * @param filterId
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IMassSpectrumFilterResult> applyFilter(List<IScanMSD> massSpectra, IMassSpectrumFilterSettings massSpectraFilterSettings, String filterId, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectrumFilterResult> processingInfo;
		IMassSpectrumFilter massSpectrumFilter = getMassSpectrumFilter(filterId);
		if(massSpectrumFilter != null) {
			processingInfo = massSpectrumFilter.applyFilter(massSpectra, massSpectraFilterSettings, monitor);
		} else {
			processingInfo = new ProcessingInfo<>();
			processingInfo.addErrorMessage(PROCESSING_DESCRIPTION, NO_MASS_SPECTRUM_FILTER_AVAILABLE);
		}
		return processingInfo;
	}

	/**
	 * Applies the specified filter, but retrieves the IMassSpectrumFilterSettings dynamically.<br/>
	 * See also method: applyFilter(List<IMassSpectrum> massSpectra, IMassSpectrumFilterSettings massSpectrumFilterSettings, String filterId, IProgressMonitor monitor)
	 * 
	 * @param List
	 *            <IMassSpectrum> massSpectra
	 * @param filterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IMassSpectrumFilterResult> applyFilter(List<IScanMSD> massSpectra, String filterId, IProgressMonitor monitor) {

		return applyFilter(massSpectra, null, filterId, monitor);
	}

	/**
	 * Returns the mass spectrum filter support instance.
	 * 
	 * @return {@link IMassSpectrumFilterSupport}
	 */
	public static IMassSpectrumFilterSupport getMassSpectrumFilterSupport() {

		MassSpectrumFilterSupplier supplier;
		MassSpectrumFilterSupport filterSupport = new MassSpectrumFilterSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new MassSpectrumFilterSupplier();
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setFilterName(element.getAttribute(FILTER_NAME));
			try {
				supplier.setConfigClass(element.createExecutableExtension(FILTER_CONFIG).getClass().asSubclass(IMassSpectrumFilterSettings.class));
			} catch(Exception e) {
				// can't use it then
			}
			filterSupport.add(supplier);
		}
		return filterSupport;
	}

	// --------------------------------------------private methods
	/**
	 * Returns a {@link IMassSpectrumFilter} instance given by the filterId or
	 * null, if none is available.
	 */
	private static IMassSpectrumFilter getMassSpectrumFilter(final String filterId) {

		IConfigurationElement element;
		element = getConfigurationElement(filterId);
		IMassSpectrumFilter instance = null;
		if(element != null) {
			try {
				instance = (IMassSpectrumFilter)element.createExecutableExtension(FILTER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an {@link IMassSpectrumFilter} instance or null if none is
	 * available.
	 * 
	 * @param filterId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String filterId) {

		if("".equals(filterId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(ID).equals(filterId)) {
				return element;
			}
		}
		return null;
	}
	// --------------------------------------------private methods
}
