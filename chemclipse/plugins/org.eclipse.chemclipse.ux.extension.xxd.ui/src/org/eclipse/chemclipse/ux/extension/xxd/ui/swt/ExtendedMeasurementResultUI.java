/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
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
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.ui.provider.ICellColorProvider;
import org.eclipse.chemclipse.ux.extension.ui.support.IMeasurementResultTitles;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

public class ExtendedMeasurementResultUI {

	private static final Logger logger = Logger.getLogger(ExtendedMeasurementResultUI.class);
	//
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.ux.extension.ui.measurementResultVisualization";
	private static final String ATTRIBUTE_IDENTIFIER = "Identifier";
	private static final String ATTRIBUTE_TITLES = "Titles";
	private static final String ATTRIBUTE_CONTENT_PROVIDER = "ContentProvider";
	private static final String ATTRIBUTE_LABEL_PROVIDER = "LabelProvider";
	private static final String ATTRIBUTE_COMPARATOR = "Comparator";
	private static final String ATTRIBUTE_SELECTION_LISTENER = "SelectionListener";
	private static final String ATTRIBUTE_CELL_COLOR_PROVIDER = "CellColorProvider";
	//
	private Label labelChromatogramInfo;
	private Label labelMeasurementResultInfo;
	private Composite toolbarChromatogramInfo;
	private Composite toolbarMeasurementResultInfo;
	private ComboViewer comboMeasurementResults;
	private ExtendedTableViewer extendedTableViewer;
	//
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();
	//
	private String resultProviderId = "";
	private List<IMeasurementResult> measurementResults = new ArrayList<>();
	private SelectionListener selectionListener = null;
	//
	private static final String[] DEFAULT_TITLES = {};
	private static final int[] DEFAULT_BOUNDS = {};
	private static final DefaultContentProvider DEFAULT_CONTENT_PROVIDER = new DefaultContentProvider();
	private static final DefaultLabelProvider DEFAULT_LABEL_PROVIDER = new DefaultLabelProvider();

	private static class DefaultContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {

			return null;
		}
	}

	private static class DefaultLabelProvider extends ColumnLabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {

			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {

			return "";
		}
	}

	@Inject
	public ExtendedMeasurementResultUI(Composite parent) {
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updateMeasurementResults();
	}

	public void clear() {

		labelChromatogramInfo.setText("");
		labelMeasurementResultInfo.setText("");
		resultProviderId = "";
		clearTable();
	}

	public void update(Object object) {

		try {
			fetchMeasurementResults(object);
			updateChromatogramInfo(object);
			resultProviderId = "";
			updateMeasurementResults();
		} catch(Exception e) {
			logger.warn(e);
		}
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

		extendedTableViewer = new ExtendedTableViewer(parent, SWT.VIRTUAL | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = extendedTableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
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
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IMeasurementResult) {
					IMeasurementResult measurementResult = (IMeasurementResult)element;
					return measurementResult.getName();
				}
				return null;
			}
		});
		combo.setToolTipText("Show the available measurement results.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof IMeasurementResult) {
					IMeasurementResult measurementResult = (IMeasurementResult)object;
					updateMeasurementResult(measurementResult);
				}
			}
		});
		return comboViewer;
	}

	private void updateMeasurementResults() {

		if(measurementResults.size() > 0) {
			comboMeasurementResults.setInput(measurementResults);
			int index = getMeasurementResultIndex();
			if(index >= 0) {
				IMeasurementResult measurementResult = measurementResults.get(index);
				comboMeasurementResults.getCombo().select(index);
				if(measurementResult != null) {
					labelMeasurementResultInfo.setText(measurementResult.getDescription());
					updateMeasurementResult(measurementResult);
				}
			}
		} else {
			comboMeasurementResults.setInput(null);
			clearTable();
		}
	}

	@SuppressWarnings("rawtypes")
	private void fetchMeasurementResults(Object object) {

		measurementResults.clear();
		if(object instanceof IChromatogramSelection) {
			IChromatogramSelection chromatogramSelection = (IChromatogramSelection)object;
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			measurementResults.addAll(chromatogram.getMeasurementResults());
		}
	}

	@SuppressWarnings("rawtypes")
	private void updateChromatogramInfo(Object object) {

		if(object instanceof IChromatogramSelection) {
			IChromatogramSelection chromatogramSelection = (IChromatogramSelection)object;
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			labelChromatogramInfo.setText(chromatogramDataSupport.getChromatogramLabel(chromatogram));
		} else {
			labelChromatogramInfo.setText("");
		}
	}

	private int getMeasurementResultIndex() {

		int index = -1;
		if(measurementResults != null) {
			/*
			 * If a provider has been selected already,
			 * set the combo box.
			 */
			if(resultProviderId.equals("") && measurementResults.size() > 0) {
				index = 0;
			} else {
				exitloop:
				for(int i = 0; i < measurementResults.size(); i++) {
					if(resultProviderId.equals(measurementResults.get(i).getIdentifier())) {
						index = i;
						break exitloop;
					}
				}
			}
		}
		return index;
	}

	private void updateMeasurementResult(IMeasurementResult measurementResult) {

		if(prepareMeasurementResultTable(measurementResult)) {
			try {
				extendedTableViewer.setInput(measurementResult);
			} catch(Exception e) {
				// TODO
			}
		}
	}

	private boolean prepareMeasurementResultTable(IMeasurementResult measurementResult) {

		boolean isContentProviderSet = false;
		if(measurementResult != null) {
			/*
			 * Get the UI provider and display the results.
			 */
			if(!resultProviderId.equals(measurementResult.getIdentifier())) {
				resultProviderId = measurementResult.getIdentifier();
				IConfigurationElement configurationElement = getMeasurementResultVisualizationProvider(resultProviderId);
				if(configurationElement != null) {
					try {
						clearTable();
						setContentProvider(configurationElement);
						setCellColorProvider(configurationElement);
						setSelectionListener(configurationElement);
						isContentProviderSet = true;
					} catch(Exception e) {
						logger.info(e);
					}
				}
			}
		} else {
			extendedTableViewer.setInput(null);
			extendedTableViewer.setContentProvider(DEFAULT_CONTENT_PROVIDER);
			extendedTableViewer.createColumns(DEFAULT_TITLES, DEFAULT_BOUNDS);
			extendedTableViewer.setLabelProvider(DEFAULT_LABEL_PROVIDER);
			extendedTableViewer.setInput(null);
		}
		//
		return isContentProviderSet;
	}

	private void clearTable() {

		try {
			extendedTableViewer.setComparator(null);
		} catch(Exception e) {
			// TODO
		}
		//
		if(extendedTableViewer.getContentProvider() == null) {
			extendedTableViewer.createColumns(DEFAULT_TITLES, DEFAULT_BOUNDS);
			extendedTableViewer.setLabelProvider(DEFAULT_LABEL_PROVIDER);
			extendedTableViewer.setContentProvider(DEFAULT_CONTENT_PROVIDER);
		} else {
			extendedTableViewer.setInput(null);
			extendedTableViewer.setContentProvider(DEFAULT_CONTENT_PROVIDER);
			extendedTableViewer.createColumns(DEFAULT_TITLES, DEFAULT_BOUNDS);
			extendedTableViewer.setLabelProvider(DEFAULT_LABEL_PROVIDER);
		}
	}

	private void setContentProvider(IConfigurationElement configurationElement) throws Exception {

		IMeasurementResultTitles titles = (IMeasurementResultTitles)configurationElement.createExecutableExtension(ATTRIBUTE_TITLES);
		IStructuredContentProvider contentProvider = (IStructuredContentProvider)configurationElement.createExecutableExtension(ATTRIBUTE_CONTENT_PROVIDER);
		ITableLabelProvider tableLabelProvider = (ITableLabelProvider)configurationElement.createExecutableExtension(ATTRIBUTE_LABEL_PROVIDER);
		ViewerComparator viewerComparator = (ViewerComparator)configurationElement.createExecutableExtension(ATTRIBUTE_COMPARATOR);
		//
		extendedTableViewer.createColumns(titles.getTitles(), titles.getBounds());
		extendedTableViewer.setLabelProvider(tableLabelProvider);
		extendedTableViewer.setContentProvider(contentProvider);
		extendedTableViewer.setComparator(viewerComparator);
	}

	private void setCellColorProvider(IConfigurationElement configurationElement) {

		/*
		 * Optional
		 */
		ICellColorProvider cellColorProvider = null;
		if(configurationElement.getAttribute(ATTRIBUTE_CELL_COLOR_PROVIDER) != null) {
			try {
				cellColorProvider = (ICellColorProvider)configurationElement.createExecutableExtension(ATTRIBUTE_CELL_COLOR_PROVIDER);
			} catch(Exception e) {
				logger.warn(e);
			}
		}
		//
		if(cellColorProvider != null) {
			cellColorProvider.setCellColor(extendedTableViewer);
		}
	}

	private void setSelectionListener(IConfigurationElement configurationElement) {

		try {
			Table table = extendedTableViewer.getTable();
			/*
			 * Remove an existing listener.
			 */
			if(selectionListener != null) {
				table.removeSelectionListener(selectionListener);
				selectionListener = null;
			}
			/*
			 * Be aware, the selection listener is optional.
			 */
			if(configurationElement.getAttribute(ATTRIBUTE_SELECTION_LISTENER) != null) {
				Object object = configurationElement.createExecutableExtension(ATTRIBUTE_SELECTION_LISTENER);
				if(object instanceof SelectionListener) {
					selectionListener = (SelectionListener)object;
					table.addSelectionListener(selectionListener);
				}
			}
		} catch(Exception e) {
			logger.info(e);
		}
	}

	public IConfigurationElement getMeasurementResultVisualizationProvider(String providerId) {

		IConfigurationElement provider = null;
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		exitloop:
		for(IConfigurationElement element : elements) {
			String identifier = element.getAttribute(ATTRIBUTE_IDENTIFIER);
			if(identifier.equals(providerId)) {
				provider = element;
				break exitloop;
			}
		}
		return provider;
	}
}
