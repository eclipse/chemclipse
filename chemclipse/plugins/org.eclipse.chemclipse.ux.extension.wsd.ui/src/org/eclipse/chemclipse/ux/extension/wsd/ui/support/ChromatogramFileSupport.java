/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.wsd.ui.support;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.wsd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.wsd.ui.internal.support.ChromatogramExportRunnable;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author eselmeister
 */
public class ChromatogramFileSupport {

	private static final Logger logger = Logger.getLogger(ChromatogramFileSupport.class);

	/**
	 * Use only static methods.
	 */
	private ChromatogramFileSupport() {
	}

	/**
	 * Opens a file dialog and tries to save the chromatogram.
	 * 
	 * @param chromatogram
	 * @throws NoConverterAvailableException
	 */
	public static boolean saveChromatogram(IChromatogramWSD chromatogram) throws NoConverterAvailableException {

		if(chromatogram == null) {
			return false;
		}
		//
		FileDialog dialog = new FileDialog(DisplayUtils.getShell(), SWT.SAVE);
		/*
		 * Create the dialogue.
		 */
		dialog.setFilterPath(Activator.getDefault().getSettingsPath());
		dialog.setFileName(chromatogram.getName());
		dialog.setText("Save Chromatogram As");
		dialog.setOverwrite(true);
		IChromatogramConverterSupport converterSupport = ChromatogramConverterWSD.getInstance().getChromatogramConverterSupport();
		/*
		 * Set the filters that allow an export of chromatographic data.
		 */
		String[] filterExtensions = converterSupport.getExportableFilterExtensions();
		dialog.setFilterExtensions(filterExtensions);
		String[] filterNames = converterSupport.getExportableFilterNames();
		dialog.setFilterNames(filterNames);
		/*
		 * Opens the dialog.<br/> Use converterSupport.getExportSupplier()
		 * instead of converterSupport.getSupplier() otherwise a wrong supplier
		 * will be taken.
		 */
		String filename = dialog.open();
		if(filename != null) {
			validateFile(dialog, converterSupport.getExportSupplier(), DisplayUtils.getShell(), converterSupport, chromatogram);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Write the chromatogram.<br/>
	 * The supplier is the converter to export the given chromatogram.
	 * 
	 * @param file
	 * @param chromatogram
	 * @param supplier
	 */
	public static void writeFile(final File file, final IChromatogramWSD chromatogram, final ISupplier supplier) {

		/*
		 * If one of these instances is null, no chance to get it running.<br/>
		 * The only possibility is to exit.
		 */
		if(file == null || chromatogram == null || supplier == null) {
			return;
		}
		/*
		 * Convert the given chromatogram.
		 */
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(DisplayUtils.getShell());
		ChromatogramExportRunnable runnable = new ChromatogramExportRunnable(file, chromatogram, supplier);
		try {
			dialog.run(true, false, runnable);
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(InterruptedException e) {
			logger.warn(e);
		}
		File data = runnable.getData();
		if(data == null) {
			MessageDialog.openInformation(DisplayUtils.getShell(), "Save Chromatogram", "There is not suitable chromatogram converter available.");
		}
	}

	// ---------------------------------------------------private methods
	/**
	 * Validates the selected file to save the chromatogram. This method checks
	 * if the chromatogram is stored in a directory or not and prepares the file
	 * system in a convenient way.
	 * 
	 * @param dialog
	 * @param supplier
	 * @param shell
	 * @param converterSupport
	 * @param chromatogram
	 */
	private static void validateFile(FileDialog dialog, List<ISupplier> supplier, Shell shell, IChromatogramConverterSupport converterSupport, IChromatogramWSD chromatogram) {

		File chromatogramFolder = null;
		boolean overwrite = dialog.getOverwrite();
		boolean folderExists = false;
		boolean isDirectory = false;
		/*
		 * Check if the selected supplier exists.<br/> If some super brain tries
		 * to edit the suppliers list.
		 */
		ISupplier selectedSupplier = supplier.get(dialog.getFilterIndex());
		if(selectedSupplier == null) {
			MessageDialog.openInformation(shell, "Chromatogram Converter", "The requested chromatogram converter does not exists.");
			return;
		}
		/*
		 * Get the file or directory name.
		 */
		String filename = dialog.getFilterPath() + File.separator + dialog.getFileName();
		if(selectedSupplier != null) {
			/*
			 * If the chromatogram file is stored in a directory create an
			 * appropriate directory.
			 */
			String directoryExtension = selectedSupplier.getDirectoryExtension();
			if(directoryExtension != "") {
				isDirectory = true;
				/*
				 * Remove a possible directory extension.
				 */
				filename = removeFileExtensions(filename, selectedSupplier);
				filename = filename.concat(selectedSupplier.getDirectoryExtension());
				/*
				 * Check if the folder still exists.
				 */
				chromatogramFolder = new File(filename);
				if(chromatogramFolder.exists()) {
					folderExists = true;
					if(MessageDialog.openQuestion(shell, "Overwrite", "Would you like to overwrite the chromatogram " + chromatogramFolder.toString() + "?")) {
						overwrite = true;
					} else {
						overwrite = false;
					}
				}
				/*
				 * Checks if the chromatogram shall be overwritten.
				 */
				if(overwrite) {
					if(!folderExists) {
						chromatogramFolder.mkdir();
					}
				}
			} else {
				/*
				 * Remove a possible file extension.
				 */
				filename = removeFileExtensions(filename, selectedSupplier);
				filename = filename.concat(selectedSupplier.getFileExtension());
				/*
				 * Check if the file shall be overwritten.<br/> If the user
				 * still has answered the dialog question to override with
				 * "yes", than don't show this message.<br/> How to check it?
				 * The corrected file name must equal the dialog file name,
				 * otherwise the dialog would not have asked to override.
				 */
				String filenameDialog = dialog.getFilterPath() + File.separator + dialog.getFileName();
				if(!filename.equals(filenameDialog)) {
					/*
					 * The file name has been modified. Ask for override if it
					 * still exists.
					 */
					File chromatogramFile = new File(filename);
					if(chromatogramFile.exists()) {
						if(MessageDialog.openQuestion(shell, "Overwrite", "Would you like to overwrite the chromatogram " + chromatogramFile.toString() + "?")) {
							overwrite = true;
						} else {
							overwrite = false;
						}
					}
				}
			}
			/*
			 * Write the chromatogram and check if the folder exists.
			 */
			if(overwrite) {
				/*
				 * Check the directory and file name and correct them if
				 * neccessary.
				 */
				if(isDirectory) {
					if(!folderExists) {
						if(chromatogramFolder != null) {
							chromatogramFolder.mkdir();
						}
					}
				} else {
					/*
					 * If the filename is e.g. /home/user/OP17760, correct it to
					 * e.g. /home/user/OP17760.chrom if the selected supplier
					 * supports .chrom files.
					 */
					String fileExtension = selectedSupplier.getFileExtension();
					if(!filename.endsWith(fileExtension)) {
						filename = filename + fileExtension;
					}
				}
				/*
				 * Export the chromatogram.
				 */
				writeFile(new File(filename), chromatogram, selectedSupplier);
			}
		}
	}

	// ---------------------------------------------------private methods
	/**
	 * Removes an existing directory extension if exists. For example if a new
	 * chromatogram should be written and the filename is "TEST.D.MS" than
	 * "TEST.D" will be returned if the extension is ".MS".
	 * 
	 * @param filePath
	 * @param supplier
	 * @return String
	 */
	private static String removeFileExtensions(String filePath, ISupplier supplier) {

		int start = 0;
		int stop = 0;
		/*
		 * If the directory extension is "", than the chromatogram is a plain
		 * old file.<br/> Otherwise, the chromatogram data is stored inside a
		 * directory.<br/> <br/> KEEP IN MIND THAT THE EXTENSIONS COULD BE IN
		 * LOWER AND UPPERCASE LETTERS.
		 */
		if(supplier.getDirectoryExtension().equals("")) {
			/*
			 * Remove an extension like *.cdf or *.chrom if exists.
			 */
			String fileExtension = supplier.getFileExtension();
			if(filePath.endsWith(fileExtension) || filePath.endsWith(fileExtension.toLowerCase()) || filePath.endsWith(fileExtension.toUpperCase())) {
				stop = filePath.length() - fileExtension.length();
				filePath = filePath.substring(start, stop);
			}
		} else {
			/*
			 * Remove the directory extension, e.g. *.D (Agilent), if exists.
			 */
			String directoryExtension = supplier.getDirectoryExtension();
			if(filePath.endsWith(directoryExtension) || filePath.endsWith(directoryExtension.toLowerCase()) || filePath.endsWith(directoryExtension.toUpperCase())) {
				stop = filePath.length() - directoryExtension.length();
				filePath = filePath.substring(start, stop);
			}
		}
		return filePath;
	}
}
