/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.baseline.IChromatogramBaseline;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramBaselinesUI extends Composite {

	private ComboViewer comboViewer;
	private Button buttonAdd;
	private Button buttonDelete;
	private IChromatogram<?> chromatogram;
	//
	private IUpdateListener updateListener;

	public ChromatogramBaselinesUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void update(IChromatogram<?> chromatogram) {

		this.chromatogram = chromatogram;
		updateData();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		//
		comboViewer = createComboViewer(composite);
		buttonAdd = createButtonAdd(composite);
		buttonDelete = createButtonDelete(composite);
	}

	private ComboViewer createComboViewer(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ListContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof String) {
					return (String)element;
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a chromatogram baseline.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(chromatogram != null) {
					Object object = comboViewer.getStructuredSelection().getFirstElement();
					if(object instanceof String) {
						String baselineId = (String)object;
						chromatogram.setActiveBaseline(baselineId);
						fireUpdate();
					}
				}
			}
		});
		//
		return comboViewer;
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add a new baseline.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(chromatogram != null) {
					InputDialog inputDialog = new InputDialog(e.display.getActiveShell(), "New Baseline", "Add a new baseline identifier.", "", new IInputValidator() {

						@Override
						public String isValid(String newText) {

							if(newText == null || newText.isBlank()) {
								return "Please select a valid baseline id.";
							}
							//
							for(String baselineId : chromatogram.getBaselineIds()) {
								if(newText.equals(baselineId)) {
									return "The baseline id exists already.";
								}
							}
							return null;
						}
					});
					//
					if(inputDialog.open() == Window.OK) {
						chromatogram.setActiveBaseline(inputDialog.getValue().trim());
						updateData();
						fireUpdate();
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Remove the baseline.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(chromatogram != null) {
					Object object = comboViewer.getStructuredSelection().getFirstElement();
					if(object instanceof String) {
						String activeBaselineId = (String)object;
						if(!activeBaselineId.equals(IChromatogramBaseline.DEFAULT_BASELINE_ID)) {
							if(MessageDialog.openConfirm(e.display.getActiveShell(), "Delete Baseline", "Do you want to delete the selected baseline?")) {
								chromatogram.removeBaseline(activeBaselineId);
								updateData();
								fireUpdate();
							}
						}
					}
				}
			}
		});
		//
		return button;
	}

	private void updateData() {

		if(chromatogram != null) {
			//
			String activeBaseline = chromatogram.getActiveBaseline();
			List<String> baselineIds = new ArrayList<>(chromatogram.getBaselineIds());
			Collections.sort(baselineIds);
			comboViewer.setInput(baselineIds);
			//
			int index = -1;
			exitloop:
			for(int i = 0; i < baselineIds.size(); i++) {
				if(baselineIds.get(i).equals(activeBaseline)) {
					index = i;
					break exitloop;
				}
			}
			//
			if(index != -1) {
				comboViewer.getCombo().select(index);
			}
			//
			buttonAdd.setEnabled(true);
			buttonDelete.setEnabled(!chromatogram.getActiveBaseline().equals(IChromatogramBaseline.DEFAULT_BASELINE_ID));
		} else {
			comboViewer.setInput(null);
			buttonAdd.setEnabled(false);
			buttonDelete.setEnabled(false);
		}
	}

	private void fireUpdate() {

		if(updateListener != null) {
			updateListener.update();
		}
	}
}