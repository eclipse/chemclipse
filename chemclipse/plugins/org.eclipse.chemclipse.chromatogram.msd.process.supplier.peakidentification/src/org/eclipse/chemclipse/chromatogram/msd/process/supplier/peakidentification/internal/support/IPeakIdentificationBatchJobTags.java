/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.internal.support;

/**
 * @author Dr. Philip Wenig
 * 
 */
public interface IPeakIdentificationBatchJobTags {

	String UTF8 = "UTF-8";
	/*
	 * Start and Stop Element /
	 */
	String PEAK_IDENTIFICATION_BATCH_JOB = "PeakIdentificationBatchJob";
	/*
	 * Header
	 */
	String HEADER = "Header";
	String REPORT_FOLDER = "ReportFolder";
	String OVERRIDE_REPORT = "OverrideReport";
	/*
	 * Peak File Input Entries
	 */
	String PEAK_INPUT_ENTRIES = "InputEntries";
	String PEAK_INPUT_ENTRY = "InputEntry";
	/*
	 * Peak File Output Entries
	 */
	String PEAK_OUTPUT_ENTRIES = "OutputEntries";
	String PEAK_OUTPUT_ENTRY = "OutputEntry";
	String PEAK_CONVERTER_ID = "converterId";
	/*
	 * PEAK INTEGRATOR
	 */
	String PEAK_INTEGRATOR = "INTEGRATOR";
	String PEAK_INTEGRATOR_ATTRIBUTE = "integrator";
	/*
	 * PEAK IDENTIFIER
	 */
	String PEAK_IDENTIFIER = "IDENTIFIER";
	String PEAK_IDENTIFIER_ATTRIBUTE = "identifier";
	/*
	 * Peak Integration/Identification Entry
	 */
	String PEAK_INTEGRATION_ENTRY = "IntegrationEntry";
	String PEAK_IDENTIFICATION_ENTRY = "IdentificationEntry";
	String PROCESSOR_ID = "processorId";
	String PROCESS_REPORT = "processReport";
}
