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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.stream.XMLStreamException;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.io.BatchProcessJobWriter;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.internal.runnables.BatchProcessImportRunnable;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.swt.BatchJobUI;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class BatchJobEditor extends EditorPart {

	private static final Logger logger = Logger.getLogger(BatchJobEditor.class);
	//
	private BatchJobUI batchJobUI;
	private IBatchProcessJob batchProcessJob = null;
	private File file;
	//
	private MDirtyable dirtyable = new MDirtyable() {

		private boolean value = false;

		@Override
		public void setDirty(boolean value) {

			this.value = value;
		}

		@Override
		public boolean isDirty() {

			return value;
		}
	};

	@Override
	public void doSave(IProgressMonitor monitor) {

		if(file != null) {
			BatchProcessJobWriter writer = new BatchProcessJobWriter();
			try {
				writer.writeBatchProcessJob(file, batchProcessJob, monitor);
				dirtyable.setDirty(false);
			} catch(FileNotFoundException e) {
				logger.warn(e);
			} catch(FileIsNotWriteableException e) {
				logger.warn(e);
			} catch(IOException e) {
				logger.warn(e);
			} catch(XMLStreamException e) {
				logger.warn(e);
			}
		}
	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		setSite(site);
		setInput(input);
		//
		String fileName = input.getName();
		fileName = fileName.substring(0, fileName.length() - 4);
		setPartName(fileName);
		//
		if(input instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput)input;
			file = fileEditorInput.getFile().getLocation().toFile();
			//
			BatchProcessImportRunnable runnable = new BatchProcessImportRunnable(file);
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(site.getShell());
			try {
				monitor.run(false, true, runnable);
				batchProcessJob = runnable.getBatchProcessJob();
			} catch(InvocationTargetException e) {
				logger.warn(e);
			} catch(InterruptedException e) {
				logger.warn(e);
			}
		} else {
			throw new PartInitException("The file could't be loaded.");
		}
	}

	@Override
	public boolean isDirty() {

		return dirtyable.isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {

		return false;
	}

	@Override
	public void createPartControl(Composite parent) {

		parent.setLayout(new FillLayout());
		batchJobUI = new BatchJobUI(parent, SWT.NONE);
	}

	@Override
	public void setFocus() {

		batchJobUI.setFocus();
		batchJobUI.update(batchProcessJob);
	}
}