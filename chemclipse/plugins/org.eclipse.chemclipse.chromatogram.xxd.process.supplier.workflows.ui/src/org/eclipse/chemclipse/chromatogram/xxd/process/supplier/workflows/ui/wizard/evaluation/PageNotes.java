/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.wizard.evaluation;

import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class PageNotes extends AbstractExtendedWizardPage {

	private IEvaluationWizardElements wizardElements;
	private Text notesText;

	public PageNotes(IEvaluationWizardElements wizardElements) {
		//
		super(PageNotes.class.getName());
		setTitle("Evaluation Notes");
		setDescription("Please add evaluation notes on demand.");
		this.wizardElements = wizardElements;
	}

	@Override
	public boolean canFinish() {

		String projectNotes = wizardElements.getNotes();
		if(projectNotes == null) {
			return false;
		}
		return true;
	}

	@Override
	public void setDefaultValues() {

	}

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible) {
			String projectNotes = wizardElements.getNotes();
			if(projectNotes != null) {
				notesText.setText(projectNotes);
			}
			validateNotes();
		}
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		/*
		 * Notes
		 */
		notesText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
		notesText.setLayoutData(new GridData(GridData.FILL_BOTH));
		notesText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				validateNotes();
			}
		});
		//
		validateNotes();
		//
		setControl(composite);
	}

	private void validateNotes() {

		String message = null;
		//
		String projectNotes = notesText.getText();
		if(projectNotes == null) {
			projectNotes = "";
		}
		wizardElements.setNotes(projectNotes.trim());
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}
