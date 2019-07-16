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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Collection;

import javax.inject.Inject;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePCR;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
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
import org.eclipse.swt.widgets.Text;

public class ExtendedWellChannelsUI {

	private Label labelInfo;
	private Composite toolbarInfo;
	private Combo comboChannels;
	private Text textName;
	private Text textTime;
	private Text textTemperature;
	private Text textCrossingPoint;
	//
	private IWell well = null;

	@Inject
	public ExtendedWellChannelsUI(Composite parent) {
		initialize(parent);
	}

	public void update(IWell well) {

		this.well = well;
		updateWidgets();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		comboChannels = createComboChannels(parent);
		createChannelDataSection(parent);
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
		button.setToolTipText("Reset the Table");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateWidgets();
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
						updateWidgets();
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

	private Combo createComboChannels(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateChannelData();
			}
		});
		//
		return combo;
	}

	private void createChannelDataSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Data
		 */
		createLabel(composite, "Name:");
		textName = createText(composite);
		//
		createLabel(composite, "Time:");
		textTime = createText(composite);
		//
		createLabel(composite, "Temperature:");
		textTemperature = createText(composite);
		//
		createLabel(composite, "Crossing Point:");
		textCrossingPoint = createText(composite);
	}

	private void createLabel(Composite parent, String content) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(content);
	}

	private Text createText(Composite parent) {

		Text text = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		text.setText("");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

	private void updateWidgets() {

		updateComboItems();
		updateLabel();
		updateChannelData();
	}

	private void updateChannelData() {

		textName.setText("");
		textTime.setText("");
		textTemperature.setText("");
		textCrossingPoint.setText("");
		//
		if(well != null) {
			int index = comboChannels.getSelectionIndex();
			if(index >= 0) {
				IChannel channel = well.getChannels().get(index);
				textName.setText(channel.getName());
				textTime.setText(Integer.toString(channel.getTime()));
				textTemperature.setText(Double.toString(channel.getTemperature()));
				IPoint crossingPoint = channel.getCrossingPoint();
				if(crossingPoint != null) {
					textCrossingPoint.setText(Double.toString(crossingPoint.getX()));
				}
			}
		}
	}

	private void updateComboItems() {

		if(well.isEmptyMeasurement()) {
			comboChannels.setItems(new String[]{});
		} else {
			String[] items = getComboItems(well);
			comboChannels.setItems(items);
			if(items.length >= 1) {
				comboChannels.select(0);
			}
		}
	}

	private void updateLabel() {

		if(well != null) {
			labelInfo.setText(well.getLabel());
		} else {
			labelInfo.setText("");
		}
	}

	private String[] getComboItems(IWell well) {

		if(well != null) {
			Collection<IChannel> channels = well.getChannels().values();
			String[] items = new String[channels.size()];
			int i = 0;
			for(IChannel channel : channels) {
				items[i++] = channel.getDetectionName();
			}
			return items;
		} else {
			return new String[]{};
		}
	}
}
