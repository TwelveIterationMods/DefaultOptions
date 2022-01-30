package net.blay09.mods.defaultoptions.api;

import java.util.function.Predicate;

public interface SimpleDefaultOptionsHandler extends DefaultOptionsHandler {
    SimpleDefaultOptionsHandler withCategory(DefaultOptionsCategory category);
    SimpleDefaultOptionsHandler withSaveHandler(Runnable saveHandler);
    SimpleDefaultOptionsHandler withLinePredicate(Predicate<String> linePredicate);
}
