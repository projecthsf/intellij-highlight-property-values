package io.github.projecthsf.property.highlight.enums;

import com.intellij.lang.annotation.HighlightSeverity;

public enum HighlightSeverityEnum {
    ERROR("Error", HighlightSeverity.ERROR, false),
    WARNING("Warning", HighlightSeverity.WARNING, true);
    private String label;
    private HighlightSeverity severity;
    private boolean selected;

    HighlightSeverityEnum(String label, HighlightSeverity severity, boolean selected) {
        this.severity = severity;
        this.label = label;
        this.selected = selected;
    }

    public String getLabel() {
        return label;
    }

    public HighlightSeverity getSeverity() {
        return severity;
    }

    public boolean isSelected() {
        return selected;
    }

}
