/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.wizards;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class PageDataType extends AbstractExtendedWizardPage {

	private static final DataType[] DATA_TYPES = new DataType[]{DataType.CSD, DataType.MSD, DataType.WSD};
	//
	private DataType[] dataTypesSelection = DATA_TYPES;

	public PageDataType() {

		super(PageDataType.class.getName());
		setTitle("Chromatogram Batch Process");
		setDescription("Select the data type(s) to analyze.");
	}

	@Override
	public boolean canFinish() {

		return true;
	}

	@Override
	public void setDefaultValues() {

	}

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
	}

	public DataType[] getDataTypes() {

		return dataTypesSelection;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		//
		createComboViewerDataTypes(composite);
		//
		setControl(composite);
	}

	private ComboViewer createComboViewerDataTypes(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof DataType) {
					return ((DataType)element).name();
				} else if(element instanceof String) {
					return element.toString();
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select the data type(s) that shall be batch processed.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof DataType) {
					dataTypesSelection = new DataType[]{(DataType)object};
				} else {
					dataTypesSelection = DATA_TYPES;
				}
			}
		});
		//
		comboViewer.setInput(new Object[]{"All Data Types", DataType.CSD, DataType.MSD, DataType.WSD});
		combo.select(0);
		//
		return comboViewer;
	}
}
