package com.nicolls.ghostevent.ghost.parse;

import com.nicolls.ghostevent.ghost.core.IWebTarget;

import java.util.concurrent.Semaphore;

public interface IWebParser {
    void parse(IWebTarget target, Semaphore semaphore);

    long getParsedDelay();
}
