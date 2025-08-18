package io.github.projecthsf.property.highlight.quickFix;

import com.intellij.modcommand.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class GoToFileLineFix implements ModCommandAction {
    private final String className;
    private final int line;
    private String src;
    public GoToFileLineFix(@NotNull String className, int line, String src) {
        this.className = className;
        this.line = line;
        this.src = src;
    }

    public @NotNull String getFamilyName() {
        return String.format("Navigate to %s:%s", className, line);
    }


    @Override
    public @Nullable Presentation getPresentation(@NotNull ActionContext actionContext) {
        return Presentation.of(getFamilyName());
    }

    @Override
    public @NotNull ModCommand perform(@NotNull ActionContext actionContext) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(src);
        if (virtualFile == null) {
            // Handle case where file does not exist or is not accessible
            return null;
        }

        PsiFile psiFile = PsiManager.getInstance(actionContext.project()).findFile(virtualFile);
        Document document = psiFile.getFileDocument();
        int startOffer = document.getLineStartOffset(line - 1);
        int endOffset = document.getLineEndOffset(line - 1);
        return new ModNavigate(virtualFile, startOffer, endOffset , startOffer);
    }
}
