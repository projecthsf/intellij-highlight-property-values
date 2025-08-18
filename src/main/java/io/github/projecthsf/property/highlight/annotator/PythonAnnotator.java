package io.github.projecthsf.property.highlight.annotator;

import io.github.projecthsf.property.highlight.enums.LanguageEnum;

public class PythonAnnotator extends CommonAnnotator {
    @Override
    protected LanguageEnum getLanguageEnum() {
        return LanguageEnum.PYTHON;
    }
}
