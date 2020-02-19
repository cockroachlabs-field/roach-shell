package io.crdb.shell;

import com.google.common.base.Function;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CompareExecutionCount implements Function<Statement, Integer> {
    @Nullable
    @Override
    public Integer apply(@Nullable Statement input) {
        if (input != null) {
            return input.getStats().getCount();
        }

        return 0;
    }
}
