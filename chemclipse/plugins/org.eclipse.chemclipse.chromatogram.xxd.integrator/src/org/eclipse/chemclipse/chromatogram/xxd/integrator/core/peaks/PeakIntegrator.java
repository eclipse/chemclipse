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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.processing.IPeakIntegratorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.processing.PeakIntegratorProcessingInfo;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

/**
 * This plugin offers an extension point to add several peak integrators.<br/>
 * Note that peak detectors and integrators are handled separately.<br/>
 * There exists several different integrators, so that each integrator can
 * implement its own integration method with its own settings.<br/>
 * You can think of:<br/>
 * FirstDerivative<br/>
 * Chromeleon<br/>
 * Empower<br/>
 * EZChrom<br/>
 * XCalibur<br/>
 * Not all of these are pure mass spectrometric evaluation tools, but may give a
 * hint how to solve some basic problems.
 * 
 * @author eselmeister
 */
public class PeakIntegrator {

	private static final Logger logger = Logger.getLogger(PeakIntegrator.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.xxd.integrator.peakIntegratorSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String INTEGRATOR_NAME = "integratorName";
	private static final String INTEGRATOR = "integrator";
	private static final String NO_INTEGRATOR_AVAILABLE = "There is no peak integrator available.";

	/**
	 * This class has only static methods.
	 */
	private PeakIntegrator() {

	}

	/**
	 * Integrates a single peak, sets the area.
	 * 
	 * @param peak
	 * @param peakIntegrationSettings
	 * @param integratorId
	 * @param monitor
	 * @return {@link IPeakIntegratorProcessingInfo}
	 */
	public static IPeakIntegratorProcessingInfo integrate(IPeak peak, IPeakIntegrationSettings peakIntegrationSettings, String integratorId, IProgressMonitor monitor) {

		IPeakIntegratorProcessingInfo processingInfo;
		IPeakIntegrator integrator = getPeakIntegrator(integratorId);
		if(integrator != null) {
			processingInfo = integrator.integrate(peak, peakIntegrationSettings, monitor);
		} else {
			processingInfo = getNoIntegratorAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Integrates a list of single peaks, sets the area.
	 * 
	 * @param peaks
	 * @param peakIntegrationSettings
	 * @param integratorId
	 * @param monitor
	 * @return {@link IPeakIntegratorProcessingInfo}
	 */
	public static IPeakIntegratorProcessingInfo integrate(List<? extends IPeak> peaks, IPeakIntegrationSettings peakIntegrationSettings, String integratorId, IProgressMonitor monitor) {

		IPeakIntegratorProcessingInfo processingInfo;
		IPeakIntegrator integrator = getPeakIntegrator(integratorId);
		if(integrator != null) {
			processingInfo = integrator.integrate(peaks, peakIntegrationSettings, monitor);
		} else {
			processingInfo = getNoIntegratorAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Integrates a list of single peaks, sets the area.
	 * 
	 * @param peaks
	 * @param integratorId
	 * @param monitor
	 * @return {@link IPeakIntegratorProcessingInfo}
	 */
	public static IPeakIntegratorProcessingInfo integrate(List<? extends IPeak> peaks, String integratorId, IProgressMonitor monitor) {

		IPeakIntegratorProcessingInfo processingInfo;
		IPeakIntegrator integrator = getPeakIntegrator(integratorId);
		if(integrator != null) {
			processingInfo = integrator.integrate(peaks, monitor);
		} else {
			processingInfo = getNoIntegratorAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Integrates the peaks in the chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @param peakIntegrationSettings
	 * @param integratorId
	 * @param monitor
	 * @return {@link IPeakIntegratorProcessingInfo}
	 */
	public static IPeakIntegratorProcessingInfo integrate(IChromatogramSelection chromatogramSelection, IPeakIntegrationSettings peakIntegrationSettings, String integratorId, IProgressMonitor monitor) {

		IPeakIntegratorProcessingInfo processingInfo;
		IPeakIntegrator integrator = getPeakIntegrator(integratorId);
		if(integrator != null) {
			processingInfo = integrator.integrate(chromatogramSelection, peakIntegrationSettings, monitor);
		} else {
			processingInfo = getNoIntegratorAvailableProcessingInfo();
		}
		return processingInfo;
	}

	/**
	 * Integrates the peaks in the chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @param integratorId
	 * @param monitor
	 * @return {@link IPeakIntegratorProcessingInfo}
	 */
	public static IPeakIntegratorProcessingInfo integrate(IChromatogramSelection chromatogramSelection, String integratorId, IProgressMonitor monitor) {

		IPeakIntegratorProcessingInfo processingInfo;
		IPeakIntegrator integrator = getPeakIntegrator(integratorId);
		if(integrator != null) {
			processingInfo = integrator.integrate(chromatogramSelection, monitor);
		} else {
			processingInfo = getNoIntegratorAvailableProcessingInfo();
		}
		return processingInfo;
	}

	// ---------------------------------------------------
	public static IPeakIntegratorSupport getPeakIntegratorSupport() {

		PeakIntegratorSupplier supplier;
		PeakIntegratorSupport integratorSupport = new PeakIntegratorSupport();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new PeakIntegratorSupplier();
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setIntegratorName(element.getAttribute(INTEGRATOR_NAME));
			integratorSupport.add(supplier);
		}
		return integratorSupport;
	}

	// --------------------------------------------private methods
	private static IPeakIntegrator getPeakIntegrator(final String integratorId) {

		IConfigurationElement element;
		element = getConfigurationElement(integratorId);
		IPeakIntegrator instance = null;
		if(element != null) {
			try {
				instance = (IPeakIntegrator)element.createExecutableExtension(INTEGRATOR);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an IIntegrator instance or null if none is available.
	 * 
	 * @param integratorId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String integratorId) {

		if("".equals(integratorId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(ID).equals(integratorId)) {
				return element;
			}
		}
		return null;
	}

	// --------------------------------------------private methods
	private static IPeakIntegratorProcessingInfo getNoIntegratorAvailableProcessingInfo() {

		IPeakIntegratorProcessingInfo processingInfo = new PeakIntegratorProcessingInfo();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.ERROR, "Peak Integrator", NO_INTEGRATOR_AVAILABLE);
		processingInfo.addMessage(processingMessage);
		return processingInfo;
	}
}
