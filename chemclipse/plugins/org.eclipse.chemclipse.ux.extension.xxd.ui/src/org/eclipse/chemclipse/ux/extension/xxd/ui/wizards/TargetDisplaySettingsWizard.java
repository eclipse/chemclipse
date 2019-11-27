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
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.maximize;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.radiobutton;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.separator;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.wizards.SinglePageWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.TargetsLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings.LibraryField;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.WorkspaceTargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.TargetsListUI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class TargetDisplaySettingsWizard {

	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 600;

	public static boolean openWizard(Shell shell, Collection<? extends ITargetSupplier> identifications, WorkspaceTargetDisplaySettings currentSettings) {

		TargetDisplaySettingsPage page = new TargetDisplaySettingsPage(identifications, currentSettings);
		SinglePageWizard wizard = new SinglePageWizard("Target Label Settings", false, page);
		WizardDialog wizardDialog = new WizardDialog(shell, wizard);
		wizardDialog.setMinimumPageSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		wizardDialog.setPageSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		if(wizardDialog.open() == Window.OK) {
			if(page.useSystemSettings) {
				currentSettings.setUseSystemSettings(true);
				page.systemSettings.copyTo(currentSettings.getSystemSettings());
			} else {
				currentSettings.setUseSystemSettings(false);
				page.userSettings.copyTo(currentSettings.getUserSettings());
			}
			return true;
		} else {
			return false;
		}
	}

	private static final class WizardTargetDisplaySettings implements TargetDisplaySettings {

		private boolean showPeakLabels;
		private boolean showScanLables;
		private LibraryField libraryField;
		private final Map<String, Boolean> visibleMap = new HashMap<>();
		private final TargetDisplaySettings base;

		public WizardTargetDisplaySettings(TargetDisplaySettings base) {
			this.base = base;
			showPeakLabels = base.isShowPeakLabels();
			showScanLables = base.isShowScanLables();
			libraryField = base.getField();
		}

		public void copyTo(TargetDisplaySettings other) {

			other.setField(libraryField);
			other.setShowPeakLabels(showPeakLabels);
			other.setShowScanLables(showScanLables);
			if(other instanceof WorkspaceTargetDisplaySettings) {
				WorkspaceTargetDisplaySettings workspaceSettings = (WorkspaceTargetDisplaySettings)other;
				workspaceSettings.updateVisible(visibleMap);
				workspaceSettings.flush();
			}
		}

		@Override
		public boolean isShowPeakLabels() {

			return showPeakLabels;
		}

		@Override
		public boolean isShowScanLables() {

			return showScanLables;
		}

		@Override
		public void setShowPeakLabels(boolean showPeakLabels) {

			this.showPeakLabels = showPeakLabels;
		}

		@Override
		public void setShowScanLables(boolean showScanLables) {

			this.showScanLables = showScanLables;
		}

		@Override
		public LibraryField getField() {

			return libraryField;
		}

		@Override
		public void setField(LibraryField libraryField) {

			this.libraryField = libraryField;
		}

		@Override
		public boolean isVisible(IIdentificationTarget target) {

			String id = WorkspaceTargetDisplaySettings.getID(target, libraryField);
			return visibleMap.computeIfAbsent(id, t -> base.isVisible(target));
		}

		public void setVisible(IIdentificationTarget target, boolean visible) {

			visibleMap.put(WorkspaceTargetDisplaySettings.getID(target, libraryField), visible);
		}
	}

	private static final class TargetDisplaySettingsPage extends WizardPage {

		boolean useSystemSettings;
		final WizardTargetDisplaySettings systemSettings;
		final WizardTargetDisplaySettings userSettings;
		private final Collection<? extends ITargetSupplier> identifications;

		protected TargetDisplaySettingsPage(Collection<? extends ITargetSupplier> identifications, WorkspaceTargetDisplaySettings settings) {
			super(TargetDisplaySettingsPage.class.getName());
			this.identifications = identifications;
			setImageDescriptor(ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_LABELS, IApplicationImage.SIZE_64x64));
			setTitle("Manage target labels to display");
			setDescription("Here you can select what target labels should be displayed in the chromatogram");
			useSystemSettings = settings.isUseSystemSettings();
			systemSettings = new WizardTargetDisplaySettings(settings.getSystemSettings());
			userSettings = new WizardTargetDisplaySettings(settings.getUserSettings());
		}

		@Override
		public void createControl(Composite parent) {

			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout());
			Button systemButton = radiobutton(composite, "Use System Settings", useSystemSettings);
			Composite systemContainer = indentedContainer(composite, 25);
			Label infoLabel = label("Changes here affect the global defaults and apply if no individual settings are used", systemContainer);
			BaseTargetSettingEditor systemEditor = new BaseTargetSettingEditor(systemContainer, systemSettings);
			separator(composite);
			Button userButton = radiobutton(composite, "Use Individual Settings", !useSystemSettings);
			Composite userContainer = maximize(indentedContainer(composite, 25));
			BaseTargetSettingEditor userEditor = new BaseTargetSettingEditor(userContainer, userSettings);
			String[] alternativeTitles = TargetsLabelProvider.TITLES.clone();
			alternativeTitles[0] = "";
			TargetsListUI listUI = new TargetsListUI(userContainer, alternativeTitles, SWT.NONE);
			maximize(listUI.getControl());
			TableViewerColumn column = listUI.getTableViewerColumns().get(0);
			column.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {

					return "";
				}

				@Override
				public Image getImage(Object element) {

					if(element instanceof IIdentificationTarget) {
						IIdentificationTarget identificationTarget = (IIdentificationTarget)element;
						if(userSettings.isVisible(identificationTarget)) {
							return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED, IApplicationImage.SIZE_16x16);
						} else {
							return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED, IApplicationImage.SIZE_16x16);
						}
					}
					return super.getImage(element);
				}
			});
			column.setEditingSupport(new EditingSupport(listUI) {

				CheckboxCellEditor cellEditor = new CheckboxCellEditor(listUI.getTable());

				@Override
				protected void setValue(Object element, Object value) {

					userSettings.setVisible((IIdentificationTarget)element, (Boolean)value);
					listUI.refresh(element);
				}

				@Override
				protected Object getValue(Object element) {

					return userSettings.isVisible((IIdentificationTarget)element);
				}

				@Override
				protected CellEditor getCellEditor(Object element) {

					return cellEditor;
				}

				@Override
				protected boolean canEdit(Object element) {

					return element instanceof IIdentificationTarget;
				}
			});
			ISelectionChangedListener comboListener = new ISelectionChangedListener() {

				private LibraryField currentField;

				@Override
				public void selectionChanged(SelectionChangedEvent event) {

					LibraryField field = userSettings.getField();
					if(field != currentField && !useSystemSettings) {
						try {
							Set<IIdentificationTarget> allTargets = new TreeSet<>(new Comparator<IIdentificationTarget>() {

								@Override
								public int compare(IIdentificationTarget o1, IIdentificationTarget o2) {

									return WorkspaceTargetDisplaySettings.getID(o1, field).compareTo(WorkspaceTargetDisplaySettings.getID(o2, field));
								}
							});
							getContainer().run(true, true, new IRunnableWithProgress() {

								@Override
								public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

									SubMonitor subMonitor = SubMonitor.convert(monitor, identifications.size());
									for(ITargetSupplier identification : identifications) {
										IIdentificationTarget target = IIdentificationTarget.getBestIdentificationTarget(identification.getTargets(), TargetDisplaySettings.COMPARATOR);
										if(target != null) {
											allTargets.add(target);
										}
										subMonitor.worked(1);
									}
								}
							});
							listUI.setInput(allTargets);
							currentField = field;
						} catch(InvocationTargetException e) {
							Activator.getDefault().getLog().log(new Status(IStatus.ERROR, getClass().getName(), "Error computing targets", e));
						} catch(InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				}
			};
			userEditor.comboViewer.addSelectionChangedListener(comboListener);
			SelectionListener buttonListener = new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					useSystemSettings = systemButton.getSelection();
					infoLabel.setEnabled(useSystemSettings);
					systemEditor.setEnabled(useSystemSettings);
					userEditor.setEnabled(!useSystemSettings);
					comboListener.selectionChanged(null);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			};
			systemButton.addSelectionListener(buttonListener);
			userButton.addSelectionListener(buttonListener);
			buttonListener.widgetSelected(null);
			setControl(composite);
		}
	}

	private static final class BaseTargetSettingEditor {

		private final Button peakLabels;
		private final Button scanLabels;
		private final Label fieldLabel;
		private final ComboViewer comboViewer;

		public BaseTargetSettingEditor(Composite composite, TargetDisplaySettings editorSettings) {
			peakLabels = checkbox(composite, "Show Peak Labels", editorSettings.isShowPeakLabels());
			peakLabels.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					editorSettings.setShowPeakLabels(peakLabels.getSelection());
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
			scanLabels = checkbox(composite, "Show Scan Labels", editorSettings.isShowScanLables());
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
			fieldLabel = label("Display Field", container);
			comboViewer = new ComboViewer(container, SWT.READ_ONLY);
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

		public void setEnabled(boolean enabled) {

			peakLabels.setEnabled(enabled);
			scanLabels.setEnabled(enabled);
			fieldLabel.setEnabled(enabled);
			comboViewer.getControl().setEnabled(enabled);
		}
	}
}
