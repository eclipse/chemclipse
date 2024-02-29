/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.converter.ui.swt;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.converter.scan.IScanConverterSupport;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.support.ui.files.ExtendedFileDialog;
import org.eclipse.chemclipse.xir.converter.core.ScanConverterXIR;
import org.eclipse.chemclipse.xir.model.core.ISpectrumXIR;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class InfraredSpectrumFileSupport {

	private static final Logger logger = Logger.getLogger(InfraredSpectrumFileSupport.class);

	/**
	 * Use only static methods.
	 */
	private InfraredSpectrumFileSupport() {

	}

	public static boolean saveSpectrum(ISpectrumXIR spectrum) throws NoConverterAvailableException {

		Shell shell = Display.getDefault().getActiveShell();
		saveSpectrum(shell, spectrum, "Spectrum");
		return true;
	}

	/**
	 * Opens a file dialog and tries to save the mass spectra
	 * 
	 * @param spectrum
	 * @throws NoConverterAvailableException
	 */
	@SuppressWarnings("deprecation")
	public static void saveSpectrum(Shell shell, ISpectrumXIR spectrum, String fileName) throws NoConverterAvailableException {

		if(spectrum == null) {
			return;
		}
		/*
		 * Create the dialogue.
		 */
		FileDialog dialog = ExtendedFileDialog.create(shell, SWT.SAVE);
		dialog.setFileName(fileName);
		dialog.setText("Save Spectrum As...");
		dialog.setOverwrite(true);
		IScanConverterSupport converterSupport = ScanConverterXIR.getScanConverterSupport();
		/*
		 * Set the filters that allow an export of chromatographic data.
		 */
		String[] filterExtensions = converterSupport.getExportableFilterExtensions();
		dialog.setFilterExtensions(filterExtensions);
		String[] filterNames = converterSupport.getExportableFilterNames();
		dialog.setFilterNames(filterNames);
		String filename = dialog.open();
		if(filename != null) {
			validateFile(dialog, converterSupport.getExportSupplier(), shell, spectrum);
		}
	}

	/**
	 * Validates the selected file to save the spectrum. This method checks
	 * if the spectrum is stored in a directory or not and prepares the file
	 * system in a convenient way.
	 * 
	 * @param dialog
	 * @param supplier
	 * @param shell
	 * @param spectrum
	 */
	private static void validateFile(FileDialog dialog, List<ISupplier> supplier, Shell shell, ISpectrumXIR spectrum) {

		boolean overwrite = dialog.getOverwrite();
		ISupplier selectedSupplier = supplier.get(dialog.getFilterIndex());
		if(selectedSupplier == null) {
			MessageDialog.openInformation(shell, "Spectrum Converter", "The requested converter does not exists.");
			return;
		}
		String filename = dialog.getFilterPath() + File.separator + dialog.getFileName();
		filename = FilenameUtils.removeExtension(filename);
		filename = filename.concat(selectedSupplier.getFileExtension());
		String filenameDialog = dialog.getFilterPath() + File.separator + dialog.getFileName();
		if(!filename.equals(filenameDialog)) {
			/*
			 * The file name has been modified. Ask for override if it
			 * still exists.
			 */
			File msFile = new File(filename);
			if(msFile.exists()) {
				if(MessageDialog.openQuestion(shell, "Overwrite", "Would you like to overwrite the mass spectra file " + msFile.toString() + "?")) {
					overwrite = true;
				} else {
					overwrite = false;
				}
			}
		}
		if(overwrite) {
			String fileExtension = selectedSupplier.getFileExtension();
			if(!filename.endsWith(fileExtension)) {
				filename = filename + fileExtension;
			}
		}
		writeFile(shell, new File(filename), spectrum, selectedSupplier);
	}

	public static void writeFile(Shell shell, final File file, final ISpectrumXIR spectrum, final ISupplier supplier) {

		if(file == null || spectrum == null || supplier == null) {
			return;
		}
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				try {
					monitor.beginTask("Save IR Spectrum", IProgressMonitor.UNKNOWN);
					IProcessingInfo<File> processingInfo = ScanConverterXIR.convert(file, spectrum, supplier.getId(), monitor);
					processingInfo.getProcessingResult();
				} catch(TypeCastException e) {
					logger.warn(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			dialog.run(true, false, runnable);
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(InterruptedException e) {
			logger.warn(e);
			Thread.currentThread().interrupt();
		}
	}
}
