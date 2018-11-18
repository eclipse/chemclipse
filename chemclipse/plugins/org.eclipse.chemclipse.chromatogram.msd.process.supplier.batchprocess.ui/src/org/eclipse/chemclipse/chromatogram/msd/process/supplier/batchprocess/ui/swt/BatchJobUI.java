/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.swt;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedMethodUI;
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

	private IModificationHandler modificationHandler = null;
	private ExtendedChromatogramUI extendedChromatogramUI;
	private ExtendedMethodUI extendedMethodUI;

	public BatchJobUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void update(IBatchProcessJob batchProcessJob) {

		extendedChromatogramUI.update(batchProcessJob);
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
		composite.setLayout(new GridLayout(2, false));
		//
		createMainComposite(composite);
		createButtonComposite(composite);
	}

	private void createMainComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout((new GridLayout(1, true)));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		createExtendedChromatogramUI(composite);
		createExtendedMethodUI(composite);
	}

	private void createExtendedChromatogramUI(Composite parent) {

		extendedChromatogramUI = new ExtendedChromatogramUI(parent, SWT.NONE);
		extendedChromatogramUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		extendedChromatogramUI.setModificationHandler(new IModificationHandler() {

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

	private void createButtonComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		//
		GridData gridDataButtons = new GridData(GridData.FILL_HORIZONTAL);
		gridDataButtons.minimumWidth = 150;
		//
		createExecuteButton(composite);
	}

	private Button createExecuteButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Execute");
		button.setToolTipText("Execute the batch job.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String title = "Batch Job";
				MessageBox messageBox = new MessageBox(e.widget.getDisplay().getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setText(title);
				messageBox.setMessage("Would you like to execute the batch job?");
				int decision = messageBox.open();
				if(SWT.YES == decision) {
					// ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, true);
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
