/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring to a separate class
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.dialogs;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.fill;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.eclipse.chemclipse.model.core.Classifiable;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramPeak;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinition;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinitionProvider;
import org.eclipse.chemclipse.support.ui.swt.columns.SimpleColumnDefinition;
import org.eclipse.chemclipse.support.ui.swt.dialogs.ContentProposalInputDialog;
import org.eclipse.chemclipse.support.ui.swt.edit.ListEdit;
import org.eclipse.chemclipse.support.ui.swt.edit.ListEditModel;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public final class ClassifierDialog extends Dialog implements ColumnDefinitionProvider, IContentProposalProvider {

	private final Classifiable classifiable;
	private ListEdit<String> listEdit;
	private IContentProposal[] proposals;

	public ClassifierDialog(Shell parentShell, Classifiable classifiable) {

		super(parentShell);
		this.classifiable = classifiable;
	}

	@Override
	public Collection<? extends ColumnDefinition<?, ?>> getColumnDefinitions() {

		return Collections.singleton(new SimpleColumnDefinition<>("Classification", 400, Function.identity()));
	}

	public List<String> getValue() {

		return listEdit.getValue();
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {

		if(proposals == null) {
			if(classifiable instanceof IChromatogramPeak) {
				Set<String> treeSetClassifier = new TreeSet<>();
				IChromatogram<?> chromatogram = ((IChromatogramPeak)classifiable).getChromatogram();
				for(IPeak peak : chromatogram.getPeaks()) {
					treeSetClassifier.addAll(peak.getClassifier());
				}
				String[] array = treeSetClassifier.toArray(new String[0]);
				proposals = new IContentProposal[array.length];
				for(int i = 0; i < proposals.length; i++) {
					proposals[i] = new ContentProposal(array[i]);
				}
			} else {
				proposals = new IContentProposal[0];
			}
		}
		return proposals;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite)super.createDialogArea(parent);
		listEdit = createListEdit(container);
		fill(listEdit.getControl());
		//
		return container;
	}

	@Override
	protected Point getInitialSize() {

		return new Point(600, 400);
	}

	@Override
	protected void configureShell(Shell newShell) {

		super.configureShell(newShell);
		newShell.setText("Edit Classification");
	}

	private ListEdit<String> createListEdit(Composite parent) {

		ListEdit<String> listEdit = new ListEdit<>(parent, this, new ListEditModel<String>() {

			@Override
			public Collection<? extends String> list() {

				return classifiable.getClassifier();
			}

			@Override
			public boolean canCreate() {

				return true;
			}

			@Override
			public boolean canDelete(String item) {

				return true;
			}

			@Override
			public String create() {

				ContentProposalInputDialog dialog = new ContentProposalInputDialog(getShell(), "Add Classification", "Enter a new Classification", "", null, ClassifierDialog.this);
				if(dialog.open() == Window.OK) {
					return dialog.getValue();
				}
				return null;
			}
		});
		//
		return listEdit;
	}
}