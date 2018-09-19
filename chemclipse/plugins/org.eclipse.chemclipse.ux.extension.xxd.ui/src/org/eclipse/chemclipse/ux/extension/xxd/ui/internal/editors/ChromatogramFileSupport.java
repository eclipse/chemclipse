/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.chemclipse.converter.chromatogram.ChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.ChromatogramExportRunnable;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("rawtypes")
public class ChromatogramFileSupport {

	private static final Logger logger = Logger.getLogger(ChromatogramFileSupport.class);

	private ChromatogramFileSupport() {
	}

	public static boolean saveChromatogram(Shell shell, IChromatogram chromatogram, DataType dataType) throws NoConverterAvailableException {

		if(chromatogram == null || shell == null) {
			return false;
		}
		//
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		/*
		 * Create the dialog.
		 */
		dialog.setFilterPath(Activator.getDefault().getSettingsPath());
		dialog.setFileName(chromatogram.getName());
		dialog.setText("Save Chromatogram As...");
		dialog.setOverwrite(true);
		//
		ChromatogramConverterSupport chromatogramConverterSupport = getChromatogramConvertSupport(dataType);
		if(chromatogramConverterSupport != null) {
			/*
			 * Set the filters that allow an export of chromatographic data.
			 */
			String[] filterExtensions = chromatogramConverterSupport.getExportableFilterExtensions();
			dialog.setFilterExtensions(filterExtensions);
			String[] filterNames = chromatogramConverterSupport.getExportableFilterNames();
			dialog.setFilterNames(filterNames);
			/*
			 * Opens the dialog.<br/> Use converterSupport.getExportSupplier()
			 * instead of converterSupport.getSupplier() otherwise a wrong supplier
			 * will be taken.
			 */
			String filename = dialog.open();
			if(filename != null) {
				validateFile(dialog, chromatogramConverterSupport.getExportSupplier(), shell, chromatogramConverterSupport, chromatogram, dataType);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private static ChromatogramConverterSupport getChromatogramConvertSupport(DataType dataType) {

		ChromatogramConverterSupport converterSupport = null;
		switch(dataType) {
			case MSD_NOMINAL:
			case MSD_TANDEM:
			case MSD_HIGHRES:
			case MSD:
				converterSupport = ChromatogramConverterMSD.getChromatogramConverterSupport();
				break;
			case CSD:
				converterSupport = ChromatogramConverterCSD.getChromatogramConverterSupport();
				break;
			case WSD:
				converterSupport = ChromatogramConverterWSD.getChromatogramConverterSupport();
				break;
			default:
				// Do nothing
		}
		//
		return converterSupport;
	}

	public static void writeFile(Shell shell, File file, IChromatogram chromatogram, ISupplier supplier, DataType dataType) {

		if(file == null || chromatogram == null || supplier == null) {
			return;
		}
		//
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		ChromatogramExportRunnable runnable = new ChromatogramExportRunnable(file, chromatogram, supplier, dataType);
		try {
			dialog.run(true, false, runnable);
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(InterruptedException e) {
			logger.warn(e);
		}
		//
		File data = runnable.getData();
		if(data == null) {
			MessageDialog.openInformation(shell, "Save Chromatogram", "There is not suitable chromatogram converter available.");
		}
	}

	private static void validateFile(FileDialog dialog, List<ISupplier> supplier, Shell shell, ChromatogramConverterSupport converterSupport, IChromatogram chromatogram, DataType dataType) {

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
				//
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
				writeFile(shell, new File(filename), chromatogram, selectedSupplier, dataType);
			}
		}
	}

	private static String removeFileExtensions(String filePath, ISupplier supplier) {

		int start = 0;
		int stop = 0;
		//
		if(supplier.getDirectoryExtension().equals("")) {
			/*
			 * Remove the file extension.
			 */
			String fileExtension = supplier.getFileExtension();
			if(filePath.endsWith(fileExtension) || filePath.endsWith(fileExtension.toLowerCase()) || filePath.endsWith(fileExtension.toUpperCase())) {
				stop = filePath.length() - fileExtension.length();
				filePath = filePath.substring(start, stop);
			}
		} else {
			/*
			 * Remove the directory extension.
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
