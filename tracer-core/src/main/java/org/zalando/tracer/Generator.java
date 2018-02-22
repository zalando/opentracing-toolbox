package org.zalando.tracer;

import java.util.function.Function;

/**
 * A generator is used to create a new trace identifier when a new lifecycle begins and no existing identifier exist.
 * This usually happens for incoming requests from external systems that are not aware of the special trace header or
 * if someone explicitly decides to start a new trace lifecycle without preserving existing identifiers.
 *
 * @see Tracer#create(String...)
 * @see Tracer#start()
 * @see Tracer#start(Function)
 */
@FunctionalInterface
public interface Generator {

    String generate();

}
