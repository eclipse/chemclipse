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
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.exceptions.NoChromatogramConverterAvailableException;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSWT;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.parts.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.parts.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.ChromatogramImportRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.DataType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.OverlaySupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.customcharts.ChromatogramChart;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
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
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractChromatogramEditor extends AbstractDataUpdateSupport implements IChromatogramEditor, IDataUpdateSupport {

	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/chromatogram.gif";
	public static final String TOOLTIP = "Chromatogram Editor";
	//
	private static final Logger logger = Logger.getLogger(AbstractChromatogramEditor.class);
	//
	private DataType dataType;
	private MPart part;
	private MDirtyable dirtyable;
	//
	private IEventBroker eventBroker;
	//
	private File chromatogramFile = null;
	private IChromatogramSelection chromatogramSelection = null;
	private List<IChromatogram> referencedChromatograms;
	//
	private Composite toolbarInfo;
	private Label labelChromatogramInfo;
	private Composite toolbarReferencedChromatograms;
	private Combo comboReferencedChromatograms;
	private ChromatogramChart chromatogramChart;
	//
	private Shell shell = Display.getDefault().getActiveShell();

	public AbstractChromatogramEditor(DataType dataType, Composite parent, MPart part, MDirtyable dirtyable) {
		super(part);
		this.dataType = dataType;
		this.part = part;
		eventBroker = ModelSupportAddon.getEventBroker();
		initialize(parent);
	}

	@Override
	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
	}

	@Override
	public void updateObjects(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(object instanceof IChromatogramSelection) {
				IChromatogramSelection chromatogramSelection = (IChromatogramSelection)object;
				if(this.chromatogramSelection == chromatogramSelection) {
					updateChromatogramSelection();
				}
			}
		}
	}

	@Focus
	public void setFocus() {

		updateChromatogramSelection();
	}

	@PreDestroy
	private void preDestroy() {

		eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UNLOAD_SELECTION, null);
		eventBroker.send(IChemClipseEvents.TOPIC_PEAK_XXD_UNLOAD_SELECTION, null);
		eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_CHROMATOGRAM_SELECTION, chromatogramSelection);
		//
		EModelService modelService = ModelSupportAddon.getModelService();
		if(modelService != null) {
			MApplication application = ModelSupportAddon.getApplication();
			MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
			part.setToBeRendered(false);
			part.setVisible(false);
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					partStack.getChildren().remove(part);
				}
			});
		}
		/*
		 * Run the garbage collector.
		 */
		System.gc();
	}

	@Persist
	public void save() {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				try {
					monitor.beginTask("Save Chromatogram", IProgressMonitor.UNKNOWN);
					try {
						saveChromatogram(monitor, shell);
					} catch(NoChromatogramConverterAvailableException e) {
						throw new InvocationTargetException(e);
					}
				} finally {
					monitor.done();
				}
			}
		};
		/*
		 * Run the export
		 */
		try {
			dialog.run(true, false, runnable);
		} catch(InvocationTargetException e) {
			saveAs();
		} catch(InterruptedException e) {
			logger.warn(e);
		}
	}

	@Override
	public boolean saveAs() {

		boolean saveSuccessful = false;
		if(chromatogramSelection != null) {
			try {
				System.out.println("TODO: Save As...");
				// saveSuccessful = ChromatogramFileSupport.saveChromatogram(chromatogramSelection.getChromatogram());
				dirtyable.setDirty(!saveSuccessful);
			} catch(Exception e) {
				logger.warn(e);
			}
		}
		return saveSuccessful;
	}

	@Override
	public IChromatogramSelection getChromatogramSelection() {

		return chromatogramSelection;
	}

	private void initialize(Composite parent) {

		loadChromatogram();
		createPages(parent);
		//
		if(chromatogramSelection != null) {
			dirtyable.setDirty(true);
			chromatogramSelection.update(true);
		}
	}

	private void updateChromatogramSelection() {

		updateLabel();
		if(chromatogramSelection == null) {
			chromatogramChart.deleteSeries();
		} else {
			chromatogramChart.update();
		}
	}

	private void loadChromatogram() {

		try {
			Object object = part.getObject();
			if(object instanceof String) {
				File file = new File((String)object);
				loadChromatogramSelection(file);
			} else {
				if(object instanceof IChromatogramMSD) {
					IChromatogramMSD chromatogram = (IChromatogramMSD)object;
					chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
				} else if(object instanceof IChromatogramCSD) {
					IChromatogramCSD chromatogram = (IChromatogramCSD)object;
					chromatogramSelection = new ChromatogramSelectionCSD(chromatogram);
				} else if(object instanceof IChromatogramWSD) {
					IChromatogramWSD chromatogram = (IChromatogramWSD)object;
					chromatogramSelection = new ChromatogramSelectionWSD(chromatogram);
				}
				chromatogramFile = null;
			}
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	private void loadChromatogramSelection(File file) throws FileNotFoundException, NoChromatogramConverterAvailableException, FileIsNotReadableException, FileIsEmptyException, ChromatogramIsNullException {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
		ChromatogramImportRunnable runnable = new ChromatogramImportRunnable(file, dataType);
		try {
			dialog.run(true, false, runnable);
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(InterruptedException e) {
			logger.warn(e);
		}
		/*
		 * Get the chromatograms.
		 */
		chromatogramSelection = runnable.getChromatogramSelection();
		referencedChromatograms = chromatogramSelection.getChromatogram().getReferencedChromatograms();
		chromatogramFile = file;
	}

	private void saveChromatogram(IProgressMonitor monitor, Shell shell) throws NoChromatogramConverterAvailableException {

		if(chromatogramSelection != null && shell != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			String converterId = chromatogram.getConverterId();
			if(converterId != null && !converterId.equals("") && chromatogramFile != null) {
				monitor.subTask("Save Chromatogram");
				System.out.println("TODO: Save Chromatogram");
				// IChromatogramExportConverterProcessingInfo processingInfo = ChromatogramConverterMSD.convert(chromatogramFile, chromatogram, converterId, monitor);
				try {
					// processingInfo.getFile();
					dirtyable.setDirty(false);
				} catch(TypeCastException e) {
					throw new NoChromatogramConverterAvailableException();
				}
			} else {
				throw new NoChromatogramConverterAvailableException();
			}
		}
	}

	private void createPages(Composite parent) {

		createChromatogramPage(parent);
	}

	private void createChromatogramPage(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		toolbarReferencedChromatograms = createToolbarReferencedChromatograms(parent);
		createChromatogramChart(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, true);
		PartSupport.setCompositeVisibility(toolbarReferencedChromatograms, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(6, false));
		//
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarReferencedChromatograms(composite);
		createToggleChartSeriesLegendButton(composite);
		createToggleLegendMarkerButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		composite.setVisible(false);
		//
		labelChromatogramInfo = new Label(composite, SWT.NONE);
		labelChromatogramInfo.setText("");
		labelChromatogramInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarReferencedChromatograms(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		composite.setVisible(false);
		//
		comboReferencedChromatograms = new Combo(composite, SWT.READ_ONLY);
		comboReferencedChromatograms.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		String[] items;
		int size = referencedChromatograms.size();
		if(size > 0) {
			items = new String[size];
			int i = 0;
			for(IChromatogram chromatogram : referencedChromatograms) {
				items[i] = "Chromatogram #" + (i + 1) + " " + chromatogram.getName();
				i++;
			}
		} else {
			items = new String[1];
			items[0] = "No referenced chromatogram available.";
		}
		comboReferencedChromatograms.setItems(items);
		//
		return composite;
	}

	private void createChromatogramChart(Composite parent) {

		chromatogramChart = new ChromatogramChart(parent, SWT.BORDER);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setShowRangeSelectorInitially(false);
		chromatogramChart.applySettings(chartSettings);
		//
		OverlaySupport overlaySupport = new OverlaySupport();
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		lineSeriesDataList.add(overlaySupport.getLineSeriesData(chromatogramSelection));
		chromatogramChart.addSeriesData(lineSeriesDataList, LineChart.MEDIUM_COMPRESSION);
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

	private Button createButtonToggleToolbarReferencedChromatograms(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle referenced chromatogram toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarReferencedChromatograms);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private void createToggleChartSeriesLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart series legend.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChart.toggleSeriesLegendVisibility();
			}
		});
	}

	private void createToggleLegendMarkerButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend marker.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHART_LEGEND_MARKER, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IChartSettings chartSettings = chromatogramChart.getChartSettings();
				boolean isShowLegendMarker = chartSettings.isShowLegendMarker();
				chartSettings.setShowLegendMarker(!isShowLegendMarker);
				chromatogramChart.applySettings(chartSettings);
			}
		});
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the chromatogram");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset();
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

				IPreferencePage preferencePageChromatogram = new PreferencePageChromatogram();
				preferencePageChromatogram.setTitle("Chromatogram Settings ");
				IPreferencePage preferencePageSWT = new PreferencePageSWT();
				preferencePageSWT.setTitle("Settings (SWT)");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageChromatogram));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageSWT));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(shell, preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(shell, "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void applySettings() {

		updateChromatogramSelection();
	}

	private void reset() {

		updateChromatogramSelection();
	}

	private void updateLabel() {

		if(chromatogramSelection != null) {
			labelChromatogramInfo.setText(ChromatogramSupport.getChromatogramLabel(chromatogramSelection.getChromatogram()));
		} else {
			labelChromatogramInfo.setText(ChromatogramSupport.getChromatogramLabel(null));
		}
	}
}
