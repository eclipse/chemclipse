/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.internal.support;

/**
 * @author Dr. Philip Wenig
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
}
