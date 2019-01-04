/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.pcr.model.comparators.DetectionFormatComparator;
import org.eclipse.chemclipse.pcr.model.core.IChannelSpecification;
import org.eclipse.chemclipse.pcr.model.core.IDetectionFormat;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePCR;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
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

public class ExtendedPlateDataUI {

	private Label labelInfo;
	private Composite toolbarInfo;
	private ComboViewer comboDetectionFormats;
	private ComboViewer comboChannelSpecifications;
	private ChannelSpecificationListUI channelSpecificationListUI;
	//
	private DetectionFormatComparator detectionFormatComparator = new DetectionFormatComparator();
	private IPlate plate = null;

	@Inject
	public ExtendedPlateDataUI(Composite parent) {
		initialize(parent);
	}

	public void update(IPlate plate) {

		this.plate = plate;
		if(plate != null) {
			labelInfo.setText("Plate: " + plate.getName());
		} else {
			labelInfo.setText("No plate data available.");
		}
		//
		updateDetectionFormats(plate);
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		comboDetectionFormats = createComboDetectionFormats(parent);
		comboChannelSpecifications = createComboChannelSpecifications(parent);
		channelSpecificationListUI = createChannelSpecificationTable(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, true);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createButtonToggleToolbarInfo(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private Button createButtonToggleToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfo);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
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
					updateDetectionFormats(plate);
				}
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = new PreferencePagePCR();
				preferencePage.setTitle("PCR");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						//
					} catch(Exception e1) {
						System.out.println(e1);
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfo = new Label(composite, SWT.NONE);
		labelInfo.setText("");
		labelInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
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

	private void updateDetectionFormats(IPlate plate) {

		IDetectionFormat detectionFormat;
		//
		if(plate != null) {
			List<IDetectionFormat> detectionFormats = new ArrayList<>(plate.getDetectionFormats());
			Collections.sort(detectionFormats, detectionFormatComparator);
			comboDetectionFormats.setInput(detectionFormats);
			detectionFormat = plate.getDetectionFormat();
		} else {
			comboDetectionFormats.setInput(null);
			detectionFormat = null;
		}
		//
		if(detectionFormat != null) {
			comboDetectionFormats.getCombo().setText(detectionFormat.getName());
		}
		updateChannelSpecification(detectionFormat);
	}

	private void updateChannelSpecification(IDetectionFormat detectionFormat) {

		IChannelSpecification channelSpecification = null;
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
