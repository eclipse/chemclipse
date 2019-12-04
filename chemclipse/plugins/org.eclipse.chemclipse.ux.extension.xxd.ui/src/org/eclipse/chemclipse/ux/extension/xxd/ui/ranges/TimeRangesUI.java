/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.TimeRangeInputValidator;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TimeRangesUI extends Composite {

	private ComboViewer comboViewer;
	private Text textStart;
	private Text textCenter;
	private Text textStop;
	private Button buttonAdd;
	private Button buttonDelete;
	/*
	 * TimeRanges is the object, that contains a map of ranges.
	 * TimeRange is the currently selected time range.
	 */
	private TimeRanges timeRanges = null;
	private TimeRange timeRange = null;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();
	//
	private IUpdateListener updateListener = null;

	public TimeRangesUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void setInput(TimeRanges timeRanges) {

		this.timeRanges = timeRanges;
		updateInput();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	/**
	 * Updates the values of the currently selected time range.
	 */
	public void update() {

		updateTimeRange(timeRange);
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(6, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		comboViewer = createComboViewer(this);
		textStart = createText(this, TimeRange.Marker.START);
		textCenter = createText(this, TimeRange.Marker.CENTER);
		textStop = createText(this, TimeRange.Marker.STOP);
		buttonAdd = createButtonAdd(this);
		buttonDelete = createButtonDelete(this);
	}

	private ComboViewer createComboViewer(Composite composite) {

		ComboViewer comboViewer = new ComboViewer(composite, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof TimeRange) {
					TimeRange timeRange = (TimeRange)element;
					return timeRange.getIdentifier();
				}
				return null;
			}
		});
		/*
		 * Select the item.
		 */
		combo.setToolTipText("Select an identifier.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof TimeRange) {
					TimeRange timeRange = (TimeRange)object;
					updateTimeRange(timeRange);
				}
			}
		});
		//
		return comboViewer;
	}

	private Text createText(Composite composite, TimeRange.Marker marker) {

		Text text = new Text(composite, SWT.BORDER);
		text.setText("");
		switch(marker) {
			case START:
				text.setToolTipText("Start Retention Time");
				break;
			case CENTER:
				text.setToolTipText("Center Retention Time");
				break;
			case STOP:
				text.setToolTipText("Stop Retention Time");
				break;
		}
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		TimeRangeValidator validator = new TimeRangeValidator(marker);
		ControlDecoration controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		//
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				super.keyReleased(e);
				if(timeRange != null) {
					validator.setTimeRange(timeRange);
					if(validate(validator, controlDecoration, text)) {
						switch(marker) {
							case START:
								timeRange.updateStart(validator.getRetentionTime());
								break;
							case CENTER:
								timeRange.updateCenter(validator.getRetentionTime());
								break;
							case STOP:
								timeRange.updateStop(validator.getRetentionTime());
								break;
						}
						//
						fireUpdate();
					}
				}
			}
		});
		//
		return text;
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add a new time range.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(timeRanges != null) {
					InputDialog dialog = new InputDialog(e.display.getActiveShell(), "Time Range", "Create a new time range.", "C10 | 10.2 | 10.4 | 10.6", new TimeRangeInputValidator(timeRanges.keySet()));
					if(IDialogConstants.OK_ID == dialog.open()) {
						String item = dialog.getValue();
						TimeRange timeRange = timeRanges.extractTimeRange(item);
						if(timeRange != null) {
							timeRanges.add(timeRange);
							updateInput();
							comboViewer.getCombo().setText(timeRange.getIdentifier());
							updateTimeRange(timeRange);
						}
					}
				}
			}
		});
		return button;
	}

	private Button createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete the selected time range.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), "Time Range", "Would you like to delete the selected time range?")) {
					Object object = comboViewer.getStructuredSelection().getFirstElement();
					if(object instanceof TimeRange) {
						timeRanges.remove((TimeRange)object);
						updateInput();
					}
				}
			}
		});
		return button;
	}

	private void updateInput() {

		timeRange = null;
		if(timeRanges != null) {
			/*
			 * Sort
			 */
			buttonAdd.setEnabled(true);
			List<TimeRange> ranges = new ArrayList<>(timeRanges.values());
			Collections.sort(ranges, new TimeRangeComparator());
			//
			Combo combo = comboViewer.getCombo();
			int selectionIndex = combo.getSelectionIndex();
			comboViewer.setInput(ranges);
			//
			if(combo.getItemCount() > 0) {
				int index = (selectionIndex >= 0 && selectionIndex < combo.getItemCount()) ? selectionIndex : 0;
				combo.select(index);
				timeRange = ranges.get(index);
			}
		} else {
			buttonAdd.setEnabled(false);
			comboViewer.setInput(null);
		}
		//
		updateTimeRange(timeRange);
	}

	private void updateTimeRange(TimeRange timeRange) {

		boolean enabled = false;
		String start = "";
		String center = "";
		String stop = "";
		//
		if(timeRange != null) {
			start = getRetentionTimeMinutes(timeRange.getStart());
			center = getRetentionTimeMinutes(timeRange.getCenter());
			stop = getRetentionTimeMinutes(timeRange.getStop());
			enabled = true;
		}
		//
		textStart.setText(start);
		textCenter.setText(center);
		textStop.setText(stop);
		//
		textStart.setEnabled(enabled);
		textCenter.setEnabled(enabled);
		textStop.setEnabled(enabled);
		buttonDelete.setEnabled(enabled);
	}

	private String getRetentionTimeMinutes(int milliseconds) {

		return decimalFormat.format(milliseconds / TimeRange.MINUTE_FACTOR);
	}

	private boolean validate(IValidator validator, ControlDecoration controlDecoration, Text text) {

		IStatus status = validator.validate(text.getText());
		if(status.isOK()) {
			controlDecoration.hide();
			return true;
		} else {
			controlDecoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());
			controlDecoration.showHoverText(status.getMessage());
			controlDecoration.show();
			return false;
		}
	}

	private void fireUpdate() {

		if(updateListener != null) {
			updateListener.update();
		}
	}
}
