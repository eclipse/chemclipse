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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePCR;
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
import org.eclipse.swt.widgets.Text;

public class ExtendedWellChannelsUI extends Composite implements IExtendedPartUI {

	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private Combo comboChannels;
	//
	private Text textId;
	private Text textDetectionName;
	private Text textName;
	private Text textTime;
	private Text textTemperature;
	private Text textCrossingPoint;
	//
	private IWell well = null;

	@Inject
	public ExtendedWellChannelsUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(IWell well) {

		this.well = well;
		updateWidgets();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		comboChannels = createComboChannels(this);
		createChannelDataSection(this);
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

		createSettingsButton(parent, Arrays.asList(PreferencePagePCR.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				updateWidgets();
			}
		});
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
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
		createLabel(composite, "ID:");
		textId = createText(composite);
		//
		createLabel(composite, "Detection Name:");
		textDetectionName = createText(composite);
		//
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

		updateChannelCombo();
		updateLabel();
		updateChannelData();
	}

	private void updateChannelData() {

		textId.setText("");
		textDetectionName.setText("");
		textName.setText("");
		textTime.setText("");
		textTemperature.setText("");
		textCrossingPoint.setText("");
		//
		if(well != null) {
			int index = comboChannels.getSelectionIndex();
			if(index >= 0) {
				IChannel channel = well.getChannels().get(index);
				if(channel != null) {
					textId.setText(Integer.toString(channel.getId()));
					textDetectionName.setText(channel.getDetectionName());
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
	}

	private void updateChannelCombo() {

		if(well != null) {
			if(well.isEmptyMeasurement()) {
				comboChannels.setItems(new String[]{});
			} else {
				comboChannels.setItems(getComboItems(well));
				IChannel channel = well.getActiveChannel();
				if(channel != null) {
					String name = channel.getDetectionName();
					String[] items = comboChannels.getItems();
					exitloop:
					for(int i = 0; i < items.length; i++) {
						if(items[i].equals(name)) {
							comboChannels.select(i);
							break exitloop;
						}
					}
				} else {
					comboChannels.select(0);
				}
			}
		} else {
			comboChannels.setItems(new String[]{""});
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

	private void updateLabel() {

		toolbarInfo.get().setText(well != null ? well.getLabel() : "--");
	}
}
