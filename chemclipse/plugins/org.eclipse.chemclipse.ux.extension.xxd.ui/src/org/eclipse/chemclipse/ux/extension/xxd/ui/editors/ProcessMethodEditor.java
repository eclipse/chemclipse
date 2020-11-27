/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - refactoring for new method API, optimize E4 access
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import java.io.File;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.support.ui.workbench.PartSupport;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChemClipseEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.ProcessMethodNotifications;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedMethodUI;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class ProcessMethodEditor implements IModificationHandler, IChemClipseEditor {

	public static final String SNIPPET_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.part.processMethodEditor";
	//
	private static final Logger logger = Logger.getLogger(ProcessMethodEditor.class);
	//
	@Inject
	private MPart part;
	@Inject
	private ProcessMethodNotifications notifications;
	@Inject
	private PartSupport partsupport;
	@Inject
	private ProcessSupplierContext processSupplierContext;
	//
	private File processMethodFile;
	private ExtendedMethodUI extendedMethodUI;
	private IProcessMethod currentProcessMethod;

	@Focus
	public void setFocus() {

		extendedMethodUI.setFocus();
	}

	@Persist
	public void save(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		File file;
		if(processMethodFile != null && processMethodFile.exists()) {
			file = processMethodFile;
		} else {
			file = getSaveFile(shell);
		}
		//
		save(file, false);
	}

	@Override
	public boolean saveAs() {

		File file = getSaveFile(extendedMethodUI.getShell());
		return save(file, true);
	}

	@PostConstruct
	public void initialize(Composite parent, @Optional IProcessMethod processMethod, @Optional File editorInput) {

		//
		if(processMethod == null && editorInput == null) {
			throw new IllegalStateException("No method file and no process method are specified.");
		}
		//
		if(editorInput != null) {
			logger.info("Method File: " + editorInput.getAbsolutePath());
			currentProcessMethod = Adapters.adapt(editorInput, IProcessMethod.class);
			processMethodFile = editorInput;
			if(currentProcessMethod == null) {
				String message = "The process method file '" + processMethodFile + "' can't be processed.";
				logger.warn(message);
				throw new RuntimeException(message);
			}
		} else {
			currentProcessMethod = processMethod;
		}
		/*
		 * Backward compatibility
		 */
		DataCategory[] categories = currentProcessMethod.getDataCategories().toArray(new DataCategory[]{});
		if(categories == null || categories.length == 0) {
			categories = new DataCategory[]{DataCategory.CSD, DataCategory.MSD, DataCategory.WSD};
		}
		/*
		 * Part check
		 */
		if(part == null) {
			String message = "The method editor part has not been injected.";
			logger.warn(message);
			throw new RuntimeException(message);
		}
		//
		String processMethodName = currentProcessMethod.getName();
		logger.info("Process Method Name: " + processMethodName);
		String label = processMethodName.isEmpty() ? part.getLabel() : processMethodName;
		part.setLabel(label + " " + Arrays.asList(categories));
		/*
		 * It seems to happen that the process supplier context is null.
		 * Probably, it is not initialized in time. Hence this additional check.
		 * Further inspections are required to check why initialization has not been done yet.
		 */
		if(processSupplierContext == null) {
			String message = "The process supplier context has not been injected.";
			logger.warn(message);
			throw new RuntimeException(message);
		}
		/*
		 * Create the method editor.
		 */
		extendedMethodUI = new ExtendedMethodUI(parent, SWT.NONE, processSupplierContext, categories);
		extendedMethodUI.setModificationHandler(this);
		extendedMethodUI.setProcessMethod(currentProcessMethod);
		extendedMethodUI.setToolbarHeaderVisible(processMethodFile == null);
	}

	@Override
	public void setDirty(boolean dirty) {

		part.setDirty(processMethodFile == null || !extendedMethodUI.getProcessMethod().contentEquals(currentProcessMethod, true));
	}

	@PreDestroy
	private void preDestroy() {

		partsupport.closePart(part);
	}

	private File getSaveFile(Shell shell) {

		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setOverwrite(true);
		fileDialog.setText("Save As...");
		String methodName = extendedMethodUI.getMethodName();
		if(methodName.isEmpty()) {
			fileDialog.setFileName(MethodConverter.DEFAULT_METHOD_FILE_NAME);
		} else {
			String extension = "." + MethodConverter.DEFAULT_METHOD_FILE_NAME_EXTENSION;
			if(!methodName.endsWith(extension)) {
				fileDialog.setFileName(methodName + extension);
			}
		}
		fileDialog.setFilterExtensions(MethodConverter.DEFAULT_METHOD_FILE_EXTENSIONS);
		fileDialog.setFilterNames(MethodConverter.DEFAULT_METHOD_FILE_NAMES);
		File userDirectory = MethodConverter.getUserMethodDirectory();
		if(userDirectory.exists()) {
			fileDialog.setFilterPath(MethodConverter.getUserMethodDirectory().getAbsolutePath());
		}
		//
		String selectedFile = fileDialog.open();
		if(selectedFile != null) {
			File file = new File(selectedFile);
			if(!userDirectory.exists()) {
				MethodConverter.setUserMethodDirectory(file.getParentFile());
			}
			return file;
		}
		//
		return null;
	}

	private boolean save(File file, boolean saveAs) {

		boolean success = false;
		if(file != null) {
			IProcessMethod oldMethod = currentProcessMethod;
			IProcessMethod editedMethod = extendedMethodUI.getProcessMethod();
			ProcessMethod newMethod = new ProcessMethod(editedMethod);
			newMethod.setSourceFile(file);
			/*
			 * Copy the UUID from the old method to keep the file consistent
			 * and copy the read only flag.
			 */
			newMethod.setUUID(oldMethod.getUUID());
			if(editedMethod.isFinal()) {
				newMethod.setReadOnly(editedMethod.isFinal());
			}
			//
			IProcessingInfo<?> processingInfo = MethodConverter.convert(file, newMethod, MethodConverter.DEFAULT_METHOD_CONVERTER_ID, new NullProgressMonitor());
			if(processingInfo.hasErrorMessages()) {
				ProcessingInfoPartSupport.getInstance().update(processingInfo);
			} else {
				success = true;
				if(!saveAs) {
					part.setDirty(false);
					currentProcessMethod = newMethod;
					if(notifications != null) {
						notifications.updated(newMethod, oldMethod);
					}
					processMethodFile = file;
					part.setLabel(processMethodFile.getName() + " " + currentProcessMethod.getDataCategories());
				}
			}
		}
		//
		return success;
	}
}
