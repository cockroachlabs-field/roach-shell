package io.crdb.tools.hsd;

import com.google.common.base.Function;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CompareQPSFunction implements Function<HotRangeVO, Float> {
    @Nullable
    @Override
    public Float apply(@Nullable HotRangeVO input) {
        return input.getQueriesPerSecond();
    }
}
