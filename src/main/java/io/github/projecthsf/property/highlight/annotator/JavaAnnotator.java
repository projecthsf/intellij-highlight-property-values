package io.github.projecthsf.property.highlight.annotator;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralValue;
import org.jetbrains.annotations.NotNull;

public class JavaAnnotator extends CommonAnnotator {
    @Override
    protected String getLiteralValue(@NotNull PsiElement element) {
        if (!(element instanceof PsiLiteralValue literalExpression)) {
            return null;
        }

        if (literalExpression.getValue() == null) {
            return null;
        }

        if (literalExpression.getValue() instanceof Boolean) {
            return null;
        }

        return literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
    }
}
