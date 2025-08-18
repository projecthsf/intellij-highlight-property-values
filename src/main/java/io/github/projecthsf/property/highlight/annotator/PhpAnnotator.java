package io.github.projecthsf.property.highlight.annotator;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiLiteralValue;
import io.github.projecthsf.property.highlight.enums.LanguageEnum;
import org.jetbrains.annotations.NotNull;

public class PhpAnnotator extends CommonAnnotator {
    @Override
    protected LanguageEnum getLanguageEnum() {
        return LanguageEnum.PHP;
    }
}
