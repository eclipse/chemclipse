/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.checkbox;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createLabelContainer;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.indentedContainer;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.label;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.radiobutton;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.wizards.SinglePageWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings.LibraryField;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.WorkspaceTargetDisplaySettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class TargetDisplaySettingsWizard {

	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 600;

	public static TargetDisplaySettings openWizard(Shell shell, Collection<? extends ITargetSupplier> identifications, WorkspaceTargetDisplaySettings currentSettings) {

		WizardDialog wizardDialog = new WizardDialog(shell, new SinglePageWizard("Target Label Settings", new TargetDisplaySettingsPage(identifications, currentSettings)));
		wizardDialog.setMinimumPageSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		if(wizardDialog.open() == Window.OK) {
			// try {
			// workspaceSettings.node.flush();
			// } catch(BackingStoreException e) {
			// // can't flush then...
			// }
			return currentSettings;
		} else {
			return null;
		}
	}

	private static final class TargetDisplaySettingsPage extends WizardPage {

		private final Set<IIdentificationTarget> allTargets;
		private final WorkspaceTargetDisplaySettings settings;

		protected TargetDisplaySettingsPage(Collection<? extends ITargetSupplier> identifications, WorkspaceTargetDisplaySettings settings) {
			super(TargetDisplaySettingsPage.class.getName());
			this.settings = settings;
			setImageDescriptor(ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_LABELS, IApplicationImage.SIZE_128x128));
			setTitle("Manage target labels to display");
			setDescription("Here you can select what target labels should be displayed in the chromatogram");
			allTargets = new HashSet<>();
			for(ITargetSupplier identification : identifications) {
				IIdentificationTarget target = IIdentificationTarget.getBestIdentificationTarget(identification.getTargets(), TargetDisplaySettings.COMPARATOR);
				if(target != null) {
					allTargets.add(target);
				}
			}
		}

		@Override
		public void createControl(Composite parent) {

			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout());
			Button systemButton = radiobutton(composite, "Use System Options", settings.useSystemSettings());
			Composite systemContainer = indentedContainer(composite, 25);
			createEditorForSettings(systemContainer, settings.getSystemSettings());
			label("Changes here affect the display of all chromatograms", systemContainer);
			setControl(composite);
		}

		private void createEditorForSettings(Composite composite, TargetDisplaySettings editorSettings) {

			Button peakLabels = checkbox(composite, "Show Peak Labels", editorSettings.isShowPeakLabels());
			peakLabels.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					editorSettings.setShowPeakLabels(peakLabels.getSelection());
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
			Button scanLabels = checkbox(composite, "Show Scan Labels", editorSettings.isShowScanLables());
			scanLabels.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					editorSettings.setShowScanLables(scanLabels.getSelection());
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
			Composite container = createLabelContainer(composite);
			label("Display Field", container);
			ComboViewer comboViewer = new ComboViewer(container, SWT.READ_ONLY);
			comboViewer.setContentProvider(ArrayContentProvider.getInstance());
			comboViewer.setInput(LibraryField.values());
			comboViewer.setSelection(new StructuredSelection(editorSettings.getField()));
			comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {

					editorSettings.setField((LibraryField)comboViewer.getStructuredSelection().getFirstElement());
				}
			});
		}
	}
}
