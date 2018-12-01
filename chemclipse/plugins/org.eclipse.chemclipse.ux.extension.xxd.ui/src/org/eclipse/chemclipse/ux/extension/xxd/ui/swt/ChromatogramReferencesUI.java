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
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("rawtypes")
public class ChromatogramReferencesUI extends Composite {

	private Combo comboChromatograms;
	//
	private IChromatogramSelection chromatogramSelection = null; // Updated
	private List<IChromatogramSelection> referencedChromatogramSelections = null; // Init on first update.
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();

	public ChromatogramReferencesUI(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	@SuppressWarnings("rawtypes")
	public void updateChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
		updateReferencedChromatograms(chromatogramSelection);
	}

	public void clear() {

		this.chromatogramSelection = null;
		referencedChromatogramSelections = null;
		comboChromatograms.setItems(new String[0]);
	}

	private void initialize() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(5, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		//
		createButtonSelectPreviousChromatogram(composite);
		comboChromatograms = createComboChromatograms(composite);
		createButtonSelectNextChromatogram(composite);
		createButtonRemoveReference(composite);
		createButtonAddReference(composite);
	}

	private void createButtonSelectPreviousChromatogram(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select previous chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_BACKWARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = comboChromatograms.getSelectionIndex();
				index--;
				index = (index < 0) ? 0 : index;
				selectChromatogram(index);
			}
		});
	}

	private Combo createComboChromatograms(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setToolTipText("Select a referenced chromatogram.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(referencedChromatogramSelections != null) {
					int index = combo.getSelectionIndex();
					selectChromatogram(index);
					System.out.println("TODO");
					// chromatogramActionUI.setChromatogramActionMenu(chromatogramSelection);
					// fireUpdate();
				}
			}
		});
		//
		return combo;
	}

	private void createButtonSelectNextChromatogram(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select next chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_FORWARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = comboChromatograms.getSelectionIndex();
				index++;
				index = (index >= comboChromatograms.getItemCount()) ? comboChromatograms.getItemCount() - 1 : index;
				selectChromatogram(index);
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private void selectChromatogram(int index) {

		comboChromatograms.select(index);
		if(referencedChromatogramSelections != null) {
			IChromatogramSelection chromatogramSelection = referencedChromatogramSelections.get(index);
			if(chromatogramSelection != null) {
				updateChromatogramSelection(chromatogramSelection);
			}
		}
	}

	private void createButtonRemoveReference(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Remove the reference chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_REMOVE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});
	}

	private void createButtonAddReference(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add a reference chromatogram.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});
	}

	/*
	 * Update the referenced chromatogram selections once on initialization.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private void updateReferencedChromatograms(IChromatogramSelection chromatogramSelection) {

		if(referencedChromatogramSelections == null && chromatogramSelection != null) {
			/*
			 * Collect the references
			 */
			referencedChromatogramSelections = new ArrayList<>();
			referencedChromatogramSelections.add(chromatogramSelection);
			List<IChromatogram> referencedChromatograms = chromatogramSelection.getChromatogram().getReferencedChromatograms();
			for(IChromatogram referencedChromatogram : referencedChromatograms) {
				if(referencedChromatogram instanceof IChromatogramCSD) {
					referencedChromatogramSelections.add(new ChromatogramSelectionCSD(referencedChromatogram));
				} else if(referencedChromatogram instanceof IChromatogramMSD) {
					referencedChromatogramSelections.add(new ChromatogramSelectionMSD(referencedChromatogram));
				} else if(referencedChromatogram instanceof IChromatogramWSD) {
					referencedChromatogramSelections.add(new ChromatogramSelectionWSD(referencedChromatogram));
				}
			}
			//
			int size = referencedChromatogramSelections.size();
			String[] references = new String[size];
			for(int i = 0; i < size; i++) {
				IChromatogramSelection chromatogramSelectionX = referencedChromatogramSelections.get(i);
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
	}
}
