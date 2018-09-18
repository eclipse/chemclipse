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
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.File;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.IMethodListener;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors.MethodEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageMethods;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
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

public class MethodSupportUI extends Composite {

	private IMethodListener methodListener;
	private ISupplierEditorSupport supplierEditorSupport = new MethodEditorSupport();

	public MethodSupportUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void reset() {

		//
	}

	public void setMethodListener(IMethodListener methodListener) {

		this.methodListener = methodListener;
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setBackground(Colors.WHITE);
		GridLayout gridLayout = new GridLayout(5, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		//
		createComboPredefinedMethod(composite);
		createButtonAddMethod(composite);
		createButtonEditMethod(composite);
		createButtonExecuteMethod(composite);
		createButtonSettings(composite);
	}

	private void createComboPredefinedMethod(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Select a chromatogram method.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});
	}

	private Button createButtonAddMethod(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Creates and adds a new method.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				supplierEditorSupport.openEditor(new File(""));
			}
		});
		//
		return button;
	}

	private Button createButtonEditMethod(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Edit the selected method.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				supplierEditorSupport.openEditor(new File(""));
			}
		});
		//
		return button;
	}

	private Button createButtonExecuteMethod(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Apply the method to the selected chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				runMethod();
			}
		});
		//
		return button;
	}

	private Button createButtonSettings(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Settings");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = new PreferencePageMethods();
				preferencePage.setTitle("Methods");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(DisplayUtils.getShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.widget.getDisplay().getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
		//
		return button;
	}

	private void applySettings() {

		/*
		 * Set combo items.
		 */
	}

	private void runMethod() {

		if(methodListener != null) {
			methodListener.execute();
		}
	}
}
