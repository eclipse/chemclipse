/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.checkbox;

import java.util.Collection;

import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.ITargetReference;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class TargetDisplaySettingsPage extends WizardPage {

	private Collection<? extends ITargetReference> targetReferenes;
	private ITargetDisplaySettings targetDisplaySettings;
	private TargetDisplaySettingsWizardListener settingsWizardListener;

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		/*
		 * Remove the focus from the "Finish" button. It prevents that the user
		 * accidentally press "Enter" and thus closes the dialog.
		 */
		Shell shell = getShell();
		shell.getDisplay().asyncExec(() -> shell.setDefaultButton(null));
	}

	protected TargetDisplaySettingsPage(Collection<? extends ITargetReference> targetReferenes, ITargetDisplaySettings targetDisplaySettings, TargetDisplaySettingsWizardListener settingsWizardListener) {

		super(TargetDisplaySettingsPage.class.getName());
		//
		setImageDescriptor(ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_LABELS, IApplicationImage.SIZE_64x64));
		setTitle("Peak/Scan Labels");
		setDescription("Select the labels and options to be displayed in the chromatogram.");
		//
		this.targetReferenes = targetReferenes;
		this.targetDisplaySettings = targetDisplaySettings;
		this.settingsWizardListener = settingsWizardListener;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		addTargetSettingsEditor(composite);
		addPreviewButton(composite);
		setControl(composite);
	}

	private void addTargetSettingsEditor(Composite parent) {

		new TargetSettingEditor(parent, targetDisplaySettings, this, targetReferenes);
	}

	private void addPreviewButton(Composite parent) {

		if(settingsWizardListener != null) {
			boolean preview = settingsWizardListener.isShowPreview();
			Button previewCheckbox = checkbox(parent, "Show preview in editor", preview);
			previewCheckbox.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					settingsWizardListener.setShowPreview(previewCheckbox.getSelection());
					notifyListener();
				}
			});
			//
			if(preview) {
				notifyListener();
			}
		}
	}

	public void notifyListener() {

		if(settingsWizardListener != null) {
			if(settingsWizardListener.isShowPreview()) {
				settingsWizardListener.setPreviewSettings(targetDisplaySettings);
			} else {
				settingsWizardListener.setPreviewSettings(null);
			}
		}
	}
}
