/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.ui.editors;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

public class EditorChromatogram extends MultiPageEditorPart {

	private PageChromatogram pageChromatogram;
	private File file;

	@Override
	protected void createPages() {

		pageChromatogram = new PageChromatogram(getContainer());
		int pageIndex = addPage(pageChromatogram.getControl());
		setPageText(pageIndex, "Chromatogram");
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

		pageChromatogram.showData(file);
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

		pageChromatogram.dispose();
		super.dispose();
	}
}
