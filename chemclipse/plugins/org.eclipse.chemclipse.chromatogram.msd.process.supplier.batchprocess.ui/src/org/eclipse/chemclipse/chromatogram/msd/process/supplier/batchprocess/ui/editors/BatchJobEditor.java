/*******************************************************************************
 * Copyright (c) 2018 , 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - rework dirty flag handling
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.stream.XMLStreamException;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.io.JobWriter;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.BatchProcessJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.internal.runnables.ImportRunnable;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.swt.BatchJobUI;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class BatchJobEditor extends EditorPart {

	private static final Logger logger = Logger.getLogger(BatchJobEditor.class);
	//
	private BatchJobUI batchJobUI;
	private BatchProcessJob batchProcessJob = null;
	private File file;
	private boolean isDirty = false;

	@Override
	public void doSave(IProgressMonitor monitor) {

		if(file != null) {
			JobWriter writer = new JobWriter();
			try {
				batchJobUI.doSave();
				writer.writeBatchProcessJob(file, batchProcessJob, monitor);
				updateDirtyStatus(false);
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
			ImportRunnable runnable = new ImportRunnable(file);
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

		return isDirty;
	}

	/**
	 * Sets the editor dirty.
	 */
	protected void updateDirtyStatus(boolean dirty) {

		this.isDirty = dirty;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public boolean isSaveAsAllowed() {

		return false;
	}

	@Override
	public void createPartControl(Composite parent) {

		parent.setLayout(new FillLayout());
		batchJobUI = new BatchJobUI(parent, SWT.NONE, new ProcessTypeSupport(), new DataType[]{DataType.CSD, DataType.MSD, DataType.WSD});
		batchJobUI.setModificationHandler(new IModificationHandler() {

			@Override
			public void setDirty(boolean dirty) {

				updateDirtyStatus(dirty);
			}
		});
	}

	@Override
	public void setFocus() {

		batchJobUI.setFocus();
		batchJobUI.doLoad(batchProcessJob);
	}
}