/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.swt.EditorToolBar;
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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;

public class ChromatogramReferencesUI {

	private static final Logger logger = Logger.getLogger(ChromatogramReferencesUI.class);
	//
	private final Action referencesAction;
	private final EditorToolBar toolBar;
	private final ComboContainer comboChromatograms;
	//
	private Action buttonPrevious;
	private Action buttonNext;
	private Action buttonAdd;
	private Action buttonImport;
	private Action buttonRemove;
	private Action buttonRemoveAll;
	private Action buttonOpen;
	//
	private HashMap<IChromatogram<?>, IChromatogramSelection<?, ?>> referenceSelections = new HashMap<>();
	private IUpdateListener updateListener;

	public ChromatogramReferencesUI(EditorToolBar editorToolBar, Consumer<IChromatogramSelection<?, ?>> chromatogramReferencesListener) {

		comboChromatograms = new ComboContainer(chromatogramReferencesListener.andThen(t -> updateButtons()));
		referencesAction = createReferencesAction();
		editorToolBar.addAction(referencesAction);
		referencesAction.setChecked(false);
		toolBar = editorToolBar.createChild();
		initialize();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public boolean isComboVisible() {

		return referencesAction.isChecked();
	}

	public void setComboVisible(boolean visible) {

		toolBar.setVisible(visible);
		referencesAction.setChecked(visible);
		updateButtons();
	}

	public List<IChromatogramSelection<?, ?>> getChromatogramSelections() {

		return Collections.unmodifiableList(comboChromatograms.data);
	}

	public void update() {

		List<IChromatogramSelection<?, ?>> chromatogramMasterAndReferences = new ArrayList<>();
		//
		if(comboChromatograms.master != null) {
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
		comboChromatograms.setInput(chromatogramMasterAndReferences);
		comboChromatograms.refreshUI();
	}

	private Action createReferencesAction() {

		Action action = new Action("References", Action.AS_CHECK_BOX) {

			@Override
			public void run() {

				toolBar.setVisible(isChecked());
				if(isChecked()) {
					updateButtons();
				}
				//
				if(updateListener != null) {
					updateListener.update();
				}
			}

			@Override
			public void setChecked(boolean checked) {

				setActionChecked(this, checked);
				super.setChecked(checked);
			}
		};
		//
		return action;
	}

	private void setActionChecked(Action action, boolean checked) {

		if(checked) {
			action.setToolTipText("Collapse the references items");
			action.setImageDescriptor(ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_COLLAPSE_ALL, IApplicationImage.SIZE_16x16));
		} else {
			action.setToolTipText("Expand the references items");
			action.setImageDescriptor(ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_EXPAND_ALL, IApplicationImage.SIZE_16x16));
		}
	}

	private IChromatogramSelection<?, ?> createChromatogramSelection(IChromatogram<?> referencedChromatogram) {

		if(referencedChromatogram instanceof IChromatogramCSD) {
			return new ChromatogramSelectionCSD((IChromatogramCSD)referencedChromatogram);
		} else if(referencedChromatogram instanceof IChromatogramMSD) {
			return new ChromatogramSelectionMSD((IChromatogramMSD)referencedChromatogram);
		} else if(referencedChromatogram instanceof IChromatogramWSD) {
			return new ChromatogramSelectionWSD((IChromatogramWSD)referencedChromatogram);
		} else {
			return null;
		}
	}

	public void setMasterChromatogram(IChromatogramSelection<?, ?> chromatogramSelection) {

		if(chromatogramSelection != null && comboChromatograms.master != chromatogramSelection) {
			comboChromatograms.master = chromatogramSelection;
			update();
			comboChromatograms.setSelection(new StructuredSelection(chromatogramSelection));
			chromatogramSelection.update(true);
			updateButtons();
		}
	}

	private void initialize() {

		buttonPrevious = createButtonSelectPreviousChromatogram(toolBar);
		createComboChromatograms(toolBar);
		buttonNext = createButtonSelectNextChromatogram(toolBar);
		buttonRemove = createButtonRemoveReference(toolBar);
		buttonRemoveAll = createButtonRemoveReferenceAll(toolBar);
		buttonAdd = createButtonAddReference(toolBar);
		buttonImport = createButtonImportReferences(toolBar);
		buttonOpen = createButtonOpenReference(toolBar);
		toolBar.addSeparator();
	}

	private Action createButtonSelectPreviousChromatogram(EditorToolBar toolBar) {

		Action action = new Action("Previous", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_ARROW_BACKWARD, IApplicationImage.SIZE_16x16)) {

			{
				setToolTipText("Select previous chromatogram.");
			}

			@Override
			public void run() {

				int index = comboChromatograms.currentIndex();
				comboChromatograms.selectChromatogram(index - 1);
				updateButtons();
			}
		};
		toolBar.addAction(action);
		return action;
	}

	private void createComboChromatograms(EditorToolBar toolBar) {

		toolBar.createCombo(viewer -> {
			ComboViewer oldViewer = comboChromatograms.viewerReference.getAndSet(viewer);
			if(oldViewer != null) {
				oldViewer.removeSelectionChangedListener(comboChromatograms);
			}
			viewer.addSelectionChangedListener(comboChromatograms);
			Control control = viewer.getControl();
			control.setToolTipText("Select a referenced chromatogram.");
			control.addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent e) {

					if(comboChromatograms.viewerReference.compareAndSet(viewer, null)) {
						viewer.removeSelectionChangedListener(comboChromatograms);
						comboChromatograms.refreshUI();
					}
				}
			});
			viewer.setLabelProvider(new LabelProvider() {

				@Override
				public String getText(Object element) {

					if(element instanceof IChromatogramSelection<?, ?>) {
						IChromatogramSelection<?, ?> selection = (IChromatogramSelection<?, ?>)element;
						int index = comboChromatograms.indexOf(selection);
						if(index > -1) {
							/*
							 * Get the information to display.
							 */
							IChromatogram<?> chromatogram = selection.getChromatogram();
							return ChromatogramDataSupport.getReferenceLabel(chromatogram, index, true);
						}
					}
					return "N/A";
				}
			});
			comboChromatograms.refreshUI();
		}, true, 300);
	}

	private Action createButtonSelectNextChromatogram(EditorToolBar toolBar) {

		Action action = new Action("Next", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_ARROW_FORWARD, IApplicationImage.SIZE_16x16)) {

			{
				setToolTipText("Select next chromatogram.");
			}

			@Override
			public void run() {

				int index = comboChromatograms.currentIndex();
				comboChromatograms.selectChromatogram(index + 1);
				updateButtons();
			}
		};
		toolBar.addAction(action);
		return action;
	}

	private Action createButtonRemoveReference(EditorToolBar toolBar) {

		Action action = new Action("Delete", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16)) {

			{
				setToolTipText("Remove the reference chromatogram.");
			}

			@Override
			public void runWithEvent(Event event) {

				ToolItem item = (ToolItem)event.widget;
				int index = comboChromatograms.currentIndex();
				if(index > 0 && comboChromatograms.master != null) {
					if(MessageDialog.openQuestion(item.getParent().getShell(), "Delete Reference", "Do you want to delete the chromatogram reference: " + index + "?")) {
						IChromatogram<?> chromatogram = comboChromatograms.master.getChromatogram();
						IChromatogramSelection<?, ?> remove = comboChromatograms.data.remove(index);
						chromatogram.removeReferencedChromatogram(remove.getChromatogram());
						comboChromatograms.selection = new StructuredSelection(comboChromatograms.master);
						comboChromatograms.refreshUI();
					}
				}
			}
		};
		toolBar.addAction(action);
		return action;
	}

	private Action createButtonRemoveReferenceAll(EditorToolBar toolBar) {

		Action action = new Action("Delete All", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImage.SIZE_16x16)) {

			{
				setToolTipText("Remove all reference chromatogram(s).");
			}

			@Override
			public void runWithEvent(Event event) {

				ToolItem item = (ToolItem)event.widget;
				int index = comboChromatograms.currentIndex();
				if(index == 0 && comboChromatograms.master != null) {
					if(MessageDialog.openQuestion(item.getParent().getShell(), "Delete Reference(s)", "Do you want to delete all chromatogram reference(s)?")) {
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
		};
		toolBar.addAction(action);
		return action;
	}

	private Action createButtonAddReference(EditorToolBar toolBar) {

		Action action = new Action("Add", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16)) {

			{
				setToolTipText("Add a reference chromatogram.");
			}

			@Override
			public void runWithEvent(Event event) {

				ToolItem item = (ToolItem)event.widget;
				ChromatogramEditorDialog dialog = new ChromatogramEditorDialog(item.getParent().getShell(), comboChromatograms.master.getChromatogram());
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
								MessageDialog.openWarning(item.getParent().getShell(), "Add Reference", "You can't add the selected chromatogram as a reference.");
							}
						}
					}
				}
			}
		};
		toolBar.addAction(action);
		return action;
	}

	private Action createButtonImportReferences(EditorToolBar toolBar) {

		Action action = new Action("Import", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_IMPORT, IApplicationImage.SIZE_16x16)) {

			{
				setToolTipText("Import reference chromatograms.");
			}

			@Override
			public void runWithEvent(Event event) {

				DataTypeDialog dataTypeDialog = new DataTypeDialog(event.display.getActiveShell(), new DataType[]{DataType.MSD, DataType.CSD, DataType.WSD});
				if(IDialogConstants.OK_ID == dataTypeDialog.open()) {
					DataType dataType = dataTypeDialog.getDataType();
					InputWizardSettings inputWizardSettings = InputWizardSettings.create(Activator.getDefault().getPreferenceStore(), dataType);
					inputWizardSettings.setTitle("Select Chromatograms");
					inputWizardSettings.setDescription("Select chromatogram that will be added as references.");
					Set<File> chromatogramFiles = InputEntriesWizard.openWizard(event.display.getActiveShell(), inputWizardSettings).keySet();
					if(!chromatogramFiles.isEmpty()) {
						IChromatogramSelection<?, ?> masterSelection = comboChromatograms.master;
						if(masterSelection != null) {
							//
							List<File> files = new ArrayList<>(chromatogramFiles);
							ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(event.display.getActiveShell());
							ChromatogramImportRunnable runnable = new ChromatogramImportRunnable(files, dataType);
							try {
								progressMonitorDialog.run(false, false, runnable);
								addReferences(masterSelection, runnable.getChromatogramSelections());
								comboChromatograms.refreshUI();
								updateButtons();
							} catch(Exception e) {
								logger.error(e.getLocalizedMessage(), e);
							}
						}
					}
				}
			}
		};
		toolBar.addAction(action);
		return action;
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
			comboChromatograms.data.add(chromatogramSelection);
		}
	}

	private Action createButtonOpenReference(EditorToolBar toolBar) {

		Action action = new Action("Open", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16)) {

			{
				setToolTipText("Open the chromatogram in a separate editor.");
			}

			@Override
			public void runWithEvent(Event event) {

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
					event.display.asyncExec(new Runnable() {

						@Override
						public void run() {

							supplierEditorSupport.openEditor(chromatogram);
						}
					});
				}
			}
		};
		toolBar.addAction(action);
		return action;
	}

	private void updateButtons() {

		try {
			if(toolBar.isVisible()) {
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
				viewer.setInput(data != null ? data : Collections.EMPTY_LIST);
				Control control = viewer.getControl();
				if(!control.isDisposed()) {
					control.setEnabled(data.size() > 1);
				}
			}
		}
	}
}
