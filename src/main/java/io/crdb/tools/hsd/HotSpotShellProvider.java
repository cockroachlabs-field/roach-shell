package io.crdb.tools.hsd;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class HotSpotShellProvider implements PromptProvider {
    @Override
    public AttributedString getPrompt() {
        return new AttributedString("cockroach-utils:>",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)
        );
    }
}
