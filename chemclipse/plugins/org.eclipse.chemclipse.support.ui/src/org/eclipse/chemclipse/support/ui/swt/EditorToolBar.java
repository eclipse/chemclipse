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
 * Philip Wenig - fix layout
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createContainer;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.gridData;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.maximize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.preferences.ToolbarPreferencePage;
import org.eclipse.jface.action.AbstractGroupMarker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.SubContributionManager;
import org.eclipse.jface.action.SubToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class EditorToolBar {

	private static final AtomicLong ID_SEQUENCE = new AtomicLong(System.currentTimeMillis());
	private final IToolBarManager toolBarManager;
	private final AbstractGroupMarker group;
	private final EditorToolBar parent;
	private final ConfigSection config;
	private final List<ActionContributionItem> actionContributions;
	private boolean showText = true;

	public EditorToolBar(Composite parent) {
		this(parent, SWT.RIGHT);
	}

	public EditorToolBar(Composite parent, int align) {
		this.parent = null;
		actionContributions = new ArrayList<>();
		ToolBarManager toolbarManagerSWT = new ToolBarManager(SWT.FLAT | SWT.WRAP);
		group = null;
		ToolBar toolBar = toolbarManagerSWT.createControl(parent);
		toolBar.setLayoutData(new GridData(align, SWT.CENTER, true, false));
		toolBarManager = toolbarManagerSWT;
		config = new ConfigSection(this);
	}

	private EditorToolBar(IToolBarManager manager, EditorToolBar parent, String groupName) {
		toolBarManager = manager;
		this.parent = parent;
		this.config = null;
		this.group = new GroupMarker(groupName);
		this.actionContributions = parent.actionContributions;
	}

	public boolean isShowText() {

		if(parent != null) {
			return parent.isShowText();
		}
		return showText;
	}

	public void setShowText(boolean showText) {

		if(parent != null) {
			parent.setShowText(showText);
		} else {
			if(this.showText != showText) {
				this.showText = showText;
				for(ActionContributionItem item : actionContributions) {
					if(showText) {
						item.setMode(ActionContributionItem.MODE_FORCE_TEXT);
					} else {
						item.setMode(0);
					}
				}
				update();
			}
		}
	}

	public void addAction(IAction action) {

		addContribution(createContribution(action), false);
	}

	private void addContribution(IContributionItem item, boolean prepend) {

		if(group == null) {
			if(config != null && config.toolbar != null) {
				if(prepend) {
					IContributionItem[] items = toolBarManager.getItems();
					toolBarManager.insertBefore(items[0].getId(), item);
				} else {
					toolBarManager.insertBefore(config.toolbar.group.getId(), item);
				}
			} else {
				if(prepend) {
					IContributionItem[] items = toolBarManager.getItems();
					if(items.length > 0) {
						toolBarManager.insertBefore(items[0].getId(), item);
						return;
					}
				}
				toolBarManager.add(item);
			}
		} else {
			if(prepend) {
				toolBarManager.prependToGroup(group.getId(), item);
			} else {
				toolBarManager.appendToGroup(group.getId(), item);
			}
		}
		update();
	}

	private ActionContributionItem createContribution(IAction action) {

		ActionContributionItem contributionItem = new ActionContributionItem(action);
		if(isShowText()) {
			contributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		}
		contributionItem.setId("Action." + ID_SEQUENCE.incrementAndGet());
		actionContributions.add(contributionItem);
		return contributionItem;
	}

	public void setVisible(boolean visible) {

		if(toolBarManager instanceof SubContributionManager) {
			((SubContributionManager)toolBarManager).setVisible(visible);
		} else if(toolBarManager instanceof ToolBarManager) {
			Control control = ((ToolBarManager)toolBarManager).getControl();
			control.setVisible(visible);
			/*
			 * Resize the layout.
			 */
			Object object = control.getLayoutData();
			if(object instanceof GridData) {
				GridData gridData = (GridData)control.getLayoutData();
				if(gridData != null) {
					gridData.exclude = !visible;
				}
			}
			/*
			 * Redraw needed!
			 */
			Composite parent = control.getParent();
			parent.layout(true);
			parent.redraw();
		}
		update();
	}

	public void createCombo(Consumer<ComboViewer> consumer, boolean readOnly, int width) {

		ControlContribution contribution = new ControlContribution(UUID.randomUUID().toString()) {

			@Override
			protected Control createControl(Composite parent) {

				Composite composite = createContainer(parent);
				ComboViewer viewer = new ComboViewer(composite, readOnly ? SWT.READ_ONLY : SWT.NONE);
				viewer.setContentProvider(ArrayContentProvider.getInstance());
				GridData data = gridData(maximize(viewer.getControl()));
				data.verticalAlignment = SWT.CENTER;
				consumer.accept(viewer);
				return composite;
			}

			@Override
			protected int computeWidth(Control control) {

				return width > 0 ? width : super.computeWidth(control);
			}
		};
		contribution.setId("Combo." + ID_SEQUENCE.incrementAndGet());
		addContribution(contribution, false);
	}

	public void update() {

		toolBarManager.update(true);
		if(parent != null) {
			parent.update();
		}
	}

	/**
	 * 
	 * @return an initial hidden child of this {@link EditorToolBar}
	 */
	public EditorToolBar createChild() {

		return createChild(false);
	}

	public EditorToolBar createChild(boolean prepend) {

		String baseName;
		if(group == null) {
			// This is the master toolbar
			baseName = "master";
		} else {
			// this is a child
			baseName = group.getId();
		}
		EditorToolBar child = new EditorToolBar(new SubToolBarManager(toolBarManager), this, baseName + "." + ID_SEQUENCE.incrementAndGet());
		addContribution(child.group, prepend);
		return child;
	}

	public void addSeparator() {

		Separator separator = new Separator();
		separator.setId("Separator." + ID_SEQUENCE.incrementAndGet());
		addContribution(separator, false);
	}

	/**
	 * enables a button that allows the user to switch text on/off
	 * 
	 * @param preferenceStore
	 *            optional store where to save the last users choice
	 * @param key
	 *            the key under that the setting should be stored
	 */
	public IAction enableToolbarTextButton(IPreferenceStore preferenceStore, String key) {

		if(parent != null) {
			return parent.enableToolbarTextButton(preferenceStore, key);
		} else {
			Action action = new Action("Show Text", Action.AS_CHECK_BOX) {

				@Override
				public void run() {

					setShowText(!isShowText());
				}

				@Override
				public void setChecked(boolean checked) {

					if(checked) {
						setImageDescriptor(ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_SELECTED, IApplicationImage.SIZE_16x16));
					} else {
						setImageDescriptor(ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_DESELECTED, IApplicationImage.SIZE_16x16));
					}
					if(preferenceStore != null) {
						preferenceStore.setValue(key, checked);
					}
					super.setChecked(checked);
				}
			};
			boolean checked = updateShowTextByPreference(preferenceStore, key);
			action.setChecked(checked);
			config.getToolbar().addAction(action);
			update();
			return action;
		}
	}

	private boolean updateShowTextByPreference(IPreferenceStore preferenceStore, String key) {

		boolean checked;
		if(preferenceStore != null) {
			preferenceStore.setDefault(key, true);
			checked = preferenceStore.getBoolean(key);
			setShowText(checked);
		} else {
			checked = isShowText();
		}
		return checked;
	}

	/**
	 * enables a Settingspage where the user can choose to show text
	 * 
	 * @param preferenceStore
	 * @param key
	 * @return
	 */
	public void enableToolbarTextPage(IPreferenceStore preferenceStore, String key) {

		if(preferenceStore != null) {
			if(parent != null) {
				parent.enableToolbarTextPage(preferenceStore, key);
			} else {
				updateShowTextByPreference(preferenceStore, key);
				config.addPreferencePageContainer(new PreferencePageContainer(() -> {
					return Collections.singleton(new ToolbarPreferencePage(preferenceStore, key));
				}, () -> updateShowTextByPreference(preferenceStore, key)));
				update();
			}
		}
	}

	public void addPreferencePages(Supplier<Collection<? extends IPreferencePage>> pageSupplier, Runnable settingsChangedRunnable) {

		if(parent != null) {
			parent.addPreferencePages(pageSupplier, settingsChangedRunnable);
			return;
		} else {
			config.addPreferencePageContainer(new PreferencePageContainer(pageSupplier, settingsChangedRunnable));
			update();
		}
	}

	public boolean isVisible() {

		if(toolBarManager instanceof SubContributionManager) {
			return ((SubContributionManager)toolBarManager).isVisible();
		} else if(toolBarManager instanceof ToolBarManager) {
			return ((ToolBarManager)toolBarManager).getControl().isVisible();
		} else {
			return false;
		}
	}

	private static final class ConfigSection {

		private final List<PreferencePageContainer> preferencePages = new ArrayList<>();
		private EditorToolBar toolbar;
		private IAction configAction;
		private final EditorToolBar parent;

		public ConfigSection(EditorToolBar editorToolBar) {
			this.parent = editorToolBar;
		}

		private void addPreferencePageContainer(PreferencePageContainer container) {

			if(configAction == null) {
				configAction = new Action("Settings", ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16)) {

					@Override
					public void runWithEvent(Event event) {

						ToolItem widget = (ToolItem)event.widget;
						PreferenceManager preferenceManager = new PreferenceManager();
						int i = 0;
						for(PreferencePageContainer container : preferencePages) {
							for(IPreferencePage page : container.supplier.get()) {
								preferenceManager.addToRoot(new PreferenceNode(String.valueOf(i++), page));
							}
						}
						ToolBar parent = widget.getParent();
						PreferenceDialog preferenceDialog = new PreferenceDialog(parent.getShell(), preferenceManager);
						preferenceDialog.create();
						preferenceDialog.getShell().setText("Settings");
						if(preferenceDialog.open() == Window.OK) {
							for(PreferencePageContainer container : preferencePages) {
								if(container.runnable != null) {
									event.display.asyncExec(container.runnable);
								}
							}
						}
					}
				};
				getToolbar().addAction(configAction);
				getToolbar().setVisible(true);
			}
			preferencePages.add(container);
		}

		public EditorToolBar getToolbar() {

			if(toolbar == null) {
				toolbar = parent.createChild(false);
			}
			return toolbar;
		}
	}

	private static final class PreferencePageContainer {

		private final Supplier<Collection<? extends IPreferencePage>> supplier;
		private final Runnable runnable;

		private PreferencePageContainer(Supplier<Collection<? extends IPreferencePage>> supplier, Runnable runnable) {
			this.supplier = supplier;
			this.runnable = runnable;
		}
	}

	public void clear() {

		toolBarManager.removeAll();
	}
}
