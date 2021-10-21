/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.chemclipse.tsd.converter.chromatogram.ChromatogramConverterTSD;
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

		return saveChromatogram(shell, chromatogram, dataType, UserManagement.getUserHome());
	}

	public static boolean saveChromatogram(Shell shell, IChromatogram chromatogram, DataType dataType, String filterPath) throws NoConverterAvailableException {

		if(chromatogram == null || shell == null) {
			return false;
		}
		/*
		 * Create the dialog.
		 */
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setFilterPath(filterPath);
		fileDialog.setFileName(chromatogram.getName());
		fileDialog.setText("Save Chromatogram As...");
		fileDialog.setOverwrite(true);
		//
		IChromatogramConverterSupport chromatogramConverterSupport = getChromatogramConvertSupport(dataType);
		if(chromatogramConverterSupport != null) {
			/*
			 * Opens the dialog.<br/> Use converterSupport.getExportSupplier()
			 * instead of converterSupport.getSupplier() otherwise a wrong supplier
			 * will be taken.
			 */
			Map<Integer, ISupplier> exportSupplierMap = setExportConverter(chromatogramConverterSupport, fileDialog);
			String filename = fileDialog.open();
			if(filename != null) {
				int key = fileDialog.getFilterIndex();
				if(exportSupplierMap.containsKey(key)) {
					ISupplier selectedSupplier = exportSupplierMap.get(key);
					String filePath = fileDialog.getFilterPath() + File.separator + fileDialog.getFileName();
					boolean overwrite = fileDialog.getOverwrite();
					validateAndExportFile(shell, chromatogram, dataType, filePath, overwrite, selectedSupplier);
					return true;
				}
			}
		}
		/*
		 * False: It was not possible to export the data.
		 */
		return false;
	}

	@SuppressWarnings("deprecation")
	private static Map<Integer, ISupplier> setExportConverter(IChromatogramConverterSupport chromatogramConverterSupport, FileDialog fileDialog) throws NoConverterAvailableException {

		Map<Integer, ISupplier> exportSupplierMap = new HashMap<>();
		/*
		 * Get the names and extensions.
		 */
		String[] names = chromatogramConverterSupport.getExportableFilterNames();
		String[] extensions = chromatogramConverterSupport.getExportableFilterExtensions();
		List<ISupplier> suppliers = chromatogramConverterSupport.getExportSupplier();
		if(extensions.length != names.length) {
			throw new NoConverterAvailableException("The size of extensions and names is unequal.");
		}
		/*
		 * Collect all converter and select the ChemClipse *.ocb format
		 * as the default converter.
		 */
		String promotedName = null;
		String promotedExtension = null;
		Map<String, String> exportFilter = new HashMap<>();
		for(int i = 0; i < names.length; i++) {
			String name = names[i];
			String extension = extensions[i];
			if(extension.contains(".ocb")) {
				promotedName = name;
				promotedExtension = extension;
			} else {
				exportFilter.put(name, extension);
			}
		}
		/*
		 * Sort the converter names.
		 */
		List<String> keys = new ArrayList<>(exportFilter.keySet());
		Collections.sort(keys);
		boolean promotionAvailable = promotedName != null;
		int size = keys.size() + ((promotionAvailable) ? 1 : 0);
		String[] extensionsSorted = new String[size];
		String[] namesSorted = new String[size];
		/*
		 * Set the ChemClipse *.ocb converter as the
		 * first element if available.
		 */
		int offset = 0;
		if(promotionAvailable) {
			namesSorted[offset] = promotedName;
			extensionsSorted[offset] = promotedExtension;
			exportSupplierMap.put(offset, getSupplier(promotedName, suppliers));
			offset++;
		}
		/*
		 * Set all other sorted converter.
		 */
		for(int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			int index = i + offset;
			namesSorted[index] = key;
			extensionsSorted[index] = exportFilter.get(key);
			exportSupplierMap.put(index, getSupplier(key, suppliers));
		}
		/*
		 * Set the FileDialog sorted names and file extensions.
		 */
		fileDialog.setFilterNames(namesSorted);
		fileDialog.setFilterExtensions(extensionsSorted);
		//
		return exportSupplierMap;
	}

	private static ISupplier getSupplier(String filterName, List<ISupplier> suppliers) {

		for(ISupplier supplier : suppliers) {
			if(supplier.isExportable() && supplier.getFilterName().equals(filterName)) {
				return supplier;
			}
		}
		return null;
	}

	private static IChromatogramConverterSupport getChromatogramConvertSupport(DataType dataType) {

		IChromatogramConverterSupport converterSupport = null;
		switch(dataType) {
			case MSD_NOMINAL:
			case MSD_TANDEM:
			case MSD_HIGHRES:
			case MSD:
				converterSupport = ChromatogramConverterMSD.getInstance().getChromatogramConverterSupport();
				break;
			case CSD:
				converterSupport = ChromatogramConverterCSD.getInstance().getChromatogramConverterSupport();
				break;
			case WSD:
				converterSupport = ChromatogramConverterWSD.getInstance().getChromatogramConverterSupport();
				break;
			case TSD:
				converterSupport = ChromatogramConverterTSD.getInstance().getChromatogramConverterSupport();
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

	private static void validateAndExportFile(Shell shell, IChromatogram chromatogram, DataType dataType, String filePath, boolean overwrite, ISupplier selectedSupplier) {

		File chromatogramFolder = null;
		boolean folderExists = false;
		boolean isDirectory = false;
		/*
		 * Get the file or directory name.
		 */
		String filename = filePath;
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
				String filenameDialog = filePath;
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
		} else {
			MessageDialog.openInformation(shell, "Chromatogram Converter", "The requested chromatogram converter does not exists.");
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
