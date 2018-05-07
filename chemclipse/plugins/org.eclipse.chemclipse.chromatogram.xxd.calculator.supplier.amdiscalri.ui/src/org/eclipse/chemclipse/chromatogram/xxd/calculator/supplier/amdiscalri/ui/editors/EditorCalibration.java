/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.editors;

import java.io.File;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.CalibrationFileReader;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.CalibrationFileWriter;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

public class EditorCalibration extends MultiPageEditorPart {

	private PageCalibration pageCalibration;
	private File file;
	private boolean isDirty = false;
	private boolean initialize = true;
	//
	private ISeparationColumnIndices separationColumnIndices;

	@Override
	protected void createPages() {

		pageCalibration = new PageCalibration(getContainer());
		int pageIndex = addPage(pageCalibration.getControl());
		setPageText(pageIndex, "Retention Index Calibration (*.cal)");
		setDirty(true);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

		CalibrationFileWriter calibrationFileWriter = new CalibrationFileWriter();
		calibrationFileWriter.write(file, separationColumnIndices);
		setDirty(false);
	}

	@Override
	public void doSaveAs() {

		FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
		fileDialog.setText("Save the *.cal file.");
		fileDialog.setFilterExtensions(new String[]{"*.cal"});
		fileDialog.setFilterNames(new String[]{"AMDIS Calibration *.cal"});
		String pathRetentionIndexFile = fileDialog.open();
		if(pathRetentionIndexFile != null) {
			File file = new File(pathRetentionIndexFile);
			CalibrationFileWriter calibrationFileWriter = new CalibrationFileWriter();
			calibrationFileWriter.write(file, separationColumnIndices);
			setDirty(false);
		}
	}

	@Override
	public boolean isSaveAsAllowed() {

		return true;
	}

	@Override
	public void setFocus() {

		if(initialize) {
			initialize = false;
			CalibrationFileReader calibrationFileReader = new CalibrationFileReader();
			separationColumnIndices = calibrationFileReader.parse(file);
			pageCalibration.showData(file, separationColumnIndices);
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		super.init(site, input);
		String fileName = input.getName();
		fileName = fileName.substring(0, fileName.length() - 4);
		setPartName(fileName);
		if(input instanceof IFileEditorInput) {
			//
			IFileEditorInput fileEditorInput = (IFileEditorInput)input;
			this.file = fileEditorInput.getFile().getLocation().toFile();
		} else {
			throw new PartInitException("The file could't be loaded.");
		}
	}

	@Override
	public void dispose() {

		super.dispose();
	}

	@Override
	public boolean isDirty() {

		return isDirty;
	}

	public void setDirty(boolean isDirty) {

		this.isDirty = isDirty;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}
}
