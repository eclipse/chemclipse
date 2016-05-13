/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
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
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.CalibrationFileReader;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IRetentionIndexEntry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

public class EditorCalibration extends MultiPageEditorPart {

	private PageCalibration pageCalibration;
	private File file;
	private List<IRetentionIndexEntry> retentionIndexEntries;

	@Override
	protected void createPages() {

		pageCalibration = new PageCalibration(getContainer());
		int pageIndex = addPage(pageCalibration.getControl());
		setPageText(pageIndex, "Retention Index Calibration (*.cal)");
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public boolean isSaveAsAllowed() {

		return false;
	}

	@Override
	public void setFocus() {

		if(retentionIndexEntries == null) {
			CalibrationFileReader calibrationFileReader = new CalibrationFileReader();
			retentionIndexEntries = calibrationFileReader.parse(file);
		}
		pageCalibration.showData(retentionIndexEntries);
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
}
