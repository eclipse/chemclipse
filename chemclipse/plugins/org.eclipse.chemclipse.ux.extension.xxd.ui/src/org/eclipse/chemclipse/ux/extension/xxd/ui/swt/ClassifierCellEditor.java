/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.fill;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
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
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ClassifierCellEditor extends DialogCellEditor {

	public ClassifierCellEditor(TableViewer tableViewer) {
		super(tableViewer.getTable());
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {

		System.out.println("ClassifierCellEditor.openDialogBox()");
		Object value = getValue();
		if(value instanceof Classifiable) {
			Classifiable classifiable = (Classifiable)value;
			ClassifierDialog dialog = new ClassifierDialog(cellEditorWindow.getShell(), classifiable);
			if(dialog.open() == Window.OK) {
				return new LinkedHashSet<>(dialog.listEdit.getValue());
			}
		}
		return value;
	}

	@Override
	protected void updateContents(Object value) {

		if(value instanceof Classifiable) {
			Label label = getDefaultLabel();
			if(label != null && !label.isDisposed()) {
				Collection<String> classifier = ((Classifiable)value).getClassifier();
				if(classifier.isEmpty()) {
					label.setText("");
				} else if(classifier.size() == 1) {
					label.setText(classifier.iterator().next());
				} else {
					label.setText(classifier.size() + " Classifier");
				}
				return;
			}
		}
		super.updateContents(value);
	}

	private static final class ClassifierDialog extends Dialog implements ColumnDefinitionProvider, IContentProposalProvider {

		private ListEdit<String> listEdit;
		private final Classifiable classifiable;
		private IContentProposal[] proposals;

		protected ClassifierDialog(Shell parentShell, Classifiable classifiable) {
			super(parentShell);
			this.classifiable = classifiable;
		}

		@Override
		protected Control createDialogArea(Composite parent) {

			Composite container = (Composite)super.createDialogArea(parent);
			listEdit = new ListEdit<>(container, this, new ListEditModel<String>() {

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

					ContentProposalInputDialog dialog = new ContentProposalInputDialog(getShell(), "Add Classification", "Enter new Classification", "", null, ClassifierDialog.this);
					if(dialog.open() == Window.OK) {
						return dialog.getValue();
					}
					return null;
				}
			});
			fill(listEdit.getControl());
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

		@Override
		public Collection<? extends ColumnDefinition<?, ?>> getColumnDefinitions() {

			return Collections.singleton(new SimpleColumnDefinition<>("Classification", 400, Function.identity()));
		}

		@Override
		public IContentProposal[] getProposals(String contents, int position) {

			if(proposals == null) {
				if(classifiable instanceof IChromatogramPeak) {
					Set<String> set = new TreeSet<>();
					IChromatogram<?> chromatogram = ((IChromatogramPeak)classifiable).getChromatogram();
					for(IPeak peak : chromatogram.getPeaks()) {
						set.addAll(peak.getClassifier());
					}
					String[] array = set.toArray(new String[0]);
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
	}
}
