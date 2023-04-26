/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.comparator.TimeRangeComparator;
import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation.TimeRangeInputValidator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTimeRanges;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class TimeRangesUI extends Composite implements IExtendedPartUI {

	/*
	 * If no selection is active, then timeRange is null.
	 */
	private static final String NO_SELECTION = "--";
	//
	private Button buttonPrevious;
	private ComboViewer comboViewer;
	private Button buttonNext;
	private TimeRangeUI timeRangeUI;
	private Button buttonAdd;
	private Button buttonDelete;
	/*
	 * TimeRanges is the object, that contains a map of ranges.
	 * TimeRange is the currently selected time range.
	 */
	private TimeRanges timeRanges = null;
	private TimeRange timeRange = null;
	private TimeRangeLabels timeRangeLabels = new TimeRangeLabels();
	//
	private ITimeRangeUpdateListener updateListener = null;

	public TimeRangesUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public void update() {

		super.update();
		updateInput();
	}

	public void setTimeRangeLabels(TimeRangeLabels timeRangeLabels) {

		this.timeRangeLabels = timeRangeLabels;
	}

	public void setInput(TimeRanges timeRanges) {

		this.timeRanges = timeRanges;
		updateInput();
	}

	public void select(TimeRange timeRange) {

		this.timeRange = timeRange;
		Combo combo = comboViewer.getCombo();
		//
		if(timeRange != null) {
			String[] items = combo.getItems();
			exitloop:
			for(int i = 0; i < items.length; i++) {
				if(items[i].equals(timeRange.getIdentifier())) {
					combo.select(i);
					break exitloop;
				}
			}
		} else {
			combo.select(0);
		}
		//
		updateTimeRange(timeRange, false);
	}

	public void setUpdateListener(ITimeRangeUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(8, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		buttonPrevious = createButtonPrevious(this);
		comboViewer = createComboViewer(this);
		buttonNext = createButtonNext(this);
		timeRangeUI = createTimeRangeUI(this);
		createSeparator(this);
		buttonAdd = createButtonAdd(this);
		buttonDelete = createButtonDelete(this);
		createButtonSettings(this);
		//
		initialize();
	}

	private void initialize() {

		updateComboViewer();
	}

	private Button createButtonPrevious(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Previous");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_BACKWARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				TimeRange timeRangePrevious = TimeRangeSupport.getTimeRangePrevious(timeRanges, timeRange);
				if(timeRangePrevious != null) {
					timeRange = timeRangePrevious;
					updateTimeRange(timeRange, true);
				}
			}
		});
		//
		return button;
	}

	private ComboViewer createComboViewer(Composite composite) {

		ComboViewer comboViewer = new EnhancedComboViewer(composite, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof TimeRange) {
					TimeRange timeRange = (TimeRange)element;
					return timeRange.getIdentifier();
				} else if(element instanceof String) {
					return (String)element;
				}
				return null;
			}
		});
		/*
		 * Select the item.
		 */
		combo.setToolTipText("Select");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof TimeRange) {
					timeRange = (TimeRange)object;
					updateTimeRange(timeRange, true);
				} else {
					timeRange = null; // NO_SELECTION
					updateTimeRange(timeRange, true);
				}
			}
		});
		//
		return comboViewer;
	}

	private Button createButtonNext(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Next");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_FORWARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				TimeRange timeRangeNext = TimeRangeSupport.getTimeRangeNext(timeRanges, timeRange);
				if(timeRangeNext != null) {
					timeRange = timeRangeNext;
					updateTimeRange(timeRange, true);
				}
			}
		});
		//
		return button;
	}

	private TimeRangeUI createTimeRangeUI(Composite parent) {

		TimeRangeUI timeRangeUI = new TimeRangeUI(parent, SWT.NONE);
		timeRangeUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		timeRangeUI.setUpdateListener(new ITimeRangeUpdateListener() {

			@Override
			public void update(TimeRange timeRange) {

				updateTimeRange(timeRange, true);
			}
		});
		//
		return timeRangeUI;
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(timeRanges != null) {
					InputDialog dialog = new InputDialog(e.display.getActiveShell(), timeRangeLabels.getTitle(), timeRangeLabels.getCreateMessage(), timeRangeLabels.getCreateInitialValue(), new TimeRangeInputValidator(timeRanges.keySet(), timeRangeLabels));
					if(IDialogConstants.OK_ID == dialog.open()) {
						String item = dialog.getValue();
						TimeRange timeRangeNew = timeRanges.extractTimeRange(item);
						if(timeRangeNew != null) {
							timeRanges.add(timeRangeNew);
							timeRange = timeRangeNew;
							updateComboViewer();
							updateTimeRange(timeRangeNew, true);
						}
					}
				}
			}
		});
		//
		return button;
	}

	private Label createSeparator(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("|");
		label.setForeground(Colors.GRAY);
		//
		return label;
	}

	private Button createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), timeRangeLabels.getTitle(), timeRangeLabels.getDeleteMessage())) {
					Object object = comboViewer.getStructuredSelection().getFirstElement();
					if(object instanceof TimeRange) {
						timeRanges.remove((TimeRange)object);
						updateComboViewer();
						updateTimeRange(timeRange, true);
					}
				}
			}
		});
		//
		return button;
	}

	private void createButtonSettings(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageTimeRanges.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void applySettings() {

		updateLabels();
	}

	private void updateLabels() {

		timeRangeUI.update();
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
		if(parent != null && !parent.isDisposed()) {
			parent.layout(true);
			parent.redraw();
		}
	}

	private void updateComboViewer() {

		timeRange = null;
		if(timeRanges != null) {
			/*
			 * Sort
			 */
			buttonPrevious.setEnabled(true);
			buttonAdd.setEnabled(true);
			buttonNext.setEnabled(true);
			/*
			 * Create the sorted ranges and add the no selection
			 * entry at the beginning.
			 */
			List<TimeRange> timeRangesSorted = new ArrayList<>(timeRanges.values());
			Collections.sort(timeRangesSorted, new TimeRangeComparator());
			List<Object> timeRangesInput = new ArrayList<Object>();
			timeRangesInput.add(NO_SELECTION); // "No Selection"
			timeRangesInput.addAll(timeRangesSorted);
			//
			Combo combo = comboViewer.getCombo();
			String currentSelection = combo.getText();
			comboViewer.setInput(timeRangesInput);
			//
			int index = 0;
			if(combo.getItemCount() > 1) {
				buttonDelete.setEnabled(true);
				for(int i = 0; i < timeRangesSorted.size(); i++) {
					TimeRange timeRangeSorted = timeRangesSorted.get(i);
					if(timeRangeSorted.getIdentifier().equals(currentSelection)) {
						timeRange = timeRangeSorted;
						index = i + 1; // + 1 because the first entry is "No Selection"
					}
				}
			} else {
				buttonDelete.setEnabled(false);
			}
			combo.select(index);
		} else {
			buttonPrevious.setEnabled(false);
			buttonNext.setEnabled(false);
			buttonAdd.setEnabled(false);
			buttonDelete.setEnabled(false);
			comboViewer.setInput(null);
		}
	}

	private void updateInput() {

		updateComboViewer();
		updateTimeRange(timeRange, true);
		updateLabels();
	}

	private void updateTimeRange(TimeRange timeRange, boolean fireUpdate) {

		/*
		 * The time spinner act independently.
		 * If one range is modified, all other ranges
		 * shall be updated.
		 */
		timeRangeUI.setInput(timeRange);
		buttonDelete.setEnabled(timeRange != null);
		/*
		 * To prevent update cycle dead loops.
		 */
		if(fireUpdate) {
			fireUpdate(timeRange);
		}
	}

	private void fireUpdate(TimeRange timeRange) {

		if(updateListener != null) {
			updateListener.update(timeRange);
		}
	}
}