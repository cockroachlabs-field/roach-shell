package io.crdb.shell;

import com.google.common.base.Function;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CompareQPSFunction implements Function<HotRangeVO, Float> {
    @Nullable
    @Override
    public Float apply(@Nullable HotRangeVO input) {
        if (input != null) {
            return input.getQueriesPerSecond();
        }

        return 0.0f;
    }
}
