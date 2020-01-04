/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createDefault;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createLabelContainer;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createTable;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.fill;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.gridData;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.indentedContainer;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.label;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.maximize;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.radiobutton;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.separator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.swt.ControlBuilder;
import org.eclipse.chemclipse.support.ui.swt.columns.SimpleColumnDefinition;
import org.eclipse.chemclipse.support.ui.wizards.SinglePageWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.SelectableTargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetDisplaySettings.LibraryField;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.TargetReference;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.VisibilityTargetDisplaySettings;
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

public class TargetDisplaySettingsWizard {

	public static final int DEFAULT_WIDTH = 1000;
	public static final int DEFAULT_HEIGHT = 600;

	public static boolean openWizard(Shell shell, Collection<? extends TargetReference> identifications, TargetDisplaySettingsWizardListener listener, TargetDisplaySettings settings) {

		TargetDisplaySettingsPage page = new TargetDisplaySettingsPage(identifications, settings, listener);
		SinglePageWizard wizard = new SinglePageWizard("Target Label Settings", false, page);
		WizardDialog wizardDialog = new WizardDialog(shell, wizard);
		wizardDialog.setMinimumPageSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		wizardDialog.setPageSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		if(wizardDialog.open() == Window.OK) {
			if(settings instanceof SelectableTargetDisplaySettings) {
				((SelectableTargetDisplaySettings)settings).setSelectedSettings(page.selectedSettings.base);
			}
			page.selectedSettings.copyTo(page.selectedSettings.base);
			return true;
		} else {
			return false;
		}
	}

	private static final class WizardTargetDisplaySettings implements TargetDisplaySettings, VisibilityTargetDisplaySettings {

		private boolean showPeakLabels;
		private boolean showScanLables;
		private LibraryField libraryField;
		private final Map<TargetReference, Boolean> visibleMap = new HashMap<>();
		private final TargetDisplaySettings base;
		private int rotation;
		private int depth;
		private String name;
		private TargetDisplaySettingsWizardListener settingsListener;
		public Predicate<TargetReference> predicate = always -> true;

		public WizardTargetDisplaySettings(TargetDisplaySettings base, String name, TargetDisplaySettingsWizardListener listener) {
			this.base = base;
			this.name = name;
			this.settingsListener = listener;
			showPeakLabels = base.isShowPeakLabels();
			showScanLables = base.isShowScanLables();
			libraryField = base.getField();
			rotation = base.getRotation();
			depth = base.getCollisionDetectionDepth();
		}

		public void copyTo(TargetDisplaySettings other) {

			other.setField(libraryField);
			other.setShowPeakLabels(showPeakLabels);
			other.setShowScanLables(showScanLables);
			other.setRotation(rotation);
			other.setCollisionDetectionDepth(depth);
			if(other instanceof VisibilityTargetDisplaySettings) {
				VisibilityTargetDisplaySettings visibilityTargetDisplaySettings = (VisibilityTargetDisplaySettings)other;
				for(Entry<TargetReference, Boolean> entry : visibleMap.entrySet()) {
					visibilityTargetDisplaySettings.setVisible(entry.getKey(), entry.getValue());
				}
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

			if(reference == null) {
				return false;
			}
			return visibleMap.computeIfAbsent(reference, t -> {
				if(base instanceof VisibilityTargetDisplaySettings) {
					VisibilityTargetDisplaySettings v = (VisibilityTargetDisplaySettings)base;
					return v.isVisible(t);
				} else {
					return true;
				}
			});
		}

		@Override
		public void setVisible(TargetReference reference, boolean visible) {

			visibleMap.put(reference, visible);
		}

		@Override
		public void setRotation(int degree) {

			rotation = degree;
		}

		@Override
		public int getRotation() {

			return rotation;
		}

		@Override
		public int getCollisionDetectionDepth() {

			return depth;
		}

		@Override
		public void setCollisionDetectionDepth(int depth) {

			this.depth = depth;
		}
	}

	private static final class TargetDisplaySettingsPage extends WizardPage {

		WizardTargetDisplaySettings selectedSettings;
		final WizardTargetDisplaySettings[] wizardSettings;
		private final Collection<? extends TargetReference> identifications;
		private TargetDisplaySettingsWizardListener listener;

		protected TargetDisplaySettingsPage(Collection<? extends TargetReference> identifications, TargetDisplaySettings settings, TargetDisplaySettingsWizardListener listener) {
			super(TargetDisplaySettingsPage.class.getName());
			this.identifications = identifications;
			this.listener = listener;
			setImageDescriptor(ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_LABELS, IApplicationImage.SIZE_64x64));
			setTitle("Manage target labels to display");
			setDescription("Here you can select what target labels should be displayed in the chromatogram");
			Set<Entry<String, TargetDisplaySettings>> entrySet;
			if(settings instanceof SelectableTargetDisplaySettings) {
				entrySet = ((SelectableTargetDisplaySettings)settings).getSettings().entrySet();
			} else {
				entrySet = Collections.singletonMap("Settings", settings).entrySet();
			}
			wizardSettings = new WizardTargetDisplaySettings[entrySet.size()];
			int index = 0;
			for(Entry<String, TargetDisplaySettings> entry : entrySet) {
				wizardSettings[index] = new WizardTargetDisplaySettings(entry.getValue(), entry.getKey(), listener);
				if(settings instanceof SelectableTargetDisplaySettings) {
					if(((SelectableTargetDisplaySettings)settings).isSelectedSettings(entry.getValue())) {
						selectedSettings = wizardSettings[index];
					}
				} else {
					selectedSettings = wizardSettings[index];
				}
				index++;
			}
		}

		@Override
		public void createControl(Composite parent) {

			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout());
			Button[] radioButtons = new Button[wizardSettings.length];
			TargetSettingEditor[] editors = new TargetSettingEditor[wizardSettings.length];
			SelectionListener buttonListener = new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					for(int i = 0; i < radioButtons.length; i++) {
						Button radiobutton = radioButtons[i];
						if(radiobutton == null) {
							editors[i].setEnabled(true);
							continue;
						}
						if(radiobutton.getSelection()) {
							editors[i].setEnabled(true);
							if(selectedSettings != wizardSettings[i]) {
								selectedSettings = wizardSettings[i];
								notifyListener();
							}
						} else {
							editors[i].setEnabled(false);
						}
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			};
			for(int i = 0; i < radioButtons.length; i++) {
				WizardTargetDisplaySettings settings = wizardSettings[i];
				if(radioButtons.length > 1) {
					radioButtons[i] = radiobutton(composite, "Use " + settings.name, settings == selectedSettings);
					radioButtons[i].addSelectionListener(buttonListener);
				}
				editors[i] = new TargetSettingEditor(indentedContainer(composite, 25), settings, this, identifications);
				separator(composite);
			}
			buttonListener.widgetSelected(null);
			if(listener != null) {
				boolean preview = listener.isShowPreview();
				Button previewCheckbox = checkbox(composite, "Show preview in editor", preview);
				previewCheckbox.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {

						listener.setShowPreview(previewCheckbox.getSelection());
						notifyListener();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {

					}
				});
				if(preview) {
					notifyListener();
				}
			}
			setControl(composite);
		}

		private void notifyListener() {

			if(listener != null) {
				if(listener.isShowPreview()) {
					listener.setPreviewSettings(selectedSettings, selectedSettings.predicate);
				} else {
					listener.setPreviewSettings(null, t -> true);
				}
			}
		}

		private static final class TargetSettingEditor {

			private final Button peakLabels;
			private final Button scanLabels;
			private final Label fieldLabel;
			private final ComboViewer fieldComboViewer;
			private final Label rotationLabel;
			private final Scale scale;
			private final Label collisionLabel;
			private final ComboViewer collisionComboViewer;
			private TableViewer listUI;
			private ToolBarManager toolbarManager;
			private Collection<? extends TargetReference> identifications;

			public TargetSettingEditor(Composite parent, WizardTargetDisplaySettings editorSettings, TargetDisplaySettingsPage page, Collection<? extends TargetReference> identifications) {
				this.identifications = identifications;
				Composite showLabelsContainer = createDefault(parent, 7);
				peakLabels = checkbox(showLabelsContainer, "Show Peak Labels", editorSettings.isShowPeakLabels());
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
				scanLabels = checkbox(showLabelsContainer, "Show Scan Labels", editorSettings.isShowScanLables());
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
				// new Label(showLabelsContainer, SWT.SEPARATOR | SWT.VERTICAL);
				new Label(showLabelsContainer, SWT.NONE).setText(" | ");
				fieldLabel = label("Display Field", showLabelsContainer);
				fieldComboViewer = new ComboViewer(showLabelsContainer, SWT.READ_ONLY);
				fieldComboViewer.setContentProvider(ArrayContentProvider.getInstance());
				fieldComboViewer.setInput(LibraryField.values());
				fieldComboViewer.setSelection(new StructuredSelection(editorSettings.getField()));
				fieldComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {

						editorSettings.setField((LibraryField)fieldComboViewer.getStructuredSelection().getFirstElement());
						page.notifyListener();
					}
				});
				collisionLabel = label("Collision Detection Depth", showLabelsContainer);
				collisionComboViewer = new ComboViewer(showLabelsContainer);
				collisionComboViewer.setContentProvider(ArrayContentProvider.getInstance());
				collisionComboViewer.setInput(new Object[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
				collisionComboViewer.setSelection(new StructuredSelection(editorSettings.getCollisionDetectionDepth()));
				collisionComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {

						editorSettings.setCollisionDetectionDepth(((Integer)collisionComboViewer.getStructuredSelection().getFirstElement()));
						page.notifyListener();
					}
				});
				Composite container = createLabelContainer(parent);
				rotationLabel = label(getRotationText(editorSettings.getRotation()), container);
				scale = fill(new Scale(container, SWT.HORIZONTAL));
				scale.setMinimum(0);
				scale.setMaximum(90);
				scale.setIncrement(1);
				scale.setPageIncrement(15);
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
				if(editorSettings.base instanceof VisibilityTargetDisplaySettings) {
					maximize(parent);
					toolbarManager = new ToolBarManager();
					fill(toolbarManager.createControl(parent));
					listUI = createTargetTable(parent, editorSettings, page::notifyListener);
					maximize(listUI.getControl());
					createToolbarActions(toolbarManager, listUI, editorSettings, page::notifyListener);
					toolbarManager.update(true);
					ISelectionChangedListener comboListener = new ISelectionChangedListener() {

						private LibraryField currentField;

						@Override
						public void selectionChanged(SelectionChangedEvent event) {

							LibraryField field = editorSettings.getField();
							if(field != currentField && page.selectedSettings == editorSettings) {
								currentField = field;
								listUI.refresh();
								page.notifyListener();
							}
						}
					};
					fieldComboViewer.addSelectionChangedListener(comboListener);
				}
			}

			private String getRotationText(int value) {

				StringBuilder sb = new StringBuilder();
				sb.append("Rotation (");
				if(value < 10) {
					sb.append(' ');
				}
				sb.append(value);
				sb.append("°)  ");
				return sb.toString();
			}

			public void setEnabled(boolean enabled) {

				peakLabels.setEnabled(enabled);
				scanLabels.setEnabled(enabled);
				fieldLabel.setEnabled(enabled);
				collisionLabel.setEnabled(enabled);
				rotationLabel.setEnabled(enabled);
				fieldComboViewer.getControl().setEnabled(enabled);
				collisionComboViewer.getControl().setEnabled(enabled);
				scale.setEnabled(enabled);
				if(listUI != null) {
					if(enabled && listUI.getInput() == null) {
						listUI.setInput(identifications);
					}
					listUI.getControl().setEnabled(enabled);
				}
				if(toolbarManager != null) {
					toolbarManager.getControl().setEnabled(enabled);
				}
			}
		}

		private static TableViewer createTargetTable(Composite parent, WizardTargetDisplaySettings wizardSettings, Runnable updateListener) {

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
						if(wizardSettings.isVisible(identificationTarget)) {
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

					wizardSettings.setVisible((TargetReference)element, (Boolean)value);
					tableViewer.refresh(element);
					updateListener.run();
				}

				@Override
				protected Object getValue(Object element) {

					return wizardSettings.isVisible((TargetReference)element);
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
			createColumn(tableViewer, new SimpleColumnDefinition<>(wizardSettings.settingsListener != null ? wizardSettings.settingsListener.getIDLabel() : "ID", 80, TargetReference::getName));
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
	}

	private static void createToolbarActions(ToolBarManager manager, TableViewer tableViewer, WizardTargetDisplaySettings wizardSettings, Runnable listener) {

		manager.add(new Action("Enable All", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_SELECTED, IApplicationImage.SIZE_16x16)) {

			@Override
			public void run() {

				for(TableItem item : tableViewer.getTable().getItems()) {
					Object data = item.getData();
					if(data instanceof TargetReference) {
						TargetReference target = (TargetReference)data;
						wizardSettings.setVisible(target, true);
						tableViewer.refresh(target);
						listener.run();
					}
				}
			}
		});
		manager.add(new Action("Disable All", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_DESELECTED, IApplicationImage.SIZE_16x16)) {

			@Override
			public void run() {

				for(TableItem item : tableViewer.getTable().getItems()) {
					Object data = item.getData();
					if(data instanceof TargetReference) {
						TargetReference target = (TargetReference)data;
						wizardSettings.setVisible(target, false);
						tableViewer.refresh(target);
						listener.run();
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
						if(filterText == null || filterText.isEmpty()) {
							tableViewer.resetFilters();
							wizardSettings.predicate = always -> true;
						} else {
							wizardSettings.predicate = new Predicate<TargetReference>() {

								@Override
								public boolean test(TargetReference target) {

									String label = wizardSettings.getField().stringTransformer().apply(target.getBestTarget());
									return label != null && label.toLowerCase().contains(filterText.toLowerCase());
								}
							};
							tableViewer.setFilters(new ViewerFilter() {

								@Override
								public boolean select(Viewer viewer, Object parentElement, Object element) {

									if(element instanceof TargetReference) {
										TargetReference target = (TargetReference)element;
										return wizardSettings.predicate.test(target);
									}
									return true;
								}
							});
						}
						listener.run();
					}
				});
				return container;
			}

			@Override
			protected int computeWidth(Control control) {

				return Math.max(super.computeWidth(control), DEFAULT_WIDTH / 2);
			}
		});
	}
}
