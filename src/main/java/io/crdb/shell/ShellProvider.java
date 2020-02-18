package io.crdb.shell;

import org.jline.utils.AttributedString;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class ShellProvider implements PromptProvider {
    @Override
    public AttributedString getPrompt() {
        return new AttributedString("roach-shell:>");
    }
}
