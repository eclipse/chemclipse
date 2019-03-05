/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.wsd.ui.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.exceptions.NoChromatogramConverterAvailableException;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.dialogs.ReferencedChromatogramDialog;
import org.eclipse.chemclipse.ux.extension.wsd.ui.internal.support.ChromatogramImportRunnable;
import org.eclipse.chemclipse.ux.extension.wsd.ui.support.ChromatogramFileSupport;
import org.eclipse.chemclipse.ux.extension.wsd.ui.support.ChromatogramSupport;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.AbstractChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.notifier.ChromatogramSelectionWSDUpdateNotifier;
import org.eclipse.chemclipse.wsd.model.notifier.IChromatogramSelectionWSDUpdateNotifier;
import org.eclipse.chemclipse.wsd.swt.ui.components.chromatogram.EditorChromatogramUI;
import org.eclipse.chemclipse.wsd.swt.ui.components.chromatogram.MarkedWavelengthsChooser;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class ChromatogramEditorWSD implements IChromatogramEditorWSD, IChromatogramSelectionWSDUpdateNotifier {

	public static final String ID = "org.eclipse.chemclipse.ux.extension.wsd.ui.part.chromatogramEditor";
	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.ux.extension.wsd.ui/org.eclipse.chemclipse.ux.extension.wsd.ui.editors.ChromatogramEditorWSD";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/chromatogram.gif";
	public static final String TOOLTIP = "Chromatogram - Detector Type: WSD";
	//
	private static final Logger logger = Logger.getLogger(ChromatogramEditorWSD.class);
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
	/*
	 * Decimal format to print values.
	 */
	private DecimalFormat decimalFormat;
	/*
	 * Chromatogram selection and the GUI element.
	 */
	private File chromatogramFile;
	private ChromatogramSelectionWSD chromatogramSelection;
	private EditorChromatogramUI chromatogramUI;
	/*
	 * Options.
	 */
	private MarkedWavelengthsChooser selectedWavelengthChooser;
	/*
	 * Indices of the pages.
	 */
	private int chromatogramPageIndex;
	private int optionsPageIndex;
	private int infoPageIndex;
	private int referencedChromatogramPageIndex;
	/*
	 * Showing additional info in tabs.
	 */
	private TabFolder tabFolder;
	/*
	 * Info page
	 */
	private Button buttonOverlay;
	private CLabel infoStartRetentionTimeLabel;
	private CLabel infoStopRetentionTimeLabel;
	private Button buttonLockOffset;
	/*
	 * FormToolkit for the error message page.
	 */
	private FormToolkit formToolkit;

	public ChromatogramEditorWSD() {
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
		/*
		 * Remove the actual editor to be not in the updatee list anymore.
		 */
		ChromatogramSelectionWSDUpdateNotifier.fireUpdateChange(null, true);
		if(chromatogramSelection != null) {
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
			partStack.getChildren().remove(part);
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
			IChromatogramWSD chromatogram = chromatogramSelection.getChromatogramWSD();
			String converterId = chromatogram.getConverterId();
			if(converterId != null && !converterId.equals("") && chromatogramFile != null) {
				/*
				 * Try to save the chromatogram.
				 */
				monitor.subTask("Save Chromatogram");
				IProcessingInfo processingInfo = ChromatogramConverterWSD.getInstance().convert(chromatogramFile, chromatogram, converterId, monitor);
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
				saveSuccessful = ChromatogramFileSupport.saveChromatogram(chromatogramSelection.getChromatogramWSD());
				dirtyable.setDirty(!saveSuccessful);
			} catch(NoConverterAvailableException e) {
				logger.warn(e);
			}
		}
		return saveSuccessful;
	}

	@Override
	public IChromatogramSelectionWSD getChromatogramSelection() {

		return chromatogramSelection;
	}

	@Override
	public void update(IChromatogramSelectionWSD chromatogramSelection, boolean forceReload) {

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

				public void handleEvent(Event event) {

					IChromatogramSelectionWSD selection = (IChromatogramSelectionWSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
					boolean forceReload = (Boolean)event.getProperty(IChemClipseEvents.PROPERTY_FORCE_RELOAD);
					update(selection, forceReload);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION, eventHandler);
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
			} else if(object instanceof IChromatogramWSD) {
				IChromatogramWSD chromatogram = (IChromatogramWSD)object;
				chromatogramSelection = new ChromatogramSelectionWSD(chromatogram);
				chromatogramFile = null;
			} else if(object instanceof String) {
				/*
				 * Legacy ... Deprecated
				 */
				File file = new File((String)object);
				importChromatogram(file, true);
			}
		} catch(Exception e) {
			logger.warn(e);
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
		ChromatogramImportRunnable runnable = new ChromatogramImportRunnable(file, chromatogramSelection);
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
		//
		chromatogramSelection = runnable.getChromatogramSelection();
		chromatogramFile = file;
		/*
		 * Ask to open the referenced chromatograms.
		 */
		int sizeReferencedChromatograms = chromatogramSelection.getChromatogramWSD().getReferencedChromatograms().size();
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
		if(chromatogramSelection != null && chromatogramSelection.getChromatogramWSD() != null) {
			part.setLabel(chromatogramSelection.getChromatogramWSD().getName());
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
			createReferencedChromatogramPage();
			final int rcp = 3;
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

		if(selectedWavelengthChooser != null) {
			selectedWavelengthChooser.setMarkedWavelengths(chromatogramSelection.getSelectedWavelengths());
		}
	}

	private void updateInfoPageValues() {

		if(buttonOverlay != null) {
			buttonOverlay.setSelection(chromatogramSelection.isOverlaySelected());
		}
		//
		if(infoStartRetentionTimeLabel != null && infoStopRetentionTimeLabel != null) {
			infoStartRetentionTimeLabel.setText("Start Retention Time (Minutes): " + decimalFormat.format(chromatogramSelection.getStartRetentionTime() / AbstractChromatogramWSD.MINUTE_CORRELATION_FACTOR));
			infoStopRetentionTimeLabel.setText("Stop Retention Time (Minutes): " + decimalFormat.format(chromatogramSelection.getStopRetentionTime() / AbstractChromatogramWSD.MINUTE_CORRELATION_FACTOR));
		}
		/*
		 * Locked Offset
		 */
		if(buttonLockOffset != null) {
			buttonLockOffset.setSelection(chromatogramSelection.isLockOffset());
		}
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
		section.setDescription("Something went wrong when trying to open this wave-length selective detector (WSD) chromatogram.");
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
		createLabel(client, "Does the selected chromatogram contain data recorded by an UV/VIS or DAD detector?", IApplicationImage.IMAGE_QUESTION);
		createLabel(client, "Is the data file corrupted?", IApplicationImage.IMAGE_QUESTION);
		createLabel(client, "Is an appropriate file format converter installed?", IApplicationImage.IMAGE_QUESTION);
		createLink(client, "Converters can be installed using the marketplace.", "https://marketplace.openchrom.net");
		createLabel(client, "Does the converter have a valid license? (Menu -> Window -> Preferences -> Converter)", IApplicationImage.IMAGE_QUESTION);
		createLink(client, "Create a log-in and retrieve free serial keys.", "https://marketplace.openchrom.net");
		createLabel(client, "Please note that WSD support is in early stages of development.", IApplicationImage.IMAGE_INFO);
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
		section.setText("Support");
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
		createLink(client, "If you have further questions, please don't hesitate to contact us directly.", "https://www.openchrom.net");
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

	private void createReferencedChromatogramHyperlink(Composite parent, int number, final IChromatogramWSD chromatogram) {

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
		createLabel(client, "Use the selected wavelengths to inspect the chromatogram using different views.", IApplicationImage.IMAGE_INFO);
		Composite compositeIons = new Composite(client, SWT.NONE);
		compositeIons.setLayout(new GridLayout(1, true));
		compositeIons.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		selectedWavelengthChooser = new MarkedWavelengthsChooser(compositeIons, SWT.NONE);
		selectedWavelengthChooser.setMarkedWavelengths(chromatogramSelection.getSelectedWavelengths());
		selectedWavelengthChooser.setLabelText("Add/Remove selected wavelengths");
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
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
		infoStartRetentionTimeLabel = createLabel(client, "", IApplicationImage.IMAGE_INFO);
		infoStopRetentionTimeLabel = createLabel(client, "", IApplicationImage.IMAGE_INFO);
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
			if(chromatogram instanceof IChromatogramWSD) {
				/*
				 * Opens the referenced chromatogram in a new editor.
				 */
				IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
				createReferencedChromatogramHyperlink(client, counter++, chromatogramWSD);
			}
		}
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	private boolean fireUpdate(IChromatogramSelectionWSD chromatogramSelection, boolean forceReload) {

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
			return this.chromatogramSelection.getChromatogramWSD() == chromatogramSelection.getChromatogramWSD();
		} else {
			return this.chromatogramSelection == chromatogramSelection;
		}
	}
}
