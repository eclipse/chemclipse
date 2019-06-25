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
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.dialogs.ChromatogramReferenceDialog;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramReferencesUI extends Composite {

	private Composite control;
	//
	private Button buttonExpand;
	private Button buttonPrevious;
	private Combo comboChromatograms;
	private Button buttonNext;
	private Button buttonAdd;
	private Button buttonRemove;
	//
	private boolean isExpanded = false;
	//
	private IChromatogramReferencesListener chromatogramReferencesListener;
	private List<IChromatogramSelection<?, ?>> chromatogramMasterAndReferences = null; // Init on first update.
	//
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();

	public ChromatogramReferencesUI(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public void setChromatogramReferencesListener(IChromatogramReferencesListener chromatogramReferencesListener) {

		this.chromatogramReferencesListener = chromatogramReferencesListener;
	}

	public void updateChromatogramSelection(IChromatogramSelection<?, ?> chromatogramSelection) {

		updateReferencedChromatograms(chromatogramSelection);
	}

	public void clear() {

		chromatogramMasterAndReferences = null;
		comboChromatograms.setItems(new String[0]);
	}

	private void initialize() {

		setLayout(new FillLayout());
		//
		control = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(6, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		control.setLayout(gridLayout);
		//
		buttonExpand = createButtonExpand(control);
		buttonPrevious = createButtonSelectPreviousChromatogram(control);
		comboChromatograms = createComboChromatograms(control);
		buttonNext = createButtonSelectNextChromatogram(control);
		buttonRemove = createButtonRemoveReference(control);
		buttonAdd = createButtonAddReference(control);
		//
		enableButtons();
		showWidgets(isExpanded);
	}

	private Button createButtonExpand(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Expand/Collapse the references items.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXPAND_ALL, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				isExpanded = !isExpanded;
				String image = isExpanded ? IApplicationImage.IMAGE_COLLAPSE_ALL : IApplicationImage.IMAGE_EXPAND_ALL;
				button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16));
				showWidgets(isExpanded);
			}
		});
		//
		return button;
	}

	private Button createButtonSelectPreviousChromatogram(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select previous chromatogram.");
		button.setLayoutData(new GridData());
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_BACKWARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = comboChromatograms.getSelectionIndex();
				index--;
				index = index < 0 ? 0 : index;
				selectChromatogram(index);
			}
		});
		//
		return button;
	}

	private Combo createComboChromatograms(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Select a referenced chromatogram.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(chromatogramMasterAndReferences != null) {
					int index = combo.getSelectionIndex();
					selectChromatogram(index);
				}
			}
		});
		//
		return combo;
	}

	private Button createButtonSelectNextChromatogram(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select next chromatogram.");
		button.setLayoutData(new GridData());
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_FORWARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = comboChromatograms.getSelectionIndex();
				index++;
				index = index >= comboChromatograms.getItemCount() ? comboChromatograms.getItemCount() - 1 : index;
				selectChromatogram(index);
			}
		});
		//
		return button;
	}

	private Button createButtonRemoveReference(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Remove the reference chromatogram.");
		button.setLayoutData(new GridData());
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_REMOVE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IChromatogramSelection<?, ?> masterSelection = getMasterSelection();
				if(masterSelection != null) {
					int index = comboChromatograms.getSelectionIndex();
					if(index > 0) {
						if(MessageDialog.openQuestion(e.display.getActiveShell(), "Delete Reference", "Do you want to delete the chromatogram reference: " + index)) {
							IChromatogram<?> chromatogram = masterSelection.getChromatogram();
							chromatogram.getReferencedChromatograms().remove(index - 1);
							reloadReferencedChromatograms(masterSelection, 0);
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
		button.setLayoutData(new GridData());
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {

				ChromatogramReferenceDialog dialog = new ChromatogramReferenceDialog(e.display.getActiveShell());
				if(IDialogConstants.OK_ID == dialog.open()) {
					IChromatogramSelection<?, ?> chromatogramSelection = dialog.getChromatogramSelection();
					if(chromatogramSelection != null) {
						IChromatogramSelection<?, ?> masterSelection = getMasterSelection();
						if(masterSelection != null) {
							if(masterSelection.getChromatogram() != chromatogramSelection.getChromatogram()) {
								List<IChromatogram> referencedChromatograms = masterSelection.getChromatogram().getReferencedChromatograms();
								referencedChromatograms.add(chromatogramSelection.getChromatogram());
								reloadReferencedChromatograms(masterSelection, referencedChromatograms.size());
							} else {
								MessageDialog.openWarning(e.display.getActiveShell(), "Add Reference", "You can't add the selected chromatogram as a reference.");
							}
						}
					}
				}
			}
		});
		//
		return button;
	}

	private void selectChromatogram(int index) {

		comboChromatograms.select(index);
		if(chromatogramMasterAndReferences != null) {
			int selectionIndex = comboChromatograms.getSelectionIndex();
			if(selectionIndex >= 0 && selectionIndex < chromatogramMasterAndReferences.size()) {
				IChromatogramSelection chromatogramSelection = chromatogramMasterAndReferences.get(selectionIndex);
				if(chromatogramSelection != null) {
					fireUpdate(chromatogramSelection);
				}
			}
		}
		enableButtons();
	}

	private IChromatogramSelection getMasterSelection() {

		if(chromatogramMasterAndReferences != null) {
			if(chromatogramMasterAndReferences.size() > 0) {
				return chromatogramMasterAndReferences.get(0);
			}
		}
		//
		return null;
	}

	private void reloadReferencedChromatograms(IChromatogramSelection chromatogramSelection, int index) {

		chromatogramMasterAndReferences = null;
		updateReferencedChromatograms(chromatogramSelection);
		selectChromatogram(index);
	}

	/*
	 * Update the referenced chromatogram selections once on initialization.
	 */
	private void updateReferencedChromatograms(IChromatogramSelection chromatogramSelection) {

		if(chromatogramMasterAndReferences == null && chromatogramSelection != null) {
			/*
			 * Collect the references
			 */
			chromatogramMasterAndReferences = new ArrayList<>();
			chromatogramMasterAndReferences.add(chromatogramSelection);
			List<IChromatogram> referencedChromatograms = chromatogramSelection.getChromatogram().getReferencedChromatograms();
			for(IChromatogram referencedChromatogram : referencedChromatograms) {
				if(referencedChromatogram instanceof IChromatogramCSD) {
					chromatogramMasterAndReferences.add(new ChromatogramSelectionCSD((IChromatogramCSD)referencedChromatogram));
				} else if(referencedChromatogram instanceof IChromatogramMSD) {
					chromatogramMasterAndReferences.add(new ChromatogramSelectionMSD((IChromatogramMSD)referencedChromatogram));
				} else if(referencedChromatogram instanceof IChromatogramWSD) {
					chromatogramMasterAndReferences.add(new ChromatogramSelectionWSD((IChromatogramWSD)referencedChromatogram));
				}
			}
			//
			int size = chromatogramMasterAndReferences.size();
			String[] references = new String[size];
			for(int i = 0; i < size; i++) {
				IChromatogramSelection chromatogramSelectionX = chromatogramMasterAndReferences.get(i);
				String type = chromatogramDataSupport.getChromatogramType(chromatogramSelectionX);
				if(i == 0) {
					references[i] = "Master Chromatogram " + type;
				} else {
					references[i] = "Referenced Chromatogram (" + i + ") " + type;
				}
			}
			//
			comboChromatograms.setItems(references);
			comboChromatograms.select(0);
		}
		//
		enableButtons();
	}

	private void enableButtons() {

		int size = chromatogramMasterAndReferences != null ? chromatogramMasterAndReferences.size() : 0;
		int selectionIndex = comboChromatograms.getSelectionIndex();
		//
		buttonExpand.setEnabled(selectionIndex >= 0);
		buttonPrevious.setEnabled(selectionIndex >= 1);
		comboChromatograms.setEnabled(selectionIndex >= 0);
		buttonNext.setEnabled(selectionIndex < size - 1);
		buttonRemove.setEnabled(selectionIndex > 0); // 0 is the master can can't be removed
		buttonAdd.setEnabled(selectionIndex == 0); // 0 references can be added only to master
		//
	}

	private void showWidgets(boolean expanded) {

		PartSupport.setControlVisibility(buttonPrevious, expanded);
		PartSupport.setControlVisibility(comboChromatograms, expanded);
		PartSupport.setControlVisibility(buttonNext, expanded);
		PartSupport.setControlVisibility(buttonRemove, expanded);
		PartSupport.setControlVisibility(buttonAdd, expanded);
		//
		Composite master = getMasterComposite();
		master.layout(true);
		master.redraw();
	}

	private Composite getMasterComposite() {

		Composite master = control;
		if(master.getParent() != null) {
			master = master.getParent();
			if(master.getParent() != null) {
				master = master.getParent();
				if(master.getParent() != null) {
					master = master.getParent();
				}
			}
		}
		//
		return master;
	}

	private void fireUpdate(IChromatogramSelection chromatogramSelection) {

		if(chromatogramReferencesListener != null) {
			chromatogramReferencesListener.update(chromatogramSelection);
		}
	}
}
