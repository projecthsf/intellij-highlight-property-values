// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package io.github.projecthsf.property.highlight.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.util.ui.FormBuilder;
import io.github.projecthsf.property.highlight.enums.HighlightScopeEnum;
import io.github.projecthsf.property.highlight.enums.HighlightSeverityEnum;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/*
 * Supports storing the application settings in a persistent way.
 * The {@link com.intellij.openapi.components.State State} and {@link Storage}
 * annotations define the name of the data and the filename where these persistent
 * application settings are stored.
 */

@State(
        name = "org.intellij.sdk.duplicatedValues.settings.AppSettings",
        storages = @Storage("SdkSettingsPlugin.xml")
)
public final class AppSettings implements PersistentStateComponent<AppSettings.State> {

    public static class State {
        public boolean checkDuplicateValueStatus = true;
        public HighlightSeverityEnum highlightSeverity = HighlightSeverityEnum.WARNING;
        public HighlightScopeEnum highlightScope = HighlightScopeEnum.PROJECT;
    }

    private State myState = new State();

    public static AppSettings getInstance() {
        return ApplicationManager.getApplication().getService(AppSettings.class);
    }

    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    /**
     * Supports creating and managing a {@link JPanel} for the Settings Dialog.
     */
    public static class AppSettingsUi {

        private final JPanel myMainPanel;

        private final JBCheckBox checkDuplicateValueStatus = new JBCheckBox("Highlight duplicate property values", true);
        Map<HighlightSeverityEnum, JBRadioButton> highlightSeverities = new HashMap<>();
        Map<HighlightScopeEnum, JBRadioButton> highlightScopes = new HashMap<>();

        public AppSettingsUi() {
            ButtonGroup highlightTypes = new ButtonGroup();
            for (HighlightSeverityEnum highlightSeverity : HighlightSeverityEnum.values()) {
                JBRadioButton button = new JBRadioButton(highlightSeverity.getLabel(), highlightSeverity.isSelected());
                highlightTypes.add(button);
                highlightSeverities.put(highlightSeverity, button);
            }

            ButtonGroup scopes = new ButtonGroup();
            for (HighlightScopeEnum highlightScope : HighlightScopeEnum.values()) {
                JBRadioButton button = new JBRadioButton(highlightScope.getLabel(), highlightScope.isSelected());
                scopes.add(button);
                highlightScopes.put(highlightScope, button);
            }

            FormBuilder builder = FormBuilder.createFormBuilder()
                    .addComponent(checkDuplicateValueStatus, 0)
                    .addComponent(new JBLabel("Highlight scope:"), 1);

            for (HighlightScopeEnum highlightScope : HighlightScopeEnum.values()) {
                builder.addLabeledComponent("    ", highlightScopes.get(highlightScope));
            }

            builder.addComponent(new JBLabel("Highlight severity:"), 2);
            for (HighlightSeverityEnum highlightSeverity : HighlightSeverityEnum.values()) {
                builder.addLabeledComponent("    ", highlightSeverities.get(highlightSeverity));
            }

            builder.addComponentFillVertically(new JPanel(), 3);
            myMainPanel = builder.getPanel();
        }

        public JPanel getPanel() {
            return myMainPanel;
        }

        public JComponent getPreferredFocusedComponent() {
            return checkDuplicateValueStatus;
        }

        public boolean getCheckDuplicateValueStatus() {
            return checkDuplicateValueStatus.isSelected();
        }

        public void setCheckDuplicateValueStatus(boolean status) {
            checkDuplicateValueStatus.setSelected(status);
        }

        public HighlightSeverityEnum getHighlightSeverity() {
            for (HighlightSeverityEnum highlightSeverity : HighlightSeverityEnum.values()) {
                if (highlightSeverities.get(highlightSeverity).isSelected()) {
                    return highlightSeverity;
                }
            }

            return HighlightSeverityEnum.WARNING;
        }

        public void setHighlightSeverity(HighlightSeverityEnum highlightSeverity) {
            highlightSeverities.get(highlightSeverity).setSelected(true);
        }

        public HighlightScopeEnum getHighlightScope() {
            for (HighlightScopeEnum highlightScope : HighlightScopeEnum.values()) {
                if (highlightScopes.get(highlightScope).isSelected()) {
                    return highlightScope;
                }
            }

            return HighlightScopeEnum.PROJECT;
        }

        public void setHighlightScope(HighlightScopeEnum highlightScope) {
            highlightScopes.get(highlightScope).setSelected(true);
        }
    }
}
