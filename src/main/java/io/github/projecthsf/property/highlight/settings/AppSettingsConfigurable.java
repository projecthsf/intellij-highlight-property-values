package io.github.projecthsf.property.highlight.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * Provides controller functionality for application settings.
 */
final class AppSettingsConfigurable implements Configurable {

    private AppSettings.AppSettingsUi mySettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Highlight Duplicate Property Values";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettings.AppSettingsUi();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettings.State state = Objects.requireNonNull(AppSettings.getInstance().getState());
        return mySettingsComponent.getCheckDuplicateValueStatus() != state.checkDuplicateValueStatus ||
                mySettingsComponent.getHighlightScope() != state.highlightScope ||
                mySettingsComponent.getHighlightSeverity() != state.highlightSeverity;
    }

    @Override
    public void apply() {
        AppSettings.State state = Objects.requireNonNull(AppSettings.getInstance().getState());
        state.checkDuplicateValueStatus = mySettingsComponent.getCheckDuplicateValueStatus();
        state.highlightScope = mySettingsComponent.getHighlightScope();
        state.highlightSeverity = mySettingsComponent.getHighlightSeverity();
    }

    @Override
    public void reset() {
        AppSettings.State state = Objects.requireNonNull(AppSettings.getInstance().getState());
        mySettingsComponent.setCheckDuplicateValueStatus(state.checkDuplicateValueStatus);
        mySettingsComponent.setHighlightScope(state.highlightScope);
        mySettingsComponent.setHighlightSeverity(state.highlightSeverity);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

}
