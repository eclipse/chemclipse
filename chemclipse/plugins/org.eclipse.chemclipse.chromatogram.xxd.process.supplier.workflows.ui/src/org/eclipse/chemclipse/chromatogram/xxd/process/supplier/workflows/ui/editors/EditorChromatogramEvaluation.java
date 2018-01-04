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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.editors;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.io.ChromatogramReportReader;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISingleChromatogramReport;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

public class EditorChromatogramEvaluation extends MultiPageEditorPart {

	private static final Logger logger = Logger.getLogger(EditorChromatogramEvaluation.class);
	private PageChromatogramEvaluation pageChromatogramEvaluation;
	private ISingleChromatogramReport chromatogramReport;

	@Override
	protected void createPages() {

		pageChromatogramEvaluation = new PageChromatogramEvaluation(getContainer());
		int pageIndex = addPage(pageChromatogramEvaluation.getControl());
		setPageText(pageIndex, "Single Chromatogram Evaluation");
		pageChromatogramEvaluation.showData(chromatogramReport);
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

		pageChromatogramEvaluation.showData(chromatogramReport);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		super.init(site, input);
		String fileName = input.getName();
		fileName = fileName.substring(0, fileName.length() - 4);
		setPartName(fileName);
		if(input instanceof IFileEditorInput) {
			//
			try {
				IFileEditorInput fileEditorInput = (IFileEditorInput)input;
				File file = fileEditorInput.getFile().getLocation().toFile();
				ChromatogramReportReader chromatogramReportReader = new ChromatogramReportReader();
				chromatogramReport = chromatogramReportReader.read(file, new NullProgressMonitor());
			} catch(JAXBException e) {
				logger.warn(e);
			}
		} else {
			throw new PartInitException("The file could't be loaded.");
		}
	}

	@Override
	public void dispose() {

		pageChromatogramEvaluation.dispose();
		super.dispose();
	}
}
