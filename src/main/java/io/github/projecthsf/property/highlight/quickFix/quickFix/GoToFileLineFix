package io.github.projecthsf.property.highlight.quickFix;

import com.intellij.modcommand.*;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class GoToFileLineFix implements ModCommandAction {
    private String fileName;
    private int line;
    public GoToFileLineFix(@NotNull String fileName, int line) {
        this.fileName = fileName;
        this.line = line;
    }

    public @NotNull String getFamilyName() {
        return String.format("Navigate to %s:%s", fileName, line);
    }


    @Override
    public @Nullable Presentation getPresentation(@NotNull ActionContext actionContext) {
        return Presentation.of(this.getFamilyName());
    }

    @Override
    public @NotNull ModCommand perform(@NotNull ActionContext actionContext) {
        PsiClass psiClass = JavaPsiFacade.getInstance(actionContext.project()).findClass(fileName, GlobalSearchScope.allScope(actionContext.project()));
        assert psiClass != null;
        int startOffer = psiClass.getContainingFile().getFileDocument().getLineStartOffset(line-1);
        int endOffset = psiClass.getContainingFile().getFileDocument().getLineEndOffset(line-1);
        return new ModNavigate(psiClass.getContainingFile().getVirtualFile(), startOffer, endOffset , 0);
    }
}
