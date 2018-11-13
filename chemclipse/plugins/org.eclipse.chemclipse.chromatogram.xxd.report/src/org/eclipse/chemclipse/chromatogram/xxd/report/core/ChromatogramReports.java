/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.core;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.chromatogram.xxd.report.chromatogram.IChromatogramReportGenerator;
import org.eclipse.chemclipse.chromatogram.xxd.report.settings.IChromatogramReportSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class ChromatogramReports {

	private static final Logger logger = Logger.getLogger(ChromatogramReports.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.xxd.report.chromatogramReportSupplier";
	/*
	 * These are the attributes of the extension point elements.
	 */
	public static final String ID = "id";
	public static final String DESCRIPTION = "description";
	public static final String FILTER_NAME = "reportName";
	public static final String FILE_EXTENSION = "fileExtension";
	public static final String FILE_NAME = "fileName";
	public static final String REPORT_GENERATOR = "reportGenerator";
	public static final String REPORT_SETTINGS = "reportSettings";

	/**
	 * This class has only static methods.
	 */
	private ChromatogramReports() {
	}

	public static IProcessingInfo generate(File file, boolean append, IChromatogram<? extends IPeak> chromatogram, IChromatogramReportSettings chromatogramReportSettings, String reportSupplierId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IChromatogramReportGenerator reportGenerator = getChromatogramReportGenerator(reportSupplierId);
		if(reportGenerator != null) {
			processingInfo = reportGenerator.generate(file, append, chromatogram, chromatogramReportSettings, monitor);
		} else {
			processingInfo = getNoChromatogramReportAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	public static IProcessingInfo generate(File file, boolean append, IChromatogram<? extends IPeak> chromatogram, String reportSupplierId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IChromatogramReportGenerator reportGenerator = getChromatogramReportGenerator(reportSupplierId);
		if(reportGenerator != null) {
			processingInfo = reportGenerator.generate(file, append, chromatogram, monitor);
		} else {
			processingInfo = getNoChromatogramReportAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	public static IProcessingInfo generate(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, IChromatogramReportSettings chromatogramReportSettings, String reportSupplierId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IChromatogramReportGenerator reportGenerator = getChromatogramReportGenerator(reportSupplierId);
		if(reportGenerator != null) {
			processingInfo = reportGenerator.generate(file, append, chromatograms, chromatogramReportSettings, monitor);
		} else {
			processingInfo = getNoChromatogramReportAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	public static IProcessingInfo generate(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, String reportSupplierId, IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		IChromatogramReportGenerator reportGenerator = getChromatogramReportGenerator(reportSupplierId);
		if(reportGenerator != null) {
			processingInfo = reportGenerator.generate(file, append, chromatograms, monitor);
		} else {
			processingInfo = getNoChromatogramReportAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	/**
	 * This methods returns an {@link ChromatogramReportSupport} instance.<br/>
	 * The {@link ChromatogramReportSupport} instance stores descriptions
	 * about all valid and registered chromatogram report suppliers.
	 * 
	 * @return ChromatogramReportSupplierSupport
	 */
	public static IChromatogramReportSupport getChromatogramReportSupplierSupport() {

		ChromatogramReportSupplier supplier;
		ChromatogramReportSupport chromatogramReportSupport = new ChromatogramReportSupport();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			/*
			 * Set the values to the ChromatogramSupplier instance first before
			 * validating them.<br/> Why? Because the return value of
			 * element.getAttribute(...) could be null. If the element is null
			 * and stored in a ChromatogramSupplier instance it will be at least
			 * "".
			 */
			supplier = new ChromatogramReportSupplier();
			supplier.setFileExtension(element.getAttribute(FILE_EXTENSION));
			supplier.setFileName(element.getAttribute(FILE_NAME));
			/*
			 * Check if these values contain not allowed characters. If yes than
			 * do not add the supplier to the supported converter list.
			 */
			if(isValid(supplier.getFileExtension()) && isValid(supplier.getFileName())) {
				supplier.setId(element.getAttribute(ID));
				supplier.setDescription(element.getAttribute(DESCRIPTION));
				supplier.setFileExtension(element.getAttribute(FILE_EXTENSION));
				supplier.setFileName(element.getAttribute(FILE_NAME));
				supplier.setFilterName(element.getAttribute(FILTER_NAME));
				if(element.getAttribute(REPORT_SETTINGS) != null) {
					try {
						IChromatogramReportSettings instance = (IChromatogramReportSettings)element.createExecutableExtension(REPORT_SETTINGS);
						supplier.setSettingsClass(instance.getClass());
					} catch(CoreException e) {
						logger.warn(e);
						// settings class is optional, set null instead
						supplier.setSettingsClass(null);
					}
				}
				chromatogramReportSupport.add(supplier);
			}
		}
		return chromatogramReportSupport;
	}

	/**
	 * Returns an IChromatogramReportGenerator instance or null if none is
	 * available.
	 * 
	 * @param reportGeneratorId
	 * @return IChromatogramReportGenerator
	 */
	private static IChromatogramReportGenerator getChromatogramReportGenerator(final String reportGeneratorId) {

		IConfigurationElement element;
		element = getConfigurationElement(reportGeneratorId);
		IChromatogramReportGenerator instance = null;
		if(element != null) {
			try {
				instance = (IChromatogramReportGenerator)element.createExecutableExtension(REPORT_GENERATOR);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an IChromatogramReportGenerator instance or null if none is
	 * available.
	 * 
	 * @param converterId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String reportGeneratorId) {

		if("".equals(reportGeneratorId)) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(ID).equals(reportGeneratorId)) {
				return element;
			}
		}
		return null;
	}

	private static IProcessingInfo getNoChromatogramReportAvailableProcessingInfo(File file) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.addErrorMessage("Chromatogram Report Generator", "There is no suitable chromatogram report generator available for: " + file.getAbsolutePath());
		return processingInfo;
	}

	/**
	 * This method return true if the input string contains a not allowed
	 * character like \/:*?"<>| It returns true if the input string is a valid
	 * string and false if not.<br/>
	 * If the input string is null it returns false.
	 * 
	 * @return boolean
	 */
	public static boolean isValid(final String input) {

		/*
		 *
		 */
		if(input == null) {
			return false;
		}
		/*
		 * Use four times backslash to search after a normal backslash. See
		 * "Mastering Regular Expressions" from Jeffrey Friedl, ISBN:
		 * 0596528124.
		 */
		String regex = "[\\\\/:*?\"<>|]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		return !matcher.find();
	}
}
