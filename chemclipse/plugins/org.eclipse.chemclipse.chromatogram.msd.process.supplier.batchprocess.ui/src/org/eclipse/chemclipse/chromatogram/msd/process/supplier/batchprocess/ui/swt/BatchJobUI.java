/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - move execute Button to the top toolbar to conserve space, use DataListUI instead
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.swt;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.BatchProcessJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.Activator;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.internal.runnables.BatchProcessRunnable;
import org.eclipse.chemclipse.converter.model.ChromatogramInputEntry;
import org.eclipse.chemclipse.converter.model.IChromatogramInputEntry;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.DataListUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedMethodUI;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;

public class BatchJobUI extends Composite {

	private static final Logger logger = Logger.getLogger(BatchJobUI.class);
	//
	private IModificationHandler modificationHandler = null;
	private DataListUI listUI;
	private ExtendedMethodUI extendedMethodUI;
	private BatchProcessJob batchProcessJob;
	private ProcessTypeSupport processingSupport;
	private DataType[] dataTypes;

	public BatchJobUI(Composite parent, int style, ProcessTypeSupport processingSupport, DataType[] dataTypes) {
		super(parent, style);
		this.processingSupport = processingSupport;
		this.dataTypes = dataTypes;
		createControl();
	}

	public void doLoad(BatchProcessJob batchProcessJob) {

		this.batchProcessJob = batchProcessJob;
		//
		if(batchProcessJob != null) {
			List<IChromatogramInputEntry> entries = batchProcessJob.getChromatogramInputEntries();
			List<File> files = new ArrayList<File>();
			for(IChromatogramInputEntry entry : entries) {
				files.add(new File(entry.getInputFile()));
			}
			listUI.setFiles(files);
			extendedMethodUI.update(batchProcessJob.getProcessMethod());
		} else {
			extendedMethodUI.update(null);
			listUI.setFiles(Collections.emptyList());
		}
		if(modificationHandler != null) {
			modificationHandler.setDirty(false);
		}
	}

	public void doSave() {

		if(batchProcessJob != null) {
			batchProcessJob.getChromatogramInputEntries().clear();
			List<File> files = listUI.getFiles();
			for(File file : files) {
				batchProcessJob.getChromatogramInputEntries().add(new ChromatogramInputEntry(listUI.getPath(file), listUI.getName(file)));
			}
		}
	}

	public void setModificationHandler(IModificationHandler modificationHandler) {

		this.modificationHandler = modificationHandler;
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		//
		createMainComposite(composite);
		GridLayout layoutToolbar = (GridLayout)extendedMethodUI.getToolbarMain().getLayout();
		layoutToolbar.numColumns = layoutToolbar.numColumns + 1;
		createExecuteButton(extendedMethodUI.getToolbarMain());
	}

	private void createMainComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout((new GridLayout(2, true)));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		createExtendedChromatogramUI(composite);
		createExtendedMethodUI(composite);
	}

	private void createExtendedChromatogramUI(Composite parent) {

		listUI = new DataListUI(parent, this::setEditorDirty, Activator.getDefault().getPreferenceStore(), PreferenceSupplier.P_FILTER_PATH_IMPORT_RECORDS, DataType.MSD, DataType.CSD, DataType.WSD);
		listUI.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createExtendedMethodUI(Composite parent) {

		extendedMethodUI = new ExtendedMethodUI(parent, SWT.NONE, processingSupport, dataTypes);
		extendedMethodUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		extendedMethodUI.setModificationHandler(new IModificationHandler() {

			@Override
			public void setDirty(boolean dirty) {

				setEditorDirty(dirty);
			}
		});
	}

	private Button createExecuteButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Execute");
		button.setToolTipText("Execute the batch job.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String title = "Batch Job";
				MessageBox messageBox = new MessageBox(e.widget.getDisplay().getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setText(title);
				messageBox.setMessage("Would you like to execute the batch job?");
				int decision = messageBox.open();
				if(SWT.YES == decision) {
					doSave();
					BatchProcessRunnable runnable = new BatchProcessRunnable(batchProcessJob);
					ProgressMonitorDialog monitor = new ProgressMonitorDialog(button.getShell());
					try {
						monitor.run(false, true, runnable);
						IProcessingInfo<?> processingInfo = runnable.getProcessingInfo();
						ProcessingInfoViewSupport.updateProcessingInfo(button.getDisplay(), processingInfo, true);
					} catch(InvocationTargetException e1) {
						logger.warn(e1);
					} catch(InterruptedException e1) {
						logger.warn(e1);
					}
				}
			}
		});
		return button;
	}

	private void setEditorDirty(boolean dirty) {

		if(modificationHandler != null) {
			modificationHandler.setDirty(dirty);
		}
	}
}
