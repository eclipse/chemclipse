/*******************************************************************************
 * Copyright (c) 2018, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - rework dirty flag handling
 *******************************************************************************/
package org.eclipse.chemclipse.process.supplier.batchprocess.ui.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.process.supplier.batchprocess.core.BatchProcessJob;
import org.eclipse.chemclipse.process.supplier.batchprocess.core.BatchProcessSupport;
import org.eclipse.chemclipse.process.supplier.batchprocess.core.IBatchProcess;
import org.eclipse.chemclipse.process.supplier.batchprocess.io.BatchInputEntry;
import org.eclipse.chemclipse.process.supplier.batchprocess.io.IBatchProcessInputEntry;
import org.eclipse.chemclipse.process.supplier.batchprocess.io.JobWriter;
import org.eclipse.chemclipse.process.supplier.batchprocess.ui.Activator;
import org.eclipse.chemclipse.process.supplier.batchprocess.ui.internal.runnables.ExportRunnable;
import org.eclipse.chemclipse.process.supplier.batchprocess.ui.internal.runnables.ImportRunnable;
import org.eclipse.chemclipse.process.supplier.batchprocess.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoPartSupport;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChemClipseEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.BatchJobUI;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

/**
 * Edits a batch job of any data type. The implementation that runs the job is
 * selected by the data type stored in the job, see {@link BatchProcessSupport}.
 */
public class BatchJobEditor implements IChemClipseEditor, IRunnableWithProgress {

	public static final String ID = "org.eclipse.chemclipse.process.supplier.batchprocess.ui.editors.BatchJobEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.process.supplier.batchprocess.ui/org.eclipse.chemclipse.process.supplier.batchprocess.ui.editors.BatchJobEditor";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/batchprocess.gif";
	public static final String TOOLTIP = "Batch Process Job";

	private static final Logger logger = Logger.getLogger(BatchJobEditor.class);

	@Inject
	private MPart part;
	@Inject
	private MDirtyable dirtyable;

	private File file;
	private BatchProcessJob batchProcessJob;
	private BatchJobUI batchJobUI;

	private IProcessSupplierContext supplierContext;

	@Persist
	public void save() {

		if(file != null && batchJobUI != null) {
			try {
				JobWriter writer = new JobWriter();
				batchProcessJob = getBatchProcessJob(batchJobUI.getDataType());
				writer.writeBatchProcessJob(file, batchProcessJob);
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
	public boolean saveAs() {

		if(batchJobUI == null) {
			return false;
		}

		Display display = Display.getCurrent();
		Shell shell = display.getActiveShell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setText("Save the batch job");
		dialog.setFileName(BatchProcessJob.FILE_NAME);
		String fileName = dialog.open();
		if(fileName != null) {
			File exportFile = new File(fileName);
			batchProcessJob = getBatchProcessJob(batchJobUI.getDataType());
			ExportRunnable runnable = new ExportRunnable(exportFile, batchProcessJob);
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
			try {
				monitor.run(false, false, runnable);
				updateDirtyStatus(false);
				return true;
			} catch(InvocationTargetException e) {
				logger.warn(e);
				logger.warn(e.getCause());
			} catch(InterruptedException e) {
				logger.warn(e);
				Thread.currentThread().interrupt();
			}
		}

		return false;
	}

	/**
	 * Sets the editor dirty.
	 */
	protected void updateDirtyStatus(boolean dirty) {

		dirtyable.setDirty(dirty);
	}

	@Focus
	public void setFocus() {

		if(batchJobUI != null) {
			batchJobUI.setFocus();
		}
	}

	@PostConstruct
	private void createControl(Composite parent) {

		parent.setLayout(new FillLayout());

		loadBatchProcessJob(parent.getShell());
		if(batchProcessJob == null) {
			return;
		}

		supplierContext = new ProcessTypeSupport();
		DataType dataType = batchProcessJob.getDataType();
		batchJobUI = new BatchJobUI(parent, supplierContext, Activator.getDefault().getPreferenceStore(), PreferenceSupplier.P_FILTER_PATH_IMPORT_RECORDS, dataType, this);
		batchJobUI.setModificationHandler(this::updateDirtyStatus);
		batchJobUI.doLoad(getBatchJobFiles(), new ProcessMethod(batchProcessJob.getProcessMethod()));
	}

	private void loadBatchProcessJob(Shell shell) {

		Object object = part.getObject();
		if(object instanceof Map<?, ?> map) {
			file = new File((String)map.get(EditorSupport.MAP_FILE));
		}

		if(file != null) {
			String fileName = file.getName();
			if(fileName.length() > 4) {
				fileName = fileName.substring(0, fileName.length() - 4);
			}
			part.setLabel(fileName);
			ImportRunnable runnable = new ImportRunnable(file);
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
			try {
				monitor.run(false, false, runnable);
				batchProcessJob = runnable.getBatchProcessJob();
			} catch(InvocationTargetException e) {
				logger.warn("The file couldn't be loaded: " + file.getAbsolutePath());
				logger.warn(e.getTargetException());
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private List<File> getBatchJobFiles() {

		List<IBatchProcessInputEntry> inputEntries = batchProcessJob.getBatchProcessInputEntries();
		List<File> files = new ArrayList<>();
		for(IBatchProcessInputEntry entry : inputEntries) {
			files.add(new File(entry.getInputFile()));
		}

		return files;
	}

	private BatchProcessJob getBatchProcessJob(DataType dataType) {

		BatchProcessJob batchProcessJob = new BatchProcessJob(batchJobUI.getMethod().getProcessMethod());
		batchProcessJob.setDataType(dataType);

		List<IBatchProcessInputEntry> entries = batchProcessJob.getBatchProcessInputEntries();
		for(File file : batchJobUI.getDataList().getFiles()) {
			entries.add(new BatchInputEntry(file.getAbsolutePath()));
		}

		return batchProcessJob;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		DataType dataType = batchProcessJob.getDataType();
		IBatchProcess batchProcess = BatchProcessSupport.getBatchProcess(dataType);
		if(batchProcess == null) {
			logger.warn("No batch process is registered for the data type: " + dataType);
			return;
		}

		IProcessingInfo<?> processingInfo = batchProcess.execute(getBatchProcessJob(dataType), supplierContext, monitor);
		ProcessingInfoPartSupport.getInstance().update(processingInfo);
	}
}
