/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.swt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.calibration.IUpdateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class RetentionTimeShifterUI extends Composite {

	private AtomicReference<Text> textControl = new AtomicReference<>();
	private AtomicReference<Button> buttonControl = new AtomicReference<>();
	//
	private Listener listener;
	private IUpdateListener updateListener;
	//
	private List<IRetentionIndexEntry> settings = new ArrayList<>();

	public RetentionTimeShifterUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void addChangeListener(Listener listener) {

		this.listener = listener;
		//
		textControl.get().addListener(SWT.KeyUp, listener);
		buttonControl.get().addListener(SWT.KeyUp, listener);
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void setInput(List<IRetentionIndexEntry> settings) {

		this.settings.clear();
		if(settings != null) {
			this.settings.addAll(settings);
		}
		//
		if(listener != null) {
			listener.handleEvent(new Event());
		}
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		//
		createText(composite);
		createButton(composite);
		//
		initialize();
		validate();
	}

	private void initialize() {

	}

	private void createText(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("0");
		text.setToolTipText("Adjust the retention time by the given value.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		/*
		 * Listen to key event.
		 */
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate();
				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					applyShift();
				}
			}
		});
		//
		textControl.set(text);
	}

	private void createButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Apply the retention time adjustment.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				applyShift();
			}
		});
		//
		buttonControl.set(button);
	}

	private void applyShift() {

		if(!settings.isEmpty()) {
			double retentionTimeMinutesShift = getRetentionTimeMinutesShift();
			if(retentionTimeMinutesShift != 0.0d) {
				int retentionTimeShift = (int)(retentionTimeMinutesShift * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				for(IRetentionIndexEntry retentionIndexEntry : settings) {
					int retentionTime = retentionIndexEntry.getRetentionTime() + retentionTimeShift;
					if(retentionTime >= 0) {
						float retentionIndex = retentionIndexEntry.getRetentionIndex();
						String name = retentionIndexEntry.getName();
						IRetentionIndexEntry retentionIndexEntryShifted = new RetentionIndexEntry(retentionTime, retentionIndex, name);
						retentionIndexEntry.copyFrom(retentionIndexEntryShifted);
					}
				}
				fireUpdate(Display.getDefault());
			}
		}
	}

	private double getRetentionTimeMinutesShift() {

		try {
			return Double.parseDouble(textControl.get().getText().trim());
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	private void validate() {

		boolean enabled = !settings.isEmpty() && getRetentionTimeMinutesShift() != 0.0d;
		buttonControl.get().setEnabled(enabled);
	}

	private void fireUpdate(Display display) {

		if(updateListener != null) {
			updateListener.update(display);
		}
	}
}