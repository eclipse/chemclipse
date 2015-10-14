/*******************************************************************************
 * Copyright (c) 2015 Daniel Mariano.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Daniel Mariano- initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PeakIntensityTableTimeRangeWizardPage extends WizardPage {

	private Text text1;
	private Composite container;

	public PeakIntensityTableTimeRangeWizardPage() {

		super("Peak Intensity Table Actions");
		setTitle("Peak Intensity Table Actions");
		setDescription("Display Time Range");
	}

	@Override
	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		Label label1 = new Label(container, SWT.NONE);
		label1.setText("Enter time range to display (Ex: 4.5-5.7): ");
		text1 = new Text(container, SWT.BORDER | SWT.SINGLE);
		text1.setText("");
		text1.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

				if(!text1.getText().isEmpty()) {
					setPageComplete(true);
				}
			}
		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		text1.setLayoutData(gd);
		/*
		 * required to avoid an error in the system
		 */
		setControl(container);
		setPageComplete(false);
	}

	/*
	 * Gets user input of first text field
	 */
	public String getText1() {

		return text1.getText();
	}
}