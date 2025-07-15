package io.github.projecthsf.property.highlight.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiLiteralExpression;
import io.github.projecthsf.property.highlight.enums.HighlightScopeEnum;
import io.github.projecthsf.property.highlight.settings.AppSettings;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CheckDuplicateValues implements Annotator {
    private final Storage storage = Storage.getInstance();

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        AppSettings.State state = AppSettings.getInstance().getState();
        if (state == null || !state.checkDuplicateValueStatus) {
            return;
        }
        if (!(element instanceof PsiLiteralExpression literalExpression)) {
            return;
        }

        if (literalExpression.getValue() == null) {
            return;
        }

        if (literalExpression.getValue() instanceof Boolean) {
            return;
        }

        if (!(literalExpression.getParent() instanceof PsiField)) {
            return;
        }

        String firstMatch = getFirstMatchReference(element);
        String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
        if (storage.containsKey(value)) {
            if (firstMatch.equals(storage.getValue(value))) {
                // ignore high light the first match
                return;
            }
            holder.newAnnotation(state.highlightSeverity.getSeverity(), "Duplicate value with " + storage.getValue(value))
                    .highlightType(ProblemHighlightType.WARNING)
                    .create();
        } else {
            storage.setValue(value, firstMatch);
        }
    }

    private String getFirstMatchReference(@NotNull PsiElement element) {
        PsiJavaFile file = (PsiJavaFile) element.getContainingFile();
        int line = file.getFileDocument().getLineNumber(element.getTextRange().getStartOffset()) + 1;
        return file.getPackageName() + "." + file.getName() + ":" + line;
    }

    static class Storage {
        private static final Map<String, String> projectValueMap = new HashMap<>();
        private final Map<String, String> classValueMap = new HashMap<>();

        static Storage getInstance() {
            return new Storage();
        }

        boolean containsKey(String key) {
            AppSettings.State state = AppSettings.getInstance().getState();

            if (state == null || state.highlightScope == HighlightScopeEnum.PROJECT) {
                return projectValueMap.containsKey(key);
            }

            return classValueMap.containsKey(key);
        }

        String getValue(String key) {
            AppSettings.State state = AppSettings.getInstance().getState();
            if (state == null || state.highlightScope == HighlightScopeEnum.PROJECT) {
                return projectValueMap.get(key);
            }

            return classValueMap.get(key);
        }

        void setValue(String key, String value) {
            AppSettings.State state = AppSettings.getInstance().getState();
            if (state == null || state.highlightScope == HighlightScopeEnum.PROJECT) {
                projectValueMap.put(key, value);
            }

            classValueMap.put(key, value);
        }
    }
}
