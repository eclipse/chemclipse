/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.messages;

public interface ISupportMessages {

	/*
	 * The strings need to be listed and translated in:
	 * OSGI-INF/l10n/bundle.properties ...
	 */
	String TASK_PREPARE_PROJECT = "taskPrepareProject";
	String TASK_OPEN_EDITOR = "taskOpenEditor";
	//
	String PROCESSING_ERROR = "processingError";
	String PROCESSING_PROCESS_INTERRUPTED = "processingProcessInterrupted";
	String PROCESSING_SOMETHING_WRONG = "processingSomethingWrong";
	String PROCESSING_SELECT_PROJECT = "processingSelectProject";
	String PROCESSING_PROJECT_NOT_EXISTS = "processingProjectNotExists";
	String PROCESSING_SELECT_VALID_FOLDER = "processingSelectValidFolder";
	String PROCESSING_SELECT_VALID_PROJECT = "processingSelectValidProject";
	String PROCESSING_NO_PROJECT = "processingNoProject";
	String PROCESSING_TYPE_PROJECT_NAME = "processingTypeProjectName";
	String PROCESSING_PROJECT_ALREADY_EXISTS = "processingProjectAlreadyExists";
	String PROCESSING_SELECT_FILE_NAME = "processingSelectFileName";
	String PROCESSING_FILE_EXISTS = "processingFileExists";
	String PROCESSING_SELECT_VALID_FOLDER_FILE = "processingSelectValidFolderFile";
	//
	String LABEL_SELECT_CREATE_PROJECT = "labelSelectCreateProject";
	String LABEL_SELECT_CREATE_PROJECT_INFO = "labelSelectCreateProjectInfo";
	String LABEL_SELECT_EXISTING_PROJECT = "labelSelectExistingProject";
	String LABEL_CREATE_NEW_PROJECT = "labelCreateNewProject";
	String LABEL_FILE_NAME = "labelFileName";
	String LABEL_FILE_NAME_INFO = "labelFileNameInfo";
	String LABEL_FILE_CREATION_PROJECT_PATH = "labelFileCreationProjectPath";
	String LABEL_SELECT_FILE_NAME = "labelSelectFileName";
	String LABEL_SELECT_PROJECT = "labelSelectProject";
	//
	String LABEL_INSTRUMENT_NAME = "labelInstrumentName";
	String LABEL_ADD_INSTRUMENT = "labelAddInstrument";
	String LABEL_DELETE_INSTRUMENT = "labelDeleteInstrument";
	String LABEL_CLEAR_INSTRUMENTS = "labelClearInstruments";
	String LABEL_QUESTION_DELETE_INSTRUMENTS = "labelQuestionDeleteInstruments";
	String LABEL_TYPE_IN_INSTRUMENT = "labelTypeInInstrument";
	String LABEL_INSTRUMENT_EXISTS = "labelInstrumentExists";
	//
	String LABEL_DILUTION = "labelDilution";
	String LABEL_ADD_DILUTION = "labelAddDilution";
	String LABEL_DELETE_DILUTION = "labelDeleteDilution";
	String LABEL_CLEAR_DILUTIONS = "labelClearDilutions";
	String LABEL_QUESTION_DELETE_DILUTIONS = "labelQuestionDeleteDilutions";
	String LABEL_TYPE_IN_DILUTION = "labelTypeInDilution";
	String LABEL_DILUTION_EXISTS = "labelDilutionExists";
	//
	String LABEL_COPY_LINES_INFO = "labelCopyLinesInfo";
	String LABEL_COPY_SELECTION_CLIPBOARD = "labelCopySelectionClipboard";
	//
	String COLUMN_DATE = "columnDate";
	String COLUMN_DESCRIPTION = "columnDescription";
	String COLUMN_EDITOR = "columnEditor";
	//
	String LABEL_NO_DESCRIPTION = "labelNoDescription";
	String LABEL_NOT_AVAILABLE = "labelNotAvailable";
	String LABEL_SELECT_ENTRIES = "labelSelectEntries";
	String LABEL_SUPPLIER_AND_EXTENSIONS = "labelSupplierAndExtensions";
}
