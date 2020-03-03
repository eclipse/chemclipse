/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.comparator.TimeRangeComparator;
import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.TimeRangeInputValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTimeRanges;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class TimeRangesUI extends Composite {

	private ComboViewer comboViewer;
	private TimeSpinner timeSpinnerStart;
	private TimeSpinner timeSpinnerCenter;
	private TimeSpinner timeSpinnerStop;
	private Button buttonAdd;
	private Button buttonDelete;
	private Button buttonSettings;
	/*
	 * TimeRanges is the object, that contains a map of ranges.
	 * TimeRange is the currently selected time range.
	 */
	private TimeRanges timeRanges = null;
	private TimeRange timeRange = null;
	//
	private IUpdateListener updateListener = null;

	public TimeRangesUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void setInput(TimeRanges timeRanges) {

		this.timeRanges = timeRanges;
		updateInput();
		updateLabels();
	}

	/**
	 * Updates the values of the currently selected time range.
	 */
	public void update() {

		super.update();
		updateTimeRange();
		updateLabels();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public String[] getItems() {

		return comboViewer.getCombo().getItems();
	}

	public void select(int index) {

		if(index >= 0 && index < getItems().length) {
			comboViewer.getCombo().select(index);
			Object object = comboViewer.getStructuredSelection().getFirstElement();
			if(object instanceof TimeRange) {
				timeRange = (TimeRange)object;
				updateTimeRange();
			}
		}
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(8, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		comboViewer = createComboViewer(this);
		timeSpinnerStart = createSpinner(this, TimeRange.Marker.START);
		timeSpinnerCenter = createSpinner(this, TimeRange.Marker.CENTER);
		timeSpinnerStop = createSpinner(this, TimeRange.Marker.STOP);
		createSeparator(this);
		buttonAdd = createButtonAdd(this);
		buttonDelete = createButtonDelete(this);
		buttonSettings = createButtonSettings(this);
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
		combo.setToolTipText("Select a time range.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof TimeRange) {
					timeRange = (TimeRange)object;
					updateTimeRange();
				}
			}
		});
		//
		return comboViewer;
	}

	private TimeSpinner createSpinner(Composite composite, TimeRange.Marker marker) {

		TimeSpinner timeSpinner = new TimeSpinner(composite, SWT.NONE, marker);
		timeSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		timeSpinner.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				fireUpdate();
			}
		});
		return timeSpinner;
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
						TimeRange timeRangeNew = timeRanges.extractTimeRange(item);
						if(timeRangeNew != null) {
							timeRanges.add(timeRangeNew);
							updateInput();
							comboViewer.getCombo().setText(timeRangeNew.getIdentifier());
							timeRange = timeRangeNew;
							fireUpdate();
						}
					}
				}
			}
		});
		return button;
	}

	private Label createSeparator(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("|");
		label.setForeground(Colors.GRAY);
		return label;
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
						fireUpdate();
					}
				}
			}
		});
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

				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", new PreferencePageTimeRanges()));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
		return button;
	}

	private void applySettings() {

		updateLabels();
	}

	private void updateLabels() {

		timeSpinnerStart.update();
		timeSpinnerCenter.update();
		timeSpinnerStop.update();
		/*
		 * Layout the outer composites to
		 * enable more space for the labels.
		 */
		Composite parent = getParent();
		if(parent != null) {
			if(getParent() != null) {
				parent = getParent();
			}
		}
		//
		if(parent != null) {
			parent.layout(true);
			parent.redraw();
		}
	}

	private void updateInput() {

		timeRange = null;
		if(timeRanges != null) {
			/*
			 * Sort
			 */
			buttonAdd.setEnabled(true);
			//
			List<TimeRange> ranges = new ArrayList<>(timeRanges.values());
			Collections.sort(ranges, new TimeRangeComparator());
			//
			Combo combo = comboViewer.getCombo();
			int selectionIndex = combo.getSelectionIndex();
			comboViewer.setInput(ranges);
			//
			if(combo.getItemCount() > 0) {
				buttonDelete.setEnabled(true);
				int index = (selectionIndex >= 0 && selectionIndex < combo.getItemCount()) ? selectionIndex : 0;
				combo.select(index);
				timeRange = ranges.get(index);
			} else {
				buttonDelete.setEnabled(false);
			}
		} else {
			buttonAdd.setEnabled(false);
			buttonDelete.setEnabled(false);
			comboViewer.setInput(null);
		}
		//
		updateTimeRange();
	}

	private void updateTimeRange() {

		timeSpinnerStart.update(timeRange);
		timeSpinnerCenter.update(timeRange);
		timeSpinnerStop.update(timeRange);
		buttonDelete.setEnabled(timeRange != null);
		buttonSettings.setEnabled(true);
	}

	private void fireUpdate() {

		if(updateListener != null) {
			/*
			 * The time spinner act independently.
			 * If one range is modified, all other ranges
			 * shall be updated.
			 */
			updateTimeRange();
			/*
			 * Inform all other listeners.
			 */
			getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {

					updateListener.update();
				}
			});
		}
	}
}
