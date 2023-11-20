/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - refactoring to use ComboViewer instead of raw combo, change to use EditorToolBar
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.support.HeaderUtil;
import org.eclipse.chemclipse.model.support.RetentionTimeRange;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.dialogs.ChromatogramEditorDialog;
import org.eclipse.chemclipse.ux.extension.xxd.ui.dialogs.DataTypeDialog;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.ChromatogramImportRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.SupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputEntriesWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.xir.model.core.IChromatogramISD;
import org.eclipse.chemclipse.xir.model.core.selection.ChromatogramSelectionISD;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ChromatogramReferencesUI extends Composite {

	private static final Logger logger = Logger.getLogger(ChromatogramReferencesUI.class);
	//
	private ComboContainer comboChromatograms = null;
	//
	private Button buttonPrevious;
	private ComboViewer comboViewerReferences;
	private Button buttonNext;
	private Button buttonAdd;
	private Button buttonImport;
	private Button buttonRemove;
	private Button buttonRemoveAll;
	private Button buttonOpen;
	private Button buttonRefresh;
	//
	private ISelectionChangedListener selectionChangeListener = null;
	private HashMap<IChromatogram<?>, IChromatogramSelection<?, ?>> referenceSelections = new HashMap<>();

	public ChromatogramReferencesUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(IChromatogramSelection<?, ?> chromatogramSelection, Consumer<IChromatogramSelection<?, ?>> chromatogramReferencesListener) {

		/*
		 * Create the container
		 */
		comboChromatograms = new ComboContainer(chromatogramReferencesListener.andThen(t -> updateButtons()));
		comboChromatograms.viewerReference.set(comboViewerReferences);
		/*
		 * Remove the existing change listener.
		 */
		if(selectionChangeListener != null) {
			comboViewerReferences.removeSelectionChangedListener(selectionChangeListener);
		}
		/*
		 * Add data and change listener.
		 */
		if(comboChromatograms != null) {
			selectionChangeListener = comboChromatograms;
			comboViewerReferences.addSelectionChangedListener(selectionChangeListener);
			//
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			List<IChromatogramSelection<?, ?>> chromatogramSelections = new ArrayList<IChromatogramSelection<?, ?>>();
			chromatogramSelections.add(chromatogramSelection);
			for(IChromatogram<?> chromatogramReference : chromatogram.getReferencedChromatograms()) {
				chromatogramSelections.add(createChromatogramSelection(chromatogramReference));
			}
			comboChromatograms.data = chromatogramSelections;
			//
			comboViewerReferences.setInput(comboChromatograms.data);
		}
	}

	public void updateInput() {

		updateButtons();
	}

	public List<IChromatogramSelection<?, ?>> getChromatogramSelections() {

		if(comboChromatograms != null) {
			return Collections.unmodifiableList(comboChromatograms.data);
		}
		//
		return Collections.emptyList();
	}

	@Override
	public void update() {

		List<IChromatogramSelection<?, ?>> chromatogramMasterAndReferences = new ArrayList<>();
		//
		if(comboChromatograms != null && comboChromatograms.master != null) {
			//
			IChromatogramSelection<?, ?> masterSelection = comboChromatograms.master;
			chromatogramMasterAndReferences.add(masterSelection);
			List<IChromatogram<?>> referencedChromatograms = masterSelection.getChromatogram().getReferencedChromatograms();
			//
			for(IChromatogram<?> referencedChromatogram : referencedChromatograms) {
				IChromatogramSelection<?, ?> referenceSelection = referenceSelections.get(referencedChromatogram);
				if(referenceSelection == null) {
					referenceSelection = createChromatogramSelection(referencedChromatogram);
					if(referenceSelection != null) {
						referenceSelections.put(referencedChromatogram, referenceSelection);
					}
				}
				chromatogramMasterAndReferences.add(referenceSelection);
			}
		}
		//
		if(comboChromatograms != null) {
			comboChromatograms.setInput(chromatogramMasterAndReferences);
			comboChromatograms.refreshUI();
		}
	}

	private IChromatogramSelection<?, ?> createChromatogramSelection(IChromatogram<?> referencedChromatogram) {

		if(referencedChromatogram instanceof IChromatogramCSD chromatogramCSD) {
			return new ChromatogramSelectionCSD(chromatogramCSD);
		} else if(referencedChromatogram instanceof IChromatogramMSD chromatogramMSD) {
			return new ChromatogramSelectionMSD(chromatogramMSD);
		} else if(referencedChromatogram instanceof IChromatogramWSD chromatogramWSD) {
			return new ChromatogramSelectionWSD(chromatogramWSD);
		} else if(referencedChromatogram instanceof IChromatogramISD chromatogramISD) {
			return new ChromatogramSelectionISD(chromatogramISD);
		} else {
			return null;
		}
	}

	public void setMasterChromatogram(IChromatogramSelection<?, ?> chromatogramSelection) {

		if(chromatogramSelection != null && comboChromatograms != null && comboChromatograms.master != chromatogramSelection) {
			comboChromatograms.master = chromatogramSelection;
			update();
			comboChromatograms.setSelection(new StructuredSelection(chromatogramSelection));
			chromatogramSelection.update(true);
			updateButtons();
		}
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(9, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		//
		buttonPrevious = createButtonSelectPreviousChromatogram(composite);
		comboViewerReferences = createComboChromatograms(composite);
		buttonNext = createButtonSelectNextChromatogram(composite);
		buttonRemove = createButtonRemoveReference(composite);
		buttonRemoveAll = createButtonRemoveReferenceAll(composite);
		buttonAdd = createButtonAddReference(composite);
		buttonImport = createButtonImportReferences(composite);
		buttonOpen = createButtonOpenReference(composite);
		buttonRefresh = createButtonRefresh(composite);
		//
		initialize();
	}

	private void initialize() {

		// comboViewerReferences.setInput(dataTypes);
	}

	private Button createButtonSelectPreviousChromatogram(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select previous chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_BACKWARD, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(comboChromatograms != null) {
					int index = comboChromatograms.currentIndex();
					comboChromatograms.selectChromatogram(index - 1);
					updateButtons();
				}
			}
		});
		//
		return button;
	}

	private ComboViewer createComboChromatograms(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IChromatogramSelection<?, ?> selection) {
					if(comboChromatograms != null) {
						int index = comboChromatograms.indexOf(selection);
						if(index > -1) {
							/*
							 * Get the information to display.
							 */
							IChromatogram<?> chromatogram = selection.getChromatogram();
							return ChromatogramDataSupport.getReferenceLabel(chromatogram, index, true);
						}
					}
				}
				return "N/A";
			}
		});
		//
		combo.setToolTipText("Select a referenced chromatogram.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return comboViewer;
	}

	private Button createButtonSelectNextChromatogram(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select next chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_FORWARD, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(comboChromatograms != null) {
					int index = comboChromatograms.currentIndex();
					comboChromatograms.selectChromatogram(index + 1);
					updateButtons();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonRemoveReference(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Remove the reference chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(comboChromatograms != null) {
					int index = comboChromatograms.currentIndex();
					if(index > 0 && comboChromatograms.master != null) {
						if(MessageDialog.openQuestion(e.display.getActiveShell(), "Delete Reference", "Do you want to delete the chromatogram reference: " + index + "?")) {
							IChromatogram<?> chromatogram = comboChromatograms.master.getChromatogram();
							IChromatogramSelection<?, ?> remove = comboChromatograms.data.remove(index);
							chromatogram.removeReferencedChromatogram(remove.getChromatogram());
							comboChromatograms.selection = new StructuredSelection(comboChromatograms.master);
							comboChromatograms.refreshUI();
						}
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonRemoveReferenceAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Remove all reference chromatograms.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(comboChromatograms != null) {
					int index = comboChromatograms.currentIndex();
					if(index == 0 && comboChromatograms.master != null) {
						if(MessageDialog.openQuestion(e.display.getActiveShell(), "Delete References", "Do you want to delete all chromatogram references?")) {
							IChromatogram<?> chromatogram = comboChromatograms.master.getChromatogram();
							while(comboChromatograms.data.size() > 1) {
								comboChromatograms.data.remove(1);
							}
							chromatogram.removeAllReferencedChromatograms();
							comboChromatograms.selection = new StructuredSelection(comboChromatograms.master);
							comboChromatograms.refreshUI();
						}
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonAddReference(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add a reference chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(comboChromatograms != null) {
					ChromatogramEditorDialog dialog = new ChromatogramEditorDialog(e.display.getActiveShell(), comboChromatograms.master.getChromatogram());
					if(IDialogConstants.OK_ID == dialog.open()) {
						IChromatogramSelection<?, ?> chromatogramSelection = dialog.getChromatogramSelection();
						if(chromatogramSelection != null) {
							IChromatogramSelection<?, ?> masterSelection = comboChromatograms.master;
							if(masterSelection != null) {
								if(masterSelection.getChromatogram() != chromatogramSelection.getChromatogram()) {
									List<IChromatogramSelection<?, ?>> chromatogramSelections = new ArrayList<>();
									chromatogramSelections.add(chromatogramSelection);
									addReferences(masterSelection, chromatogramSelections);
									comboChromatograms.selection = new StructuredSelection(chromatogramSelection);
									comboChromatograms.refreshUI();
									updateButtons();
								} else {
									MessageDialog.openWarning(e.display.getActiveShell(), "Add Reference", "You can't add the selected chromatogram as a reference.");
								}
							}
						}
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonImportReferences(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Import reference chromatograms.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(comboChromatograms != null) {
					DataTypeDialog dataTypeDialog = new DataTypeDialog(e.display.getActiveShell(), new DataType[]{DataType.MSD, DataType.CSD, DataType.WSD});
					if(IDialogConstants.OK_ID == dataTypeDialog.open()) {
						DataType dataType = dataTypeDialog.getDataType();
						InputWizardSettings inputWizardSettings = InputWizardSettings.create(Activator.getDefault().getPreferenceStore(), dataType);
						inputWizardSettings.setTitle("Select Chromatograms");
						inputWizardSettings.setDescription("Select chromatogram that will be added as references.");
						Set<File> chromatogramFiles = InputEntriesWizard.openWizard(e.display.getActiveShell(), inputWizardSettings).keySet();
						if(!chromatogramFiles.isEmpty()) {
							IChromatogramSelection<?, ?> masterSelection = comboChromatograms.master;
							if(masterSelection != null) {
								//
								List<File> files = new ArrayList<>(chromatogramFiles);
								ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(e.display.getActiveShell());
								ChromatogramImportRunnable runnable = new ChromatogramImportRunnable(files, dataType);
								try {
									progressMonitorDialog.run(false, false, runnable);
									List<IChromatogramSelection<?, ?>> references = runnable.getChromatogramSelections();
									references.removeIf(r -> new RetentionTimeRange(r).contentEquals(masterSelection));
									Collections.sort(references, (r1, r2) -> r1.getChromatogram().getName().compareTo(r2.getChromatogram().getName()));
									addReferences(masterSelection, references);
									comboChromatograms.refreshUI();
									updateButtons();
								} catch(InterruptedException e1) {
									logger.error(e1);
									Thread.currentThread().interrupt();
								} catch(InvocationTargetException e1) {
									logger.error(e1.getCause());
								}
							}
						}
					}
				}
			}
		});
		//
		return button;
	}

	private void addReferences(IChromatogramSelection<?, ?> masterSelection, List<IChromatogramSelection<?, ?>> chromatogramSelections) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		HeaderField headerField = HeaderUtil.getHeaderField(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_NAME_TO_REFERENCES_HEADER_FIELD));
		//
		for(IChromatogramSelection<?, ?> chromatogramSelection : chromatogramSelections) {
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			String name = chromatogram.getName();
			switch(headerField) {
				case DATA_NAME:
					chromatogram.setDataName(name);
					break;
				case SAMPLE_NAME:
					chromatogram.setSampleName(name);
					break;
				case SAMPLE_GROUP:
					chromatogram.setSampleGroup(name);
					break;
				case SHORT_INFO:
					chromatogram.setShortInfo(name);
					break;
				default:
					/*
					 * NAME and DEFAULT are not supported here as NAME can't be set.
					 */
					break;
			}
			//
			masterSelection.getChromatogram().addReferencedChromatogram(chromatogram);
			if(comboChromatograms != null) {
				comboChromatograms.data.add(chromatogramSelection);
			}
		}
	}

	private Button createButtonOpenReference(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Open the chromatogram in a separate editor.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(comboChromatograms != null) {
					int index = comboChromatograms.currentIndex();
					IChromatogramSelection<?, ?> chromatogramSelection = comboChromatograms.data.get(index);
					IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
					//
					DataType dataType = null;
					if(chromatogram instanceof IChromatogramMSD) {
						dataType = DataType.MSD;
					} else if(chromatogram instanceof IChromatogramCSD) {
						dataType = DataType.CSD;
					} else if(chromatogram instanceof IChromatogramWSD) {
						dataType = DataType.WSD;
					}
					//
					if(dataType != null) {
						ISupplierEditorSupport supplierEditorSupport = new SupplierEditorSupport(dataType, () -> Activator.getDefault().getEclipseContext());
						e.display.asyncExec(new Runnable() {

							@Override
							public void run() {

								supplierEditorSupport.openEditor(chromatogram);
							}
						});
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonRefresh(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Refresh the references selection.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_REFRESH, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				update();
			}
		});
		//
		return button;
	}

	private void updateButtons() {

		try {
			if(isVisible()) {
				if(comboChromatograms != null) {
					int size = comboChromatograms.data.size();
					int selectionIndex = comboChromatograms.currentIndex();
					//
					buttonPrevious.setEnabled(selectionIndex > 0);
					buttonNext.setEnabled(selectionIndex < size - 1);
					buttonRemove.setEnabled(selectionIndex > 0); // 0 is the master can't be removed
					buttonRemoveAll.setEnabled(selectionIndex == 0 && size > 1); // Remove all when in master modus
					buttonAdd.setEnabled(selectionIndex == 0); // 0 references can be added only to master
					buttonImport.setEnabled(selectionIndex == 0); // 0 references can be added only to master
					buttonOpen.setEnabled(true); // Always true
					buttonRefresh.setEnabled(selectionIndex > 0);
				}
			}
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	private static final class ComboContainer implements ISelectionChangedListener {

		private final AtomicReference<ComboViewer> viewerReference = new AtomicReference<>();
		private final Consumer<IChromatogramSelection<?, ?>> listener;
		//
		private IChromatogramSelection<?, ?> master;
		private IStructuredSelection selection = StructuredSelection.EMPTY;
		private List<IChromatogramSelection<?, ?>> data = Collections.emptyList();

		public ComboContainer(Consumer<IChromatogramSelection<?, ?>> chromatogramReferencesListener) {

			this.listener = chromatogramReferencesListener;
		}

		private void selectChromatogram(int index) {

			if(index < 0) {
				index = 0;
			} else if(index >= data.size()) {
				index = data.size() - 1;
			}
			setSelection(new StructuredSelection(data.get(index)));
		}

		private int currentIndex() {

			return indexOf((IChromatogramSelection<?, ?>)selection.getFirstElement());
		}

		public int indexOf(IChromatogramSelection<?, ?> chromatogramSelection) {

			if(data != null) {
				return data.indexOf(chromatogramSelection);
			}
			return -1;
		}

		@Override
		public void selectionChanged(SelectionChangedEvent event) {

			selection = (IStructuredSelection)event.getSelection();
			listener.accept((IChromatogramSelection<?, ?>)selection.getFirstElement());
		}

		private void setSelection(IStructuredSelection selection) {

			ComboViewer viewer = viewerReference.get();
			if(viewer != null) {
				viewer.setSelection(selection);
			}
			this.selection = selection;
		}

		private void refreshUI() {

			setInput(data);
			setSelection(selection);
			ComboViewer viewer = viewerReference.get();
			if(viewer != null) {
				viewer.refresh();
			}
		}

		private void setInput(List<IChromatogramSelection<?, ?>> data) {

			this.data = data;
			ComboViewer viewer = viewerReference.get();
			if(viewer != null) {
				viewer.setInput(data != null ? data : Collections.emptyList());
				Control control = viewer.getControl();
				if(!control.isDisposed()) {
					control.setEnabled(data != null && data.size() > 1);
				}
			}
		}
	}
}