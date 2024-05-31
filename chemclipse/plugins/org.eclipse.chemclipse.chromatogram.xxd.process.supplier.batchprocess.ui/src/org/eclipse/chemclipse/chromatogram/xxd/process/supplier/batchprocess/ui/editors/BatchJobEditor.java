/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - rework dirty flag handling
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.ui.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.core.BatchProcess;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.io.JobWriter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.model.BatchProcessJob;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.ui.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.ui.internal.runnables.ExportRunnable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.ui.internal.runnables.ImportRunnable;
import org.eclipse.chemclipse.converter.model.ChromatogramInputEntry;
import org.eclipse.chemclipse.converter.model.IChromatogramInputEntry;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.BatchJobUI;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class BatchJobEditor extends EditorPart implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(BatchJobEditor.class);
	//
	private File file;
	private boolean isDirty = false;
	private BatchProcessJob batchProcessJob;
	private BatchJobUI batchJobUI;
	//
	private IProcessSupplierContext supplierContext;

	@Override
	public void doSave(IProgressMonitor monitor) {

		if(file != null) {
			try {
				JobWriter writer = new JobWriter();
				batchProcessJob = getBatchProcessJob(batchJobUI.getDataType());
				writer.writeBatchProcessJob(file, batchProcessJob, monitor);
				updateDirtyStatus(false);
			} catch(FileNotFoundException e) {
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

		Display display = Display.getCurrent();
		Shell shell = display.getActiveShell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setText("Save the batch job");
		dialog.setFileName("ChromatogramBatchJob.obj");
		String fileName = dialog.open();
		if(fileName != null) {
			File exportFile = new File(fileName);
			batchProcessJob = getBatchProcessJob(batchJobUI.getDataType());
			ExportRunnable runnable = new ExportRunnable(exportFile, batchProcessJob);
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
			try {
				monitor.run(false, false, runnable);
				updateDirtyStatus(false);
			} catch(InvocationTargetException e) {
				logger.warn(e);
				logger.warn(e.getCause());
			} catch(InterruptedException e) {
				logger.warn(e);
				Thread.currentThread().interrupt();
			}
		}
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
		if(batchProcessJob == null && input instanceof IFileEditorInput fileEditorInput) {
			file = fileEditorInput.getFile().getLocation().toFile();
			ImportRunnable runnable = new ImportRunnable(file);
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(site.getShell());
			try {
				monitor.run(false, false, runnable);
				batchProcessJob = runnable.getBatchProcessJob();
			} catch(InvocationTargetException e) {
				throw new PartInitException("The file could't be loaded.", e.getTargetException());
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
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

		return false; // enable once the Date Explorer can open .obj
	}

	@Override
	public void createPartControl(Composite parent) {

		parent.setLayout(new FillLayout());
		//
		supplierContext = new ProcessTypeSupport();
		DataType dataType = batchProcessJob.getDataType();
		batchJobUI = new BatchJobUI(parent, supplierContext, Activator.getDefault().getPreferenceStore(), PreferenceSupplier.P_FILTER_PATH_IMPORT_RECORDS, dataType, this);
		batchJobUI.setModificationHandler(this::updateDirtyStatus);
		batchJobUI.doLoad(getBatchJobFiles(), new ProcessMethod(batchProcessJob.getProcessMethod()));
	}

	@Override
	public void setFocus() {

		batchJobUI.setFocus();
	}

	private List<File> getBatchJobFiles() {

		List<IChromatogramInputEntry> chromatogramInputEntries = batchProcessJob.getChromatogramInputEntries();
		List<File> files = new ArrayList<>();
		for(IChromatogramInputEntry entry : chromatogramInputEntries) {
			files.add(new File(entry.getInputFile()));
		}
		//
		return files;
	}

	private BatchProcessJob getBatchProcessJob(DataType dataType) {

		BatchProcessJob batchProcessJob = new BatchProcessJob(batchJobUI.getMethod().getProcessMethod());
		batchProcessJob.setDataType(dataType);
		//
		List<IChromatogramInputEntry> entries = batchProcessJob.getChromatogramInputEntries();
		for(File file : batchJobUI.getDataList().getFiles()) {
			entries.add(new ChromatogramInputEntry(file.getAbsolutePath()));
		}
		//
		return batchProcessJob;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		DataType dataType = batchProcessJob.getDataType();
		BatchProcess batchProcess = new BatchProcess(dataType, supplierContext);
		IProcessingInfo<?> processingInfo = batchProcess.execute(getBatchProcessJob(dataType), monitor);
		ProcessingInfoPartSupport.getInstance().update(processingInfo);
	}
}
