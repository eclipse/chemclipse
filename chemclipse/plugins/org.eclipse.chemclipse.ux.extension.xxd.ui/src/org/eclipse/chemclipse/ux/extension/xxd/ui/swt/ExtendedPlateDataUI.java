/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.pcr.model.comparators.DetectionFormatComparator;
import org.eclipse.chemclipse.pcr.model.core.IChannelSpecification;
import org.eclipse.chemclipse.pcr.model.core.IDetectionFormat;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePCR;
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

public class ExtendedPlateDataUI extends Composite implements IExtendedPartUI {

	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private ComboViewer comboDetectionFormats;
	private ComboViewer comboChannelSpecifications;
	private ChannelSpecificationListUI channelSpecificationListUI;
	//
	private DetectionFormatComparator detectionFormatComparator = new DetectionFormatComparator();
	private IPlate plate = null;

	public ExtendedPlateDataUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(IPlate plate) {

		this.plate = plate;
		updateLabel();
		updateDetectionFormats();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		comboDetectionFormats = createComboDetectionFormats(this);
		comboChannelSpecifications = createComboChannelSpecifications(this);
		channelSpecificationListUI = createChannelSpecificationTable(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IMAGE_INFO, TOOLTIP_INFO, true);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(plate != null) {
					plate.setDetectionFormat(null);
					updateDetectionFormats();
				}
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePagePCR.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void applySettings() {

	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private ComboViewer createComboDetectionFormats(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ListContentProvider());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IDetectionFormat) {
					IDetectionFormat detectionFormat = (IDetectionFormat)element;
					return detectionFormat.getName();
				}
				return null;
			}
		});
		combo.setToolTipText("Select a detection format and apply it.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(plate != null) {
					Object object = comboViewer.getStructuredSelection().getFirstElement();
					if(object instanceof IDetectionFormat) {
						IDetectionFormat detectionFormat = (IDetectionFormat)object;
						plate.setDetectionFormat(detectionFormat);
						updateChannelSpecification(detectionFormat);
					}
				}
			}
		});
		//
		return comboViewer;
	}

	private ComboViewer createComboChannelSpecifications(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ListContentProvider());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IChannelSpecification) {
					IChannelSpecification channelSpecification = (IChannelSpecification)element;
					return channelSpecification.getName();
				}
				return null;
			}
		});
		combo.setToolTipText("Select a channel specification.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(plate != null) {
					Object object = comboViewer.getStructuredSelection().getFirstElement();
					if(object instanceof IChannelSpecification) {
						IChannelSpecification channelSpecification = (IChannelSpecification)object;
						channelSpecificationListUI.setInput(channelSpecification);
					}
				}
			}
		});
		//
		return comboViewer;
	}

	private ChannelSpecificationListUI createChannelSpecificationTable(Composite parent) {

		ChannelSpecificationListUI listUI = new ChannelSpecificationListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		listUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		return listUI;
	}

	private void updateLabel() {

		toolbarInfo.get().setText(plate != null ? plate.getName() : "--");
	}

	private void updateDetectionFormats() {

		comboDetectionFormats.setInput(null);
		updateChannelSpecification(null);
		//
		if(plate != null) {
			/*
			 * Available Formats
			 */
			List<IDetectionFormat> detectionFormats = new ArrayList<>(plate.getDetectionFormats());
			Collections.sort(detectionFormats, detectionFormatComparator);
			comboDetectionFormats.setInput(detectionFormats);
			/*
			 * Selected Format
			 */
			IDetectionFormat detectionFormat = plate.getDetectionFormat();
			if(detectionFormat != null) {
				String[] items = comboDetectionFormats.getCombo().getItems();
				exitloop:
				for(int i = 0; i < items.length; i++) {
					if(items[i].equals(detectionFormat.getName())) {
						comboDetectionFormats.getCombo().select(i);
						updateChannelSpecification(detectionFormat);
						break exitloop;
					}
				}
			}
		}
	}

	private void updateChannelSpecification(IDetectionFormat detectionFormat) {

		IChannelSpecification channelSpecification = null;
		comboChannelSpecifications.setInput(null);
		//
		if(detectionFormat != null) {
			List<IChannelSpecification> channelSpecifications = detectionFormat.getChannelSpecifications();
			comboChannelSpecifications.setInput(channelSpecifications);
			if(channelSpecifications.size() > 0) {
				comboChannelSpecifications.getCombo().select(0);
				channelSpecification = channelSpecifications.get(0);
			}
		}
		//
		channelSpecificationListUI.setInput(channelSpecification);
	}
}
