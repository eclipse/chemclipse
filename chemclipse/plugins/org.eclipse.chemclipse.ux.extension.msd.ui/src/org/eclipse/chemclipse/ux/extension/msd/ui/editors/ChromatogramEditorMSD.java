/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.exceptions.NoChromatogramConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.ChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.msd.model.notifier.IChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.msd.swt.ui.components.chromatogram.EditorChromatogramUI;
import org.eclipse.chemclipse.msd.swt.ui.components.ions.MarkedIonsChooser;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider.IonTransitionCheckBoxEditingSupport;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider.IonTransitionContentProvider;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider.IonTransitionLabelProvider;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider.IonTransitionTableComparator;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.support.ChromatogramImportRunnable;
import org.eclipse.chemclipse.ux.extension.msd.ui.support.ChromatogramFileSupport;
import org.eclipse.chemclipse.ux.extension.msd.ui.support.ChromatogramSupport;
import org.eclipse.chemclipse.ux.extension.ui.dialogs.ReferencedChromatogramDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class ChromatogramEditorMSD implements IChromatogramEditorMSD, IChromatogramSelectionMSDUpdateNotifier {

	public static final String ID = "org.eclipse.chemclipse.ux.extension.msd.ui.part.chromatogramEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.msd.ui/org.eclipse.chemclipse.ux.extension.msd.ui.editors.ChromatogramEditorMSD";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/chromatogram.gif";
	public static final String TOOLTIP = "Chromatogram - Detector Type: MSD";
	//
	private static final Logger logger = Logger.getLogger(ChromatogramEditorMSD.class);
	/*
	 * Injected member in constructor
	 */
	@Inject
	private MPart part;
	@Inject
	private MDirtyable dirtyable;
	@Inject
	private IEventBroker eventBroker;
	private EventHandler eventHandler;
	@Inject
	private MApplication application;
	@Inject
	private EModelService modelService;
	@Inject
	private EPartService partService;
	/*
	 * Decimal format to print values.
	 */
	private DecimalFormat decimalFormat;
	/*
	 * Chromatogram selection and the GUI element.
	 */
	private File chromatogramFile;
	private ChromatogramSelectionMSD chromatogramSelection;
	private EditorChromatogramUI chromatogramUI;
	/*
	 * Options.
	 */
	private MarkedIonsChooser selectedIonChooser;
	private MarkedIonsChooser excludedIonChooser;
	/*
	 * Indices of the pages.
	 */
	private int chromatogramPageIndex;
	private int optionsPageIndex;
	private int infoPageIndex;
	private int ionTransitionsPageIndex;
	private int referencedChromatogramPageIndex;
	/*
	 * Showing additional info in tabs.
	 */
	private TabFolder tabFolder;
	/*
	 * Info page
	 */
	private Button buttonOverlay;
	private CLabel labelChromatogramRetentionTimeRange;
	private Text textStartRetentionTime;
	private Text textStopRetentionTime;
	private Button buttonLockOffset;
	/*
	 * FormToolkit for the error message page.
	 */
	private FormToolkit formToolkit;

	public ChromatogramEditorMSD() {
		/*
		 * Decimal format.
		 */
		decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0##");
	}

	@PostConstruct
	private void createControl(Composite parent) {

		/*
		 * Load the chromatogram and create the pages.
		 */
		subscribe();
		loadChromatogram();
		createPages(parent);
		/*
		 * Force menu updates etc.
		 */
		if(chromatogramSelection != null) {
			chromatogramSelection.update(true);
		}
	}

	@Focus
	public void setFocus() {

		if(tabFolder != null) {
			tabFolder.setFocus();
			/*
			 * Update the chromatogram selection on focus.
			 */
			if(tabFolder.getSelectionIndex() == chromatogramPageIndex && chromatogramSelection != null) {
				chromatogramSelection.update(false);
			}
		}
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
		//
		IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
		eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UNLOAD_SELECTION, null);
		eventBroker.send(IChemClipseEvents.TOPIC_PEAK_XXD_UNLOAD_SELECTION, null);
		/*
		 * Remove the actual editor to be not in the updatee list anymore.
		 */
		ChromatogramSelectionMSDUpdateNotifier.fireUpdateChange(null, true);
		if(chromatogramSelection != null) {
			chromatogramSelection.getChromatogram().setUnloaded();
			chromatogramSelection.dispose();
		}
		//
		DisplayUtils.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {

				IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
				eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_SELECTION, chromatogramSelection);
			}
		});
		/*
		 * Remove the editor from the listed parts.
		 */
		if(modelService != null) {
			MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
			part.setToBeRendered(false);
			part.setVisible(false);
			DisplayUtils.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {

					partStack.getChildren().remove(part);
				}
			});
		}
		/*
		 * Dispose the form toolkit.
		 */
		if(formToolkit != null) {
			formToolkit.dispose();
		}
		/*
		 * Run the garbage collector.
		 */
		System.gc();
	}

	@Persist
	public void save() {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(DisplayUtils.getShell());
		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				try {
					monitor.beginTask("Save Chromatogram", IProgressMonitor.UNKNOWN);
					try {
						saveChromatogram(monitor, DisplayUtils.getShell());
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
			/*
			 * True to show the moving progress bar. False, a chromatogram
			 * should be imported as a whole.
			 */
			dialog.run(true, false, runnable);
		} catch(InvocationTargetException e) {
			saveAs();
		} catch(InterruptedException e) {
			logger.warn(e);
		}
	}

	private void saveChromatogram(IProgressMonitor monitor, Shell shell) throws NoChromatogramConverterAvailableException {

		/*
		 * Try to save the chromatogram automatically if it is an *.chrom
		 * type.<br/> If not, show the file save dialog.
		 */
		if(chromatogramSelection != null && shell != null) {
			/*
			 * Each chromatogram import converter should save its converter id
			 * to the converted chromatogram instance.<br/> The id is used to
			 * save the chromatogram automatically when "Save" or "Save all" is
			 * called.
			 */
			IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
			String converterId = chromatogram.getConverterId();
			if(converterId != null && !converterId.equals("") && chromatogramFile != null) {
				/*
				 * Try to save the chromatogram.
				 */
				monitor.subTask("Save Chromatogram");
				IProcessingInfo processingInfo = ChromatogramConverterMSD.getInstance().convert(chromatogramFile, chromatogram, converterId, monitor);
				try {
					/*
					 * If no failures have occurred, set the dirty status to
					 * false.
					 */
					processingInfo.getProcessingResult(File.class);
					dirtyable.setDirty(false);
				} catch(TypeCastException e) {
					throw new NoChromatogramConverterAvailableException();
				}
			} else {
				throw new NoChromatogramConverterAvailableException();
			}
		}
	}

	@Override
	public boolean saveAs() {

		boolean saveSuccessful = false;
		if(chromatogramSelection != null) {
			try {
				saveSuccessful = ChromatogramFileSupport.saveChromatogram(chromatogramSelection.getChromatogramMSD());
				dirtyable.setDirty(!saveSuccessful);
			} catch(Exception e) {
				logger.warn(e);
			}
		}
		return saveSuccessful;
	}

	@Override
	public IChromatogramSelectionMSD getChromatogramSelection() {

		return chromatogramSelection;
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		/*
		 * Do the update if the chromatogram selection is the same and the
		 * chromatogram selection is not null.
		 */
		if(fireUpdate(chromatogramSelection, forceReload)) {
			/*
			 * Marks the chromatogram as dirty.
			 */
			if(forceReload) {
				dirtyable.setDirty(true);
			}
			/*
			 * Update the chromatogram options page if visible.
			 */
			int selectionIndex = tabFolder.getSelectionIndex();
			if(selectionIndex == optionsPageIndex) {
				updateOptionsPageValues();
			} else if(selectionIndex == infoPageIndex) {
				updateInfoPageValues();
			} else if(selectionIndex == ionTransitionsPageIndex) {
				updateIonTransitionPageValues();
			} else if(selectionIndex == referencedChromatogramPageIndex) {
				// do nothing
			}
			/*
			 * Used to show the properties of the actual chromatogram.
			 */
			chromatogramUI.update(chromatogramSelection, forceReload);
		}
	}

	/**
	 * Subscribes the selection update events.
	 */
	private void subscribe() {

		if(eventBroker != null) {
			/*
			 * Receives and handles chromatogram selection updates.
			 */
			eventHandler = new EventHandler() {

				@Override
				public void handleEvent(Event event) {

					IChromatogramSelectionMSD selection = (IChromatogramSelectionMSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
					boolean forceReload = (Boolean)event.getProperty(IChemClipseEvents.PROPERTY_FORCE_RELOAD);
					update(selection, forceReload);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION, eventHandler);
		}
	}

	/**
	 * Unsubscribes the selection update events.
	 */
	private void unsubscribe() {

		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}

	private void loadChromatogram() {

		try {
			/*
			 * Import the chromatogram without showing it on the gui. The GUI
			 * will take care itself of this action.
			 */
			Object object = part.getObject();
			if(object instanceof Map) {
				/*
				 * String
				 */
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>)object;
				File file = new File((String)map.get(EditorSupport.MAP_FILE));
				boolean batch = (boolean)map.get(EditorSupport.MAP_BATCH);
				importChromatogram(file, batch);
			} else if(object instanceof IChromatogramMSD) {
				IChromatogramMSD chromatogram = (IChromatogramMSD)object;
				chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
				chromatogramFile = null;
			} else if(object instanceof String) {
				/*
				 * Legacy ... Deprecated
				 */
				File file = new File((String)object);
				importChromatogram(file, true);
			}
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Imports the selected chromatogram.
	 * 
	 * @param file
	 * @throws FileIsEmptyException
	 * @throws FileIsNotReadableException
	 * @throws NoChromatogramConverterAvailableException
	 * @throws FileNotFoundException
	 * @throws ChromatogramIsNullException
	 */
	@SuppressWarnings("rawtypes")
	private void importChromatogram(File file, boolean batch) throws FileNotFoundException, NoChromatogramConverterAvailableException, FileIsNotReadableException, FileIsEmptyException, ChromatogramIsNullException {

		/*
		 * Import the chromatogram here, but do not set to the chromatogram ui,
		 * as it must be initialized first.
		 */
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(DisplayUtils.getShell());
		ChromatogramImportRunnable runnable = new ChromatogramImportRunnable(file);
		try {
			/*
			 * No fork, otherwise it might crash when loading the data takes too long.
			 */
			boolean fork = (batch) ? false : true;
			dialog.run(fork, false, runnable);
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(InterruptedException e) {
			logger.warn(e);
		}
		chromatogramSelection = runnable.getChromatogramSelection();
		chromatogramFile = file;
		/*
		 * Ask to open the referenced chromatograms.
		 */
		int sizeReferencedChromatograms = chromatogramSelection.getChromatogramMSD().getReferencedChromatograms().size();
		if(sizeReferencedChromatograms > 0) {
			ReferencedChromatogramDialog referencedChromatogramDialog = new ReferencedChromatogramDialog(DisplayUtils.getShell(), chromatogramSelection.getChromatogram());
			referencedChromatogramDialog.create();
			if(referencedChromatogramDialog.open() == Window.OK) {
				List<IChromatogram> selectedChromatograms = referencedChromatogramDialog.getSelectedChromatograms();
				for(IChromatogram chromatogram : selectedChromatograms) {
					ChromatogramSupport.getInstanceEditorSupport().openEditor(chromatogram);
				}
			}
		}
	}

	private void createPages(Composite parent) {

		/*
		 * Create the editor pages.
		 */
		if(chromatogramSelection != null && chromatogramSelection.getChromatogramMSD() != null) {
			part.setLabel(chromatogramSelection.getChromatogramMSD().getName());
			/*
			 * Create the tab folder.
			 */
			tabFolder = new TabFolder(parent, SWT.BOTTOM);
			//
			createChromatogramPage();
			final int cpi = 0;
			chromatogramPageIndex = cpi;
			//
			createOptionsPage();
			final int opi = 1;
			optionsPageIndex = opi;
			//
			createInfoPage();
			final int ipi = 2;
			infoPageIndex = ipi;
			//
			createIonTransitionPage();
			final int itp = 3;
			ionTransitionsPageIndex = itp;
			//
			createReferencedChromatogramPage();
			final int rcp = 4;
			referencedChromatogramPageIndex = rcp;
			/*
			 * React on tab folder selection.
			 */
			tabFolder.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					int index = tabFolder.getSelectionIndex();
					/*
					 * Set the options.
					 */
					if(chromatogramSelection != null) {
						switch(index) {
							case cpi:
								update(chromatogramSelection, false);
								break;
							case opi:
								updateOptionsPageValues();
								break;
							case ipi:
								updateInfoPageValues();
								break;
							case itp:
								updateIonTransitionPageValues();
								break;
							case rcp:
								// do nothing
								break;
						}
					}
				}
			});
		} else {
			createErrorMessagePage(parent);
		}
	}

	private void updateOptionsPageValues() {

		if(selectedIonChooser != null && excludedIonChooser != null) {
			selectedIonChooser.setMarkedIons(chromatogramSelection.getSelectedIons());
			excludedIonChooser.setMarkedIons(chromatogramSelection.getExcludedIons());
		}
	}

	private void updateInfoPageValues() {

		/*
		 * Min/Max RT
		 */
		if(buttonOverlay != null) {
			buttonOverlay.setSelection(chromatogramSelection.isOverlaySelected());
		}
		//
		if(labelChromatogramRetentionTimeRange != null) {
			IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
			String minRetentionTime = decimalFormat.format(chromatogram.getStartRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
			String maxRetentionTime = decimalFormat.format(chromatogram.getStopRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
			labelChromatogramRetentionTimeRange.setText("Chromatogram (Minutes): " + minRetentionTime + " - " + maxRetentionTime);
		}
		/*
		 * Editable text box.
		 */
		if(textStartRetentionTime != null && textStopRetentionTime != null) {
			textStartRetentionTime.setText(decimalFormat.format(chromatogramSelection.getStartRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
			textStopRetentionTime.setText(decimalFormat.format(chromatogramSelection.getStopRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
		}
		/*
		 * Locked Offset
		 */
		if(buttonLockOffset != null) {
			buttonLockOffset.setSelection(chromatogramSelection.isLockOffset());
		}
	}

	private void updateIonTransitionPageValues() {

	}

	private void createChromatogramPage() {

		/*
		 * Create the chromatogram UI.
		 */
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Chromatogram");
		//
		chromatogramUI = new EditorChromatogramUI(tabFolder, SWT.NONE);
		chromatogramUI.setMaster(true);
		chromatogramUI.update(chromatogramSelection, true);
		//
		tabItem.setControl(chromatogramUI);
	}

	private void createErrorMessagePage(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Forms API
		 */
		formToolkit = new FormToolkit(composite.getDisplay());
		ScrolledForm scrolledForm = formToolkit.createScrolledForm(composite);
		Composite scrolledFormComposite = scrolledForm.getBody();
		formToolkit.decorateFormHeading(scrolledForm.getForm());
		scrolledFormComposite.setLayout(new TableWrapLayout());
		scrolledForm.setText("Chromatogram Editor");
		/*
		 * Sections
		 */
		createProblemSection(scrolledFormComposite);
		createResolutionSection(scrolledFormComposite);
	}

	private void createProblemSection(Composite parent) {

		Section section;
		Composite client;
		GridLayout layout;
		/*
		 * Section Problem
		 */
		section = formToolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Problem");
		section.setDescription("There has gone something wrong to open the chromatogram.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		client = formToolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		client.setLayout(layout);
		/*
		 * Labels
		 */
		createLabel(client, "The chromatogram couldn't be loaded.", IApplicationImage.IMAGE_WARN);
		createLabel(client, "Does the selected chromatogram contains data recorded by a mass spectrometer?", IApplicationImage.IMAGE_QUESTION);
		createLabel(client, "Is the data file corrupted?", IApplicationImage.IMAGE_QUESTION);
		createLabel(client, "Is an appropriate file format converter installed?", IApplicationImage.IMAGE_QUESTION);
		createLink(client, "Converters can be installed using the marketplace.", "https://marketplace.openchrom.net");
		createLabel(client, "Does the converter has a valid license? (Menu -> Window -> Preferences -> Converter)", IApplicationImage.IMAGE_QUESTION);
		createLink(client, "Create a log-in and retrieve free serial keys.", "https://marketplace.openchrom.net");
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void createResolutionSection(Composite parent) {

		Section section;
		Composite client;
		GridLayout layout;
		GridData gridData;
		/*
		 * Section Problem
		 */
		section = formToolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Further Questions");
		section.setDescription("The developers may have a solution for you.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		client = formToolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		client.setLayout(layout);
		/*
		 * Labels and Links
		 */
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		createLabel(client, "FID data is also supported.", IApplicationImage.IMAGE_INFO);
		createLink(client, "Please contact the developers, if you have further questions.", "https://www.openchrom.net");
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private CLabel createLabel(Composite parent, String text, String image) {

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		CLabel label = new CLabel(parent, SWT.LEFT);
		label.setText(text);
		if(image != null) {
			label.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16));
		}
		label.setLayoutData(gridData);
		return label;
	}

	private void createReferencedChromatogramHyperlink(Composite parent, int number, final IChromatogramMSD chromatogram) {

		ImageHyperlink imageHyperlink = new ImageHyperlink(parent, SWT.LEFT);
		imageHyperlink.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		imageHyperlink.setText("Chromatogram #" + number);
		imageHyperlink.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {

				/*
				 * Opens the overview.
				 */
				ChromatogramSupport.getInstanceEditorSupport().openOverview(chromatogram);
			}

			@Override
			public void mouseDown(MouseEvent e) {

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				/*
				 * Opens the editor.
				 */
				ChromatogramSupport.getInstanceEditorSupport().openEditor(chromatogram);
			}
		});
	}

	private void createLink(Composite parent, String text, final String url) {

		Link link = new Link(parent, SWT.NONE);
		link.setText(text + " (<a>" + url + "</a>)");
		link.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				Program.launch(url);
			}
		});
	}

	private void createOptionsPage() {

		/*
		 * Options Page
		 */
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Options");
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Forms API
		 */
		formToolkit = new FormToolkit(composite.getDisplay());
		ScrolledForm scrolledForm = formToolkit.createScrolledForm(composite);
		Composite scrolledFormComposite = scrolledForm.getBody();
		formToolkit.decorateFormHeading(scrolledForm.getForm());
		scrolledFormComposite.setLayout(new TableWrapLayout());
		scrolledForm.setText("Chromatogram Options");
		/*
		 * Add the sections
		 */
		createOptionsPageSection(scrolledFormComposite);
		//
		tabItem.setControl(composite);
	}

	private void createOptionsPageSection(Composite parent) {

		Section section;
		Composite client;
		GridLayout layout;
		/*
		 * Sections
		 */
		section = formToolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Options");
		section.setDescription("This page shows several additional chromatogram options.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		client = formToolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout(1, true);
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		client.setLayout(layout);
		/*
		 * Labels and Forms
		 */
		createLabel(client, "Use the selected/excluded ions to inspect the chromatogram using different views.", IApplicationImage.IMAGE_INFO);
		Composite compositeIons = new Composite(client, SWT.NONE);
		compositeIons.setLayout(new GridLayout(2, true));
		compositeIons.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		selectedIonChooser = new MarkedIonsChooser(compositeIons, SWT.NONE);
		selectedIonChooser.setMarkedIons(chromatogramSelection.getSelectedIons());
		selectedIonChooser.setLabelText("Add/Remove selected ions");
		//
		excludedIonChooser = new MarkedIonsChooser(compositeIons, SWT.NONE);
		excludedIonChooser.setMarkedIons(chromatogramSelection.getExcludedIons());
		excludedIonChooser.setLabelText("Add/Remove excluded ions");
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void createInfoPage() {

		/*
		 * Miscellaneous Page
		 */
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Info");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Forms API
		 */
		formToolkit = new FormToolkit(composite.getDisplay());
		ScrolledForm scrolledForm = formToolkit.createScrolledForm(composite);
		Composite scrolledFormComposite = scrolledForm.getBody();
		formToolkit.decorateFormHeading(scrolledForm.getForm());
		scrolledFormComposite.setLayout(new TableWrapLayout());
		scrolledForm.setText("Chromatogram Info");
		/*
		 * Add the sections
		 */
		createInfoPageSection(scrolledFormComposite);
		//
		tabItem.setControl(composite);
	}

	private void createInfoPageSection(Composite parent) {

		Section section;
		Composite client;
		GridLayout layout;
		/*
		 * Sections
		 */
		section = formToolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Chromatogram Selection");
		section.setDescription("Display of the current chromatogram selection.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		client = formToolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout(1, true);
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		client.setLayout(layout);
		/*
		 * Overlay
		 */
		buttonOverlay = new Button(client, SWT.CHECK);
		buttonOverlay.setSelection(chromatogramSelection.isOverlaySelected());
		buttonOverlay.setText("Show chromatogram in overlay");
		buttonOverlay.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean overlaySelected = buttonOverlay.getSelection();
				chromatogramSelection.setOverlaySelected(overlaySelected);
			}
		});
		/*
		 * Labels
		 */
		labelChromatogramRetentionTimeRange = createLabel(client, "Retention Time Range (Chromatogram):", IApplicationImage.IMAGE_INFO);
		//
		createLabel(client, "Start Retention Time (Minutes):", null);
		textStartRetentionTime = new Text(client, SWT.NONE);
		textStartRetentionTime.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		createLabel(client, "Stop Retention Time (Minutes):", null);
		textStopRetentionTime = new Text(client, SWT.NONE);
		textStopRetentionTime.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Button button = new Button(client, SWT.PUSH);
		button.setText("Set Retention Time Range");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setRetentionTimeRange();
			}
		});
		Button buttonAll = new Button(client, SWT.PUSH);
		buttonAll.setText("Set Retention Time Range for all open Chromatograms");
		buttonAll.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setRetentionTimeRangeForAllOpenChromatograms();
			}
		});
		/*
		 * Lock Offset
		 */
		buttonLockOffset = new Button(client, SWT.CHECK);
		buttonLockOffset.setSelection(chromatogramSelection.isLockOffset());
		buttonLockOffset.setText("Lock Offset");
		buttonLockOffset.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean lockOffset = buttonLockOffset.getSelection();
				chromatogramSelection.setLockOffset(lockOffset);
			}
		});
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private void setRetentionTimeRange() {

		String startRetentionTimeText = textStartRetentionTime.getText().trim();
		String stopRetentionTimeText = textStopRetentionTime.getText().trim();
		//
		try {
			int startRetentionTime = (int)(decimalFormat.parse(startRetentionTimeText).doubleValue() * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
			int stopRetentionTime = (int)(decimalFormat.parse(stopRetentionTimeText).doubleValue() * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
			//
			if(startRetentionTime < stopRetentionTime) {
				/*
				 * Set it to the given range.
				 */
				chromatogramSelection.reset();
				chromatogramSelection.update(true);
				//
				chromatogramSelection.setStartRetentionTime(startRetentionTime);
				chromatogramSelection.setStopRetentionTime(stopRetentionTime);
				chromatogramUI.updateSelectionManually(chromatogramSelection);
				fireUpdate(chromatogramSelection, true);
			}
		} catch(ParseException e) {
			logger.warn(e);
		}
	}

	private void setRetentionTimeRangeForAllOpenChromatograms() {

		String startRetentionTimeText = textStartRetentionTime.getText().trim();
		String stopRetentionTimeText = textStopRetentionTime.getText().trim();
		//
		try {
			int startRetentionTime = (int)(decimalFormat.parse(startRetentionTimeText).doubleValue() * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
			int stopRetentionTime = (int)(decimalFormat.parse(stopRetentionTimeText).doubleValue() * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
			//
			if(startRetentionTime < stopRetentionTime) {
				/*
				 * Get all open parts.
				 */
				Collection<MPart> parts = partService.getParts();
				for(MPart part : parts) {
					if(part.getElementId().equals(ChromatogramEditorMSD.ID)) {
						/*
						 * Select the chromatogram editor parts only.
						 */
						Object object = part.getObject();
						if(object != null) {
							if(object instanceof ChromatogramEditorMSD) {
								ChromatogramEditorMSD editor = (ChromatogramEditorMSD)object;
								IChromatogramSelectionMSD selection = editor.getChromatogramSelection();
								selection.setStartRetentionTime(startRetentionTime);
								selection.setStopRetentionTime(stopRetentionTime);
								selection.fireUpdateChange(true);
							}
						}
					}
				}
			}
		} catch(ParseException e) {
			logger.warn(e);
		}
	}

	private void createIonTransitionPage() {

		/*
		 * Ion Transition Page
		 */
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Ion Transitions");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Table
		 */
		String[] titles = {"name", "parent m/z", "parent resolution", "daughter m/z", "daughter resolution", "collision energy", "show"};
		int bounds[] = {250, 100, 150, 100, 150, 150, 60};
		//
		ExtendedTableViewer tableViewer = new ExtendedTableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(new IonTransitionContentProvider());
		tableViewer.setLabelProvider(new IonTransitionLabelProvider());
		tableViewer.setComparator(new IonTransitionTableComparator());
		List<TableViewerColumn> tableViewerColumns = tableViewer.getTableViewerColumns();
		TableViewerColumn tableViewerColumn = tableViewerColumns.get(6); // see
		// index
		// in
		// titles[]
		tableViewerColumn.setEditingSupport(new IonTransitionCheckBoxEditingSupport(tableViewer));
		tableViewer.setInput(chromatogramSelection);
		//
		tabItem.setControl(composite);
	}

	private void createReferencedChromatogramPage() {

		/*
		 * Miscellaneous Page
		 */
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Referenced Chromatograms");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Forms API
		 */
		formToolkit = new FormToolkit(composite.getDisplay());
		ScrolledForm scrolledForm = formToolkit.createScrolledForm(composite);
		Composite scrolledFormComposite = scrolledForm.getBody();
		formToolkit.decorateFormHeading(scrolledForm.getForm());
		scrolledFormComposite.setLayout(new TableWrapLayout());
		scrolledForm.setText("Chromatograms");
		/*
		 * Add the sections
		 */
		createReferencedChromatogramPageSection(scrolledFormComposite);
		//
		tabItem.setControl(composite);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void createReferencedChromatogramPageSection(Composite parent) {

		Section section;
		Composite client;
		GridLayout layout;
		/*
		 * Sections
		 */
		section = formToolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("References");
		section.setDescription("Display of the chromatogram references.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Client
		 */
		client = formToolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout(1, true);
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		client.setLayout(layout);
		/*
		 * Labels
		 */
		List<IChromatogram> references = chromatogramSelection.getChromatogram().getReferencedChromatograms();
		int counter = 1;
		for(IChromatogram chromatogram : references) {
			if(chromatogram instanceof IChromatogramMSD) {
				/*
				 * Opens the referenced chromatogram in a new editor.
				 */
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				createReferencedChromatogramHyperlink(client, counter++, chromatogramMSD);
			}
		}
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private boolean fireUpdate(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		/*
		 * Verify that the ui and the selections are not null.
		 */
		if(chromatogramUI == null || chromatogramSelection == null || this.chromatogramSelection == null) {
			return false;
		}
		/*
		 * A force reload should only rely on the same chromatogram, cause the
		 * editor class will show multiple chromatograms. Hence, only the
		 * corresponding chromatogram shall be updated and not all the
		 * chromatograms opened in the editor.
		 */
		if(forceReload) {
			return this.chromatogramSelection.getChromatogramMSD() == chromatogramSelection.getChromatogramMSD();
		} else {
			return this.chromatogramSelection == chromatogramSelection;
		}
	}
}
