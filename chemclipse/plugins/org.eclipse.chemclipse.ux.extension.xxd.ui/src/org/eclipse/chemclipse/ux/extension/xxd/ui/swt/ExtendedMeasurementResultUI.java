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

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramDataSupport;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ExtendedMeasurementResultUI {

	private Label labelChromatogramInfo;
	private Label labelMeasurementResultInfo;
	private Composite toolbarChromatogramInfo;
	private Composite toolbarMeasurementResultInfo;
	private ComboViewer comboMeasurementResults;
	private ScanTableUI scanTableUI;
	//
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();

	@Inject
	public ExtendedMeasurementResultUI(Composite parent) {
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updateMeasurementResult();
	}

	@SuppressWarnings("rawtypes")
	public void update(Object object) {

		labelChromatogramInfo.setText("");
		labelMeasurementResultInfo.setText("");
		//
		if(object instanceof IChromatogramSelection) {
			IChromatogramSelection chromatogramSelection = (IChromatogramSelection)object;
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			labelChromatogramInfo.setText(chromatogramDataSupport.getChromatogramLabel(chromatogram));
			comboMeasurementResults.setInput(chromatogram.getMeasurementResults());
		}
		//
		updateMeasurementResult();
	}

	private void updateMeasurementResult() {

		updateMeasurementResultData();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarChromatogramInfo = createToolbarChromatogramInfo(parent);
		toolbarMeasurementResultInfo = createToolbarMeasurementResultInfo(parent);
		createResultSection(parent);
		//
		PartSupport.setCompositeVisibility(toolbarChromatogramInfo, true);
		PartSupport.setCompositeVisibility(toolbarMeasurementResultInfo, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createButtonToggleChromatogramToolbarInfo(composite);
		comboMeasurementResults = createResultCombo(composite);
		createButtonToggleMeasurementResultToolbarInfo(composite);
	}

	private Composite createToolbarChromatogramInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelChromatogramInfo = new Label(composite, SWT.NONE);
		labelChromatogramInfo.setText("");
		labelChromatogramInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarMeasurementResultInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelMeasurementResultInfo = new Label(composite, SWT.NONE);
		labelMeasurementResultInfo.setText("");
		labelMeasurementResultInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private void createResultSection(Composite parent) {

		scanTableUI = new ScanTableUI(parent, SWT.VIRTUAL | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		scanTableUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private Button createButtonToggleChromatogramToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarChromatogramInfo);
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

	private Button createButtonToggleMeasurementResultToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle measurement results description toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MEASUREMENT_RESULTS_DEFAULT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarMeasurementResultInfo);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MEASUREMENT_RESULTS_DEFAULT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MEASUREMENT_RESULTS_DEFAULT, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private ComboViewer createResultCombo(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.PUSH);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new ILabelProvider() {

			@Override
			public void removeListener(ILabelProviderListener listener) {

			}

			@Override
			public boolean isLabelProperty(Object element, String property) {

				return false;
			}

			@Override
			public void dispose() {

			}

			@Override
			public void addListener(ILabelProviderListener listener) {

			}

			@Override
			public String getText(Object element) {

				if(element instanceof IMeasurementResult) {
					IMeasurementResult measurementResult = (IMeasurementResult)element;
					return measurementResult.getName();
				}
				return null;
			}

			@Override
			public Image getImage(Object element) {

				return null;
			}
		});
		comboViewer.getCombo().setToolTipText("Show the available measurement results.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		comboViewer.getCombo().setLayoutData(gridData);
		comboViewer.getCombo().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof IMeasurementResult) {
					IMeasurementResult measurementResult = (IMeasurementResult)object;
					labelMeasurementResultInfo.setText(measurementResult.getDescription());
				}
			}
		});
		return comboViewer;
	}

	private void updateMeasurementResultData() {

	}
}
