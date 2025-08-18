package io.github.projecthsf.property.highlight.annotator;

import io.github.projecthsf.property.highlight.enums.LanguageEnum;

public class GoAnnotator extends CommonAnnotator {
    @Override
    protected LanguageEnum getLanguageEnum() {
        return LanguageEnum.GO;
    }
}
