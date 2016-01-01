/*******************************************************************************
 * Copyright (c) 2010, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.internal.support;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public interface IBatchProcessJobTags {

	String UTF8 = "UTF-8";
	/*
	 * Start and Stop Element /
	 */
	String BATCH_PROCESS_JOB = "BatchProcessJob";
	/*
	 * Header
	 */
	String HEADER = "Header";
	String REPORT_FOLDER = "ReportFolder";
	String OVERRIDE_REPORT = "OverrideReport";
	/*
	 * Chromatogram Input Entries
	 */
	String CHROMATOGRAM_INPUT_ENTRIES = "InputEntries";
	String CHROMATOGRAM_INPUT_ENTRY = "InputEntry";
	/*
	 * Chromatogram Process Entries
	 */
	String CHROMATOGRAM_PROCESS_ENTRIES = "ProcessEntries";
	String CHROMATOGRAM_PROCESS_ENTRY = "ProcessEntry";
	String PROCESSOR_TYPE = "processorType";
	String PROCESSOR_ID = "processorId";
	/*
	 * Chromatogram Output Entries
	 */
	String CHROMATOGRAM_OUTPUT_ENTRIES = "OutputEntries";
	String CHROMATOGRAM_OUTPUT_ENTRY = "OutputEntry";
	String CHROMATOGRAM_CONVERTER_ID = "converterId";
	/*
	 * Chromatogram Report Entries
	 */
	String CHROMATOGRAM_REPORT_ENTRIES = "ReportEntries";
	String CHROMATOGRAM_REPORT_ENTRY = "ReportEntry";
	String CHROMATOGRAM_REPORT_SUPPLIER_ID = "reportSupplierId";
}
