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
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.updates.IUpdateListener;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class TimeRangeShifterUI extends Composite {

	private AtomicReference<Text> textControl = new AtomicReference<>();
	private AtomicReference<Button> buttonControl = new AtomicReference<>();
	//
	private Collection<TimeRange> settings;
	private IUpdateListener updateListener;
	//
	private Listener listener;

	public TimeRangeShifterUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void addChangeListener(Listener listener) {

		this.listener = listener;
		//
		textControl.get().addListener(SWT.KeyUp, listener);
		buttonControl.get().addListener(SWT.KeyUp, listener);
	}

	public void setInput(Collection<TimeRange> settings) {

		this.settings = settings;
		//
		if(listener != null) {
			listener.handleEvent(new Event());
		}
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
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
		text.setToolTipText("Adjust the selected retention time [min] by the given value.");
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
		button.setToolTipText("Apply the retention time [min] adjustment.");
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

	private void validate() {

		boolean enabled = settings != null && getTimeAdjustment() != 0;
		buttonControl.get().setEnabled(enabled);
	}

	private double getTimeAdjustment() {

		try {
			return Double.parseDouble(textControl.get().getText().trim());
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	private void applyShift() {

		if(settings != null) {
			double timeAdjustment = getTimeAdjustment();
			if(timeAdjustment != 0) {
				int retentionTimeDelta = (int)(timeAdjustment * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				for(TimeRange setting : settings) {
					int start = setting.getStart() + retentionTimeDelta;
					int stop = setting.getStop() + retentionTimeDelta;
					setting.update(start, stop);
				}
				fireUpdate();
			}
		}
	}

	private void fireUpdate() {

		if(updateListener != null) {
			updateListener.update();
		}
	}
}