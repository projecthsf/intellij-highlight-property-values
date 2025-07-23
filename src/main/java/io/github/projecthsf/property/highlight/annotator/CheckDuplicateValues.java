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
        String firstMatch = getFirstMatchReference(element);
        String fileName = firstMatch.split(":")[0];
        storage.resetValue(fileName);
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


        String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
        String storageValue = storage.getValue(value);
        if (storageValue == null) {
            storage.setValue(fileName, value, firstMatch);
            return;
        }

        if (firstMatch.equals(storageValue)) {
            // ignore high light the first match
            return;
        }
        holder.newAnnotation(state.highlightSeverity.getSeverity(), "Duplicate value with " + storageValue)
                .highlightType(ProblemHighlightType.WARNING)
                .create();

    }

    private String getFirstMatchReference(@NotNull PsiElement element) {
        PsiJavaFile file = (PsiJavaFile) element.getContainingFile();
        int line = file.getFileDocument().getLineNumber(element.getTextRange().getStartOffset()) + 1;
        return file.getPackageName() + "." + file.getName() + ":" + line;
    }

    static class Storage {
        private static final Map<String, Map<String, String>> projectValueMap = new HashMap<>();
        private final Map<String, Map<String, String>> classValueMap = new HashMap<>();
        private boolean isResetValue = false;

        static Storage getInstance() {
            return new Storage();
        }

        String getValue(String key) {
            if (key == null) {
                return null;
            }
            AppSettings.State state = AppSettings.getInstance().getState();
            if (state == null || state.highlightScope == HighlightScopeEnum.PROJECT) {
                for (String fileName: projectValueMap.keySet()) {
                    if (projectValueMap.get(fileName).containsKey(key)) {
                        return projectValueMap.get(fileName).get(key);
                    }

                    return null;
                }

                return null;
            }

            for (String fileName: classValueMap.keySet()) {
                if (classValueMap.get(fileName).containsKey(key)) {
                    return classValueMap.get(fileName).get(key);
                }


            }
            return null;
        }

        void setValue(String fileName, String key, String value) {
            if (key == null) {
                return;
            }
            
            AppSettings.State state = AppSettings.getInstance().getState();
            if (state == null || state.highlightScope == HighlightScopeEnum.PROJECT) {
                if (!projectValueMap.containsKey(fileName)) {
                    projectValueMap.put(fileName, new HashMap<>());
                }
                projectValueMap.get(fileName).put(key, value);

                return;
            }

            if (!classValueMap.containsKey(fileName)) {
                classValueMap.put(fileName, new HashMap<>());
            }
            classValueMap.get(fileName).put(key, value);
        }

        void resetValue(String fileName) {
            if (isResetValue) {
                return;
            }

            AppSettings.State state = AppSettings.getInstance().getState();
            if (state == null || state.highlightScope == HighlightScopeEnum.PROJECT) {
                projectValueMap.remove(fileName);
                isResetValue = true;
            }
        }
    }
}
