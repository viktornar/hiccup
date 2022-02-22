package com.github.viktornar.hiccup.game.character;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.BiConsumer;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TrainerActions<T, E> implements Consumer<E> {
    @Slf4j
    public static class State<T, E> {
        private final String name;
        private BiConsumer<T, State<T, E>> enter;
        private BiConsumer<T, State<T, E>> exit;
        private final Map<E, State<T, E>> transitions = new HashMap<>();

        public State(String name) {
            this.name = name;
        }

        public State<T, E> onTransition(BiConsumer<T, State<T, E>> func) {
            this.enter = func;
            return this;
        }

        private void enter(T context) throws Throwable {
            if (enter != null) {
                enter.accept(context, this);
            }
        }

        private void exit(T context) throws Throwable {
            if (exit != null) {
                exit.accept(context, this);
            }
        }

        public State<T, E> target(E event, State<T, E> state) {
            transitions.put(event, state);
            return this;
        }

        public State<T, E> next(E event) {
            return transitions.get(event);
        }

        public String toString() {
            return name;
        }
    }

    private State<T, E> state;

    private final T context;

    private final PublishSubject<E> events = PublishSubject.create();

    public TrainerActions(T context, State<T, E> initial) {
        this.state = initial;
        this.context = context;
    }

    public Observable<Void> connect() {
        return Observable.create(sub -> {
            state.enter(context);

            sub.setDisposable(events.collect(() -> context, (ctx, event) -> {
                        final State<T, E> next = state.next(event);

                        if (next != null) {
                            state.exit(ctx);
                            state = next;
                            next.enter(ctx);
                        } else {
                            log.info("Invalid event: {}", event);
                        }
                    })
                    // Log error
                    .doOnError(error -> log.error("Unable to completely process flow: {}", error.getMessage()))
                    // Recover, will repeat steps.
                    .onErrorReturnItem(context)
                    .subscribe());
        });
    }

    @Override
    public void accept(E event) {
        events.onNext(event);
    }

    public synchronized State<T, E> getState() {
        return state;
    }
}
