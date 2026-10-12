/*******************************************************************************
 * Copyright (c) 2010, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.process.supplier.batchprocess.internal;

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
	 * Input Entries
	 */
	String INPUT_ENTRIES = "InputEntries";
	String INPUT_ENTRY = "InputEntry";
	/*
	 * Process Method
	 */
	String CHROMATOGRAM_PROCESS_ENTRIES = "ProcessEntries";
	String CHROMATOGRAM_PROCESS_ENTRY = "ProcessEntry";

	String PROCESSOR_ID = "id";
	String PROCESSOR_NAME = "name";
	String PROCESSOR_DESCRIPTION = "description";
	String PROCESSOR_JSON_SETTINGS = "jsonSettings";
	String PROCESSOR_SYMBOLIC_NAME = "symbolicName";
	String PROCESSOR_CLASS_NAME = "className";
	String PROCESSOR_DATA_TYPES = "dataTypes";

	String DELIMITER_DATA_TYPE = ",";
}