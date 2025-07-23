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
        RefDTO firstMatch = getFirstMatchReference(element);
        storage.resetValue(firstMatch.getFile());
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
        RefDTO storageValue = storage.getValue(value);
        if (storageValue == null) {
            storage.setValue(firstMatch.getFile(), value, firstMatch.getLine());
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

    private RefDTO getFirstMatchReference(@NotNull PsiElement element) {
        PsiJavaFile file = (PsiJavaFile) element.getContainingFile();
        int line = file.getFileDocument().getLineNumber(element.getTextRange().getStartOffset()) + 1;

        return new RefDTO(String.format("%s.%s", file.getPackageName(), file.getName()), line);
    }

    static class RefDTO {
        private final String file;
        private final int line;

        public RefDTO(String file, int line) {
            this.file = file;
            this.line = line;
        }

        public String getFile() {
            return file;
        }

        public int getLine() {
            return line;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof RefDTO dto)) {
                return false;
            }
            return file.equals(dto.getFile()) && line == dto.getLine();
        }

        @Override
        public String toString() {
            return String.format("%s:%s", file, line);
        }
    }

    static class Storage {
        private static final Map<String, Map<String, Integer>> projectValueMap = new HashMap<>();
        private final Map<String, Map<String, Integer>> classValueMap = new HashMap<>();
        private boolean isResetValue = false;

        static Storage getInstance() {
            return new Storage();
        }

        RefDTO getValue(String key) {
            if (key == null) {
                return null;
            }
            AppSettings.State state = AppSettings.getInstance().getState();
            if (state == null || state.highlightScope == HighlightScopeEnum.PROJECT) {
                for (String fileName: projectValueMap.keySet()) {
                    if (projectValueMap.get(fileName).containsKey(key)) {
                        return new RefDTO(fileName, projectValueMap.get(fileName).get(key));
                    }
                }

                return null;
            }

            for (String fileName: classValueMap.keySet()) {
                if (classValueMap.get(fileName).containsKey(key)) {
                    return new RefDTO(fileName, classValueMap.get(fileName).get(key));
                }
            }
            return null;
        }

        void setValue(String fileName, String key, int line) {
            if (key == null) {
                return;
            }

            AppSettings.State state = AppSettings.getInstance().getState();
            if (state == null || state.highlightScope == HighlightScopeEnum.PROJECT) {
                if (!projectValueMap.containsKey(fileName)) {
                    projectValueMap.put(fileName, new HashMap<>());
                }
                projectValueMap.get(fileName).put(key, line);

                return;
            }

            if (!classValueMap.containsKey(fileName)) {
                classValueMap.put(fileName, new HashMap<>());
            }
            classValueMap.get(fileName).put(key, line);
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
