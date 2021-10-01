/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.File;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.converter.methods.MethodConverterSupport;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class MethodFileSupport {

	private MethodFileSupport() {

	}

	public static boolean saveProccessMethod(IProcessMethod processMethod) throws NoConverterAvailableException {

		Shell shell = Display.getDefault().getActiveShell();
		File currentFile = processMethod.getSourceFile();
		String filename = currentFile != null ? currentFile.getName() : "ProcessMethod";
		return saveProccessMethod(shell, processMethod, filename);
	}

	public static boolean saveProccessMethod(Shell shell, IProcessMethod processMethod, String fileName) throws NoConverterAvailableException {

		if(processMethod == null) {
			return false;
		}
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterPath(Activator.getDefault().getSettingsPath());
		dialog.setFileName(fileName);
		dialog.setText("Save Process Method As...");
		dialog.setOverwrite(true);
		MethodConverterSupport converterSupport = MethodConverter.getMethodConverterSupport();
		dialog.setFilterExtensions(converterSupport.getFilterExtensions());
		dialog.setFilterNames(converterSupport.getFilterNames());
		String filename = dialog.open();
		int index = dialog.getFilterIndex();
		if(index == -1) {
			return false;
		}
		ISupplier selectedSupplier = converterSupport.getSupplier().get(index);
		if(selectedSupplier == null) {
			MessageDialog.openInformation(shell, "Save Process Method As", "The requested converter does not exists.");
			return false;
		}
		File file = new File(filename);
		ProcessMethod newMethod = (ProcessMethod)processMethod;
		newMethod.setSourceFile(file);
		newMethod.setName(newMethod.getName()); // derive from filename
		writeFile(shell, file, newMethod, selectedSupplier);
		return true;
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
