/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.processors;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.CalibrationFile;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.ChromatogramWriter;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.IndexExportSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.ui.swt.dialogs.WindowsFileDialog;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class CalibrationFileExportProcessSupplier implements IProcessTypeSupplier {

	private static final Logger logger = Logger.getLogger(CalibrationFileExportProcessSupplier.class);
	//
	private static final String ID = "org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.processors.calibrationFileExportProcessSupplier";
	private static final String NAME = "AMDIS RI Calibration UI (*.cal)";
	private static final String DESCRIPTION = "Exports the calibration entries from the chromatogram to a file.";

	@Override
	public String getCategory() {

		return "Chromatogram Export";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<IndexExportSettings> implements IChromatogramSelectionProcessSupplier<IndexExportSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, IndexExportSettings.class, parent, DataCategory.MSD, DataCategory.CSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IndexExportSettings processSettings, ProcessExecutionContext context) throws InterruptedException {

			try {
				DisplayUtils.executeBusy(new Callable<Void>() {

					@Override
					public Void call() throws Exception {

						Display display = Display.getDefault();
						if(display != null) {
							display.asyncExec(new Runnable() {

								@Override
								public void run() {

									if(OperatingSystemUtils.isWindows()) {
										WindowsFileDialog.ClearInitialDirectoryWorkaround();
									}
									FileDialog fileDialog = new FileDialog(display.getActiveShell(), SWT.SAVE);
									fileDialog.setOverwrite(true);
									fileDialog.setText(CalibrationFile.DESCRIPTION);
									fileDialog.setFilterExtensions(new String[]{CalibrationFile.FILTER_EXTENSION});
									fileDialog.setFilterNames(new String[]{CalibrationFile.FILTER_NAME});
									fileDialog.setFileName(CalibrationFile.FILE_NAME);
									fileDialog.setFilterPath(PreferenceSupplier.getListPathExport());
									String path = fileDialog.open();
									if(path != null) {
										PreferenceSupplier.setListPathExport(fileDialog.getFilterPath());
										File file = new File(path);
										IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
										ChromatogramWriter writer = new ChromatogramWriter();
										try {
											writer.writeChromatogram(file, chromatogram, processSettings, context.getProgressMonitor());
										} catch(Exception e) {
											logger.warn(e);
										}
									}
								}
							});
						}
						//
						return null;
					}
				});
			} catch(Exception e) {
				logger.warn(e);
				Thread.currentThread().interrupt();
			}
			//
			return chromatogramSelection;
		}
	}
}