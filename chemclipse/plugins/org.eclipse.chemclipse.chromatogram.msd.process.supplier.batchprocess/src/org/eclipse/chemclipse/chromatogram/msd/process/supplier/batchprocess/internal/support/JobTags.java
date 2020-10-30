/*******************************************************************************
 * Copyright (c) 2010, 2020 Lablicate GmbH.
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

public interface JobTags {

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
	 * Data Type
	 */
	String DATA_TYPE_ENTRIES = "DataTypes";
	String DATA_TYPE_ENTRY = "DataType";
	/*
	 * Chromatogram Input Entries
	 */
	String CHROMATOGRAM_INPUT_ENTRIES = "InputEntries";
	String CHROMATOGRAM_INPUT_ENTRY = "InputEntry";
	/*
	 * Process Method
	 */
	String CHROMATOGRAM_PROCESS_ENTRIES = "ProcessEntries";
	String CHROMATOGRAM_PROCESS_ENTRY = "ProcessEntry";
	//
	String PROCESSOR_ID = "id";
	String PROCESSOR_NAME = "name";
	String PROCESSOR_DESCRIPTION = "description";
	String PROCESSOR_JSON_SETTINGS = "jsonSettings";
	String PROCESSOR_SYMBOLIC_NAME = "symbolicName";
	String PROCESSOR_CLASS_NAME = "className";
	String PROCESSOR_DATA_TYPES = "dataTypes";
	//
	String DELIMITER_DATA_TYPE = ",";
}
