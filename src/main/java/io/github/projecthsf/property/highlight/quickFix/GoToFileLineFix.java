package io.github.projecthsf.property.highlight.quickFix;

import com.intellij.modcommand.*;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class GoToFileLineFix implements ModCommandAction {
    private final String className;
    private final int line;
    public GoToFileLineFix(@NotNull String className, int line) {
        this.className = className;
        this.line = line;
    }

    public @NotNull String getFamilyName() {
        return String.format("Navigate to %s:%s", className, line);
    }


    @Override
    public @Nullable Presentation getPresentation(@NotNull ActionContext actionContext) {
        return Presentation.of(this.getFamilyName());
    }

    @Override
    public @NotNull ModCommand perform(@NotNull ActionContext actionContext) {
        PsiClass psiClass = JavaPsiFacade.getInstance(actionContext.project()).findClass(className, GlobalSearchScope.allScope(actionContext.project()));
        assert psiClass != null;

        Document document = psiClass.getContainingFile().getFileDocument();
        int startOffer = document.getLineStartOffset(line - 1);
        int endOffset = document.getLineEndOffset(line - 1);
        return new ModNavigate(psiClass.getContainingFile().getVirtualFile(), startOffer, endOffset , 0);
    }
}
