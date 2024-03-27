/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 * Philip Wenig - provide shell explicitly
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.File;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.converter.methods.MethodConverterSupport;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.support.ui.files.ExtendedFileDialog;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class MethodFileSupport {

	private MethodFileSupport() {

	}

	public static boolean saveProccessMethod(Shell shell, IProcessMethod processMethod) throws NoConverterAvailableException {

		File currentFile = processMethod.getSourceFile();
		String filename = currentFile != null ? currentFile.getName() : ExtensionMessages.processMethodFilename;
		//
		return saveProccessMethod(shell, processMethod, filename);
	}

	public static boolean saveProccessMethod(Shell shell, IProcessMethod processMethod, String fileName) throws NoConverterAvailableException {

		if(processMethod == null) {
			return false;
		}
		/*
		 * The currently selected method directory shall be preferred.
		 */
		File directory = MethodConverter.getUserMethodDirectory();
		String filterPath = directory.exists() ? directory.getAbsolutePath() : Activator.getDefault().getSettingsPath();
		//
		FileDialog dialog = ExtendedFileDialog.create(shell, SWT.SAVE);
		dialog.setFilterPath(filterPath);
		dialog.setFileName(fileName);
		dialog.setText(ExtensionMessages.saveProcessMethodAs + "...");
		dialog.setOverwrite(true);
		//
		MethodConverterSupport converterSupport = MethodConverter.getMethodConverterSupport();
		dialog.setFilterExtensions(getFilterExtensions(converterSupport));
		dialog.setFilterNames(converterSupport.getFilterNames());
		String filename = dialog.open();
		int index = dialog.getFilterIndex();
		if(index == -1) {
			return false;
		}
		//
		ISupplier selectedSupplier = converterSupport.getSupplier().get(index);
		if(selectedSupplier == null) {
			MessageDialog.openInformation(shell, ExtensionMessages.saveProcessMethodAs, ExtensionMessages.requestedConverterDoesNotExist);
			return false;
		}
		//
		File file = new File(filename);
		ProcessMethod newMethod = (ProcessMethod)processMethod;
		newMethod.setSourceFile(file);
		newMethod.setName(newMethod.getName());
		writeFile(shell, file, newMethod, selectedSupplier);
		//
		return true;
	}

	private static String[] getFilterExtensions(MethodConverterSupport converterSupport) throws NoConverterAvailableException {

		String[] extensions = converterSupport.getFilterExtensions();
		for(int i = 0; i < extensions.length; i++) {
			String extension = extensions[i];
			extensions[i] = extension.startsWith(".") ? ("*" + extension) : extension;
		}
		//
		return extensions;
	}

	public static void writeFile(Shell shell, final File file, final IProcessMethod processMethod, final ISupplier supplier) {

		if(file == null || processMethod == null || supplier == null) {
			return;
		}
		IProcessingInfo<?> processingInfo = MethodConverter.convert(file, processMethod, MethodConverter.DEFAULT_METHOD_CONVERTER_ID, new NullProgressMonitor());
		if(processingInfo.hasErrorMessages()) {
			ProcessingInfoPartSupport.getInstance().update(processingInfo);
		}
	}
}