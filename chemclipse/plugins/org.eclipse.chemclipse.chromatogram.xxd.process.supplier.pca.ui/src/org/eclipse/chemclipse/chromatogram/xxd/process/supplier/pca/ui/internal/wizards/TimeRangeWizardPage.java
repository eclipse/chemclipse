/*******************************************************************************
 * Copyright (c) 2015, 2018 Daniel Mariano.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Daniel Mariano- initial API and implementation
 * Dr. Philip Wenig - minor improvements
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TimeRangeWizardPage extends WizardPage {

	private Text textRetentionTimeRange;

	public TimeRangeWizardPage() {
		super("Peak Intensity Table Actions");
		setTitle("Peak Intensity Table Actions");
		setDescription("Display Time Range");
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 2;
		//
		Label label = new Label(composite, SWT.NONE);
		label.setText("Enter time range to display (Ex: 4.5-5.7): ");
		//
		textRetentionTimeRange = new Text(composite, SWT.BORDER | SWT.SINGLE);
		textRetentionTimeRange.setText("");
		textRetentionTimeRange.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(!textRetentionTimeRange.getText().isEmpty()) {
					setPageComplete(true);
				}
			}
		});
		//
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		textRetentionTimeRange.setLayoutData(gridData);
		/*
		 * required to avoid an error in the system
		 */
		setControl(composite);
		setPageComplete(false);
	}

	/*
	 * Gets user input of first text field
	 */
	public String getTextRetentionTimeRange() {

		return textRetentionTimeRange.getText();
	}
}