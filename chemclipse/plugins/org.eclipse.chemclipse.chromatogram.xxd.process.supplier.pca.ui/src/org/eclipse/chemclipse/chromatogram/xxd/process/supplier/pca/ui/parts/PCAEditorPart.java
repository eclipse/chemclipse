/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IDataExtraction;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors.PcaEditor;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable.PcaInputRunnable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.IPcaInputWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.PcaChromatogramsMSDInputWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.PcaPeaksInputWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility.PcaColorGroup;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import javafx.collections.ListChangeListener;

public class PCAEditorPart {

	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui/org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.PCAEditor";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/chromatogram.gif";
	public static final String ID = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.PCAEditor";
	private static final Logger logger = Logger.getLogger(PcaEditor.class);
	public static final String TOOLTIP = "PCA Editor";
	private ListChangeListener<ISample<? extends ISampleData>> actualSelectionChangeListener;
	private Label countSelectedSamples;
	private Composite mainComposite;
	private Map<String, Color> mapGroupColor = new HashMap<>();
	private Samples samples;
	private Sample selectedSample;
	private Label selectedSampleLable;
	private CheckboxTableViewer tableSamples;

	public PCAEditorPart() {
		actualSelectionChangeListener = new ListChangeListener<ISample<? extends ISampleData>>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ISample<? extends ISampleData>> c) {

				if(!c.getList().isEmpty()) {
				}
			}
		};
	}

	private void createColumnsTableSamples() {

		String[] titles = {"Filename", "Group"};
		int[] bounds = {100, 100};
		// first column is for the first name
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				Sample sample = (Sample)element;
				return sample.getName();
			}
		});
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {

				Sample sample = (Sample)cell.getElement();
				String groupName = sample.getGroupName();
				cell.setImage(getGroupColorImage(groupName));
				cell.setText(groupName != null ? groupName : "");
			}
		});
		col.setEditingSupport(new EditingSupport(tableSamples) {

			private TextCellEditor editor = new TextCellEditor(tableSamples.getTable());

			@Override
			protected boolean canEdit(Object element) {

				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {

				return editor;
			}

			@Override
			protected Object getValue(Object element) {

				Sample sample = (Sample)element;
				String groupName = sample.getGroupName();
				if(groupName == null) {
					return "";
				} else {
					return groupName;
				}
			}

			@Override
			protected void setValue(Object element, Object value) {

				Sample sample = (Sample)element;
				String groupName = (String)value;
				groupName = groupName.trim();
				groupName = groupName.isEmpty() ? null : groupName;
				sample.setGroupName(groupName);
				updateTableSamples();
			}
		});
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, false));
		mainComposite = new Composite(composite, SWT.None);
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainComposite.setLayout(new GridLayout(2, false));
		/*
		 * Create the section.
		 */
		createOverviewSamples(mainComposite);
		/*
		 * create button area
		 */
		Composite buttonComposite = new Composite(mainComposite, SWT.None);
		buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, true));
		buttonComposite.setLayout(new FillLayout(SWT.VERTICAL));
		Button button = new Button(buttonComposite, SWT.PUSH);
		button.setText("PCA evaulate");
		button.addListener(SWT.Selection, e -> {
			SelectionManagerSamples.getInstance().evaluatePca(samples, new PcaSettings(), new NullProgressMonitor(), true);
		});
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Load Peaks from files");
		button.addListener(SWT.Selection, e -> {
			try {
				openWizardPcaPeaksInput();
			} catch(InvocationTargetException | InterruptedException e1) {
				logger.error(e1.getMessage());
			}
		});
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Load Scans from files");
		button.addListener(SWT.Selection, e -> {
			try {
				openWizardPcaScansInput();
			} catch(InvocationTargetException | InterruptedException e1) {
				logger.error(e1.getMessage());
			}
		});
		createLabelSamplesSelection(composite);
	}

	/**
	 * Creates the file count labels.
	 *
	 * @param client
	 */
	private void createLabelSamplesSelection(Composite client) {

		countSelectedSamples = new Label(client, SWT.NONE);
		countSelectedSamples.setText("Selected: 0 from 0 samples.");
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		countSelectedSamples.setLayoutData(gridData);
	}

	private void createLableSelectedSample(Composite client) {

		selectedSampleLable = new Label(client, SWT.NONE);
		selectedSampleLable.setText("Selected Sample:");
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		selectedSampleLable.setLayoutData(gridData);
	}

	private void createOverviewSamples(Composite parent) {

		Composite client = new Composite(parent, SWT.None);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.widthHint = 400;
		client.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		client.setLayout(layout);
		/*
		 * Creates the table and the action buttons.
		 */
		createTableSamples(client);
		Composite comositeButtons = new Composite(client, SWT.None);
		comositeButtons.setLayout(new GridLayout(2, false));
		Button button = new Button(comositeButtons, SWT.PUSH);
		button.setText("Select All");
		button.addListener(SWT.Selection, e -> tableSamples.setAllChecked(true));
		button = new Button(comositeButtons, SWT.PUSH);
		button.setText("Deselect All");
		button.addListener(SWT.Selection, e -> tableSamples.setAllChecked(false));
		createLableSelectedSample(client);
		client.pack();
	}

	private void createTableSamples(Composite client) {

		GridData gridData;
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 400;
		gridData.widthHint = 100;
		gridData.verticalSpan = 5;
		Table table = new Table(client, SWT.CHECK | SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(gridData);
		tableSamples = new CheckboxTableViewer(table);
		tableSamples.setContentProvider(new ArrayContentProvider());
		tableSamples.addSelectionChangedListener(event -> {
			IStructuredSelection selection = (IStructuredSelection)event.getSelection();
			if(!selection.isEmpty()) {
				Sample sample = ((Sample)selection.getFirstElement());
				if(sample != null && sample != selectedSample) {
					selectedSample = sample;
					selectedSampleLable.setText("Selected Sample: " + selectedSample.getName());
					selectedSampleLable.getParent().layout();
				}
			}
		});
		createColumnsTableSamples();
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {

		final TableViewerColumn viewerColumn = new TableViewerColumn(tableSamples, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	private Image getGroupColorImage(String groupName) {

		Color color = mapGroupColor.get(groupName);
		int len = 16;
		Image image = new Image(Display.getCurrent(), len, len);
		GC gc = new GC(image);
		gc.setBackground(color);
		gc.fillRectangle(0, 0, len, len);
		gc.dispose();
		return image;
	}

	@Focus
	public void onfocus() {

		if(samples != null && !SelectionManagerSamples.getInstance().getSelection().contains(samples)) {
			SelectionManagerSamples.getInstance().getSelection().setAll(samples);
		}
	}

	private int openWizardPcaInput(IPcaInputWizard wizard) throws InvocationTargetException, InterruptedException {

		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getCurrent().getActiveShell(), wizard);
		int status = wizardDialog.open();
		if(status == Window.OK) {
			SelectionManagerSamples.getInstance().getElements().remove(samples);
			SelectionManagerSamples.getInstance().getSelection().clear();
			PcaFiltrationData pcaFiltrationData = wizard.getPcaFiltrationData();
			PcaPreprocessingData pcaPreprocessingData = wizard.getPcaPreprocessingData();
			IDataExtraction pcaExtractionData = wizard.getPcaExtractionData();
			/*
			 * Run the process.
			 */
			PcaInputRunnable runnable = new PcaInputRunnable(pcaExtractionData, pcaFiltrationData, pcaPreprocessingData);
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			/*
			 * Calculate the results and show the score plot page.
			 */
			monitor.run(true, true, runnable);
			this.samples = runnable.getSamples();
			SelectionManagerSamples.getInstance().getElements().add(samples);
			SelectionManagerSamples.getInstance().getSelection().add(samples);
			updateSamples();
		}
		return status;
	}

	private int openWizardPcaPeaksInput() throws InvocationTargetException, InterruptedException {

		return openWizardPcaInput(new PcaPeaksInputWizard());
	}

	private int openWizardPcaScansInput() throws InvocationTargetException, InterruptedException {

		return openWizardPcaInput(new PcaChromatogramsMSDInputWizard());
	}

	private void redrawSamplesSelectedCount() {

		long selected = samples.getSampleList().stream().filter(ISample::isSelected).count();
		countSelectedSamples.setText("Selected: " + selected + " from " + samples.getSampleList().size() + " samples");
	}

	private void updateColorMap() {

		mapGroupColor = PcaColorGroup.getColorSWT(samples.getSampleList().stream().map(s -> s.getGroupName()).collect(Collectors.toSet()));
	}

	private void updateSamples() {

		List<Sample> samplesList = new ArrayList<Sample>(this.samples.getSampleList());
		PcaUtils.sortSampleListByName(samplesList);
		updateColorMap();
		tableSamples.setInput(samplesList);
		updateTableSamples();
		samples.getSampleList().forEach(s -> tableSamples.setChecked(s, s.isSelected()));
		redrawSamplesSelectedCount();
	}

	private void updateTableSamples() {

		updateColorMap();
		TableColumn column = tableSamples.getTable().getColumn(0);
		column.pack();
		column.setWidth(column.getWidth() + 20);
		column = tableSamples.getTable().getColumn(1);
		column.pack();
		int width = column.getWidth() + 20;
		if(width < 150) {
			width = 150;
		}
		column.setWidth(width);
		tableSamples.refresh();
	}
}
