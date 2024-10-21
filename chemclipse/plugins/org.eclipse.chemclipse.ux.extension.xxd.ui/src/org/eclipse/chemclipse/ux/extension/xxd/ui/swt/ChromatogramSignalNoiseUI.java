/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
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

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.INoiseCalculator;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swtchart.extensions.preferences.PreferencePage;

public class ChromatogramSignalNoiseUI extends Composite implements IExtendedPartUI {

	private AtomicReference<Button> buttonToolbarInfo = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private AtomicReference<Text> textDetails = new AtomicReference<>();
	//
	private IChromatogramSelection<?, ?> chromatogramSelection = null;
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0000");

	public ChromatogramSignalNoiseUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(IChromatogramSelection<?, ?> chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
		updateInput();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createInspectorUI(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo.get(), IMAGE_INFO, TOOLTIP_INFO, true);
	}

	private Composite createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));
		//
		createButtonToggleToolbar(composite);
		createSettingsButton(composite);
		//
		return composite;
	}

	private void createButtonToggleToolbar(Composite parent) {

		buttonToolbarInfo.set(createButtonToggleToolbar(parent, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO));
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createInspectorUI(Composite parent) {

		Text text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		textDetails.set(text);
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePage.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void applySettings() {

		updateInput();
	}

	private void updateInput() {

		toolbarInfo.get().setText("");
		textDetails.get().setText("");
		//
		if(chromatogramSelection != null) {
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram != null) {
				toolbarInfo.get().setText(ChromatogramDataSupport.getChromatogramLabel(chromatogram));
				INoiseCalculator noiseCalculator = getNoiseCalculator(chromatogram);
				if(noiseCalculator != null) {
					int intensity = 10000;
					float signalToNoiseRatio = chromatogram.getSignalToNoiseRatio(intensity);
					StringBuilder builder = new StringBuilder();
					/*
					 * Noise Factor
					 */
					builder.append("Noise Factor: ");
					builder.append(decimalFormat.format(noiseCalculator.getNoiseFactor()));
					builder.append("\n");
					/*
					 * S/N Example
					 */
					builder.append("S/N (");
					builder.append(Integer.toString(intensity));
					builder.append("): ");
					builder.append(decimalFormat.format(signalToNoiseRatio));
					//
					textDetails.get().setText(builder.toString());
				} else {
					textDetails.get().setText("The chromatogram offers no noise calculation yet.");
				}
			}
		}
	}

	private INoiseCalculator getNoiseCalculator(IChromatogram<?> chromatogram) {

		if(chromatogram instanceof IChromatogramCSD chromatogramCSD) {
			return chromatogramCSD.getNoiseCalculator();
		} else if(chromatogram instanceof IChromatogramMSD chromatogramMSD) {
			return chromatogramMSD.getNoiseCalculator();
		} else if(chromatogram instanceof IChromatogramWSD chromatogramWSD) {
			return chromatogramWSD.getNoiseCalculator();
		} else {
			return null;
		}
	}
}