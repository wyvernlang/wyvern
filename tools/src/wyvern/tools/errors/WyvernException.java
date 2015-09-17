package wyvern.tools.errors;

import wyvern.tools.parsing.Wyvern;

import java.util.Optional;

public class WyvernException extends RuntimeException {
    public WyvernException(String text) {
        super("Positionless error: "+text);
    }

    public WyvernException(String text, FileLocation location) {
        super("Wyvern has encountered the exception " + text + " at location " + location);
    }

    public WyvernException(String text, HasLocation location) {
        this(text, Optional.ofNullable(location).map(HasLocation::getLocation).orElse(null));
    }
}
