package com.vaadin.resource;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.DebouncePhase;
import com.vaadin.flow.router.*;

@DomEvent("click")
public class UpdateEvent extends ComponentEvent<VerticalLayout> {
    private final int message;

    public UpdateEvent(VerticalLayout source,
                       boolean fromClient,
                       @EventData("event.button") int message) {
        super(source, fromClient);
        this.message = message;
    }

    public int getMessage() {
        return message;
    }
}