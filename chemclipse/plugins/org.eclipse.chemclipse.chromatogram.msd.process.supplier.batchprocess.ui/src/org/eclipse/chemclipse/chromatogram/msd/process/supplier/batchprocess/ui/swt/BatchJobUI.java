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
 * Christoph Läubrich - move execute Button to the top toolbar to conserve space
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.swt;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.BatchProcessJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.internal.runnables.BatchProcessRunnable;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedMethodUI;
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
	private ExtendedChromatogramListUI extendedChromatogramListUI;
	private ExtendedMethodUI extendedMethodUI;
	private BatchProcessJob batchProcessJob;

	public BatchJobUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void update(BatchProcessJob batchProcessJob) {

		this.batchProcessJob = batchProcessJob;
		extendedChromatogramListUI.update(batchProcessJob);
		//
		if(batchProcessJob != null) {
			extendedMethodUI.update(batchProcessJob.getProcessMethod());
		} else {
			extendedMethodUI.update(null);
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

		extendedChromatogramListUI = new ExtendedChromatogramListUI(parent, SWT.NONE);
		extendedChromatogramListUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		extendedChromatogramListUI.setModificationHandler(new IModificationHandler() {

			@Override
			public void setDirty(boolean dirty) {

				setEditorDirty(dirty);
			}
		});
	}

	private void createExtendedMethodUI(Composite parent) {

		extendedMethodUI = new ExtendedMethodUI(parent, SWT.NONE);
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
		// button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String title = "Batch Job";
				MessageBox messageBox = new MessageBox(e.widget.getDisplay().getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setText(title);
				messageBox.setMessage("Would you like to execute the batch job?");
				int decision = messageBox.open();
				if(SWT.YES == decision) {
					BatchProcessRunnable runnable = new BatchProcessRunnable(batchProcessJob);
					ProgressMonitorDialog monitor = new ProgressMonitorDialog(button.getShell());
					try {
						monitor.run(false, true, runnable);
						IProcessingInfo processingInfo = runnable.getProcessingInfo();
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
