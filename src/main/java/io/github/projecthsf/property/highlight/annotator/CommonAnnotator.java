package io.github.projecthsf.property.highlight.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiLiteralValue;
import io.github.projecthsf.property.highlight.enums.HighlightScopeEnum;
import io.github.projecthsf.property.highlight.enums.LanguageEnum;
import io.github.projecthsf.property.highlight.quickFix.GoToFileLineFix;
import io.github.projecthsf.property.highlight.settings.AppSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class CommonAnnotator implements Annotator {
    private final AppSettings.State state = AppSettings.getInstance().getState();
    private final Storage storage;
    private boolean isResetValue = false;

    public CommonAnnotator() {
        storage = Storage.getInstance(state == null ? HighlightScopeEnum.PROJECT : state.highlightScope);
    }

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (state == null || !state.checkDuplicateValueStatus) {
            return;
        }
        if (!isResetValue) {
            storage.resetValue(element);
            isResetValue = true;
        }

        String value = getLiteralValue(element);
        if (value == null) {
            return;
        }
        RefDTO firstMatch = getFirstMatchReference(element);
        RefDTO storageValue = storage.getValue(value);
        if (storageValue == null) {
            storage.setValue(value, firstMatch);
            return;
        }

        if (firstMatch.equals(storageValue)) {
            // ignore high light the first match
            return;
        }
        holder.newAnnotation(state.highlightSeverity.getSeverity(), "Duplicate value with " + storageValue)
                .withFix(new GoToFileLineFix(storageValue.getClassName(), storageValue.getLine(), storageValue.getSrc()))
                .highlightType(ProblemHighlightType.WARNING)
                .create();

    }

    private RefDTO getFirstMatchReference(@NotNull PsiElement element) {
        PsiFile file = element.getContainingFile();
        int line = file.getFileDocument().getLineNumber(element.getTextRange().getStartOffset()) + 1;
        return new RefDTO(file.getName(), line, file.getVirtualFile().getPath());
    }

    private String getLiteralValue(@NotNull PsiElement element) {
        if (element instanceof PsiLiteralValue literalExpression) {
            return getLiteralValue(literalExpression);
        }

        if (element instanceof PsiLanguageInjectionHost literalExpression) {
            return getLiteralValue(literalExpression);
        }

        return null;
    }

    private String getLiteralValue(@NotNull PsiLiteralValue literalExpression) {
        if (literalExpression.getValue() == null) {
            return null;
        }

        if (literalExpression.getValue() instanceof Boolean) {
            return null;
        }

        return literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
    }

    private String getLiteralValue(@NotNull PsiLanguageInjectionHost literalExpression) {
        return literalExpression.getText();
    }

    abstract protected LanguageEnum getLanguageEnum();

    static class RefDTO {
        private String src;
        private final String className;
        private final int line;

        public RefDTO(String className, int line) {
            this(className, line, null);
        }
        public RefDTO(String className, int line, @Nullable String src) {
            this.src = src;
            this.className = className;
            this.line = line;
        }

        public String getClassName() {
            return className;
        }

        public int getLine() {
            return line;
        }

        public String getSrc() {
            return src;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof RefDTO dto)) {
                return false;
            }
            return className.equals(dto.getClassName()) && line == dto.getLine();
        }

        @Override
        public String toString() {
            return String.format("%s:%s", className, line);
        }
    }

    static class Storage {
        private static final Map<String, Map<String, RefDTO>> projectValueMap = new HashMap<>();
        private static final Map<String, String> projectValueToFile = new HashMap<>();
        private final Map<String, Map<String, RefDTO>> classValueMap = new HashMap<>();
        private boolean isResetValue = false;

        private HighlightScopeEnum highlightScope;
        static Storage getInstance(HighlightScopeEnum highlightScope) {
            return new Storage(highlightScope);
        }

        private Storage(HighlightScopeEnum highlightScope) {
            this.highlightScope = highlightScope;
        }

        RefDTO getValue(String key) {
            if (key == null || !projectValueToFile.containsKey(key)) {
                return null;
            }

            String src = projectValueToFile.get(key);

            if (highlightScope == HighlightScopeEnum.PROJECT) {
                if (!projectValueMap.containsKey(src)) {
                    return null;
                }
                if (projectValueMap.get(src).containsKey(key)) {
                    return projectValueMap.get(src).get(key);
                }

                return null;
            }

            if (!classValueMap.containsKey(src)) {
                return null;
            }
            if (classValueMap.get(src).containsKey(key)) {
                return classValueMap.get(src).get(key);
            }

            return null;
        }

        void setValue(String key, RefDTO dto) {
            if (key == null) {
                return;
            }

            projectValueToFile.put(key, dto.getSrc());
            if (highlightScope == HighlightScopeEnum.PROJECT) {
                if (!projectValueMap.containsKey(dto.getSrc())) {
                    projectValueMap.put(dto.getSrc(), new HashMap<>());
                }
                projectValueMap.get(dto.getSrc()).put(key, dto);

                return;
            }

            if (!classValueMap.containsKey(dto.getSrc())) {
                classValueMap.put(dto.getSrc(), new HashMap<>());
            }
            classValueMap.get(dto.getSrc()).put(key, dto);
        }

        void resetValue(@NotNull PsiElement element) {
            if (isResetValue) {
                return;
            }
            projectValueMap.remove(element.getContainingFile().getVirtualFile().getPath());
            isResetValue = true;
        }
    }
}
