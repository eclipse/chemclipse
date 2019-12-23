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
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createColumn;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createContainer;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createLabelContainer;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createTable;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.fill;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.gridData;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.indentedContainer;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.label;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.maximize;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.radiobutton;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.separator;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.span;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.swt.ControlBuilder;
import org.eclipse.chemclipse.support.ui.swt.columns.SimpleColumnDefinition;
import org.eclipse.chemclipse.support.ui.wizards.SinglePageWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings.LibraryField;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetReference;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.WorkspaceTargetDisplaySettings;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

public class TargetDisplaySettingsWizard {

	public static final int DEFAULT_WIDTH = 1000;
	public static final int DEFAULT_HEIGHT = 600;

	public static boolean openWizard(Shell shell, Collection<? extends TargetReference> identifications, TargetDisplaySettingsWizardListener listener, WorkspaceTargetDisplaySettings currentSettings) {

		TargetDisplaySettingsPage page = new TargetDisplaySettingsPage(identifications, currentSettings, listener);
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
			currentSettings.flush();
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
		private int rotation;

		public WizardTargetDisplaySettings(TargetDisplaySettings base) {
			this.base = base;
			showPeakLabels = base.isShowPeakLabels();
			showScanLables = base.isShowScanLables();
			libraryField = base.getField();
			rotation = base.getRotation();
		}

		public void copyTo(TargetDisplaySettings other) {

			other.setField(libraryField);
			other.setShowPeakLabels(showPeakLabels);
			other.setShowScanLables(showScanLables);
			if(other instanceof WorkspaceTargetDisplaySettings) {
				WorkspaceTargetDisplaySettings workspaceSettings = (WorkspaceTargetDisplaySettings)other;
				workspaceSettings.updateVisible(visibleMap);
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
		public boolean isVisible(TargetReference reference) {

			return visibleMap.computeIfAbsent(reference.getID(), t -> base.isVisible(reference));
		}

		public void setVisible(TargetReference reference, boolean visible) {

			visibleMap.put(reference.getID(), visible);
		}

		@Override
		public void setRotation(int degree) {

			rotation = degree;
		}

		@Override
		public int getRotation() {

			return rotation;
		}
	}

	private static final class TargetDisplaySettingsPage extends WizardPage {

		boolean useSystemSettings;
		boolean showPreview;
		final WizardTargetDisplaySettings systemSettings;
		final WizardTargetDisplaySettings userSettings;
		private final Collection<? extends TargetReference> identifications;
		private TableViewer listUI;
		private final TargetDisplaySettingsWizardListener listener;
		private Predicate<TargetReference> predicate;

		protected TargetDisplaySettingsPage(Collection<? extends TargetReference> identifications, WorkspaceTargetDisplaySettings settings, TargetDisplaySettingsWizardListener listener) {
			super(TargetDisplaySettingsPage.class.getName());
			this.identifications = identifications;
			this.listener = listener;
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
			BaseTargetSettingEditor systemEditor = new BaseTargetSettingEditor(systemContainer, systemSettings, this);
			separator(composite);
			Button userButton = radiobutton(composite, "Use Individual Settings", !useSystemSettings);
			Composite userContainer = maximize(indentedContainer(composite, 25));
			BaseTargetSettingEditor userEditor = new BaseTargetSettingEditor(userContainer, userSettings, this);
			ToolBar toolbar = createToolbar(userContainer);
			listUI = createTargetTable(userContainer);
			listUI.setInput(identifications);
			maximize(listUI.getControl());
			ISelectionChangedListener comboListener = new ISelectionChangedListener() {

				private LibraryField currentField;

				@Override
				public void selectionChanged(SelectionChangedEvent event) {

					LibraryField field = userSettings.getField();
					if(field != currentField && !useSystemSettings) {
						currentField = field;
						listUI.refresh();
						notifyListener();
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
					toolbar.setEnabled(!useSystemSettings);
					listUI.getControl().setEnabled(!useSystemSettings);
					notifyListener();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			};
			systemButton.addSelectionListener(buttonListener);
			userButton.addSelectionListener(buttonListener);
			separator(composite);
			if(listener != null) {
				Button previewCheckbox = checkbox(composite, "Show preview in editor", showPreview);
				previewCheckbox.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {

						showPreview = previewCheckbox.getSelection();
						notifyListener();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {

					}
				});
			}
			buttonListener.widgetSelected(null);
			setControl(composite);
		}

		private void notifyListener() {

			if(listener != null) {
				if(showPreview) {
					if(useSystemSettings) {
						listener.setPreviewSettings(systemSettings, t -> true);
					} else {
						listener.setPreviewSettings(userSettings, predicate);
					}
				} else {
					listener.setPreviewSettings(null, t -> true);
				}
			}
		}

		private TableViewer createTargetTable(Composite parent) {

			TableViewer tableViewer = createTable(parent, false);
			createColumn(tableViewer, new SimpleColumnDefinition<>("", 18, new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {

					return "";
				}

				@Override
				public Image getImage(Object element) {

					if(element instanceof TargetReference) {
						TargetReference identificationTarget = (TargetReference)element;
						if(userSettings.isVisible(identificationTarget)) {
							return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED, IApplicationImage.SIZE_16x16);
						} else {
							return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED, IApplicationImage.SIZE_16x16);
						}
					}
					return super.getImage(element);
				}
			}).minWidth(18).resizable(false)).setEditingSupport(new EditingSupport(tableViewer) {

				CheckboxCellEditor cellEditor = new CheckboxCellEditor(tableViewer.getTable());

				@Override
				protected void setValue(Object element, Object value) {

					userSettings.setVisible((TargetReference)element, (Boolean)value);
					listUI.refresh(element);
					notifyListener();
				}

				@Override
				protected Object getValue(Object element) {

					return userSettings.isVisible((TargetReference)element);
				}

				@Override
				protected CellEditor getCellEditor(Object element) {

					return cellEditor;
				}

				@Override
				protected boolean canEdit(Object element) {

					return element instanceof TargetReference;
				}
			});
			createColumn(tableViewer, new SimpleColumnDefinition<>(listener != null ? listener.getIDLabel() : "ID", 80, TargetReference::getName));
			createColumn(tableViewer, new SimpleColumnDefinition<>("Type", 50, TargetReference::getType));
			for(LibraryField field : LibraryField.values()) {
				createColumn(tableViewer, new SimpleColumnDefinition<>(field.toString(), 100, new ColumnLabelProvider() {

					@Override
					public String getText(Object element) {

						if(element instanceof TargetReference) {
							TargetReference reference = (TargetReference)element;
							return field.stringTransformer().apply(reference.getBestTarget());
						}
						return "";
					}
				}));
			}
			return tableViewer;
		}

		private ToolBar createToolbar(Composite parent) {

			ToolBarManager manager = new ToolBarManager();
			manager.add(new Action("Enable All", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_SELECTED, IApplicationImage.SIZE_16x16)) {

				@Override
				public void run() {

					for(TableItem item : listUI.getTable().getItems()) {
						Object data = item.getData();
						if(data instanceof TargetReference) {
							TargetReference target = (TargetReference)data;
							userSettings.setVisible(target, true);
							listUI.refresh(target);
							notifyListener();
						}
					}
				}
			});
			manager.add(new Action("Disable All", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_DESELECTED, IApplicationImage.SIZE_16x16)) {

				@Override
				public void run() {

					for(TableItem item : listUI.getTable().getItems()) {
						Object data = item.getData();
						if(data instanceof TargetReference) {
							TargetReference target = (TargetReference)data;
							userSettings.setVisible(target, false);
							listUI.refresh(target);
							notifyListener();
						}
					}
				}
			});
			manager.add(new Separator());
			manager.add(new ControlContribution("searchbar") {

				@Override
				protected Control createControl(Composite parent) {

					Composite container = createContainer(parent, 3);
					Text text = ControlBuilder.fill(new Text(container, SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH));
					gridData(text).horizontalIndent = 3;
					text.addModifyListener(new ModifyListener() {

						@Override
						public void modifyText(ModifyEvent e) {

							String filterText = text.getText();
							updateFilter(filterText);
						}
					});
					return container;
				}

				@Override
				protected int computeWidth(Control control) {

					return Math.max(super.computeWidth(control), DEFAULT_WIDTH - 50);
				}
			});
			return manager.createControl(parent);
		}

		private void updateFilter(String text) {

			if(text == null || text.isEmpty()) {
				listUI.resetFilters();
				predicate = null;
			} else {
				predicate = new Predicate<TargetReference>() {

					@Override
					public boolean test(TargetReference target) {

						String label = userSettings.getField().stringTransformer().apply(target.getBestTarget());
						return label != null && label.toLowerCase().contains(text.toLowerCase());
					}
				};
				listUI.setFilters(new ViewerFilter() {

					@Override
					public boolean select(Viewer viewer, Object parentElement, Object element) {

						if(element instanceof TargetReference) {
							TargetReference target = (TargetReference)element;
							return predicate.test(target);
						}
						return true;
					}
				});
			}
			notifyListener();
		}
	}

	private static final class BaseTargetSettingEditor {

		private final Button peakLabels;
		private final Button scanLabels;
		private final Label fieldLabel;
		private final ComboViewer comboViewer;
		private final Label rotationLabel;
		private final Scale scale;

		public BaseTargetSettingEditor(Composite composite, TargetDisplaySettings editorSettings, TargetDisplaySettingsPage page) {
			Composite container = createLabelContainer(composite);
			peakLabels = span(checkbox(container, "Show Peak Labels", editorSettings.isShowPeakLabels()), 2);
			peakLabels.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					editorSettings.setShowPeakLabels(peakLabels.getSelection());
					page.notifyListener();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
			scanLabels = span(checkbox(container, "Show Scan Labels", editorSettings.isShowScanLables()), 2);
			scanLabels.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					editorSettings.setShowScanLables(scanLabels.getSelection());
					page.notifyListener();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
			fieldLabel = label("Display Field", container);
			comboViewer = new ComboViewer(container, SWT.READ_ONLY);
			comboViewer.setContentProvider(ArrayContentProvider.getInstance());
			comboViewer.setInput(LibraryField.values());
			comboViewer.setSelection(new StructuredSelection(editorSettings.getField()));
			comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {

					editorSettings.setField((LibraryField)comboViewer.getStructuredSelection().getFirstElement());
					page.notifyListener();
				}
			});
			rotationLabel = label(getRotationText(editorSettings.getRotation()), container);
			scale = fill(new Scale(container, SWT.HORIZONTAL));
			scale.setMinimum(0);
			scale.setMaximum(90);
			scale.setIncrement(1);
			scale.setSelection(editorSettings.getRotation());
			scale.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					int selection = scale.getSelection();
					editorSettings.setRotation(selection);
					rotationLabel.setText(getRotationText(selection));
					page.notifyListener();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
		}

		private String getRotationText(int value) {

			StringBuilder sb = new StringBuilder();
			sb.append("Rotation (");
			if(value < 10) {
				sb.append(' ');
			}
			sb.append(value);
			sb.append("°)");
			return sb.toString();
		}

		public void setEnabled(boolean enabled) {

			peakLabels.setEnabled(enabled);
			scanLabels.setEnabled(enabled);
			fieldLabel.setEnabled(enabled);
			rotationLabel.setEnabled(enabled);
			comboViewer.getControl().setEnabled(enabled);
			scale.setEnabled(enabled);
		}
	}
}
