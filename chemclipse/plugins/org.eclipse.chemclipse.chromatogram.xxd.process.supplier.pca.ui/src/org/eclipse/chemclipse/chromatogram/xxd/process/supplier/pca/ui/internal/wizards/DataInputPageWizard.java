/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - get rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.InputFilesTable;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public abstract class DataInputPageWizard extends WizardPage {

	private static final String FILES = "Input Files: ";
	//
	private List<IDataInputEntry> dataInputEntries = new ArrayList<>();
	//
	private InputFilesTable inputFilesTable;
	private Label countFiles;

	public DataInputPageWizard(String pageName) {

		super(pageName);
		setPageComplete(false);
	}

	public void addInputFiles(List<IDataInputEntry> addInput) {

		Map<String, IDataInputEntry> uniqueInputs = new HashMap<>();
		dataInputEntries.forEach(e -> uniqueInputs.put(e.getName(), e));
		addInput.forEach(e -> uniqueInputs.put(e.getName(), e));
		dataInputEntries.clear();
		dataInputEntries.addAll(uniqueInputs.values());
		update();
	}

	public List<IDataInputEntry> getUniqueDataInputEnties() {

		return dataInputEntries;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		inputFilesTable = createInputFilesTable(composite);
		countFiles = createLabel(composite, FILES + "0");
		createToolbarBottom(composite);
		//
		setControl(composite);
	}

	@Override
	public void setVisible(boolean visible) {

		if(visible) {
			update();
		}
		super.setVisible(visible);
	}

	protected abstract void addFiles();

	protected void update() {

		inputFilesTable.update();
		redrawCountFiles();
		setPageComplete(!dataInputEntries.isEmpty());
	}

	private InputFilesTable createInputFilesTable(Composite parent) {

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		gridData.heightHint = 300;
		inputFilesTable = new InputFilesTable(parent, gridData);
		inputFilesTable.setDataInputEntries(dataInputEntries);
		return inputFilesTable;
	}

	private Label createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		label.setText(text);
		return label;
	}

	private void createToolbarBottom(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createButtonAdd(composite);
		createButtonRemove(composite);
		createButtonRemoveAll(composite);
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Add new file(s).");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				addFiles();
			}
		});
		//
		return button;
	}

	private Button createButtonRemove(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Remove selected file(s).");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				inputFilesTable.removeSelection();
				update();
			}
		});
		//
		return button;
	}

	private Button createButtonRemoveAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Remove all file(s).");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				dataInputEntries.clear();
				update();
			}
		});
		//
		return button;
	}

	private void redrawCountFiles() {

		countFiles.setText(FILES + Integer.toString(dataInputEntries.size()));
	}
}
