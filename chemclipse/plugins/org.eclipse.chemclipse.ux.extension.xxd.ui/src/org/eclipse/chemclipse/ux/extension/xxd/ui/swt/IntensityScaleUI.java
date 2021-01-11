/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;

public class IntensityScaleUI extends Composite {

	private Spinner spinner;
	private Scale scale;
	//
	private String tooltip = "Value";
	private int selection = 0;
	//
	private IScaleUpdateListener updateListener;
	private ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(2));

	public interface IScaleUpdateListener {

		void update(int selection);
	}

	public IntensityScaleUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public void setToolTipText(String string) {

		this.tooltip = string;
	}

	@Override
	public String getToolTipText() {

		return tooltip;
	}

	public void setUpdateListener(IScaleUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void setMinimum(int value) {

		spinner.setMinimum(value);
		scale.setMinimum(value);
		setPageIncrement();
	}

	public void setMaximum(int value) {

		spinner.setMaximum(value);
		scale.setMaximum(value);
		setPageIncrement();
	}

	public void setSelection(int value) {

		this.selection = value;
		String tooltip = getToolTipText();
		//
		spinner.setSelection(value);
		spinner.setToolTipText(tooltip);
		//
		scale.setSelection(value);
		scale.setToolTipText(tooltip);
	}

	public int getSelection() {

		return selection;
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
		spinner = createSpinner(composite);
		scale = createScale(composite);
		//
		setSelection(1);
	}

	private Spinner createSpinner(Composite parent) {

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setMinimum(1);
		spinner.setMaximum(1);
		spinner.setPageIncrement(1);
		spinner.setSelection(1);
		spinner.setToolTipText("");
		GridData gridData = new GridData();
		gridData.widthHint = 80;
		spinner.setLayoutData(gridData);
		//
		spinner.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					updateScale(e.display, spinner.getSelection());
				}
			}
		});
		//
		spinner.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				if(e.button == 1) {
					updateScale(e.display, spinner.getSelection());
				}
			}
		});
		//
		return spinner;
	}

	private Scale createScale(Composite parent) {

		Scale scale = new Scale(parent, SWT.NONE);
		scale.setMinimum(1);
		scale.setMaximum(1);
		scale.setPageIncrement(1);
		scale.setSelection(1);
		scale.setToolTipText("");
		scale.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		scale.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateScale(e.display, scale.getSelection());
			}
		});
		//
		return scale;
	}

	private void setPageIncrement() {

		int scaleValue = (scale.getMaximum() - scale.getMinimum() + 1) / 20;
		//
		spinner.setPageIncrement(1);
		scale.setPageIncrement(scaleValue >= 1 ? scaleValue : 1);
	}

	private void updateScale(Display display, int selection) {

		setSelection(selection);
		//
		if(updateListener != null) {
			try {
				executorService.execute(new Runnable() {

					@Override
					public void run() {

						display.asyncExec(new Runnable() {

							@Override
							public void run() {

								updateListener.update(selection);
							}
						});
					}
				});
			} catch(RejectedExecutionException e) {
				/*
				 * Updates are already waiting for execution.
				 * Don't update too often, otherwise the performance would lack.
				 */
			}
		}
	}
}
