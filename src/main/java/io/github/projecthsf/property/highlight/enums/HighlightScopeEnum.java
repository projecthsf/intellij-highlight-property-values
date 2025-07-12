package io.github.projecthsf.property.highlight.enums;

public enum HighlightScopeEnum {
    PROJECT("Whole project", true),
    CLASS("Current class", false),
    ;

    private String label;
    private boolean selected;

    HighlightScopeEnum(String label, boolean selected) {
        this.label = label;
        this.selected = selected;
    }

    public String getLabel() {
        return label;
    }

    public boolean isSelected() {
        return selected;
    }
}
